package powercrystals.minefactoryreloaded.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		MineFactoryReloadedClient.init();
		if (!Minecraft.getMinecraft().getFramebuffer().isStencilEnabled()) {
			Minecraft.getMinecraft().getFramebuffer().enableStencil();
		}
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		e.setPositionAndUpdate(x, y, z);
	}
}
