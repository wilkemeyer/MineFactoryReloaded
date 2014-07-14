package powercrystals.minefactoryreloaded.tile.transport;

import static powercrystals.minefactoryreloaded.block.BlockRedNetCable.subSelection;
import static powercrystals.minefactoryreloaded.tile.transport.FluidNetwork.TRANSFER_RATE;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Vector3;
import cofh.render.hitbox.CustomHitBox;
import cofh.render.hitbox.ICustomHitBox;
import cofh.util.CoreUtils;
import cofh.util.FluidHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import powercrystals.minefactoryreloaded.core.IGridController;
import powercrystals.minefactoryreloaded.core.INode;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

public class TileEntityPlasticPipe extends TileEntityBase implements INode, ICustomHitBox, IFluidHandler
{
	private byte[] sideMode = {1,1, 1,1,1,1, 0};
	private IFluidHandler[] handlerCache = null;
	private byte upgradeItem = 0;
	private boolean deadCache = true;
	private boolean isPowered = false;

	boolean isNode = false;
	FluidStack fluidForGrid = null;

	FluidNetwork _grid;
	
	public TileEntityPlasticPipe() {
	}

	@Override
	public void validate() {
		super.validate();
		deadCache = true;
		handlerCache = null;
		if (worldObj.isRemote)
			return;
		FluidNetwork.HANDLER.addConduitForTick(this);
	}

