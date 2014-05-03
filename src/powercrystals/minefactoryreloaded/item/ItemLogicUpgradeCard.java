package powercrystals.minefactoryreloaded.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemLogicUpgradeCard extends ItemMulti
{
	private static String[] _upgradeNames = { "100", "300", "500" };
	
	public ItemLogicUpgradeCard()
	{
		setNames(_upgradeNames);
		setHasIcons(false);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		super.addInformation(stack, player, infoList, advancedTooltips);
		infoList.add("Circuits: " + getCircuitsForLevel(stack.getItemDamage() + 1));
		infoList.add("Variables: " + getVariablesForLevel(stack.getItemDamage() + 1));
	}
	
	public static int getCircuitsForLevel(int level)
	{
		return level == 0 ? 0 : 1 + 2 * (level - 1);
	}
	
	public static int getVariablesForLevel(int level)
	{
		return level == 0 ? 0 : 8 * level;
	}
}
