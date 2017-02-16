package powercrystals.minefactoryreloaded.item;

import cofh.api.item.IInventoryContainerItem;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;

public class ItemFactoryBag extends ItemFactory implements IInventoryContainerItem {

	public ItemFactoryBag() {

		setUnlocalizedName("mfr.plastic.bag");
		setMaxStackSize(24);
		setRegistryName(MineFactoryReloadedCore.modId, "plastic_bag");
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && (tag.hasKey("inventory") || tag.hasKey("Inventory")))
			return 1;
		return maxStackSize;
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);

		if (getItemStackLimit(stack) == 1) {
			if (stack.getTagCompound().hasKey("loot")) {
				infoList.add(MFRUtil.localize("info.cofh.loot", true));
			} else if (stack.getTagCompound().hasKey("inventory")) {
				infoList.add(MFRUtil.localize("info.cofh.legacy", true));
			} else if (!StringHelper.displayShiftForDetail || MFRUtil.isShiftKeyDown()) {
				ItemHelper.addInventoryInformation(stack, infoList);
			} else {
				infoList.add(MFRUtil.shiftForInfo());
			}
		} else {
			infoList.add(MFRUtil.localize("info.cofh.folded", true));
		}
	}

	@Override
	public int getSizeInventory(ItemStack container) {

		return 5;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		if (stack.stackSize != 1) {
			if (!world.isRemote)
				player.addChatMessage(new TextComponentTranslation("chat.info.mfr.bag.stacksize"));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		stack.setTagInfo("Accessible", new NBTTagCompound());
		stack.getTagCompound().removeTag("loot");

		if (!world.isRemote)
			player.openGui(MineFactoryReloadedCore.instance(), 2, world, 0, 0, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "plastic_bag");
	}
}
