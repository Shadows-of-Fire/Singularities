package shadows.singularity.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shadows.placebo.Placebo;
import shadows.singularity.Singularities;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.client.ItemLayerWrapper;
import shadows.singularity.client.RenderCompressor;
import shadows.singularity.client.RenderSingularity;
import shadows.singularity.item.Singularity;

public class ClientProxy extends Proxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		ClientRegistry.bindTileEntitySpecialRenderer(TileCompressor.class, new RenderCompressor());
		Singularities.SINGULARITY.setTileEntityItemStackRenderer(new RenderSingularity());
	}

	private static final ModelResourceLocation MRL = new ModelResourceLocation(new ResourceLocation(Singularities.MODID, "default"), "type=singularity");

	@SubscribeEvent
	public void models(ModelRegistryEvent e) {
		Placebo.PROXY.useRenamedMapper(Singularities.COMPRESSOR, "default", ",type=compressor");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Singularities.COMPRESSOR), 0, new ModelResourceLocation(new ResourceLocation(Singularities.MODID, "default"), "facing=north,type=compressor"));
		for (Singularity s : Singularity.getSingularities())
			ModelLoader.setCustomModelResourceLocation(Singularities.SINGULARITY, s.getID(), MRL);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tint) -> {
			Singularity sing = Singularity.getByID(stack.getMetadata());
			if (tint == 0) return sing.getColor2();
			if (tint == 1) return sing.getColor1();
			return -1;
		}, Singularities.SINGULARITY);
	}

	@SubscribeEvent
	public void onTexStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(new ResourceLocation(Singularities.MODID, "items/halo"));
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		RenderSingularity.model = new ItemLayerWrapper(event.getModelRegistry().getObject(MRL));
		event.getModelRegistry().putObject(MRL, RenderSingularity.model);
	}

	@Override
	public String translate(String lang, Object... args) {
		return I18n.format(lang, args);
	}
}
