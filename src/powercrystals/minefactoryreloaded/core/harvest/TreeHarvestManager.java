package powercrystals.minefactoryreloaded.core.harvest;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.nbt.NBTTagSmartByteArray;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.BlockPool;
import powercrystals.minefactoryreloaded.core.BlockPool.BlockNode;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.core.SideOffset;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class TreeHarvestManager implements IHarvestManager {

	private BlockPool _blocks;
	private boolean _isDone;

	private Map<String, Boolean> _settings;
	private HarvestMode _harvestMode;
	private Area _area;
	private World _world;

	public TreeHarvestManager(Area treeArea) {

		_area = treeArea;
		_isDone = true;
	}

	private BlockPos getNextBlock() {

		BlockNode bn = _blocks.shift();
		searchForTreeBlocks(bn);
		BlockPos bp = bn.pos;
		bn.free();
		return bp;
	}

	private void moveNext() {

		if (_blocks.size() == 0) {
			_isDone = true;
		}
	}

	private void searchForTreeBlocks(BlockNode bn) {

		Map<Block, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		BlockNode cur;

		HarvestType type = getType(bn, harvestables);
		if (type == null || type == HarvestType.TreeFruit)
			return;

		SideOffset[] sides = !_harvestMode.isInverted ? SideOffset.ADJACENT_CUBE :
				SideOffset.ADJACENT_CUBE_INVERTED;

		for (SideOffset side : sides) {
			cur = BlockPool.getNext(bn.pos.add(side.offset));
			addIfValid(getType(cur, harvestables), cur);
		}
	}

	private void addIfValid(HarvestType type, BlockNode node) {

		if (type != null) {
			if (type == HarvestType.TreeFruit ||
					type == HarvestType.TreeLeaf) {
				_blocks.unshift(node);
				return;
			}
			else if (type == HarvestType.Tree ||
					type == HarvestType.TreeFlipped) {
				_blocks.push(node);
				return;
			}
		}
		node.free();
	}

	private HarvestType getType(BlockNode bp, Map<Block, IFactoryHarvestable> harvestables) {

		Area area = _area;
		if (bp.pos.getX() < area.xMin || bp.pos.getX() > area.xMax ||
				bp.pos.getY() < area.yMin || bp.pos.getY() > area.yMax ||
				bp.pos.getZ() < area.zMin || bp.pos.getZ() > area.zMax ||
				!_world.isBlockLoaded(bp.pos))
			return null;

		Block block = _world.getBlockState(bp.pos).getBlock();
		if (harvestables.containsKey(block)) {
			IFactoryHarvestable h = harvestables.get(block);
			if (h.canBeHarvested(_world, _settings, bp.pos)) {
				return h.getHarvestType();
			}
		}
		return null;
	}

	private void reset(World world, Area treeArea, Map<String, Boolean> settings) {

		_world = world;
		_area = treeArea;
		free();
		_isDone = false;
		_blocks = new BlockPool();
		BlockPos bp = treeArea.getOrigin();
		_blocks.push(BlockPool.getNext(bp));
		_settings = settings;
	}

	@Override
	public BlockPos getOrigin() {

		return _area.getOrigin();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		tag.setBoolean("done", _isDone);
		tag.setInteger("mode", _harvestMode.ordinal());
		BlockPos o = getOrigin();
		tag.setIntArray("area", new int[] { o.getX() - _area.xMin, o.getY() - _area.yMin, _area.yMax - o.getY() });
		tag.setIntArray("origin", new int[] { o.getX(), o.getY(), o.getZ() });
		NBTTagSmartByteArray list = new NBTTagSmartByteArray(_blocks.size() * 3 * 3);
		BlockNode bn = _blocks.poke();
		list.addVarInt(_blocks.size());
		while (bn != null) {
			list.addVarInt(bn.pos.getX()).addVarInt(bn.pos.getY()).addVarInt(bn.pos.getZ());
			bn = bn.next;
		}
		tag.setTag("curPos", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		free();
		_blocks = new BlockPool();

		_isDone = tag.getBoolean("done");
		_harvestMode = HarvestMode.values()[tag.getInteger("mode")];
		int[] area = tag.getIntArray("area"), o = tag.getIntArray("origin");
		if (area == null | o == null || o.length < 3 | area.length < 3) {
			_area = new Area(new BlockPos(0, -1, 0), 0, 0, 0);
			_isDone = true;
			return;
		}
		_area = new Area(new BlockPos(o[0], o[1], o[2]), area[0], area[1], area[2]);
		NBTBase baseList = tag.getTag("curPos");
		if (baseList.getId() == Constants.NBT.TAG_BYTE_ARRAY) {
			PacketCoFHBase tempPacket = new PacketCoFHBase(((NBTTagByteArray)baseList).getByteArray()){
				@Override public void handlePacket(EntityPlayer player, boolean isServer) {}
				};
			for (int length = tempPacket.getVarInt(); length-- > 0;) {
				_blocks.push(BlockPool.getNext(new BlockPos(tempPacket.getVarInt(), tempPacket.getVarInt(), tempPacket.getVarInt())));
			}
		} else {
			NBTTagList list = (NBTTagList) baseList;
			if (list.getTagType() == Constants.NBT.TAG_INT_ARRAY) {
				for (int i = 0, e = list.tagCount(); i < e; ++i) {
					int[] p = list.getIntArrayAt(i);
					_blocks.push(BlockPool.getNext(new BlockPos(p[0], p[1], p[2])));
				}
			}
			else
				for (int i = 0, e = list.tagCount(); i < e; ++i) {
					NBTTagCompound p = list.getCompoundTagAt(i);
					_blocks.push(BlockPool.getNext(new BlockPos(p.getInteger("x"), p.getInteger("y"), p.getInteger("z"))));
				}
		}
		if (_blocks.size() == 0)
			_isDone = true;
	}

	@Override
	public void free() {

		if (_blocks != null) while (_blocks.poke() != null)
			_blocks.shift().free();
		_isDone = true;
	}

	@Override
	public BlockPos getNextHarvest(World world, BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings) {

		Block block;
		_settings.put("isHarvestingTree", true);

		if (!pos.equals(getOrigin()) || _isDone) {
			int lowerBound = 0;
			int upperBound = MFRConfig.treeSearchMaxVertical.getInt();
			if (harvestable.getHarvestType() == HarvestType.TreeFlipped) {
				lowerBound = upperBound;
				upperBound = 0;
			}

			Area a = new Area(pos, MFRConfig.treeSearchMaxHorizontal.getInt(), lowerBound, upperBound);

			_harvestMode = harvestable.getHarvestType() == HarvestType.TreeFlipped ? HarvestMode.HarvestTreeInverted : HarvestMode.HarvestTree;
			reset(world, a, settings);
		}

		Map<Block, IFactoryHarvestable> harvestables = MFRRegistry.getHarvestables();
		while (!_isDone) {
			BlockPos bp = getNextBlock();
			moveNext();
			if (!_world.isBlockLoaded(bp)) {
				return null;
			}
			block = _world.getBlockState(bp).getBlock();

			if (harvestables.containsKey(block)) {
				IFactoryHarvestable obj = harvestables.get(block);
				HarvestType t = obj.getHarvestType();
				if (t == HarvestType.Tree | t == HarvestType.TreeFlipped |
						t == HarvestType.TreeLeaf | t == HarvestType.TreeFruit)
					if (obj.canBeHarvested(_world, settings, bp))
						return bp;
			}
		}
		return null;
	}

	@Override
	public boolean supportsType(HarvestType type) {

		return type == HarvestType.Tree || type == HarvestType.TreeFlipped || type == HarvestType.TreeLeaf;
	}
}
