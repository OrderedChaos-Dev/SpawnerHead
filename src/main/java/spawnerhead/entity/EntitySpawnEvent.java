package spawnerhead.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.IServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import spawnerhead.EntityInit;
import spawnerhead.SpawnerHeadConfig;

public class EntitySpawnEvent {

	@SubscribeEvent
	public void spawnSpawnerHead(LivingSpawnEvent.CheckSpawn event) {
		LivingEntity entity = event.getEntityLiving();
		
		if(entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.HUSK) {
			if(event.getResult() != Result.DENY && event.getSpawnReason() == SpawnReason.NATURAL) {
				int rate = SpawnerHeadConfig.spawnRate.get();
				if(event.getWorld().getRandom().nextInt(rate) == 0) {
					SpawnerHeadEntity spawner = EntityInit.SPAWNER_HEAD.create(event.getEntityLiving().level);
					spawner.copyPosition(entity);
					
					if(event.getWorld() instanceof IServerWorld)
						spawner.finalizeSpawn((IServerWorld) event.getWorld(), event.getWorld().getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.NATURAL, null, null);
					
					//TODO: write system to support other bipeds
					if(entity.getType() == EntityType.ZOMBIE)
						spawner.setSpawnerHeadType(0);
					else
						spawner.setSpawnerHeadType(1);
					
					entity.level.addFreshEntity(spawner);
					event.setResult(Result.DENY);
				}
			}
		}
	}
}
