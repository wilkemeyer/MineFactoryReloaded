package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.gui.slot.SlotRemoveOnly;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityDeepStorageUnit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerDeepStorageUnit extends ContainerFactoryInventory
{
	private TileEntityDeepStorageUnit _dsu;
	private int _tempQuantity;
	
	public ContainerDeepStorageUnit(TileEntityDeepStorageUnit dsu, InventoryPlayer inventoryPlayer)
	{
		super(dsu, inventoryPlayer);
		_dsu = dsu;
	}
	
	@Override
	protected void addSlots()
	{
		addSlotToContainer(new Slot(_te, 0, 134, 16));
		addSlotToContainer(new Slot(_te, 1, 152, 16));
		addSlotToContainer(new SlotRemoveOnly(_te, 2, 152, 49));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		
		if(slotObject != null && slotObject.getHasStack())
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			if(slot < 3)
			{
				if(!mergeItemStack(stackInSlot, 3, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(!mergeItemStack(stackInSlot, 0, 2, false))
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
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < crafters.size(); i++)
		{
			
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 200, _dsu.getQuantity());
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 201, _dsu.getQuantity() >> 16);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if(var == 200) _tempQuantity = upcastShort(value);
		if(var == 201) _dsu.setQuantity(_tempQuantity | (value << 16));
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 124;
	}
	
	private int upcastShort(int value)
	{
		if(value < 0) value += 65536;
		return value;
	}
}
