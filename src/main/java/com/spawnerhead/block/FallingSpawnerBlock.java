package com.spawnerhead.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static net.minecraft.world.level.block.FallingBlock.isFree;

public class FallingSpawnerBlock extends Block implements Fallable {

  public FallingSpawnerBlock() {
    super(BlockBehaviour.Properties.copy(Blocks.SPAWNER));
  }

  @Override
  public void onPlace(BlockState p_53233_, Level p_53234_, BlockPos p_53235_, BlockState p_53236_, boolean p_53237_) {
    p_53234_.scheduleTick(p_53235_, this, 2);
  }

  @Override
  public BlockState updateShape(BlockState p_53226_, Direction p_53227_, BlockState p_53228_, LevelAccessor p_53229_, BlockPos p_53230_, BlockPos p_53231_) {
    p_53229_.scheduleTick(p_53230_, this, 2);
    return super.updateShape(p_53226_, p_53227_, p_53228_, p_53229_, p_53230_, p_53231_);
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
      FallingBlockEntity.fall(level, pos, state);
    } else {
      level.removeBlock(pos, false);
    }
  }

  @Override
  public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
    if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
      FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level, pos, this.defaultBlockState());
      fallingBlockEntity.blockData = entity.blockData.copy();
    } else {
      level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity) {
        CompoundTag tag = entity.blockData;
        Optional<EntityType<?>> entityType = EntityType.by(tag);
        entityType.ifPresent(type -> spawnerBlockEntity.setEntityId(type, level.random));
      }
    }
  }

  @Override
  public void onLand(Level level, BlockPos pos, BlockState fallingBlockState, BlockState landingState, FallingBlockEntity entity) {
    if (entity.blockData.getBoolean("placeBlock")) {
      level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity) {
        CompoundTag tag = entity.blockData;
        Optional<EntityType<?>> entityType = EntityType.by(tag);
        entityType.ifPresent(type -> spawnerBlockEntity.setEntityId(type, level.random));
      }
    }
  }
}
