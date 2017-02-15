package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;

import javax.annotation.Nullable;

public class ItemMilkBottle extends ItemFactory {

	public ItemMilkBottle() {

		setContainerItem(Items.GLASS_BOTTLE);
		setUnlocalizedName("mfr.milkbottle");
		setMaxStackSize(16);
	}

	@Nullable
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {

		if (!(entity instanceof EntityPlayer))
			return stack;

		EntityPlayer player = (EntityPlayer) entity;

		if (!world.isRemote) {
			player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
		}

		if (!player.capabilities.isCreativeMode) {
			stack.stackSize--;

			if (stack.stackSize <= 0) {
				return new ItemStack(Items.GLASS_BOTTLE);
			} else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
				player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false, true);
			}
		}

		if (stack.stackSize <= 0) {
			stack.stackSize = 0;
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		player.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "milk_bottle");
	}
}
