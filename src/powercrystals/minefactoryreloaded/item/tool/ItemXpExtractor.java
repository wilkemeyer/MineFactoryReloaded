package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nullable;

public class ItemXpExtractor extends ItemFactoryTool {

	public static DamageSource damage = new DamageSource("mfr.xpsuck").setDamageBypassesArmor().setDamageIsAbsolute();

	public ItemXpExtractor() {
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entity)
			{
				if (entity == null)
				{
					return 1F;
				}
				else
				{
					if (entity.isHandActive() && stack != null && entity.getActiveItemStack() == stack && stack.getItem() == MFRThings.xpExtractorItem) {
						int useRemaining = entity.getItemInUseCount();
						if (useRemaining > 24) return 1F;
						if (useRemaining > 12) return 2F;
						return 3F;
					}
					return 1F;
				}
			}
		});
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (player.experienceLevel > 0 && UtilInventory.playerHasItem(player, Items.BUCKET)) {
			player.setActiveHand(hand);
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Nullable
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
		if (world.isRemote || !(entity instanceof EntityPlayer))
			return stack;
		EntityPlayer player = (EntityPlayer) entity;
		suckExperience(player, player);
		return stack;
	}

	private void suckExperience(EntityPlayer target, EntityPlayer player) {
		if (target.capabilities.isCreativeMode && !player.capabilities.isCreativeMode)
			return;

		if (target.experienceLevel > 0) {
			ItemStack bucketStack = UtilInventory.findItem(player, Items.BUCKET);
			if (bucketStack != null) {
				UtilInventory.consumeItem(bucketStack, player);
				if (!target.capabilities.isCreativeMode) {
					target.addExperienceLevel(-1);
					target.attackEntityFrom(damage, 0.25f);
					target.worldObj.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.15f, 0.25f);
				}
				ItemStack essenceBucket = MFRUtil.getBucketFor(MFRThings.essence);
				if (!player.inventory.addItemStackToInventory(essenceBucket)) {
					player.entityDropItem(essenceBucket, 0f);
				}
			}
		}
	}

/*
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (usingItem != null && usingItem.getItem().equals(this)) {
			if (useRemaining > 24) return _icon1;
			if (useRemaining > 12) return _icon2;
			return _icon3;
		}
		return _icon1;
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		_icon1 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".1");
		_icon2 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".2");
		_icon3 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".3");

		itemIcon = _icon1;
	}
*/

}
