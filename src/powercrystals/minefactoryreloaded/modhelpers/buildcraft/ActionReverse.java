package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor;

import net.minecraft.tileentity.TileEntity;

public class ActionReverse extends MFRBCAction
{
	public ActionReverse()
	{
		super("MFR:ConveyorReverse", "Reverse Conveyor", "ReverseConveyor");
	}

	@Override
	public boolean canApplyTo(TileEntity tile)
	{
		return tile instanceof TileEntityConveyor;
	}
}
