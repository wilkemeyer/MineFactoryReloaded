package powercrystals.minefactoryreloaded.item.syringe;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.render.ModelHelper;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(MFRThings.syringeHealthItem, "syringe", "variant=health");
	}
}
