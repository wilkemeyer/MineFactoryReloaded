package powercrystals.minefactoryreloaded.tile.rednet;

import cofh.util.position.BlockPosition;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOutputNode;
import powercrystals.minefactoryreloaded.core.IGrid;
import powercrystals.minefactoryreloaded.net.GridTickHandler;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class RedstoneNetwork implements IGrid
{
	static final GridTickHandler<RedstoneNetwork, TileEntityRedNetCable> HANDLER =
			GridTickHandler.redstone;
	
	private boolean _ignoreUpdates;
	
	private boolean _mustUpdate, updatePowerLevels;
	private boolean[] _updateSubnets = new boolean[16];
	
	@SuppressWarnings("unchecked") // cast is a warning too. using a generic type is an error?
	private Set<BlockPosition>[] _singleNodes = new Set[16];
	private Set<BlockPosition> _omniNodes = new LinkedHashSet<BlockPosition>();
	
	private Set<BlockPosition> _weakNodes = new LinkedHashSet<BlockPosition>();
	
	private boolean regenerating;
	private LinkedHashSet<TileEntityRedNetCable> nodeSet = new LinkedHashSet<TileEntityRedNetCable>();
	private LinkedHashSet<TileEntityRedNetCable> conduitSet;
	
	private int[] _powerLevelOutput = new int[16];
	private BlockPosition[] _powerProviders = new BlockPosition[16];
	
	private World _world;
	
	private static boolean log = false;
	private static Logger _log = LogManager.getLogger("RedNet Debug");
	public static void log(String format, Object... data)
	{
		if (log && format != null)
		{
			_log.debug(format, data);
		}
	}
	
	private RedstoneNetwork(World world)
	{
		_world = world;
		log = MFRConfig.redNetDebug.getBoolean(false);
		
		for (int i = 0; i < 16; i++)
		{
			_singleNodes[i] = new LinkedHashSet<BlockPosition>();
		}
	}

	public RedstoneNetwork(TileEntityRedNetCable base) { this(base.getWorldObj());
		conduitSet = new LinkedHashSet<TileEntityRedNetCable>();
		regenerating = true;
		addConduit(base);
		regenerating = false;
	}

	@Override
	public void doGridPreUpdate() {
		// may be able to not tick at all if _mustUpdate and updatePowerLevels are false
		if (_mustUpdate) {
			for (int i = 16; i --> 0; )
				if (_updateSubnets[i])
					notifyNodes(i);
			_mustUpdate = false;
		}
	}

	@Override
	public void doGridUpdate() {
		if (updatePowerLevels) {
			updatePowerLevels();
			updatePowerLevels = false;
		}
	}

	public void addConduit(TileEntityRedNetCable cond) {
		if (conduitSet.add(cond))
			if (!conduitAdded(cond))
				return;
		if (cond.isRSNode) {
			if (nodeSet.add(cond)) {
				nodeAdded(cond);
			}
		} else if (!nodeSet.isEmpty()) {
			if (nodeSet.remove(cond)) {
				nodeRemoved(cond);
			}
		}
	}

	public void removeConduit(TileEntityRedNetCable cond) {
		conduitSet.remove(cond);
		if (!nodeSet.isEmpty()) {
			if (nodeSet.remove(cond)) {
				nodeRemoved(cond);
			}
		}
	}

	@Override
	public void markSweep() {
		destroyGrid();
		if (conduitSet.isEmpty())
			return;
		TileEntityRedNetCable main = conduitSet.iterator().next();
		nodeSet.clear();
		LinkedHashSet<TileEntityRedNetCable> oldSet = conduitSet;
		conduitSet = new LinkedHashSet<TileEntityRedNetCable>(Math.min(oldSet.size() / 6, 5));
		rebalanceGrid();
		
		LinkedHashSet<TileEntityRedNetCable> toCheck = new LinkedHashSet<TileEntityRedNetCable>();
		BlockPosition bp = new BlockPosition(0,0,0);
		ForgeDirection[] dir = ForgeDirection.VALID_DIRECTIONS;
		toCheck.add(main);
		while (!toCheck.isEmpty()) {
			main = toCheck.iterator().next();
			addConduit(main);
			World world = main.getWorldObj();
			for (int i = 6; i --> 0; ) {
				bp.x = main.xCoord; bp.y = main.yCoord; bp.z = main.zCoord;
				bp.step(dir[i]);
				if (world.blockExists(bp.x, bp.y, bp.z)) {
					TileEntity te = bp.getTileEntity(world);
					if (te instanceof TileEntityRedNetCable)
						if (main.canInterface((TileEntityRedNetCable)te) && !conduitSet.contains(te))
							toCheck.add((TileEntityRedNetCable)te);
				}
			}
			toCheck.remove(main);
			oldSet.remove(main);
		}
		if (!oldSet.isEmpty()) {
			RedstoneNetwork newGrid = new RedstoneNetwork(_world);
			newGrid.conduitSet = oldSet;
			newGrid.regenerating = true;
			newGrid.markSweep();
		}
		if (nodeSet.isEmpty())
			HANDLER.removeGrid(this);
		else
			HANDLER.addGrid(this);
		regenerating = false;
		updatePowerLevels = !nodeSet.isEmpty();
	}
	
	public void destroyGrid() {
		regenerating = true;
		for (TileEntityRedNetCable curCond : nodeSet)
			destroyNode(curCond);
		for (TileEntityRedNetCable curCond : conduitSet)
			destroyConduit(curCond);
		HANDLER.removeGrid(this);
	}

	public void destroyNode(TileEntityRedNetCable cond) {
		cond._network = null;
	}

	public void destroyConduit(TileEntityRedNetCable cond) {
		cond._network = null;
	}

	public void nodeAdded(TileEntityRedNetCable cond) {
		rebalanceGrid();
		if (!nodeSet.isEmpty()) {
			HANDLER.addGrid(this);
		}
	}

	public void nodeRemoved(TileEntityRedNetCable cond) {
		rebalanceGrid();
		if (nodeSet.isEmpty()) {
			HANDLER.removeGrid(this);
		}
	}

	public void rebalanceGrid() {
	}

	public boolean conduitAdded(TileEntityRedNetCable cond) {
		if (cond._network != null) {
			if (cond._network != this) {
				conduitSet.remove(cond);
				if (canGridMerge(cond._network)) {
					mergeGrid(cond._network);
				} else
					return false;
			} else
				return false;
		} else
			cond.setNetwork(this);
		return true;
	}
	
	public boolean canGridMerge(RedstoneNetwork otherGrid) {
		LinkedHashSet<TileEntityRedNetCable> toCheck = otherGrid.conduitSet;
		return !toCheck.isEmpty() && !conduitSet.isEmpty() &&
				toCheck.iterator().next().canInterface(conduitSet.iterator().next());
	}

	public void mergeGrid(RedstoneNetwork theGrid) {
		theGrid.destroyGrid();
		boolean r = regenerating || theGrid.regenerating;
		if (!regenerating & r)
			regenerate();
		
		regenerating = true;
		for (TileEntityRedNetCable cond : theGrid.conduitSet)
			addConduit(cond);
		regenerating = r;
		
		theGrid.conduitSet.clear();
		theGrid.nodeSet.clear();
	}
	
	public void regenerate() {
		regenerating = true;
		HANDLER.regenerateGrid(this);
	}

	public boolean isRegenerating() {
		return regenerating;
	}

	public int getConduitCount() {
		return conduitSet.size();
	}

	public int getNodeCount() {
		return nodeSet.size();
	}
	
	public int getPowerLevelOutput(int subnet)
	{
		return _powerLevelOutput[subnet];
	}
	
	boolean isPowerProvider(int subnet, BlockPosition node)
	{
		return node.equals(_powerProviders[subnet]);
	}
	
	@Override
	public String toString() {
		return "RedstoneNetwork@" + Integer.toString(hashCode()) + "" +
				"; regenerating:" + regenerating + "; isTicking:" + HANDLER.isGridTicking(this);
	}
	
	 /// OLD CODE
	
	public boolean isWeakNode(BlockPosition node)
	{
		return _weakNodes.contains(node);
	}
	
	public void addOrUpdateNode(BlockPosition node)
	{
		Block block = _world.getBlock(node.x, node.y, node.z);
		if (block.equals(MineFactoryReloadedCore.rednetCableBlock))
		{
			return;
		}
		
		if (!_omniNodes.contains(node))
		{
			RedstoneNetwork.log("Network with ID %d adding omni node %s", hashCode(), node.toString());
			_omniNodes.add(node);
			notifyOmniNode(node);
		}
		
		int[] powers = getOmniNodePowerLevel(node);
		for (int subnet = 0; subnet < 16; subnet++)
		{
			int power = powers[subnet];
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet]))
			{
				RedstoneNetwork.log("Network with ID %d:%d has omni node %s as new power provider", hashCode(), subnet, node.toString());
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
				_mustUpdate = true;
				_updateSubnets[subnet] = true;
			}
			else if (node.equals(_powerProviders[subnet]) && Math.abs(power) < Math.abs(_powerLevelOutput[subnet]))
			{
				updatePowerLevels(subnet);
			}
		}
	}
	
	public void addOrUpdateNode(BlockPosition node, int subnet, boolean allowWeak)
	{
		Block block = _world.getBlock(node.x, node.y, node.z);
		if (block.equals(MineFactoryReloadedCore.rednetCableBlock))
		{
			return;
		}
		
		if (!_singleNodes[subnet].contains(node))
		{
			removeNode(node);
			RedstoneNetwork.log("Network with ID %d:%d adding node %s", hashCode(), subnet, node.toString());
			
			_singleNodes[subnet].add(node);
			notifySingleNode(node, subnet);
		}
		
		if (allowWeak)
		{
			_weakNodes.add(node);
		}
		else
		{
			_weakNodes.remove(node);
		}
		
		int power = getSingleNodePowerLevel(node, subnet);
		RedstoneNetwork.log("Network with ID %d:%d calculated power for node %s as %d", hashCode(), subnet, node.toString(), power);
		if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet]))
		{
			RedstoneNetwork.log("Network with ID %d:%d has node %s as new power provider", hashCode(), subnet, node.toString());
			_powerLevelOutput[subnet] = power;
			_powerProviders[subnet] = node;
			_mustUpdate = true;
			_updateSubnets[subnet] = true;
		}
		else if (node.equals(_powerProviders[subnet]) && Math.abs(power) < Math.abs(_powerLevelOutput[subnet]))
		{
			RedstoneNetwork.log("Network with ID %d:%d removing power provider node, recalculating", hashCode(), subnet);
			updatePowerLevels(subnet);
		}
	}
	
	public void removeNode(BlockPosition node)
	{
		boolean omniNode = _omniNodes.contains(node);
		boolean notify = omniNode;
		
		if (omniNode)
			_omniNodes.remove(node);
		_weakNodes.remove(node);
		
		for (int subnet = 0; subnet < 16; subnet++)
		{
			if (_singleNodes[subnet].contains(node))
			{
				notify = true;
				RedstoneNetwork.log("Network with ID %d:%d removing node %s", hashCode(), subnet, node.toString());
				_singleNodes[subnet].remove(node);
			}
			
			if (node.equals(_powerProviders[subnet]))
			{
				RedstoneNetwork.log("Network with ID %d:%d removing power provider node, recalculating", hashCode(), subnet);
				updatePowerLevels(subnet);
			}
		}
		
		Block block = _world.getBlock(node.x, node.y, node.z);
		if (notify)
		{
			if (block.equals(MineFactoryReloadedCore.rednetCableBlock))
			{
				return;
			}
			else if (block instanceof IRedNetInputNode)
			{
				if (omniNode)
					((IRedNetInputNode)block).onInputsChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
				else
					((IRedNetInputNode)block).onInputChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), 0);
			}
		}
		_world.notifyBlockOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock);
		_world.notifyBlocksOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock);
	}
	
	public void mergeNetwork(RedstoneNetwork network)
	{
		//...
		
		updatePowerLevels();
	}
	
	public void updatePowerLevels()
	{
		for (int subnet = 0; subnet < 16; subnet++)
		{
			updatePowerLevels(subnet);
		}
	}
	
	public void updatePowerLevels(int subnet)
	{
		int lastPower = _powerLevelOutput[subnet];
		
		_powerLevelOutput[subnet] = 0;
		_powerProviders[subnet] = null;
		
		log("Network with ID %d:%d recalculating power levels for %d single nodes and %d omni nodes", hashCode(), subnet, _singleNodes[subnet].size(), _omniNodes.size());
		
		for (BlockPosition node : _singleNodes[subnet])
		{
			if (!isNodeLoaded(node))
			{
				continue;
			}
			int power = getSingleNodePowerLevel(node, subnet);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet]))
			{
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
			}
		}
		
		for (BlockPosition node : _omniNodes)
		{
			if (!isNodeLoaded(node))
			{
				continue;
			}
			int power = getOmniNodePowerLevel(node, subnet);
			if (Math.abs(power) > Math.abs(_powerLevelOutput[subnet]))
			{
				_powerLevelOutput[subnet] = power;
				_powerProviders[subnet] = node;
			}
			
		}
		
		RedstoneNetwork.log("Network with ID %d:%d recalculated power levels as: output: %d with powering node %s", hashCode(), subnet, _powerLevelOutput[subnet], _powerProviders[subnet]);
		_mustUpdate = _powerLevelOutput[subnet] != lastPower;
		_updateSubnets[subnet] = _mustUpdate;
	}
	
	private void notifyNodes(int subnet)
	{
		if (_ignoreUpdates)
		{
			RedstoneNetwork.log("Network asked to notify nodes while ignoring updates (API misuse?)!");
			_mustUpdate = true;
			_updateSubnets[subnet] = true;
			return;
		}
		_updateSubnets[subnet] = false;
		_ignoreUpdates = true;
		RedstoneNetwork.log("Network with ID %d:%d notifying %d single nodes and %d omni nodes", hashCode(), subnet, _singleNodes[subnet].size(), _omniNodes.size());
		for (BlockPosition bp : _singleNodes[subnet])
		{
			RedstoneNetwork.log("Network with ID %d:%d notifying node %s of power state change to %d", hashCode(), subnet, bp.toString(), _powerLevelOutput[subnet]);
			notifySingleNode(bp, subnet);
		}
		for (BlockPosition bp : _omniNodes)
		{
			RedstoneNetwork.log("Network with ID %d:%d notifying omni node %s of power state change to %d", hashCode(), subnet, bp.toString(), _powerLevelOutput[subnet]);
			notifyOmniNode(bp);
		}
		_ignoreUpdates = false;
	}
	
	private boolean isNodeLoaded(BlockPosition node)
	{
		return _world.getChunkProvider().chunkExists(node.x >> 4, node.z >> 4);
	}
	
	private void notifySingleNode(BlockPosition node, int subnet)
	{
		if (isNodeLoaded(node))
		{
			Block block = _world.getBlock(node.x, node.y, node.z);
			if (block.equals(MineFactoryReloadedCore.rednetCableBlock))
			{
				return;
			}
			else if (block instanceof IRedNetInputNode)
			{
				((IRedNetInputNode)block).onInputChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), _powerLevelOutput[subnet]);
			}
			else
			{
				_world.notifyBlockOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock);
				_world.notifyBlocksOfNeighborChange(node.x, node.y, node.z, MineFactoryReloadedCore.rednetCableBlock);
			}
		}
	}
	
	private void notifyOmniNode(BlockPosition node)
	{
		if (isNodeLoaded(node))
		{
			Block block = _world.getBlock(node.x, node.y, node.z);
			if (block instanceof IRedNetInputNode)
			{
				((IRedNetInputNode)block).onInputsChanged(_world, node.x, node.y, node.z, node.orientation.getOpposite(), Arrays.copyOf(_powerLevelOutput, 16));
			}
		}
	}
	
	private int getOmniNodePowerLevel(BlockPosition node, int subnet)
	{
		if(!isNodeLoaded(node))
		{
			return 0;
		}
		return getOmniNodePowerLevel(node)[subnet];
	}
	
	private int[] getOmniNodePowerLevel(BlockPosition node)
	{
		if(!isNodeLoaded(node))
		{
			return null;
		}
		Block b = _world.getBlock(node.x, node.y, node.z);
		if (b instanceof IRedNetOutputNode)
		{
			return ((IRedNetOutputNode)b).getOutputValues(_world, node.x, node.y, node.z, node.orientation.getOpposite());
		}
		else
		{
			return null;
		}
	}
	
	private int getSingleNodePowerLevel(BlockPosition node, int subnet)
	{
		if (!isNodeLoaded(node))
		{
			return 0;
		}
		
		Block block = _world.getBlock(node.x, node.y, node.z);
		if (block instanceof IRedNetOutputNode)
		{
			return ((IRedNetOutputNode)block).getOutputValue(_world, node.x, node.y, node.z, node.orientation, subnet);
		}

		int offset = 0;
		if (block.equals(Blocks.redstone_wire))
		{
			offset = -1;
		}
		
		int ret = 0;
		if (_weakNodes.contains(node))
		{
			int weakPower = _world.getIndirectPowerLevelTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
			int strongPower = _world.isBlockProvidingPowerTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
			ret = Math.max(weakPower, strongPower);
		}
		else
		{
			ret =  _world.isBlockProvidingPowerTo(node.x, node.y, node.z, node.orientation.ordinal()) + offset;
		}
		
		if (offset == ret)
			return 0;
		return ret;
	}
}