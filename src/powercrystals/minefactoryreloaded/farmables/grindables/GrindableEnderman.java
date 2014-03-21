package powercrystals.minefactoryreloaded.farmables.grindables;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.MobDrop;

public class GrindableEnderman implements IFactoryGrindable
{
	@Override
	public Class<?> getGrindableEntity()
	{
		return EntityEnderman.class;
	}

	@Override
	public List<MobDrop> grind(World world, EntityLivingBase entity, Random random)
	{
		List<MobDrop> drops = new LinkedList<MobDrop>();
		int blockid = ((EntityEnderman)entity).getCarried();
		int meta = ((EntityEnderman)entity).getCarryingData();
		if (blockid > 0)
			drops.add(new MobDrop(10, new ItemStack(blockid, 1, meta)));
		return drops;
	}

	@Override
	public boolean processEntity(EntityLivingBase entity)
	{
		return false;
	}

}
