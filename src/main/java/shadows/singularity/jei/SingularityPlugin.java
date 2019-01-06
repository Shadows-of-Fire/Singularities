package shadows.singularity.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import shadows.singularity.Singularities;
import shadows.singularity.recipe.CompressorManager;
import shadows.singularity.recipe.ICompressorRecipe;
import shadows.singularity.recipe.SingularityConfig;

@JEIPlugin
public class SingularityPlugin implements IModPlugin {

	public static final String C_UID = "singularities.compressor";

	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) {
		if (!SingularityConfig.hideCompressor) reg.addRecipeCategories(new CompressorCategory(reg.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void register(IModRegistry reg) {
		if (SingularityConfig.hideCompressor) return;
		reg.handleRecipes(ICompressorRecipe.class, CompressorWrapper::new, C_UID);
		reg.addRecipeCatalyst(new ItemStack(Singularities.COMPRESSOR), C_UID);
		reg.addRecipes(CompressorManager.getValidRecipes(), C_UID);
	}
}
