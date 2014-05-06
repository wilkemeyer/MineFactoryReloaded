package powercrystals.minefactoryreloaded.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import powercrystals.minefactoryreloaded.item.ItemFactoryBag;

public class BagContainerWrapper implements IInventory
{
	private ItemStack _stack;
	
	public BagContainerWrapper(ItemStack stack)
	{
		_stack = stack;
	}
	
	public ItemStack getStack()
	{
		_stack.getTagCompound().setLong("code", (_stack.hashCode() << 32) | _stack.getTagCompound().hashCode());
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
		if(_stack.getTagCompound().getCompoundTag("slot" + i) == null || _stack.getTagCompound().getCompoundTag("slot" + i).hasNoTags())
		{
			return null;
		}
		else
		{
			return ItemStack.loadItemStackFromNBT(_stack.getTagCompound().getCompoundTag("slot" + i));
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if(_stack.getTagCompound().getCompoundTag("slot" + i) == null || _stack.getTagCompound().getCompoundTag("slot" + i).hasNoTags())
		{
			return null;
		}
		ItemStack s = ItemStack.loadItemStackFromNBT(_stack.getTagCompound().getCompoundTag("slot" + i));
		ItemStack r = s.splitStack(j);
		if(s.stackSize <= 0)
		{
			_stack.getTagCompound().setTag("slot" + i, new NBTTagCompound());
		}
		else
		{
			NBTTagCompound t = new NBTTagCompound();
			s.writeToNBT(t);
			_stack.getTagCompound().setTag("slot" + i, t);
		}
		return r;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		if(itemstack == null)
		{
			_stack.getTagCompound().setTag("slot" + i, new NBTTagCompound());
		}
		else
		{
			NBTTagCompound t = new NBTTagCompound();
			itemstack.writeToNBT(t);
			_stack.getTagCompound().setTag("slot" + i, t);
		}
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
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
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

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return itemstack != null && !(itemstack.getItem() instanceof ItemFactoryBag);
	}
}
