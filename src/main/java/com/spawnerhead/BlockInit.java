package com.spawnerhead;

import com.spawnerhead.block.FallingSpawnerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SpawnerHead.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockInit {

  public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, SpawnerHead.MOD_ID);

  public static final RegistryObject<Block> FALLING_SPAWNER = REGISTER.register("falling_spawner", FallingSpawnerBlock::new);

}
