package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityMeatPacker extends TileEntityFactoryPowered implements IFluidHandler
{
	public TileEntityMeatPacker()
	{
		super(Machine.MeatPacker);
		setManageSolids(true);
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
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
		if (drain(2, false, _tanks[0]) == 2)
		{
			if (!incrementWorkDone()) return false;

			if (getWorkDone() >= getWorkMax())
			{
				ItemStack item;
				if (_tanks[0].getFluid().equals(FluidRegistry.getFluidStack("meat", 1)))
				{
					item = new ItemStack(MFRThings.meatIngotRawItem);
				}
				else
				{
					item = new ItemStack(MFRThings.meatNuggetRawItem);
				}

				doDrop(item);

				setWorkDone(0);
			}
			drain(2, true, _tanks[0]);
			return true;
		}
		return false;
	}

	@Override
	public int getWorkMax()
	{
		return 50;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 0;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill)
	{
		if (resource == null || !(resource.isFluidEqual(FluidRegistry.getFluidStack("meat", 1)) ||
				resource.isFluidEqual(FluidRegistry.getFluidStack("pinkslime", 1))))
		{
			return 0;
		}
		else
		{
			if (drain(2, false, _tanks[0]) == 1)
			{
				drain(1, true, _tanks[0]);
			}
			return _tanks[0].fill(resource, doFill);
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
	{
		return null;
	}

	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[]{new FluidTankAdv(4 * BUCKET_VOLUME)};
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
