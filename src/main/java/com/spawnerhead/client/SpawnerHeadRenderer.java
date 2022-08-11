package com.spawnerhead.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.spawnerhead.entity.SpawnerHeadEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpawnerHeadRenderer extends HumanoidMobRenderer<SpawnerHeadEntity, SpawnerHeadModel<SpawnerHeadEntity>> {
   private static final ResourceLocation[] TEXTURES = {new ResourceLocation("textures/entity/zombie/zombie.png"), new ResourceLocation("textures/entity/zombie/husk.png")};

   public SpawnerHeadRenderer(EntityRendererProvider.Context context) {
	      super(context, new SpawnerHeadModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
	      this.addLayer(new HumanoidArmorLayer<>(this, new SpawnerHeadModel(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)), new SpawnerHeadModel(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR))));
   }

   public ResourceLocation getTextureLocation(SpawnerHeadEntity entity) {
	   int type = entity.getSpawnerHeadType();
      return TEXTURES[type];
   }
   
   @Override
   public void render(SpawnerHeadEntity entity, float rotation, float delta, PoseStack stack, MultiBufferSource buffer, int light) {
		super.render(entity, rotation, delta, stack, buffer, light);
		stack.pushPose();
		stack.translate(0.0D, entity.getEyeHeight() - 0.4, 0.0D);
		BaseSpawner spawner = entity.getSpawner();
		Entity modelEntity = spawner.getOrCreateDisplayEntity(entity.getLevel());

		if (modelEntity != null) {
			float f = 0.4F;
			float f1 = Math.max(modelEntity.getBbWidth(), modelEntity.getBbHeight());
			if ((double) f1 > 1.0D) {
				f /= f1;
			}

			stack.translate(0.0D, (double) 0.4F, 0.0D);
			stack.mulPose(Vector3f.YP.rotationDegrees((float) Mth.lerp((double) delta, spawner.getoSpin(), spawner.getSpin()) * 10.0F));
			stack.translate(0.0D, (double) -0.2F, 0.0D);
			stack.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
			stack.scale(f, f, f);
			Minecraft.getInstance().getEntityRenderDispatcher().render(modelEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, stack, buffer, 15728880);
		}

		stack.popPose();
   }
}