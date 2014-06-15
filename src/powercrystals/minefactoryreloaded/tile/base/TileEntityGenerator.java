package powercrystals.minefactoryreloaded.tile.base;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.util.CoreUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityGenerator extends TileEntityFactoryInventory
										implements IEnergyConnection
{
	private int _ticksBetweenConsumption;
	private int _outputPulseSize;
	
	private int _ticksSinceLastConsumption = 0;
	private int _bufferMax;
	private int _buffer;

	private IEnergyHandler[] handlerCache;
	private boolean deadCache;
	
	protected TileEntityGenerator(Machine machine, int ticksBetweenConsumption)
	{
		super(machine);
		assert machine.getActivationEnergy() > 0 : "Generators cannot produce 0 energy.";
		_ticksBetweenConsumption = ticksBetweenConsumption;
		_outputPulseSize = machine.getActivationEnergy();
		_bufferMax = machine.getMaxEnergyStorage();
	}

	@Override
	public void validate()
	{
		super.validate();
		deadCache = true;
		handlerCache = null;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!worldObj.isRemote)
		{
			if (deadCache) reCache();
			setIsActive(_buffer > _outputPulseSize * 2);
			
			boolean skipConsumption = ++_ticksSinceLastConsumption < _ticksBetweenConsumption;
			
			if (CoreUtils.isRedstonePowered(this))
				return;
			
			int pulse = Math.min(_buffer, _outputPulseSize);
			_buffer -= pulse;
			_buffer += transmitEnergy(pulse);
			
			if (skipConsumption || !canConsumeFuel(_bufferMax - _buffer))
				return;
			
			if (consumeFuel())
			{
				_buffer += produceEnergy();
				_ticksSinceLastConsumption = 0;
			}
		}
	}
	
	protected abstract boolean canConsumeFuel(int space);
	protected abstract boolean consumeFuel();
	protected abstract int produceEnergy();
	
	protected final int transmitEnergy(int energy)
	{
		if (handlerCache != null)
			for (int i = handlerCache.length; i --> 0; )
			{
				IEnergyHandler tile = handlerCache[i];
				if (tile == null)
					continue;

				ForgeDirection from = ForgeDirection.VALID_DIRECTIONS[i];
				if (tile.receiveEnergy(from, energy, true) > 0)
					energy -= tile.receiveEnergy(from, energy, false);
				if (energy <= 0)
					return 0;
			}

		return energy;
	}
	
	public int getBuffer()
	{
		return _buffer;
	}
	
	public void setBuffer(int buffer)
	{
		_buffer = buffer;
	}
	
	public int getBufferMax()
	{
		return _bufferMax;
	}
	
	private void reCache() {
		if (deadCache) {
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				onNeighborTileChange(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
			deadCache = false;
		}
	}

	@Override
	public void onNeighborTileChange(int x, int y, int z)
	{
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

	private void addCache(TileEntity tile, int side)
	{
		if (handlerCache != null)
			handlerCache[side] = null;

		if (tile instanceof IEnergyHandler)
		{
			if (((IEnergyHandler)tile).canConnectEnergy(ForgeDirection.VALID_DIRECTIONS[side]))
			{
				if (handlerCache == null) handlerCache = new IEnergyHandler[6];
				handlerCache[side] = (IEnergyHandler)tile;
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		if (_ticksSinceLastConsumption > 0)
			nbttagcompound.setInteger("ticksSinceLastConsumption", _ticksSinceLastConsumption);
		if (_buffer > 0)
			nbttagcompound.setInteger("buffer", _buffer);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		
		_ticksSinceLastConsumption = nbttagcompound.getInteger("ticksSinceLastConsumption");
		_buffer = nbttagcompound.getInteger("buffer");
	}

	// TE methods

	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}
}
