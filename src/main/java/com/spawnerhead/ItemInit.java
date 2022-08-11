package com.spawnerhead;

import java.util.function.Supplier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SpawnerHead.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ItemInit {
	
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, SpawnerHead.MOD_ID);

	public static RegistryObject<Item> spawnerhead_spawn_egg = REGISTER.register("spawner_head_spawn_egg", () -> createSpawnEgg(EntityInit.SPAWNER_HEAD, 0x302840, 0x267353));
	
	public static Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> entity, int color1, int color2) {
		return new ForgeSpawnEggItem(entity, color1, color2, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
	}
}
