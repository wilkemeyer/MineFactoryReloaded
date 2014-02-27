package powercrystals.minefactoryreloaded.tile.rednet;

import powercrystals.minefactoryreloaded.net.GridTickHandler;

import cofh.api.energy.EnergyStorage;

import java.util.LinkedHashSet;

import net.minecraftforge.common.ForgeDirection;

public class RedstoneEnergyNetwork
{
	public static final int TRANSFER_RATE = 640;

	private LinkedHashSet<TileEntityRedNetEnergy> nodeSet = new LinkedHashSet<TileEntityRedNetEnergy>();
	private LinkedHashSet<TileEntityRedNetEnergy> conduitSet = new LinkedHashSet<TileEntityRedNetEnergy>();
	private TileEntityRedNetEnergy master;
	EnergyStorage storage = new EnergyStorage(480, 80);

	public RedstoneEnergyNetwork(TileEntityRedNetEnergy base) {
		storage.setCapacity(TRANSFER_RATE * 6);
		storage.setMaxTransfer(TRANSFER_RATE);
	}

	public int getNodeShare(TileEntityRedNetEnergy cond) {
		int size = nodeSet.size();
		if (size == 1)
			return storage.getEnergyStored();
		else if (master != cond)
			return storage.getEnergyStored() % size;
		else
			return storage.getEnergyStored() / size;
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
		} else {
			if (nodeSet.remove(cond)) {
				nodeRemoved(cond);
			}
		}
	}

	public void removeConduit(TileEntityRedNetEnergy cond) {
		conduitSet.remove(cond);
		if (nodeSet.remove(cond)) {
			nodeRemoved(cond);
		}
	}

	public void destroyNode(TileEntityRedNetEnergy cond) {
		cond.energyForGrid = getNodeShare(cond);
		cond.setGrid(null);
	}

	public void destroyConduit(TileEntityRedNetEnergy cond) {
		cond.setGrid(null);
	}

	public void doGridUpdate() {
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
				for (int i = 6; i --> 0; )
					cond.transfer(directions[i], sideDistribute);
		
		toDistribute = storage.getEnergyStored() % size;
		sideDistribute = toDistribute / 6;
		
		if (sideDistribute > 0) for (int i = 6; i --> 0; )
			master.transfer(directions[i], sideDistribute);
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
		cond.energyForGrid = getNodeShare(cond);
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
		storage.setCapacity(nodeSet.size() * TRANSFER_RATE * 6);
	}
}
