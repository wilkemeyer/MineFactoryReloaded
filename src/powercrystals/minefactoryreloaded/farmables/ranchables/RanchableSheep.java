package powercrystals.minefactoryreloaded.farmables.ranchables;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.RanchedItem;

public class RanchableSheep implements IFactoryRanchable
{
	@Override
	public Class<?> getRanchableEntity()
	{
		return EntitySheep.class;
	}
	
	@Override
	public List<RanchedItem> ranch(World world, EntityLivingBase entity, IInventory rancher)
	{
		EntitySheep s = (EntitySheep)entity;
		
		if(s.getSheared() || s.getGrowingAge() < 0)
		{
			return null;
		}
		
		List<RanchedItem> stacks = new LinkedList<RanchedItem>();
		stacks.add(new RanchedItem(Block.cloth, 1, s.getFleeceColor()));
		s.setSheared(true);
		
		return stacks;
	}
}
