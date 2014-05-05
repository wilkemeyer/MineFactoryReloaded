package powercrystals.minefactoryreloaded.gui.client;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.gui.GuiButton;

import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradable;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityPlanter;

public class GuiPlanter extends GuiUpgradable
{
	private TileEntityPlanter _planter;

	private GuiButton _consumeToggle;

	public GuiPlanter(ContainerUpgradable container, TileEntityPlanter te)
	{
		super(container, te);
		_planter = te;
		ySize = 200;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		_consumeToggle = new GuiButton(1, (this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 87, 100, 20, "Consume Stack: On");
		_consumeToggle.displayString = _planter.getConsumeAll() ? "Consume Stack: Off" : "Consume Stack: On";
		buttonList.add(_consumeToggle);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		_consumeToggle.displayString = _planter.getConsumeAll() ? "Consume Stack: Off" : "Consume Stack: On";
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 1)
		{
			PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.RouterButton,
					new Object[] { _planter.xCoord, _planter.yCoord, _planter.zCoord, 1 }));
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString("Filter", 8, 22, 4210752); // TODO: Localize
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
}
