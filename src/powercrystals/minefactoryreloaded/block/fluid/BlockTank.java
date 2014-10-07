package powercrystals.minefactoryreloaded.block.fluid;

import cofh.api.block.IBlockInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.render.IconOverlay;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

public class BlockTank extends BlockFactory implements IBlockInfo
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
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdFluidTank;
	}

	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank)tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getLuminosity(fluid);
		}
		return 0;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side <= 1)
			return icons[side];
		if (meta == 3) {
			return new IconOverlay(icons[2], 3, 3, 2, 0);
		}
		return new IconOverlay(icons[2], 3, 3, 0, 0);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		if (side == 3) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityTank) {
				TileEntityTank tank = (TileEntityTank)tile;
				FluidStack fluid = tank.getFluid();
				if (fluid != null)
					return fluid.getFluid().getIcon(fluid);
			}
		}
		return getIcon(side, 3);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank)tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getColor(fluid);
		}
		return 0xFFFFFF;
	}

	@Override
	protected boolean activated(World world, int x, int y, int z, EntityPlayer player, int side)
	{
		super.activated(world, x, y, z, player, side);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityTank();
	}

	@Override
	public void getBlockInfo(IBlockAccess world, int x, int y, int z, ForgeDirection side,
			EntityPlayer player, List<IChatComponent> info, boolean debug)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank)
		{
			((TileEntityTank)tile).getTileInfo(info, side, player, debug);
		}
	}
}
