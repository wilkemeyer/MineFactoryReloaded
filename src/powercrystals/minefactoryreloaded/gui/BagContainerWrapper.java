package powercrystals.minefactoryreloaded.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import powercrystals.minefactoryreloaded.item.ItemFactoryBag;

public class BagContainerWrapper implements IInventory
{
	private ItemStack _stack;
	private NBTTagCompound _inventory;
	private ItemStack[] _stacks = new ItemStack[getSizeInventory()];
	
	public BagContainerWrapper(ItemStack stack)
	{
		_stack = stack;
		_inventory = stack.getTagCompound().getCompoundTag("inventory");
		for (int i = _stacks.length; i --> 0; )
			if (_inventory.hasKey("slot" + i))
				_stacks[i] = ItemStack.loadItemStackFromNBT(_inventory.getCompoundTag("slot" + i));
			else
				_stacks[i] = null;
		markDirty();
	}

	@Override
	public void markDirty()
	{
		for (int i = _stacks.length; i --> 0; )
			_inventory.setTag("slot" + i, _stacks[i] == null ? null : _stacks[i].writeToNBT(new NBTTagCompound()));
		_stack.setTagInfo("inventory", _inventory);
	}
	
	public ItemStack getStack()
	{
		markDirty();
		return _stack;
	}

	@Override
	public int getSizeInventory()
	{
		return 5;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		return _stacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		ItemStack s = _stacks[i];
		if (s == null)
			return null;
		ItemStack r = s.splitStack(j);
		if (s.stackSize <= 0)
		{
			_stacks[i] = null;
		}
		return r;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		_stacks[i] = itemstack;
	}

	@Override
	public String getInventoryName()
	{
		return _stack.getDisplayName();
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return itemstack != null && !(itemstack.getItem() instanceof ItemFactoryBag);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return null;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return true;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}
}
