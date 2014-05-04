package powercrystals.minefactoryreloaded.setup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import cpw.mods.fml.common.IFuelHandler;

public class MineFactoryReloadedFuelHandler implements IFuelHandler
{
	@Override
	public int getBurnTime(ItemStack fuel)
	{
		if (fuel == null)
			return 0;
		Item item = fuel.getItem();
		if(item.equals(MineFactoryReloadedCore.rubberWoodBlock))
		{
			return 350;
		}
		if(item.equals(MineFactoryReloadedCore.rubberLeavesBlock))
		{
			return 4 * (fuel.getItemDamage() + 1);
		}
		else if(item.equals(MineFactoryReloadedCore.rubberSaplingBlock))
		{
			return 100;
		}
		else if(item.equals(MineFactoryReloadedCore.sugarCharcoalItem))
		{
			return 400;
		}
		else if(item.equals(MineFactoryReloadedCore.rawRubberItem))
		{
			return 20;
		}
		else if(item.equals(MineFactoryReloadedCore.bioFuelBucketItem))
		{
			return 35000;
		}
		
		return 0;
	}
}
