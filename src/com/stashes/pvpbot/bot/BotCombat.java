/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.entity.Enemy
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Mob
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.util.Vector
 */
package com.stashes.pvpbot.bot;

import com.stashes.pvpbot.PvpBotPlugin;
import com.stashes.pvpbot.bot.BotAwareness;
import com.stashes.pvpbot.bot.BotCart;
import com.stashes.pvpbot.bot.BotClutch;
import com.stashes.pvpbot.bot.BotElytra;
import com.stashes.pvpbot.bot.BotFaction;
import com.stashes.pvpbot.bot.BotFollow;
import com.stashes.pvpbot.bot.BotGuard;
import com.stashes.pvpbot.bot.BotHide;
import com.stashes.pvpbot.bot.BotHuman;
import com.stashes.pvpbot.bot.BotLunge;
import com.stashes.pvpbot.bot.BotMace;
import com.stashes.pvpbot.bot.BotManager;
import com.stashes.pvpbot.bot.BotMotion;
import com.stashes.pvpbot.bot.BotNavigation;
import com.stashes.pvpbot.bot.BotObjective;
import com.stashes.pvpbot.bot.BotOrbital;
import com.stashes.pvpbot.bot.BotPatrol;
import com.stashes.pvpbot.bot.BotPearl;
import com.stashes.pvpbot.bot.BotPing;
import com.stashes.pvpbot.bot.BotRanged;
import com.stashes.pvpbot.bot.BotReach;
import com.stashes.pvpbot.bot.BotRepairRetreat;
import com.stashes.pvpbot.bot.BotRetreat;
import com.stashes.pvpbot.bot.BotSettings;
import com.stashes.pvpbot.bot.BotStates;
import com.stashes.pvpbot.bot.BotSwap;
import com.stashes.pvpbot.bot.BotUtils;
import com.stashes.pvpbot.nms.BotControl;
import com.stashes.pvpbot.platform.Platform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class BotCombat {
    private static final Map<String, State> states = BotStates.map();
    private static final Random RANDOM = new Random();
    private static final double FOLLOW_MOB_TOLERANCE = 3.0;
    private static final int HIT_SELECT_SETTLE_TICKS = 3;
    private static final int SWEEP_CLUSTER_SIZE = 2;
    private static final float METER_PER_TICK = 0.08f;
    private static final Map<String, double[]> damageDealt = BotStates.map();
    private static final double OVERHEAD_MIN_HEIGHT = 3.0;
    private static final double OVERHEAD_MAX_HORIZONTAL = 14.0;
    private static final double SPREAD_RING_MIN = 5.5;
    private static final double SPREAD_MIN_GAP = 4.0;

    public static boolean seesShield(String string) {
        State state = states.get(string);
        return state != null && state.targetBlockTicks > state.shieldReaction;
    }

    public static boolean isAxeHold(String string) {
        State state = states.get(string);
        return state != null && state.axeHold;
    }

    public static State getState(String string2) {
        return states.computeIfAbsent(string2, string -> new State());
    }

    public static void recordDamageDealt(String string2, double d) {
        double[] dArray = damageDealt.computeIfAbsent(string2, string -> new double[2]);
        dArray[0] = d;
        dArray[1] = Math.max(dArray[1], d);
    }

    public static boolean isRecovering(String string) {
        State state = states.get(string);
        return state != null && state.attackCooldown > 2;
    }

    public static String critReport(String string) {
        State state = states.get(string);
        if (state == null) {
            return "no state";
        }
        return String.format("jumps=%d critHits=%d window=%d rest=%d | fallen=%.2f grounded=%s vertVel=%.3f", state.critJumps, state.critHits, state.critWindow, state.critRest, state.lastFallen, state.lastGrounded, state.lastVertVel);
    }

    public static double[] damageStats(String string) {
        return damageDealt.get(string);
    }

    public static Entity getTarget(String string) {
        return BotCombat.getState((String)string).target;
    }

    public static void setForcedTarget(String string, String string2) {
        BotCombat.getState((String)string).forcedTargetName = string2;
    }

    public static void clearTarget(String string) {
        State state = BotCombat.getState(string);
        state.forcedTargetName = null;
        state.target = null;
        state.lastAttacker = null;
        state.sizingUp = null;
        BotRetreat.clear(string);
        BotHide.setHiding(string, false);
    }

    private static void trackEvasion(State state, Entity entity) {
        if (entity == null) {
            state.lastTargetPos = null;
            state.lastTargetDir = null;
            state.evasion = 0.0;
            return;
        }
        Location location = entity.getLocation();
        if (state.lastTargetPos == null || state.lastTargetPos.getWorld() != location.getWorld()) {
            state.lastTargetPos = location;
            state.lastTargetDir = null;
            return;
        }
        Vector vector = location.toVector().subtract(state.lastTargetPos.toVector());
        vector.setY(0);
        state.lastTargetPos = location;
        double d = vector.length();
        if (d < 0.02) {
            state.evasion *= 0.85;
            return;
        }
        Vector vector2 = vector.multiply(1.0 / d);
        if (state.lastTargetDir != null) {
            double d2 = state.lastTargetDir.distance(vector2) / 2.0;
            state.evasion = state.evasion * 0.75 + d2 * 0.25;
        }
        state.lastTargetDir = vector2;
    }

    public static void recordAttacker(String string, LivingEntity livingEntity) {
        State state = BotCombat.getState(string);
        state.lastAttacker = livingEntity;
        state.lastAttackTime = System.currentTimeMillis();
    }

    public static void update(Player player, BotSettings botSettings) {
        State state = BotCombat.getState(player.getName());
        ++state.age;
        if (state.attackCooldown > 0) {
            --state.attackCooldown;
        }
        if (BotClutch.update(player, botSettings)) {
            return;
        }
        Entity entity = BotCombat.findTarget(player, state, botSettings);
        if (entity != null && !(entity instanceof Player) && BotFollow.isFollowing(player.getName()) && player.getLocation().distance(entity.getLocation()) > botSettings.getMeleeRange() + 3.0) {
            entity = null;
        }
        if (entity != null && !BotGuard.withinPost(player, entity)) {
            entity = null;
            state.forcedTargetName = null;
            state.lastAttacker = null;
        }
        if (BotCombat.holdsFire(state, botSettings, entity)) {
            entity = null;
        }
        state.target = entity;
        BotCombat.trackEvasion(state, entity);
        state.axeHold = false;
        BotHide.tick(player, entity);
        boolean bl = BotHide.isHiding(player.getName());
        if (BotUtils.update(player, botSettings, entity)) {
            return;
        }
        if (BotElytra.isGliding(player.getName())) {
            return;
        }
        if (BotPearl.update(player, entity, botSettings)) {
            return;
        }
        if (BotRepairRetreat.update(player, entity, botSettings)) {
            return;
        }
        if (BotRetreat.update(player, entity, botSettings)) {
            return;
        }
        if (entity == null || bl) {
            if (BotGuard.update(player, botSettings)) {
                return;
            }
            if (BotPatrol.update(player, botSettings)) {
                return;
            }
            if (BotFollow.update(player, botSettings)) {
                return;
            }
            if (BotObjective.update(player, botSettings)) {
                return;
            }
            if (state.sizingUp != null && !bl) {
                BotNavigation.stop(player);
                BotControl.turnToward(player, state.sizingUp.getEyeLocation(), botSettings.getTurnSpeed());
                return;
            }
            if (bl) {
                BotNavigation.stop(player);
                return;
            }
            BotCombat.wander(player, botSettings);
            return;
        }
        Location location = entity.getLocation();
        double d = player.getLocation().distance(location);
        if (BotOrbital.update(player, entity, botSettings)) {
            return;
        }
        if (BotElytra.update(player, entity, botSettings)) {
            return;
        }
        if (BotCart.update(player, entity, botSettings)) {
            return;
        }
        if (BotCrystal.update(player, entity, botSettings)) {
            return;
        }
        if (BotMace.update(player, entity, botSettings)) {
            return;
        }
        if (BotRanged.update(player, entity, botSettings)) {
            return;
        }
        BotControl.turnToward(player, location.clone().add(0.0, entity.getHeight() * 0.5, 0.0), botSettings.getTurnSpeed());
        boolean bl2 = BotReach.inReach(player, entity, botSettings.getMeleeRange());
        boolean bl3 = bl2 && !BotReach.hasClearStrike(player, entity);
        boolean bl4 = BotCombat.isOverheadAirborne(player, entity);
        if (!bl2 || bl3) {
            state.ticksInRange = 0;
            if (bl4) {
                BotCombat.spreadBeneath(player, entity, botSettings);
            } else if (d > botSettings.getMeleeRange() + 2.0) {
                BotLunge.chase(player, entity, botSettings);
                BotNavigation.moveTowardPosition(player, location, botSettings.getSpeed());
            } else if (bl3) {
                BotNavigation.moveTowardPosition(player, location, botSettings.getSpeed());
            } else {
                BotNavigation.moveTowardCombat(player, location, botSettings.getSpeed());
            }
        } else if (BotUtils.dodgingMace(player.getName())) {
            state.ticksInRange = 0;
            BotNavigation.moveAway(player, entity, botSettings.getSpeed());
        } else {
            boolean bl5;
            int n;
            boolean bl6;
            boolean bl7;
            ++state.ticksInRange;
            if (bl4) {
                BotCombat.spreadBeneath(player, entity, botSettings);
            } else {
                BotNavigation.circleTarget(player, location, botSettings.getSpeed() * 0.6, botSettings.getMeleeStandoff());
            }
            if (BotCombat.isBlocking(entity)) {
                if (state.targetBlockTicks++ == 0) {
                    state.shieldReaction = BotHuman.reaction(botSettings);
                }
            } else {
                state.targetBlockTicks = 0;
                state.shieldNoticed = null;
            }
            boolean bl8 = state.shieldNoticed == null;
            boolean bl9 = bl7 = botSettings.isShieldDisable() && BotCombat.seesShield(player.getName()) && BotCombat.noticesShield(state, botSettings);
            if (bl7) {
                BotCombat.equipAxe(player);
                state.axeHold = true;
                if (bl8) {
                    state.axeSwingIn = 1 + Math.max(0, BotHuman.timing(botSettings));
                }
            } else {
                state.axeSwingIn = -1;
            }
            if (BotUtils.canAttack(player) && BotSwap.tryCombo(player, entity, botSettings)) {
                state.attackCooldown = botSettings.getAttackCooldown();
                return;
            }
            if (bl7 && state.axeSwingIn > 0 && --state.axeSwingIn == 0 && BotUtils.canAttack(player)) {
                state.axeSwingIn = -1;
                BotUtils.lowerShield(player);
                BotCombat.attack(player, entity, botSettings, state);
                return;
            }
            float f = botSettings.isCooldownTrick() ? 0.848f : 1.0f;
            boolean bl10 = bl6 = !botSettings.isHitSelect() || state.ticksInRange >= 3;
            if (state.swingTiming == Integer.MIN_VALUE) {
                state.swingTiming = BotHuman.timing(botSettings);
            }
            boolean bl11 = state.attackCooldown <= (n = Math.max(0, -state.swingTiming)) && player.getAttackCooldown() >= f - (float)n * 0.08f;
            state.readyTicks = bl11 ? state.readyTicks + 1 : 0;
            boolean bl12 = bl5 = bl11 && state.readyTicks > Math.max(0, state.swingTiming) && BotUtils.canAttack(player) && bl6;
            if (bl5) {
                BotUtils.lowerShield(player);
                if (botSettings.isCrits()) {
                    BotCombat.critAttack(player, entity, botSettings, state);
                } else {
                    BotCombat.attack(player, entity, botSettings, state);
                }
            }
        }
    }

    private static boolean isOverheadAirborne(Player player, Entity entity) {
        Player player2;
        if (!(entity instanceof Player) || (player2 = (Player)entity).isOnGround() || player2.isInsideVehicle()) {
            return false;
        }
        Location location = player.getLocation();
        Location location2 = player2.getLocation();
        if (location2.getY() - location.getY() < 3.0) {
            return false;
        }
        return Math.hypot(location2.getX() - location.getX(), location2.getZ() - location.getZ()) <= 14.0;
    }

    private static void spreadBeneath(Player player, Entity entity, BotSettings botSettings) {
        Vector vector = BotMotion.of(entity);
        double d = entity.getLocation().getX() + Math.max(-6.0, Math.min(6.0, vector.getX() * 10.0));
        double d2 = entity.getLocation().getZ() + Math.max(-6.0, Math.min(6.0, vector.getZ() * 10.0));
        Location location = new Location(player.getWorld(), d, player.getLocation().getY(), d2);
        int n = BotCombat.mix(player.getName().hashCode());
        double d3 = Math.toRadians(Math.floorMod(n, 360));
        double d4 = 5.5 + (double)Math.floorMod(n >>> 9, 3);
        Location location2 = location.clone().add(Math.cos(d3) * d4, 0.0, Math.sin(d3) * d4);
        for (Player player2 : Platform.nearbyPlayers(player.getWorld(), location2, 4.0)) {
            double d5;
            if (player2.equals((Object)player) || player2.equals((Object)entity)) continue;
            double d6 = location2.getX() - player2.getLocation().getX();
            double d7 = Math.hypot(d6, d5 = location2.getZ() - player2.getLocation().getZ());
            if (d7 < 0.001) {
                d6 = Math.cos(d3);
                d5 = Math.sin(d3);
                d7 = 1.0;
            }
            double d8 = 4.0 - d7;
            location2.add(d6 / d7 * d8, 0.0, d5 / d7 * d8);
        }
        if (player.getLocation().distance(location2) > 2.0) {
            BotNavigation.moveTowardPosition(player, location2, botSettings.getSpeed());
        } else {
            BotNavigation.circleTarget(player, location, botSettings.getSpeed() * 0.5, d4);
        }
    }

    private static int mix(int n) {
        n ^= n >>> 16;
        n *= -2048144789;
        n ^= n >>> 13;
        n *= -1028477387;
        n ^= n >>> 16;
        return n;
    }

    private static void critAttack(Player player, Entity entity, BotSettings botSettings, State state) {
        boolean bl;
        double d;
        boolean bl2 = BotControl.onGround(player);
        double d2 = player.getLocation().getY();
        state.critPeakY = bl2 ? d2 : Math.max(state.critPeakY, d2);
        state.lastFallen = d = state.critPeakY - d2;
        state.lastGrounded = bl2;
        state.lastVertVel = player.getVelocity().getY();
        boolean bl3 = !bl2 && d > 0.1 && player.getVelocity().getY() < 0.0 && !player.isInWater() && !player.isClimbing() && !player.isInsideVehicle();
        double d3 = player.getVelocity().getY();
        if (state.critWindow > 0 && state.critTiming < 0 && !bl2 && d3 >= 0.0 && d3 < (double)(-state.critTiming) * 0.1) {
            state.critWindow = 0;
            BotCombat.attack(player, entity, botSettings, state);
            return;
        }
        if (state.critWindow > 0 && bl2 && state.descendTicks > 0) {
            state.critWindow = 0;
            BotCombat.attack(player, entity, botSettings, state);
            return;
        }
        boolean bl4 = bl = !bl2 && !bl3 && player.getVelocity().getY() > 0.05;
        if (botSettings.isMirrorPunish() && bl) {
            return;
        }
        if (bl3) {
            if (++state.descendTicks <= Math.max(0, state.critTiming)) {
                return;
            }
            ++state.critHits;
            state.critWindow = 0;
            player.setSprinting(false);
            BotCombat.attack(player, entity, botSettings, state);
            return;
        }
        if (state.critWindow > 0) {
            --state.critWindow;
            if (state.critWindow == 0) {
                BotCombat.attack(player, entity, botSettings, state);
            }
            return;
        }
        if (botSettings.isSweepHit() && bl2 && BotCombat.clusteredEnemyCount(player, entity, botSettings) >= 2) {
            player.setSprinting(false);
            BotCombat.attack(player, entity, botSettings, state);
            return;
        }
        if (state.critRest > 0) {
            --state.critRest;
            BotCombat.attack(player, entity, botSettings, state);
            return;
        }
        if (bl2 && !player.isInWater() && !player.isClimbing() && BotReach.inReach(player, entity, botSettings.getMeleeRange())) {
            BotControl.jump(player);
            ++state.critJumps;
            state.critTiming = BotHuman.timing(botSettings);
            state.descendTicks = 0;
            state.critWindow = 20;
            state.critRest = botSettings.isCritChain() ? 0 : 2;
            return;
        }
        BotCombat.attack(player, entity, botSettings, state);
    }

    private static int clusteredEnemyCount(Player player, Entity entity, BotSettings botSettings) {
        int n = 0;
        double d = botSettings.getMeleeRange() + 1.0;
        BotFaction botFaction = PvpBotPlugin.get().factions();
        for (Entity entity2 : entity.getNearbyEntities(d, d, d)) {
            LivingEntity livingEntity;
            if (entity2.equals((Object)player) || !(entity2 instanceof LivingEntity) || (livingEntity = (LivingEntity)entity2).isDead() || !BotCombat.isValidTarget(player, livingEntity, botSettings)) continue;
            if (livingEntity instanceof Player) {
                Player player2 = (Player)livingEntity;
                if (botFaction.areAllies(player.getName(), player2.getName()) || botFaction.areAtPeace(player.getName(), player2.getName())) {
                    continue;
                }
            } else if (!(livingEntity instanceof Enemy)) continue;
            ++n;
        }
        return n;
    }

    private static boolean isBlocking(Entity entity) {
        Player player;
        return entity instanceof Player && (player = (Player)entity).isBlocking();
    }

    private static boolean noticesShield(State state, BotSettings botSettings) {
        if (state.shieldNoticed == null) {
            state.shieldNoticed = RANDOM.nextInt(100) < botSettings.getShieldDisableChance();
        }
        return state.shieldNoticed;
    }

    private static void equipAxe(Player player) {
        ItemStack itemStack;
        int n;
        PlayerInventory playerInventory = player.getInventory();
        if (playerInventory.getItemInMainHand().getType().name().endsWith("_AXE")) {
            return;
        }
        int n2 = -1;
        int n3 = -1;
        for (n = 0; n < 36; ++n) {
            int n4;
            itemStack = playerInventory.getItem(n);
            if (itemStack == null || !itemStack.getType().name().endsWith("_AXE") || (n4 = BotCombat.axeTier(itemStack.getType().name())) <= n3) continue;
            n3 = n4;
            n2 = n;
        }
        if (n2 < 0) {
            return;
        }
        if (n2 < 9) {
            playerInventory.setHeldItemSlot(n2);
        } else {
            n = playerInventory.getHeldItemSlot();
            itemStack = playerInventory.getItem(n);
            playerInventory.setItem(n, playerInventory.getItem(n2));
            playerInventory.setItem(n2, itemStack);
        }
    }

    private static int axeTier(String string) {
        if (string.startsWith("NETHERITE")) {
            return 6;
        }
        if (string.startsWith("DIAMOND")) {
            return 5;
        }
        if (string.startsWith("IRON")) {
            return 4;
        }
        if (string.startsWith("STONE")) {
            return 3;
        }
        if (string.startsWith("GOLDEN")) {
            return 2;
        }
        return 1;
    }

    private static void attack(Player player, Entity entity, BotSettings botSettings, State state) {
        state.attackCooldown = botSettings.getAttackCooldown();
        state.swingTiming = Integer.MIN_VALUE;
        state.readyTicks = 0;
        int n = botSettings.getMissChance() + (int)Math.round(state.evasion * 40.0);
        if (n > 0 && RANDOM.nextInt(100) < n) {
            player.swingMainHand();
            return;
        }
        BotPing.delayed(player, () -> {
            LivingEntity livingEntity;
            if (!entity.isValid() || entity instanceof LivingEntity && (livingEntity = (LivingEntity)entity).isDead()) {
                return;
            }
            if (!BotReach.hasClearStrike(player, entity)) {
                player.swingMainHand();
                return;
            }
            player.attack(entity);
            player.swingMainHand();
            BotNavigation.startWtap(player.getName());
            if (botSettings.isDTap()) {
                BotNavigation.startDtap(player.getName());
            }
        });
    }

    private static Entity findTarget(Player player, State state, BotSettings botSettings) {
        boolean bl;
        Entity livingEntity;
        boolean bl2;
        Player player2;
        state.autoPicked = false;
        if (state.forcedTargetName != null && (player2 = player.getServer().getPlayerExact(state.forcedTargetName)) != null && player2.isValid() && !player2.isDead() && BotCombat.isValidTarget(player, (LivingEntity)player2, botSettings) && BotCombat.chaseAcrossWorlds(player, (Entity)player2, botSettings)) {
            BotAwareness.reveal(player.getName(), (Entity)player2, botSettings);
            return player2;
        }
        boolean bl3 = bl2 = PvpBotPlugin.get().ownership().ownerOf(player.getName()) != null;
        if ((botSettings.isRevenge() || bl2) && state.lastAttacker != null) {
            boolean bl4;
            livingEntity = state.lastAttacker;
            bl = System.currentTimeMillis() - state.lastAttackTime >= 30000L;
            boolean bl5 = bl4 = !botSettings.isFriendlyFire() && PvpBotPlugin.get().factions().areAllies(player.getName(), livingEntity.getName());
            if (bl4) {
                state.lastAttacker = null;
            } else if (livingEntity.isValid() && !livingEntity.isDead() && !bl && BotCombat.isValidTarget(player, (LivingEntity)livingEntity, botSettings) && BotCombat.chaseAcrossWorlds(player, (Entity)livingEntity, botSettings)) {
                BotAwareness.reveal(player.getName(), (Entity)livingEntity, botSettings);
                return livingEntity;
            }
            state.lastAttacker = null;
        }
        if (state.scanCooldown > 0) {
            --state.scanCooldown;
            livingEntity = state.scannedTarget;
            if (livingEntity != null && livingEntity.isValid() && !livingEntity.isDead() && livingEntity.getWorld().equals((Object)player.getWorld()) && player.getLocation().distance(livingEntity.getLocation()) <= botSettings.getMaxTargetDistance() && BotAwareness.knows(player, (Entity)livingEntity, botSettings)) {
                state.autoPicked = state.scannedAuto;
                return livingEntity;
            }
            if (livingEntity == null) {
                return null;
            }
        }
        state.scanCooldown = botSettings.getTargetScanInterval();
        livingEntity = BotCombat.findGrudgeTarget(player, botSettings);
        if (livingEntity == null) {
            livingEntity = BotCombat.findFactionEnemy(player, botSettings);
        }
        bl = false;
        if (livingEntity == null && botSettings.isAutoTarget()) {
            livingEntity = BotCombat.findNearestEnemy(player, botSettings);
            bl = livingEntity != null;
        }
        state.scannedTarget = livingEntity;
        state.scannedAuto = bl;
        state.autoPicked = bl;
        return livingEntity;
    }

    private static boolean holdsFire(State state, BotSettings botSettings, Entity entity) {
        Player player;
        block9: {
            block8: {
                if (!(entity instanceof Player)) break block8;
                player = (Player)entity;
                if (state.autoPicked) break block9;
            }
            state.sizingUp = null;
            return false;
        }
        if (state.age < botSettings.getSpawnGrace() * 20) {
            state.sizingUp = player;
            state.sizingUpLeft = 0;
            return true;
        }
        if (state.target != null) {
            state.sizingUp = null;
            return false;
        }
        int n = botSettings.getReactionTicks();
        if (n <= 0) {
            state.sizingUp = null;
            return false;
        }
        if (!player.equals((Object)state.sizingUp)) {
            state.sizingUp = player;
            state.sizingUpLeft = n + RANDOM.nextInt(n / 2 + 1);
        }
        if (state.sizingUpLeft > 0) {
            --state.sizingUpLeft;
            return true;
        }
        state.sizingUp = null;
        return false;
    }

    private static Entity findGrudgeTarget(Player player, BotSettings botSettings) {
        BotFaction botFaction = PvpBotPlugin.get().factions();
        if (botFaction.factionOf(player.getName()) == null) {
            return null;
        }
        double d = botSettings.getMaxTargetDistance();
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (Entity entity : player.getNearbyEntities(d, d, d)) {
            LivingEntity livingEntity;
            if (entity.equals((Object)player) || !(entity instanceof LivingEntity) || (livingEntity = (LivingEntity)entity).isDead() || !BotCombat.isValidTarget(player, livingEntity, botSettings) || !botFaction.hasGrudge(player.getName(), (Entity)livingEntity)) continue;
            arrayList.add((Entity)livingEntity);
        }
        return BotCombat.nearestDetected(player, arrayList, botSettings);
    }

    private static Entity findFactionEnemy(Player player, BotSettings botSettings) {
        BotFaction botFaction = PvpBotPlugin.get().factions();
        if (botFaction.factionOf(player.getName()) == null) {
            return null;
        }
        double d = botSettings.getMaxTargetDistance();
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (Entity entity : player.getNearbyEntities(d, d, d)) {
            Player player2;
            if (!(entity instanceof Player) || (player2 = (Player)entity).equals((Object)player) || player2.isDead() || !BotCombat.isValidTarget(player, (LivingEntity)player2, botSettings) || !botFaction.areEnemies(player.getName(), player2.getName())) continue;
            arrayList.add((Entity)player2);
        }
        return BotCombat.nearestDetected(player, arrayList, botSettings);
    }

    private static Entity findNearestEnemy(Player player, BotSettings botSettings) {
        double d = botSettings.getMaxTargetDistance();
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (Entity entity : player.getNearbyEntities(d, d, d)) {
            Player player2;
            LivingEntity livingEntity;
            if (entity.equals((Object)player) || !(entity instanceof LivingEntity) || (livingEntity = (LivingEntity)entity).isDead() || !BotCombat.isValidTarget(player, livingEntity, botSettings) || (!(livingEntity instanceof Player) ? !(livingEntity instanceof Mob) || !(livingEntity instanceof Enemy) && !botSettings.isTargetPassiveMobs() : !botSettings.isTargetPlayers())) continue;
            if (livingEntity instanceof Player) {
                player2 = (Player)livingEntity;
                if (PvpBotPlugin.get().factions().areAtPeace(player.getName(), player2.getName())) continue;
            }
            if (livingEntity instanceof Player) {
                player2 = (Player)livingEntity;
                if (!botSettings.isFriendlyFire() && (PvpBotPlugin.get().factions().areAllies(player.getName(), player2.getName()) || !botSettings.isBotVsBot() && BotManager.isBot(player2.getName()) && BotManager.isBot(player.getName()) && PvpBotPlugin.get().factions().factionOf(player.getName()) == null)) continue;
            }
            arrayList.add((Entity)livingEntity);
        }
        return BotCombat.nearestDetected(player, arrayList, botSettings);
    }

    private static Entity nearestDetected(Player player, List<Entity> list, BotSettings botSettings) {
        if (list.isEmpty()) {
            return null;
        }
        Location location = player.getLocation();
        Comparator<Entity> comparator = botSettings.isPriorityHpTarget() ? Comparator.comparingDouble(entity -> location.distance(entity.getLocation()) * 2.0 + BotCombat.healthOf(entity)) : Comparator.comparingDouble(entity -> location.distanceSquared(entity.getLocation()));
        list.sort(comparator);
        for (Entity entity2 : list) {
            if (!BotAwareness.knows(player, entity2, botSettings)) continue;
            return entity2;
        }
        return null;
    }

    private static double healthOf(Entity entity) {
        double d;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            d = livingEntity.getHealth();
        } else {
            d = 20.0;
        }
        return d;
    }

    private static boolean isValidTarget(Player player, LivingEntity livingEntity, BotSettings botSettings) {
        if (livingEntity instanceof Player) {
            Player player2 = (Player)livingEntity;
            if (!botSettings.isAttackInvincible() && (player2.getGameMode() == GameMode.SPECTATOR || player2.getGameMode() == GameMode.CREATIVE || player2.isInvulnerable())) {
                return false;
            }
            if (PvpBotPlugin.get().ownership().isOwnedBy(player.getName(), player2.getUniqueId())) {
                return false;
            }
        }
        return true;
    }

    private static boolean sameWorld(Player player, Entity entity) {
        return player.getWorld().equals((Object)entity.getWorld());
    }

    private static boolean chaseAcrossWorlds(Player player, Entity entity, BotSettings botSettings) {
        if (!BotCombat.sameWorld(player, entity)) {
            player.teleport(entity.getLocation());
            return true;
        }
        return player.getLocation().distance(entity.getLocation()) <= botSettings.getMaxTargetDistance();
    }

    private static void wander(Player player, BotSettings botSettings) {
        if (!botSettings.isWander()) {
            BotNavigation.stop(player);
            return;
        }
        BotNavigation.State state = BotNavigation.getState(player);
        if (state.wanderCooldown > 0) {
            --state.wanderCooldown;
        }
        Location location = player.getLocation();
        if (state.wanderTarget == null || Math.hypot(location.getX() - state.wanderTarget.getX(), location.getZ() - state.wanderTarget.getZ()) < 2.0 || state.wanderCooldown <= 0) {
            double d = Math.random() * Math.PI * 2.0;
            double d2 = 6.0 + Math.random() * 10.0;
            state.wanderTarget = location.clone().add(Math.cos(d) * d2, 0.0, Math.sin(d) * d2);
            state.wanderCooldown = 100;
        }
        BotNavigation.walkTo(player, state.wanderTarget, botSettings.getSpeed() * 0.5);
    }

    public static final class State {
        public int attackCooldown;
        public String forcedTargetName;
        public LivingEntity lastAttacker;
        public long lastAttackTime;
        public Entity target;
        public int critWindow;
        public int critRest;
        public double critPeakY;
        public int critJumps;
        public int critHits;
        public double lastFallen;
        public boolean lastGrounded;
        public double lastVertVel;
        public int scanCooldown;
        public Entity scannedTarget;
        public Location lastTargetPos;
        public Vector lastTargetDir;
        public double evasion;
        public boolean axeHold;
        public Boolean shieldNoticed;
        public int ticksInRange;
        public int age;
        public boolean autoPicked;
        public boolean scannedAuto;
        public Player sizingUp;
        public int sizingUpLeft;
        public int swingTiming = Integer.MIN_VALUE;
        public int readyTicks;
        public int critTiming;
        public int descendTicks;
        public int targetBlockTicks;
        public int shieldReaction;
        public int axeSwingIn = -1;
    }
}

