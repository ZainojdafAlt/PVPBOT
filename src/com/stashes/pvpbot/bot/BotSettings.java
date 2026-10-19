/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package com.stashes.pvpbot.bot;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;

public final class BotSettings {
    private int checkInterval = 20;
    private int targetScanInterval = 10;
    private boolean botsRelog = true;
    private boolean forceVulnerable = true;
    private boolean authAutologin = true;
    private boolean bypassDamageProtection = false;
    private boolean autoTarget = true;
    private boolean targetPlayers = true;
    private boolean targetPassiveMobs = false;
    private boolean revenge = true;
    private boolean friendlyFire = false;
    private boolean botVsBot = false;
    private boolean attackInvincible = false;
    private double maxTargetDistance = 32.0;
    private double meleeRange = 3.0;
    private double meleeStandoff = 2.2;
    private int attackCooldown = 12;
    private int pingMillis = 50;
    private int missChance = 10;
    private int mistakeChance = 12;
    private int leaderCallSeconds = 30;
    private int reactionTicks = 30;
    private int spawnGrace = 5;
    private int skill = 50;
    private double turnSpeed = 30.0;
    private int specialWeaponMeleeChance = 35;
    private int shieldDisableChance = 70;
    private double maceRangeBonus = 0.0;
    private boolean macePearlLaunch = false;
    private boolean attributeSwap = false;
    private boolean breachSwap = true;
    private boolean maceStun = true;
    private boolean spearLunge = true;
    private boolean elytraMace = false;
    private int elytraMaceHeight = 20;
    private boolean cartPvp = false;
    private double cartMaxSelfDamage = 10.0;
    private int cartCooldown = 80;
    private boolean crystalPvp = true;
    private double crystalMaxSelfDamage = 8.0;
    private boolean critChain = true;
    private boolean cooldownTrick = true;
    private boolean jumpReset = false;
    private boolean mirrorPunish = true;
    private boolean hitSelect = true;
    private boolean dTap = true;
    private boolean hitWebCombo = true;
    private boolean sweepHit = true;
    private boolean priorityHpTarget = true;
    private boolean shieldReadWindup = true;
    private int swapCooldown = 30;
    private int maceDefenceShield = 55;
    private int maceDefenceFlee = 20;
    private int maceTotemChance = 35;
    private int totemDelayTicks = 10;
    private boolean stasis = false;
    private boolean bhop = true;
    private double speed = 1.0;
    private boolean wander = true;
    private double followDistance = 3.5;
    private boolean autoEquip = true;
    private boolean autoRepair = true;
    private boolean autoRepairBottles = true;
    private boolean autoTotem = true;
    private double totemHealth = 8.0;
    private boolean retreat = true;
    private double retreatHealth = 6.0;
    private boolean autoFood = true;
    private int foodThreshold = 16;
    private boolean autoShield = true;
    private double shieldHealth = 8.0;
    private boolean autoPotion = true;
    private boolean buffPotions = true;
    private boolean pearls = true;
    private double pearlEscapeHealth = 5.0;
    private double pearlChaseDistance = 14.0;
    private int pearlCooldown = 120;
    private double potionHealth = 10.0;
    private boolean cobwebs = false;
    private double cobwebRange = 3.5;
    private int cobwebCooldown = 160;
    private int cobwebGlobalCooldown = 60;
    private boolean drain = true;
    private int drainDelay = 20;
    private int webEscapeDelay = 15;
    private int webEscapeFumbleChance = 35;
    private boolean waterClutch = true;
    private double waterClutchMinFall = 5.0;
    private double waterClutchWindow = 5.0;
    private boolean waterClutchPickup = true;
    private boolean mace = true;
    private boolean orbital = true;
    private int orbitalCooldown = 200;
    private boolean crits = true;
    private boolean shieldDisable = true;
    private boolean breakStashes = true;
    private boolean ranged = true;
    private double rangedMinRange = 6.0;
    private double rangedOptimalRange = 12.0;
    private int bowMinDrawTicks = 18;
    private boolean arrowPrediction = true;
    private int rangedShotGap = 25;
    private int rangedVolleySize = 3;
    private int rangedRestTicks = 80;
    private boolean buildingEnabled = true;
    private boolean buildingBridge = true;
    private boolean buildingPillar = true;
    private boolean buildingClearSoft = true;
    private int buildingCooldown = 6;
    private int buildingBudget = 64;
    private int buildingMaxPillar = 6;
    private Set<String> buildingExtraBreakable = Set.of();
    private boolean detectionEnabled = true;
    private double detectionTouchRadius = 3.0;
    private int detectionMemoryTicks = 100;
    private double detectionInvisible = 0.15;
    private double detectionSneaking = 0.5;
    private double detectionArmoured = 1.0;
    private double detectionUnarmoured = 0.7;

    private BotSettings() {
    }

    public static BotSettings fromConfig(ConfigurationSection configurationSection) {
        BotSettings botSettings = new BotSettings();
        botSettings.apply(configurationSection);
        return botSettings;
    }

    public BotSettings withOverrides(ConfigurationSection configurationSection) {
        BotSettings botSettings = this.copy();
        if (configurationSection != null) {
            botSettings.apply(configurationSection);
        }
        return botSettings;
    }

