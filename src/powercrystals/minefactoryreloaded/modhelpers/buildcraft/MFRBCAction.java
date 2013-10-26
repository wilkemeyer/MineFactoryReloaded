package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

import buildcraft.api.gates.IAction;

public abstract class MFRBCAction implements IAction
{
	@SideOnly(Side.CLIENT)
	protected Icon _icon;
	protected String _iconName; 
	protected String _tag;
	protected String _desc;
	
	protected MFRBCAction(String tag, String desc, String icon)
	{
		_tag = tag;
		_desc = desc;
		_iconName = "minefactoryreloaded:" + icon;
	}
	
	@Override
	public int getLegacyId()
	{
		return -1;
	}

	@Override
	public String getUniqueTag()
	{
		return _tag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon()
	{
		return _icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		if (_iconName != null)
			_icon = iconRegister.registerIcon(_iconName);
	}

	@Override
	public boolean hasParameter()
	{
		return false;
	}

	@Override
	public String getDescription()
	{
		return _desc;
	}
	
	public abstract boolean canApplyTo(TileEntity tile);
}
