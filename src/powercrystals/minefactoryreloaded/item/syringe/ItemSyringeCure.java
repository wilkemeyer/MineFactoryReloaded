package powercrystals.minefactoryreloaded.item.syringe;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemSyringeCure extends ItemSyringe
{
	public ItemSyringeCure()
	{
		setUnlocalizedName("mfr.syringe.cure");
		setContainerItem(MFRThings.syringeEmptyItem);
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return (entity instanceof EntityZombie && ((EntityZombie)entity).isVillager()); 
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		((EntityZombie)entity).startConversion(300);
		return true;
	}
}
