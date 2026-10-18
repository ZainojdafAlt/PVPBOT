package dev.pvpbot;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PvPBotPlugin extends JavaPlugin implements Listener, TabExecutor {

    static class Session {
        Player p; Zombie bot; String mode, tierName;
        ConfigurationSection t;
        ItemStack[] saved; BukkitTask task;
        int tick, totems, gapples; long lastHeal;
        boolean selfBlast, over, ended;
    }

    private final Map<UUID, Session> sessions = new HashMap<>();
    private final Map<UUID, Session> byBot = new HashMap<>();
    private static final List<String> TIERS = List.of("LT5","HT5","LT4","HT4","LT3","HT3","LT2","HT2","LT1","HT1");
    private static final List<String> MODES = List.of("cpvp","mace");

    @Override public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("bot").setExecutor(this);
        getCommand("bot").setTabCompleter(this);
    }

    @Override public void onDisable() {
        new ArrayList<>(sessions.values()).forEach(s -> end(s, true));
    }

    // ---------- helpers ----------
    private Component c(String s) { return LegacyComponentSerializer.legacyAmpersand().deserialize(s); }
    private void msg(Player p, String s) { p.sendMessage(c(getConfig().getString("settings.prefix", "") + s)); }

    private ItemStack item(String s) {
        if (s == null || s.isBlank()) return null;
        String[] parts = s.split(";");
        Material m = Material.matchMaterial(parts[0].trim());
        if (m == null) { getLogger().warning("Unknown material in kit: " + parts[0]); return null; }
        ItemStack it = new ItemStack(m);
        for (int i = 1; i < parts.length; i++) {
            String x = parts[i].trim();
            if (x.matches("\\d+")) it.setAmount(Integer.parseInt(x));
            else if (x.contains(":")) {
                String[] e = x.split(":");
                Enchantment en = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(e[0].toLowerCase()));
                if (en == null) getLogger().warning("Unknown enchant in kit: " + e[0]);
                else it.addUnsafeEnchantment(en, Integer.parseInt(e[1]));
            }
        }
        return it;
    }

    private void giveKit(Player p, ConfigurationSection k) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setHelmet(item(k.getString("helmet")));
        inv.setChestplate(item(k.getString("chestplate")));
        inv.setLeggings(item(k.getString("leggings")));
        inv.setBoots(item(k.getString("boots")));
        inv.setItemInOffHand(item(k.getString("offhand")));
        List<String> l = k.getStringList("items");
        for (int i = 0; i < l.size() && i < 36; i++) inv.setItem(i, item(l.get(i)));
    }

    // ---------- session control ----------
    private void start(Player p, String tierName, String mode) {
        Session old = sessions.get(p.getUniqueId());
        if (old != null) end(old, true);

        ConfigurationSection t = getConfig().getConfigurationSection("tiers." + tierName);
        ConfigurationSection kit = getConfig().getConfigurationSection("kits." + mode);
        if (t == null || kit == null) { msg(p, "&cMissing tier/kit in config."); return; }

        Session s = new Session();
        s.p = p; s.mode = mode; s.tierName = tierName; s.t = t;
        s.totems = t.getInt("totems"); s.gapples = t.getInt("gapples");
        s.saved = p.getInventory().getContents().clone();

        giveKit(p, kit);
        if (getConfig().getBoolean("settings.heal-player-on-start", true)) {
            p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
            p.setFoodLevel(20); p.setSaturation(20f);
        }

        Vector d = p.getLocation().getDirection().setY(0);
        if (d.lengthSquared() < 1e-4) d = new Vector(1, 0, 0);
        d.normalize().multiply(getConfig().getDouble("settings.spawn-distance", 8));
        Location l = p.getLocation().add(d);
        for (int i = 0; i < 6 && l.getBlock().getType().isSolid(); i++) l.add(0, 1, 0);
        l.setDirection(p.getLocation().toVector().subtract(l.toVector()));

        List<String> items = kit.getStringList("items");
        s.bot = p.getWorld().spawn(l, Zombie.class, z -> {
            z.setBaby(false);
            z.setShouldBurnInDay(false);
            z.setCanPickupItems(false);
            z.setRemoveWhenFarAway(false);
            z.setPersistent(false);
            z.customName(c("&c[" + tierName + "] " + (mode.equals("cpvp") ? "CrystalBot" : "MaceBot")));
            z.setCustomNameVisible(true);
            z.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
            z.setHealth(20);
            z.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(t.getDouble("speed", 0.25));
            z.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(t.getDouble("attack", 1));
            z.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(64);
            EntityEquipment eq = z.getEquipment();
            eq.setHelmet(item(kit.getString("helmet")));
            eq.setChestplate(item(kit.getString("chestplate")));
            eq.setLeggings(item(kit.getString("leggings")));
            eq.setBoots(item(kit.getString("boots")));
            if (!items.isEmpty()) eq.setItemInMainHand(item(items.get(0)));
            eq.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            z.setTarget(p);
        });

        sessions.put(p.getUniqueId(), s);
        byBot.put(s.bot.getUniqueId(), s);
        s.task = Bukkit.getScheduler().runTaskTimer(this, () -> tick(s), 2L, 2L);
        msg(p, "&aFight started vs &c" + tierName + " " + mode + " &abot. Good luck!");
    }

    private void end(Session s, boolean restore) {
        if (s.ended) return;
        s.ended = true;
        if (s.task != null) s.task.cancel();
        if (s.bot.isValid()) s.bot.remove();
        byBot.remove(s.bot.getUniqueId());
        sessions.remove(s.p.getUniqueId());
        if (restore && s.p.isOnline()) {
            s.p.getInventory().setContents(s.saved);
            s.p.updateInventory();
        }
    }

    // ---------- bot AI ----------
    private void tick(Session s) {
        if (s.over || s.ended) return;
        Player p = s.p;
        if (!p.isOnline() || p.isDead()) return;
        if (!s.bot.isValid() || !s.bot.getWorld().equals(p.getWorld())) { end(s, true); msg(p, "&7Bot removed."); return; }

        s.tick += 2;
        s.bot.setTarget(p);
        double dist = s.bot.getLocation().distance(p.getLocation());
        int reaction = Math.max(2, s.t.getInt("reaction", 20));
        ThreadLocalRandom r = ThreadLocalRandom.current();
        boolean attempt = s.tick % reaction == 0 && r.nextDouble() < s.t.getDouble("hit", 0.5);
        World w = s.bot.getWorld();

        if (attempt && s.mode.equals("cpvp") && dist < 7) {
            Location loc = p.getLocation().add(r.nextDouble(-1.5, 1.5), 0, r.nextDouble(-1.5, 1.5));
            EnderCrystal ec = w.spawn(loc, EnderCrystal.class, e -> { e.setShowingBottom(false); e.setInvulnerable(true); });
            float power = (float) s.t.getDouble("power", 4);
            Bukkit.getScheduler().runTaskLater(this, () -> {
                ec.remove();
                if (s.over || s.ended || !s.bot.isValid()) return;
                s.selfBlast = true;
                try { w.createExplosion(loc, power, false, false, s.bot); }
                finally { s.selfBlast = false; }
            }, 5L);
        }

        if (attempt && s.mode.equals("mace") && dist < 7 && s.bot.isOnGround()) {
            Vector to = p.getLocation().toVector().subtract(s.bot.getLocation().toVector()).setY(0);
            if (to.lengthSquared() > 0) to.normalize().multiply(0.35);
            s.bot.setVelocity(new Vector(to.getX(), 1.0, to.getZ()));
        }

        if (s.gapples > 0 && s.bot.getHealth() < 10 && s.tick - s.lastHeal >= 100) {
            s.gapples--; s.lastHeal = s.tick;
            s.bot.setHealth(Math.min(20, s.bot.getHealth() + 8));
            s.bot.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1));
            w.spawnParticle(Particle.HEART, s.bot.getLocation().add(0, 2, 0), 6, .3, .3, .3);
            w.playSound(s.bot.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
        }
    }

    // ---------- events ----------
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBotDamaged(EntityDamageEvent e) {
        Session s = byBot.get(e.getEntity().getUniqueId());
        if (s == null || s.over) return;
        if (s.selfBlast && (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) { e.setCancelled(true); return; }
        if (e.getFinalDamage() >= s.bot.getHealth() && s.totems > 0) {
            e.setCancelled(true);
            s.totems--;
            s.bot.setHealth(10);
            s.bot.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));
            Location l = s.bot.getLocation();
            l.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 1f, 1f);
            l.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, l.add(0, 1, 0), 40, .3, .5, .3, .3);
            msg(s.p, "&eThe bot popped a totem! &7(" + s.totems + " left)");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBotHit(EntityDamageByEntityEvent e) {
        Session s = byBot.get(e.getDamager().getUniqueId());
        if (s == null || !s.mode.equals("mace") || !(e.getEntity() instanceof Player)) return;
        double f = s.bot.getFallDistance();
        if (f > 1.5) {
            double extra = Math.min(f, 3) * 4 + Math.min(Math.max(f - 3, 0), 5) * 2 + Math.max(f - 8, 0);
            e.setDamage(e.getDamage() + extra * getConfig().getDouble("settings.mace-damage-multiplier", 0.7));
            s.bot.setFallDistance(0);
            Location l = e.getEntity().getLocation();
            l.getWorld().spawnParticle(Particle.EXPLOSION, l, 1);
            l.getWorld().playSound(l, Sound.ITEM_MACE_SMASH_GROUND, 1f, 1f);
        }
    }

    @EventHandler
    public void onBotDeath(EntityDeathEvent e) {
        Session s = byBot.get(e.getEntity().getUniqueId());
        if (s == null) return;
        e.getDrops().clear(); e.setDroppedExp(0);
        s.over = true;
        if (s.task != null) s.task.cancel();
        msg(s.p, "&a&lYou won &7vs the &c" + s.tierName + " &7bot!");
        Bukkit.getScheduler().runTaskLater(this, () -> end(s, true), 40L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Session s = sessions.get(e.getEntity().getUniqueId());
        if (s == null) return;
        e.setKeepInventory(true); e.setKeepLevel(true);
        e.getDrops().clear(); e.setDroppedExp(0);
        s.over = true;
        if (s.task != null) s.task.cancel();
        if (s.bot.isValid()) s.bot.remove();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Session s = sessions.get(e.getPlayer().getUniqueId());
        if (s == null || !s.over) return;
        Bukkit.getScheduler().runTaskLater(this, () -> {
            end(s, true);
            msg(s.p, "&cYou lost to the &7" + s.tierName + " &cbot. Inventory restored.");
        }, 2L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Session s = sessions.get(e.getPlayer().getUniqueId());
        if (s != null) end(s, true);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        Session s = byBot.get(e.getEntity().getUniqueId());
        if (s != null && e.getTarget() != s.p) e.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS && !sessions.isEmpty()) e.setCancelled(true);
    }

    // ---------- commands ----------
    @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] a) {
        if (a.length >= 1 && a[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("pvpbot.admin")) { sender.sendMessage("No permission."); return true; }
            reloadConfig(); sender.sendMessage("PvPBot config reloaded."); return true;
        }
        if (!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
        if (a.length >= 1 && a[0].equalsIgnoreCase("stop")) {
            Session s = sessions.get(p.getUniqueId());
            if (s == null) msg(p, "&cYou're not fighting a bot."); else { end(s, true); msg(p, "&7Fight stopped."); }
            return true;
        }
        if (a.length < 2 || !TIERS.contains(a[0].toUpperCase()) || !MODES.contains(a[1].toLowerCase())) {
            msg(p, "&cUsage: /bot <LT5-HT1> <cpvp|mace> | /bot stop"); return true;
        }
        start(p, a[0].toUpperCase(), a[1].toLowerCase());
        return true;
    }

    @Override public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        List<String> out = new ArrayList<>();
        if (a.length == 1) { out.addAll(TIERS); out.add("stop"); out.add("reload"); }
        else if (a.length == 2) out.addAll(MODES);
        String last = a[a.length - 1].toLowerCase();
        out.removeIf(x -> !x.toLowerCase().startsWith(last));
        return out;
    }
}
