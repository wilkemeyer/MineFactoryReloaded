package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;

public class ConnectionHandler implements IConnectionHandler, IPlayerTracker
{
	public static HashMap<String, Boolean> onlinePlayerMap = new HashMap<String, Boolean>();
	
	public ConnectionHandler()
	{
		GameRegistry.registerPlayerTracker(this);
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
	{
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
	{
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
	{
	}

	@Override
	public void connectionClosed(INetworkManager manager)
	{
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
	{
		MineFactoryReloadedClient.prcPages.clear();
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		String name = player.getCommandSenderName();
		onlinePlayerMap.put(name, Boolean.TRUE);
		Iterator<Ticket> i = CommonProxy.ticketsInLimbo.iterator();
		while (i.hasNext())
		{
			Ticket ticket = i.next();
			if (ticket.getPlayerName().equals(name) && CommonProxy.loadTicket(ticket, false))
				i.remove();
		}		
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		onlinePlayerMap.remove(player.getCommandSenderName());
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
	}
}
