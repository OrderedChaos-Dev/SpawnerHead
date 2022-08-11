package com.spawnerhead.entity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spawnerhead.SpawnerHead;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;

public class SpawnerHeadSpawns {

	public static SimpleWeightedRandomList.Builder<EntityType<?>> builder = SimpleWeightedRandomList.builder();
	public static SimpleWeightedRandomList<EntityType<?>> SPAWN_POTENTIALS = null;
	
	public static final List<SpawnPotentialsData> SPAWN_POTENTIAL_DEFAULTS = List.of(
										new SpawnPotentialsData("minecraft:zombie", 100),
										new SpawnPotentialsData("minecraft:spider", 100),
										new SpawnPotentialsData("minecraft:skeleton", 50),
										new SpawnPotentialsData("minecraft:husk", 75),
										new SpawnPotentialsData("minecraft:stray", 50));
	
	public static void initSpawnList() {		
		Path path = FMLPaths.CONFIGDIR.get().resolve("spawnerhead/spawn_potentials.json");
		if(!path.toFile().exists()) {
			try {
				Files.createDirectories(path.getParent());
				Files.write(path, new GsonBuilder().setPrettyPrinting().create().toJson(SPAWN_POTENTIAL_DEFAULTS).getBytes());
			} catch(Exception e) {
				SpawnerHead.LOGGER.error(e.toString());
			}
		}
		
		HashMap<String, Integer> spawnPotentialMap = new HashMap<String, Integer>();
		
		try {
			String input = Files.readString(path);
			JsonArray array = JsonParser.parseString(input).getAsJsonArray();

			array.forEach((element) -> {
				JsonObject data = element.getAsJsonObject();
				String entityType = data.getAsJsonPrimitive("entityID").getAsString();
				int weight = data.getAsJsonPrimitive("weight").getAsInt();
				if(spawnPotentialMap.containsKey(entityType)) {
					SpawnerHead.LOGGER.warn("Skipping duplicate spawn entry found for spawner head: {}", entityType);
				} else {
					spawnPotentialMap.put(entityType, weight);
				}
			});
			
			if(spawnPotentialMap.isEmpty()) {
				SpawnerHead.LOGGER.error("No spawn potentials for spawner heads! Adding zombie to prevent issues.");
				spawnPotentialMap.put(EntityType.ZOMBIE.getRegistryName().toString(), 100);
			}
			
			spawnPotentialMap.forEach((a, b) -> {
				Optional<EntityType<?>> entity = EntityType.byString(a);
				if(entity.isPresent()) {
					SpawnerHead.LOGGER.debug("Adding [{} with weight {} to spawner header spawn potentials pool]", a, b);
					builder.add(entity.get(), b);
				} else {
					SpawnerHead.LOGGER.warn("Entity type for spawner head not found: {}", a);
				}
			});
			
		} catch(Exception e) {
			SpawnerHead.LOGGER.error(e.toString());
		}
		
		SPAWN_POTENTIALS = builder.build();

	}
}
