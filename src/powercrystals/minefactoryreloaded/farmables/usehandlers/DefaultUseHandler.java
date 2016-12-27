package powercrystals.minefactoryreloaded.farmables.usehandlers;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import powercrystals.minefactoryreloaded.core.IAdvFluidContainerItem;
import powercrystals.minefactoryreloaded.core.IUseHandler;
import powercrystals.minefactoryreloaded.core.IUseable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;

public class DefaultUseHandler implements IUseHandler {
	@Override
	public boolean canUse(ItemStack bucket, EntityLivingBase entity, EnumHand hand) {
		IAdvFluidContainerItem item = (IAdvFluidContainerItem)bucket.getItem();
		FluidStack liquid = item.getFluid(bucket);
		if (liquid == null || liquid.amount <= 0)
			return item.canBeFilledFromWorld();
		return item.canPlaceInWorld();
	}

	@Override
	public ItemStack onTryUse(ItemStack bucket, World world, EntityLivingBase entity, EnumHand hand) {
		EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
		if (world.isRemote) return bucket;
		Item item = bucket.getItem();
		IAdvFluidContainerItem container = (IAdvFluidContainerItem)item;
		ItemStack q = new ItemStack(Items.BUCKET, 1, 0);
		FluidStack liquid = container.getFluid(bucket);
		if (liquid == null || liquid.amount <= 0) {
			if (!container.canBeFilledFromWorld()) return bucket;
			ItemStack bucket2 = bucket.stackSize > 1 ? bucket.copy() : bucket;
			bucket2.stackSize = 1;
			RayTraceResult objectPosition = ((IUseable)container).rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == Type.BLOCK) {
				BlockPos pos = objectPosition.getBlockPos();
				if (canEntityAct(world, entity, pos, objectPosition.sideHit, bucket, false))
				{
					Block block = world.getBlockState(pos).getBlock();
					if (block instanceof IFluidBlock) {
						liquid = ((IFluidBlock)block).drain(world, pos, false);
						if (liquid != null) {
							if (container.fill(bucket2, liquid, false) == liquid.amount) {
								container.fill(bucket2, ((IFluidBlock)block).drain(world, pos, true), true);
								if (!container.shouldReplaceWhenFilled() || bucket2 != bucket)
									MFRLiquidMover.disposePlayerItem(bucket, bucket2, player,
											true, container.shouldReplaceWhenFilled());
								return bucket;
							}
						}
					}
				}
			}
			if (player == null) return bucket;
			q = q.getItem().onItemRightClick(q, world, player, hand).getResult();
			if (FluidContainerRegistry.isEmptyContainer(q)) return bucket;
			container.fill(bucket2, FluidContainerRegistry.getFluidForFilledItem(q), true);
			if (!container.shouldReplaceWhenFilled() || bucket2 != bucket)
				MFRLiquidMover.disposePlayerItem(bucket, bucket2, player, true, container.shouldReplaceWhenFilled());
			return bucket;
		}
		if (container.canPlaceInWorld()) {
			if (!liquid.getFluid().canBePlacedInWorld()) return bucket;
			Block block = liquid.getFluid().getBlock();
			if (!(block instanceof IFluidBlock)) return bucket;
			RayTraceResult objectPosition = ((IUseable)container).rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == Type.BLOCK) {
				BlockPos pos = objectPosition.getBlockPos();
				if (canEntityAct(world, entity, pos, objectPosition.sideHit, bucket, true))
				{
					if (world.setBlockState(pos, block.getDefaultState(), 3))
					{
						liquid = ((IFluidBlock)block).drain(world, pos, false);
						ItemStack drop = bucket.splitStack(1);
						container.drain(drop, liquid.amount, true);
						if (item.hasContainerItem(drop)) {
							drop = item.getContainerItem(drop);
							if (drop != null && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
								drop = null;
						}
						return drop;
					}
				}
			}
		}
		return bucket;
	}

	private boolean canEntityAct(World world, EntityLivingBase entity, BlockPos pos, EnumFacing side,
			ItemStack item, boolean isPlace) {
		EntityPlayer player = (entity instanceof EntityPlayer) ? (EntityPlayer)entity : null;
		return (player == null || (world.isBlockModifiable(player, pos) &&
						player.canPlayerEdit(pos, side, item))) &&
						(!isPlace || world.isAirBlock(pos) ||
								!world.getBlockState(pos).getMaterial().isSolid());
	}

	@Override
	public int getMaxUseDuration(ItemStack item) {
		return 0;
	}
	@Override
	public boolean isUsable(ItemStack item) {
		return false;
	}
	@Override
	public EnumAction useAction(ItemStack item) {
		return EnumAction.NONE;
	}
	@Override
	public ItemStack onUse(ItemStack item, EntityLivingBase entity, EnumHand hand) {
		return item;
	}
}
