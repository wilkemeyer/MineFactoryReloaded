package powercrystals.minefactoryreloaded.core;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import cofh.util.position.Area;
import cofh.util.position.BlockPosition;
import powercrystals.minefactoryreloaded.core.BlockPool.BlockNode;

public class FluidFillingManager implements IHarvestManager
{
	private BlockPool _blocks;
	private boolean _isDone;
	
	private Area _area;
	private World _world;

	public FluidFillingManager(World world, Area area)
	{
		reset(world, area, null);
		_isDone = true;
	}

	@Override
	public BlockPosition getNextBlock()
	{
		BlockNode bn = _blocks.poke();
		return bn.bp;
	}

	@Override
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
		BlockPosition bp = bn.bp;
		BlockNode cur;

		SideOffset[] sides = SideOffset.ADJACENT;

		for (int i = 0, e = sides.length; i < e; ++i)
		{
			SideOffset side = sides[i];
			cur = BlockPool.getNext(bp.x + side.offsetX, bp.y + side.offsetY, bp.z + side.offsetZ);
			if (isValid(cur.bp))
				_blocks.push(cur);
			else
				cur.free();
		}

		bn.free();
	}

	private boolean isValid(BlockPosition bp)
	{
		if ((bp.x > _area.xMax) | (bp.x < _area.xMin) |
				(bp.z > _area.zMax) | (bp.z < _area.zMin) |
				(bp.y > _area.yMax) | (bp.y < _area.yMin) ||
				!_world.blockExists(bp.x, bp.y, bp.z))
			return false;

		Block block = _world.getBlock(bp.x, bp.y, bp.z);
		return block == null || block.isReplaceable(_world, bp.x, bp.y, bp.z);
	}

	@Override
	public void reset(World world, Area area, HarvestMode harvestMode)
	{
		setWorld(world);
		_area = area;
		free();
		_isDone = false;
		_blocks = new BlockPool();
		BlockPosition bp = area.getOrigin();
		_blocks.push(BlockPool.getNext(bp.x, bp.y, bp.z));
	}

	@Override
	public void setWorld(World world)
	{
		_world = world;
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

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setBoolean("done", _isDone);
		BlockPosition o = getOrigin();
		data.setIntArray("area", new int[] {o.x - _area.xMin, o.y - _area.yMin, _area.yMax - o.y});
		data.setIntArray("origin", new int[] {o.x, o.y, o.z});
		NBTTagList list = new NBTTagList();
		BlockNode bn = _blocks.poke();
		while (bn != null)
		{
			BlockPosition bp = bn.bp;
			NBTTagCompound p = new NBTTagCompound();
			p.setInteger("x", bp.x);
			p.setInteger("y", bp.y);
			p.setInteger("z", bp.z);
			list.appendTag(p);
			bn = bn.next;
		}
		data.setTag("curPos", list);
		tag.setTag("harvestManager", data);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		free();
		_blocks = new BlockPool();

		NBTTagCompound data = tag.getCompoundTag("harvestManager");
		_isDone = data.getBoolean("done");
		int[] area = data.getIntArray("area"), o = data.getIntArray("origin");
		if (area == null | o == null || o.length < 3 | area.length < 3)
		{
			_area = new Area(new BlockPosition(0,-1,0),0,0,0);
			_isDone = true;
			return;
		}
		_area = new Area(new BlockPosition(o[0], o[1], o[2]), area[0], area[1], area[2]);
		NBTTagList list = (NBTTagList)data.getTag("curPos");
		for (int i = 0, e = list.tagCount(); i < e; ++i)
		{
			NBTTagCompound p = list.getCompoundTagAt(i);
			_blocks.push(BlockPool.getNext(p.getInteger("x"), p.getInteger("y"), p.getInteger("z")));
		}
	}

	@Override
	public void free()
	{
		if (_blocks != null) while (_blocks.poke() != null)
			_blocks.shift().free();
		_isDone = true;
	}
}
