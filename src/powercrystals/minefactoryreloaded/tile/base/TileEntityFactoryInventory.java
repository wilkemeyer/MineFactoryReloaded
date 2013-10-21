package powercrystals.minefactoryreloaded.tile.base;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import powercrystals.core.asm.relauncher.Implementable;
import powercrystals.core.position.BlockPosition;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.core.BlockNBTManager;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.setup.Machine;
import buildcraft.api.gates.IAction;

@Implementable("buildcraft.core.IMachine")
public abstract class TileEntityFactoryInventory extends TileEntityFactory implements ISidedInventory
{
	protected String _invName;
	protected boolean _hasInvName = false;
	
	protected List<ItemStack> failedDrops = null;
	private List<ItemStack> missedDrops = new ArrayList<ItemStack>();
	
	protected int _failedDropTicksMax = 20;
	private int _failedDropTicks = 0;
	
	protected FluidTank _tank;
	
	protected TileEntityFactoryInventory(Machine machine)
	{
		super(machine);
		_inventory = new ItemStack[getSizeInventory()];
	}
	
	@Override
	public String getInvName()
	{
		return _hasInvName ? _invName : StatCollector.
				translateToLocal(_machine.getInternalName() + ".name");
	}
	
	@Override
	public boolean isInvNameLocalized()
	{
		return _hasInvName;
	}
	
	public void setInvName(String name)
	{
		this._invName = name;
		this._hasInvName = name != null && name.length() > 0;
	}
	
