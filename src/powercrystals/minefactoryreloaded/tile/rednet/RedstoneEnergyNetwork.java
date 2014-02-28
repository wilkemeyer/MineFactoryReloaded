package powercrystals.minefactoryreloaded.tile.rednet;

import cofh.api.energy.EnergyStorage;

import java.util.LinkedHashSet;

import net.minecraftforge.common.ForgeDirection;

import powercrystals.minefactoryreloaded.net.GridTickHandler;

public class RedstoneEnergyNetwork
{
	public static final int TRANSFER_RATE = 1000;
	public static final int STORAGE = TRANSFER_RATE * 6;

	private LinkedHashSet<TileEntityRedNetEnergy> nodeSet = new LinkedHashSet<TileEntityRedNetEnergy>();
	private LinkedHashSet<TileEntityRedNetEnergy> conduitSet = new LinkedHashSet<TileEntityRedNetEnergy>();
	private TileEntityRedNetEnergy master;
	private boolean regenerating = false;
	EnergyStorage storage = new EnergyStorage(480, 80);

	public RedstoneEnergyNetwork(TileEntityRedNetEnergy base) {
		storage.setCapacity(STORAGE);
		storage.setMaxTransfer(TRANSFER_RATE);
		addConduit(base);
	}

	public int getNodeShare(TileEntityRedNetEnergy cond) {
		int size = nodeSet.size();
		if (size == 1)
			return storage.getEnergyStored();
		int amt = 0;
		if (master == cond) amt = storage.getEnergyStored() % size;
		return amt + storage.getEnergyStored() / size;
	}

	public void addConduit(TileEntityRedNetEnergy cond) {
		if (conduitSet.add(cond))
			conduitAdded(cond);
		if (cond.isNode) {
			if (nodeSet.add(cond)) {
				if (nodeSet.size() == 1) {
					master = cond;
					GridTickHandler.tickingGridsToAdd.add(this);
				}
				rebalanceGrid();
				nodeAdded(cond);
			}
		} else if (!nodeSet.isEmpty()) {
			int share = getNodeShare(cond);
			if (nodeSet.remove(cond)) {
				cond.energyForGrid = storage.extractEnergy(share, false);
				nodeRemoved(cond);
			}
		}
	}

	public void removeConduit(TileEntityRedNetEnergy cond) {
		conduitSet.remove(cond);
		if (!nodeSet.isEmpty()) {
			int share = getNodeShare(cond);
			if (nodeSet.remove(cond)) {
				cond.energyForGrid = storage.extractEnergy(share, false);
				nodeRemoved(cond);
			}
		}
	}
	
	public void regenerate() {
		regenerating = true;
		GridTickHandler.tickingGridsToRegenerate.add(this);
	}
	
	public void markSweep() {
		for (TileEntityRedNetEnergy curCond : nodeSet) {
			destroyNode(curCond);
		}
		for (TileEntityRedNetEnergy curCond : conduitSet) {
			destroyConduit(curCond);
		}
		
		for (TileEntityRedNetEnergy curCond : conduitSet) {
			curCond.validate();
		}
	}

	public void destroyNode(TileEntityRedNetEnergy cond) {
		cond.energyForGrid = getNodeShare(cond);
		cond.setGrid(null);
	}

	public void destroyConduit(TileEntityRedNetEnergy cond) {
		cond.setGrid(null);
	}

	public void doGridPreUpdate()
	{
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			GridTickHandler.tickingGridsToRemove.add(this);
			return;
		}
		ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
		
		for (TileEntityRedNetEnergy cond : nodeSet)
			for (int i = 6; i --> 0; )
				cond.extract(directions[i]);
	}

	public void doGridUpdate() {
		if (regenerating)
			return;
		if (nodeSet.isEmpty()) {
			GridTickHandler.tickingGridsToRemove.add(this);
			return;
		}
		ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
		int size = nodeSet.size();
		int toDistribute = storage.getEnergyStored() / size;
		int sideDistribute = toDistribute / 6;
		
		if (sideDistribute > 0) for (TileEntityRedNetEnergy cond : nodeSet)
			if (cond != master)
				for (int i = 6; i --> 0; ) {
					int e = cond.transfer(directions[i], sideDistribute);
					if (e > 0) storage.extractEnergy(e, false);
				}
		
		toDistribute = storage.getEnergyStored() % size;
		sideDistribute = toDistribute / 6;
		
		if (sideDistribute > 0) for (int i = 6; i --> 0; ) {
			int e = master.transfer(directions[i], sideDistribute);
			if (e > 0) storage.extractEnergy(e, false);
		}
	}

	public boolean canGridMerge(RedstoneEnergyNetwork theNewGrid) {
		return true;
	}

	public void mergeGrid(RedstoneEnergyNetwork theGrid) {
		for (TileEntityRedNetEnergy cond : theGrid.conduitSet) {
			cond.setGrid(this);
			conduitSet.add(cond);
		}
		for (TileEntityRedNetEnergy cond : theGrid.nodeSet) {
			cond.setGrid(this);
			nodeSet.add(cond);
		}
		theGrid.conduitSet.clear();
		theGrid.nodeSet.clear();
		rebalanceGrid();
		storage.modifyEnergyStored(theGrid.storage.getEnergyStored());
		GridTickHandler.tickingGridsToRemove.add(theGrid);
	}

	public void nodeAdded(TileEntityRedNetEnergy cond) {
		storage.modifyEnergyStored(cond.energyForGrid);
	}

	public void nodeRemoved(TileEntityRedNetEnergy cond) {
		rebalanceGrid();
		if (cond == master) {
			if (nodeSet.isEmpty()) {
				master = null;
				GridTickHandler.tickingGridsToRemove.add(this);
			} else {
				master = nodeSet.iterator().next();
			}
		}
	}

	public void conduitAdded(TileEntityRedNetEnergy cond) {
	}

	public void rebalanceGrid() {
		storage.setCapacity(nodeSet.size() * STORAGE);
	}
}
