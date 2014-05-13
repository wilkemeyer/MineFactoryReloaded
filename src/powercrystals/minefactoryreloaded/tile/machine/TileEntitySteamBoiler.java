package powercrystals.minefactoryreloaded.tile.machine;

import cofh.util.CoreUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;

import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiSteamBoiler;
import powercrystals.minefactoryreloaded.gui.container.ContainerSteamBoiler;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntitySteamBoiler extends TileEntityFactoryInventory
								implements IFluidHandler
{
	public static final int maxTemp = 730;
	private final int _liquidId;
	private int _ticksUntilConsumption = 0;
	private int _ticksSinceLastConsumption = 0;
	private int _totalBurningTime;
	private float _temp;

	public TileEntitySteamBoiler()
	{
		super(Machine.SteamBoiler);
		setManageSolids(true);
		_liquidId = FluidRegistry.getFluid("steam").getID();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiSteamBoiler(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerSteamBoiler getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerSteamBoiler(this, inventoryPlayer);
	}
	
	public float getTemp()
	{
		return _temp;
	}
	
	public int getWorkMax()
	{
		return _ticksUntilConsumption;
	}
	
	public int getWorkDone()
	{
		return _ticksSinceLastConsumption;
	}
	
	@SideOnly(Side.CLIENT)
	public void setTemp(int temp)
	{
		_temp = (temp / 100f);
	}
	
	@SideOnly(Side.CLIENT)
	public void setWorkDone(int a)
	{
		_ticksSinceLastConsumption = a;
	}
	
	@SideOnly(Side.CLIENT)
	public void setWorkMax(int a)
	{
		_ticksUntilConsumption = a;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (!worldObj.isRemote)
		{
			setIsActive(_ticksSinceLastConsumption < _ticksUntilConsumption);

			_ticksSinceLastConsumption = Math.min(_ticksSinceLastConsumption + 1, _ticksUntilConsumption);
			boolean skipConsumption = _ticksSinceLastConsumption < _ticksUntilConsumption;

			if (isActive())
				_totalBurningTime = Math.max(Math.min(_totalBurningTime + 1, 10648), -80);
			else
			{
				_totalBurningTime = Math.max(_totalBurningTime - 16, -10648);
				_ticksUntilConsumption = 0;
			}

			float diff = (float)Math.cbrt(_totalBurningTime) / 22f;

			_temp = Math.max(Math.min(_temp + diff, maxTemp), 0);

			if (_temp > 100)
			{
				int i = drain(_tanks[1], 80, true);
				_tanks[0].fill(new FluidStack(_liquidId, i + i / 2), true);
			}

			if (CoreUtils.isRedstonePowered(this))
				return;
			
			if (_inventory[3] != null)
				for (int i = 0; _inventory[3].stackSize < _inventory[3].getMaxStackSize() && i < 3; ++i)
				{
					UtilInventory.mergeStacks(_inventory[3], _inventory[i]);
					if (_inventory[i] != null && _inventory[i].stackSize == 0)
						_inventory[i] = null;
				}
			else
				for (int i = 0; i < 3; ++i)
					if (_inventory[i] != null)
					{
						_inventory[3] = _inventory[i];
						_inventory[i] = null;
						break;
					}
			
			if (skipConsumption)
				return;

			if (consumeFuel())
				_ticksSinceLastConsumption = 0;
		}
	}

	protected boolean consumeFuel()
	{
		if (_inventory[3] == null)
			return false;

		int burnTime = TileEntityFurnace.getItemBurnTime(_inventory[3]);
		if (burnTime <= 0)
			return false;

		_ticksUntilConsumption = burnTime / 2;
		decrStackSize(3, 1);

		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setInteger("ticksSinceLastConsumption", _ticksSinceLastConsumption);
		tag.setInteger("ticksUntilConsumption", _ticksUntilConsumption);
		tag.setInteger("buffer", _totalBurningTime);
		tag.setFloat("temp", _temp);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		_ticksSinceLastConsumption = tag.getInteger("ticksSinceLastConsumption");
		_ticksUntilConsumption = tag.getInteger("ticksUntilConsumption");
		_totalBurningTime = tag.getInteger("buffer");
		_temp = tag.getFloat("temp");
	}

	//{ Solids
	@Override
	public int getSizeInventory()
	{
		return 4;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if (stack != null)
			return TileEntityFurnace.getItemBurnTime(stack) > 0;

			return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		return TileEntityFurnace.getItemBurnTime(_inventory[slot]) <= 0;
	}
	//}

	//{ Fluids
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}
	
	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16),
				new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 32)};
	}

	@Override
	public boolean allowBucketFill()
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource != null && resource.getFluid() == FluidRegistry.WATER)
			return _tanks[1].fill(resource, doFill);
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
		FluidTank _tank = _tanks[0];
		if (_tank.getFluidAmount() > 0)
			return _tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null)
		{
			FluidTank _tank = _tanks[0];
			if (resource.isFluidEqual(_tank.getFluid()))
				return _tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return fluid == FluidRegistry.WATER;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	//}
}
