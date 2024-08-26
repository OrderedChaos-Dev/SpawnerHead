package com.spawnerhead.entity;

import com.spawnerhead.EntityInit;
import com.spawnerhead.SpawnerHeadConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntitySpawnEvent {

	@SubscribeEvent
	public void spawnSpawnerHead(MobSpawnEvent.FinalizeSpawn event) {
		Mob entity = event.getEntity();
		
		if(entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.HUSK) {
			if(event.getResult() != Result.DENY && event.getSpawnType() == MobSpawnType.NATURAL) {
				int rate = SpawnerHeadConfig.spawnRate.get();
				if(event.getLevel().getRandom().nextInt(rate) == 0) {
					SpawnerHeadEntity spawner = EntityInit.SPAWNER_HEAD.get().create(event.getEntity().level());
					spawner.copyPosition(entity);
					
					if(event.getLevel() instanceof ServerLevelAccessor)
						spawner.finalizeSpawn((ServerLevelAccessor) event.getLevel(), event.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.NATURAL, null, null);
					
					if(entity.getType() == EntityType.ZOMBIE)
						spawner.setSpawnerHeadType(0);
					else
						spawner.setSpawnerHeadType(1);
					
					entity.level().addFreshEntity(spawner);
					event.setResult(Result.DENY);
				}
			}
		}
	}
}
