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
	public BlockRenderLayer getBlockLayer() {
		
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		if (side.getAxis().isHorizontal() && world.getBlockState(pos.offset(side)).getBlock().equals(this)) {
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

