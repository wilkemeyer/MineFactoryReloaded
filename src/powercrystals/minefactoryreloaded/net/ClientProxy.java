package powercrystals.minefactoryreloaded.net;

import net.minecraft.entity.EntityLivingBase;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		MineFactoryReloadedClient.init();
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		e.setPositionAndUpdate(x, y, z);
	}
}
