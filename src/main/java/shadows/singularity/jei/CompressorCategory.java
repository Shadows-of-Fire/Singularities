package shadows.singularity.jei;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import shadows.singularity.Singularities;

public class CompressorCategory implements IRecipeCategory<CompressorWrapper> {

	private final IDrawable background;
	private static final ResourceLocation TEX = new ResourceLocation(Singularities.MODID, "textures/gui/jei_recipe.png");

	public CompressorCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(TEX, 0, 60, 116, 54);
	}

	@Override
	public String getUid() {
		return SingularityPlugin.C_UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei." + Singularities.MODID + ".compressor.name");
	}

	@Override
	public String getModName() {
		return Singularities.MODNAME;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, CompressorWrapper wrapper, IIngredients ingredients) {
		IGuiIngredientGroup<ItemStack> stacks = layout.getIngredientsGroup(ItemStack.class);

		stacks.init(0, false, 76, 18);
		ItemStack output = ingredients.getOutputs(ItemStack.class).get(0).get(0);
		stacks.set(0, output);

		List<ItemStack> inputs = ingredients.getInputs(ItemStack.class).get(0);
		stacks.init(1, true, 18, 18);
		stacks.set(1, inputs);
	}

}
