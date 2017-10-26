package shadows.singularity.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.singularity.recipe.CompressorManager;
import shadows.singularity.recipe.ICompressorRecipe;

public class TileCompressor extends TileEntity implements ITickable {

	private ICompressorRecipe recipe;
	private int ticks = 0;
	private int counter = 0;

	public TileCompressor() {
	}

	public ICompressorRecipe getRecipe() {
		return recipe;
	}

	public int getCounter() {
		return counter;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("count", counter);
		tag.setString("recipe", recipe == null ? "null" : recipe.getID().toString());
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		counter = tag.getInteger("count");
		recipe = CompressorManager.searchByName(new ResourceLocation(tag.getString("recipe")));
		super.readFromNBT(tag);
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		ticks++;
		if (ticks % 20 == 0) {
			ticks = 0;
			for (EntityItem ei : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(3, 1, 3))) {
				ItemStack stack = ei.getItem();
				ICompressorRecipe rec = CompressorManager.searchByStack(stack);
				if (rec != null && (rec == recipe || recipe == null)) {
					if (recipe == null) recipe = rec;
					tryIncreaseCount(stack);
				}
			}
		}
	}

	private void tryIncreaseCount(ItemStack stack) {
		int stacksize = stack.getCount();
		int needed = recipe.getRequiredInputs() - counter;

		if (stacksize - needed < 0) {
			counter += stacksize;
			stack.setCount(0);
		} else if (stacksize - needed >= 0) {
			counter = 0;
			Block.spawnAsEntity(world, pos.up(), recipe.getOutputStack().copy());
			recipe = null;
			stack.shrink(needed);
		}

		if (!stack.isEmpty()) {
			ICompressorRecipe rec = CompressorManager.searchByStack(stack);
			if (rec != null && (rec == recipe || recipe == null)) if (recipe == null) recipe = rec;
			tryIncreaseCount(stack);
		}

		markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 150, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return Block.FULL_BLOCK_AABB.expand(1, 1, 1).offset(pos);
	}

}
