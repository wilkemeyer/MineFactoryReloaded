package powercrystals.minefactoryreloaded.item.syringe;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemSyringeHealth extends ItemSyringe
{

	public ItemSyringeHealth() {

		setUnlocalizedName("mfr.syringe.health");
		setContainerItem(MFRThings.syringeEmptyItem);
	}
	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return entity.getHealth() < entity.getMaxHealth();
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		entity.heal(5);
		return true;
	}
}
