package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableCropPlant extends HarvestableStandard
{
	private int _targetMeta;
	
	public HarvestableCropPlant(Block block, int targetMeta)
	{
		super(block, HarvestType.Normal);
		_targetMeta = targetMeta;
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) >= _targetMeta;
	}
}
