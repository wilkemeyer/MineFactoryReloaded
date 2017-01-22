package powercrystals.minefactoryreloaded.item.gun;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.entity.EntityFlyingItem;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;

public class ItemPotatoCannon extends ItemFactoryGun {

	private static final Item[] ammo = { Items.POTATO, Items.POISONOUS_POTATO, Items.SNOWBALL, Items.CLAY_BALL,
		Items.APPLE, Items.BOWL, Items.BRICK, Items.NETHERBRICK };
	private static final float[] dmg = { 1f, 1f, 0.3f, 0.6f, 1f, 1.1f, 1.3f, 0.9f };
	private static final int[] recover = { 7, 7, 0, 5, 8, 2, 1, 1 };

	@Override
	protected boolean hasGUI(ItemStack stack) {

		return false;
	}

	public int cofh_canEnchantApply(ItemStack stack, Enchantment ench) {

		if (ench == Enchantments.LOOTING)
			return 1;
		if (ench.type == EnumEnchantmentType.BOW)
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

		boolean flag = player.capabilities.isCreativeMode, a = false;

		int i = 0;
		if (flag) {
			flag |= EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			for (; !a && i < ammo.length; ++i)
				a = UtilInventory.playerHasItem(player, ammo[i]);
			if (a) --i;
			else if (flag) i = 0;
		}
		if (flag || a) {

			ItemStack fstack = new ItemStack(ammo[i]);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
            	ItemStack sStack = FurnaceRecipes.instance().getSmeltingResult(fstack);
            	if (sStack != null)
            		fstack = sStack;
            }
            fstack.stackSize = 1;
			EntityFlyingItem item = new EntityFlyingItem(world, player, fstack);
			item.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1.5f, 0.5f);

            int k = Math.max(0, EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack));

            item.setDamage(dmg[i] * (item.getDamage() + k * 1.2f));
            item.pickupChance = recover[i];

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
            item.setKnockbackStrength(l);

			if (flag) {
				item.canBePickedUp = 2;
			} else {
				ItemStack ammoStack = UtilInventory.findItem(player, ammo[i]);
				--ammoStack.stackSize;

				if (ammoStack.stackSize == 0)
				{
					player.inventory.deleteStack(ammoStack);
				}
			}
			if (!world.isRemote) {
				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1F, 0.5F / (itemRand.nextFloat() * 0.4F + 1.2F));
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
