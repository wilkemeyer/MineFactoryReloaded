package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import powercrystals.minefactoryreloaded.gui.slot.SlotAcceptUpgrade;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class ContainerUpgradable extends ContainerFactoryPowered
{
	private int _slotUpgrade;
	public ContainerUpgradable(TileEntityFactoryPowered te, InventoryPlayer inv)
	{
		this(te, inv, 9);
	}
	
	public ContainerUpgradable(TileEntityFactoryPowered te, InventoryPlayer inv, int slot)
	{
		super(te, inv);
		_slotUpgrade = slot;
	}
	
	@Override
	protected void addSlots()
	{
		super.addSlots();
		
		addSlotToContainer(new SlotAcceptUpgrade(_te, _slotUpgrade, 152, 79));
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 99;
	}
}
