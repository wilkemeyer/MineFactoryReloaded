package powercrystals.minefactoryreloaded.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.api.IFactoryLaserSource;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.core.GrindingDamage;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;

import javax.annotation.Nullable;

public class BlockFakeLaser extends Block implements IRedNetNoConnection {

	public static final PropertyDirection FACING = BlockDirectional.FACING;
	private static final AxisAlignedBB NO_AABB = new AxisAlignedBB(0D, 0D, 0D, 0D, 0D, 0D);
	
	public static Material laser = new MaterialTransparent(MapColor.AIR);
	private static GrindingDamage laserDamage = new GrindingDamage("mfr.laser");

	public BlockFakeLaser() {

		super(laser);
		setHardness(-1);
		setResistance(Float.POSITIVE_INFINITY);
		setUnlocalizedName("mfr.laserair");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		
		return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		
		return state.getValue(FACING).getIndex();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		
		return NO_AABB;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {

		if (world.isRemote)
			return;

		entity.setFire(15);
		long t = entity.getEntityData().getLong("mfr:laserTime"), t2 = world.getTotalWorldTime();
		t = t2 - t;
		long d = t / 20 >= 5 ? 1 : entity.getEntityData().getLong("mfr:laserDamage") | 1;
		d &= -1L >>> 1L;
		if (t > 10 && entity.attackEntityFrom(laserDamage, d)) {
			entity.getEntityData().setLong("mfr:laserTime", t2 + (d == 1 ? 20 : 0));
			entity.getEntityData().setLong("mfr:laserDamage", d * 2);
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		return 15;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return true;
	}

	@Override
	public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {

		return true;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

		world.scheduleBlockUpdate(pos, this, 1, 1);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

		world.scheduleBlockUpdate(pos, this, 1, 1);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

		if (world.isRemote) return;

		EnumFacing facing = state.getValue(FACING);
		l: if (facing != EnumFacing.DOWN) {
			IBlockState neighborState = world.getBlockState(pos.offset(facing));
			if (neighborState.getBlock().equals(this))
				if (neighborState.getValue(FACING) == facing)
					return;
				else
					break l;
			TileEntity te = world.getTileEntity(pos.offset(facing));
			if (te instanceof IFactoryLaserSource && ((IFactoryLaserSource) te).canFormBeamFrom(facing))
				return;
			state.withProperty(FACING, EnumFacing.DOWN);
		}

		Block upperBlock = world.getBlockState(pos.up()).getBlock();
		if (!upperBlock.equals(this) && !(world.getTileEntity(pos.up()) instanceof TileEntityLaserDrill)) {
			world.setBlockToAir(pos);
			return;
		}

		Block lowerBlock = world.getBlockState(pos.down()).getBlock();
		if ((!lowerBlock.equals(this) || world.getBlockState(pos.down()).getValue(FACING) != EnumFacing.DOWN) &&
				TileEntityLaserDrill.canReplaceBlock(lowerBlock, world, pos.down())) {
			world.setBlockState(pos.down(), this.getDefaultState());
		}
	}
}
