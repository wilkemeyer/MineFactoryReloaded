package powercrystals.minefactoryreloaded.item;

import cofh.api.item.IAugmentItem;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemMulti;

public class ItemUpgrade extends ItemMulti implements IAugmentItem {

	public static ResourceLocation background;
	private static int NEGATIVE_START = (Short.MIN_VALUE >>> 1) & Short.MAX_VALUE;
	
	public ItemUpgrade() {

		setNames(0, "lapis", "tin", "iron", "copper", "bronze", "silver", "gold", "quartz", "diamond", "platinum", "emerald");
		setNames(NEGATIVE_START, "cobble");
		setUnlocalizedName("mfr.upgrade.radius");
		setMaxStackSize(64);
	}

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {

		super.addInfo(stack, player, infoList, advancedTooltips);
		infoList.add(String.format(MFRUtil.localize("tip.info.mfr.upgrade.radius", true), getAugmentLevel(stack, "radius")));
	}

	//TODO fix upgrades when it comes to former augment level implementation
	//@Override
	public int getAugmentLevel(ItemStack stack, String type) {

		if (type.equals("radius")) {
			int dmg = stack.getItemDamage();
			int mult = dmg >= NEGATIVE_START ? -1 : 1;
			dmg &= NEGATIVE_START - 1;
			return (dmg + 1) * mult;
		}
		return 0;
	}

	@Override
	public AugmentType getAugmentType(ItemStack stack) {

		return AugmentType.BASIC;
	}

	@Override
	public String getAugmentIdentifier(ItemStack stack) {

		return "radius";
	}

}
