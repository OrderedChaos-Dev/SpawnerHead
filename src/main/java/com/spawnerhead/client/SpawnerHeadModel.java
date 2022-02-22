package com.spawnerhead.client;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpawnerHeadModel<T extends Monster> extends AbstractZombieModel<T> {
	
	public SpawnerHeadModel(ModelPart part) {
		super(part);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of();
	}

	@Override
	public boolean isAggressive(T entity) {
		return true;
	}
}