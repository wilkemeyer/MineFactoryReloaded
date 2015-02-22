package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.relauncher.ReflectionHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.ForgeHooksClient;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		MineFactoryReloadedClient.init();
		if (!Boolean.parseBoolean(System.getProperty("forge.forceDisplayStencil", "false"))) {
			try {
				ReflectionHelper.findField(ForgeHooksClient.class, "stencilBits").setInt(null, 2);
				Framebuffer b = Minecraft.getMinecraft().getFramebuffer();
				b.createBindFramebuffer(b.framebufferWidth, b.framebufferHeight);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		e.setPositionAndUpdate(x, y, z);
	}
}
