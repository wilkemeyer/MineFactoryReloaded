package powercrystals.minefactoryreloaded.net;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.chunk.Chunk;

public interface IMFRProxy
{
	public void init();

	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z);

	public void relightChunk(Chunk chunk);
}
