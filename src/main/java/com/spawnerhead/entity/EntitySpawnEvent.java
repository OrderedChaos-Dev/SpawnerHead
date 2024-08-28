package com.spawnerhead.entity;

import com.spawnerhead.EntityInit;
import com.spawnerhead.SpawnerHeadConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
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
				ServerLevelAccessor level = event.getLevel();
				if(level.getRandom().nextInt(rate) == 0) {
					SpawnerHeadEntity spawnerHead = EntityInit.SPAWNER_HEAD.get().create(entity.level());
					spawnerHead.copyPosition(entity);

					if(event.getLevel() != null)
						spawnerHead.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.NATURAL, null, null);
					
					if(entity.getType() == EntityType.ZOMBIE)
						spawnerHead.setSpawnerHeadType(0);
					else
						spawnerHead.setSpawnerHeadType(1);
					
					entity.level().addFreshEntity(spawnerHead);
					event.setResult(Result.DENY);
				}
			}
		} else if (entity instanceof Creeper creeper) {
			if (event.getResult() != Result.DENY && event.getSpawnType() == MobSpawnType.SPAWNER) {
				if (event.getSpawner() instanceof SpawnerHeadSpawner spawner && SpawnerHeadConfig.chargedSpawnsChargedCreepers.get()) {
					if (spawner.isCharged()) {
						creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
					}
				}
			}
		}
	}
}