    private BotSettings copy() {
        BotSettings botSettings = new BotSettings();
        botSettings.checkInterval = this.checkInterval;
        botSettings.targetScanInterval = this.targetScanInterval;
        botSettings.botsRelog = this.botsRelog;
        botSettings.forceVulnerable = this.forceVulnerable;
        botSettings.authAutologin = this.authAutologin;
        botSettings.bypassDamageProtection = this.bypassDamageProtection;
        botSettings.autoTarget = this.autoTarget;
        botSettings.targetPlayers = this.targetPlayers;
        botSettings.targetPassiveMobs = this.targetPassiveMobs;
        botSettings.revenge = this.revenge;
        botSettings.friendlyFire = this.friendlyFire;
        botSettings.botVsBot = this.botVsBot;
        botSettings.attackInvincible = this.attackInvincible;
        botSettings.maxTargetDistance = this.maxTargetDistance;
        botSettings.meleeRange = this.meleeRange;
        botSettings.meleeStandoff = this.meleeStandoff;
        botSettings.attackCooldown = this.attackCooldown;
        botSettings.pingMillis = this.pingMillis;
        botSettings.missChance = this.missChance;
        botSettings.mistakeChance = this.mistakeChance;
        botSettings.leaderCallSeconds = this.leaderCallSeconds;
        botSettings.reactionTicks = this.reactionTicks;
        botSettings.spawnGrace = this.spawnGrace;
        botSettings.skill = this.skill;
        botSettings.turnSpeed = this.turnSpeed;
        botSettings.specialWeaponMeleeChance = this.specialWeaponMeleeChance;
        botSettings.shieldDisableChance = this.shieldDisableChance;
        botSettings.maceRangeBonus = this.maceRangeBonus;
        botSettings.macePearlLaunch = this.macePearlLaunch;
        botSettings.attributeSwap = this.attributeSwap;
        botSettings.breachSwap = this.breachSwap;
        botSettings.maceStun = this.maceStun;
        botSettings.spearLunge = this.spearLunge;
        botSettings.elytraMace = this.elytraMace;
        botSettings.elytraMaceHeight = this.elytraMaceHeight;
        botSettings.cartPvp = this.cartPvp;
        botSettings.cartMaxSelfDamage = this.cartMaxSelfDamage;
        botSettings.cartCooldown = this.cartCooldown;
        botSettings.crystalPvp = this.crystalPvp;
        botSettings.crystalMaxSelfDamage = this.crystalMaxSelfDamage;
        botSettings.critChain = this.critChain;
        botSettings.cooldownTrick = this.cooldownTrick;
        botSettings.jumpReset = this.jumpReset;
        botSettings.mirrorPunish = this.mirrorPunish;
        botSettings.hitSelect = this.hitSelect;
        botSettings.dTap = this.dTap;
        botSettings.hitWebCombo = this.hitWebCombo;
        botSettings.sweepHit = this.sweepHit;
        botSettings.priorityHpTarget = this.priorityHpTarget;
        botSettings.shieldReadWindup = this.shieldReadWindup;
        botSettings.swapCooldown = this.swapCooldown;
        botSettings.maceDefenceShield = this.maceDefenceShield;
        botSettings.maceDefenceFlee = this.maceDefenceFlee;
        botSettings.maceTotemChance = this.maceTotemChance;
        botSettings.totemDelayTicks = this.totemDelayTicks;
        botSettings.stasis = this.stasis;
        botSettings.bhop = this.bhop;
        botSettings.speed = this.speed;
        botSettings.wander = this.wander;
        botSettings.followDistance = this.followDistance;
        botSettings.autoEquip = this.autoEquip;
        botSettings.autoRepair = this.autoRepair;
        botSettings.autoRepairBottles = this.autoRepairBottles;
        botSettings.autoTotem = this.autoTotem;
        botSettings.totemHealth = this.totemHealth;
        botSettings.retreat = this.retreat;
        botSettings.retreatHealth = this.retreatHealth;
        botSettings.autoFood = this.autoFood;
        botSettings.foodThreshold = this.foodThreshold;
        botSettings.autoShield = this.autoShield;
        botSettings.shieldHealth = this.shieldHealth;
        botSettings.autoPotion = this.autoPotion;
        botSettings.buffPotions = this.buffPotions;
        botSettings.pearls = this.pearls;
        botSettings.pearlEscapeHealth = this.pearlEscapeHealth;
        botSettings.pearlChaseDistance = this.pearlChaseDistance;
        botSettings.pearlCooldown = this.pearlCooldown;
        botSettings.potionHealth = this.potionHealth;
        botSettings.cobwebs = this.cobwebs;
        botSettings.cobwebRange = this.cobwebRange;
        botSettings.cobwebCooldown = this.cobwebCooldown;
        botSettings.cobwebGlobalCooldown = this.cobwebGlobalCooldown;
        botSettings.drain = this.drain;
        botSettings.drainDelay = this.drainDelay;
        botSettings.webEscapeDelay = this.webEscapeDelay;
        botSettings.webEscapeFumbleChance = this.webEscapeFumbleChance;
        botSettings.waterClutch = this.waterClutch;
        botSettings.waterClutchMinFall = this.waterClutchMinFall;
        botSettings.waterClutchWindow = this.waterClutchWindow;
        botSettings.waterClutchPickup = this.waterClutchPickup;
        botSettings.mace = this.mace;
        botSettings.orbital = this.orbital;
        botSettings.orbitalCooldown = this.orbitalCooldown;
        botSettings.crits = this.crits;
        botSettings.shieldDisable = this.shieldDisable;
        botSettings.breakStashes = this.breakStashes;
        botSettings.ranged = this.ranged;
        botSettings.rangedMinRange = this.rangedMinRange;
        botSettings.rangedOptimalRange = this.rangedOptimalRange;
        botSettings.bowMinDrawTicks = this.bowMinDrawTicks;
        botSettings.arrowPrediction = this.arrowPrediction;
        botSettings.rangedShotGap = this.rangedShotGap;
        botSettings.rangedVolleySize = this.rangedVolleySize;
        botSettings.rangedRestTicks = this.rangedRestTicks;
        botSettings.buildingEnabled = this.buildingEnabled;
        botSettings.buildingBridge = this.buildingBridge;
        botSettings.buildingPillar = this.buildingPillar;
        botSettings.buildingClearSoft = this.buildingClearSoft;
        botSettings.buildingCooldown = this.buildingCooldown;
        botSettings.buildingBudget = this.buildingBudget;
        botSettings.buildingMaxPillar = this.buildingMaxPillar;
        botSettings.buildingExtraBreakable = this.buildingExtraBreakable;
        botSettings.detectionEnabled = this.detectionEnabled;
        botSettings.detectionTouchRadius = this.detectionTouchRadius;
        botSettings.detectionMemoryTicks = this.detectionMemoryTicks;
        botSettings.detectionInvisible = this.detectionInvisible;
        botSettings.detectionSneaking = this.detectionSneaking;
        botSettings.detectionArmoured = this.detectionArmoured;
        botSettings.detectionUnarmoured = this.detectionUnarmoured;
        return botSettings;
    }

