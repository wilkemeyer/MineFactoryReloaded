package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor;

import buildcraft.api.gates.ITriggerParameter;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TriggerIsRunning extends MFRBCTrigger
{
	public TriggerIsRunning(String id, String desc, String icon)
	{
		super(id, desc, icon);
	}
	
	public TriggerIsRunning()
	{
		this("MFR:ConveyorIsRunning", "Is Running", "buildcraft_trigger_conveyoractive");
	}

	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		if (!canApplyTo(tile))
			return false;
		return ((TileEntityConveyor)tile).getConveyorActive();
	}

	@Override
	public boolean canApplyTo(TileEntity tile)
	{
		return tile instanceof TileEntityConveyor;
	}
}
