package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor;

import buildcraft.api.gates.ITriggerParameter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerIsRunning extends MFRBCTrigger
{
	private boolean ownIcon = false;
	public TriggerIsRunning(String id, String desc, String icon)
	{
		super(id, desc, icon);
	}
	
	public TriggerIsRunning()
	{
		this("MFR:ConveyorIsRunning", "Is Running", "ConveyorActive");
		ownIcon = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		if (!ownIcon)
		{
			super.registerIcons(iconRegister);
			return;
		}
		_icon = iconRegister.registerIcon("buildcraft:triggers/action_machinecontrol_on");
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
