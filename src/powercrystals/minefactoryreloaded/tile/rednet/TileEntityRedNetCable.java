package powercrystals.minefactoryreloaded.tile.rednet;

import static powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType.None;
import static powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable.subSelection;

import cofh.render.hitbox.CustomHitBox;
import cofh.render.hitbox.ICustomHitBox;
import cofh.repack.codechicken.lib.raytracer.IndexedCuboid6;
import cofh.repack.codechicken.lib.vec.Vector3;
import cofh.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicPoint;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.INode;
import powercrystals.minefactoryreloaded.core.ITraceable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

public class TileEntityRedNetCable extends TileEntityBase implements INode, ITraceable, ICustomHitBox
{
	protected int[] _sideColors = new int [6];
	protected byte[] _cableMode = {0,0,0, 0,0,0, 0};
	protected RedNetConnectionType[] _connectionState = {None,None,None, None,None,None};
	protected IRedNetLogicPoint[] _sideUpgrade = {null,null,null, null,null,null};
	
	RedstoneNetwork _network;
	boolean isRSNode = false;
	
	private static THashSet<Block> _connectionBlackList;
	
	public TileEntityRedNetCable()
	{
		if(_connectionBlackList == null)
		{
			_connectionBlackList = new THashSet<Block>(256);
			for(String s : MFRConfig.redNetConnectionBlacklist.getStringList())
			{
				try
				{
					if (!StringUtils.isNullOrEmpty(s))
						_connectionBlackList.add(Block.getBlockFromName(s));
				}
				catch(NumberFormatException x)
				{
					System.out.println("Empty or invalid rednet blacklist entry found. Not adding to rednet blacklist.");
				}
			}
			List<Integer> wl = Arrays.asList(23, 25, 27, 28, 29, 33, 46, 50, 55, 64, 69, 70, 71,
					72, 75, 76, 77, 93, 94, 96, 107, 123, 124, 131, 143, 147, 148, 149, 150,
					151, 152, 154, 157, 158);
			int i = 176; // as of 1.7.2
			while (i --> 0)
				if (!wl.contains(i))
					_connectionBlackList.add(Block.getBlockById(i));
		}
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if (worldObj.isRemote)
			return;
		RedstoneNetwork.HANDLER.addConduitForTick(this);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (_network != null) {
			_network.removeConduit(this);
			int c = 0;
			for (int i = 6; i --> 0; )
				if (_connectionState[i].isAllSubnets)
					++c;
			if (c > 1)
				_network.regenerate();
			_network = null;
		}
	}
	
	@Override
	public boolean isNotValid()
	{
		return tileEntityInvalid;
	}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	@Override
	public void onNeighborBlockChange()
	{
		RedstoneNetwork.HANDLER.addConduitForUpdate(this);
	}
	
	@Override
	public void onMatchedNeighborBlockChange()
	{
		RedstoneNetwork.HANDLER.addConduitForUpdate(this);
	}

	@Override
	public void firstTick(IGridController grid)
	{
		if (worldObj == null || worldObj.isRemote) return;
		if (grid != RedstoneNetwork.HANDLER) return;
		if (_network == null) {
			incorporateTiles();
			if (_network != null)
			{
				markDirty();
			}
		}
		if (_network == null)
		{
			setNetwork(new RedstoneNetwork(this));
			markDirty();
		}
		updateInternalTypes(RedstoneNetwork.HANDLER);
		Packets.sendToAllPlayersWatching(this);
	}

