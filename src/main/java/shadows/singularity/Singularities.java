package shadows.singularity;

import java.util.Comparator;

import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import shadows.placebo.registry.RegistryInformation;
import shadows.placebo.util.RecipeHelper;
import shadows.singularity.avaritia.AvaritiaCompat;
import shadows.singularity.block.BlockCompressor;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.ItemSingularity;
import shadows.singularity.item.Singularity;
import shadows.singularity.proxy.Proxy;
import shadows.singularity.recipe.SingularityConfig;

@Mod(modid = Singularities.MODID, name = Singularities.MODNAME, version = Singularities.VERSION, dependencies = Singularities.DEPS)
public class Singularities {

	public static final String MODID = "singularities";
	public static final String MODNAME = "Singularities";
	public static final String VERSION = "2.4.1";
	public static final String DEPS = "required-after:placebo@[1.2.0,);before:crafttweaker;after:avaritia";

	public static final CreativeTabs TAB = new CreativeTabs(MODID) {

		@Override
		public ItemStack createIcon() {
			return new ItemStack(SINGULARITY);
		}
	};

	public static final RegistryInformation INFO = new RegistryInformation(MODID, TAB);
	public static final RecipeHelper HELPER = new RecipeHelper(MODID, MODNAME, INFO.getRecipeList());

	public static final Item SINGULARITY = new ItemSingularity();
	public static final Block COMPRESSOR = new BlockCompressor();

	@SidedProxy(clientSide = "shadows.singularity.proxy.ClientProxy", serverSide = "shadows.singularity.proxy.Proxy")
	public static Proxy PROXY;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		SingularityConfig.preInit(e);
		MinecraftForge.EVENT_BUS.register(this);
		PROXY.preInit(e);
	}

	@SubscribeEvent
	public void block(Register<Block> e) {
		e.getRegistry().register(COMPRESSOR);
		GameRegistry.registerTileEntity(TileCompressor.class, COMPRESSOR.getRegistryName());
	}

	@SubscribeEvent
	public void item(Register<Item> e) {
		e.getRegistry().registerAll(INFO.getItemList().toArray(new Item[2]));
		Singularity.operateSorted(s -> OreDictionary.registerOre(s.makeOreName(), new ItemStack(SINGULARITY, 1, s.getID())));
	}

	@SubscribeEvent
	public void recipe(Register<IRecipe> e) {
		if (SingularityConfig.hideCompressor) return;
		HELPER.addShaped(COMPRESSOR, 3, 3, "blockDiamond", Blocks.PISTON, "blockDiamond", Blocks.ENDER_CHEST, Items.NETHER_STAR, Blocks.ENDER_CHEST, "blockDiamond", Blocks.PISTON, "blockDiamond");
		e.getRegistry().registerAll(INFO.getRecipeList().toArray(new IRecipe[1]));
		if (Loader.isModLoaded("avaritia") && SingularityConfig.adjustCatalyst) AvaritiaCompat.messWithCatalyst();
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		SingularityConfig.init(e);
		PROXY.init(e);
		INFO.purge();
	}

	public static class SingularityComparator implements Comparator<Singularity> {

		public static SingularityComparator INSTANCE = new SingularityComparator();

		@Override
		public int compare(Singularity o1, Singularity o2) {
			return IntComparators.NATURAL_COMPARATOR.compare(o1.getID(), o2.getID());
		}
	}
}