	public void onBlockBroken()
	{
		if (isInvNameLocalized())
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound name = new NBTTagCompound();
			name.setString("Name", getInvName());
			tag.setTag("display", name);
			BlockNBTManager.setForBlock(new BlockPosition(xCoord, yCoord, zCoord), tag);
		}
	}
	
	public IFluidTank getTank(ForgeDirection direction, FluidStack type)
	{
		return _tank;
	}
	
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		IFluidTank[] tanks = getTanks();
		FluidTankInfo[] r = new FluidTankInfo[tanks.length];
		for (int i = tanks.length; i --> 0; )
			r[i] = tanks[i].getInfo();
		return r;
	}
	
	public IFluidTank[] getTanks()
	{
		if (_tank != null)
			return new IFluidTank[] {_tank};
		return new IFluidTank[] {};
	}
	
	protected boolean shouldPumpLiquid()
	{
		return false;
	}
	
	public boolean allowBucketFill()
	{
		return false;
	}
	
	public boolean allowBucketDrain()
	{
		return false;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!worldObj.isRemote && shouldPumpLiquid())
		{
			for (IFluidTank tank : getTanks())
				MFRLiquidMover.pumpLiquid(tank, this);
		}
		
		if (failedDrops != null)
		{
			if (_failedDropTicks < _failedDropTicksMax)
			{
				_failedDropTicks++;
				return;
			}
			_failedDropTicks = 0;
			if (!doDrop(failedDrops))
			{
				return;
			}
			failedDrops = null;
		}
	}

	public boolean doDrop(ItemStack drop)
	{
		drop = UtilInventory.dropStack(this, drop, this.getDropDirection());
		if (drop != null && drop.stackSize > 0)
		{
			if (failedDrops == null)
			{
				failedDrops = new ArrayList<ItemStack>();
			}
			failedDrops.add(drop);
		}
		return true;
	}
	
	public boolean doDrop(List<ItemStack> drops)
	{
		if (drops == null || drops.size() <= 0)
		{
			return true;
		}
		List<ItemStack> missed = missedDrops;
		missed.clear();
		for (int i = drops.size(); i --> 0; )
		{
			ItemStack dropStack = drops.get(i);
			dropStack = UtilInventory.dropStack(this, dropStack, this.getDropDirection());
			if (dropStack != null && dropStack.stackSize > 0)
			{
				missed.add(dropStack);
			}
		}
		
		if (missed.size() != 0)
		{
			if (drops != failedDrops)
			{
				if (failedDrops == null)
				{
					failedDrops = new ArrayList<ItemStack>();
				}
				failedDrops.addAll(missed);
			}
			else
			{
				failedDrops.clear();
				failedDrops.addAll(missed);
			}
			return false;
		}
		
		return true;
	}
	
	public boolean hasDrops()
	{
		return failedDrops != null;
	}
	
	protected ItemStack[] _inventory;
	
	@Override
	public ItemStack getStackInSlot(int i)
	{
		return _inventory[i];
	}
	
	@Override
	public void openChest()
	{
	}
	
	@Override
	public void closeChest()
	{
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int size)
	{
		if(_inventory[slot] != null)
		{
			if(_inventory[slot].stackSize <= size)
			{
				ItemStack itemstack = _inventory[slot];
				_inventory[slot] = null;
				onFactoryInventoryChanged();
				return itemstack;
			}
			ItemStack itemstack1 = _inventory[slot].splitStack(size);
			if(_inventory[slot].stackSize == 0)
			{
				_inventory[slot] = null;
			}
			onFactoryInventoryChanged();
			return itemstack1;
		}
		else
		{
			onFactoryInventoryChanged();
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		_inventory[i] = itemstack;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
		onFactoryInventoryChanged();
	}
	
	@Override
	public void onInventoryChanged()
	{
		super.onInventoryChanged();
		onFactoryInventoryChanged();
	}
	
	protected void onFactoryInventoryChanged()
	{
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
		{
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList nbttaglist = tag.getTagList("Items");
		_inventory = new ItemStack[getSizeInventory()];
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound slot = (NBTTagCompound)nbttaglist.tagAt(i);
			int j = slot.getByte("Slot") & 0xff;
			if(j >= 0 && j < _inventory.length)
			{
				_inventory[j] = ItemStack.loadItemStackFromNBT(slot);
			}
		}
		onFactoryInventoryChanged();

		if (tag.hasKey("mTanks")) {
			IFluidTank[] _tanks = getTanks();
			
			nbttaglist = tag.getTagList("mTanks");
			for(int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
				int j = nbttagcompound1.getByte("Tank") & 0xff;
				if(j >= 0 && j < _tanks.length)
				{
					FluidStack l = FluidStack.loadFluidStackFromNBT(nbttagcompound1);
					if(l != null)
					{
						((FluidTank)_tanks[j]).setFluid(l);
					}
				}
			}
		}
		else
		{
			IFluidTank tank = _tank;
			if (tank != null && tag.hasKey("tankFluidName"))
			{
				int tankAmount = tag.getInteger("tankAmount");
				FluidStack fluid = FluidRegistry.
						getFluidStack(tag.getString("tankFluidName"), tankAmount);
				if (fluid != null)
				{
					if(fluid.amount > tank.getCapacity())
					{
						fluid.amount = tank.getCapacity();
					}

					((FluidTank)tank).setFluid(fluid);
				}
				tag.removeTag("tankFluidName");
				tag.removeTag("tankAmount");
			}
		}
		
		if (tag.hasKey("display"))
		{
			NBTTagCompound display = tag.getCompoundTag("display");
			if (display.hasKey("Name"))
			{
				this.setInvName(display.getString("Name"));
			}
		}

		if (tag.hasKey("DropItems"))
		{
			List<ItemStack> drops = new ArrayList<ItemStack>();
			nbttaglist = tag.getTagList("DropItems");
			for (int i = nbttaglist.tagCount(); i --> 0; )
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
				ItemStack item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (item != null && item.stackSize > 0)
				{
					drops.add(item);
				}
			}
			if (drops.size() != 0)
			{
				failedDrops = drops;
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < _inventory.length; i++)
		{
			if(_inventory[i] != null)
			{
				NBTTagCompound slot = new NBTTagCompound();
				slot.setByte("Slot", (byte)i);
				_inventory[i].writeToNBT(slot);
				nbttaglist.appendTag(slot);
			}
		}
		tag.setTag("Items", nbttaglist);
		
		IFluidTank[] _tanks = getTanks();
		
		NBTTagList tanks = new NBTTagList();
		for(int i = 0, n = _tanks.length; i < n; i++)
		{
			if(_tanks[i].getFluid() != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Tank", (byte)i);
				
				FluidStack l = _tanks[i].getFluid();
				l.writeToNBT(nbttagcompound1);
				tanks.appendTag(nbttagcompound1);
			}
		}
		
		tag.setTag("mTanks", tanks);
		
		if (this.isInvNameLocalized())
		{
			NBTTagCompound display = new NBTTagCompound();
			display.setString("Name", getInvName());
			tag.setCompoundTag("display", display);
		}
		
		if (failedDrops != null)
		{
			nbttaglist = new NBTTagList();
			for (ItemStack item : failedDrops)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				item.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
			tag.setTag("DropItems", nbttaglist);
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int var1)
	{
		return null;
	}
	
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		int start = getStartInventorySide(ForgeDirection.getOrientation(side));
		int size = getSizeInventorySide(ForgeDirection.getOrientation(side));
		
		int[] slots = new int[size];
		for(int i = 0; i < size; i++)
		{
			slots[i] = i + start;
		}
		return slots;
	}
	
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}
	
	public int getSizeInventorySide(ForgeDirection side)
	{
		return getSizeInventory();
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return this.isItemValidForSlot(slot, itemstack);
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return true;
	}

	public float getComparatorOutput(int side)
	{
		int[] slots = getAccessibleSlotsFromSide(side);
		int len = 0;
		float ret = 0;
		for (int i = slots.length; i --> 0; )
		{
			if (canInsertItem(slots[i], null, side))
			{
				ItemStack stack = getStackInSlot(slots[i]);
				if (stack != null)
				{
					float maxStack = Math.min(stack.getMaxStackSize(), getInventoryStackLimit()); 
					ret += Math.max(Math.min(stack.stackSize / maxStack, 1), 0);
				}
				++len;
			}
		}
		return ret / len;
	}
	
	public boolean allowAction(IAction _)
	{
		return false;
	}
}
