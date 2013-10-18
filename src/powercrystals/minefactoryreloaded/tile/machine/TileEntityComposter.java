package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityComposter extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	public TileEntityComposter()
	{
		super(Machine.Composter);
		_tank = new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME);
		setManageFluids(true);
		setManageSolids(true);
	}
	
	@Override
	public String getGuiBackground()
	{
		return "composter.png";
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
	
	@Override
	protected boolean activateMachine()
	{
		if(_tank.getFluid() != null && _tank.getFluid().amount >= 20)
		{
			setWorkDone(getWorkDone() + 1);
			
			if(getWorkDone() >= getWorkMax())
			{
				doDrop(new ItemStack(MineFactoryReloadedCore.fertilizerItem));
				setWorkDone(0);
			}
			_tank.drain(20, true);
			return true;
		}
		return false;
	}
	
	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.UP;
	}
	
	@Override
	public int getEnergyStoredMax()
	{
		return 16000;
	}
	
	@Override
	public int getWorkMax()
	{
		return 100;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 1;
	}
	
	@Override
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource == null || !resource.isFluidEqual(FluidRegistry.getFluidStack("sewage", 1)))
		{
			return 0;
		}
		else
		{
			return _tank.fill(resource, doFill);
		}
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return null;
	}
	
	@Override
	public IFluidTank getTank(ForgeDirection direction, FluidStack type)
	{
		if(type != null && type.isFluidEqual(FluidRegistry.getFluidStack("sewage", 1)))
		{
			return _tank;
		}
		return null;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
}
