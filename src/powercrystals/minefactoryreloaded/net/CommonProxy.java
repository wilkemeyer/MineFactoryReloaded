package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import org.bouncycastle.util.Arrays;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader;

public class CommonProxy implements IMFRProxy, LoadingCallback 
{
	private GridTickHandler gridTickHandler;
	public static LinkedList<Ticket> ticketsInLimbo = new LinkedList<Ticket>();
	
	public static boolean loadTicket(Ticket ticket, boolean addToList)
	{
		int x = ticket.getModData().getInteger("X");
		int y = ticket.getModData().getInteger("Y");
		int z = ticket.getModData().getInteger("Z");
		
		TileEntity tile = ticket.world.getTileEntity(x, y, z);
		if (!(tile instanceof TileEntityChunkLoader))
		{
			ForgeChunkManager.releaseTicket(ticket);
			return true;
		}
		boolean r = ((TileEntityChunkLoader)tile).receiveTicket(ticket); 
		if (addToList & !r)
			ticketsInLimbo.push(ticket);
		return r;
	}
		
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for (Ticket ticket : tickets)
			loadTicket(ticket, true);
	}
	
	@Override
	public void init()
	{
		gridTickHandler = new GridTickHandler();
		TickRegistry.registerScheduledTickHandler(gridTickHandler, Side.SERVER);
		ForgeChunkManager.setForcedChunkLoadingCallback(MineFactoryReloadedCore.instance(), this);
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

	@Override
	public void relightChunk(Chunk chunk)
	{
		if (chunk != null)
		{
			chunk.generateSkylightMap();
			ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
			for (int i = storage.length; i --> 0;)
				if (storage[i] != null)
					Arrays.fill(storage[i].getSkylightArray().data, (byte)0);
			chunk.resetRelightChecks();
			chunk.isModified = true;
			World world = chunk.worldObj;
			if (world instanceof WorldServer)
			{
				PlayerInstance watcher = ((WorldServer)world).getPlayerManager().
						getOrCreateChunkWatcher(chunk.xPosition, chunk.zPosition, false);
				if (watcher != null)
					watcher.sendToAllPlayersWatchingChunk(new Packet51MapChunk(chunk, false, -1));
			}
		}
	}
}
