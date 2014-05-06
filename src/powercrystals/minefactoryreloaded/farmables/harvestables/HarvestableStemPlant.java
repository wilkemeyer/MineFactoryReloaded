package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableStemPlant extends HarvestableStandard
{
	protected Block _fruit;
	
	public HarvestableStemPlant(Block block, Block fruit)
	{
		super(block, HarvestType.Gourd);
		_fruit = fruit;
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> s, int x, int y, int z)
	{
		return world.getBlock(x, y, z).equals(_fruit);
	}
	
	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
		Block ground = world.getBlock(x, y - 1, z);
		if (world.getBlock(x, y, z).isAir(world, x, y, z) &&
				(ground.equals(Blocks.dirt) || ground.equals(Blocks.grass)))
		{
			world.setBlock(x, y - 1, z, Blocks.farmland);
		}
	}
}
