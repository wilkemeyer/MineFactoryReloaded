package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraft.api.gates.ITriggerParameter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TriggerIsBackstuffed extends MFRBCTrigger
{
	public TriggerIsBackstuffed()
	{
		super("MFR:IsBackstuffed", "Has Drops", "MachineHasDrops");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		_icon = iconRegister.registerIcon("buildcraft:triggers/guitriggers_3_4");
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
