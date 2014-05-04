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
		super(102);
		setHardness(0.3F);
		setStepSound(soundGlassFootstep);
		setUnlocalizedName("thinGlass");
	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
	{
		return false;
	}
	
	@Override
	public IIcon getBlockOverlayTexture()
	{
		return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 0, 7);
	}
	
	@Override
	public IIcon getBlockOverlayTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		return getBlockOverlayTexture();
	}
	
	@Override
	public IIcon getIcon(int side, int meta)
	{
		return _iconPane;
	}
	
	@Override
	public IIcon getSideTextureIndex()
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