    private void apply(ConfigurationSection configurationSection) {
        this.checkInterval = Math.max(1, configurationSection.getInt("general.check-interval", configurationSection.getInt("check-interval", this.checkInterval)));
        this.targetScanInterval = configurationSection.getInt("general.target-scan-interval", configurationSection.getInt("target-scan-interval", this.targetScanInterval));
        this.botsRelog = configurationSection.getBoolean("general.bots-relog", configurationSection.getBoolean("bots-relog", this.botsRelog));
        this.forceVulnerable = configurationSection.getBoolean("general.force-vulnerable", configurationSection.getBoolean("force-vulnerable", this.forceVulnerable));
        this.authAutologin = configurationSection.getBoolean("general.auth-autologin", configurationSection.getBoolean("auth-autologin", this.authAutologin));
        this.bypassDamageProtection = configurationSection.getBoolean("general.bypass-damage-protection", configurationSection.getBoolean("bypass-damage-protection", this.bypassDamageProtection));
        this.autoTarget = configurationSection.getBoolean("combat.auto-target", configurationSection.getBoolean("auto-target", this.autoTarget));
        this.targetPlayers = configurationSection.getBoolean("combat.target-players", configurationSection.getBoolean("target-players", this.targetPlayers));
        this.targetPassiveMobs = configurationSection.getBoolean("combat.target-passive-mobs", configurationSection.getBoolean("target-passive-mobs", this.targetPassiveMobs));
        this.revenge = configurationSection.getBoolean("combat.revenge", configurationSection.getBoolean("revenge", this.revenge));
        this.friendlyFire = configurationSection.getBoolean("combat.friendly-fire", configurationSection.getBoolean("friendly-fire", this.friendlyFire));
        this.botVsBot = configurationSection.getBoolean("combat.bot-vs-bot", configurationSection.getBoolean("bot-vs-bot", this.botVsBot));
        this.attackInvincible = configurationSection.getBoolean("combat.attack-invincible", configurationSection.getBoolean("attack-invincible", this.attackInvincible));
        this.maxTargetDistance = configurationSection.getDouble("combat.max-target-distance", configurationSection.getDouble("max-target-distance", this.maxTargetDistance));
        this.meleeRange = configurationSection.getDouble("combat.melee-range", configurationSection.getDouble("melee-range", this.meleeRange));
        this.meleeStandoff = configurationSection.getDouble("combat.melee-standoff", configurationSection.getDouble("melee-standoff", this.meleeStandoff));
        this.attackCooldown = configurationSection.getInt("combat.attack-cooldown", configurationSection.getInt("attack-cooldown", this.attackCooldown));
        this.pingMillis = configurationSection.getInt("combat.ping-millis", configurationSection.getInt("ping-millis", this.pingMillis));
        this.missChance = configurationSection.getInt("combat.miss-chance", configurationSection.getInt("miss-chance", this.missChance));
        this.mistakeChance = configurationSection.getInt("combat.mistake-chance", configurationSection.getInt("mistake-chance", this.mistakeChance));
        this.leaderCallSeconds = configurationSection.getInt("combat.leader-call-seconds", configurationSection.getInt("leader-call-seconds", this.leaderCallSeconds));
        this.reactionTicks = configurationSection.getInt("combat.reaction-ticks", configurationSection.getInt("reaction-ticks", this.reactionTicks));
        this.spawnGrace = configurationSection.getInt("combat.spawn-grace", configurationSection.getInt("spawn-grace", this.spawnGrace));
        this.skill = configurationSection.getInt("combat.skill", configurationSection.getInt("skill", this.skill));
        this.turnSpeed = configurationSection.getDouble("combat.turn-speed", configurationSection.getDouble("turn-speed", this.turnSpeed));
        this.specialWeaponMeleeChance = configurationSection.getInt("combat.special-weapon-melee-chance", configurationSection.getInt("special-weapon-melee-chance", this.specialWeaponMeleeChance));
        this.shieldDisableChance = configurationSection.getInt("combat.shield-disable-chance", configurationSection.getInt("shield-disable-chance", this.shieldDisableChance));
        this.maceRangeBonus = configurationSection.getDouble("combat.mace-range-bonus", configurationSection.getDouble("mace-range-bonus", this.maceRangeBonus));
        this.macePearlLaunch = configurationSection.getBoolean("combat.mace-pearl-launch", configurationSection.getBoolean("mace-pearl-launch", this.macePearlLaunch));
        this.attributeSwap = configurationSection.getBoolean("combat.attribute-swap", configurationSection.getBoolean("attribute-swap", this.attributeSwap));
        this.breachSwap = configurationSection.getBoolean("combat.breach-swap", configurationSection.getBoolean("breach-swap", this.breachSwap));
        this.maceStun = configurationSection.getBoolean("combat.mace-stun", configurationSection.getBoolean("mace-stun", this.maceStun));
        this.spearLunge = configurationSection.getBoolean("combat.spear-lunge", configurationSection.getBoolean("spear-lunge", this.spearLunge));
        this.elytraMace = configurationSection.getBoolean("combat.elytra-mace", configurationSection.getBoolean("elytra-mace", this.elytraMace));
        this.elytraMaceHeight = configurationSection.getInt("combat.elytra-mace-height", configurationSection.getInt("elytra-mace-height", this.elytraMaceHeight));
        this.cartPvp = configurationSection.getBoolean("combat.cart-pvp", configurationSection.getBoolean("cart-pvp", this.cartPvp));
        this.cartMaxSelfDamage = configurationSection.getDouble("combat.cart-max-self-damage", configurationSection.getDouble("cart-max-self-damage", this.cartMaxSelfDamage));
        this.cartCooldown = configurationSection.getInt("combat.cart-cooldown", configurationSection.getInt("cart-cooldown", this.cartCooldown));
        this.crystalPvp = configurationSection.getBoolean("combat.crystal-pvp", configurationSection.getBoolean("crystal-pvp", this.crystalPvp));
        this.crystalMaxSelfDamage = configurationSection.getDouble("combat.crystal-max-self-damage", configurationSection.getDouble("crystal-max-self-damage", this.crystalMaxSelfDamage));
        this.critChain = configurationSection.getBoolean("combat.crit-chain", configurationSection.getBoolean("crit-chain", this.critChain));
        this.cooldownTrick = configurationSection.getBoolean("combat.cooldown-trick", configurationSection.getBoolean("cooldown-trick", this.cooldownTrick));
        this.jumpReset = configurationSection.getBoolean("combat.jump-reset", configurationSection.getBoolean("jump-reset", this.jumpReset));
        this.mirrorPunish = configurationSection.getBoolean("combat.mirror-punish", configurationSection.getBoolean("mirror-punish", this.mirrorPunish));
        this.hitSelect = configurationSection.getBoolean("combat.hit-select", configurationSection.getBoolean("hit-select", this.hitSelect));
        this.dTap = configurationSection.getBoolean("combat.d-tap", configurationSection.getBoolean("d-tap", this.dTap));
        this.hitWebCombo = configurationSection.getBoolean("combat.hit-web-combo", configurationSection.getBoolean("hit-web-combo", this.hitWebCombo));
        this.sweepHit = configurationSection.getBoolean("combat.sweep-hit", configurationSection.getBoolean("sweep-hit", this.sweepHit));
        this.priorityHpTarget = configurationSection.getBoolean("combat.priority-hp-target", configurationSection.getBoolean("priority-hp-target", this.priorityHpTarget));
        this.shieldReadWindup = configurationSection.getBoolean("combat.shield-read-windup", configurationSection.getBoolean("shield-read-windup", this.shieldReadWindup));
        this.swapCooldown = configurationSection.getInt("combat.swap-cooldown", configurationSection.getInt("swap-cooldown", this.swapCooldown));
        this.maceDefenceShield = configurationSection.getInt("combat.mace-defence-shield", configurationSection.getInt("mace-defence-shield", this.maceDefenceShield));
        this.maceDefenceFlee = configurationSection.getInt("combat.mace-defence-flee", configurationSection.getInt("mace-defence-flee", this.maceDefenceFlee));
        this.maceTotemChance = configurationSection.getInt("combat.mace-totem-chance", configurationSection.getInt("mace-totem-chance", this.maceTotemChance));
        this.totemDelayTicks = configurationSection.getInt("utility.totem-delay-ticks", configurationSection.getInt("totem-delay-ticks", this.totemDelayTicks));
        this.stasis = configurationSection.getBoolean("combat.stasis", configurationSection.getBoolean("stasis", this.stasis));
        this.bhop = configurationSection.getBoolean("movement.bhop", configurationSection.getBoolean("bhop", this.bhop));
        this.speed = configurationSection.getDouble("movement.speed", configurationSection.getDouble("speed", this.speed));
        this.wander = configurationSection.getBoolean("movement.wander", configurationSection.getBoolean("wander", this.wander));
        this.followDistance = configurationSection.getDouble("movement.follow-distance", configurationSection.getDouble("follow-distance", this.followDistance));
        this.autoEquip = configurationSection.getBoolean("utility.auto-equip", configurationSection.getBoolean("auto-equip", this.autoEquip));
        this.autoRepair = configurationSection.getBoolean("utility.auto-repair", configurationSection.getBoolean("auto-repair", this.autoRepair));
        this.autoRepairBottles = configurationSection.getBoolean("utility.auto-repair-bottles", configurationSection.getBoolean("auto-repair-bottles", this.autoRepairBottles));
        this.autoTotem = configurationSection.getBoolean("utility.auto-totem", configurationSection.getBoolean("auto-totem", this.autoTotem));
        this.totemHealth = configurationSection.getDouble("utility.totem-health", configurationSection.getDouble("totem-health", this.totemHealth));
        this.retreat = configurationSection.getBoolean("utility.retreat", configurationSection.getBoolean("retreat", this.retreat));
        this.retreatHealth = configurationSection.getDouble("utility.retreat-health", configurationSection.getDouble("retreat-health", this.retreatHealth));
        this.autoFood = configurationSection.getBoolean("utility.auto-food", configurationSection.getBoolean("auto-food", this.autoFood));
        this.foodThreshold = configurationSection.getInt("utility.food-threshold", configurationSection.getInt("food-threshold", this.foodThreshold));
        this.autoShield = configurationSection.getBoolean("utility.auto-shield", configurationSection.getBoolean("auto-shield", this.autoShield));
        this.shieldHealth = configurationSection.getDouble("utility.shield-health", configurationSection.getDouble("shield-health", this.shieldHealth));
        this.autoPotion = configurationSection.getBoolean("utility.auto-potion", configurationSection.getBoolean("auto-potion", this.autoPotion));
        this.buffPotions = configurationSection.getBoolean("utility.buff-potions", configurationSection.getBoolean("buff-potions", this.buffPotions));
        this.pearls = configurationSection.getBoolean("utility.pearls", configurationSection.getBoolean("pearls", this.pearls));
        this.pearlEscapeHealth = configurationSection.getDouble("utility.pearl-escape-health", configurationSection.getDouble("pearl-escape-health", this.pearlEscapeHealth));
        this.pearlChaseDistance = configurationSection.getDouble("utility.pearl-chase-distance", configurationSection.getDouble("pearl-chase-distance", this.pearlChaseDistance));
        this.pearlCooldown = configurationSection.getInt("utility.pearl-cooldown", configurationSection.getInt("pearl-cooldown", this.pearlCooldown));
        this.potionHealth = configurationSection.getDouble("utility.potion-health", configurationSection.getDouble("potion-health", this.potionHealth));
        this.cobwebs = configurationSection.getBoolean("utility.cobwebs", configurationSection.getBoolean("cobwebs", this.cobwebs));
        this.cobwebRange = configurationSection.getDouble("utility.cobweb-range", configurationSection.getDouble("cobweb-range", this.cobwebRange));
        this.cobwebCooldown = configurationSection.getInt("utility.cobweb-cooldown", configurationSection.getInt("cobweb-cooldown", this.cobwebCooldown));
        this.cobwebGlobalCooldown = configurationSection.getInt("utility.cobweb-global-cooldown", configurationSection.getInt("cobweb-global-cooldown", this.cobwebGlobalCooldown));
        this.drain = configurationSection.getBoolean("utility.drain", configurationSection.getBoolean("drain", this.drain));
        this.drainDelay = configurationSection.getInt("utility.drain-delay", configurationSection.getInt("drain-delay", this.drainDelay));
        this.webEscapeDelay = configurationSection.getInt("utility.web-escape-delay", configurationSection.getInt("web-escape-delay", this.webEscapeDelay));
        this.webEscapeFumbleChance = configurationSection.getInt("utility.web-escape-fumble-chance", configurationSection.getInt("web-escape-fumble-chance", this.webEscapeFumbleChance));
        this.waterClutch = configurationSection.getBoolean("utility.water-clutch", configurationSection.getBoolean("water-clutch", this.waterClutch));
        this.waterClutchMinFall = configurationSection.getDouble("utility.water-clutch-min-fall", configurationSection.getDouble("water-clutch-min-fall", this.waterClutchMinFall));
        this.waterClutchWindow = configurationSection.getDouble("utility.water-clutch-window", configurationSection.getDouble("water-clutch-window", this.waterClutchWindow));
        this.waterClutchPickup = configurationSection.getBoolean("utility.water-clutch-pickup", configurationSection.getBoolean("water-clutch-pickup", this.waterClutchPickup));
        this.mace = configurationSection.getBoolean("combat.mace", configurationSection.getBoolean("mace", this.mace));
        this.orbital = configurationSection.getBoolean("combat.orbital", configurationSection.getBoolean("orbital", this.orbital));
        this.orbitalCooldown = configurationSection.getInt("combat.orbital-cooldown", configurationSection.getInt("orbital-cooldown", this.orbitalCooldown));
        this.crits = configurationSection.getBoolean("combat.crits", configurationSection.getBoolean("crits", this.crits));
        this.shieldDisable = configurationSection.getBoolean("combat.shield-disable", configurationSection.getBoolean("shield-disable", this.shieldDisable));
        this.breakStashes = configurationSection.getBoolean("combat.break-stashes", configurationSection.getBoolean("break-stashes", this.breakStashes));
        this.ranged = configurationSection.getBoolean("ranged.enabled", configurationSection.getBoolean("ranged", this.ranged));
        this.rangedMinRange = configurationSection.getDouble("ranged.min-range", configurationSection.getDouble("ranged-min-range", this.rangedMinRange));
        this.rangedOptimalRange = configurationSection.getDouble("ranged.optimal-range", configurationSection.getDouble("ranged-optimal-range", this.rangedOptimalRange));
        this.bowMinDrawTicks = configurationSection.getInt("ranged.bow-min-draw-ticks", configurationSection.getInt("bow-min-draw-ticks", this.bowMinDrawTicks));
        this.arrowPrediction = configurationSection.getBoolean("ranged.arrow-prediction", configurationSection.getBoolean("arrow-prediction", this.arrowPrediction));
        this.rangedShotGap = configurationSection.getInt("ranged.shot-gap", configurationSection.getInt("ranged-shot-gap", this.rangedShotGap));
        this.rangedVolleySize = configurationSection.getInt("ranged.volley-size", configurationSection.getInt("ranged-volley-size", this.rangedVolleySize));
        this.rangedRestTicks = configurationSection.getInt("ranged.rest-ticks", configurationSection.getInt("ranged-rest-ticks", this.rangedRestTicks));
        this.buildingEnabled = configurationSection.getBoolean("building.enabled", configurationSection.getBoolean("building", this.buildingEnabled));
        this.buildingBridge = configurationSection.getBoolean("building.bridge", configurationSection.getBoolean("building-bridge", this.buildingBridge));
        this.buildingPillar = configurationSection.getBoolean("building.pillar", configurationSection.getBoolean("building-pillar", this.buildingPillar));
        this.buildingClearSoft = configurationSection.getBoolean("building.clear-soft-blocks", configurationSection.getBoolean("building-clear-soft", this.buildingClearSoft));
        this.buildingCooldown = configurationSection.getInt("building.cooldown", configurationSection.getInt("building-cooldown", this.buildingCooldown));
        this.buildingBudget = configurationSection.getInt("building.budget", configurationSection.getInt("building-budget", this.buildingBudget));
        this.buildingMaxPillar = configurationSection.getInt("building.max-pillar", configurationSection.getInt("building-max-pillar", this.buildingMaxPillar));
        if (configurationSection.contains("building.extra-breakable")) {
            this.buildingExtraBreakable = configurationSection.getStringList("building.extra-breakable").stream().map(string -> string.toUpperCase(Locale.ROOT)).collect(Collectors.toUnmodifiableSet());
        }
        this.detectionEnabled = configurationSection.getBoolean("detection.enabled", configurationSection.getBoolean("detection", this.detectionEnabled));
        this.detectionTouchRadius = configurationSection.getDouble("detection.touch-radius", configurationSection.getDouble("detection-touch-radius", this.detectionTouchRadius));
        this.detectionMemoryTicks = configurationSection.getInt("detection.memory-ticks", configurationSection.getInt("detection-memory-ticks", this.detectionMemoryTicks));
        this.detectionInvisible = configurationSection.getDouble("detection.invisible-multiplier", configurationSection.getDouble("detection-invisible", this.detectionInvisible));
        this.detectionSneaking = configurationSection.getDouble("detection.sneaking-multiplier", configurationSection.getDouble("detection-sneaking", this.detectionSneaking));
        this.detectionArmoured = configurationSection.getDouble("detection.armoured-multiplier", configurationSection.getDouble("detection-armoured", this.detectionArmoured));
        this.detectionUnarmoured = configurationSection.getDouble("detection.unarmoured-multiplier", configurationSection.getDouble("detection-unarmoured", this.detectionUnarmoured));
    }

