package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import buildcraft.api.gates.ITriggerParameter;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TriggerIsBackstuffed extends MFRBCTrigger
{
	public TriggerIsBackstuffed()
	{
		super("MFR:IsBackstuffed", "Has Drops", "buildcraft_trigger_machinecandrop");
	}

	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		if (!canApplyTo(tile))
			return false;
		return ((TileEntityFactoryInventory)tile).hasDrops();
	}

	@Override
	public boolean canApplyTo(TileEntity tile)
	{
		return (tile instanceof TileEntityFactoryInventory);
	}
}
