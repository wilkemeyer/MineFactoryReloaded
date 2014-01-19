package powercrystals.minefactoryreloaded.core;

import java.util.Map;
import java.util.TreeSet;

import net.minecraft.world.World;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

public class TreeHarvestManager implements IHarvestManager
{
	private TreeSet<BlockPosition> _treeBlocks;
	private boolean _isDone;

	private HarvestMode _harvestMode;
	private Area _treeArea;
	private World _world;

	public TreeHarvestManager(World world, Area treeArea, HarvestMode harvestMode)
	{
		reset(world, treeArea, harvestMode);
	}

	@Override
	public BlockPosition getNextBlock()
	{
		return _treeBlocks.first();
	}

	@Override
	public void moveNext()
	{
		searchForTreeBlocks(_treeBlocks.pollFirst());
		if (_treeBlocks.size() == 0)
		{
			_isDone = true;
		}
	}

	private void searchForTreeBlocks(BlockPosition bp)
	{
		Map<Integer, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		for (BlockPosition curPos : bp.getAdjacent(true))
			if (isValid(curPos, harvestables))
				_treeBlocks.add(curPos);
	}

	private boolean isValid(BlockPosition bp, Map<Integer, IFactoryHarvestable> harvestables)
	{
		if ((bp.x > _treeArea.xMax) | (bp.x < _treeArea.xMin) |
			(bp.z > _treeArea.zMax) | (bp.z < _treeArea.zMin) |
			(bp.y > _treeArea.yMax) | (bp.y < _treeArea.yMin) ||
			!_world.blockExists(bp.x, bp.y, bp.z))
			return false;
		
		Integer blockId = _world.getBlockId(bp.x, bp.y, bp.z);
		if (harvestables.containsKey(blockId))
		{
			HarvestType obj = harvestables.get(blockId).getHarvestType();
			return obj == HarvestType.TreeFlipped |
					obj == HarvestType.TreeLeaf |
					obj == HarvestType.Tree;
		}
		return false;
	}

	@Override
	public void reset(World world, Area treeArea, HarvestMode harvestMode)
	{
		_world = world;
		_harvestMode = harvestMode;
		_treeArea = treeArea;
		_isDone = false;
		_treeBlocks = new TreeSet<BlockPosition>();
		_treeBlocks.add(treeArea.getOrigin());
	}

	@Override
	public boolean getIsDone()
	{
		return _isDone;
	}

	@Override
	public BlockPosition getOrigin()
	{
		return _treeArea.getOrigin();
	}
}
