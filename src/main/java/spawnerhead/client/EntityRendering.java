package spawnerhead.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import spawnerhead.EntityInit;

public class EntityRendering {
	
	@OnlyIn(Dist.CLIENT)
	public static void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityInit.SPAWNER_HEAD, SpawnerHeadRenderer::new);
	}
}
