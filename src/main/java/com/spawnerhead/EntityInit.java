package com.spawnerhead;

import com.spawnerhead.entity.SpawnerHeadEntity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SpawnerHead.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {
	
	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, SpawnerHead.MOD_ID);
	
	public static final RegistryObject<EntityType<SpawnerHeadEntity>> SPAWNER_HEAD = REGISTER.register("spawner_head", () -> registerEntity(EntityType.Builder.of(SpawnerHeadEntity::new, MobCategory.MONSTER).sized(0.6F, 1.99F), "spawner_head"));

	public static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<?> builder, String name) {
		return (EntityType<T>) builder.build(name);
	}
	
//	@SubscribeEvent
//	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
//		event.getRegistry().register(SPAWNER_HEAD);
//		SpawnPlacements.register(SPAWNER_HEAD, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
//	}
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		SpawnPlacements.register(SPAWNER_HEAD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
		event.put(SPAWNER_HEAD.get(), SpawnerHeadEntity.createAttributes().build());
	}
}
