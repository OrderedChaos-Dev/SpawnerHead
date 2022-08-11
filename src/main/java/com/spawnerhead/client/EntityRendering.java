package com.spawnerhead.client;

import com.spawnerhead.EntityInit;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityRendering {
	
	@OnlyIn(Dist.CLIENT)
	public static void registerRenderers() {
		EntityRenderers.register(EntityInit.SPAWNER_HEAD.get(), SpawnerHeadRenderer::new);
	}
}
