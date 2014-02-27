package powercrystals.minefactoryreloaded.tile.rednet;

import static powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered.energyPerEU;
import static powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered.energyPerMJ;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.net.GridTickHandler;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler;

public class TileEntityRedNetEnergy extends TileEntityRedNetCable implements
						IPowerEmitter, IEnergySink, IEnergyHandler//, IEnergyInfo
{
	private byte[] sideMode = {1,1, 1,1,1,1, 0};
	private IEnergyHandler[] handlerCache = null;
	private IPowerReceptor[] receiverCache = null;
	private boolean deadCache = false;
	
	int energyForGrid = 0;
	boolean isNode = false;
	
	private RedstoneEnergyNetwork grid;
	
	public TileEntityRedNetEnergy() {}
	
	@Override
	public void validate() {
		super.validate();
		deadCache = true;
		handlerCache = null;
		receiverCache = null;
		GridTickHandler.conduitToAdd.add(this);
	}

	public void firstTick() {
		if (worldObj == null) return;
		if (deadCache) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				onNeighborTileChange(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
			deadCache = false;
			ServerPacketHandler.sendToAllPlayersWatching(worldObj, xCoord, yCoord, zCoord, getDescriptionPacket());
		}
	}
	
	@Override
	public void onNeighborTileChange(int x, int y, int z) {
		TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
		
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
		if (receiverCache != null)
			receiverCache[side] = null;
		sideMode[side] &= 1;
		if (tile instanceof TileEntityRedNetCable) {
			sideMode[side] = (4 << 1) | 1; // always enable
		} else if (tile instanceof IEnergyHandler) {
			if (((IEnergyHandler)tile).canInterface(ForgeDirection.VALID_DIRECTIONS[side].getOpposite())) {
				if (handlerCache == null) handlerCache = new IEnergyHandler[6];
				handlerCache[side] = (IEnergyHandler)tile;
				sideMode[side] |= 1 << 1;
			}
		} else if (tile instanceof IPowerReceptor) {
			if (((IPowerReceptor)tile).
					getPowerReceiver(ForgeDirection.VALID_DIRECTIONS[side].getOpposite()).
					getMaxEnergyReceived() > 0) {
				if (receiverCache == null) receiverCache = new IPowerReceptor[6];
				receiverCache[side] = (IPowerReceptor)tile;
				sideMode[side] |= 2 << 1;
			}
		} else if (tile instanceof IEnergyEmitter) {
			sideMode[side] |= 3 << 1;
		}
		updateInternalTypes();
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		if (deadCache)
			return null;
		return PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel,
				Packets.EnergyCableDescription, new Object[]
				{
					xCoord, yCoord, zCoord,
					_sideColors[0], _sideColors[1], _sideColors[2],
					_sideColors[3], _sideColors[4], _sideColors[5],
					_mode, sideMode[0], sideMode[1], sideMode[2], 
					sideMode[3], sideMode[4], sideMode[5] 
				});
	}
	
	@SideOnly(Side.CLIENT)
	public void setModes(byte[] modes)
	{
		sideMode = modes;
	}
	
	// IEnergyHandler

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if ((sideMode[from.ordinal()] & 1) != 0 & grid != null)
			return grid.storage.receiveEnergy(maxReceive, simulate);
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return (sideMode[from.ordinal()] & 1) != 0 & grid != null;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if ((sideMode[from.ordinal()] & 1) != 0 & grid != null)
			return grid.storage.getEnergyStored();
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if ((sideMode[from.ordinal()] & 1) != 0 & grid != null)
			return grid.storage.getMaxEnergyStored();
		return 0;
	}
	
	// IPowerEmitter

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return canInterface(side);
	}
	
	// IEnergySink

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return canInterface(direction);
	}

	@Override
	public double demandedEnergyUnits() {
		return RedstoneEnergyNetwork.TRANSFER_RATE / energyPerEU;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection from, double amount) {
		if ((sideMode[from.ordinal()] & 1) != 0) {
			int r = (int)(amount * energyPerEU);
			return amount - grid.storage.receiveEnergy(r, false);
		}
		return amount;
	}

	@Override
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}
	
	// internal
	
	public boolean isInterfacing(ForgeDirection from) {
		int bSide = from.ordinal();
		int mode = sideMode[bSide] >> 1;
		return (sideMode[bSide] & 1) != 0 & mode != 0 & (mode == 4 | grid != null);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt);
		energyForGrid = nbt.getInteger("Energy");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt);
		if (grid != null) {
			if (isNode) {
				energyForGrid = grid.getNodeShare(this);
				nbt.setInteger("Energy", energyForGrid);
			}
		} else if (energyForGrid > 0)
			nbt.setInteger("Energy", energyForGrid);
		else
			energyForGrid = 0;
	}
	
	@Override
	public void onChunkUnload() { super.onChunkUnload();
		if (grid != null) {
			grid.storage.extractEnergy(energyForGrid, false);
		}
	}
	
	int transfer(ForgeDirection side, int energy) {
		int bSide = side.getOpposite().ordinal();
		if ((sideMode[bSide] & 1) != 0) {
			switch (sideMode[bSide] >> 1)
			{
			case 1: // IEnergyHandler
				IEnergyHandler handlerTile = handlerCache[bSide];
				if (handlerTile != null)
					return handlerTile.receiveEnergy(side, energy, false);
				break;
			case 2: // IPowerReceptor
				IPowerReceptor receiverTile = receiverCache[bSide];
				PowerReceiver pp = null;
				if (receiverTile != null)
					pp = receiverTile.getPowerReceiver(side);
				
				if (pp != null) {
					float max = pp.getMaxEnergyReceived();
					float powerToSend = Math.min(max, pp.getMaxEnergyStored() - pp.getEnergyStored());
					if (powerToSend > 0) {
						powerToSend = Math.min(energy / (float)energyPerMJ, powerToSend);
						return (int)(Math.ceil(pp.receiveEnergy(Type.PIPE, powerToSend, side)) *
								energyPerMJ);
					}
				}
				break;
			case 3: // IEnergyTile
				// sending cannot happen in-line presently
				break;
			case 4: // TileEntityRednetCable
				// no-op
				break;
			}
		}
		return 0;
	}

	void setGrid(RedstoneEnergyNetwork newGrid) {
		grid = newGrid;
	}
	
	public void updateInternalTypes() {
		boolean prevNode = isNode;
		isNode = false;
		for (int i = 0; i < 6; i++) {
			if ((sideMode[i] & 1) != 0 & (sideMode[i] & ~1) != 0) {
				isNode = true;
			}
		}
		if (prevNode != isNode & grid != null)
			grid.addConduit(this);
	}
}
