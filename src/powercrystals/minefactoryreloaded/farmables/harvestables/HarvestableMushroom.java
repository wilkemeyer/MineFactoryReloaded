package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableMushroom extends HarvestableStandard
{
	public HarvestableMushroom(Block block)
	{
		super(block, HarvestType.Normal);
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return harvesterSettings.get("harvestSmallMushrooms") == Boolean.TRUE;
	}
}
