package powercrystals.minefactoryreloaded.block.transport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRailPassengerDropoff extends BlockFactoryRail {

	public BlockRailPassengerDropoff() {

		super(true, false);
		setUnlocalizedName("mfr.rail.passenger.dropoff");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart minecart, BlockPos pos) {

		if (world.isRemote || minecart.getPassengers().size() < 1)
			return;

		Class<? extends EntityLivingBase> target = isPowered(world, pos) ? EntityLiving.class : EntityPlayer.class;
		if (!target.isInstance(minecart.getPassengers().get(0)))
			return;

		Entity entity = minecart.getPassengers().get(0);
		entity.dismountRidingEntity();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		registerRailModel(this, "passenger_dropoff");
	}
}
