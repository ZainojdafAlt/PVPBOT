package com.stashes.pvpbot.bot;

import com.stashes.pvpbot.nms.BotControl;
import com.stashes.pvpbot.platform.Platform;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * Crystal PvP, ported from pvp-bot-fabric's BotCrystalPvp onto the Bukkit API.
 * Loop: obsidian beside the target -> end crystal on it -> hit the crystal.
 * Additions over the Fabric version: positions are scored with ExplosionMath
 * (damage to target vs. damage to the bot) and unsafe crystals are never detonated.
 */
public final class BotCrystal {
    private static final Map<String, State> states = BotStates.map();
    private static final double POWER = 6.0;
    private static final double REACH = 4.5;

    private BotCrystal() {
    }

    static final class State {
        int step;
        Block base;
        int cooldown;
        int stuck;
        int lastStep = -1;
        int notFound;
        int placeFails;
        int swordCooldown;
        final Set<Block> tried = new HashSet<>();
    }

    private static State state(String name) {
        return states.computeIfAbsent(name, k -> new State());
    }

    public static void reset(String name) {
        states.remove(name);
    }

    /** @return true if crystal PvP took over this tick. */
    public static boolean update(Player bot, Entity entity, BotSettings settings) {
        if (!settings.isCrystalPvp() || !(entity instanceof Player target) || !target.isValid()
                || target.getWorld() != bot.getWorld() || BotControl.isUsingItem(bot)) {
            return false;
        }
        State st = state(bot.getName());
        double dist = bot.getLocation().distance(target.getLocation());
        EnderCrystal near = nearestCrystal(bot, target, 6.0);
        boolean hasKit = findItem(bot, Material.END_CRYSTAL) >= 0
                && (findItem(bot, Material.OBSIDIAN) >= 0 || findBase(bot, target) != null);
        if (near == null && (!hasKit || dist < 2.5 || dist > 8.0)) {
            st.step = 0;
            st.base = null;
            return false;
        }
        if (st.step == st.lastStep) {
            if (++st.stuck > 100) {
                st.step = 0;
                st.base = null;
                st.cooldown = 0;
                st.stuck = 0;
                return true;
            }
        } else {
            st.stuck = 0;
            st.lastStep = st.step;
        }
        if (st.swordCooldown > 0) {
            st.swordCooldown--;
        }
        if (st.cooldown > 0) {
            st.cooldown--;
            keepDistance(bot, target, settings);
            return true;
        }
        if (near != null) {
            return hitCrystal(bot, target, near, st, settings);
        }
        if (dist < 2.5) {
            BotNavigation.moveAway(bot, target, settings.getSpeed());
            return true;
        }
        if (meleeFirst(bot, target, st, settings)) {
            return true;
        }
        return st.step == 0 ? placeObsidian(bot, target, st, settings) : placeCrystal(bot, target, st, settings);
    }

    private static boolean meleeFirst(Player bot, Player target, State st, BotSettings settings) {
        if (st.swordCooldown > 0 || bot.getLocation().distance(target.getLocation()) > settings.getMeleeRange()) {
            return false;
        }
        BotEquipment.equipWeapon(bot, settings);
        BotControl.lookAt(bot, target.getEyeLocation());
        if (bot.getAttackCooldown() < 1.0f || !BotUtils.canAttack(bot)) {
            return false;
        }
        bot.attack(target);
        bot.swingMainHand();
        st.swordCooldown = 10;
        return true;
    }

