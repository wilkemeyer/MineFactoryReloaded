package powercrystals.minefactoryreloaded.item.syringe;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
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

public class ItemSyringeSlime extends ItemSyringe
{
	public ItemSyringeSlime()
	{
		setUnlocalizedName("mfr.syringe.slime");
		setContainerItem(MFRThings.syringeEmptyItem);
		setRegistryName(MineFactoryReloadedCore.modId, "syringe_slime");
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
		setSlimeSize(slime, slime.getSlimeSize() << 1);
		return true;
	}

	private static final Method SET_SLIME_SIZE;
	static {
		SET_SLIME_SIZE = ReflectionHelper.findMethod(EntitySlime.class, null, new String[]{"func_70799_a", "setSlimeSize"}, int.class);
	}

	private void setSlimeSize(EntitySlime e, int slimeSize) {
		try {
			SET_SLIME_SIZE.invoke(e, slimeSize);
		}
		catch(IllegalAccessException|InvocationTargetException ex) {
			MineFactoryReloadedCore.log().error("Error setting slime size", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "syringe", "variant=slime");
	}
}
