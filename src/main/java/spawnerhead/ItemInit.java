package spawnerhead;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SpawnerHead.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ItemInit {

	public static Item spawn_egg = createSpawnEgg(EntityInit.SPAWNER_HEAD, 0x302840, 0x267353);
	
	@SubscribeEvent
	public static void initItems(RegistryEvent.Register<Item> event) {		
		event.getRegistry().register(spawn_egg);
	}
	
	public static Item registerItem(Item item, String name) {
		item.setRegistryName(new ResourceLocation(SpawnerHead.MOD_ID, name));
		
		return item;
	}
	
	public static Item createSpawnEgg(EntityType<?> entity, int color1, int color2) {
		return registerItem(new SpawnEggItem(entity, color1, color2, new Item.Properties().tab(ItemGroup.TAB_MISC)), entity.getRegistryName().getPath() + "_spawn_egg");
	}
}