    private static boolean placeObsidian(Player bot, Player target, State st, BotSettings settings) {
        Block existing = findBase(bot, target);
        if (existing != null) {
            st.base = existing;
            st.step = 1;
            return true;
        }
        int slot = findItem(bot, Material.OBSIDIAN);
        if (slot < 0) {
            return false;
        }
        Block pos = bestPosition(bot, target, st, settings);
        if (pos == null) {
            st.tried.clear();
            keepDistance(bot, target, settings);
            return true;
        }
        if (center(pos).distance(bot.getEyeLocation()) > 4.0) {
            BotNavigation.moveTowardPosition(bot, target.getLocation(), settings.getSpeed());
            return true;
        }
        BotNavigation.stop(bot);
        hold(bot, slot);
        BotControl.lookAt(bot, center(pos));
        if (BotBuilder.place(bot, pos, Material.OBSIDIAN, pos.getRelative(BlockFace.DOWN))) {
            consume(bot, bot.getInventory().getHeldItemSlot());
            bot.swingMainHand();
            st.base = pos;
            st.step = 1;
            st.cooldown = 2;
        } else {
            st.tried.add(pos);
            st.cooldown = 3;
        }
        return true;
    }

    private static boolean placeCrystal(Player bot, Player target, State st, BotSettings settings) {
        Block base = st.base;
        if (base == null || !isBase(base.getType()) || !free(base.getRelative(BlockFace.UP))) {
            if (base != null) {
                st.tried.add(base);
            }
            st.step = 0;
            st.base = null;
            return true;
        }
        int slot = findItem(bot, Material.END_CRYSTAL);
        if (slot < 0) {
            st.step = 0;
            st.base = null;
            return false;
        }
        Location at = center(base).add(0, 0.5, 0);
        if (!safe(bot, at, settings) || at.distance(bot.getEyeLocation()) > REACH + 1.0) {
            st.tried.add(base);
            st.step = 0;
            st.base = null;
            st.cooldown = 3;
            return true;
        }
        if (st.placeFails >= 5) {
            st.step = 0;
            st.base = null;
            st.placeFails = 0;
            st.cooldown = 5;
            return true;
        }
        BotNavigation.stop(bot);
        hold(bot, slot);
        BotControl.lookAt(bot, center(base));
        int heldSlot = bot.getInventory().getHeldItemSlot();
        EnderCrystal crystal = bot.getWorld().spawn(at, EnderCrystal.class, c -> c.setShowingBottom(false));
        if (crystal.isValid()) {
            consume(bot, heldSlot);
            bot.swingMainHand();
            st.placeFails++;
            st.cooldown = 1;
        } else {
            st.placeFails++;
        }
        return true;
    }

    private static boolean hitCrystal(Player bot, Player target, EnderCrystal crystal, State st, BotSettings settings) {
        Location cl = crystal.getLocation();
        if (!safe(bot, cl, settings)) {
            BotNavigation.moveAway(bot, target, settings.getSpeed());
            return true;
        }
        if (cl.distance(bot.getEyeLocation()) > REACH) {
            BotNavigation.moveTowardPosition(bot, cl, settings.getSpeed());
            return true;
        }
        BotNavigation.stop(bot);
        int weapon = weaponSlot(bot);
        if (weapon >= 0) {
            hold(bot, weapon);
        }
        BotControl.lookAt(bot, cl.clone().add(0, 0.5, 0));
        bot.attack(crystal);
        bot.swingMainHand();
        st.notFound = 0;
        st.placeFails = 0;
        st.step = st.base != null ? 1 : 0;
        st.cooldown = 2;
        return true;
    }

    private static void keepDistance(Player bot, Player target, BotSettings settings) {
        double d = bot.getLocation().distance(target.getLocation());
        if (d < 3.0) {
            BotNavigation.moveAway(bot, target, settings.getSpeed() * 0.7);
        } else if (d > 5.5) {
            BotNavigation.moveTowardPosition(bot, target.getLocation(), settings.getSpeed() * 0.7);
        } else {
            BotNavigation.stop(bot);
            BotControl.lookAt(bot, target.getEyeLocation());
        }
    }

    // ---- placement scoring ----

