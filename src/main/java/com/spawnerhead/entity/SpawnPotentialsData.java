package com.spawnerhead.entity;

import java.util.Optional;

import net.minecraft.world.entity.EntityType;

public record SpawnPotentialsData(String entityID, int weight) {

	public Optional<EntityType<?>> getEntityType() {
		return EntityType.byString(this.entityID);
	}
}
