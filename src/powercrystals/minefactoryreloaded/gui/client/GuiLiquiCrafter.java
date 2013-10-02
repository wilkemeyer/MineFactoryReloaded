package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.block.Block;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
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
	
	public GuiLiquiCrafter(ContainerLiquiCrafter container, TileEntityLiquiCrafter router)
	{
		super(container, router);
		_crafter = router;
		xSize = 231;
		ySize = 214;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRenderer.drawString("Template", 67, 27, 4210752);
		fontRenderer.drawString("Output", 128, 26, 4210752);
		
		FluidTankInfo[] tanks = _crafter.getTankInfo(ForgeDirection.UNKNOWN);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		for(int i = 0; i < 9; i++)
		{
			FluidStack l = tanks[i].fluid;
			if(l != null)
			{
				drawTank(-50 + (i % 3 * 18), 43 + (i / 3 * 35),  l, l.amount * 33 / tanks[i].capacity);
			}
		}
		
		this.mc.renderEngine.bindTexture(background);
		for(int i = 0; i < 8; i++)
		{
			this.drawTexturedModalRect(-50 + (i % 3 * 18), 10 + (i / 3 * 35), 232, 0, 16, 33);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
	{
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(new ResourceLocation(MineFactoryReloadedCore.guiFolder + _tileEntity.getGuiBackground()));
		int x = (width - xSize) / 2 - 56;
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
		
		Icon icon = fluid.getIcon(stack);
		if (icon == null)
			icon = Block.lavaMoving.getIcon(0, 0);
		
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

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fluid.getSpriteNumber());
			drawTexturedModelRectFromIcon(xOffset, yOffset - texHeight - vertOffset, icon, 16, texHeight);
			vertOffset = vertOffset + 16;
		}
		
		this.mc.renderEngine.bindTexture(new ResourceLocation(MineFactoryReloadedCore.guiFolder + _tileEntity.getGuiBackground()));
		this.drawTexturedModalRect(xOffset, yOffset - 33, 232, 0, 16, 33);
	}
}
