package shadows.singularity.item;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import shadows.placebo.Placebo;
import shadows.singularity.Singularities;

public class ItemSingularity extends Item {

	public ItemSingularity() {
		setHasSubtypes(true);
		setCreativeTab(Singularities.TAB);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return Placebo.PROXY.translate(getTranslationKey() + ".name", WordUtils.capitalize(Singularity.getByID(stack.getMetadata()).getName()));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) Singularity.operateSorted(s -> list.add(new ItemStack(this, 1, s.getID())));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return Singularity.getByID(stack.getMetadata()).getRarity();
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return Singularity.getByID(stack.getMetadata()).hasEffect();
	}
}
