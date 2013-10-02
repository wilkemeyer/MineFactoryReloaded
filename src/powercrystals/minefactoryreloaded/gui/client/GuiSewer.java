package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.gui.container.ContainerSewer;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySewer;

public class GuiSewer extends GuiFactoryInventory
{
	public GuiSewer(ContainerSewer container, TileEntitySewer tileentity)
	{
		super(container, tileentity);
		ySize = 180;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(_tileEntity.getInvName(), 8, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(_tileEntity.getTank() != null && _tileEntity.getTank().getFluid() != null)
		{
			int tankSize = _tileEntity.getTank().getFluid().amount * _tankSizeMax / _tileEntity.getTank().getCapacity();
			drawTank(152, 75, _tileEntity.getTank().getFluid(), tankSize);
		}
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(152, 15, 16, 60, mouseX, mouseY) && _tileEntity.getTank() != null && _tileEntity.getTank().getFluid() != null && _tileEntity.getTank().getFluid().amount > 0)
		{
			drawBarTooltip(_tileEntity.getTank().getFluid().getFluid().getLocalizedName(),
					"mB", _tileEntity.getTank().getFluid().amount, _tileEntity.getTank().getCapacity(), mouseX, mouseY);
		}
	}
}
