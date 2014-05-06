package powercrystals.minefactoryreloaded.net;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class Packets
{
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
	
	public static final int TileDescription = 1;
	public static final int EnchanterButton = 2;
	public static final int HarvesterButton = 3;
	public static final int ChronotyperButton = 4;
	public static final int HAMUpdate = 5;
	public static final int ConveyorDescription = 6;
	public static final int AutoJukeboxPlay = 7;
	public static final int RoadBlockUpdate = 8;
	public static final int AutoJukeboxButton = 9;
	public static final int AutoSpawnerButton = 10;
	public static final int CableDescription = 11;
	public static final int LogicCircuitDefinition = 12;
	public static final int LogicRequestCircuitDefinition = 13;
	public static final int LogicSetCircuit = 14;
	public static final int LogicSetPin = 15;
	public static final int LogicReinitialize = 16;
	public static final int RouterButton = 17;
	public static final int HistorianValueChanged = 18;
	public static final int FakeSlotChange = 19;
	public static final int RocketLaunchWithLock = 20;
	public static final int EnergyCableDescription = 21;
}
