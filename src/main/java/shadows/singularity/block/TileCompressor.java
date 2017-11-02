package shadows.singularity.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import shadows.singularity.recipe.CompressorManager;
import shadows.singularity.recipe.ICompressorRecipe;
import shadows.singularity.recipe.SingularityConfig;

public class TileCompressor extends TileEntity implements ITickable {

	public static double distance = 1.5D;

	private ICompressorRecipe recipe;
	private int ticks = 0;
	private int counter = 0;

	private final ItemStackHandler handler;

	public TileCompressor() {
		handler = new CompressorItemHandler(this);
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
		tag.setTag("handler", handler.serializeNBT());
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		counter = tag.getInteger("count");
		recipe = CompressorManager.searchByName(new ResourceLocation(tag.getString("recipe")));
		handler.deserializeNBT(tag.getCompoundTag("handler"));
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
		if (recipe != null) tryIncreaseCount(handler.getStackInSlot(0));
	}

	public void tryIncreaseCount(ItemStack stack) {
		int stacksize = stack.getCount();
		int needed = recipe.getRequiredInputs() - counter;

		if (stacksize - needed < 0) {
			counter += stacksize;
			stack.setCount(0);
		} else if (stacksize - needed >= 0) {
			counter = 0;
			EntityItem i = new EntityItem(world, pos.getX(), pos.getY() + distance, pos.getZ(), recipe.getOutputStack().copy());
			i.setDefaultPickupDelay();
			world.spawnEntity(i);
			recipe = null;

			stack.shrink(needed);
		}

		if (!stack.isEmpty()) {
			ICompressorRecipe rec = CompressorManager.searchByStack(stack);
			if (rec != null && (rec == recipe || recipe == null)) if (recipe == null) recipe = rec;
			if (rec != null) tryIncreaseCount(stack);
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

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
		if (SingularityConfig.pipeInput && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return super.hasCapability(cap, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
		if (SingularityConfig.pipeInput && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler);
		return super.getCapability(cap, facing);
	}

	private static final class CompressorItemHandler extends ItemStackHandler {

		private final TileCompressor tile;

		public CompressorItemHandler(TileCompressor tile) {
			this.tile = tile;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (tile.recipe != null && tile.recipe.getInput().apply(stack)) return super.insertItem(slot, stack, simulate);
			else if (tile.recipe == null && (tile.recipe = CompressorManager.searchByStack(stack)) != null) return super.insertItem(slot, stack, simulate);
			return stack;
		}

	}

}
