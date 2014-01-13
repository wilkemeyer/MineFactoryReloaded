package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.gui.BagContainerWrapper;
import powercrystals.minefactoryreloaded.gui.slot.SlotViewOnly;

public class ContainerBag extends Container
{
	private BagContainerWrapper _ncw;
	private int _nsi;
	
	public ContainerBag(BagContainerWrapper ncw, InventoryPlayer inv)
	{
		_ncw = ncw;
		_nsi = inv.currentItem;

        for (int i = 0; i < _ncw.getSizeInventory(); ++i)
            this.addSlotToContainer(new Slot(_ncw, i, 44 + i * 18, 20));

		bindPlayerInventory(inv);
	}
	
	public String getName()
	{
		return _ncw.getInvName();
	}
	
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++)
		{
			if (i == _nsi)
				addSlotToContainer(new SlotViewOnly(inventoryPlayer, i, 8 + i * 18, 84 + 58));
			else
				addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 84 + 58));
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
		return null;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		if (UtilInventory.stacksEqual(player.inventory.mainInventory[_nsi], _ncw.getStack(), false))
			player.inventory.mainInventory[_nsi] = _ncw.getStack();
		else
			player.dropPlayerItem(((Slot)inventorySlots.get(0)).getStack());
		super.onContainerClosed(player);
	}
}
