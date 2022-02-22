package com.spawnerhead.entity;

import com.spawnerhead.EntityInit;
import com.spawnerhead.SpawnerHeadConfig;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntitySpawnEvent {

	@SubscribeEvent
	public void spawnSpawnerHead(LivingSpawnEvent.CheckSpawn event) {
		LivingEntity entity = event.getEntityLiving();
		
		if(entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.HUSK) {
			if(event.getResult() != Result.DENY && event.getSpawnReason() == MobSpawnType.NATURAL) {
				int rate = SpawnerHeadConfig.spawnRate.get();
				if(event.getWorld().getRandom().nextInt(rate) == 0) {
					SpawnerHeadEntity spawner = EntityInit.SPAWNER_HEAD.create(event.getEntityLiving().level);
					spawner.copyPosition(entity);
					
					if(event.getWorld() instanceof ServerLevelAccessor)
						spawner.finalizeSpawn((ServerLevelAccessor) event.getWorld(), event.getWorld().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.NATURAL, null, null);
					
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
