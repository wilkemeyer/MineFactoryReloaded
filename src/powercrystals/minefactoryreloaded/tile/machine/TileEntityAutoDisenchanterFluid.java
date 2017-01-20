package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankCore;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;

public class TileEntityAutoDisenchanterFluid extends TileEntityAutoDisenchanter implements ITankContainerBucketable
{
	public TileEntityAutoDisenchanterFluid()
	{
		_tanks[0].setLock(FluidRegistry.getFluid("mob_essence"));
	}

	@Override
	protected boolean incrementWorkDone()
	{
		if (drain(4, false, _tanks[0]) != 4)
			return false;
		drain(4, true, _tanks[0]);
		return super.incrementWorkDone();
	}

	@Override
	public String getGuiBackground()
	{
		if (_machine == null)
			return null;
		return _machine.getName().toLowerCase(Locale.US) + "2";
	}

	@Override
	protected FluidTankCore[] createTanks()
	{
		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill)
	{
		return fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
	{
		return drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
	{
		return drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid)
	{
		return false;
	}
}