    public int getCheckInterval() {
        return this.checkInterval;
    }

    public int getTargetScanInterval() {
        return this.targetScanInterval;
    }

    public boolean isBotsRelog() {
        return this.botsRelog;
    }

    public boolean isForceVulnerable() {
        return this.forceVulnerable;
    }

    public boolean isAuthAutologin() {
        return this.authAutologin;
    }

    public boolean isBypassDamageProtection() {
        return this.bypassDamageProtection;
    }

    public boolean isAutoTarget() {
        return this.autoTarget;
    }

    public boolean isTargetPlayers() {
        return this.targetPlayers;
    }

    public boolean isTargetPassiveMobs() {
        return this.targetPassiveMobs;
    }

    public boolean isRevenge() {
        return this.revenge;
    }

    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    public boolean isBotVsBot() {
        return this.botVsBot;
    }

    public boolean isAttackInvincible() {
        return this.attackInvincible;
    }

    public double getMaxTargetDistance() {
        return this.maxTargetDistance;
    }

    public double getMeleeRange() {
        return this.meleeRange;
    }

    public double getMeleeStandoff() {
        return this.meleeStandoff;
    }

    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    public int getPingMillis() {
        return this.pingMillis;
    }

    public int getMissChance() {
        return this.missChance;
    }

    public int getMistakeChance() {
        return this.mistakeChance;
    }

