package powercrystals.minefactoryreloaded.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.conveyor.TileEntityConveyor;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoJukebox;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IPacketHandler
{
	@SuppressWarnings("rawtypes")
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		int packetType = PacketWrapper.readPacketID(data);
		
		Class[] decodeAs;
		Object[] packetReadout;
		TileEntity te;
		
		switch (packetType)
		{
		case Packets.TileDescription: // server -> client; server propagating machine rotation; args X Y Z rotation isActive
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class, Boolean.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityFactory)
			{
				TileEntityFactory tef = (TileEntityFactory) te;
				tef.rotateDirectlyTo((Integer)packetReadout[3]);
				tef.setIsActive((Boolean)packetReadout[4]);
				if (tef.hasHAM())
				{
					Packet t = PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel,
							Packets.HAMUpdate, new Object[]{te.xCoord, te.yCoord, te.zCoord});
					PacketDispatcher.sendPacketToServer(t);
				}
			}
			break;
		case Packets.ConveyorDescription: // server -> client; server propagating conveyor color, activity state
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class,
					Integer.class, Boolean.class, Boolean.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if (te instanceof TileEntityConveyor)
			{
				TileEntityConveyor tec = (TileEntityConveyor) te;
				tec.setDyeColor((Integer)packetReadout[3]);
				tec.setConveyorActive((Boolean)packetReadout[4]);
				tec.setFast((Boolean)packetReadout[5]);
			}
			break;
		case Packets.AutoJukeboxPlay: // server -> client; server playing a record
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityAutoJukebox)
			{
				Minecraft.getMinecraft().ingameGUI.setRecordPlayingMessage(((ItemRecord)Item.itemsList[(Integer)packetReadout[3]]).recordName);
			}
			break;
		case Packets.RoadBlockUpdate: // server -> client; road block light changed
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			((EntityPlayer)player).worldObj.setBlock((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2], MineFactoryReloadedCore.factoryRoadBlock.blockID, (Integer)packetReadout[3], 6);
			((EntityPlayer)player).worldObj.markBlockForRenderUpdate((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			break;
		case Packets.CableDescription: // server -> client; cable side colors
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetCable)
			{
				TileEntityRedNetCable tec = (TileEntityRedNetCable) te;
				tec.setSideColor(ForgeDirection.DOWN, (Integer)packetReadout[3]);
				tec.setSideColor(ForgeDirection.UP, (Integer)packetReadout[4]);
				tec.setSideColor(ForgeDirection.NORTH, (Integer)packetReadout[5]);
				tec.setSideColor(ForgeDirection.SOUTH, (Integer)packetReadout[6]);
				tec.setSideColor(ForgeDirection.WEST, (Integer)packetReadout[7]);
				tec.setSideColor(ForgeDirection.EAST, (Integer)packetReadout[8]);
				int mode = (Integer)packetReadout[9];
				tec.setMode(0, (byte)(mode & 0xFF));
				tec.setMode(1, (byte)((mode >> 8) & 0xFF));
				tec.setMode(2, (byte)((mode >> 16) & 0xFF));
				tec.setMode(3, (byte)((mode >> 24) & 0xFF));
				mode = (Integer)packetReadout[10];
				tec.setMode(4, (byte)(mode & 0xFF));
				tec.setMode(5, (byte)((mode >> 8) & 0xFF));
				tec.setMode(6, (byte)((mode >> 16) & 0xFF));
				((EntityPlayer)player).worldObj.markBlockForRenderUpdate((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			}
			break;
		case Packets.EnergyCableDescription: // server -> client; cable side colors
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class, Integer.class,
					Integer.class, Integer.class,
					Byte.class, Byte.class, Byte.class,
					Byte.class, Byte.class, Byte.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetEnergy)
			{
				TileEntityRedNetEnergy tec = (TileEntityRedNetEnergy) te;
				tec.setSideColor(ForgeDirection.DOWN, (Integer)packetReadout[3]);
				tec.setSideColor(ForgeDirection.UP, (Integer)packetReadout[4]);
				tec.setSideColor(ForgeDirection.NORTH, (Integer)packetReadout[5]);
				tec.setSideColor(ForgeDirection.SOUTH, (Integer)packetReadout[6]);
				tec.setSideColor(ForgeDirection.WEST, (Integer)packetReadout[7]);
				tec.setSideColor(ForgeDirection.EAST, (Integer)packetReadout[8]);
				int mode = (Integer)packetReadout[9];
				tec.setMode(0, (byte)(mode & 0xFF));
				tec.setMode(1, (byte)((mode >> 8) & 0xFF));
				tec.setMode(2, (byte)((mode >> 16) & 0xFF));
				tec.setMode(3, (byte)((mode >> 24) & 0xFF));
				mode = (Integer)packetReadout[10];
				tec.setMode(4, (byte)(mode & 0xFF));
				tec.setMode(5, (byte)((mode >> 8) & 0xFF));
				tec.setMode(6, (byte)((mode >> 16) & 0xFF));
				tec.setModes(new byte[] {(Byte)packetReadout[11],(Byte)packetReadout[12],
						(Byte)packetReadout[13],(Byte)packetReadout[14],
						(Byte)packetReadout[15],(Byte)packetReadout[16],0});
				((EntityPlayer)player).worldObj.markBlockForRenderUpdate((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			}
			break;
		case Packets.LogicCircuitDefinition: // server -> client: logic circuit (class and pins)
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetLogic)
			{
				((TileEntityRedNetLogic)te).setCircuitFromPacket(data);
			}
			break;
		case Packets.HistorianValueChanged:
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);
			
			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if(te instanceof TileEntityRedNetHistorian)
			{
				((TileEntityRedNetHistorian)te).setClientValue((Integer)packetReadout[3]);
			}
			break;
		case Packets.HAMUpdate:
			decodeAs = new Class[]{ Integer.class, Integer.class, Integer.class, Integer.class };
			packetReadout = PacketWrapper.readPacketData(data, decodeAs);

			te = ((EntityPlayer)player).worldObj.getTileEntity((Integer)packetReadout[0], (Integer)packetReadout[1], (Integer)packetReadout[2]);
			if (te instanceof TileEntityFactory)
			{
				if (((TileEntityFactory)te).hasHAM())
					((TileEntityFactory)te).getHAM().setUpgradeLevel((Integer)packetReadout[3]);
			}
		}
	}
}
