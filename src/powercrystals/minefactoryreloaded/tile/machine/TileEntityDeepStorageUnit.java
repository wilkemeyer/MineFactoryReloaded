package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiDeepStorageUnit;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerDeepStorageUnit;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityDeepStorageUnit extends TileEntityFactoryInventory implements IDeepStorageUnit
{
	private boolean _ignoreChanges = true;
	private boolean _shouldTick = true;

	private int _storedQuantity;
	private ItemStack _storedItem = null;

	public TileEntityDeepStorageUnit()
	{
		super(Machine.DeepStorageUnit);
		setManageSolids(true);
	}

	@Override
	public void validate()
	{
		super.validate();
		_ignoreChanges = false;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		_ignoreChanges = true;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return slot < 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiDeepStorageUnit(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerDeepStorageUnit getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerDeepStorageUnit(this, inventoryPlayer);
	}

	public int getQuantity()
	{
		return _storedQuantity;
	}

	public int getQuantityAdjusted()
	{
		int quantity = _storedQuantity;

		for(int i = 0; i < getSizeInventory(); i++)
		{
			if(_inventory[i] != null && _storedQuantity == 0)
			{
				quantity += _inventory[i].stackSize;
			}
			else if(_inventory[i] != null && UtilInventory.stacksEqual(_storedItem, _inventory[i]))
			{
				quantity += _inventory[i].stackSize;
			}
		}

		return quantity;
	}

	public void setQuantity(int quantity)
	{
		_storedQuantity = quantity;
	}

	public void clearSlots()
	{
		for(int i = 0; i < getSizeInventory(); i++)
		{
			_inventory[i] = null;
		}
	}

	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.UP;
	}

	@Override
	public boolean hasWorldObj()
	{
		return _shouldTick & worldObj != null;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		_shouldTick = false;

		if(worldObj.isRemote)
			return;

		onFactoryInventoryChanged();
	}

	@Override
	protected void onFactoryInventoryChanged()
	{
		if (_ignoreChanges | worldObj == null || worldObj.isRemote)
			return;

		if((_inventory[2] == null) & _storedItem != null & _storedQuantity == 0)
		{
			_storedItem = null;
		}
		checkInput(0);
		checkInput(1);

		if((_inventory[2] == null) & _storedItem != null)
		{
			_inventory[2] = _storedItem.copy();
			_inventory[2].stackSize = Math.min(_storedQuantity,
					Math.min(_storedItem.getMaxStackSize(), getInventoryStackLimit()));
			_storedQuantity -= _inventory[2].stackSize;
		}
		else if(_inventory[2] != null & _storedQuantity > 0 &&
				_inventory[2].stackSize < _inventory[2].getMaxStackSize() &&
				UtilInventory.stacksEqual(_storedItem, _inventory[2]))
		{
			int amount = Math.min(_inventory[2].getMaxStackSize() - _inventory[2].stackSize, _storedQuantity);
			_inventory[2].stackSize += amount;
			_storedQuantity -= amount;
		}
	}

	private void checkInput(int slot)
	{
		if(_inventory[slot] != null)
		{
			if(_storedQuantity == 0 &&
					(_storedItem == null ||
					UtilInventory.stacksEqual(_inventory[slot], _storedItem)))
			{
				_storedItem = _inventory[slot].copy();
				_storedItem.stackSize = 1;
				_storedQuantity = _inventory[slot].stackSize;
				_inventory[slot] = null;
			}
			else if(UtilInventory.stacksEqual(_inventory[slot], _storedItem) &&
					(getMaxStoredCount() - _storedItem.getMaxStackSize()) - _inventory[slot].stackSize > _storedQuantity)
			{
				_storedQuantity += _inventory[slot].stackSize;
				_inventory[slot] = null;
			}
			// boot improperly typed items from the input slots
			else if (!UtilInventory.stacksEqual(_inventory[slot], _storedItem))
			{
				_inventory[slot] = UtilInventory.dropStack(this, _inventory[slot], this.getDropDirection());
			}
			// internal inventory is full
			else
			{
				_inventory[slot] = UtilInventory.dropStack(this, _inventory[slot], this.getDropDirection());
			}
		}
	}

	@Override
	public int getSizeInventory()
	{
		return 3;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return player.getDistanceSq(xCoord, yCoord, zCoord) <= 64D;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return getSizeInventory();
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		_inventory[i] = itemstack;
		markDirty();
	}

	/*
	 * Should only allow matching items to be inserted in the "in" slots. Nothing goes in the "out" slot.
	 */
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if(slot >= 2) return false;
		ItemStack stored = _storedItem;
		if (stored == null) stored = _inventory[2];
		return stored == null || UtilInventory.stacksEqual(stored, stack);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return canInsertItem(slot, itemstack, -1);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		return true;
	}

	@Override
	public void writeItemNBT(NBTTagCompound nbttagcompound)
	{
		int storedAdd = 0;
		ItemStack o = _inventory[2];
		if (o != null)
		{
			storedAdd = o.stackSize;
			_inventory[2] = null;
		}
		super.writeItemNBT(nbttagcompound);
		_inventory[2] = o;

		if (_storedItem != null)
		{
			nbttagcompound.setTag("storedStack", _storedItem.writeToNBT(new NBTTagCompound()));
			nbttagcompound.setInteger("storedQuantity", _storedQuantity + storedAdd);
		}
		else
			nbttagcompound.setInteger("storedQuantity", 0);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		ItemStack o = _inventory[2];
		_inventory[2] = null;
		super.writeToNBT(nbttagcompound);
		_inventory[2] = o;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		_ignoreChanges = true;
		super.readFromNBT(nbttagcompound);

		_storedQuantity = nbttagcompound.getInteger("storedQuantity");
		_storedItem = null;

		if (nbttagcompound.hasKey("storedStack"))
		{
			_storedItem = ItemStack.
					loadItemStackFromNBT((NBTTagCompound)nbttagcompound.getTag("storedStack"));
		}

		if (_storedItem == null & _storedQuantity > 0)
		{
			_storedQuantity = 0;
		}
		_ignoreChanges = false;
	}

	@Override
	public ItemStack getStoredItemType()
	{
		int quantity = getQuantityAdjusted();
		if((quantity != 0) & _storedItem != null)
		{
			ItemStack stack = _storedItem.copy();
			stack.stackSize = quantity;
			return stack;
		}
		return null;
	}

	@Override
	public void setStoredItemCount(int amount)
	{
		for(int i = 0; i < getSizeInventory(); i++)
		{
			if(UtilInventory.stacksEqual(_inventory[i], _storedItem))
			{
				if(amount == 0)
				{
					_inventory[i] = null;
				}
				else if(amount >= _inventory[i].stackSize)
				{
					amount -= _inventory[i].stackSize;
				}
				else if(amount < _inventory[i].stackSize)
				{
					_inventory[i].stackSize = amount;
					amount = 0;
				}
			}
		}
		_storedQuantity = amount;
		markDirty();
	}

	@Override
	public void setStoredItemType(ItemStack type, int amount)
	{
		clearSlots();
		_storedQuantity = amount;
		_storedItem = null;
		if (type == null)
			return;
		_storedItem = type.copy();
		_storedItem.stackSize = 1;
		markDirty();
	}

	@Override
	public int getMaxStoredCount()
	{
		return Integer.MAX_VALUE;
	}
}
