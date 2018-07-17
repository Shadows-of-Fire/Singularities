package shadows.singularity.recipe;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreIngredient;
import shadows.singularity.Singularities;

public class CompressorRecipe implements ICompressorRecipe {

	protected final Ingredient input;
	protected final int numInputs;
	protected final ItemStack output;
	protected final ResourceLocation name;

	public CompressorRecipe(ResourceLocation name, Ingredient input, int numInputs, ItemStack output) {
		Preconditions.checkArgument(input != Ingredient.EMPTY, "pls stop");
		this.input = input;
		this.numInputs = numInputs;
		this.output = output.copy();
		this.name = name;
	}

	@Override
	public int getRequiredInputs() {
		return numInputs;
	}

	@Override
	public Ingredient getInput() {
		return input;
	}

	@Override
	public ItemStack getOutputStack() {
		return output;
	}

	@Override
	public ResourceLocation getID() {
		return name;
	}

	public static CompressorRecipe fromString(String s) {
		String[] split = s.split(", ");
		Preconditions.checkArgument(split.length == 4, "Invalid Compressor Recipe (failed to split on \", \"): " + s);
		ResourceLocation name = split[0].split(":").length == 2 ? new ResourceLocation(split[0]) : new ResourceLocation(Singularities.MODID, split[0]);
		Ingredient input;
		if (split[1].split(":").length == 1) input = new OreIngredient(split[1]);
		else input = Ingredient.fromStacks(parseItemStack(split[1]));
		int numInputs = Integer.parseInt(split[2]);
		ItemStack output = parseItemStack(split[3]);
		return new CompressorRecipe(name, input, numInputs, output);
	}

	private static ItemStack parseItemStack(String s) {
		String[] split = s.split(":");
		return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1])), 1, split.length == 3 ? Integer.parseInt(split[2]) : 0);
	}

}
