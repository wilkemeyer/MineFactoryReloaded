package powercrystals.minefactoryreloaded.core;

import java.util.List;

import net.minecraft.world.World;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;

public class FruitHarvestManager implements IHarvestManager
{
	private List<BlockPosition> _treeBlocks;
	private int _currentBlock;
	private boolean _isDone;
	
	private HarvestMode _harvestMode; 
	
	private Area _area;
	
	public FruitHarvestManager(World world, Area area, HarvestMode harvestMode)
	{
		reset(world, area, harvestMode);
	}
	
	@Override
	public BlockPosition getNextBlock()
	{
		return _treeBlocks.get(_currentBlock);
	}
	
	@Override
	public void moveNext()
	{
		_currentBlock++;
		if (_currentBlock >= _treeBlocks.size())
		{
			_isDone = true;
		}
	}
	
	@Override
	public void reset(World world, Area area, HarvestMode harvestMode)
	{
		_harvestMode = harvestMode;
		_area = area;
		_currentBlock = 0;
		_isDone = false;
		_treeBlocks = (_harvestMode.isInverted ? _area.getPositionsTopFirst() : _area.getPositionsBottomFirst());
	}
	
	@Override
	public boolean getIsDone()
	{
		return _isDone;
	}

	@Override
	public BlockPosition getOrigin()
	{
		return _area.getOrigin();
	}
}
