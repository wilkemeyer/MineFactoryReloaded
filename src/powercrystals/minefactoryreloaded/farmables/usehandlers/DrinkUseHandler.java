package powercrystals.minefactoryreloaded.farmables.usehandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.IUseHandler;

public class DrinkUseHandler implements IUseHandler {
	@Override
	public boolean canUse(ItemStack item, EntityLivingBase entity, EnumHand hand) {
		return entity instanceof EntityPlayer && isUsable(item);
	}

	@Override
	public ItemStack onTryUse(ItemStack item, World world, EntityLivingBase entity, EnumHand hand) {
		if (canUse(item, entity, hand))
			entity.setActiveHand(hand);
		return item;
	}

	@Override
	public int getMaxUseDuration(ItemStack item) {
		return 32;
	}

	@Override
	public boolean isUsable(ItemStack item) {
		return item.stackSize == 1 && isDrinkableLiquid(getFluidName(item));
	}

	@Override
	public EnumAction useAction(ItemStack item) {
		return isUsable(item) ? EnumAction.DRINK : EnumAction.NONE;
	}

	@Override
	public ItemStack onUse(ItemStack item, EntityLivingBase entity, EnumHand hand) {
		String liquid = getFluidName(item);
		ItemStack r = item;
		if (item.stackSize == 1 && liquid != null &&
				entity instanceof EntityPlayer && isDrinkableLiquid(liquid)) {
			EntityPlayer player = (EntityPlayer)entity;
			if (!player.capabilities.isCreativeMode) {
				ItemStack drop = item.splitStack(1);
				((IFluidContainerItem)item.getItem()).drain(drop, FluidContainerRegistry.BUCKET_VOLUME, true);
				if (drop.getItem().hasContainerItem(drop)) {
					drop = drop.getItem().getContainerItem(drop);
					if (drop != null && drop.isItemStackDamageable() && drop.getItemDamage() > drop.getMaxDamage())
						drop = null;
				}
				if (item.stackSize < 1)
					item = drop;
				else if (drop != null && !player.inventory.addItemStackToInventory(drop))
					player.dropItem(drop, false, true);
			}
			MFRRegistry.getLiquidDrinkHandlers().get(liquid).onDrink(player);
		}
		if (item == null)
		{
			item = r;
			item.stackSize = 0;
		}
		return item;
	}

	public String getFluidName(ItemStack item) {
		FluidStack liquid = ((IFluidContainerItem)item.getItem()).getFluid(item);
		if (liquid == null || liquid.amount < FluidContainerRegistry.BUCKET_VOLUME) return null;
		return liquid.getFluid().getName();
	}

	public boolean isDrinkableLiquid(String name) {
		return name != null && MFRRegistry.getLiquidDrinkHandlers().containsKey(name);
	}
}
