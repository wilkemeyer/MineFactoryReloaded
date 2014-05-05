package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.gui.slot.SlotPotionIngredient;
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
			addSlotToContainer(new Slot(_te, row * 5, 8, 34 + row * 18));
			addSlotToContainer(new SlotFake(_te, row * 5 + 1, 44, 34 + row * 18));
			addSlotToContainer(new SlotPotionIngredient(_te, row * 5 + 2, 80, 34 + row * 18));
			addSlotToContainer(new SlotPotionIngredient(_te, row * 5 + 3, 98, 34 + row * 18));
			addSlotToContainer(new SlotPotionIngredient(_te, row * 5 + 4, 116, 34 + row * 18));
		}
		addSlotToContainer(new Slot(_te, 30, 8, 142));
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 174;
	}
	
	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse)
	{ // TODO: merge this into ContainerFactoryInventory?
		boolean successful = false;
		int slotIndex = slotStart;
		int maxStack = Math.min(stack.getMaxStackSize(), _te.getInventoryStackLimit());
		int machineEnd = _te.getSizeInventory();
		
		if(reverse)
		{
			slotIndex = slotRange - 1;
		}
		
		Slot slot;
		ItemStack existingStack;
		
		if(stack.isStackable())
		{
			while(stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart))
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();
				
				l: if(slot.isItemValid(stack) && existingStack != null &&
						existingStack.getItem().equals(stack.getItem()) &&
						(!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) &&
						ItemStack.areItemStackTagsEqual(stack, existingStack))
				{
					if (slotIndex < machineEnd && !_te.canInsertItem(slotIndex, stack, -1))
						break l;
					int existingSize = existingStack.stackSize + stack.stackSize;
					
					if(existingSize <= maxStack)
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
				
				if(reverse)
				{
					--slotIndex;
				}
				else
				{
					++slotIndex;
				}
			}
		}
		
		if(stack.stackSize > 0)
		{
			if(reverse)
			{
				slotIndex = slotRange - 1;
			}
			else
			{
				slotIndex = slotStart;
			}
			
			while(!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();
				
				l: if(slot.isItemValid(stack) && existingStack == null)
				{
					if (slotIndex < machineEnd && !_te.canInsertItem(slotIndex, stack, -1))
						break l;
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
					successful = true;
					break;
				}
				
				if(reverse)
				{
					--slotIndex;
				}
				else
				{
					++slotIndex;
				}
			}
		}
		
		return successful;
	}
}
