package powercrystals.minefactoryreloaded.tile.machine;

import cofh.api.item.IAugmentItem;
import cofh.core.util.fluid.FluidTankCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.FluidFillingManager;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradeable;
import powercrystals.minefactoryreloaded.gui.container.ContainerFountain;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFountain extends TileEntityFactoryPowered implements ITankContainerBucketable {

	private IHarvestManager _fillingManager;
	private boolean _reverse;

	public TileEntityFountain() {

		super(Machine.Fountain);
		createHAM(this, 0, 0, 0, false);
		_areaManager.setOverrideDirection(EnumFacing.UP);
		_areaManager.setUpgradeVertical(true);
		_reverse = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiUpgradeable(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFountain getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFountain(this, inventoryPlayer);
	}

	@Override
	protected boolean activateMachine() {

		int idleTicks = 5;
		l: {
			if (_reverse ? _tanks[0].getSpace() >= BUCKET_VOLUME :
					(_tanks[0].getFluidAmount() >= BUCKET_VOLUME &&
					_tanks[0].getFluid().getFluid().canBePlacedInWorld())) {
				BlockPos fillPos = pos.up();
				if (_fillingManager != null) {
					if (_fillingManager.getIsDone())
						onFactoryInventoryChanged();
					fillPos = _fillingManager.getNextBlock();
					_fillingManager.moveNext();
				}
				if (!worldObj.isBlockLoaded(fillPos)) break l;

				IBlockState state = worldObj.getBlockState(fillPos);
				Block block = state.getBlock();
				if (_reverse) {
					idleTicks = 10;
					l2: if (block != null && state.getMaterial().isLiquid())
						if (block instanceof IFluidBlock) {
							IFluidBlock fluidBlock = ((IFluidBlock) block);
							if (!fluidBlock.canDrain(worldObj, fillPos))
								break l;
							FluidStack fluid = fluidBlock.drain(worldObj, fillPos, false);
							int amt = _tanks[0].fill(fluid, false);
							if (amt != fluid.amount) break l2;
							_tanks[0].fill(fluidBlock.drain(worldObj, fillPos, true), true);
							setIdleTicks(5);
							return true;
						}
						else if (block instanceof BlockLiquid) {
							if (state.getValue(BlockLiquid.LEVEL) != 0)
								break l;
							boolean drained = false;
							if (block.equals(Blocks.WATER) || block.equals(Blocks.FLOWING_WATER)) {
								if (_tanks[0].fill(new FluidStack(FluidRegistry.WATER, BUCKET_VOLUME), true) != 0)
									drained = true;
							} else if (block.equals(Blocks.LAVA) || block.equals(Blocks.FLOWING_LAVA))
								if (_tanks[0].fill(new FluidStack(FluidRegistry.LAVA, BUCKET_VOLUME), true) != 0)
									drained = true;
							if (drained) {
								worldObj.setBlockToAir(fillPos);
								setIdleTicks(5);
								return true;
							}
						}
				}
				else if (block == null || block.isReplaceable(worldObj, fillPos)) {
					if (block != null && state.getMaterial().isLiquid())
						if (block instanceof BlockFluidClassic) {
							if (((BlockFluidClassic) block).isSourceBlock(worldObj, fillPos))
								break l;
						}
						else if (block instanceof BlockLiquid) {
							if (state.getValue(BlockLiquid.LEVEL) == 0)
								break l;
						}
					if (worldObj.setBlockState(fillPos, getFlowingState(_tanks[0].getFluid()), 11)) {// TODO: when forge supports NBT fluid blocks, adapt this
						drain(BUCKET_VOLUME, true, _tanks[0]);
						setIdleTicks(1);
						return true;
					}
				}
			}
			if (_fillingManager != null)
				_fillingManager.free();
			idleTicks = getIdleTicksMax();
		}
		setIdleTicks(idleTicks);
		return false;
	}

	private IBlockState getFlowingState(FluidStack fluid) {
		
		if (fluid.getFluid() == FluidRegistry.LAVA)
			return Blocks.FLOWING_LAVA.getDefaultState();
		else if (fluid.getFluid() == FluidRegistry.WATER)
			return Blocks.FLOWING_WATER.getDefaultState();
		
		return fluid.getFluid().getBlock().getDefaultState();
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(BUCKET_VOLUME * 32) };
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		_reverse = false;
		if (isUsableAugment(_inventory[0])) {
			IAugmentItem upgrade = (IAugmentItem) _inventory[0].getItem();
			int r = upgrade.getAugmentLevel(_inventory[0], "radius");
			if (r > 0) {
				_areaManager.setUpgradeLevel(r);
				Area area = new Area(pos.up(), r, 0, r * 2);
				if (_fillingManager == null)
					_fillingManager = new FluidFillingManager(worldObj, area);
				else
					_fillingManager.reset(worldObj, area, null, null);
			} else if (r < 0) {
				_reverse = true;
				r = -r;
				if (r > 1) {
					_areaManager.setUpgradeLevel(r - 1);
					Area area = new Area(pos.up(), r, 0, r * 2);
					if (_fillingManager == null)
						_fillingManager = new FluidFillingManager(worldObj, area);
					else
						_fillingManager.reset(worldObj, area, null, null);
				}
			} else {
				_fillingManager = null;
				_areaManager.setUpgradeLevel(0);
			}
		} else {
			_fillingManager = null;
			_areaManager.setUpgradeLevel(0);
		}
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		return fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

		return drain(resource, doDrain);
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack) {

		return true;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack) {

		return true;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {

		return true;
	}

	@Override
	public int getSizeInventory() {

		return 1;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 20;
	}

	@Override
	public int getUpgradeSlot() {

		return 0;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		if (stack != null) {
			if (slot == 0) {
				return isUsableAugment(stack);
			}
		}
		return false;
	}
}
