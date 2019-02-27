package shadows.singularity;

import java.util.Comparator;

import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import shadows.placebo.util.CreativeTab;
import shadows.placebo.util.PlaceboUtil;
import shadows.placebo.util.RecipeHelper;
import shadows.placebo.util.StackPrimer;
import shadows.singularity.avaritia.AvaritiaCompat;
import shadows.singularity.block.BlockCompressor;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.ItemSingularity;
import shadows.singularity.item.Singularity;
import shadows.singularity.recipe.SingularityConfig;

@Mod(modid = Singularities.MODID, name = Singularities.MODNAME, version = Singularities.VERSION, dependencies = Singularities.DEPS)
public class Singularities extends RecipeHelper {

	public static final String MODID = "singularities";
	public static final String MODNAME = "Singularities";
	public static final String VERSION = "3.0.0";
	public static final String DEPS = "required-after:forge@[14.23.5.2768,);required-after:placebo@[2.0.0,);before:crafttweaker;after:avaritia";
	public static final CreativeTabs TAB = new CreativeTab(MODID, new StackPrimer(MODID + ":singularity"));

	@ObjectHolder("singularities:singularity")
	public static final Item SINGULARITY = new ItemSingularity();

	@ObjectHolder("singularities:compressor")
	public static final Block COMPRESSOR = new BlockCompressor();

	public Singularities() {
		super(MODID, MODNAME);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		SingularityConfig.preInit(e);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void block(Register<Block> e) {
		e.getRegistry().register(PlaceboUtil.initBlock(COMPRESSOR, MODID, "compressor", 5, 300));
		GameRegistry.registerTileEntity(TileCompressor.class, COMPRESSOR.getRegistryName());
	}

	@SubscribeEvent
	public void item(Register<Item> e) {
		e.getRegistry().register(PlaceboUtil.initItem(SINGULARITY, MODID, "singularity"));
		Singularity.operateSorted(s -> OreDictionary.registerOre(s.makeOreName(), new ItemStack(SINGULARITY, 1, s.getID())));
	}

	@Override
	public void addRecipes() {
		if (SingularityConfig.hideCompressor) return;
		addShaped(COMPRESSOR, 3, 3, "blockDiamond", Blocks.PISTON, "blockDiamond", Blocks.ENDER_CHEST, Items.NETHER_STAR, Blocks.ENDER_CHEST, "blockDiamond", Blocks.PISTON, "blockDiamond");
		if (Loader.isModLoaded("avaritia") && SingularityConfig.adjustCatalyst) AvaritiaCompat.messWithCatalyst();
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		SingularityConfig.init(e);
	}

	public static class SingularityComparator implements Comparator<Singularity> {

		public static SingularityComparator INSTANCE = new SingularityComparator();

		@Override
		public int compare(Singularity o1, Singularity o2) {
			return IntComparators.NATURAL_COMPARATOR.compare(o1.getID(), o2.getID());
		}
	}
}
