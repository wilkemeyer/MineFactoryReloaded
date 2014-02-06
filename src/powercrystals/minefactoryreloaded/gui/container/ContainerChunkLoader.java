package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class ContainerChunkLoader extends ContainerFactoryPowered
{
	public ContainerChunkLoader(TileEntityFactoryPowered te, InventoryPlayer inv)
	{
		super(te, inv);
	}

	@Override public void addSlots() {}
}
