package powercrystals.minefactoryreloaded.tile.base;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityLiquidFabricator extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private int _liquidId;
	private int _liquidFabPerTick;
	
	protected TileEntityLiquidFabricator(int liquidId, int liquidFabPerTick, Machine machine)
	{
		super(machine, machine.getActivationEnergyMJ() * liquidFabPerTick);
		_liquidId = liquidId;
		_liquidFabPerTick = liquidFabPerTick;
		_tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
		setManageFluids(true);
	}
	
	@Override
	protected boolean activateMachine()
	{
		if(_liquidId < 0)
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		
		if(_tank.getFluid() != null && _tank.getCapacity() - _tank.getFluid().amount < _liquidFabPerTick)
		{
			return false;
		}
		
		_tank.fill(new FluidStack(_liquidId, _liquidFabPerTick), true);
		
		return true;
	}
	
	@Override
	public int getEnergyStoredMax()
	{
		return 16000;
	}
	
	@Override
	public int getWorkMax()
	{
		return 0;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}
	
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}
	
	@Override
	public boolean allowBucketDrain()
	{
		return true;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return _tank.drain(maxDrain, doDrain);
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null && resource.isFluidEqual(_tank.getFluid()))
			return _tank.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
}
