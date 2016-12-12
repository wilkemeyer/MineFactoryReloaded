package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.RayTraceResult;
import net.minecraft.world.World;

public interface IUseable
{
	public RayTraceResult rayTrace(World world, EntityLivingBase entity, boolean adjacent);
	public boolean addUseHandler(IUseHandler handler);
	public boolean removeUseHandler(IUseHandler handler);
}
