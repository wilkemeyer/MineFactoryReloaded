package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import powercrystals.minefactoryreloaded.core.BlockNBTManager;
import powercrystals.minefactoryreloaded.gui.client.GuiDeepStorageUnit;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerDeepStorageUnit;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityDeepStorageUnit extends TileEntityFactoryInventory implements IDeepStorageUnit
{
	public TileEntityDeepStorageUnit()
	{
		super(Machine.DeepStorageUnit);
	}
	
	private int _storedQuantity;
	private ItemStack _storedItem = null;
	
	private boolean _canUpdate = true;
	
	@Override
	public String getGuiBackground()
	{
		return "dsu.png";
	}
	
	@Override
	public void onBlockBroken()
	{
		if (getQuantityAdjusted() > 0 || isInvNameLocalized())
		{
			BlockNBTManager.setForBlock(this);
		}
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
	public void updateEntity()
	{
		super.updateEntity();
		if(worldObj.isRemote)
		{
			return;
		}
		// TODO: move calculations to onFactoryInventoryChanged
		if((_inventory[2] == null) & _storedItem != null)
		{
			if (_storedQuantity > 0)
			{
				_inventory[2] = _storedItem.copy();
				_inventory[2].stackSize = Math.min(_storedQuantity, _storedItem.getMaxStackSize());
				_storedQuantity -= _inventory[2].stackSize;
			}
			else
				_storedItem = null;
		}
		else if(_inventory[2] != null && _inventory[2].stackSize < _inventory[2].getMaxStackSize() && UtilInventory.stacksEqual(_storedItem, _inventory[2]) && _storedQuantity > 0)
		{
			int amount = Math.min(_inventory[2].getMaxStackSize() - _inventory[2].stackSize, _storedQuantity);
			_inventory[2].stackSize += amount;
			_storedQuantity -= amount;
		}
		checkInput(0);
		checkInput(1);
		
		if(_inventory[0] == null && _inventory[1] == null)
		{
			_canUpdate = false;
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
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		return true;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		int storedAdd = 0;
		ItemStack o = _inventory[2];
		if (o != null)
		{
			storedAdd = o.stackSize;
			_inventory[2] = null;
		}
		super.writeToNBT(nbttagcompound);
		_inventory[2] = o;
		
		if (_storedItem != null)
		{
			nbttagcompound.setCompoundTag("storedStack", _storedItem.writeToNBT(new NBTTagCompound()));
			nbttagcompound.setInteger("storedQuantity", _storedQuantity + storedAdd);
		}
		else
			nbttagcompound.setInteger("storedQuantity", 0);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		
		_storedQuantity = nbttagcompound.getInteger("storedQuantity");
		_storedItem = null;
		
		if (nbttagcompound.hasKey("storedId"))
		{
			if (_storedQuantity != 0)
			{
				int storedID = nbttagcompound.getInteger("storedId");
				if (storedID < Item.itemsList.length && Item.itemsList[storedID] != null)
				{
					_storedItem = new ItemStack(storedID, 1, nbttagcompound.getInteger("storedMeta"));
				}
			}
			else if (_inventory[2] != null)
			{
				_storedItem = _inventory[2].copy();
				_storedItem.stackSize = 1;
			}
			nbttagcompound.removeTag("storedId");
			nbttagcompound.removeTag("storedMeta");
		}
		else if (nbttagcompound.hasKey("storedStack"))
		{
			_storedItem = ItemStack.
					loadItemStackFromNBT((NBTTagCompound)nbttagcompound.getTag("storedStack"));
		}
		
		if (_storedItem == null & _storedQuantity > 0)
		{
			_storedQuantity = 0;
		}
	}
	
	@Override
	public ItemStack getStoredItemType()
	{
		if(_storedQuantity > 0 & _storedItem != null)
		{
			ItemStack stack = _storedItem.copy();
			stack.stackSize = getQuantityAdjusted();
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
		_canUpdate = true;
	}
	
	@Override
	public void setStoredItemType(ItemStack type, int amount)
	{
		clearSlots();
		_storedQuantity = amount;
		_storedItem = null;
		_canUpdate = true;
		if (type == null)
			return;
		_storedItem = type.copy();
		_storedItem.stackSize = 1;
	}
	
	@Override
	public int getMaxStoredCount()
	{
		return Integer.MAX_VALUE;
	}
	
	@Override
	public boolean canUpdate()
	{
		return _canUpdate;
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_canUpdate = true;
	}
}
