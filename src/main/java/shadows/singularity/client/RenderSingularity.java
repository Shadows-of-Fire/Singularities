package shadows.singularity.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import shadows.singularity.Singularities;

public class RenderSingularity extends TileEntityItemStackRenderer {

	public static ItemLayerWrapper model;
	public static TransformType transform;
	private static int haloColour = 0xFF000000;
	private static float ca = (haloColour >> 24 & 255) / 255.0F;
	private static float cr = (haloColour >> 16 & 255) / 255.0F;
	private static float cg = (haloColour >> 8 & 255) / 255.0F;
	private static float cb = (haloColour & 255) / 255.0F;

	@Override
	public void renderByItem(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		TextureAtlasSprite halo = mc.getTextureMapBlocks().getAtlasSprite(Singularities.MODID + ":items/halo");
		Tessellator t = Tessellator.getInstance();
		model.handlePerspective(transform);

		GlStateManager.pushMatrix();

		if (transform == TransformType.FIXED || transform == TransformType.GUI) {
			if (transform == TransformType.GUI) GlStateManager.disableDepth();

			GlStateManager.color(cr, cg, cb, ca);
			GlStateManager.disableAlpha();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			BufferBuilder bob = t.getBuffer();
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			GlStateManager.translate(0, 0, 0.5);
			bob.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bob.pos(-.25, -.25, 0).tex(halo.getMinU(), halo.getMinV()).endVertex();
			bob.pos(1.25, -.25, 0).tex(halo.getMinU(), halo.getMaxV()).endVertex();
			bob.pos(1.25, 1.25, 0).tex(halo.getMaxU(), halo.getMaxV()).endVertex();
			bob.pos(-.25, 1.25, 0).tex(halo.getMaxU(), halo.getMinV()).endVertex();
			t.draw();

			GlStateManager.translate(0, 0, -0.5);
			if (transform == TransformType.GUI) GlStateManager.enableDepth();
			GlStateManager.enableAlpha();
		}

		GlStateManager.translate(0.5, 0.5, 0.5);
		mc.getRenderItem().renderItem(stack, model.getInternal());
		GlStateManager.popMatrix();
	}

}
