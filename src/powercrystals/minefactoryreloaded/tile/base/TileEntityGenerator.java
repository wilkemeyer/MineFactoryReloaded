package powercrystals.minefactoryreloaded.tile.base;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeTile.PipeType;

import cofh.api.energy.IEnergyHandler;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityGenerator extends TileEntityFactoryInventory
										implements IPowerEmitter, IEnergyHandler
{
	protected TileEntityGenerator(Machine machine)
	{
		super(machine);
	}
	
	private IEnergyHandler[] energyHandlerCache;
	private IPowerReceptor[] powerReceptorCache;
	private boolean deadCache;
	
	@Override
	public void validate()
	{
		super.validate();
		deadCache = true;
		energyHandlerCache = new IEnergyHandler[6];
		powerReceptorCache = new IPowerReceptor[6];
	}
	
	@Override
	public void updateEntity()
	{
		if (deadCache)
		{
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
				onNeighborTileChange(xCoord + dir.offsetX,
						yCoord + dir.offsetY, zCoord + dir.offsetZ);
			deadCache = false;
		}
		super.updateEntity();
	}
	
	@Override
	public void onNeighborTileChange(int x, int y, int z)
	{
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
	
	private void addCache(TileEntity tile, int side)
	{
		if (tile instanceof IEnergyHandler)
			energyHandlerCache[side] = (IEnergyHandler)tile;
		else if (tile instanceof IPowerReceptor)
			powerReceptorCache[side] = (IPowerReceptor)tile;
		else
		{
			energyHandlerCache[side] = null;
			powerReceptorCache[side] = null;
		}
	}
	
	protected final int producePower(int energy)
	{
		for (int i = energyHandlerCache.length; i --> 0; )
		{
			IEnergyHandler tile = energyHandlerCache[i];
			if (tile == null)
				continue;
			
			ForgeDirection from = ForgeDirection.VALID_DIRECTIONS[i];
			if (tile.canInterface(from))
			{
				if (tile.receiveEnergy(from, energy, true) > 0)
					energy -= tile.receiveEnergy(from, energy, false);
				if (energy <= 0)
					return 0;
			}
		}
		
		float mjS = energy / (float)TileEntityFactoryPowered.energyPerMJ, mj = mjS;

		for (int i = powerReceptorCache.length; i --> 0; )
		{
			IPowerReceptor ipr = powerReceptorCache[i];
			if (ipr == null)
				continue;
			
			ForgeDirection from = ForgeDirection.VALID_DIRECTIONS[i];
			PowerReceiver pp = ipr.getPowerReceiver(from);
			float max;
			if(pp != null && Math.min((max = pp.getMaxEnergyReceived()), 
				pp.getMaxEnergyStored() - pp.getEnergyStored()) > 0)
			{
				float mjUsed = Math.min(Math.min(max, mj),
						pp.getMaxEnergyStored() - pp.getEnergyStored());
				pp.receiveEnergy(PowerHandler.Type.GATE, mjUsed, from);
				
				mj -= mjUsed;
				if(mj <= 0)
				{
					return 0;
				}
			}
		}
		
		energy -= mjS - mj;
		
		return energy;
	}

	// TE methods
	
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean doExtract)
	{
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from)
	{
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return 0;
	}

    @Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return 0;
	}
    
    // BC methods

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with)
	{
		if (type == PipeType.POWER)
			return ConnectOverride.CONNECT;
		return super.overridePipeConnection(type, with);
	}

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side)
	{
		return true;
	}
}
