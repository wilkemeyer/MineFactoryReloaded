package powercrystals.minefactoryreloaded.block;

import powercrystals.minefactoryreloaded.render.IconOverlay;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVanillaGlassPane extends BlockFactoryGlassPane
{
	private IIcon _iconPane;
	
	public BlockVanillaGlassPane()
	{
		super(false);
		setUnlocalizedName("thinGlass");
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
	{
		return false;
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return getIcon(side, 1);
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		meta /= 16;
		if (meta > 0)
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 0, 7);
		return _iconPane;
	}
	
	@Override
	public IIcon func_150097_e()
	{
		return _iconSide;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister ir)
	{
		_iconPane = ir.registerIcon("glass");
		_iconSide = ir.registerIcon("glass_pane_top");
	}
}
