package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemSyringeSlime extends ItemSyringe
{
	public ItemSyringeSlime()
	{
		setUnlocalizedName("mfr.syringe.slime");
		setContainerItem(MFRThings.syringeEmptyItem);
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return entity instanceof EntitySlime && ((EntitySlime)entity).getSlimeSize() < 8;
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		EntitySlime slime = (EntitySlime)entity;
		slime.setSlimeSize(slime.getSlimeSize() << 1);
		return true;
	}
	
}