    public int getLeaderCallSeconds() {
        return this.leaderCallSeconds;
    }

    public int getReactionTicks() {
        return this.reactionTicks;
    }

    public int getSpawnGrace() {
        return this.spawnGrace;
    }

    public int getSkill() {
        return this.skill;
    }

    public double getTurnSpeed() {
        return this.turnSpeed;
    }

    public int getSpecialWeaponMeleeChance() {
        return this.specialWeaponMeleeChance;
    }

    public int getShieldDisableChance() {
        return this.shieldDisableChance;
    }

    public double getMaceRangeBonus() {
        return this.maceRangeBonus;
    }

    public boolean isMacePearlLaunch() {
        return this.macePearlLaunch;
    }

    public boolean isAttributeSwap() {
        return this.attributeSwap;
    }

    public boolean isBreachSwap() {
        return this.breachSwap;
    }

    public boolean isMaceStun() {
        return this.maceStun;
    }

    public boolean isSpearLunge() {
        return this.spearLunge;
    }

    public boolean isElytraMace() {
        return this.elytraMace;
    }

    public int getElytraMaceHeight() {
        return this.elytraMaceHeight;
    }

    public boolean isCrystalPvp() {
        return this.crystalPvp;
    }

    public double getCrystalMaxSelfDamage() {
        return this.crystalMaxSelfDamage;
    }

