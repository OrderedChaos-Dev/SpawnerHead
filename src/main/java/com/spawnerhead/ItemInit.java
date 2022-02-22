package com.spawnerhead;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SpawnerHead.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ItemInit {

	public static Item spawnerhead_spawn_egg = createSpawnEgg(EntityInit.SPAWNER_HEAD, 0x302840, 0x267353);
	
	@SubscribeEvent
	public static void initItems(RegistryEvent.Register<Item> event) {		
		event.getRegistry().register(spawnerhead_spawn_egg);
	}
	
	public static Item registerItem(Item item, String name) {
		item.setRegistryName(new ResourceLocation(SpawnerHead.MOD_ID, name));
		
		return item;
	}
	
	public static Item createSpawnEgg(EntityType<? extends Mob> entity, int color1, int color2) {
		return registerItem(new ForgeSpawnEggItem(() -> entity, color1, color2, new Item.Properties().tab(CreativeModeTab.TAB_MISC)), entity.getRegistryName().getPath() + "_spawn_egg");
	}
}
