package powercrystals.minefactoryreloaded.core;

import java.util.List;

import codechicken.lib.raytracer.IndexedCuboid6;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public interface ITraceable
{
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean hasTool, boolean offsetCuboids);
	
	public boolean onPartHit(EntityPlayer player, int side, int subHit);
}
