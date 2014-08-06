package powercrystals.minefactoryreloaded.gui.container;


import cofh.lib.gui.slot.SlotAcceptValid;
import cofh.lib.gui.slot.SlotViewOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.BagContainerWrapper;

public class ContainerBag extends Container
{
	private BagContainerWrapper _ncw;
	private int _nsi;
	
	public ContainerBag(BagContainerWrapper ncw, InventoryPlayer inv)
	{
		_ncw = ncw;
		_nsi = inv.currentItem;

        for (int i = 0; i < _ncw.getSizeInventory(); ++i)
            this.addSlotToContainer(new SlotAcceptValid(_ncw, i, 44 + i * 18, 20));

		bindPlayerInventory(inv);
	}
	
	public String getName()
	{
		return _ncw.getInventoryName();
	}
	
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++)
		{
			if (i == _nsi)
				addSlotToContainer(new SlotViewOnly(inventoryPlayer, i, 8 + i * 18, 51 + 58));
			else
				addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 51 + 58));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		int machInvSize = _ncw.getSizeInventory();
		
		if(slotObject != null && slotObject.getHasStack())
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			if(slot < machInvSize)
			{
				if(!mergeItemStack(stackInSlot, machInvSize, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(!mergeItemStack(stackInSlot, 0, machInvSize, false))
			{
				return null;
			}
			
			if(stackInSlot.stackSize == 0)
			{
				slotObject.putStack(null);
			}
			else
			{
				slotObject.onSlotChanged();
			}
			
			if(stackInSlot.stackSize == stack.stackSize)
			{
				return null;
			}
			
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		
		return stack;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		if (UtilInventory.stacksEqual(player.inventory.mainInventory[_nsi], _ncw.getStack(), false))
			player.inventory.mainInventory[_nsi] = _ncw.getStack();
		super.onContainerClosed(player);
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse)
	{
		boolean successful = false;
		int slotIndex = !reverse ? slotStart : slotRange - 1;
		int iterOrder = !reverse ? 1 : -1;

		Slot slot;
		ItemStack existingStack;

		if (stack.isStackable())
		{
			while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart))
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();

				if (slot.isItemValid(stack) && existingStack != null &&
						existingStack.getItem().equals(stack.getItem()) &&
						(!stack.getHasSubtypes() ||
								stack.getItemDamage() == existingStack.getItemDamage()) &&
								ItemStack.areItemStackTagsEqual(stack, existingStack))
				{
					int existingSize = existingStack.stackSize + stack.stackSize;
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());

					if (existingSize <= maxStack)
					{
						stack.stackSize = 0;
						existingStack.stackSize = existingSize;
						slot.onSlotChanged();
						successful = true;
					}
					else if (existingStack.stackSize < maxStack)
					{
						stack.stackSize -= maxStack - existingStack.stackSize;
						existingStack.stackSize = maxStack;
						slot.onSlotChanged();
						successful = true;
					}
				}

				slotIndex += iterOrder;
			}
		}

		if (stack.stackSize > 0)
		{
			slotIndex = !reverse ? slotStart : slotRange - 1;

			while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart))
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();

				if (slot.isItemValid(stack) && existingStack == null)
				{
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					existingStack = stack.splitStack(Math.min(stack.stackSize, maxStack));
					slot.putStack(existingStack);
					slot.onSlotChanged();
					successful = true;
				}

				slotIndex += iterOrder;
			}
		}

		return successful;
	}

	@Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
    {
		if (par3 == 2 && par2 == _nsi)
			return null;
		return super.slotClick(par1, par2, par3, par4EntityPlayer);
    }
}
