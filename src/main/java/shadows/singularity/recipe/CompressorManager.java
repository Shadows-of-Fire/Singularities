package shadows.singularity.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CompressorManager {

	private static final Map<ResourceLocation, ICompressorRecipe> RECIPES = new HashMap<>();

	public static void registerRecipe(ICompressorRecipe recipe) {
		if (RECIPES.get(recipe.getID()) == null) RECIPES.put(recipe.getID(), recipe);
		else throw new IllegalArgumentException(String.format("This recipe (ID: %s) is already in the registry!", recipe.getID().toString()));
	}

	public static ICompressorRecipe searchByStack(ItemStack input) {
		for (ICompressorRecipe r : RECIPES.values()) {
			if (r.getInput().apply(input)) return r;
		}
		return null;
	}

	public static ICompressorRecipe searchByName(ResourceLocation name) {
		return RECIPES.get(name);
	}

	public static Collection<ICompressorRecipe> getValidRecipes() {
		return RECIPES.values().stream().filter((r) -> r.getInput().getMatchingStacks().length > 0).collect(Collectors.toList());
	}
}
