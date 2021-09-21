package spawnerhead;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
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
	
	//TODO: maybe populate this with common hostile mobs from other mods?
	public static final List<String> DEFAULT_ENTITY_LIST = Arrays.asList(new String[] {
																				"minecraft:zombie-100",
																				"minecraft:spider-100",
																				"minecraft:husk-75",
																				"minecraft:stray-50",
																				"minecraft:skeleton-50"
																				});
	
	static {
		COMMON_BUILDER.push("Spawner Head Settings");
		spawnRate = COMMON_BUILDER.comment("Average Spawn Rate (example: '75' means 1 in 75 zombies will be a spawner head)").defineInRange("Spawn Rate", 50, 1, 100000);
		burnsInSunlight = COMMON_BUILDER.comment("Set spawner head mobs to burn in sunlight like regular undead").define("Burns In Sunlight", true);
		immuneToSkeletonArrows = COMMON_BUILDER.comment("Make spawner heads immune to skeleton arrows").define("Skeleton Arrow Immunity", false);
		immuneToCreeperExplosions = COMMON_BUILDER.comment("Make spawner heads immune to creeper explosions").define("Creeper Explosion Immunity", false);
		canBeLeashed = COMMON_BUILDER.comment("Allow players to use lead items on spawner heads").define("Can be leashed", false);
		dropSpecialLoot = COMMON_BUILDER.comment("Spawner heads loot will be based off their mob spawner entity. Setting to false will have them drop zombie loot").define("Drop Special Loot", true);
		COMMON_BUILDER.push("Spawner Head Potential Spawns Settings");
		potentialSpawnerMobs = COMMON_BUILDER.comment("Weighted list of possible spawner mobs, use 'entity_id-weight' format, eg (minecraft:husk-100)").defineList("Potential Spawner Mobs", DEFAULT_ENTITY_LIST, SpawnerHeadConfig::isValidString);
		COMMON_BUILDER.pop(2);
		
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
	
	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		
		configData.load();
		spec.setConfig(configData);
	}
	
	private static boolean isValidString(Object obj) {
		if(obj instanceof String) {
			return ((String)obj).split("-").length == 2;
		}
		
		return false;
	}
}
