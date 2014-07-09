package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLiquiCrafter;

public class GuiLiquiCrafter extends GuiFactoryInventory
{
	private static final ResourceLocation background = new ResourceLocation(MineFactoryReloadedCore.guiFolder + "liquicrafter.png");
	private TileEntityLiquiCrafter _crafter;
	private static final int TANK_OFFSET_X = -22;
	
	public GuiLiquiCrafter(ContainerLiquiCrafter container, TileEntityLiquiCrafter router)
	{
		super(container, router);
		_crafter = router;
		xSize = 232;
		ySize = 214;
		_xOffset = _xOffset + 27;
		_renderTanks = false;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		fontRendererObj.drawString("Template", 67 + 27, 27, 4210752);
		fontRendererObj.drawString("Output", 128 + 27, 26, 4210752);
		// TODO: localize
		
		FluidTankInfo[] tanks = _crafter.getTankInfo(ForgeDirection.UNKNOWN);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		for(int i = 0; i < 9; i++)
		{
			FluidStack l = tanks[i].fluid;
			if(l != null)
			{
				drawTank(TANK_OFFSET_X + (i % 3 * 18), 43 + (i / 3 * 35),  l, l.amount * 33 / tanks[i].capacity);
			}
		}
		
		this.mc.renderEngine.bindTexture(background);
		for(int i = 0; i < 8; i++)
		{
			this.drawTexturedModalRect(TANK_OFFSET_X + (i % 3 * 18), 10 + (i / 3 * 35), 232, 0, 16, 33);
		}
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if (isPointInRegion(TANK_OFFSET_X + 1, 11, 18 * 3 - 2, 35 * 3 - 2, mouseX, mouseY))
		{
			int tankX = mouseX - TANK_OFFSET_X - this.guiLeft;
			int tankY = mouseY - 10 - this.guiTop;
			if ((tankX % 18 >= 16) | tankY % 35 >= 33)
				return;
			tankX /= 18;
			tankY /= 35;
			int i = tankX + tankY * 3;
			drawTankTooltip(_crafter.getTanks()[i], mouseX, mouseY);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(new ResourceLocation(MineFactoryReloadedCore.guiFolder + _tileEntity.getGuiBackground()));
		int x = (width - xSize - 56) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawTank(int xOffset, int yOffset, FluidStack stack, int level)
	{
		if (stack == null) return;
		Fluid fluid = stack.getFluid();
		if(fluid == null) return;
		
		int vertOffset = 0;
		
		IIcon icon = fluid.getIcon(stack);
		if (icon == null)
			icon = Blocks.flowing_lava.getIcon(0, 0);
		
		while(level > 0)
		{
			int texHeight = 0;
			
			if(level > 16)
			{
				texHeight = 16;
				level -= 16;
			}
			else
			{
				texHeight = level;
				level = 0;
			}

			bindTexture(fluid);
			
			drawTexturedModelRectFromIcon(xOffset, yOffset - texHeight - vertOffset, icon, 16, texHeight);
			vertOffset = vertOffset + 16;
		}
		
		this.mc.renderEngine.bindTexture(new ResourceLocation(MineFactoryReloadedCore.guiFolder + _tileEntity.getGuiBackground()));
		this.drawTexturedModalRect(xOffset, yOffset - 33, 232, 0, 16, 33);
	}
}
