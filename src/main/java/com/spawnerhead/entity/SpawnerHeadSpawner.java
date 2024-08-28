package com.spawnerhead.entity;

import com.spawnerhead.SpawnerHeadConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpawnerHeadSpawner extends BaseSpawner {

  private final SpawnerHeadEntity spawnerHeadEntity;

  public SpawnerHeadSpawner(SpawnerHeadEntity entity) {
    this.spawnerHeadEntity = entity;
    if (entity.getEntityData().get(SpawnerHeadEntity.IS_CHARGED)) {
      this.minSpawnDelay = SpawnerHeadConfig.chargedMinSpawnDelay.get();
      this.maxSpawnDelay = SpawnerHeadConfig.chargedMaxSpawnDelay.get();
    } else {
      this.minSpawnDelay = SpawnerHeadConfig.minSpawnDelay.get();
      this.maxSpawnDelay = SpawnerHeadConfig.maxSpawnDelay.get();
    }
  }

  @Override
  public void broadcastEvent(Level level, BlockPos pos, int i) {
    level.broadcastEntityEvent(spawnerHeadEntity, (byte) i);
  }

  @Override
  @Nullable
  public Entity getSpawnerEntity() {
    return spawnerHeadEntity;
  }

  @Override
  @Nullable
  public Entity getOrCreateDisplayEntity(Level level, RandomSource random, BlockPos pos) {
    Optional<EntityType<?>> type = EntityType.byString(spawnerHeadEntity.getEntityData().get(SpawnerHeadEntity.SPAWNER_ENTITY_ID));
    if (this.displayEntity == null) {
      //sync entity with basespawner on client
      if (type.isPresent()) {
        if (level.isClientSide) {
          this.setEntityId(type.get(), level, random, pos);
        }

        this.displayEntity = super.getOrCreateDisplayEntity(level, random, pos);
      }
    } else if (type.isPresent() && this.displayEntity.getType() != type.get()) {
      this.displayEntity = null;
      this.setEntityId(EntityType.byString(spawnerHeadEntity.getEntityData().get(SpawnerHeadEntity.SPAWNER_ENTITY_ID)).get(), level, random, pos);
      this.displayEntity = super.getOrCreateDisplayEntity(level, random, pos);
    }

    if (spawnerHeadEntity.getEntityData().get(SpawnerHeadEntity.REFRESH_DISPLAY_ENTITY)) {
      if (this.displayEntity instanceof Creeper creeper && this.isCharged()) {
        creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
      }
      spawnerHeadEntity.getEntityData().set(SpawnerHeadEntity.REFRESH_DISPLAY_ENTITY, false);
    }

    return this.displayEntity;
  }

  public boolean isCharged() {
    return spawnerHeadEntity.getEntityData().get(SpawnerHeadEntity.IS_CHARGED);
  }

  public void setMaxSpawnDelay(int maxSpawnDelay) {
    this.maxSpawnDelay = maxSpawnDelay;
  }

  public void setMinSpawnDelay(int minSpawnDelay) {
    this.minSpawnDelay = minSpawnDelay;
  }

  @Override
  public void clientTick(Level level, BlockPos pos) {
    if (!this.spawnerHeadEntity.isUpsideDown()) {
      super.clientTick(level, pos);
    } else {
      if (!this.isNearPlayer(level, pos)) {
        this.oSpin = this.spin;
      } else if (this.displayEntity != null) {
        RandomSource randomsource = level.getRandom();
        double d0 = (double)pos.getX() + randomsource.nextDouble() + 0.5;
        double d1 = (double)spawnerHeadEntity.blockPosition().getY() + 0.4;
        double d2 = (double)pos.getZ() + randomsource.nextDouble() + 0.5;
        level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        if (this.spawnDelay > 0) {
          --this.spawnDelay;
        }

        this.oSpin = this.spin;
        this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
      }
    }
  }
}