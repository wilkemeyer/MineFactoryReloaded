package skyboy.core.container;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import powercrystals.minefactoryreloaded.core.IUseHandler;

import skyboy.core.fluid.LiquidRegistry;

public class DefaultUseHandler implements IUseHandler {
	@Override
	public boolean canUse(ItemStack stack, EntityLivingBase entity) {
		CarbonContainer item = (CarbonContainer)stack.getItem();
		if (stack.getItemDamage() == 0)
			return item.canBeFilledFromWorld;
		return item.canPlaceInWorld;
	}

	@Override
	public ItemStack onTryUse(ItemStack bucket, World world, EntityLivingBase entity) {
		EntityPlayer player = (EntityPlayer)entity;
		if (world.isRemote) return bucket;
		CarbonContainer item = (CarbonContainer)bucket.getItem();
		ItemStack q = new ItemStack(Items.bucket, 1, 0);
		int id = bucket.getItemDamage();
		if (id == 0) {
			if (!item.canBeFilledFromWorld) return bucket;
			MovingObjectPosition objectPosition = item.rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == MovingObjectType.BLOCK) {
				int x = objectPosition.blockX;
				int y = objectPosition.blockY;
				int z = objectPosition.blockZ;
				if (canEntityAct(world, entity, x, y, z, objectPosition.sideHit, bucket, false))
				{
					Block block = world.getBlock(x, y, z);
					if (block instanceof IFluidBlock) {
						FluidStack liquid = ((IFluidBlock)block).drain(world, x, y, z, false);
						if (liquid != null && FluidRegistry.isFluidRegistered(liquid.getFluid())) {
							ItemStack r = FluidContainerRegistry.fillFluidContainer(liquid, bucket);
							if (r != null && FluidContainerRegistry.isFilledContainer(r)) {
								((IFluidBlock)block).drain(world, x, y, z, true);
								return r;
							}
						}
					}
				}
			}
			q = q.getItem().onItemRightClick(q, world, player);
			if (FluidContainerRegistry.isEmptyContainer(q)) return bucket;
			return item.setLiquid(bucket, FluidContainerRegistry.getFluidForFilledItem(q), player);
		}
		if (item.canPlaceInWorld) {
			FluidStack liquid = LiquidRegistry.getLiquid(id);
			if (liquid == null) return bucket;
			MovingObjectPosition objectPosition = item.rayTrace(world, entity, false);
			if (objectPosition != null && objectPosition.typeOfHit == MovingObjectType.BLOCK) {
				int x = objectPosition.blockX;
				int y = objectPosition.blockY;
				int z = objectPosition.blockZ;
				if (canEntityAct(world, entity, x, y, z, objectPosition.sideHit, bucket, true))
				{
					if (world.setBlock(x, y, z, liquid.getFluid().getBlock(), 0, 3))
						return item.getContainerItem(bucket);
				}
			}
		}
		return bucket;
	}

	private boolean canEntityAct(World world, EntityLivingBase entity, int x, int y, int z, int side, ItemStack item, boolean isPlace) {
		EntityPlayer player = (entity instanceof EntityPlayer) ? (EntityPlayer)entity : null;
		return (player == null || (world.canMineBlock(player, x, y, z) &&
						player.canPlayerEdit(x, y, z, side, item))) &&
						(!isPlace || world.isAirBlock(x, y, z) ||
								!world.getBlock(x, y, z).getMaterial().isSolid());
	}

	@SuppressWarnings("unused")
	private boolean canEntityPlace(World world, EntityLiving entity, int x, int y, int z, int side, ItemStack item) { return canEntityAct(world, entity, x, y, z, side, item, true); }
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
		return EnumAction.none;
	}
	@Override
	public ItemStack onUse(ItemStack item, EntityLivingBase entity) {
		return item;
	}
}
