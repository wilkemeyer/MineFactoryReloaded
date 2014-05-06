package powercrystals.minefactoryreloaded.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.net.ServerPacketHandler.MFRMessage;

public final class Packets
{
	public static void sendToServer(IMessage message)
	{
		MineFactoryReloadedCore.networkWrapper.sendToServer(message);
	}
	public static void sendToAllPlayersWatching(World world, int x, int y, int z, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			PlayerInstance watcher = ((WorldServer)world).getPlayerManager().
					getOrCreateChunkWatcher(x >> 4, x >> 4, false);
			if (watcher != null)
				watcher.sendToAllPlayersWatchingChunk(packet);
		}
	}
	public static void sendToAllPlayersInRange(World world, int x, int y, int z, int range, Packet packet)
	{
		if (packet == null)
			return;
		if (world instanceof WorldServer)
		{
			PlayerInstance watcher = ((WorldServer)world).getPlayerManager().
					getOrCreateChunkWatcher(x >> 4, x >> 4, false);
			if (watcher != null)
			{
				@SuppressWarnings("unchecked")
				List<EntityPlayerMP> players = watcher.playersWatchingChunk;
				for (int i = players.size(); i --> 0; )
				{
					EntityPlayerMP player = players.get(i);
					if (Math.abs(player.posX - x) < range)
						if (Math.abs(player.posY - x) < range)
							if (Math.abs(player.posZ - x) < range)
								player.playerNetServerHandler.sendPacket(packet);
				}
			}
		}
	}
	
	public static final short TileDescription = 1;// UNUSED
	public static final short EnchanterButton = 2;
	public static final short HarvesterButton = 3;
	public static final short ChronotyperButton = 4;
	public static final short HAMUpdate = 5;
	public static final short ConveyorDescription = 6;
	public static final short AutoJukeboxPlay = 7; // UNUSED
	public static final short RoadBlockUpdate = 8; //UNUSED
	public static final short AutoJukeboxButton = 9;
	public static final short AutoSpawnerButton = 10;
	public static final short CableDescription = 11; // UNUSED
	public static final short LogicCircuitDefinition = 12; // UNUSED
	public static final short LogicRequestCircuitDefinition = 13;
	public static final short LogicSetCircuit = 14;
	public static final short LogicSetPin = 15;
	public static final short LogicReinitialize = 16;
	public static final short RouterButton = 17;
	public static final short HistorianValueChanged = 18; // UNUSED
	public static final short FakeSlotChange = 19;
	public static final short RocketLaunchWithLock = 20;
	public static final short EnergyCableDescription = 21; // UNUSED
	
	public static void sendToServer(short packet, TileEntity te, Object... args)
	{
		sendToServer(new MFRMessage(packet, te, args));
	}
	public static void sendToServer(short packet, Entity te, Object... args)
	{
		sendToServer(new MFRMessage(packet, te, args));
	}
}
