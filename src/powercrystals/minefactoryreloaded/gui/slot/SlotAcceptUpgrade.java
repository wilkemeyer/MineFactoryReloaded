package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class SlotAcceptUpgrade extends Slot
{
	protected TileEntityFactoryInventory _inv;

	public SlotAcceptUpgrade(TileEntityFactoryInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
		_inv = inv;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return _inv.isUsableAugment(stack);
	}
}
