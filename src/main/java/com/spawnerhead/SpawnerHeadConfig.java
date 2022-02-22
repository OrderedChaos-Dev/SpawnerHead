package com.spawnerhead;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class SpawnerHeadConfig {
	
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;
	
	public static ForgeConfigSpec.ConfigValue<Integer> spawnRate;
	public static ForgeConfigSpec.ConfigValue<Boolean> burnsInSunlight;
	public static ForgeConfigSpec.ConfigValue<Boolean> immuneToSkeletonArrows;
	public static ForgeConfigSpec.ConfigValue<Boolean> immuneToCreeperExplosions;
	public static ForgeConfigSpec.ConfigValue<Boolean> canBeLeashed;
	public static ForgeConfigSpec.ConfigValue<Boolean> dropSpecialLoot;
	public static ConfigValue<List<? extends String>> potentialSpawnerMobs;
	
	static {
		COMMON_BUILDER.push("Spawner Head Settings");
		spawnRate = COMMON_BUILDER.comment("Average Spawn Rate (example: '75' means 1 in 75 zombies will be a spawner head)").defineInRange("Spawn Rate", 50, 1, 100000);
		burnsInSunlight = COMMON_BUILDER.comment("Set spawner head mobs to burn in sunlight like regular undead").define("Burns In Sunlight", true);
		immuneToSkeletonArrows = COMMON_BUILDER.comment("Make spawner heads immune to skeleton arrows").define("Skeleton Arrow Immunity", false);
		immuneToCreeperExplosions = COMMON_BUILDER.comment("Make spawner heads immune to creeper explosions").define("Creeper Explosion Immunity", false);
		canBeLeashed = COMMON_BUILDER.comment("Allow players to use lead items on spawner heads").define("Can Be Leashed", false);
		dropSpecialLoot = COMMON_BUILDER.comment("Make spawner heads drop dungeon loot on death. Drops zombie loot when set to false.").define("Drop Special Loot", true);
		COMMON_BUILDER.pop();
		
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
}