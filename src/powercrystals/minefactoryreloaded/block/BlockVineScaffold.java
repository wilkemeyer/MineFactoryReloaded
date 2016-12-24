package powercrystals.minefactoryreloaded.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

import javax.annotation.Nullable;

public class BlockVineScaffold extends Block implements IRedNetDecorative {

	private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.125D, 0D, 0.125D, 0.875D, 1D, 0.875D);
			
	private static final EnumFacing[] _attachDirections = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH,
			EnumFacing.EAST, EnumFacing.WEST };
	private static final int _attachDistance = 11;

	public BlockVineScaffold() {

		super(Material.LEAVES);
		setUnlocalizedName("mfr.vinescaffold");
		setSoundType(SoundType.GROUND);
		setHardness(0.1F);
		setTickRandomly(true);
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {

		float shrinkAmount = 1f / 45f;
		if (entity.getEntityBoundingBox().minY >= pos.getY() + (1f - shrinkAmount) ||
				entity.getEntityBoundingBox().maxY <= pos.getY() + shrinkAmount)
			return;
		entity.fallDistance = 0;
		if (entity.isCollidedHorizontally) {
			entity.motionY = 0.2D;
		} else if (entity.isSneaking()) {
			double diff = entity.prevPosY - entity.posY;
			entity.motionY = 0.0D;
		} else {
			entity.motionY = -0.12D;
		}
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return COLLISION_AABB;
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		_sideIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".side");
		_topIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(EnumFacing side, int meta) {

		return side < 2 ? _topIcon : _sideIcon;
	}
*/

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return !state.isOpaqueCube();
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {

		return ColorizerFoliage.getFoliageColorBasic();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, BlockPos pos) {

		int r = 0;
		int g = 0;
		int b = 0;

		for (int zOffset = -1; zOffset <= 1; ++zOffset) {
			for (int xOffset = -1; xOffset <= 1; ++xOffset) {
				int biomeColor = world.getBiome(x + xOffset, z + zOffset).getBiomeFoliageColor(x, y, z);
				r += (biomeColor & 16711680) >> 16;
				g += (biomeColor & 65280) >> 8;
				b += biomeColor & 255;
			}
		}

		return (r / 9 & 255) << 16 | (g / 9 & 255) << 8 | b / 9 & 255;
	}

*/

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float xOffset,
			float yOffset, float zOffset) {

		if (heldItem != null && Block.getBlockFromItem(heldItem.getItem()).equals(this)) {
			for (int i = pos.getY() + 1, e = world.getActualHeight(); i < e; ++i) {
				BlockPos placePos = new BlockPos(pos.getX(), i, pos.getZ());
				Block block = world.getBlockState(placePos).getBlock();
				if (world.isAirBlock(placePos) || block.isReplaceable(world, placePos)) {
					if (!world.isRemote && world.setBlockState(placePos, getDefaultState())) {
						world.playEvent(null, 2001, placePos, Block.getIdFromBlock(this));
						if (!player.capabilities.isCreativeMode) {
							heldItem.stackSize--;
							if (heldItem.stackSize == 0) {
								player.inventory.mainInventory[player.inventory.currentItem] = null;
							}
						}
					}
					return true;
				} else if (!block.equals(this)) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {

		return canBlockStay(world, pos);
	}

	public boolean canBlockStay(World world, BlockPos pos) {

		if (world.isSideSolid(pos.down(), EnumFacing.UP)) {
			return true;
		}
		for (EnumFacing facing : _attachDirections) {
			for (int i = 1; i <= _attachDistance; i++) {
				BlockPos offsetPos = pos.offset(facing, i);
				if (world.getBlockState(offsetPos).getBlock().equals(this)) {
					if (world.isSideSolid(offsetPos.down(), EnumFacing.UP)) {
						return true;
					}
				} else
					break;
			}
		}
		return false;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

		neighborChanged(state, world, pos, null);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

		if (!canBlockStay(world, pos)) {
			int height = world.getActualHeight();
			BlockPos posAbove = pos;
			while((posAbove = posAbove.up()).getY() < height) {
				IBlockState stateAbove = world.getBlockState(posAbove);
				block = stateAbove.getBlock();
				if (!block.equals(this))
					break;
				dropBlockAsItem(world, posAbove, stateAbove, 0);
				world.setBlockState(posAbove, Blocks.AIR.getDefaultState());
				for (EnumFacing facing : _attachDirections) {
					for (int i = 1; i <= _attachDistance; i++) {
						BlockPos posSide = posAbove.offset(facing, i);
						block = world.getBlockState(posSide).getBlock();
						if (block.equals(this)) {
							world.scheduleBlockUpdate(posSide, block, 0, 0);
						} else
							break;
					}
				}
			}
			for (int offset = 1 ; offset < height - pos.getY(); offset++) {
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}
}
