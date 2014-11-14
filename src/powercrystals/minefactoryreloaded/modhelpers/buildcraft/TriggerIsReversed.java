package powercrystals.minefactoryreloaded.modhelpers.buildcraft;


public class TriggerIsReversed extends TriggerIsRunning
{
	public TriggerIsReversed()
	{
		super("MFR:ConveyorIsReversed", "Is Reversed", "ConveyorReversed");
	}

	/*@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		if (!canApplyTo(tile))
			return false;
		return ((TileEntityConveyor)tile).getConveyorReversed();
	}//*/
}
