package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;

public class HarvestableCocoa extends HarvestableStandard
{
	public HarvestableCocoa()
	{
		this(Blocks.cocoa);
	}
	
	public HarvestableCocoa(Block blockId)
	{
		super(blockId, HarvestType.TreeFruit);
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		if (harvesterSettings.get("isHarvestingTree") == Boolean.TRUE)
			return true;
		int blockMetadata = world.getBlockMetadata(x, y, z);
		return ((blockMetadata & 12) >> 2) >= 2;
	}
}
