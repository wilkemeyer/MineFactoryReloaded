package powercrystals.minefactoryreloaded.item.gun;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.entity.EntityFlyingItem;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;

public class ItemPotatoCannon extends ItemFactoryGun {

	@Override
	protected boolean hasGUI(ItemStack stack) {

		return false;
	}

	public int cofh_canEnchantApply(ItemStack stack, Enchantment ench) {

		if (ench.effectId == Enchantment.looting.effectId)
			return 1;
		if (ench.type == EnumEnchantmentType.bow)
			return 1;
		return -1;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 1;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player) {

		boolean flag = player.capabilities.isCreativeMode ||
				EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if (flag || player.inventory.hasItem(Items.potato)) {

			ItemStack fstack = new ItemStack(Items.potato);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
            	ItemStack sStack = FurnaceRecipes.smelting().getSmeltingResult(fstack);
            	if (sStack != null)
            		fstack = sStack;
            }
            fstack.stackSize = 1;
			EntityFlyingItem item = new EntityFlyingItem(world, player, fstack);

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (k > 0)
            	item.setDamage(item.getDamage() + k * 1.2f);

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            item.setKnockbackStrength(l);

			if (flag) {
				item.canBePickedUp = 2;
			} else {
				player.inventory.consumeInventoryItem(Items.potato);
			}
			if (!world.isRemote) {
				world.playSoundAtEntity(player, "random.bow", 1F, 0.5F / (itemRand.nextFloat() * 0.4F + 1.2F));
				world.spawnEntityInWorld(item);
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired) {

		return fired ? 10 : 20;
	}

	@Override
	protected String getDelayTag(ItemStack stack) {

		return "mfr:PotatoLaunched";
	}

}
