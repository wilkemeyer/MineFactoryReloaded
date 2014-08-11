package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter;

public class ContainerAutoDisenchanter extends ContainerFactoryPowered
{
	private TileEntityAutoDisenchanter _disenchanter;

	public ContainerAutoDisenchanter(TileEntityAutoDisenchanter disenchanter, InventoryPlayer inv)
	{
		super(disenchanter, inv);
		_disenchanter = disenchanter;
	}

	@Override
	protected void addSlots()
	{
		addSlotToContainer(new Slot(_te, 0, 8, 18));
		addSlotToContainer(new Slot(_te, 1, 26, 18));

		addSlotToContainer(new SlotRemoveOnly(_te, 4, 8, 37));

		addSlotToContainer(new SlotRemoveOnly(_te, 2, 8, 56));
		addSlotToContainer(new SlotRemoveOnly(_te, 3, 26, 56));
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 100, _disenchanter.getRepeatDisenchant() ? 1 : 0);
		}
	}

	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		if(var == 100) _disenchanter.setRepeatDisenchant(value == 1 ? true : false);
	}
}
