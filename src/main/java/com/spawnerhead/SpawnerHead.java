package com.spawnerhead;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.spawnerhead.client.EntityRendering;
import com.spawnerhead.entity.EntitySpawnEvent;
import com.spawnerhead.entity.SpawnerHeadSpawns;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpawnerHead.MOD_ID)
public class SpawnerHead
{
	public static final String MOD_ID = "spawnerhead";
    public static final Logger LOGGER = LogManager.getLogger();

    public SpawnerHead() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::setup);
    	bus.addListener(this::doClientStuff);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpawnerHeadConfig.COMMON_CONFIG);
        EntityInit.REGISTER.register(bus);
        ItemInit.REGISTER.register(bus);
    }

    private void setup(final FMLCommonSetupEvent event){
    	event.enqueueWork(() -> {
        	SpawnerHeadSpawns.initSpawnList();
        	MinecraftForge.EVENT_BUS.register(new EntitySpawnEvent());
    	});
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    	EntityRendering.registerRenderers();
    }
}