package shadows.singularity.recipe;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import shadows.singularity.block.TileCompressor;
import shadows.singularity.item.Singularity;

public class SingularityConfig {

	public static Configuration config;

	public static final String[] DEFAULT_SINGULARITY = { "iron, 0xBFBFBF, 0x7F7F7F, 1", "gold, 0xE8EF23, 0xdba213, 1", "lapis, 0x5a82e2, 0x224baf, 1", "redstone, 0xDF0000, 0x900000, 1", "quartz, 0xeeebe6, 0x94867d, 1", "copper, 0xE47200, 0x89511A, 1", "tin, 0xA5C7DE, 0x9BA9B2, 1", "lead, 0x444072, 0x3E3D4E, 1", "silver, 0xF9F9F9, 0xD5D5D5, 1", "nickel, 0xDEE187, 0xC4C698, 1" };
	public static final String[] DEFAULT_RECIPES = { "iron_singularity, blockIron, 5000, singularities:singularity:0", "gold_singularity, blockGold, 5000, singularities:singularity:1", "lapis_singularity, blockLapis, 5000, singularities:singularity:2", "redstone_singularity, blockRedstone, 5000, singularities:singularity:3", "quartz_singularity, blockQuartz, 5000, singularities:singularity:4", "copper_singularity, blockCopper, 5000, singularities:singularity:5", "tin_singularity, blockTin, 5000, singularities:singularity:6", "lead_singularity, blockLead, 5000, singularities:singularity:7", "silver_singularity, blockSilver, 5000, singularities:singularity:8", "nickel_singularity, blockNickel, 5000, singularities:singularity:9", };

	public static void preInit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		String[] singularities = config.getStringList("Singularities", "general", DEFAULT_SINGULARITY, "A list of singularity definitions.  Order is name, color1, color2, rarity.  String, integer, integer, integer.  Rarity must be between 0 and 3 inclusive.");

		for (String s : singularities)
			Singularity.register(Singularity.fromString(s));
		
		TileCompressor.distance = config.getFloat("Compressor Spawn Distance", "general", 1.5F, 1, 10, "How high above the compressor singularities will spawn when finished crafting.");
		
		if (config.hasChanged()) config.save();
	}

	public static void init(FMLInitializationEvent e) {
		config.load();

		String[] recipes = config.getStringList("Recipes", "general", DEFAULT_RECIPES, "A list of compressor recipe definitions.  Order is name, input, number of inputs, output.  Input may also be an itemstack, provided examples use oredict.");

		for (String s : recipes)
			CompressorManager.registerRecipe(CompressorRecipe.fromString(s));

		if (config.hasChanged()) config.save();

	}

}
