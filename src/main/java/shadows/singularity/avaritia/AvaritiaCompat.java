package shadows.singularity.avaritia;

import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;

import codechicken.lib.item.ItemMultiType;
import morph.avaritia.init.ModItems;
import morph.avaritia.item.ItemSingularity;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.CompressorRecipe;
import morph.avaritia.recipe.extreme.ExtremeShapelessRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import shadows.singularity.item.Singularity;
import shadows.singularity.recipe.CompressorManager;
import shadows.singularity.recipe.ICompressorRecipe;

public class AvaritiaCompat {

	public static void copyToAvaritia(ICompressorRecipe recipe) {
		if (AvaritiaRecipeManager.getCompressorRecipeFromInput(recipe.getInput().getMatchingStacks()[0]) != null) return;
		AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getID(), new CompressorRecipe(recipe.getOutputStack(), recipe.getRequiredInputs(), true, Lists.newArrayList(recipe.getInput())).setRegistryName(recipe.getID()));
	}

	public static void copyFromAvaritia(morph.avaritia.recipe.compressor.ICompressorRecipe recipe) {
		if (CompressorManager.searchByStack(recipe.getIngredients().get(0).getMatchingStacks()[0]) != null) return;
		CompressorManager.registerRecipe(new shadows.singularity.recipe.CompressorRecipe(recipe.getRegistryName(), recipe.getIngredients().get(0), recipe.getCost(), recipe.getResult()));
	}

	public static void copyRecipesFromAvaritia() {
		for (morph.avaritia.recipe.compressor.ICompressorRecipe r : AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()) {
			copyFromAvaritia(r);
		}
	}

	public static void messWithCatalyst() {
		IExtremeRecipe catalyst = null;
		for (IExtremeRecipe r : AvaritiaRecipeManager.EXTREME_RECIPES.values()) {
			if (r.getRecipeOutput().isItemEqual(ModItems.infinity_catalyst)) {
				catalyst = r;
				break;
			}
		}

		NonNullList<Ingredient> ing = catalyst.getIngredients();
		ing.replaceAll((i) -> {
			if(i.getMatchingStacks()[0].getItem() instanceof ItemSingularity)
			return fromAvaritiaStack(i.getMatchingStacks()[0]);
			return i;
		});
		
		for(Singularity s : Singularity.getSingularities()) {
			if(!ing.stream().anyMatch((i) -> {return i.apply(s.getStack());} )){
				ing.add(new OreIngredient(s.makeOreName()));
			}
		}

		IExtremeRecipe newCatalyst = new ExtremeShapelessRecipe(ing, catalyst.getRecipeOutput()).setRegistryName(catalyst.getRegistryName());
		AvaritiaRecipeManager.EXTREME_RECIPES.put(newCatalyst.getRegistryName(), newCatalyst);
	}
	
	static Map<Integer, String> names;
	
	private static OreIngredient fromAvaritiaStack(ItemStack stack) {
		if(names == null) names = ReflectionHelper.getPrivateValue(ItemMultiType.class, (ItemMultiType) stack.getItem(), "names");
		String ore = "singularity" + WordUtils.capitalize(names.get(stack.getMetadata()));
		OreDictionary.registerOre(ore, stack);
		return new OreIngredient(ore);
	}
	

}
