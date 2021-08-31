package spawnerhead.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spawnerhead.entity.SpawnerHeadEntity;

@OnlyIn(Dist.CLIENT)
public class SpawnerHeadRenderer extends BipedRenderer<SpawnerHeadEntity, SpawnerHeadModel<SpawnerHeadEntity>> {
   private static final ResourceLocation[] TEXTURES = {new ResourceLocation("textures/entity/zombie/zombie.png"), new ResourceLocation("textures/entity/zombie/husk.png")};

   public SpawnerHeadRenderer(EntityRendererManager manager) {
      super(manager, new SpawnerHeadModel<>(0.0F) ,0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new SpawnerHeadModel(0.5F), new SpawnerHeadModel(1.0F)));
   }

   public ResourceLocation getTextureLocation(SpawnerHeadEntity entity) {
	   int type = entity.getSpawnerHeadType();
      return TEXTURES[type];
   }
   
   @Override
   public void render(SpawnerHeadEntity entity, float rotation, float delta, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
		super.render(entity, rotation, delta, stack, buffer, light);
		stack.pushPose();
		stack.translate(0.0D, entity.getEyeHeight() - 0.4, 0.0D);
		AbstractSpawner spawner = entity.getSpawner();
		Entity modelEntity = spawner.getOrCreateDisplayEntity();
		if (modelEntity != null) {
			float f = 0.45F;
			float f1 = Math.max(modelEntity.getBbWidth(), modelEntity.getBbHeight());
			if ((double) f1 > 1.0D) {
				f /= f1;
			}

			stack.translate(0.0D, (double) 0.4F, 0.0D);
			stack.mulPose(Vector3f.YP.rotationDegrees((float) MathHelper.lerp((double) delta, spawner.getoSpin(), spawner.getSpin()) * 10.0F));
			stack.translate(0.0D, (double) -0.2F, 0.0D);
			stack.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
			stack.scale(f, f, f);
			Minecraft.getInstance().getEntityRenderDispatcher().render(modelEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, stack, buffer, 15728880);
		}

		stack.popPose();
   }
}