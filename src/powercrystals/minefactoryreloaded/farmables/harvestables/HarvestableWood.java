package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableWood extends HarvestableStandard
{
	public HarvestableWood(int blockId)
	{
		super(blockId, HarvestType.Tree);
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		if (getPlantId() == Block.wood.blockID)
		{
			if(BlockLog.limitToValidMetadata(world.getBlockMetadata(x, y, z)) == 3)
			{ // TODO: remove in 1.7
				if(harvesterSettings.get("harvestJungleWood") == null)
					return false;
				return harvesterSettings.get("harvestJungleWood");
			}
		}
		return true;
	}
	
	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
		int id = getPlantId();
		
        notifyBlock(world, x, y - 1, z, id);
        notifyBlock(world, x - 1, y, z, id);
        notifyBlock(world, x + 1, y, z, id);
        notifyBlock(world, x, y, z - 1, id);
        notifyBlock(world, x, y, z + 1, id);
        notifyBlock(world, x, y + 1, z, id);
	}
	
	protected void notifyBlock(World world, int x, int y, int z, int id)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (block != null && !block.isLeaves(world, x, y, z))
			world.notifyBlockOfNeighborChange(x, y, z, id);
	}
}
