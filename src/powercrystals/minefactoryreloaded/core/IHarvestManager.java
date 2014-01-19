package powercrystals.minefactoryreloaded.core;

import net.minecraft.world.World;

import powercrystals.core.position.Area;
import powercrystals.core.position.BlockPosition;

public interface IHarvestManager
{
	public void moveNext();
	public BlockPosition getNextBlock();
	public BlockPosition getOrigin();
	public void reset(World world, Area area, HarvestMode harvestMode);
	public boolean getIsDone();
}