    public boolean isCartPvp() {
        return this.cartPvp;
    }

    public double getCartMaxSelfDamage() {
        return this.cartMaxSelfDamage;
    }

    public int getCartCooldown() {
        return this.cartCooldown;
    }

    public boolean isCritChain() {
        return this.critChain;
    }

    public boolean isCooldownTrick() {
        return this.cooldownTrick;
    }

    public boolean isJumpReset() {
        return this.jumpReset;
    }

    public boolean isMirrorPunish() {
        return this.mirrorPunish;
    }

    public boolean isHitSelect() {
        return this.hitSelect;
    }

    public boolean isDTap() {
        return this.dTap;
    }

    public boolean isHitWebCombo() {
        return this.hitWebCombo;
    }

    public boolean isSweepHit() {
        return this.sweepHit;
    }

    public boolean isPriorityHpTarget() {
        return this.priorityHpTarget;
    }

    public boolean isShieldReadWindup() {
        return this.shieldReadWindup;
    }

    public int getSwapCooldown() {
        return this.swapCooldown;
    }

    public int getMaceDefenceShield() {
        return this.maceDefenceShield;
    }

    public int getMaceDefenceFlee() {
        return this.maceDefenceFlee;
    }

    public int getMaceTotemChance() {
        return this.maceTotemChance;
    }

    public int getTotemDelayTicks() {
        return this.totemDelayTicks;
    }

    public boolean isStasis() {
        return this.stasis;
    }

    public boolean isBhop() {
        return this.bhop;
    }

    public double getSpeed() {
        return this.speed;
    }

    public boolean isWander() {
        return this.wander;
    }

    public double getFollowDistance() {
        return this.followDistance;
    }

    public boolean isAutoEquip() {
        return this.autoEquip;
    }

    public boolean isAutoRepair() {
        return this.autoRepair;
    }

    public boolean isAutoRepairBottles() {
        return this.autoRepairBottles;
    }

    public boolean isAutoTotem() {
        return this.autoTotem;
    }

    public double getTotemHealth() {
        return this.totemHealth;
    }

    public boolean isRetreat() {
        return this.retreat;
    }

    public double getRetreatHealth() {
        return this.retreatHealth;
    }

    public boolean isAutoFood() {
        return this.autoFood;
    }

    public int getFoodThreshold() {
        return this.foodThreshold;
    }

    public boolean isAutoShield() {
        return this.autoShield;
    }

    public double getShieldHealth() {
        return this.shieldHealth;
    }

    public boolean isAutoPotion() {
        return this.autoPotion;
    }

