package spawnerhead.entity;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.EntityType;
import net.minecraft.util.WeightedList;
import spawnerhead.SpawnerHead;
import spawnerhead.SpawnerHeadConfig;

public class SpawnerHeadSpawns {

	public static final WeightedList<EntityType<?>> SPAWN_POTENTIALS = new WeightedList<EntityType<?>>();
	
	public static void initSpawnList() {
		List<? extends String> list = SpawnerHeadConfig.potentialSpawnerMobs.get();
		list.forEach((item) -> {
			try {
				String[] split = item.split("-");
				Optional<EntityType<?>> entity = EntityType.byString(split[0]);
				int weight = Integer.parseInt(split[1]);
				
				if(entity.isPresent()) {
					SPAWN_POTENTIALS.add(entity.get(), weight);
				} else {
					SpawnerHead.LOGGER.error("Error parsing string ({}) for spawner list: invalid entity id", item);
				}
				
			} catch(Exception e) {
				SpawnerHead.LOGGER.error("Error parsing string ({}) for spawner list", item);
			}
		});
		if(list.size() == 0) {
			SpawnerHead.LOGGER.error("Error: empty list in config");
		}
	}
}
