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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import shadows.singularity.recipe.CompressorManager;
import shadows.singularity.recipe.ICompressorRecipe;
import shadows.singularity.recipe.SingularityConfig;

public class TileCompressor extends TileEntity implements ITickable {

	public static final String RECENT_KEY = "recent_eject";

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
				if (ei.getEntityData().getBoolean(RECENT_KEY)) continue;
				matchAndUse(ei.getItem());
			}
		}
		for (int i = 0; i < handler.getSlots(); i++) {
			matchAndUse(handler.getStackInSlot(i));
		}
	}

	public void compressStack(ItemStack stack) {
		int needed = recipe.getRequiredInputs() - counter;
		counter += stack.getCount();
		stack.shrink(needed);
		if (counter >= recipe.getRequiredInputs()) {
			TileEntity e = world.getTileEntity(pos.down());
			if (e == null || !putOutputInHandler(e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP))) spawnOutputAsItem();
			counter = 0;
			recipe = null;
		}
		if (!stack.isEmpty()) matchAndUse(stack);
		markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}

	private void matchAndUse(ItemStack stack) {
		if (recipe != null && recipe.getInput().apply(stack)) compressStack(stack);
		else if (recipe == null) {
			ICompressorRecipe rec = CompressorManager.searchByStack(stack);
			if (rec != null) {
				recipe = rec;
				compressStack(stack);
			}
		}
	}

	private void spawnOutputAsItem() {
		EntityItem i = new EntityItem(world, pos.getX(), pos.getY() + distance, pos.getZ(), recipe.getOutputStack().copy());
		i.setDefaultPickupDelay();
		i.getEntityData().setBoolean(RECENT_KEY, true);
		world.spawnEntity(i);
	}

	private boolean putOutputInHandler(IItemHandler handler) {
		if (handler == null) return false;
		for (int i = 0; i < handler.getSlots(); i++)
			if (handler.insertItem(i, recipe.getOutputStack().copy(), false).isEmpty()) return true;
		return false;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("count", counter);
		tag.setString("recipe", recipe == null ? "null" : recipe.getID().toString());
		return tag;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		counter = tag.getInteger("count");
		recipe = CompressorManager.searchByName(new ResourceLocation(tag.getString("recipe")));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return Block.FULL_BLOCK_AABB.expand(1, 1, 1).offset(pos);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
		if (SingularityConfig.pipeInput && facing != EnumFacing.UP && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return super.hasCapability(cap, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
		if (SingularityConfig.pipeInput && facing != EnumFacing.UP && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handler);
		return super.getCapability(cap, facing);
	}

	private static final class CompressorItemHandler extends ItemStackHandler {

		private final TileCompressor tile;

		public CompressorItemHandler(TileCompressor tile) {
			super(10);
			this.tile = tile;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (isItemValid(slot, stack)) return super.insertItem(slot, stack, simulate);
			return stack;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (tile.recipe != null && tile.recipe.getInput().apply(stack)) return true;
			else return tile.recipe == null && (tile.recipe = CompressorManager.searchByStack(stack)) != null;
		}

	}

}
