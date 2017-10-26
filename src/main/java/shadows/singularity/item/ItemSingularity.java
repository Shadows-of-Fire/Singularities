package shadows.singularity.item;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import shadows.singularity.Singularities;

public class ItemSingularity extends Item {

	public ItemSingularity() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		setRegistryName(Singularities.MODID, "singularity");
		this.setUnlocalizedName(Singularities.MODID + ".singularity");
		this.setCreativeTab(Singularities.TAB);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		int i = MathHelper.clamp(stack.getItemDamage(), 0, Singularity.getTotalSingularityCount() - 1);
		return Singularities.proxy.translate(getUnlocalizedName() + ".name", StringUtils.capitalize(Singularity.getByID(i).getName()));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) for (int i = 0; i < Singularity.getTotalSingularityCount(); i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return Singularity.getByID(stack.getMetadata()).getRarity();
	}
}
