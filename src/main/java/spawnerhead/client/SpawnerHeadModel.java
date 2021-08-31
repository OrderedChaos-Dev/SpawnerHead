package spawnerhead.client;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.AbstractZombieModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpawnerHeadModel<T extends MonsterEntity> extends AbstractZombieModel<T> {
	
	public SpawnerHeadModel(float f) {
		super(f, 0.0F, 64, 64);
	}

	@Override
	protected Iterable<ModelRenderer> headParts() {
		return ImmutableList.of();
	}

	@Override
	public boolean isAggressive(T entity) {
		return true;
	}
}