    public boolean isBuffPotions() {
        return this.buffPotions;
    }

    public boolean isPearls() {
        return this.pearls;
    }

    public double getPearlEscapeHealth() {
        return this.pearlEscapeHealth;
    }

    public double getPearlChaseDistance() {
        return this.pearlChaseDistance;
    }

    public int getPearlCooldown() {
        return this.pearlCooldown;
    }

    public double getPotionHealth() {
        return this.potionHealth;
    }

    public boolean isCobwebs() {
        return this.cobwebs;
    }

    public double getCobwebRange() {
        return this.cobwebRange;
    }

    public int getCobwebCooldown() {
        return this.cobwebCooldown;
    }

    public int getCobwebGlobalCooldown() {
        return this.cobwebGlobalCooldown;
    }

    public boolean isDrain() {
        return this.drain;
    }

    public int getDrainDelay() {
        return this.drainDelay;
    }

    public int getWebEscapeDelay() {
        return this.webEscapeDelay;
    }

    public int getWebEscapeFumbleChance() {
        return this.webEscapeFumbleChance;
    }

    public boolean isWaterClutch() {
        return this.waterClutch;
    }

    public double getWaterClutchMinFall() {
        return this.waterClutchMinFall;
    }

    public double getWaterClutchWindow() {
        return this.waterClutchWindow;
    }

    public boolean isWaterClutchPickup() {
        return this.waterClutchPickup;
    }

    public boolean isMace() {
        return this.mace;
    }

    public boolean isOrbital() {
        return this.orbital;
    }

    public int getOrbitalCooldown() {
        return this.orbitalCooldown;
    }

    public boolean isCrits() {
        return this.crits;
    }

    public boolean isShieldDisable() {
        return this.shieldDisable;
    }

    public boolean isBreakStashes() {
        return this.breakStashes;
    }

    public boolean isRanged() {
        return this.ranged;
    }

    public double getRangedMinRange() {
        return this.rangedMinRange;
    }

    public double getRangedOptimalRange() {
        return this.rangedOptimalRange;
    }

    public int getBowMinDrawTicks() {
        return this.bowMinDrawTicks;
    }

    public boolean isArrowPrediction() {
        return this.arrowPrediction;
    }

    public int getRangedShotGap() {
        return this.rangedShotGap;
    }

    public int getRangedVolleySize() {
        return this.rangedVolleySize;
    }

    public int getRangedRestTicks() {
        return this.rangedRestTicks;
    }

    public boolean isBuildingEnabled() {
        return this.buildingEnabled;
    }

    public boolean isBuildingBridge() {
        return this.buildingBridge;
    }

    public boolean isBuildingPillar() {
        return this.buildingPillar;
    }

    public boolean isBuildingClearSoft() {
        return this.buildingClearSoft;
    }

    public int getBuildingCooldown() {
        return this.buildingCooldown;
    }

    public int getBuildingBudget() {
        return this.buildingBudget;
    }

    public int getBuildingMaxPillar() {
        return this.buildingMaxPillar;
    }

    public Set<String> getBuildingExtraBreakable() {
        return this.buildingExtraBreakable;
    }

    public boolean isDetectionEnabled() {
        return this.detectionEnabled;
    }

    public double getDetectionTouchRadius() {
        return this.detectionTouchRadius;
    }

    public int getDetectionMemoryTicks() {
        return this.detectionMemoryTicks;
    }

    public double getDetectionInvisibleMultiplier() {
        return this.detectionInvisible;
    }

    public double getDetectionSneakingMultiplier() {
        return this.detectionSneaking;
    }

    public double getDetectionArmouredMultiplier() {
        return this.detectionArmoured;
    }

    public double getDetectionUnarmouredMultiplier() {
        return this.detectionUnarmoured;
    }

