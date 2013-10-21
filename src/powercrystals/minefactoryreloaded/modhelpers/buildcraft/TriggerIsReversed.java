package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import buildcraft.api.gates.ITriggerParameter;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor;

public class TriggerIsReversed extends TriggerIsRunning
{
	public TriggerIsReversed()
	{
		super("MFR:ConveyorIsReversed", "Is Reversed", "buildcraft_trigger_conveyorreversed");
	}

	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		if (!canApplyTo(tile))
			return false;
		return ((TileEntityConveyor)tile).getConveyorReversed();
	}
}
