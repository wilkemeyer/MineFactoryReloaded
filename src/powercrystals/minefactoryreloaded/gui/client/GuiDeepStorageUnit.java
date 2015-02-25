package powercrystals.minefactoryreloaded.gui.client;

import powercrystals.minefactoryreloaded.core.MFRUtil;
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

		fontRendererObj.drawString(MFRUtil.localize("info.cofh.stored"), 110, 70, 4210752);
		fontRendererObj.drawString(String.valueOf(_dsu.getQuantity()), 110, 80, 4210752);
	}
}
