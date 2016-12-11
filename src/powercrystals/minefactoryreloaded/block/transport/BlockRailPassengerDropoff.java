package powercrystals.minefactoryreloaded.block.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockRailPassengerDropoff extends BlockFactoryRail {

	public BlockRailPassengerDropoff() {

		super(true, false);
		setUnlocalizedName("mfr.rail.passenger.dropoff");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, BlockPos pos) {

		if (world.isRemote)
			return;

		Class<? extends EntityLivingBase> target = isPowered(world, pos) ? EntityLiving.class : EntityPlayer.class;
		if (!target.isInstance(minecart.getPassengers().get(0)))
			return;

		Entity player = minecart.getPassengers().get(0);
		AxisAlignedBB dropCoords = findSpaceForPlayer(player, pos, world);
		if (dropCoords == null)
			return;

		player.dismountRidingEntity();
		MineFactoryReloadedCore.proxy.movePlayerToCoordinates((EntityLivingBase) player,
			dropCoords.minX + (dropCoords.maxX - dropCoords.minX) / 2,
			dropCoords.minY,
			dropCoords.minZ + (dropCoords.maxZ - dropCoords.minZ) / 2);
	}

	private AxisAlignedBB findSpaceForPlayer(Entity entity, BlockPos pos, World world) {

		final int searchX = MFRConfig.passengerRailSearchMaxHorizontal.getInt();
		final int searchY = MFRConfig.passengerRailSearchMaxVertical.getInt() * 2;

		AxisAlignedBB bb = entity.getEntityBoundingBox();

		final double halfX = (bb.maxX - bb.minX) / 2;
		final double halfZ = (bb.maxZ - bb.minZ) / 2;

		bb.offset(pos.getX() - bb.minX + .5 - halfX, pos.getY() - bb.minY + 0.05, pos.getZ() - bb.minZ + .5 - halfZ);

		bb.offset(0, -(searchY >> 1), 0);
		for (int offsetY = -searchY; offsetY <= searchY; offsetY++) {
			bb.offset(-searchX, 0, 0);
			for (int offsetX = -searchX; offsetX <= searchX; offsetX++) {
				bb.offset(0, 0, -searchX);
				for (int offsetZ = -searchX; offsetZ <= searchX; offsetZ++) {

					if (world.getCollisionBoxes(bb).isEmpty()
							&& !isBadBlockToStandIn(world, bb)
							&& !world.containsAnyLiquid(bb)) {
						int targetX = MathHelper.floor_double(bb.minX + halfX);
						int targetY = MathHelper.floor_double(bb.minY);
						int targetZ = MathHelper.floor_double(bb.minZ + halfZ);

						if (!isBadBlockToStandOn(world, new BlockPos(targetX, targetY - 1, targetZ)))
							return bb;
					}
					bb.offset(0, 0, 1);
				}
				bb.offset(1, 0, -searchX - 1);
			}
			bb.offset(-searchX - 1, 0.5, 0);
		}

		return null;
	}

	private boolean isBadBlockToStandOn(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (world.isAirBlock(pos)
				|| null == block.getCollisionBoundingBox(state, world, pos)) {
			return true;
		}
		return false;
	}

	private boolean isBadBlockToStandIn(World world, AxisAlignedBB bb) {

		int i = MathHelper.floor_double(bb.minX);
		int j = MathHelper.floor_double(bb.maxX + 1.0D);
		int k = MathHelper.floor_double(bb.minY);
		int l = MathHelper.floor_double(bb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(bb.minZ);
		int j1 = MathHelper.floor_double(bb.maxZ + 1.0D);

		if (bb.minX < 0.0D) {
			--i;
		}

		if (bb.minY < 0.0D) {
			--k;
		}

		if (bb.minZ < 0.0D) {
			--i1;
		}

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = k; l1 < l; ++l1) {
				for (int i2 = i1; i2 < j1; ++i2) {
					BlockPos pos = new BlockPos(k1, l1, i2);
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();

					if (block == Blocks.FIRE ||
							state.getMaterial().isLiquid() ||
							block.isBurning(world, pos) ||
							BlockRailBase.isRailBlock(state)) {

						return true;
					}
				}
			}
		}

		return false;
	}

}
