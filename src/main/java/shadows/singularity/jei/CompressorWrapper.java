package shadows.singularity.jei;

import java.awt.Color;

import com.google.common.collect.Lists;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import shadows.singularity.Singularities;
import shadows.singularity.recipe.ICompressorRecipe;

public class CompressorWrapper implements IRecipeWrapper {

	protected final ICompressorRecipe rec;

	public CompressorWrapper(ICompressorRecipe recipe) {
		rec = recipe;
	}

	@Override
	public void getIngredients(IIngredients ig) {
		ig.setInputs(ItemStack.class, Lists.newArrayList(rec.getInput().getMatchingStacks()));
		ig.setOutput(ItemStack.class, rec.getOutputStack());
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		Minecraft.getMinecraft().fontRenderer.drawString(I18n.format("jei." + Singularities.MODID + ".needed.name", rec.getRequiredInputs()), 21, 0, Color.BLACK.getRGB());
	}

}
