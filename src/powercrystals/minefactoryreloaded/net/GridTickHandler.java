package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class GridTickHandler implements IScheduledTickHandler
{
	private EnumSet<TickType> ticks = EnumSet.of(TickType.SERVER);
	
	public static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToRegenerate = new LinkedHashSet<RedstoneEnergyNetwork>();
	public static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToAdd = new LinkedHashSet<RedstoneEnergyNetwork>();
	LinkedHashSet<RedstoneEnergyNetwork> tickingGrids = new LinkedHashSet<RedstoneEnergyNetwork>();
	public static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToRemove = new LinkedHashSet<RedstoneEnergyNetwork>();
	LinkedHashSet<TileEntityRedNetEnergy> conduit = new LinkedHashSet<TileEntityRedNetEnergy>();
	public static LinkedHashSet<TileEntityRedNetEnergy> conduitToAdd = new LinkedHashSet<TileEntityRedNetEnergy>();
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (!tickingGridsToRegenerate.isEmpty())
			synchronized (tickingGridsToRegenerate) {
			for (RedstoneEnergyNetwork grid : tickingGridsToRegenerate)
				grid.markSweep();
		}
				
		if (!tickingGrids.isEmpty())
			for (RedstoneEnergyNetwork grid : tickingGrids)
				grid.doGridPreUpdate();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (!tickingGridsToAdd.isEmpty())
		synchronized(tickingGridsToAdd)
		{
			tickingGrids.addAll(tickingGridsToAdd);
			tickingGridsToAdd.clear();
		}
		if (!tickingGridsToRemove.isEmpty())
		synchronized(tickingGridsToRemove)
		{
			tickingGrids.removeAll(tickingGridsToRemove);
			tickingGridsToRemove.clear();
		}
		
		if (!tickingGrids.isEmpty())
			for (RedstoneEnergyNetwork grid : tickingGrids)
				grid.doGridUpdate();
		
		if (!conduitToAdd.isEmpty())
		synchronized(conduitToAdd)
		{
			conduit.addAll(conduitToAdd);
			conduitToAdd.clear();
		}
		if (!conduit.isEmpty())
		{
			for (TileEntityRedNetEnergy cond : conduit)
				cond.firstTick();
			conduit.clear();
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return ticks;
	}

	@Override
	public String getLabel()
	{
		return "MFR EnergyNet";
	}

	@Override
	public int nextTickSpacing()
	{
		return 1;
	}
}
