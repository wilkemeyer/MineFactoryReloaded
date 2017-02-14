package powercrystals.minefactoryreloaded.item.gun;

import cofh.lib.util.helpers.ItemHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.EntityRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;

public class ItemSafariNetLauncher extends ItemFactoryGun {

	public ItemSafariNetLauncher() {
		setHasIcons(true);
		setUnlocalizedName("mfr.safarinet.launcher");
		setMaxStackSize(1);
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {
		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add(I18n.translateToLocal("tip.info.mfr.safarinet.mode"));
	}

	@Override
	protected boolean hasGUI(ItemStack stack) {
		return false;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player) {
		if (player.isSneaking()) {
			stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
			if (world.isRemote) {
				if (isCaptureMode(stack)) {
					player.addChatMessage(new TextComponentTranslation("chat.info.mfr.safarinet.capture"));
				} else {
					player.addChatMessage(new TextComponentTranslation("chat.info.mfr.safarinet.release"));
				}
			}
			return false;
		}

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack ammo = player.inventory.getStackInSlot(i);
			if (ItemSafariNet.isSafariNet(ammo)) {
				if (ItemSafariNet.isEmpty(ammo) == isCaptureMode(stack)) {
					player.inventory.setInventorySlotContents(i, ItemHelper.consumeItem(ammo));
					if (ammo.stackSize > 0) {
						ammo = ammo.copy();
					}
					ammo.stackSize = 1;
					if (!world.isRemote) {
						EntitySafariNet esn = new EntitySafariNet(world, player, ammo);
						esn.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 2f, .5f);
						world.spawnEntityInWorld(esn);

						world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,  0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					}
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isCaptureMode(ItemStack stack) {
		return stack != null && stack.getItemDamage() == 1;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired) {
		return fired ? 10 : 3;
	}

	@Override
	protected String getDelayTag(ItemStack stack) {
		return "mfr:SafariLaunch";
	}

	@Override
	public boolean preInit() {

		super.preInit();
		EntityRegistry.registerModEntity(EntitySafariNet.class, "SafariNet", 0, MineFactoryReloadedCore.instance(), 160, 5, true);

		return true;
	}
}
