package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotAcceptValid;
import cofh.lib.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;

import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter;

public class ContainerAutoDisenchanter extends ContainerFactoryPowered {

	public static ResourceLocation background;
	private TileEntityAutoDisenchanter _disenchanter;

	public ContainerAutoDisenchanter(TileEntityAutoDisenchanter disenchanter, InventoryPlayer inv)
	{
		super(disenchanter, inv);
		_disenchanter = disenchanter;
	}

	@Override
	protected void addSlots()
	{
		addSlotToContainer(new SlotAcceptValid(_te, 0, 8, 18));
		addSlotToContainer(new SlotAcceptValid(_te, 1, 26, 18));

		addSlotToContainer(new SlotRemoveOnly(_te, 4, 8, 37));

		addSlotToContainer(new SlotRemoveOnly(_te, 2, 8, 56));
		addSlotToContainer(new SlotRemoveOnly(_te, 3, 26, 56));

		getSlot(1).setBackgroundLocation(background);
		getSlot(4).setBackgroundLocation(background);
		// getSlot is for the slot id (order it was added) not the slot index
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).sendProgressBarUpdate(this, 100, _disenchanter.getRepeatDisenchant() ? 1 : 0);
		}
	}

	@Override
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		if(var == 100) _disenchanter.setRepeatDisenchant(value == 1 ? true : false);
	}
}
