package com.spawnerhead.entity;

import com.spawnerhead.SpawnerHeadConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpawnerHeadSpawner extends BaseSpawner {

  private final SpawnerHeadEntity spawnerHeaderEntity;

  public SpawnerHeadSpawner(SpawnerHeadEntity entity) {
    this.spawnerHeaderEntity = entity;
    if (entity.getEntityData().get(SpawnerHeadEntity.IS_CHARGED)) {
      this.minSpawnDelay = SpawnerHeadConfig.chargedMinSpawnDelay.get();
      this.maxSpawnDelay = SpawnerHeadConfig.chargedMaxSpawnDelay.get();
    }
  }

  @Override
  public void broadcastEvent(Level level, BlockPos pos, int i) {
    level.broadcastEntityEvent(spawnerHeaderEntity, (byte) i);
  }

  @Override
  @Nullable
  public Entity getSpawnerEntity() {
    return spawnerHeaderEntity;
  }

  @Override
  @Nullable
  public Entity getOrCreateDisplayEntity(Level level, RandomSource random, BlockPos pos) {
    if (this.displayEntity == null) {
      Optional<EntityType<?>> type = EntityType.byString(spawnerHeaderEntity.getEntityData().get(SpawnerHeadEntity.SPAWNER_ENTITY_ID));
      //sync entity with basespawner on client
      if (type.isPresent()) {
        if (level.isClientSide) {
          this.setEntityId(type.get(), level, random, pos);
        }

        this.displayEntity = super.getOrCreateDisplayEntity(level, random, pos);
      }
    } else if (this.displayEntity.getType() != EntityType.byString(spawnerHeaderEntity.getEntityData().get(SpawnerHeadEntity.SPAWNER_ENTITY_ID)).get()) {
      this.displayEntity = null;
      this.setEntityId(EntityType.byString(spawnerHeaderEntity.getEntityData().get(SpawnerHeadEntity.SPAWNER_ENTITY_ID)).get(), level, random, pos);
      this.displayEntity = super.getOrCreateDisplayEntity(level, random, pos);
    }

    if (this.displayEntity instanceof Creeper creeper && this.isCharged()) {
      creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
    }

    return this.displayEntity;
  }

  public boolean isCharged() {
    return spawnerHeaderEntity.getEntityData().get(SpawnerHeadEntity.IS_CHARGED);
  }

  public void setMaxSpawnDelay(int maxSpawnDelay) {
    this.maxSpawnDelay = maxSpawnDelay;
  }

  public void setMinSpawnDelay(int minSpawnDelay) {
    this.minSpawnDelay = minSpawnDelay;
  }
}