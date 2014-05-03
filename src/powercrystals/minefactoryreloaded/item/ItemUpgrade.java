package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import powercrystals.minefactoryreloaded.api.IUpgrade;

public class ItemUpgrade extends ItemMulti implements IUpgrade
{
	private static String[] _upgradeNames = { "lapis", "iron", "tin", "copper", "bronze",
		"silver", "gold", "quartz", "diamond", "platinum", "emerald", "cobble" };
	
	public ItemUpgrade()
	{
		setNames(_upgradeNames);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		super.addInformation(stack, player, infoList, advancedTooltips);
		infoList.add(StatCollector.translateToLocal("tip.info.mfr.upgrade.radius") + 
				" " + getUpgradeLevel(UpgradeType.RADIUS, stack));
	}
	
	@Override
	public int getUpgradeLevel(UpgradeType type, ItemStack stack)
	{
		if (type != UpgradeType.RADIUS)
			return 0;
		
		int dmg = stack.getItemDamage();
		switch (dmg)
		{
		case 11:
			return -1;
		default:
			return dmg + 1;
		}
	}
	
	@Override
	public boolean isApplicableFor(UpgradeType type, ItemStack stack)
	{
		return type == UpgradeType.RADIUS;
	}
}