    private static Block bestPosition(Player bot, Player target, State st, BotSettings settings) {
        Block feet = target.getLocation().getBlock();
        Block best = null;
        double bestScore = -1e9;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                Block b = feet.getRelative(dx, 0, dz);
                if (st.tried.contains(b) || !free(b) || !free(b.getRelative(BlockFace.UP))) {
                    continue;
                }
                if (!supported(b) || center(b).distance(bot.getEyeLocation()) > 4.0) {
                    continue;
                }
                Location cl = center(b).add(0, 1.5, 0);
                if (!safe(bot, cl, settings)) {
                    continue;
                }
                double gain = ExplosionMath.damageTo(target, cl.toVector(), POWER, false);
                double self = ExplosionMath.damageTo(bot, cl.toVector(), POWER, false);
                double score = gain - 0.6 * self;
                if (score > bestScore) {
                    bestScore = score;
                    best = b;
                }
            }
        }
        return best;
    }

    private static Block findBase(Player bot, Player target) {
        Block feet = target.getLocation().getBlock();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    Block b = feet.getRelative(dx, dy, dz);
                    if (isBase(b.getType()) && free(b.getRelative(BlockFace.UP))
                            && free(b.getRelative(BlockFace.UP, 2))
                            && center(b).distance(bot.getEyeLocation()) <= 4.0
                            && center(b).distance(target.getLocation()) <= 5.0) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    private static boolean safe(Player bot, Location crystal, BotSettings settings) {
        double self = ExplosionMath.damageTo(bot, crystal.toVector(), POWER, false);
        double hp = bot.getHealth() + bot.getAbsorptionAmount();
        return self <= settings.getCrystalMaxSelfDamage() && hp - self > 2.0;
    }

    private static boolean supported(Block b) {
        for (BlockFace f : new BlockFace[]{BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            if (b.getRelative(f).getType().isSolid()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBase(Material m) {
        return m == Material.OBSIDIAN || m == Material.BEDROCK;
    }

    private static boolean free(Block b) {
        if (!b.getType().isAir() && !Platform.isReplaceable(b)) {
            return false;
        }
        BoundingBox box = BoundingBox.of(b);
        return b.getWorld().getNearbyEntities(box, e -> !(e instanceof org.bukkit.entity.Item)).isEmpty();
    }

    private static Location center(Block b) {
        return b.getLocation().add(0.5, 0.5, 0.5);
    }

    private static EnderCrystal nearestCrystal(Player bot, Player target, double radius) {
        EnderCrystal best = null;
        double bd = radius + 1;
        for (Entity e : target.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof EnderCrystal c) {
                double d = c.getLocation().distance(target.getLocation());
                if (d < bd) {
                    bd = d;
                    best = c;
                }
            }
        }
        return best;
    }

    // ---- inventory ----

    private static int findItem(Player bot, Material m) {
        PlayerInventory inv = bot.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack s = inv.getItem(i);
            if (s != null && s.getType() == m) {
                return i;
            }
        }
        return -1;
    }

    private static int weaponSlot(Player bot) {
        PlayerInventory inv = bot.getInventory();
        int best = -1;
        int bp = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = inv.getItem(i);
            if (s == null) {
                continue;
            }
            String n = s.getType().name();
            int p = n.endsWith("_SWORD") ? 10 : n.endsWith("_AXE") ? 5 : -1;
            if (p > bp) {
                bp = p;
                best = i;
            }
        }
        return best;
    }

    /** Puts the item from slot into the held hotbar slot (swapping if it's in the main inventory). */
    private static void hold(Player bot, int slot) {
        PlayerInventory inv = bot.getInventory();
        if (slot >= 9) {
            int held = inv.getHeldItemSlot();
            ItemStack a = inv.getItem(slot);
            ItemStack b = inv.getItem(held);
            inv.setItem(slot, b);
            inv.setItem(held, a);
        } else {
            inv.setHeldItemSlot(slot);
        }
    }

    private static void consume(Player bot, int slot) {
        ItemStack s = bot.getInventory().getItem(slot);
        if (s == null) {
            return;
        }
        if (s.getAmount() <= 1) {
            bot.getInventory().setItem(slot, null);
        } else {
            s.setAmount(s.getAmount() - 1);
            bot.getInventory().setItem(slot, s);
        }
    }
}
