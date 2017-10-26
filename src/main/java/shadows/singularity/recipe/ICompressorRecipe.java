package shadows.singularity.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface ICompressorRecipe {

	public int getRequiredInputs();

	public Ingredient getInput();

	public ItemStack getOutputStack();

	public ResourceLocation getID();

}
