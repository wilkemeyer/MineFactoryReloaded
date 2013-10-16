package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBioFuelGenerator;

public class GuiBioFuelGenerator extends GuiFactoryInventory
{
	private static final int _barColorEnergy = (0)   | (0 << 8)   | (255 << 16) | (255 << 24);
	
	public GuiBioFuelGenerator(ContainerFactoryInventory container, TileEntityBioFuelGenerator tileentity)
	{
		super(container, tileentity);
		ySize = 165;
		_tanksOffsetX = 142;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		drawBar(160, 75, ((TileEntityBioFuelGenerator)_tileEntity).getBufferMax(), ((TileEntityBioFuelGenerator)_tileEntity).getBuffer(), _barColorEnergy);
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(161, 15, 8, 60, mouseX, mouseY))
		{
			int buffer = ((TileEntityBioFuelGenerator)_tileEntity).getBuffer();
			int bufferMax = ((TileEntityBioFuelGenerator)_tileEntity).getBufferMax();
			int energyPerMJ = TileEntityFactoryPowered.energyPerMJ;
			drawBarTooltip("Energy", "MJ", buffer / energyPerMJ, bufferMax / energyPerMJ, mouseX, mouseY);
		}
		else
			super.drawTooltips(mouseX, mouseY);
	}
}
