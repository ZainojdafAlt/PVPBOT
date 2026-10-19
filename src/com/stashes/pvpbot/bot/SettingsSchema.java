/*
 * Decompiled with CFR 0.152.
 */
package com.stashes.pvpbot.bot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SettingsSchema {
    private static final Map<String, Entry> ENTRIES = new LinkedHashMap<String, Entry>();

    private static void add(String string, String string2, String string3, Type type, double d, double d2, String string4) {
        ENTRIES.put(string, new Entry(string, string2, string3, type, d, d2, string4));
    }

    private SettingsSchema() {
    }

    public static Entry get(String string) {
        return ENTRIES.get(string.toLowerCase(Locale.ROOT));
    }

    public static List<String> keys() {
        return new ArrayList<String>(ENTRIES.keySet());
    }

    public static List<Entry> all() {
        return new ArrayList<Entry>(ENTRIES.values());
    }

    public static Parsed parse(Entry entry, String string) {
        switch (entry.type().ordinal()) {
            case 0: {
                if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("on")) {
                    return new Parsed(Boolean.TRUE, null);
                }
                if (string.equalsIgnoreCase("false") || string.equalsIgnoreCase("off")) {
                    return new Parsed(Boolean.FALSE, null);
                }
                return new Parsed(null, "expected true or false");
            }
            case 1: {
                try {
                    int n = Integer.parseInt(string);
                    if ((double)n < entry.min() || (double)n > entry.max()) {
                        return new Parsed(null, "must be between " + (int)entry.min() + " and " + (int)entry.max());
                    }
                    return new Parsed(n, null);
                }
                catch (NumberFormatException numberFormatException) {
                    return new Parsed(null, "expected a whole number");
                }
            }
        }
        try {
            double d = Double.parseDouble(string);
            if (d < entry.min() || d > entry.max()) {
                return new Parsed(null, "must be between " + entry.min() + " and " + entry.max());
            }
            return new Parsed(d, null);
        }
        catch (NumberFormatException numberFormatException) {
            return new Parsed(null, "expected a number");
        }
    }

    public static List<String> suggestions(Entry entry) {
        return switch (entry.type().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> List.of("true", "false");
            case 1 -> List.of(String.valueOf((int)entry.min()), String.valueOf((int)entry.max()));
            case 2 -> List.of(String.valueOf(entry.min()), String.valueOf(entry.max()));
        };
    }

    static {
        SettingsSchema.add("check-interval", "general.check-interval", "general", Type.INT, 1.0, 200.0, "ticks between auto-equip passes");
        SettingsSchema.add("bots-relog", "general.bots-relog", "general", Type.BOOL, 0.0, 0.0, "respawn bots after a restart");
        SettingsSchema.add("force-vulnerable", "general.force-vulnerable", "general", Type.BOOL, 0.0, 0.0, "clear the invulnerable flag on bots (login plugins set it and never unset it)");
        SettingsSchema.add("auth-autologin", "general.auth-autologin", "general", Type.BOOL, 0.0, 0.0, "register bots with the login plugin so it stops freezing them");
        SettingsSchema.add("bypass-damage-protection", "general.bypass-damage-protection", "general", Type.BOOL, 0.0, 0.0, "un-cancel damage involving bots - overrides region/PvP protection, use with care");
        SettingsSchema.add("target-scan-interval", "general.target-scan-interval", "general", Type.INT, 1.0, 60.0, "ticks between target sweeps (raise this if a big crowd lags)");
        SettingsSchema.add("auto-target", "combat.auto-target", "combat", Type.BOOL, 0.0, 0.0, "hunt the nearest enemy unprompted");
        SettingsSchema.add("target-players", "combat.target-players", "combat", Type.BOOL, 0.0, 0.0, "auto-target players (off = bots only hunt mobs unless ordered or hit)");
        SettingsSchema.add("target-passive-mobs", "combat.target-passive-mobs", "combat", Type.BOOL, 0.0, 0.0, "auto-target passive and neutral mobs too (off = hostile mobs only)");
        SettingsSchema.add("revenge", "combat.revenge", "combat", Type.BOOL, 0.0, 0.0, "fight back against whoever hit us");
        SettingsSchema.add("friendly-fire", "combat.friendly-fire", "combat", Type.BOOL, 0.0, 0.0, "allow hitting allies");
        SettingsSchema.add("bot-vs-bot", "combat.bot-vs-bot", "combat", Type.BOOL, 0.0, 0.0, "bots pick fights with each other unprompted");
        SettingsSchema.add("attack-invincible", "combat.attack-invincible", "combat", Type.BOOL, 0.0, 0.0, "attack creative/spectator players");
        SettingsSchema.add("max-target-distance", "combat.max-target-distance", "combat", Type.DOUBLE, 1.0, 128.0, "how far a bot looks for targets");
        SettingsSchema.add("melee-range", "combat.melee-range", "combat", Type.DOUBLE, 1.0, 6.0, "reach, eyes to hitbox as vanilla measures it (3.0 = a player's)");
        SettingsSchema.add("melee-standoff", "combat.melee-standoff", "combat", Type.DOUBLE, 0.5, 5.0, "fighting distance held in melee (stops bots standing inside each other)");
        SettingsSchema.add("attack-cooldown", "combat.attack-cooldown", "combat", Type.INT, 1.0, 100.0, "ticks between swings (lower = faster)");
        SettingsSchema.add("ping-millis", "combat.ping-millis", "combat", Type.INT, 0.0, 500.0, "simulated connection latency - delays when a decided attack actually lands, the same round trip a real player's client has (0 = instant, unrealistically perfect)");
        SettingsSchema.add("miss-chance", "combat.miss-chance", "combat", Type.INT, 0.0, 100.0, "percent of swings/shots deliberately missed");
        SettingsSchema.add("mistake-chance", "combat.mistake-chance", "combat", Type.INT, 0.0, 100.0, "percent chance to hesitate or panic on a clutch action (0 = flawless)");
        SettingsSchema.add("leader-call-seconds", "combat.leader-call-seconds", "combat", Type.INT, 0.0, 600.0, "how long a faction stays hostile to whoever its leader is fighting (0 = off)");
        SettingsSchema.add("reaction-ticks", "combat.reaction-ticks", "combat", Type.INT, 0.0, 200.0, "ticks a bot sizes up a player it noticed itself before attacking (0 = instant)");
        SettingsSchema.add("spawn-grace", "combat.spawn-grace", "combat", Type.INT, 0.0, 600.0, "seconds after spawning before a bot picks a fight with a player on its own");
        SettingsSchema.add("skill", "combat.skill", "combat", Type.INT, 0.0, 100.0, "how human bots' reactions, timing and aim are - never frame-perfect, even at 100");
        SettingsSchema.add("turn-speed", "combat.turn-speed", "combat", Type.DOUBLE, 1.0, 180.0, "degrees a bot may turn per tick (180 = instant snap-aim, the old behaviour)");
        SettingsSchema.add("special-weapon-melee-chance", "combat.special-weapon-melee-chance", "combat", Type.INT, 0.0, 100.0, "percent of engagements a mace carrier fights with an ordinary weapon instead");
        SettingsSchema.add("shield-disable-chance", "combat.shield-disable-chance", "combat", Type.INT, 0.0, 100.0, "percent of raised shields a bot actually reacts to by swapping to an axe");
        SettingsSchema.add("mace-range-bonus", "combat.mace-range-bonus", "combat", Type.DOUBLE, 0.0, 4.0, "extra reach on a smash, on top of melee-range (a deliberate cheat)");
        SettingsSchema.add("attribute-swap", "combat.attribute-swap", "combat", Type.BOOL, 0.0, 0.0, "master switch for weapon-swap techniques (breach swap, mace stun, lunge)");
        SettingsSchema.add("breach-swap", "combat.breach-swap", "combat", Type.BOOL, 0.0, 0.0, "swing a sword carrying the mace's Breach/Density enchantments");
        SettingsSchema.add("mace-stun", "combat.mace-stun", "combat", Type.BOOL, 0.0, 0.0, "axe to break a raised shield, then the mace straight after without recharging");
        SettingsSchema.add("spear-lunge", "combat.spear-lunge", "combat", Type.BOOL, 0.0, 0.0, "chain spear jabs to close distance (needs Lunge to actually dash)");
        SettingsSchema.add("swap-cooldown", "combat.swap-cooldown", "combat", Type.INT, 0.0, 200.0, "ticks between swap combos - the real rate limit, since combos skip the attack gate");
        SettingsSchema.add("mace-pearl-launch", "combat.mace-pearl-launch", "combat", Type.BOOL, 0.0, 0.0, "throw a pearl then pop a wind charge on it mid-flight for a much higher launch");
        SettingsSchema.add("mace-defence-shield", "combat.mace-defence-shield", "combat", Type.INT, 0.0, 100.0, "percent of incoming mace smashes answered with a shield");
        SettingsSchema.add("mace-defence-flee", "combat.mace-defence-flee", "combat", Type.INT, 0.0, 100.0, "percent answered by getting out of the way instead (remainder: eat it)");
        SettingsSchema.add("mace-totem-chance", "combat.mace-totem-chance", "combat", Type.INT, 0.0, 100.0, "percent of mace fights where the bot puts a totem in the offhand and keeps it there instead of a shield");
        SettingsSchema.add("mace", "combat.mace", "combat", Type.BOOL, 0.0, 0.0, "use mace jump attacks");
        SettingsSchema.add("elytra-mace", "combat.elytra-mace", "combat", Type.BOOL, 0.0, 0.0, "elytra mace: climb on rockets, dive, and smash without landing (needs elytra, rockets, mace)");
        SettingsSchema.add("elytra-mace-height", "combat.elytra-mace-height", "combat", Type.INT, 8.0, 60.0, "blocks above the target an elytra mace climbs to before diving - higher hits harder");
        SettingsSchema.add("cart-pvp", "combat.cart-pvp", "combat", Type.BOOL, 0.0, 0.0, "cart PvP: a Flame arrow, then a rail and a TNT minecart in its path (needs all three, plus arrows)");
        SettingsSchema.add("cart-max-self-damage", "combat.cart-max-self-damage", "combat", Type.DOUBLE, 0.0, 40.0, "most damage (after armour, on an average roll) a bot accepts from its own cart; never a lethal roll without a totem");
        SettingsSchema.add("cart-cooldown", "combat.cart-cooldown", "combat", Type.INT, 0.0, 1200.0, "ticks between cart plays");
        SettingsSchema.add("crystal-pvp", "combat.crystal-pvp", "combat", Type.BOOL, 0.0, 0.0, "crystal PvP: obsidian beside the target, an end crystal on it, then hit it (needs obsidian and end crystals)");
        SettingsSchema.add("crystal-max-self-damage", "combat.crystal-max-self-damage", "combat", Type.DOUBLE, 0.0, 40.0, "most damage a bot accepts from its own crystal; it never detonates a crystal that would hurt it more");
        SettingsSchema.add("crit-chain", "combat.crit-chain", "combat", Type.BOOL, 0.0, 0.0, "chain jump-crits back to back instead of resting between hops");
        SettingsSchema.add("cooldown-trick", "combat.cooldown-trick", "combat", Type.BOOL, 0.0, 0.0, "swing once attack cooldown clears 84.8% instead of waiting for 100% - still full crit/knockback, just faster");
        SettingsSchema.add("jump-reset", "combat.jump-reset", "combat", Type.BOOL, 0.0, 0.0, "chain another mace smash right after landing one, instead of resetting on cooldown");
        SettingsSchema.add("mirror-punish", "combat.mirror-punish", "combat", Type.BOOL, 0.0, 0.0, "swing back mid-air off the enemy's own knockback the instant they hit us (a punish crit)");
        SettingsSchema.add("hit-select", "combat.hit-select", "combat", Type.BOOL, 0.0, 0.0, "hold a swing back until the angle/spacing is actually favourable, instead of swinging on cooldown alone");
        SettingsSchema.add("d-tap", "combat.d-tap", "combat", Type.BOOL, 0.0, 0.0, "brief sideways sprint-reset after landing a hit, the same idea as w-tap on a lateral axis");
        SettingsSchema.add("hit-web-combo", "combat.hit-web-combo", "combat", Type.BOOL, 0.0, 0.0, "follow a landed cobweb trap with an immediate swing instead of leaving it to work alone");
        SettingsSchema.add("sweep-hit", "combat.sweep-hit", "combat", Type.BOOL, 0.0, 0.0, "drop sprint to land vanilla's sweep AOE when 2+ enemies are clustered in melee range");
        SettingsSchema.add("priority-hp-target", "combat.priority-hp-target", "combat", Type.BOOL, 0.0, 0.0, "within a priority tier, prefer the lowest-health target over the nearest one");
        SettingsSchema.add("shield-read-windup", "combat.shield-read-windup", "combat", Type.BOOL, 0.0, 0.0, "raise shield proactively against a read jump-crit windup, not just an incoming mace smash");
        SettingsSchema.add("orbital", "combat.orbital", "combat", Type.BOOL, 0.0, 0.0, "fire Orbital Strike Cannon rods, keeping clear of their own blast");
        SettingsSchema.add("orbital-cooldown", "combat.orbital-cooldown", "combat", Type.INT, 20.0, 2000.0, "ticks between orbital strikes");
        SettingsSchema.add("crits", "combat.crits", "combat", Type.BOOL, 0.0, 0.0, "jump before swinging to land critical hits");
        SettingsSchema.add("stasis", "combat.stasis", "combat", Type.BOOL, 0.0, 0.0, "block a killing hit and teleport away (ender pearl effects) instead of dying - off makes a protected faction killable again");
        SettingsSchema.add("shield-disable", "combat.shield-disable", "combat", Type.BOOL, 0.0, 0.0, "switch to an axe to break a blocking enemy's shield");
        SettingsSchema.add("break-stashes", "combat.break-stashes", "combat", Type.BOOL, 0.0, 0.0, "break a shulker box or ender chest the current target placed nearby, denying a mid-fight restock");
        SettingsSchema.add("bhop", "movement.bhop", "movement", Type.BOOL, 0.0, 0.0, "jump constantly while chasing");
        SettingsSchema.add("speed", "movement.speed", "movement", Type.DOUBLE, 0.1, 2.0, "movement throttle (1.0 = sprint)");
        SettingsSchema.add("wander", "movement.wander", "movement", Type.BOOL, 0.0, 0.0, "walk around when idle");
        SettingsSchema.add("follow-distance", "movement.follow-distance", "movement", Type.DOUBLE, 1.0, 20.0, "how far a follower sits from whoever it is escorting");
        SettingsSchema.add("auto-equip", "utility.auto-equip", "utility", Type.BOOL, 0.0, 0.0, "wear the best armour/weapon carried");
        SettingsSchema.add("auto-repair", "utility.auto-repair", "utility", Type.BOOL, 0.0, 0.0, "collect nearby XP orbs and mend damaged gear with them");
        SettingsSchema.add("auto-repair-bottles", "utility.auto-repair-bottles", "utility", Type.BOOL, 0.0, 0.0, "crack open carried XP bottles to mend gear once nearby orbs run out");
        SettingsSchema.add("auto-totem", "utility.auto-totem", "utility", Type.BOOL, 0.0, 0.0, "grab a totem when close to death");
        SettingsSchema.add("totem-health", "utility.totem-health", "utility", Type.DOUBLE, 0.0, 20.0, "health at which to panic-swap a totem in");
        SettingsSchema.add("retreat", "utility.retreat", "utility", Type.BOOL, 0.0, 0.0, "back off when low");
        SettingsSchema.add("retreat-health", "utility.retreat-health", "utility", Type.DOUBLE, 0.0, 20.0, "health at which to retreat");
        SettingsSchema.add("auto-food", "utility.auto-food", "utility", Type.BOOL, 0.0, 0.0, "eat when hungry");
        SettingsSchema.add("food-threshold", "utility.food-threshold", "utility", Type.INT, 0.0, 20.0, "food level below which to eat");
        SettingsSchema.add("auto-shield", "utility.auto-shield", "utility", Type.BOOL, 0.0, 0.0, "raise a shield when hurt");
        SettingsSchema.add("shield-health", "utility.shield-health", "utility", Type.DOUBLE, 0.0, 20.0, "health at which to start blocking");
        SettingsSchema.add("auto-potion", "utility.auto-potion", "utility", Type.BOOL, 0.0, 0.0, "drink healing potions / eat gapples");
        SettingsSchema.add("potion-health", "utility.potion-health", "utility", Type.DOUBLE, 0.0, 20.0, "health at which to heal");
        SettingsSchema.add("buff-potions", "utility.buff-potions", "utility", Type.BOOL, 0.0, 0.0, "drink strength/speed/fire-res before engaging");
        SettingsSchema.add("pearls", "utility.pearls", "utility", Type.BOOL, 0.0, 0.0, "use ender pearls to escape and to chase runners");
        SettingsSchema.add("pearl-escape-health", "utility.pearl-escape-health", "utility", Type.DOUBLE, 0.0, 20.0, "health at which to pearl out");
        SettingsSchema.add("pearl-chase-distance", "utility.pearl-chase-distance", "utility", Type.DOUBLE, 4.0, 64.0, "how far a fleeing target must be before pearling after them");
        SettingsSchema.add("pearl-cooldown", "utility.pearl-cooldown", "utility", Type.INT, 20.0, 600.0, "ticks between pearls");
        SettingsSchema.add("cobwebs", "utility.cobwebs", "utility", Type.BOOL, 0.0, 0.0, "web enemies to pin them");
        SettingsSchema.add("cobweb-range", "utility.cobweb-range", "utility", Type.DOUBLE, 0.5, 6.0, "how close before webbing (lower = must commit)");
        SettingsSchema.add("cobweb-cooldown", "utility.cobweb-cooldown", "utility", Type.INT, 20.0, 600.0, "ticks between webs from one bot");
        SettingsSchema.add("cobweb-global-cooldown", "utility.cobweb-global-cooldown", "utility", Type.INT, 0.0, 600.0, "ticks between webs from ANY bot - stops a squad chain-webbing you (0 = off)");
        SettingsSchema.add("drain", "utility.drain", "utility", Type.BOOL, 0.0, 0.0, "scoop up water an enemy places (MLG denial)");
        SettingsSchema.add("drain-delay", "utility.drain-delay", "utility", Type.INT, 0.0, 100.0, "reaction delay in ticks before draining");
        SettingsSchema.add("web-escape-delay", "utility.web-escape-delay", "utility", Type.INT, 0.0, 100.0, "ticks a webbed bot takes to react and reach for its water bucket (each web rolls up to 50% more)");
        SettingsSchema.add("web-escape-fumble-chance", "utility.web-escape-fumble-chance", "utility", Type.INT, 0.0, 100.0, "percent of water-escape attempts a webbed bot botches, wasting a beat before it can try again");
        SettingsSchema.add("totem-delay-ticks", "utility.totem-delay-ticks", "utility", Type.INT, 0.0, 60.0, "pause before a panic totem reaches the offhand (instant reads as inhuman)");
        SettingsSchema.add("water-clutch", "utility.water-clutch", "utility", Type.BOOL, 0.0, 0.0, "MLG a water bucket to survive a long fall");
        SettingsSchema.add("water-clutch-min-fall", "utility.water-clutch-min-fall", "utility", Type.DOUBLE, 0.0, 64.0, "shortest fall worth a bucket (below this the bot just takes the damage)");
        SettingsSchema.add("water-clutch-window", "utility.water-clutch-window", "utility", Type.DOUBLE, 1.0, 32.0, "blocks above the ground the water goes down (widens with falling speed)");
        SettingsSchema.add("water-clutch-pickup", "utility.water-clutch-pickup", "utility", Type.BOOL, 0.0, 0.0, "scoop the water back up after landing");
        SettingsSchema.add("ranged", "ranged.enabled", "ranged", Type.BOOL, 0.0, 0.0, "use bows and crossbows");
        SettingsSchema.add("ranged-min-range", "ranged.min-range", "ranged", Type.DOUBLE, 0.0, 64.0, "closer than this, switch to melee");
        SettingsSchema.add("ranged-optimal-range", "ranged.optimal-range", "ranged", Type.DOUBLE, 1.0, 64.0, "distance to hold while shooting");
        SettingsSchema.add("bow-min-draw-ticks", "ranged.bow-min-draw-ticks", "ranged", Type.INT, 1.0, 60.0, "draw time before loosing (20 = full)");
        SettingsSchema.add("arrow-prediction", "ranged.arrow-prediction", "ranged", Type.BOOL, 0.0, 0.0, "lead shots ahead of moving targets");
        SettingsSchema.add("ranged-shot-gap", "ranged.shot-gap", "ranged", Type.INT, 0.0, 200.0, "minimum ticks between one arrow and the next draw");
        SettingsSchema.add("ranged-volley-size", "ranged.volley-size", "ranged", Type.INT, 1.0, 20.0, "most arrows fired in a row before the bot lowers the bow (each volley is 1 to this many)");
        SettingsSchema.add("ranged-rest-ticks", "ranged.rest-ticks", "ranged", Type.INT, 0.0, 600.0, "how long the bot stops shooting after a volley, closing in instead");
        SettingsSchema.add("building", "building.enabled", "building", Type.BOOL, 0.0, 0.0, "let bots bridge, pillar and clear soft cover (never stone or anything built)");
        SettingsSchema.add("building-bridge", "building.bridge", "building", Type.BOOL, 0.0, 0.0, "place a block to cross a gap");
        SettingsSchema.add("building-pillar", "building.pillar", "building", Type.BOOL, 0.0, 0.0, "tower up toward a target that is above and unreachable");
        SettingsSchema.add("building-clear-soft", "building.clear-soft-blocks", "building", Type.BOOL, 0.0, 0.0, "dig through dirt/sand/gravel/leaves/undergrowth in the way");
        SettingsSchema.add("building-cooldown", "building.cooldown", "building", Type.INT, 0.0, 200.0, "ticks between block actions");
        SettingsSchema.add("building-budget", "building.budget", "building", Type.INT, 0.0, 2000.0, "most blocks a bot may place+break per minute");
        SettingsSchema.add("building-max-pillar", "building.max-pillar", "building", Type.INT, 0.0, 64.0, "tallest tower a bot will build");
        SettingsSchema.add("detection", "detection.enabled", "detection", Type.BOOL, 0.0, 0.0, "bots must actually see a target to know about it (off = old plain-radius sweep)");
        SettingsSchema.add("detection-touch-radius", "detection.touch-radius", "detection", Type.DOUBLE, 0.0, 16.0, "within this, noticed without needing line of sight");
        SettingsSchema.add("detection-memory-ticks", "detection.memory-ticks", "detection", Type.INT, 0.0, 1200.0, "how long a spotted target stays known after it breaks line of sight");
        SettingsSchema.add("detection-invisible", "detection.invisible-multiplier", "detection", Type.DOUBLE, 0.0, 2.0, "notice range against an invisible player (0.15 = spotted at 15% of the usual distance)");
        SettingsSchema.add("detection-sneaking", "detection.sneaking-multiplier", "detection", Type.DOUBLE, 0.0, 2.0, "notice range against a crouching player");
        SettingsSchema.add("detection-armoured", "detection.armoured-multiplier", "detection", Type.DOUBLE, 0.0, 2.0, "notice range against a player wearing armour");
        SettingsSchema.add("detection-unarmoured", "detection.unarmoured-multiplier", "detection", Type.DOUBLE, 0.0, 2.0, "notice range against a player wearing none");
    }

    public record Entry(String key, String path, String group, Type type, double min, double max, String help) {
    }

    public static enum Type {
        BOOL,
        INT,
        DOUBLE;

    }

    public record Parsed(Object value, String error) {
        public boolean ok() {
            return this.error == null;
        }
    }
}

