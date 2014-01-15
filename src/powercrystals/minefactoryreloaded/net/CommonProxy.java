package powercrystals.minefactoryreloaded.net;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.TextureStitchEvent.Post;

public class CommonProxy implements IMFRProxy
{
	@Override
	public void init()
	{
	}
	
	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z)
	{
		if (e instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e;
			ep.playerNetServerHandler.setPlayerLocation(x, y, z, ep.cameraYaw, ep.cameraPitch);
		}
		e.setPositionAndUpdate(x, y, z);
	}
	
	@Override
	public void onPostTextureStitch(Post e)
	{
	}
}
