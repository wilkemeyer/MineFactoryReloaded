package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.Iterator;
import java.util.LinkedHashSet;

import powercrystals.minefactoryreloaded.tile.rednet.RedstoneEnergyNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class GridTickHandler
{
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToRegenerate =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToAdd =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGrids =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	private static LinkedHashSet<RedstoneEnergyNetwork> tickingGridsToRemove =
			new LinkedHashSet<RedstoneEnergyNetwork>();
	
	private static LinkedHashSet<TileEntityRedNetEnergy> conduit =
			new LinkedHashSet<TileEntityRedNetEnergy>();
	private static LinkedHashSet<TileEntityRedNetEnergy> conduitToAdd =
			new LinkedHashSet<TileEntityRedNetEnergy>();
	private static LinkedHashSet<TileEntityRedNetEnergy> conduitToUpd =
			new LinkedHashSet<TileEntityRedNetEnergy>();
	
	static GridTickHandler instance = new GridTickHandler();
	
	public static void addGrid(RedstoneEnergyNetwork grid)
	{
		tickingGridsToAdd.add(grid);
		tickingGridsToRemove.remove(grid);
	}
	
	public static void removeGrid(RedstoneEnergyNetwork grid)
	{
		tickingGridsToRemove.add(grid);
		tickingGridsToAdd.remove(grid);
	}
	
	public static void regenerateGrid(RedstoneEnergyNetwork grid)
	{
		tickingGridsToRegenerate.add(grid);
	}
	
	public static boolean isGridTicking(RedstoneEnergyNetwork grid)
	{
		return tickingGrids.contains(grid);
	}
	
	public static void addConduitForTick(TileEntityRedNetEnergy grid)
	{
		conduitToAdd.add(grid);
	}
	
	public static void addConduitForUpdate(TileEntityRedNetEnergy grid)
	{
		conduitToUpd.add(grid);
	}

	@SubscribeEvent
	public void tick(ServerTickEvent evt)
	{
		// TODO: this needs split up into groups per-world when worlds are threaded
		if (evt.phase == Phase.START)
			tickStart();
		else
			tickEnd();
	}

	public void tickStart()
	{
		//{ Grids that have had significant conduits removed and need to rebuild/split 
		if (!tickingGridsToRegenerate.isEmpty())
		synchronized (tickingGridsToRegenerate) {
			for (RedstoneEnergyNetwork grid : tickingGridsToRegenerate)
				grid.markSweep();
		}
		//}
		
		//{ Updating internal types of conduits
		// this pass is needed to handle issues with threading
		if (!conduitToUpd.isEmpty())
		synchronized (conduitToUpd) {
			conduit.addAll(conduitToUpd);
			conduitToUpd.clear();
		}
		
		if (!conduit.isEmpty())
		{
			TileEntityRedNetEnergy cond = null;
			try {
				Iterator<TileEntityRedNetEnergy> iter = conduit.iterator();
				while (iter.hasNext())
				{
					cond = iter.next();
					if (!cond.isInvalid())
						cond.updateInternalTypes();
				}
				conduit.clear();
			} catch(Throwable _) {
				throw new RuntimeException("Crashing on conduit " + cond, _);
			}
		}
		//}
		
		//{ Early update pass to extract energy from sources
		if (!tickingGrids.isEmpty())
			for (RedstoneEnergyNetwork grid : tickingGrids)
				grid.doGridPreUpdate();
		//}
	}

	public void tickEnd()
	{
		//{ Changes in what grids are being ticked
		if (!tickingGridsToRemove.isEmpty())
		synchronized(tickingGridsToRemove)
		{
			tickingGrids.removeAll(tickingGridsToRemove);
			tickingGridsToRemove.clear();
		}
		
		if (!tickingGridsToAdd.isEmpty())
		synchronized(tickingGridsToAdd)
		{
			tickingGrids.addAll(tickingGridsToAdd);
			tickingGridsToAdd.clear();
		}
		//}
		
		//{ Ticking grids to transfer energy/etc.
		if (!tickingGrids.isEmpty())
			for (RedstoneEnergyNetwork grid : tickingGrids)
				grid.doGridUpdate();
		//}
		
		
		//{ Initial update tick for conduits added to the world
		if (!conduitToAdd.isEmpty())
		synchronized(conduitToAdd)
		{
			conduit.addAll(conduitToAdd);
			conduitToAdd.clear();
		}
		
		if (!conduit.isEmpty())
		{
			TileEntityRedNetEnergy cond = null;
			try {
				Iterator<TileEntityRedNetEnergy> iter = conduit.iterator();
				while (iter.hasNext())
				{
					cond = iter.next();
					if (!cond.isInvalid())
						cond.firstTick();
				}
				conduit.clear();
			} catch(Throwable _) {
				throw new RuntimeException("Crashing on conduit " + cond, _);
			}
		}
		//}
	}
}
