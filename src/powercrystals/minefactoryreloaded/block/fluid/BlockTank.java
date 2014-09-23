package powercrystals.minefactoryreloaded.block.fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.render.IconOverlay;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

public class BlockTank extends BlockFactory
{
	protected IIcon[] icons = new IIcon[3];
	public BlockTank()
	{
		super(0.5f);
		setBlockName("mfr.tank");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		icons[0] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.bottom");
		icons[1] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.top");
		icons[2] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.side");
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side <= 1)
			return icons[side];
		if (meta == 3)
			return new IconOverlay(icons[2], 3, 3, 2, 0);
		return new IconOverlay(icons[2], 3, 3, 0, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityTank();
	}
}
