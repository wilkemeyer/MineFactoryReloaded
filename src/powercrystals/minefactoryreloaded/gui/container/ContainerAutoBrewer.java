package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotAcceptInsertable;
import cofh.lib.gui.slot.SlotPotion;
import cofh.lib.gui.slot.SlotPotionIngredient;
import cofh.lib.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class ContainerAutoBrewer extends ContainerFactoryPowered
{
	public ContainerAutoBrewer(TileEntityFactoryPowered te, InventoryPlayer inv)
	{
		super(te, inv);
	}
	
	@Override
	protected void addSlots()
	{
		for(int row = 0; row < 6; row++)
		{
			addSlotToContainer(new SlotPotion(_te, row * 5, 8, 34 + row * 18));
			addSlotToContainer(new SlotFake(_te, row * 5 + 1, 44, 34 + row * 18));
			addSlotToContainer(new SlotPotionIngredient(_te, row * 5 + 2, 80, 34 + row * 18));
			addSlotToContainer(new SlotPotionIngredient(_te, row * 5 + 3, 98, 34 + row * 18));
			addSlotToContainer(new SlotPotionIngredient(_te, row * 5 + 4, 116, 34 + row * 18));
		}
		addSlotToContainer(new SlotRemoveOnly(_te, 30, 8, 142));
		addSlotToContainer(new SlotAcceptInsertable(_te, 31, 146, 142));
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 174;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse)
	{
		boolean successful = false;
		int slotIndex = !reverse ? slotStart : slotRange - 1;
		int iterOrder = !reverse ? 1 : -1;
		int machineEnd = _te.getSizeInventory();

		Slot slot;
		ItemStack existingStack;

		l: if (stack.isStackable())
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
					if (slotIndex < machineEnd && !_te.canInsertItem(slotIndex, stack, -1))
						break l;
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

		l: if (stack.stackSize > 0)
		{
			slotIndex = !reverse ? slotStart : slotRange - 1;

			while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart))
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();

				if (slot.isItemValid(stack) && existingStack == null)
				{
					if (slotIndex < machineEnd && !_te.canInsertItem(slotIndex, stack, -1))
						break l;
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
}