	@Override // cannot share mcp names
	public boolean isNotValid() {
		return tileEntityInvalid;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (_grid != null) {
			_grid.removeConduit(this);
			_grid.storage.drain(fluidForGrid, true);
			int c = 0;
			for (int i = 6; i --> 0; )
				if ((sideMode[i] >> 2) == 2)
					++c;
			if (c > 1)
				_grid.regenerate();
			deadCache = true;
			_grid = null;
		}
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public void onNeighborBlockChange() {
		boolean last = isPowered;
		switch (upgradeItem) {
		case 0:
			isPowered = CoreUtils.isRedstonePowered(this);
			break;
		case 1:
			isPowered = !CoreUtils.isRedstonePowered(this);
			break;
		case 2:
			isPowered = true;
		}
		if (last != isPowered)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private void reCache() {
		if (deadCache) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				onNeighborTileChange(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
			deadCache = false;
			// This method is only ever called from the same thread as the tick handler
			// so this method can be safely called *here* without worrying about threading
			updateInternalTypes(FluidNetwork.HANDLER);
		}
	}

	@Override
	public void firstTick(IGridController grid) {
		if (worldObj == null || worldObj.isRemote) return;
		if (grid != FluidNetwork.HANDLER) return;
		if (_grid == null) {
			incorporateTiles();
			if (_grid != null) {
				markDirty();
			}
		}
		if (_grid == null) {
			setGrid(new FluidNetwork(this));
			markDirty();
		}
		reCache();
		Packets.sendToAllPlayersWatching(this);
	}

	@Override
	public void onNeighborTileChange(int x, int y, int z) {
		if (worldObj.isRemote)
			return;
		TileEntity tile = worldObj.getTileEntity(x, y, z);

		if (x < xCoord)
			addCache(tile, 5);
		else if (x > xCoord)
			addCache(tile, 4);
		else if (z < zCoord)
			addCache(tile, 3);
		else if (z > zCoord)
			addCache(tile, 2);
		else if (y < yCoord)
			addCache(tile, 1);
		else if (y > yCoord)
			addCache(tile, 0);
	}

	private void addCache(TileEntity tile, int side) {
		if (handlerCache != null)
			handlerCache[side] = null;
		int lastMode = sideMode[side];
		sideMode[side] &= 3;
		if (tile instanceof TileEntityPlasticPipe) {
			sideMode[side] = (2 << 2);
			if (((TileEntityPlasticPipe)tile).canInterface(this)) {
				 sideMode[side] |= 1; // always enable
			}
		} else if (tile instanceof IFluidHandler) {
			//if (((IFluidHandler)tile).canFill(ForgeDirection.VALID_DIRECTIONS[side]))
			{
				if (handlerCache == null) handlerCache = new IFluidHandler[6];
				handlerCache[side] = (IFluidHandler)tile;
				sideMode[side] |= 1 << 2;
			}
		}
		if (!deadCache) {
			FluidNetwork.HANDLER.addConduitForUpdate(this);
			if (lastMode != sideMode[side])
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	private void incorporateTiles() {
		if (_grid != null) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				if (worldObj.blockExists(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
					TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
							yCoord + dir.offsetY, zCoord + dir.offsetZ);
					if (tile instanceof TileEntityPlasticPipe &&
							((TileEntityPlasticPipe)tile).canInterface(this))
						_grid.addConduit((TileEntityPlasticPipe)tile);
				}
			}
		} else for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (worldObj.blockExists(xCoord + dir.offsetX,
					yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
				TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (tile instanceof TileEntityPlasticPipe &&
						((TileEntityPlasticPipe)tile)._grid != null &&
						((TileEntityPlasticPipe)tile).canInterface(this)) {
					((TileEntityPlasticPipe)tile)._grid.addConduit(this);
					break;
				}
			}
		}
	}

	public boolean canInterface(TileEntityPlasticPipe te) {
		if (_grid != null && te._grid != null)
			if (_grid.storage.getFluid() == te._grid.storage.getFluid())
				return true;
			else
				return FluidHelper.isFluidEqual(_grid.storage.getFluid(), te._grid.storage.getFluid());
		return fluidForGrid == te.fluidForGrid || FluidHelper.isFluidEqual(fluidForGrid, te.fluidForGrid);
	}

	public boolean couldInterface(TileEntityPlasticPipe te) {
		if (_grid != null && te._grid != null)
			if (_grid.storage.getFluid() == te._grid.storage.getFluid())
				return true;
			else
				return FluidHelper.isFluidEqualOrNull(_grid.storage.getFluid(), te._grid.storage.getFluid());
		return fluidForGrid == te.fluidForGrid || FluidHelper.isFluidEqualOrNull(fluidForGrid, te.fluidForGrid);
	}

	public void mergeWith(TileEntityPlasticPipe te) {
		if (_grid != null && te._grid != null && couldInterface(te)) {
			te._grid.mergeGrid(_grid);
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		if (deadCache)
			return null;
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("mode[0]", sideMode[0] | (sideMode[1] << 8) | (sideMode[2] << 16) |
				(sideMode[3] << 24));
		data.setInteger("mode[1]", sideMode[4] | (sideMode[5] << 8) | (sideMode[6] << 16) |
				(isPowered ? 1 << 24 : 0));
		S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
		return packet;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound data = pkt.func_148857_g();
		switch (pkt.func_148853_f())
		{
		case 0:
			int mode = data.getInteger("mode[0]");
			sideMode[0] = (byte)((mode >>  0) & 0xFF);
			sideMode[1] = (byte)((mode >>  8) & 0xFF);
			sideMode[2] = (byte)((mode >> 16) & 0xFF);
			sideMode[3] = (byte)((mode >> 24) & 0xFF);
			mode = data.getInteger("mode[1]");
			sideMode[4] = (byte)((mode >>  0) & 0xFF);
			sideMode[5] = (byte)((mode >>  8) & 0xFF);
			sideMode[6] = (byte)((mode >> 16) & 0xFF);
			isPowered = (mode >> 24) > 0;
			break;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void setUpgrade(int i) {
		upgradeItem = (byte)i;
	}
	
	public int getUpgrade() {
		return upgradeItem;
	}

	@SideOnly(Side.CLIENT)
	public void setModes(byte[] modes) {
		sideMode = modes;
	}
	
	public byte getMode(int side) {
		return (byte) (sideMode[ForgeDirection.OPPOSITES[side]] & 3);
	}
	
	public void setMode(int side, byte mode) {
		side = ForgeDirection.OPPOSITES[side];
		mode &= 3;
		int t = sideMode[side];
		boolean mustUpdate = (mode != (t & 3));
		sideMode[side] = (byte) ((t & ~3) | mode);
		if (mustUpdate)
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	// IFluidHandler

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (_grid == null | sideMode[6] == 1) return 0;
		int t = sideMode[from.ordinal()];
		if ((t & 1) != 0 & isPowered & (t & 2) == 2)
		{
			return _grid.storage.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (_grid == null | sideMode[6] == 1) return null;
		int t = sideMode[from.ordinal()];
		if ((t & 1) != 0 & (t & 2) == 0)
		{
			return _grid.storage.drain(resource, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (_grid == null | sideMode[6] == 1) return null;
		int t = sideMode[from.ordinal()];
		if ((t & 1) != 0 & (t & 2) == 0)
			return _grid.storage.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (sideMode[6] == 1) return false;
		int t = sideMode[from.ordinal()];
		return (t & 1) != 0 & (t & 2) == 2;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (sideMode[6] == 1) return false;
		int t = sideMode[from.ordinal()];
		return (t & 1) != 0 & (t & 2) == 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if (_grid == null)
			return null;
		return new FluidTankInfo[] {_grid.storage.getInfo()};
	}

	// internal

	public boolean isInterfacing(ForgeDirection to) {
		int bSide = to.getOpposite().ordinal();
		int mode = sideMode[bSide] >> 2;
		return ((sideMode[bSide] & 1) != 0) & (sideMode[6] == 1 ? mode == 2 : mode != 0);
	}

	public int interfaceMode(ForgeDirection to) {
		int bSide = to.getOpposite().ordinal();
		int mode = sideMode[bSide] >> 2;
		return (sideMode[bSide] & 1) != 0 ? mode : 0;
	}
	
	public boolean isPowered() {
		return isPowered;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		upgradeItem = nbt.getByte("Upgrade");
		sideMode = nbt.getByteArray("SideMode");
		if (sideMode.length != 7)
			sideMode = new byte[]{1,1, 1,1,1,1, 0};
		if (nbt.hasKey("Fluid"))
			fluidForGrid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("Fluid"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("Upgrade", upgradeItem);
		nbt.setByteArray("SideMode", sideMode);
		if (_grid != null) {
			if (isNode) {
				fluidForGrid = _grid.storage.drain(_grid.getNodeShare(this), false);
			} else {
				fluidForGrid = _grid.storage.drain(0, false);
			}
			if (fluidForGrid != null)
				nbt.setTag("Fluid", fluidForGrid.writeToNBT(new NBTTagCompound()));
		} else if (fluidForGrid != null)
			nbt.setTag("Fluid", fluidForGrid.writeToNBT(new NBTTagCompound()));
		else
			fluidForGrid = null;
	}

	void extract(ForgeDirection side, IFluidTank tank) {
		if (deadCache) return;
		int bSide = side.ordinal();
		int m = sideMode[bSide];
		if (isPowered & ((m & 1) != 0) & (m & 2) == 2) {
			switch (m >> 2) {
			case 1: // IFluidHandler
				if (handlerCache != null) {
					IFluidHandler handlerTile = handlerCache[bSide];
					if (handlerTile != null && handlerTile.canDrain(side, null))
					{
						FluidStack e = handlerTile.drain(side, TRANSFER_RATE, false);
						if (e != null && e.amount > 0)
							handlerTile.drain(side, tank.fill(e, true), true);
					}
				}
				break;
			case 2: // TileEntityPlasticPipe
				break;
			case 0: // no mode
				// no-op
				break;
			}
		}
	}

	int transfer(ForgeDirection side, FluidStack fluid) {
		if (deadCache) return 0;
		int bSide = side.ordinal();
		int m = sideMode[bSide];
		if (((m & 1) != 0) & (m & 2) == 0) {
			switch (m >> 2) {
			case 1: // IFluidHandler
				if (handlerCache != null) {
					IFluidHandler handlerTile = handlerCache[bSide];
					if (handlerTile != null && handlerTile.canFill(side, null))
						return handlerTile.fill(side, fluid, true);
				}
				break;
			case 2: // TileEntityRednetCable
			case 0: // no mode
				// no-op
				break;
			}
		}
		return 0;
	}

	public void setGrid(FluidNetwork newGrid) {
		_grid = newGrid;
		if (_grid != null && !_grid.isRegenerating())
			incorporateTiles();
	}

	@Override
	public void updateInternalTypes(IGridController grid) {
		if (deadCache) return;
		if (grid != FluidNetwork.HANDLER) return;
		isNode = false;
		for (int i = 0; i < 6; i++) {
			int mode = sideMode[i] >> 2;
			if (((sideMode[i] & 1) != 0) & (mode != 0) & (mode != 2)) {
				isNode = true;
			}
		}
		if (_grid != null)
			_grid.addConduit(this);
		markDirty();
		Packets.sendToAllPlayersWatching(this);
	}

	public boolean onPartHit(EntityPlayer player, int side, int subHit) {
		return false;
	}

	@Override
	public boolean shouldRenderCustomHitBox(int subHit, EntityPlayer player) {
		return subHit < 2;
	}

	@Override
	public CustomHitBox getCustomHitBox(int hit, EntityPlayer player) {
		final List<IndexedCuboid6> list = new ArrayList<IndexedCuboid6>(7);
		addTraceableCuboids(list, true, false);
		IndexedCuboid6 cube = list.get(0);
		cube.expand(0.003);
		Vector3 min = cube.min, max = cube.max.sub(min);
		CustomHitBox box = new CustomHitBox(max.x, max.y, max.z, min.x, min.y, min.z);
		for (int i = 1, e = list.size(); i < e; ++i) {
			cube = list.get(i);
			if (shouldRenderCustomHitBox((Integer)cube.data, player)) {
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
		}
		for (int i = box.sideLength.length; i --> 0; )
			box.drawSide[i] = box.sideLength[i] > 0;
		return box;
	}

	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean forDraw) {
		Vector3 offset = new Vector3(xCoord, yCoord, zCoord);

		IndexedCuboid6 main = new IndexedCuboid6(0, subSelection[0]); // main body
		list.add(main);

		ForgeDirection[] side = ForgeDirection.VALID_DIRECTIONS;
		int[] opposite = ForgeDirection.OPPOSITES;
		boolean cableMode = sideMode[6] == 1;
		for (int i = side.length; i --> 0; ) {
			int mode = sideMode[opposite[i]] >> 2;
			boolean iface = (mode > 0) & mode != 2;
			int o = 2 + i;
			if (((sideMode[opposite[i]] & 1) != 0) & mode > 0) {
				if (mode == 2) {
					if (forDraw)
						main.setSide(i, i & 1);
					else {
						o = 2 + 6*3 + i;
						list.add((IndexedCuboid6)new IndexedCuboid6(1,
								subSelection[o]).setSide(i, i & 1).add(offset)); // cable part
					}
					continue;
				}
				if (cableMode) continue;
				list.add((IndexedCuboid6)new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point
				o = 2 + 6*3 + i;
				list.add((IndexedCuboid6)new IndexedCuboid6(1, subSelection[o]).add(offset)); // cable part
			}
			else if (forTrace & !cableMode & iface) {
				list.add((IndexedCuboid6)new IndexedCuboid6(o, subSelection[o]).add(offset)); // connection point (raytrace)
			}
		}
		main.add(offset);
	}

	public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug) {
		if (_grid != null) {/* TODO: advanced monitoring
			if (isNode) {
				info.add("Throughput All: " + _grid.distribution);
				info.add("Throughput Side: " + _grid.distributionSide);
			} else//*/
			if (!debug)
				info.add("Saturation: " +
					(Math.ceil(_grid.storage.getFluidAmount() /
							(float)_grid.storage.getCapacity() * 1000) / 10f));
		} else if (!debug)
			info.add("Null Grid");
		if (debug) {
			if (_grid != null) {
				info.add("Grid:" + _grid);
				info.add("Conduits: " + _grid.getConduitCount() + ", Nodes: " + _grid.getNodeCount());
				info.add("Grid Max: " + _grid.storage.getCapacity() + ", Grid Cur: " +
						_grid.storage.getFluidAmount());
				info.add("Cache: (" + Arrays.toString(handlerCache) + ")");
			} else {
				info.add("Null Grid");
			}
			info.add("SideType: " + Arrays.toString(sideMode));
			info.add("Node: " + isNode);
			return;
		}
	}
}
