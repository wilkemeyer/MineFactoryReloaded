package powercrystals.minefactoryreloaded.block.transport;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockRailPassengerPickup extends BlockFactoryRail {

	public BlockRailPassengerPickup() {

		super(true, false);
		setUnlocalizedName("mfr.rail.passenger.pickup");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, BlockPos pos) {

		if (world.isRemote | minecart.isBeingRidden() || !minecart.canBeRidden())
			return;

		int searchX = MFRConfig.passengerRailSearchMaxHorizontal.getInt();
		int searchY = MFRConfig.passengerRailSearchMaxVertical.getInt();
		AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - searchX, pos.getY() - searchY, pos.getZ() - searchX,
			pos.getX() + searchX + 1, pos.getY() + searchY + 1, pos.getZ() + searchX + 1);

		Class<? extends EntityLivingBase> target = isPowered(world, pos) ? EntityLiving.class : EntityPlayer.class;
		List<? extends EntityLivingBase> entities = world.getEntitiesWithinAABB(target, bb);

		for (EntityLivingBase o : entities)
			if (!o.isDead & !o.isRiding() && o.getHealth() > 0) {
				o.startRiding(minecart);
				return;
			}
	}

}
