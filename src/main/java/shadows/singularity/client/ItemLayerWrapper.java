package shadows.singularity.client;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLayerWrapper implements IBakedModel {

	private final IBakedModel internal;

	private class Overrides extends ItemOverrideList {

		public Overrides() {
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			RenderSingularity.stack = stack;
			return ItemLayerWrapper.this;
		}
	}

	public ItemLayerWrapper(IBakedModel internal) {
		this.internal = internal;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return internal.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return internal.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return internal.isGui3d();
	}

	public IBakedModel getInternal() {
		return internal;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return internal.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new Overrides();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		RenderSingularity.transform = cameraTransformType;
		return Pair.of(this, internal.handlePerspective(cameraTransformType).getRight());
	}

}
