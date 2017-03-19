package powercrystals.minefactoryreloaded.item.syringe;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemSyringeCure extends ItemSyringe
{
	public ItemSyringeCure()
	{
		setUnlocalizedName("mfr.syringe.cure");
		setContainerItem(MFRThings.syringeEmptyItem);
		setRegistryName(MineFactoryReloadedCore.modId, "syringe_cure");
	}

	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return (entity instanceof EntityZombie && ((EntityZombie)entity).isVillager()); 
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		startConversion((EntityZombie)entity, 300);
		return true;
	}

	private static final Method START_CONVERSION;
	static {
		START_CONVERSION = ReflectionHelper
				.findMethod(EntityZombie.class, null, new String[]{"func_82228_a", "startConversion"}, int.class);
	}

	private void startConversion(EntityZombie e, int ticks) {
		try {
			START_CONVERSION.invoke(e, ticks);
		}
		catch(IllegalAccessException|InvocationTargetException ex) {
			MineFactoryReloadedCore.log().error("Error starting zombie conversion", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(MFRThings.syringeCureItem, "syringe", "variant=cure");
	}
}
