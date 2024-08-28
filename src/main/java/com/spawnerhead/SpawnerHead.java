package com.spawnerhead;

import com.spawnerhead.entity.EntitySpawnEvent;
import com.spawnerhead.entity.SpawnerHeadSpawns;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SpawnerHead.MOD_ID)
public class SpawnerHead
{
	public static final String MOD_ID = "spawnerhead";
    public static final Logger LOGGER = LogManager.getLogger();

    public SpawnerHead() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::setup);
        
      ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpawnerHeadConfig.COMMON_CONFIG);
      EntityInit.REGISTER.register(bus);
      ItemInit.REGISTER.register(bus);
      BlockInit.REGISTER.register(bus);
      bus.addListener(this::clientSetup);
      bus.addListener(this::handleCreativeTabs);
    }

    private void setup(final FMLCommonSetupEvent event){
    	event.enqueueWork(() -> {
        	SpawnerHeadSpawns.initSpawnList();
        	MinecraftForge.EVENT_BUS.register(new EntitySpawnEvent());
    	});
    }

  private void clientSetup(final FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ItemBlockRenderTypes.setRenderLayer(BlockInit.FALLING_SPAWNER.get(), RenderType.cutout());
    });
  }

    private void handleCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        event.accept(ItemInit.SPAWNER_HEAD_SPAWN_EGG);
      }
    }
}