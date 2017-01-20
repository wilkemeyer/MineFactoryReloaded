package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityComposter extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	public TileEntityComposter()
	{
		super(Machine.Composter);
		setManageSolids(true);
		_tanks[0].setLock(FluidRegistry.getFluid("sewage"));
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
		if(drain(20, false, _tanks[0]) == 20)
		{
			if (!incrementWorkDone()) return false;

			if(getWorkDone() >= getWorkMax())
			{
				doDrop(new ItemStack(MFRThings.fertilizerItem));
				setWorkDone(0);
			}
			drain(20, true, _tanks[0]);
			return true;
		}
		return false;
	}

	@Override
	public EnumFacing getDropDirection()
	{
		return EnumFacing.UP;
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
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill)
	{
		return fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
	{
		return drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
	{
		return drain(resource, doDrain);
	}

	@Override
	protected FluidTankCore[] createTanks()
	{
		return new FluidTankCore[]{new FluidTankCore(4 * BUCKET_VOLUME)};
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid)
	{
		return false;
	}
}
