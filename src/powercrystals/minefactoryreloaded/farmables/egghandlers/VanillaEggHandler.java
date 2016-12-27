package powercrystals.minefactoryreloaded.farmables.egghandlers;

import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.IMobEggHandler;

public class VanillaEggHandler implements IMobEggHandler
{
	@Override
	public EntityEggInfo getEgg(ItemStack safariNet)
	{
		return EntityList.ENTITY_EGGS.get(safariNet.getTagCompound().getString("id"));
	}
}
