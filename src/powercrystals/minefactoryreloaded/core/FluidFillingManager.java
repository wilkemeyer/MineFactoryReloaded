package powercrystals.minefactoryreloaded.core;

import net.minecraft.util.math.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.BlockPool;
import powercrystals.minefactoryreloaded.core.BlockPool.BlockNode;

public class FluidFillingManager
{
	private BlockPool _blocks;
	private boolean _isDone;
	
	private Area _area;
	private World _world;

	public FluidFillingManager(World world, Area area)
	{
		reset(world, area);
		_isDone = true;
	}

	public BlockPos getNextBlock()
	{
		BlockNode bn = _blocks.poke();
		return bn.pos;
	}

	public void moveNext()
	{
		searchForFreeBlocks(_blocks.shift());
		if (_blocks.size() == 0)
		{
			_isDone = true;
		}
	}

	private void searchForFreeBlocks(BlockNode bn)
	{
		BlockNode cur;

		for (EnumFacing side : EnumFacing.VALUES)
		{
			cur = BlockPool.getNext(bn.pos.offset(side));
			if (isValid(cur))
				_blocks.push(cur);
			else
				cur.free();
		}

		bn.free();
	}

	private boolean isValid(BlockNode bp)
	{
		Area area = _area;
		if (bp.pos.getX() < area.xMin || bp.pos.getX() > area.xMax ||
				bp.pos.getY() < area.yMin || bp.pos.getY() > area.yMax ||
				bp.pos.getZ() < area.zMin || bp.pos.getZ() > area.zMax ||
				!_world.isBlockLoaded(bp.pos))
			return false;

		Block block = _world.getBlockState(bp.pos).getBlock();
		return block.isReplaceable(_world, bp.pos);
	}

	public void reset(World world, Area area)
	{
		setWorld(world);
		_area = area;
		free();
		_isDone = false;
		_blocks = new BlockPool();
		_blocks.push(BlockPool.getNext(area.getOrigin()));
	}

	public void setWorld(World world)
	{
		_world = world;
	}

	public boolean getIsDone()
	{
		return _isDone;
	}

	public void free()
	{
		if (_blocks != null) while (_blocks.poke() != null)
			_blocks.shift().free();
		_isDone = true;
	}
}