    public Object valueOf(String string) {
        return switch (string) {
            case "check-interval" -> this.checkInterval;
            case "target-scan-interval" -> this.targetScanInterval;
            case "bots-relog" -> Boolean.valueOf(this.botsRelog);
            case "force-vulnerable" -> Boolean.valueOf(this.forceVulnerable);
            case "auth-autologin" -> Boolean.valueOf(this.authAutologin);
            case "bypass-damage-protection" -> Boolean.valueOf(this.bypassDamageProtection);
            case "auto-target" -> Boolean.valueOf(this.autoTarget);
            case "target-players" -> Boolean.valueOf(this.targetPlayers);
            case "target-passive-mobs" -> Boolean.valueOf(this.targetPassiveMobs);
            case "revenge" -> Boolean.valueOf(this.revenge);
            case "friendly-fire" -> Boolean.valueOf(this.friendlyFire);
            case "bot-vs-bot" -> Boolean.valueOf(this.botVsBot);
            case "attack-invincible" -> Boolean.valueOf(this.attackInvincible);
            case "max-target-distance" -> this.maxTargetDistance;
            case "melee-range" -> this.meleeRange;
            case "melee-standoff" -> this.meleeStandoff;
            case "attack-cooldown" -> this.attackCooldown;
            case "ping-millis" -> this.pingMillis;
            case "miss-chance" -> this.missChance;
            case "mistake-chance" -> this.mistakeChance;
            case "leader-call-seconds" -> this.leaderCallSeconds;
            case "reaction-ticks" -> this.reactionTicks;
            case "spawn-grace" -> this.spawnGrace;
            case "skill" -> this.skill;
            case "turn-speed" -> this.turnSpeed;
            case "special-weapon-melee-chance" -> this.specialWeaponMeleeChance;
            case "shield-disable-chance" -> this.shieldDisableChance;
            case "mace-range-bonus" -> this.maceRangeBonus;
            case "mace-pearl-launch" -> Boolean.valueOf(this.macePearlLaunch);
            case "attribute-swap" -> Boolean.valueOf(this.attributeSwap);
            case "breach-swap" -> Boolean.valueOf(this.breachSwap);
            case "mace-stun" -> Boolean.valueOf(this.maceStun);
            case "spear-lunge" -> Boolean.valueOf(this.spearLunge);
            case "elytra-mace" -> Boolean.valueOf(this.elytraMace);
            case "elytra-mace-height" -> this.elytraMaceHeight;
            case "cart-pvp" -> Boolean.valueOf(this.cartPvp);
            case "crystal-pvp" -> Boolean.valueOf(this.crystalPvp);
            case "crystal-max-self-damage" -> this.crystalMaxSelfDamage;
            case "cart-max-self-damage" -> this.cartMaxSelfDamage;
            case "cart-cooldown" -> this.cartCooldown;
            case "crit-chain" -> Boolean.valueOf(this.critChain);
            case "cooldown-trick" -> Boolean.valueOf(this.cooldownTrick);
            case "jump-reset" -> Boolean.valueOf(this.jumpReset);
            case "mirror-punish" -> Boolean.valueOf(this.mirrorPunish);
            case "hit-select" -> Boolean.valueOf(this.hitSelect);
            case "d-tap" -> Boolean.valueOf(this.dTap);
            case "hit-web-combo" -> Boolean.valueOf(this.hitWebCombo);
            case "sweep-hit" -> Boolean.valueOf(this.sweepHit);
            case "priority-hp-target" -> Boolean.valueOf(this.priorityHpTarget);
            case "shield-read-windup" -> Boolean.valueOf(this.shieldReadWindup);
            case "swap-cooldown" -> this.swapCooldown;
            case "mace-defence-shield" -> this.maceDefenceShield;
            case "mace-defence-flee" -> this.maceDefenceFlee;
            case "mace-totem-chance" -> this.maceTotemChance;
            case "totem-delay-ticks" -> this.totemDelayTicks;
            case "stasis" -> Boolean.valueOf(this.stasis);
            case "mace" -> Boolean.valueOf(this.mace);
            case "orbital" -> Boolean.valueOf(this.orbital);
            case "orbital-cooldown" -> this.orbitalCooldown;
            case "crits" -> Boolean.valueOf(this.crits);
            case "shield-disable" -> Boolean.valueOf(this.shieldDisable);
            case "break-stashes" -> Boolean.valueOf(this.breakStashes);
            case "bhop" -> Boolean.valueOf(this.bhop);
            case "speed" -> this.speed;
            case "wander" -> Boolean.valueOf(this.wander);
            case "follow-distance" -> this.followDistance;
            case "auto-equip" -> Boolean.valueOf(this.autoEquip);
            case "auto-repair" -> Boolean.valueOf(this.autoRepair);
            case "auto-repair-bottles" -> Boolean.valueOf(this.autoRepairBottles);
            case "auto-totem" -> Boolean.valueOf(this.autoTotem);
            case "totem-health" -> this.totemHealth;
            case "retreat" -> Boolean.valueOf(this.retreat);
            case "retreat-health" -> this.retreatHealth;
            case "auto-food" -> Boolean.valueOf(this.autoFood);
            case "food-threshold" -> this.foodThreshold;
            case "auto-shield" -> Boolean.valueOf(this.autoShield);
            case "shield-health" -> this.shieldHealth;
            case "auto-potion" -> Boolean.valueOf(this.autoPotion);
            case "potion-health" -> this.potionHealth;
            case "buff-potions" -> Boolean.valueOf(this.buffPotions);
            case "pearls" -> Boolean.valueOf(this.pearls);
            case "pearl-escape-health" -> this.pearlEscapeHealth;
            case "pearl-chase-distance" -> this.pearlChaseDistance;
            case "pearl-cooldown" -> this.pearlCooldown;
            case "cobwebs" -> Boolean.valueOf(this.cobwebs);
            case "cobweb-range" -> this.cobwebRange;
            case "cobweb-cooldown" -> this.cobwebCooldown;
            case "cobweb-global-cooldown" -> this.cobwebGlobalCooldown;
            case "drain" -> Boolean.valueOf(this.drain);
            case "drain-delay" -> this.drainDelay;
            case "web-escape-delay" -> this.webEscapeDelay;
            case "web-escape-fumble-chance" -> this.webEscapeFumbleChance;
            case "water-clutch" -> Boolean.valueOf(this.waterClutch);
            case "water-clutch-min-fall" -> this.waterClutchMinFall;
            case "water-clutch-window" -> this.waterClutchWindow;
            case "water-clutch-pickup" -> Boolean.valueOf(this.waterClutchPickup);
            case "ranged" -> Boolean.valueOf(this.ranged);
            case "ranged-min-range" -> this.rangedMinRange;
            case "ranged-optimal-range" -> this.rangedOptimalRange;
            case "bow-min-draw-ticks" -> this.bowMinDrawTicks;
            case "arrow-prediction" -> Boolean.valueOf(this.arrowPrediction);
            case "ranged-shot-gap" -> this.rangedShotGap;
            case "ranged-volley-size" -> this.rangedVolleySize;
            case "ranged-rest-ticks" -> this.rangedRestTicks;
            case "building" -> Boolean.valueOf(this.buildingEnabled);
            case "building-bridge" -> Boolean.valueOf(this.buildingBridge);
            case "building-pillar" -> Boolean.valueOf(this.buildingPillar);
            case "building-clear-soft" -> Boolean.valueOf(this.buildingClearSoft);
            case "building-cooldown" -> this.buildingCooldown;
            case "building-budget" -> this.buildingBudget;
            case "building-max-pillar" -> this.buildingMaxPillar;
            case "detection" -> Boolean.valueOf(this.detectionEnabled);
            case "detection-touch-radius" -> this.detectionTouchRadius;
            case "detection-memory-ticks" -> this.detectionMemoryTicks;
            case "detection-invisible" -> this.detectionInvisible;
            case "detection-sneaking" -> this.detectionSneaking;
            case "detection-armoured" -> this.detectionArmoured;
            case "detection-unarmoured" -> this.detectionUnarmoured;
            default -> null;
        };
    }
}

