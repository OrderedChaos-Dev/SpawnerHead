package spawnerhead;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import spawnerhead.entity.SpawnerHeadEntity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {
	
	public static final EntityType<SpawnerHeadEntity> SPAWNER_HEAD = registerEntity(EntityType.Builder.of(SpawnerHeadEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.99F), "spawner_head");

	public static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<?> builder, String name) {
		EntityType<T> entity = (EntityType<T>) builder.build(name).setRegistryName(new ResourceLocation(SpawnerHead.MOD_ID, name));

		return entity;
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		event.getRegistry().register(SPAWNER_HEAD);
		EntitySpawnPlacementRegistry.register(SPAWNER_HEAD, PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
	}
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(SPAWNER_HEAD, SpawnerHeadEntity.createAttributes().build());
	}
}
