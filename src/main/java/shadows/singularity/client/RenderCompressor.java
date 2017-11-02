package shadows.singularity.client;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.ItemSingularity;
import shadows.singularity.item.Singularity;
import shadows.singularity.recipe.ICompressorRecipe;

public class RenderCompressor extends TileEntitySpecialRenderer<TileCompressor> {

	@Override
	public void render(TileCompressor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te == null) return;
		if (!te.getWorld().isAirBlock(te.getPos().up()) || Minecraft.getMinecraft().world.rayTraceBlocks(playerVec(), new Vec3d(te.getPos().up()).addVector(0.5, 0.5, 0.5)) != null) return;

		ICompressorRecipe rec = te.getRecipe();
		if (rec != null) {
			GlStateManager.pushMatrix();
			int i = te.getWorld().getCombinedLight(te.getPos().up(), 0);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % 65536, i / 65536);

			boolean thirdPerson = Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2;
			float viewerYaw = this.rendererDispatcher.entityYaw;
			GlStateManager.translate(x + 0.5, y + 1.9, z + 0.5);
			float angleRotate = thirdPerson ? -viewerYaw : -viewerYaw % 360 + 180;
			float angleRotateItem = !thirdPerson ? -viewerYaw : -viewerYaw % 360 + 180;

			GlStateManager.rotate(angleRotateItem, 0, 1, 0);
			GlStateManager.enableBlend();
			GlStateManager.disableDepth();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			Minecraft.getMinecraft().getRenderItem().renderItem(rec.getOutputStack(), TransformType.FIXED);

			GlStateManager.rotate(-angleRotateItem, 0, 1, 0);
			GlStateManager.rotate(angleRotate, 0, 1, 0);

			BufferBuilder dad = Tessellator.getInstance().getBuffer();
			GlStateManager.disableTexture2D();
			GlStateManager.translate(-.375, -.7, 0);

			GlStateManager.glNormal3f(0, 0, 0);

			double prog = te.getCounter() / (double) rec.getRequiredInputs();
			prog *= 0.75;
			int color1 = Color.WHITE.getRGB();
			int r1 = (color1 >> 16) & 0xFF;
			int g1 = (color1 >> 8) & 0xFF;
			int b1 = (color1 >> 0) & 0xFF;

			Singularity s = rec.getOutputStack().getItem() instanceof ItemSingularity ? Singularity.getByID(rec.getOutputStack().getMetadata()) : null;

			int color = s == null ? Color.GRAY.getRGB() : s.getColor1();
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = (color >> 0) & 0xFF;

			dad.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			dad.pos(prog, 0, 0).color(r1, g1, b1, 255).endVertex();
			dad.pos(.75, 0, 0).color(r1, g1, b1, 255).endVertex();
			dad.pos(.75, .1, 0).color(r1, g1, b1, 255).endVertex();
			dad.pos(prog, .1, 0).color(r1, g1, b1, 255).endVertex();
			Tessellator.getInstance().draw();

			dad.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			dad.pos(0, 0, 0).color(r, g, b, 255).endVertex();
			dad.pos(prog, 0, 0).color(r, g, b, 255).endVertex();
			dad.pos(prog, .1, 0).color(r, g, b, 255).endVertex();
			dad.pos(0, .1, 0).color(r, g, b, 255).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.enableTexture2D();
			GlStateManager.enableDepth();
			GlStateManager.disableBlend();

			GlStateManager.popMatrix();
		} else {
			int i = te.getWorld().getCombinedLight(te.getPos().up(), 0);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % 65536, i / 65536);
			this.drawNameplate(te, "Idle", x, y, z, 12);
		}
	}

	private Vec3d playerVec() {
		return Minecraft.getMinecraft().player.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
	}

}