	private void incorporateTiles() {
		if (_network != null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				if (worldObj.blockExists(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
					TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
							yCoord + dir.offsetY, zCoord + dir.offsetZ);
					if (tile instanceof TileEntityRedNetCable)
						_network.addConduit((TileEntityRedNetCable)tile);
				}
			}
		} else for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (worldObj.blockExists(xCoord + dir.offsetX,
					yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
				TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (tile instanceof TileEntityRedNetCable && ((TileEntityRedNetCable)tile)._network != null) {
					((TileEntityRedNetCable)tile)._network.addConduit(this);
					break;
				}
			}
		}
	}
	
	public void setNetwork(RedstoneNetwork network)
	{
		_network = network;
		if (_network != null && !_network.isRegenerating())
			incorporateTiles();
	}

	public RedstoneNetwork getNetwork()
	{
		return _network;
	}

	@Override
	public void updateInternalTypes(IGridController grid)
	{
		if (grid != RedstoneNetwork.HANDLER) return;
		boolean lastNode = isRSNode;
		ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
		for (ForgeDirection d : dirs)
			updateNearbyNode(d);
		isRSNode = false;
		for (int i = _connectionState.length; i --> 0; ) {
			ForgeDirection d = dirs[i];
			int x = xCoord + d.offsetX, y = yCoord + d.offsetY, z = zCoord + d.offsetZ;
			if (!worldObj.getBlock(x, y, z).equals(MineFactoryReloadedCore.rednetCableBlock))
				isRSNode |= _connectionState[i].isConnected;
		}
		if (lastNode != isRSNode)
			_network.addConduit(this);
		markChunkDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void updateNearbyNode(ForgeDirection from)
	{
		updateNearbyNode(getSideColor(from), from);
	}

	public void updateNearbyNode(int subnet, ForgeDirection from)
	{
		BlockPosition bp = new BlockPosition(xCoord, yCoord, zCoord, from);
		bp.step(from);
		updateNearbyNode(bp, subnet);
	}

	private void updateNearbyNode(BlockPosition bp, int subnet)
	{
		if (_network == null)
			return;
		RedNetConnectionType connectionType = getConnectionState(bp.orientation);

		if (!connectionType.isDecorative & connectionType.isConnected && !worldObj.isAirBlock(bp.x, bp.y, bp.z))
		{
			if (connectionType.isAllSubnets)
			{
				_network.addOrUpdateNode(bp);
			}
			else
			{
				_network.addOrUpdateNode(bp, subnet, connectionType.isPlate);
			}
		}
		else
		{
			_network.removeNode(bp);
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public int getWeakPower(ForgeDirection to)
	{
		if (_network == null)
			return 0;
		RedNetConnectionType state = _connectionState[to.ordinal()];
		if (!state.isConnected | !state.isSingleSubnet)
		{
			return 0;
		}

		BlockPosition nodebp = new BlockPosition(xCoord, yCoord, zCoord, to);
		nodebp.step(to);

		int subnet = getSideColor(to), power;

		RedstoneNetwork.log("Asked for weak power at " + xCoord + "," + yCoord + "," + zCoord + ";" + to);
		if (_network.isPowerProvider(subnet, nodebp))
		{
			RedstoneNetwork.log("\t- power provider for network " + _network.hashCode() + ", power 0");
			return 0;
		}
		else
		{
			power = Math.min(Math.max(_network.getPowerLevelOutput(subnet), 0), 15);
			RedstoneNetwork.log("\t- got " + power + " from network " + _network.hashCode() + ":" + subnet);
		}
		return power;
	}

	public int getStrongPower(ForgeDirection to)
	{
		if (_network == null)
			return 0;
		RedNetConnectionType state = _connectionState[to.ordinal()];
		if (!state.isConnected | !state.isSingleSubnet)
		{
			return 0;
		}

		BlockPosition nodebp = new BlockPosition(xCoord, yCoord, zCoord, to);
		nodebp.step(to);

		int subnet = getSideColor(nodebp.orientation);

		RedstoneNetwork.log("Asked for strong power at " + xCoord + "," + yCoord + "," + zCoord + ";" + to);
		if (_network.isPowerProvider(subnet, nodebp))
		{
			RedstoneNetwork.log("\t- power provider for network " + _network.hashCode() + ", power 0");
			return 0;
		}
		else if (_network.isWeakNode(nodebp))
		{
			RedstoneNetwork.log("\t- weak node for network " + _network.hashCode() + ", power 0");
			return 0;
		}
		else
		{
			int power = Math.min(Math.max(_network.getPowerLevelOutput(subnet), 0), 15);
			RedstoneNetwork.log("\t- got " + power + " from network " + _network.hashCode() + ":" + subnet);
			return power;
		}
	}

	@Override
	public boolean onPartHit(EntityPlayer player, int side, int subHit)
	{
		markChunkDirty();
		return false;
	}

	@Override
	public boolean isLargePart(EntityPlayer player, int subHit)
	{
		return subHit < 2 | ((subHit >= (2 + 6 * 3)) & subHit < (2 + 6 * 5));
	}

	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace)
	{
		addTraceableCuboids(list, forTrace, false);
	}

	@Override
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean forDraw)
	{
		Vector3 offset = new Vector3(xCoord, yCoord, zCoord);

		IndexedCuboid6 main = new IndexedCuboid6(0, subSelection[0]); // main body
		list.add(main);

		ForgeDirection[] sides = ForgeDirection.VALID_DIRECTIONS;
		for (int i = sides.length; i --> 0; )
		{
			RedNetConnectionType c = getConnectionState(sides[i], true);
			RedNetConnectionType f = getConnectionState(sides[i], false);
			int o = 2 + i;
			if (c.isConnected)
			{
				if (c.isPlate)
					o += 6;
				else if (c.isCable)
					if (c.isAllSubnets)
					{
						if (forDraw)
							main.setSide(i, i & 1);
						else {
							o = 2 + 6*3 + i;
							list.add((IndexedCuboid6)new IndexedCuboid6(1, // cable part
									subSelection[o]).setSide(i, i & 1).add(offset));
						}
						continue;
					}
				list.add((IndexedCuboid6)new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point
				o = 2 + 6*2 + i;
				if (c.isSingleSubnet) // color band
					list.add((IndexedCuboid6)new IndexedCuboid6(o, subSelection[o]).add(offset));
				o += 6;
				list.add((IndexedCuboid6)new IndexedCuboid6(1, subSelection[o]).add(offset)); // cable part
			}
			else if (forTrace & f.isConnected && _cableMode[6] != 1)
			{ // cable-only
				list.add((IndexedCuboid6)new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point (raytrace)
			}
		}
		main.add(offset);
	}

	@Override
	public boolean shouldRenderCustomHitBox(int subHit, EntityPlayer player)
	{
		return subHit < 2;
	}

	@Override
	public CustomHitBox getCustomHitBox(int hit, EntityPlayer player)
	{
		final List<IndexedCuboid6> list = new ArrayList<IndexedCuboid6>(7);
		addTraceableCuboids(list, true, false);
		IndexedCuboid6 cube = list.get(0);
		cube.expand(0.003);
		Vector3 min = cube.min, max = cube.max.sub(min);
		CustomHitBox box = new CustomHitBox(max.x, max.y, max.z, min.x, min.y, min.z);
		for (int i = 1, e = list.size(); i < e; ++i)
		{
			cube = list.get(i);
			if (shouldRenderCustomHitBox((Integer)cube.data, player))
			{
				cube.sub(min);
				if (cube.min.y < 0)
					box.sideLength[0] = Math.max(box.sideLength[0], -cube.min.y);
				if (cube.min.z < 0)
					box.sideLength[2] = Math.max(box.sideLength[2], -cube.min.z);
				if (cube.min.x < 0)
					box.sideLength[4] = Math.max(box.sideLength[4], -cube.min.x);
				cube.sub(max);
				if (cube.max.y > 0)
					box.sideLength[1] = Math.max(box.sideLength[1], cube.max.y);
				if (cube.max.z > 0)
					box.sideLength[3] = Math.max(box.sideLength[3], cube.max.z);
				if (cube.max.x > 0)
					box.sideLength[5] = Math.max(box.sideLength[5], cube.max.x);
			}
			// TODO: clamp side to smaller than the non-custom render parts
		}
		for (int i = box.sideLength.length; i --> 0; )
			box.drawSide[i] = box.sideLength[i] > 0;
		return box;
	}

	public boolean canInterface(TileEntityRedNetCable with)
	{
		return !isNotValid();
	}

	public String getRedNetInfo(ForgeDirection side, EntityPlayer player) {
		// TODO: localize
		String o;
		if (side != ForgeDirection.UNKNOWN)
			o = "Side " + side + " is " + ItemRedNetMeter._colorNames[getSideColor(side)];
		else {
			o = "Sides are: ";
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				o += dir + ": " + ItemRedNetMeter._colorNames[getSideColor(dir)] + "; ";
		}
		return o;
	}

	@Override
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side, EntityPlayer player, boolean debug) {
		if (!debug) {
			info.add(text(getRedNetInfo(side, player)));
		} else { // debug
			if (_network != null) {
				info.add(text("Grid:" + _network));
				info.add(text("Conduits: " + _network.getConduitCount() + ", Nodes: " + _network.getNodeCount()));
				String o = "[";
				for (int i = 0; i < 15; ++i)
					o += _network.getPowerLevelOutput(i) + ",";
				o += _network.getPowerLevelOutput(15) + "]";
				info.add(text("Outputs: " + o));
			} else {
				info.add(text("Null Grid"));
			}
			info.add(text("ConnectionState: " + Arrays.toString(_connectionState)));
			info.add(text("SideMode: " + Arrays.toString(_cableMode)));
			info.add(text("Colors: " + Arrays.toString(_sideColors)));
			info.add(text("Upgrades: " + Arrays.toString(_sideUpgrade)));
			info.add(text("Node: " + isRSNode));
			return;
		}
	}

	public int getSideColorValue(ForgeDirection side)
	{
		return (MFRUtil.COLORS[getSideColor(side) & 15] << 8) | 0xFF;
	}

	public byte getMode(int side)
	{
		return _cableMode[side];
	}

	public void setMode(int side, byte mode)
	{
		boolean mustUpdate = (mode != _cableMode[side]);
		_cableMode[side] = mode;
		if (mustUpdate)
		{
			onNeighborBlockChange();
		}
	}

	@SideOnly(Side.CLIENT)
	public RedNetConnectionType getCachedConnectionState(ForgeDirection side)
	{
		return _connectionState[side.ordinal()];
	}

	public RedNetConnectionType getConnectionState(ForgeDirection side)
	{
		RedNetConnectionType type = getConnectionState(side, true);
		_connectionState[side.ordinal()] = type;
		return type;
	}

	protected RedNetConnectionType getConnectionState(ForgeDirection side, boolean decorative)
	{
		byte _mode = _cableMode[side.ordinal()];
		if (!decorative)
			_mode = 1;
		if (_cableMode[6] == 1)
			_mode = 3;

		int x, y, z;
		{
			BlockPosition bp = new BlockPosition(this);
			bp.orientation = side;
			bp.moveForwards(1);
			x = bp.x; y = bp.y; z = bp.z;
		}

		Block b = worldObj.getBlock(x, y, z);
		boolean node = false;

		// air - never connect
		if (b.isAir(worldObj, x, y, z))
		{
			return RedNetConnectionType.None;
		}
		// cables - always connect
		else if (b == MineFactoryReloadedCore.rednetCableBlock)
		{
			if (((TileEntityRedNetCable)worldObj.getTileEntity(x, y, z)).canInterface(this))
				return RedNetConnectionType.CableAll;
			else
				return RedNetConnectionType.None;
		}
		// cable-only, and not a cable - don't connect
		else if (_mode == 3)
		{
			return RedNetConnectionType.None;
		}

		short nodeFlags = -1;
		if (b instanceof IRedNetConnection) // API node - let them figure it out
		{
			RedNetConnectionType type = ((IRedNetConnection)b).
					getConnectionType(worldObj, x, y, z, side.getOpposite());
			if (_mode == 0 && type.isConnectionForced)
				return RedNetConnectionType.None;
			else if (!type.isDecorative)
				return type;
			node = true;
			nodeFlags = type.flags;
		}
		// Placed here so subclasses that are API nodes can override
		else if (b instanceof IRedNetNoConnection)
		{
			return RedNetConnectionType.None;
		}

		/**
		 * The else/if chain is broken here and no values are directly
		 * returned from the function below here for support of API nodes
		 * that want to use the standard connection logic. 
		 */
		RedNetConnectionType ret;

		// mode 2 forces cable mode for strong power
		if (_mode == 2)
		{
			if (b.isSideSolid(worldObj, x, y, z, side.getOpposite()))
				ret = RedNetConnectionType.ForcedCableSingle;
			else
				ret = RedNetConnectionType.ForcedPlateSingle; 
		}
		// mode 1 forces plate mode for weak power
		else if (_mode == 1)
		{
			ret = RedNetConnectionType.ForcedPlateSingle;
		}
		// standard connection logic, then figure out if we shouldn't connect
		// modes 1, 2, and 3 will skip this. _connectionBlackList is a HashMap
		else if (!node && (_connectionBlackList.contains(b) ||
					b instanceof IRedNetDecorative))
		{
			ret = RedNetConnectionType.None;
		}
		else if (b.isSideSolid(worldObj, x, y, z, side.getOpposite()))
		{
			ret = RedNetConnectionType.CableSingle;
		}
		else
		{
			ret = RedNetConnectionType.PlateSingle;
		}
		/**
		 * End of conditions.
		 */
		if (nodeFlags > 0)
		{
			short type = ret.flags;
			type &= ~24; // 24 represents the two connection type bits
			type |= nodeFlags;
			ret = RedNetConnectionType.fromFlags(type);
		}
		return ret;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setIntArray("sideSubnets", _sideColors);
		tag.setByte("v", (byte)3);
		tag.setByteArray("cableMode", _cableMode);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		_sideColors = tag.getIntArray("sideSubnets");
		if(_sideColors.length == 0)
		{
			_sideColors = new int[6];
		}
		byte _mode = tag.getByte("mode");
		_cableMode = tag.getByteArray("cableMode");
		if (_cableMode.length < 6) _cableMode = new byte[] {0,0,0, 0,0,0, 0};
		switch (tag.getByte("v"))
		{
		case 2:
			_cableMode[6] = (byte)(_cableMode[6] == 3 ? 1 : 0);
			break;
		case 0:
			if (_mode == 2)
				_mode = 3;
		case 1:
			_cableMode = new byte[] {_mode,_mode,_mode,
					_mode,_mode,_mode, (byte)(_mode == 3 ? 1 : 0)};
			break;
		default:
			break;
		}
	}

	public void setSideColor(ForgeDirection side, int color)
	{
		if(side == ForgeDirection.UNKNOWN)
		{
			return;
		}
		_sideColors[side.ordinal()] = color;
		//updateNetwork();
	}

	public int getSideColor(ForgeDirection side)
	{
		if(side == ForgeDirection.UNKNOWN)
		{
			return 0;
		}
		return _sideColors[side.ordinal()];
	}

	public boolean isSolidOnSide(int side)
	{
		return _cableMode[side] != 3 & _cableMode[6] != 1;
	}

    @Override
	public boolean shouldRenderInPass(int pass)
    {
        return false;
    }

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setIntArray("colors", _sideColors);
		data.setInteger("mode[0]", _cableMode[0] | (_cableMode[1] << 8) | (_cableMode[2] << 16) |
				(_cableMode[3] << 24));
		data.setInteger("mode[1]", _cableMode[4] | (_cableMode[5] << 8) | (_cableMode[6] << 16));
		data.setInteger("state[0]", _connectionState[0].ordinal() | (_connectionState[1].ordinal() << 4) |
				(_connectionState[2].ordinal() << 8) | (_connectionState[3].ordinal() << 12) |
				(_connectionState[4].ordinal() << 16) | (_connectionState[5].ordinal() << 20));
		S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound data = pkt.func_148857_g();
		switch (pkt.func_148853_f())
		{
		case 0:
			_sideColors = data.getIntArray("colors");
			int mode = data.getInteger("mode[0]");
			_cableMode[0] = (byte)((mode >>  0) & 0xFF);
			_cableMode[1] = (byte)((mode >>  8) & 0xFF);
			_cableMode[2] = (byte)((mode >> 16) & 0xFF);
			_cableMode[3] = (byte)((mode >> 24) & 0xFF);
			mode = data.getInteger("mode[1]");
			_cableMode[4] = (byte)((mode >>  0) & 0xFF);
			_cableMode[5] = (byte)((mode >>  8) & 0xFF);
			_cableMode[6] = (byte)((mode >> 16) & 0xFF);
			mode = data.getInteger("state[0]");
			_connectionState[0] = RedNetConnectionType.values()[(mode >>  0) & 0xF];
			_connectionState[1] = RedNetConnectionType.values()[(mode >>  4) & 0xF];
			_connectionState[2] = RedNetConnectionType.values()[(mode >>  8) & 0xF];
			_connectionState[3] = RedNetConnectionType.values()[(mode >> 12) & 0xF];
			_connectionState[4] = RedNetConnectionType.values()[(mode >> 16) & 0xF];
			_connectionState[5] = RedNetConnectionType.values()[(mode >> 20) & 0xF];
			break;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
