package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityDeepStorageUnit;

public class GuiDeepStorageUnit extends GuiFactoryInventory
{
	private TileEntityDeepStorageUnit _dsu;
	
	public GuiDeepStorageUnit(ContainerFactoryInventory container, TileEntityDeepStorageUnit dsu)
	{
		super(container, dsu);
		_dsu = dsu;
		ySize = 205;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		fontRenderer.drawString("Stored:", 110, 70, 4210752);
		fontRenderer.drawString(((Integer)_dsu.getQuantity()).toString(), 110, 80, 4210752);
	}
}
