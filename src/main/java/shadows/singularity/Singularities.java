package shadows.singularity;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import shadows.singularity.block.BlockCompressor;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.ItemSingularity;
import shadows.singularity.item.TileSingularity;
import shadows.singularity.proxy.Proxy;
import shadows.singularity.recipe.RecipeHelper;
import shadows.singularity.recipe.SingularityConfig;

@Mod(modid = Singularities.MODID, name = Singularities.NAME, version = "1.0.0")
public class Singularities {

	public static final String MODID = "singularities";
	public static final String NAME = "Singularities";

	@Instance
	public static Singularities INSTANCE;

	public static final CreativeTabs TAB = new CreativeTabs(MODID) {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(SINGULARITY);
		}
	};

	public static final Item SINGULARITY = new ItemSingularity();
	public static final Block COMPRESSOR = new BlockCompressor();

	@SidedProxy(clientSide = "shadows.singularity.proxy.ClientProxy", serverSide = "shadows.singularity.proxy.Proxy")
	public static Proxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		SingularityConfig.preInit(e);
		MinecraftForge.EVENT_BUS.register(this);
		proxy.preInit(e);
	}

	@SubscribeEvent
	public void item(Register<Item> e) {
		e.getRegistry().registerAll(SINGULARITY, new ItemBlock(COMPRESSOR).setRegistryName(COMPRESSOR.getRegistryName()));
	}
	
	@SubscribeEvent
	public void recipe(Register<IRecipe> e) {
		RecipeHelper.registry = e.getRegistry();
		RecipeHelper.addShaped(COMPRESSOR, 3, 3, "blockDiamond", Blocks.PISTON, "blockDiamond", Blocks.ENDER_CHEST, Items.NETHER_STAR, Blocks.ENDER_CHEST, "blockDiamond", Blocks.PISTON, "blockDiamond");
	}

	@SubscribeEvent
	public void block(Register<Block> e) {
		e.getRegistry().registerAll(COMPRESSOR);
		GameRegistry.registerTileEntity(TileCompressor.class, COMPRESSOR.getRegistryName().toString());
		GameRegistry.registerTileEntity(TileSingularity.class, MODID + ":what");
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		SingularityConfig.init(e);
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	}
}
