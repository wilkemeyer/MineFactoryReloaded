package powercrystals.minefactoryreloaded.tile.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityLiquidFabricator extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private int _liquidId;
	private int _liquidFabPerTick;
	
	protected TileEntityLiquidFabricator(int liquidId, int liquidFabPerTick, Machine machine)
	{
		super(machine, machine.getActivationEnergy() * liquidFabPerTick);
		_liquidId = liquidId;
		_liquidFabPerTick = liquidFabPerTick;
	}
	
	@Override
	protected boolean activateMachine()
	{
		if(_liquidId < 0)
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		
		FluidStack fluid = new FluidStack(_liquidId, _liquidFabPerTick);
		
		if (fill(ForgeDirection.UNKNOWN, fluid, false) != _liquidFabPerTick)
			return false;
		
		fill(ForgeDirection.UNKNOWN, fluid, true);
		
		return true;
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
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(FluidContainerRegistry.BUCKET_VOLUME)};
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource != null)
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (_tank.getFluidAmount() == 0 || resource.isFluidEqual(_tank.getFluid()))
					return _tank.fill(resource, doFill);
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
		for (FluidTank _tank : (FluidTank[])getTanks())
			if (_tank.getFluidAmount() > 0)
				return _tank.drain(maxDrain, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null)
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (resource.isFluidEqual(_tank.getFluid()))
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}
}
