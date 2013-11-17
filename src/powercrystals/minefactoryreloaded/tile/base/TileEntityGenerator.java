package powercrystals.minefactoryreloaded.tile.base;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeTile.PipeType;

import cofh.api.energy.IEnergyHandler;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityGenerator extends TileEntityFactoryInventory
										implements IPowerEmitter, IEnergyHandler
{
	protected TileEntityGenerator(Machine machine)
	{
		super(machine);
	}
	
	protected final int producePower(int energy)
	{
		BlockPosition ourbp = BlockPosition.fromFactoryTile(this);
		
		TileEntity[] tiles = new TileEntity[6];
		
		for (BlockPosition bp : ourbp.getAdjacent(true))
			tiles[bp.orientation.getOpposite().ordinal()] = worldObj.getBlockTileEntity(bp.x, bp.y, bp.z);
		
		for (int i = tiles.length; i --> 0; )
		{
			TileEntity te = tiles[i];
			if (te == null || !(te instanceof IEnergyHandler))
				continue;
			
			IEnergyHandler tile = (IEnergyHandler)te;
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

		for (int i = tiles.length; i --> 0; )
		{
			TileEntity te = tiles[i];
			if (te == null || !(te instanceof IPowerReceptor))
				continue;
			
			IPowerReceptor ipr = (IPowerReceptor)te;
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
