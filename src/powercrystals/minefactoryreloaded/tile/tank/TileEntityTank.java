package powercrystals.minefactoryreloaded.tile.tank;

import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.position.BlockPosition;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import powercrystals.minefactoryreloaded.core.IDelayedValidate;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.net.ConnectionHandler;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public class TileEntityTank extends TileEntityFactory implements ITankContainerBucketable, IDelayedValidate
{
	static int CAPACITY = FluidHelper.BUCKET_VOLUME * 4;
	TankNetwork grid;
	FluidStack fluidForGrid;
	protected byte sides;

	public TileEntityTank()
	{
		super(null);
	}

	@Override
	public void invalidate()
	{
		if (isInvalid())
			return;
		super.invalidate();
		if (worldObj.isRemote)
			return;

		for (ForgeDirection to : ForgeDirection.VALID_DIRECTIONS) {
			if ((sides & (1 << to.ordinal())) == 0)
				continue;
			TileEntityTank tank = BlockPosition.getAdjacentTileEntity(this, to, TileEntityTank.class);
			if (tank != null)
				tank.part(to.getOpposite());
		}
		if (grid != null)
			grid.removeNode(this, false);
	}

	@Override
	public final boolean isNotValid() {
		return isInvalid();
	}

	@Override
	public void firstTick()
	{/*
		for (ForgeDirection to : ForgeDirection.VALID_DIRECTIONS) {
			if (to.offsetY != 0 || !BlockPosition.blockExists(this, to))
				continue;
			TileEntityTank tank = BlockPosition.getAdjacentTileEntity(this, to, TileEntityTank.class);
			if (tank != null && tank.grid != null && FluidHelper.isFluidEqualOrNull(tank.grid.storage.getFluid(), fluidForGrid)) {
				if (tank.grid != null)
					tank.grid.addNode(this);
				if (grid != null)
				{
					tank.join(to.getOpposite());
					join(to);
				}
			}
		}//*/
		if (grid == null)
			grid = new TankNetwork(this);
	}

	@Override
	public void validate()
	{
		super.validate();
		if (worldObj.isRemote)
			return;
		ConnectionHandler.update(this);
	}

	public void join(ForgeDirection from)
	{
		sides |= (1 << from.ordinal());
	}

	public void part(ForgeDirection from)
	{
		sides &= ~(1 << from.ordinal());
	}

	boolean isInterfacing(ForgeDirection to)
	{
		return 0 != (sides & (1 << to.ordinal()));
	}

	int interfaceCount()
	{
		return Integer.bitCount(sides);
	}

	public void remove()
	{
		if (grid != null)
			grid.removeNode(this, true);
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag)
	{
		super.writeItemNBT(tag);
		remove();
		if (fluidForGrid != null)
		{
			tag.setTag("fluid", fluidForGrid.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		fluidForGrid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (grid == null)
			return 0;
		return grid.storage.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (grid == null)
			return null;
		return grid.storage.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if (grid == null)
			return null;
		return grid.storage.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return grid != null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return grid != null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if (grid == null)
			return FluidHelper.NULL_TANK_INFO;
		return new FluidTankInfo[] { grid.storage.getInfo() };
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean allowBucketDrain(ItemStack stack)
	{
		return true;
	}

	@Override
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side, EntityPlayer player, boolean debug)
	{
		if (debug) {
			info.add(new ChatComponentText("Grid: " + grid));
		}
		if (grid == null) {
			info.add(new ChatComponentText("Null Grid!!"));
			if (debug)
				info.add(new ChatComponentText("FluidForGrid: " + fluidForGrid));
			return;
		}
		if (grid.storage.getFluidAmount() == 0)
			info.add(new ChatComponentText(MFRUtil.empty()));
		else
			info.add(new ChatComponentText(MFRUtil.getFluidName(grid.storage.getFluid())));
		info.add(new ChatComponentText((grid.storage.getFluidAmount() / (float)grid.storage.getCapacity() * 100f) + "%"));
		if (debug) {
			info.add(new ChatComponentText(grid.storage.getFluidAmount() + " / " + grid.storage.getCapacity()));
			info.add(new ChatComponentText("Size: " + grid.getSize() + " | Share: " + grid.getNodeShare(this)));
			info.add(new ChatComponentText("FluidForGrid: " + fluidForGrid));
		}
	}
}
