package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.position.BlockPosition;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public class TileEntityTank extends TileEntityFactory implements ITankContainerBucketable
{
	static int CAPACITY = FluidHelper.BUCKET_VOLUME * 4;
	protected FluidTankAdv _tank;
	protected byte sides;

	public TileEntityTank()
	{
		super(null);
		_tank = new FluidTankAdv(CAPACITY);
	}

	@Override
	public void validate()
	{
		if (!isInvalid())
			return;
		super.validate();
		if (worldObj.isRemote)
			return;

		for (ForgeDirection to : ForgeDirection.VALID_DIRECTIONS) {
			if (to.offsetY != 0)
				continue;
			TileEntityTank tank = BlockPosition.getAdjacentTileEntity(this, to, TileEntityTank.class);
			if (FluidHelper.isFluidEqualOrNull(tank._tank.getFluid(), _tank.getFluid())) {
				tank.join(to.getOpposite());
				join(to);
			}
		}
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
			tank.part(to.getOpposite());
		}
	}

	public void join(ForgeDirection from)
	{
		sides |= (1 << from.ordinal());
	}

	public void part(ForgeDirection from)
	{
		sides &= ~(1 << from.ordinal());
	}

	@Override
	public void writeItemNBT(NBTTagCompound tag)
	{
		tag.setTag("tank", _tank.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		_tank.readFromNBT(tag.getCompoundTag("tank"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return _tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return _tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return _tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return new FluidTankInfo[] { _tank.getInfo() };
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
}
