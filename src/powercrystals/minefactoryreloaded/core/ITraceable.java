package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.player.EntityPlayer;

import codechicken.lib.raytracer.IndexedCuboid6;

import java.util.List;

public interface ITraceable
{
	public void addTraceableCuboids(List<IndexedCuboid6> list, boolean forTrace, boolean forDraw);
	public boolean onPartHit(EntityPlayer player, int side, int subHit);
	public boolean isLargePart(EntityPlayer player, int subHit);
}
