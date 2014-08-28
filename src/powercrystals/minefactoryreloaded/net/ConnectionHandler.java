package powercrystals.minefactoryreloaded.net;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ConnectionHandler
{
	public static HashMap<String, Boolean> onlinePlayerMap = new HashMap<String, Boolean>();

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent player)
	{
		String name = player.player.getCommandSenderName();
		onlinePlayerMap.put(name, Boolean.TRUE);
		Iterator<Ticket> i = CommonProxy.ticketsInLimbo.iterator();
		while (i.hasNext())
		{
			Ticket ticket = i.next();
			if (ticket.getPlayerName().equals(name) && CommonProxy.loadTicket(ticket, false))
				i.remove();
		}		
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent player)
	{
		onlinePlayerMap.remove(player.player.getCommandSenderName());
	}

	@SubscribeEvent
	public void onItemPickUp(ItemPickupEvent evt) {
		if (evt.pickedUp.getEntityItem().getItem() == MineFactoryReloadedCore.rubberWoodItem) {
			evt.player.triggerAchievement(AchievementList.mineWood);
		}
	}
}
