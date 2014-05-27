package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySteamBoiler;

public class GuiSteamBoiler extends GuiFactoryInventory
{
	protected static final int _barColorWork =   (0)   | (255 << 8) | (0 << 16)   | (255 << 24);
	protected static final int _barColorTemp =   (0)   | (0 << 8)   | (255 << 16) | (255 << 24);
	private TileEntitySteamBoiler _boiler;

	public GuiSteamBoiler(ContainerFactoryInventory container, TileEntitySteamBoiler tileentity)
	{
		super(container, tileentity);
		_boiler = tileentity;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		drawBar(150, 75, _boiler.getWorkMax(), _boiler.getWorkDone(), _barColorWork);
		drawBar(160, 75, TileEntitySteamBoiler.maxTemp + 20, (int)_boiler.getTemp() + 20, _barColorTemp);
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(150, 15, 8, 60, mouseX, mouseY))
		{
			drawBarTooltip("Work", "Wk", _boiler.getWorkDone(), _boiler.getWorkMax(), mouseX, mouseY);
		}
		else if(isPointInRegion(160, 15, 8, 60, mouseX, mouseY))
		{
			drawBarTooltip("Temperature", "C", (int)_boiler.getTemp(), TileEntitySteamBoiler.maxTemp, mouseX, mouseY);
		}
		else
			super.drawTooltips(mouseX, mouseY);
	}

}
