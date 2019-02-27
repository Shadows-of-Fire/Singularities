package shadows.singularity.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import shadows.placebo.util.PlaceboUtil;
import shadows.singularity.Singularities;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.Singularity;

@EventBusSubscriber(modid = Singularities.MODID, value = Side.CLIENT)
public class ClientHandler {

	static ModelResourceLocation mrl = new ModelResourceLocation(Singularities.SINGULARITY.getRegistryName(), "inventory");

	@SubscribeEvent
	public static void models(ModelRegistryEvent e) {
		PlaceboUtil.sMRL(Singularities.COMPRESSOR, 0, "facing=north");
		for (Singularity s : Singularity.getSingularities())
			ModelLoader.setCustomModelResourceLocation(Singularities.SINGULARITY, s.getID(), mrl);

		ClientRegistry.bindTileEntitySpecialRenderer(TileCompressor.class, new RenderCompressor());
		Singularities.SINGULARITY.setTileEntityItemStackRenderer(new RenderSingularity());
	}

	@SubscribeEvent
	public static void colors(ColorHandlerEvent.Item e) {
		e.getItemColors().registerItemColorHandler((stack, tint) -> {
			Singularity sing = Singularity.getByID(stack.getMetadata());
			if (tint == 0) return sing.getColor2();
			if (tint == 1) return sing.getColor1();
			return -1;
		}, Singularities.SINGULARITY);
	}

	@SubscribeEvent
	public static void onTexStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(new ResourceLocation(Singularities.MODID, "items/halo"));
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		RenderSingularity.model = new ItemLayerWrapper(event.getModelRegistry().getObject(mrl));
		event.getModelRegistry().putObject(mrl, RenderSingularity.model);
	}

}
