package shadows.singularity.recipe;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import shadows.singularity.avaritia.AvaritiaCompat;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.Singularity;

public class SingularityConfig {

	public static Configuration config;

	public static final String[] DEFAULT_SINGULARITY = { "iron, 0xBFBFBF, 0x7F7F7F, 1, 0", "gold, 0xE8EF23, 0xdba213, 1, 1", "lapis, 0x5a82e2, 0x224baf, 1, 2", "redstone, 0xDF0000, 0x900000, 1, 3", "quartz, 0xeeebe6, 0x94867d, 1, 4", "copper, 0xE47200, 0x89511A, 1, 5", "tin, 0xA5C7DE, 0x9BA9B2, 1, 6", "lead, 0x444072, 0x3E3D4E, 1, 7", "silver, 0xF9F9F9, 0xD5D5D5, 1, 8", "nickel, 0xDEE187, 0xC4C698, 1, 9" };
	public static final String[] DEFAULT_RECIPES = { "iron_singularity, blockIron, 5000, singularities:singularity:0", "gold_singularity, blockGold, 5000, singularities:singularity:1", "lapis_singularity, blockLapis, 5000, singularities:singularity:2", "redstone_singularity, blockRedstone, 5000, singularities:singularity:3", "quartz_singularity, blockQuartz, 5000, singularities:singularity:4", "copper_singularity, blockCopper, 5000, singularities:singularity:5", "tin_singularity, blockTin, 5000, singularities:singularity:6", "lead_singularity, blockLead, 5000, singularities:singularity:7", "silver_singularity, blockSilver, 5000, singularities:singularity:8", "nickel_singularity, blockNickel, 5000, singularities:singularity:9", };

	public static boolean pipeInput = false;
	public static boolean hideCompressor = false;

	public static boolean copyToAvaritia = true;
	public static boolean copyFromAvaritia = true;
	public static boolean adjustCatalyst = true;

	private static String[] recipes;

	public static void preInit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		int dataFixMarker = config.getInt("Data Fix Marker", "Data", 0, 0, 1, "Do not edit.");

		String[] singularities = config.getStringList("Singularities", "general", DEFAULT_SINGULARITY, "A list of singularity definitions.  Order is name, color1, color2, rarity, meta.  String, integer, integer, integer, integer.  Rarity must be between 0 and 3 inclusive.");
		recipes = config.getStringList("Recipes", "general", DEFAULT_RECIPES, "A list of compressor recipe definitions.  Order is name, input, number of inputs, output.  Input may also be an itemstack, provided examples use oredict.");

		if (dataFixMarker == 0) {
			singularities = Singularity.updateToV220(singularities);
			Property p = config.get("general", "Singularities", DEFAULT_SINGULARITY, "A list of singularity definitions.  Order is name, color1, color2, rarity.  String, integer, integer, integer.  Rarity must be between 0 and 3 inclusive.");
			p.setValues(singularities);
			Property dfm = config.get("Data", "Data Fix Marker", 0, "Do not edit.");
			dfm.set(1);
		}

		for (String s : singularities)
			Singularity.register(Singularity.fromString(s));

		pipeInput = config.getBoolean("Pipe Input", "general", false, "If compressors will accept input via pipes");
		hideCompressor = config.getBoolean("Hide Compressor", "general", false, "If compressors are uncraftable and hidden");

		copyToAvaritia = config.getBoolean("Copy to Avaritia", "avaritia", true, "If recipes are copied to the avaritia compressor.  This will not make recipes if a recipe is found for the input.");
		copyFromAvaritia = config.getBoolean("Copy from Avaritia", "avaritia", true, "If recipes are copied from the avaritia compressor.  This will not make recipes if a recipe is found for the input.");
		adjustCatalyst = config.getBoolean("Adjust Infintiy Catalyst", "avaritia", true, "If Singularities modifies the Infinity Catalyst recipe to use new singularities.");

		TileCompressor.distance = config.getFloat("Compressor Spawn Distance", "general", 1.5F, 1, 10, "How high above the compressor singularities will spawn when finished crafting.");

		if (config.hasChanged()) config.save();
	}

	public static void init(FMLInitializationEvent e) {
		for (String s : recipes)
			CompressorManager.registerRecipe(CompressorRecipe.fromString(s));
		if (Loader.isModLoaded("avaritia")) {
			if (copyToAvaritia) for (ICompressorRecipe r : CompressorManager.getValidRecipes())
				AvaritiaCompat.copyToAvaritia(r);
			if (copyFromAvaritia) AvaritiaCompat.copyRecipesFromAvaritia();
		}

	}

}
