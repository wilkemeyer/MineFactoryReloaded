package powercrystals.minefactoryreloaded.gui.client;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.gui.GuiButton;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityEjector;

public class GuiEjector extends GuiFactoryInventory
{
	protected TileEntityEjector _ejector;
	
	protected GuiButton _whitelist;
	protected GuiButton _ignoreNBT;
	protected GuiButton _ignoreDamage;

	public GuiEjector(ContainerFactoryInventory container, TileEntityEjector tileentity)
	{
		super(container, tileentity);
		_ejector = tileentity;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		
		_whitelist    = new GuiButton(1, guiLeft + 70, guiTop + 14, 70, 18,  "Blacklist");
		_ignoreNBT    = new GuiButton(2, guiLeft + 70, guiTop + 32, 70, 18,  "Match NBT");
		_ignoreDamage = new GuiButton(3, guiLeft + 70, guiTop + 50, 70, 20, "Match Meta");
		updateButtons();
		
		buttonList.add(_whitelist);
		buttonList.add(_ignoreDamage);
		buttonList.add(_ignoreNBT);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		updateButtons();
	}
	
	private void updateButtons()
	{
		_whitelist.displayString    =  _ejector.getIsWhitelist() ? "Whitelist"  : "Blacklist";
		_ignoreNBT.displayString    =  _ejector.getIsNBTMatch() ? "Match NBT"  : "Ignore NBT";
		_ignoreDamage.displayString = !_ejector.getIsIDMatch() ? "Match Meta" : "Ignore Meta";
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 1)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _ejector.xCoord, _ejector.yCoord, _ejector.zCoord, 1 }));
		}
		else if(button.id == 2)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _ejector.xCoord, _ejector.yCoord, _ejector.zCoord, 2 }));
		}
		else if(button.id == 3)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _ejector.xCoord, _ejector.yCoord, _ejector.zCoord, 3 }));
		}
	}

}
