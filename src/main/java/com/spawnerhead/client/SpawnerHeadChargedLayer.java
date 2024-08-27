package com.spawnerhead.client;

import com.spawnerhead.entity.SpawnerHeadEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpawnerHeadChargedLayer extends EnergySwirlLayer<SpawnerHeadEntity, SpawnerHeadModel<SpawnerHeadEntity>> {
  private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

  private final SpawnerHeadModel<SpawnerHeadEntity> model;

  public SpawnerHeadChargedLayer(RenderLayerParent<SpawnerHeadEntity, SpawnerHeadModel<SpawnerHeadEntity>> renderLayerParent, EntityModelSet entityModelSet) {
    super(renderLayerParent);
    this.model = new SpawnerHeadModel<>(entityModelSet.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR));
  }

  protected float xOffset(float tick) {
    return tick * 0.01F;
  }

  protected ResourceLocation getTextureLocation() {
    return POWER_LOCATION;
  }

  protected EntityModel<SpawnerHeadEntity> model() {
    return this.model;
  }
}