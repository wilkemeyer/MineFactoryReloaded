package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoBrewer;

public class GuiAutoBrewer extends GuiFactoryPowered
{
	public GuiAutoBrewer(ContainerFactoryPowered container, TileEntityAutoBrewer te)
	{
		super(container, te);
		ySize = 255;
		_tanksOffsetX = 146;
		_tanksOffsetY = 80;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRendererObj.drawString("Potion", 6, 22, 4210752);
		fontRendererObj.drawString("Type", 42, 22, 4210752);
		fontRendererObj.drawString("Resources", 80, 22, 4210752);
		// TODO: localize
	}
}
