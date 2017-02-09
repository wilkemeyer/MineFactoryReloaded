package powercrystals.minefactoryreloaded.block.fluid;

import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.IBakeryBlock;
import codechicken.lib.model.blockbakery.ICustomBlockBakery;
import cofh.api.block.IBlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.render.block.BlockTankRenderer;
import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

import java.util.List;

public class BlockTank extends BlockFactory implements IBlockInfo, IBakeryBlock {

	public static final IUnlistedProperty<String> FLUID = new IUnlistedProperty<String>() {

		@Override public String getName() {	return "fluid_rl"; }
		@Override public boolean isValid(String value) { return true; }
		@Override public Class<String> getType() { return String.class;	}
		@Override public String valueToString(String value) { return value;	}
	};

	public static final IUnlistedProperty<Byte> SIDES = new IUnlistedProperty<Byte>() {

		@Override public String getName() {	return "sides"; }
		@Override public boolean isValid(Byte value) { return value >= 0 && value <= 15; }
		@Override public Class<Byte> getType() { return Byte.class; }
		@Override public String valueToString(Byte value) {	return value.toString(); }
	};


	/*
	protected IIcon[] icons = new IIcon[3];
*/

	public BlockTank() {

		super(0.5f);
		setUnlocalizedName("mfr.tank");
		setLightOpacity(1);
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		icons[0] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.bottom");
		icons[1] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.top");
		icons[2] = ir.registerIcon("minefactoryreloaded:machines/tile.mfr.tank.side");
	}

	@Override
	public int getRenderType() {

		return MineFactoryReloadedCore.renderIdFluidTank;
	}
*/

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer.Builder(this).add(FLUID, SIDES).build();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world.getTileEntity(pos));
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		Block block = state.getBlock();
		if (side.getAxis().isHorizontal() && block.equals(this)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getLuminosity(fluid);
		}
		return 0;
	}

/*
	@Override
	public IIcon getIcon(EnumFacing side, int meta) {

		if (side == 0)
			return icons[0];
		if ((side % 6) == 1) {
			return new IconOverlay(icons[1], 2, 2, meta);
		}
		return new IconOverlay(icons[2], 3, 3, meta);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, BlockPos pos, EnumFacing side) {

		int meta = 0;
		if (side == 6 || side == 7) {
			meta = 1;
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityTank) {
				TileEntityTank tank = (TileEntityTank) tile;
				FluidStack fluid = tank.getFluid();
				if (fluid != null)
					return fluid.getFluid().getIcon(fluid);
			}
		}
		if (side > 1 && side < 6) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileEntityTank) {
				TileEntityTank tank = (TileEntityTank) tile;
				int side2 = (((side / 2 - 1) ^ 1) + 1) * 2 + ((side & 1) ^ (side / 2 - 1));
				boolean a = tank.isInterfacing(EnumFacing.getOrientation(side2));
				boolean b = tank.isInterfacing(EnumFacing.getOrientation(side2 ^ 1));
				if (a) {
					if (b)
						meta = 2;
					else
						meta = 1;
				} else if (b)
					meta = 3;
				if (meta != 0)
					meta += 2;
			}
		}
		return getIcon(side, meta);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) tile;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				return fluid.getFluid().getColor(fluid);
		}
		return 0xFFFFFF;
	}
*/

	@Override
	protected boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side, EnumHand hand, ItemStack heldItem) {

		super.activated(world, pos, player, side, hand, heldItem);
		return true;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return new TileEntityTank();
	}

	@Override
	public void getBlockInfo(List<ITextComponent> info, IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, boolean debug) {

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityTank) {
			((TileEntityTank) tile).getTileInfo(info, side, player, debug);
		}
	}

	@Override
	public ICustomBlockBakery getCustomBakery() {
		return BlockTankRenderer.INSTANCE;
	}
}

