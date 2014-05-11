package powercrystals.minefactoryreloaded.render.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactoryGlassPane;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class FactoryGlassPaneRenderer implements ISimpleBlockRenderingHandler
{
	@Override
	public void renderInventoryBlock(Block tile, int metadata, int modelID, RenderBlocks renderer)
	{
		BlockFactoryGlassPane block = (BlockFactoryGlassPane)tile;

		Tessellator tessellator = Tessellator.instance;
		int color = block.getRenderColor(metadata);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float anaglyphRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float anaglyphGreen = (red * 30.0F + green * 70.0F) / 100.0F;
			float anaglyphBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = anaglyphRed;
			green = anaglyphGreen;
			blue = anaglyphBlue;
		}
		IIcon iconGlass, iconStreaks, iconSide, iconOverlay;

		iconGlass = block.getIcon(0, metadata);
		iconStreaks = block.getIcon(0, 16 | metadata);
		iconSide = block.func_150097_e();
		iconOverlay = block.getIcon(0, 32 | metadata);

		double minXGlass = iconGlass.getMinU();
		double maxXGlass = iconGlass.getMaxU();
		double minYGlass = iconGlass.getMinV();
		double maxYGlass = iconGlass.getMaxV();

		double minXStreaks = iconStreaks.getMinU();
		double maxXStreaks = iconStreaks.getMaxU();
		double minYStreaks = iconStreaks.getMinV();
		double maxYStreaks = iconStreaks.getMaxV();

		double minXSide = iconSide.getInterpolatedU(7.0D);
		double maxXSide = iconSide.getInterpolatedU(9.0D);
		double minYSide = iconSide.getMinV();
		double maxYSide = iconSide.getMaxV();

		double minXOverlay = iconOverlay.getMinU();
		double maxXOverlay = iconOverlay.getMaxU();
		double minYOverlay = iconOverlay.getMinV();
		double maxYOverlay = iconOverlay.getMaxV();

		double offset = 0.001D;

		double xMin = 0, xMax = 1;
		double yMin = 0, yMax = 1;
		double zMid = 0.5;

		double negSideXOffset = zMid - 0.0625D;
		double posSideXOffset = zMid + 0.0625D;
		
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin, yMax, zMid, minXGlass, minYGlass);
		tessellator.addVertexWithUV(xMin, yMin, zMid, minXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMin, zMid, maxXGlass, maxYGlass);
		tessellator.addVertexWithUV(xMax, yMax, zMid, maxXGlass, minYGlass);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMax, zMid, minXStreaks, minYStreaks);
		tessellator.addVertexWithUV(xMin, yMin, zMid, minXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMin, zMid, maxXStreaks, maxYStreaks);
		tessellator.addVertexWithUV(xMax, yMax, zMid, maxXStreaks, minYStreaks);

		tessellator.addVertexWithUV(xMin, yMax, zMid, minXOverlay, minYOverlay);
		tessellator.addVertexWithUV(xMin, yMin, zMid, minXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMin, zMid, maxXOverlay, maxYOverlay);
		tessellator.addVertexWithUV(xMax, yMax, zMid, maxXOverlay, minYOverlay);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMax, negSideXOffset, minXSide, minYSide);
		tessellator.addVertexWithUV(xMin, yMin, negSideXOffset, minXSide, maxYSide);
		tessellator.addVertexWithUV(xMin, yMin, posSideXOffset, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMin, yMax, posSideXOffset, maxXSide, minYSide);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMax, yMax, negSideXOffset, minXSide, minYSide);
		tessellator.addVertexWithUV(xMax, yMin, negSideXOffset, minXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMin, posSideXOffset, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMax, posSideXOffset, maxXSide, minYSide);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMax + offset, posSideXOffset, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMax + offset, posSideXOffset, maxXSide, minYSide);
		tessellator.addVertexWithUV(xMax, yMax + offset, negSideXOffset, minXSide, minYSide);
		tessellator.addVertexWithUV(xMin, yMax + offset, negSideXOffset, minXSide, maxYSide);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.addVertexWithUV(xMin, yMin - offset, posSideXOffset, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMin - offset, posSideXOffset, maxXSide, minYSide);
		tessellator.addVertexWithUV(xMax, yMin - offset, negSideXOffset, minXSide, minYSide);
		tessellator.addVertexWithUV(xMin, yMin - offset, negSideXOffset, minXSide, maxYSide);
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z,
			Block tile, int modelId, RenderBlocks renderer)
	{
		BlockPane block = (BlockPane)tile;

		int worldHeight = blockAccess.getHeight();
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		Tessellator tess = Tessellator.instance;
		tess.setBrightness(tile.getMixedBrightnessForBlock(blockAccess, x, y, z));
		int color = block.getRenderColor(metadata);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float anaglyphRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float anaglyphGreen = (red * 30.0F + green * 70.0F) / 100.0F;
			float anaglyphBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = anaglyphRed;
			green = anaglyphGreen;
			blue = anaglyphBlue;
		}

		IIcon iconGlass, iconStreaks, iconSide, iconOverlaySouth, iconOverlayWest;

		if (renderer.hasOverrideBlockTexture())
		{
			iconGlass = iconStreaks = iconSide = 
			iconOverlaySouth = iconOverlayWest =
					renderer.overrideBlockTexture;
		}
		else
		{
			iconGlass = block.getIcon(0, metadata);
			iconStreaks = block.getIcon(0, 16 | metadata);
			iconSide = block.func_150097_e();
			iconOverlaySouth = block.getIcon(blockAccess, x, y, z, 2);
			iconOverlayWest = block.getIcon(blockAccess, x, y, z, 5);
		}

		double minXGlass = iconGlass.getMinU();
		double midXGlass = iconGlass.getInterpolatedU(8.0D);
		double maxXGlass = iconGlass.getMaxU();
		double minYGlass = iconGlass.getMinV();
		double maxYGlass = iconGlass.getMaxV();

		double minXStreaks = iconStreaks.getMinU();
		double midXStreaks = iconStreaks.getInterpolatedU(8.0D);
		double maxXStreaks = iconStreaks.getMaxU();
		double minYStreaks = iconStreaks.getMinV();
		double maxYStreaks = iconStreaks.getMaxV();

		double minXSide = iconSide.getInterpolatedU(7.0D);
		double maxXSide = iconSide.getInterpolatedU(9.0D);
		double minYSide = iconSide.getMinV();
		double midYSide = iconSide.getInterpolatedV(8.0D);
		double maxYSide = iconSide.getMaxV();

		double minXSouth = iconOverlaySouth.getMinU();
		double midXSouth = iconOverlaySouth.getInterpolatedU(8.0D);
		double maxXSouth = iconOverlaySouth.getMaxU();
		double minYSouth = iconOverlaySouth.getMinV();
		double maxYSouth = iconOverlaySouth.getMaxV();

		double minXWest = iconOverlayWest.getMinU();
		double midXWest = iconOverlayWest.getInterpolatedU(8.0D);
		double maxXWest = iconOverlayWest.getMaxU();
		double minYWest = iconOverlayWest.getMinV();
		double maxYWest = iconOverlayWest.getMaxV();

		double xMin = x;
		double xMid = x + 0.5D;
		double xMax = x + 1;

		double zMin = z;
		double zMid = z + 0.5D;
		double zMax = z + 1;

		double yMin = y;
		double yMax = y + 1;

		double vertSideZOffset = 0.001D;
		double vertSideXOffset = 0.002D;
		
		double offset = 1 / 16f; 

		double negSideZOffset = xMid - offset;
		double posSideZOffset = xMid + offset;
		double negSideXOffset = zMid - offset;
		double posSideXOffset = zMid + offset;

		boolean connectedNegZ = block.canPaneConnectTo(blockAccess, x, y, z - 1, ForgeDirection.NORTH);
		boolean connectedPosZ = block.canPaneConnectTo(blockAccess, x, y, z + 1, ForgeDirection.SOUTH);
		boolean connectedNegX = block.canPaneConnectTo(blockAccess, x - 1, y, z, ForgeDirection.WEST);
		boolean connectedPosX = block.canPaneConnectTo(blockAccess, x + 1, y, z, ForgeDirection.EAST);

		boolean renderTop = y >= worldHeight || block.shouldSideBeRendered(blockAccess, x, y + 1, z, 1);
		boolean renderBottom = y <= 0 || block.shouldSideBeRendered(blockAccess, x, y - 1, z, 0);

		if ((!connectedNegX || !connectedPosX) && (connectedNegX || connectedPosX || connectedNegZ || connectedPosZ))
		{
			if (connectedNegX && !connectedPosX)
			{
				tess.setColorOpaque_F(red, green, blue);
				drawBox(tess, xMin, yMin, zMid, xMid, yMax, zMid, minXGlass, minYGlass, midXGlass, maxYGlass, 0, offset);
				drawBox(tess, xMin, yMin, zMid, xMid, yMax, zMid, minXGlass, minYGlass, midXGlass, maxYGlass, 0, -offset);

				tess.setColorOpaque_F(1, 1, 1);
				drawBox(tess, xMin, yMin, zMid, xMid, yMax, zMid, minXStreaks, minYStreaks, midXStreaks, maxYStreaks, 0, offset);
				drawBox(tess, xMin, yMin, zMid, xMid, yMax, zMid, minXStreaks, minYStreaks, midXStreaks, maxYStreaks, 0, -offset);
				
				drawBox(tess, xMin, yMin, zMid, xMid, yMax, zMid, minXSouth, minYSouth, midXSouth, maxYSouth, 0, offset);
				drawBox(tess, xMin, yMin, zMid, xMid, yMax, zMid, minXSouth, minYSouth, midXSouth, maxYSouth, 0, -offset);

				if (!connectedPosZ && !connectedNegZ)
				{
					tess.addVertexWithUV(xMid, yMax, posSideXOffset, minXSide, minYSide);
					tess.addVertexWithUV(xMid, yMin, posSideXOffset, minXSide, maxYSide);
					tess.addVertexWithUV(xMid, yMin, negSideXOffset, maxXSide, maxYSide);
					tess.addVertexWithUV(xMid, yMax, negSideXOffset, maxXSide, minYSide); 
				}

				if (renderTop)
				{
					tess.addVertexWithUV(xMin, yMax + vertSideXOffset, posSideXOffset, maxXSide, midYSide);
					tess.addVertexWithUV(xMid, yMax + vertSideXOffset, posSideXOffset, maxXSide, maxYSide);
					tess.addVertexWithUV(xMid, yMax + vertSideXOffset, negSideXOffset, minXSide, maxYSide);
					tess.addVertexWithUV(xMin, yMax + vertSideXOffset, negSideXOffset, minXSide, midYSide);
				}

				if (renderBottom)
				{
					tess.addVertexWithUV(xMin, yMin - vertSideXOffset, posSideXOffset, maxXSide, midYSide);
					tess.addVertexWithUV(xMid, yMin - vertSideXOffset, posSideXOffset, maxXSide, maxYSide);
					tess.addVertexWithUV(xMid, yMin - vertSideXOffset, negSideXOffset, minXSide, maxYSide);
					tess.addVertexWithUV(xMin, yMin - vertSideXOffset, negSideXOffset, minXSide, midYSide);
				}
			}
			else if (!connectedNegX && connectedPosX)
			{
				tess.setColorOpaque_F(red, green, blue);
				drawBox(tess, xMid, yMin, zMid, xMax, yMax, zMid, midXGlass, minYGlass, maxXGlass, maxYGlass, 0, offset);
				drawBox(tess, xMid, yMin, zMid, xMax, yMax, zMid, midXGlass, minYGlass, maxXGlass, maxYGlass, 0, -offset);

				tess.setColorOpaque_F(1, 1, 1);
				drawBox(tess, xMid, yMin, zMid, xMax, yMax, zMid, midXStreaks, minYStreaks, maxXStreaks, maxYStreaks, 0, offset);
				drawBox(tess, xMid, yMin, zMid, xMax, yMax, zMid, midXStreaks, minYStreaks, maxXStreaks, maxYStreaks, 0, -offset);
				
				drawBox(tess, xMid, yMin, zMid, xMax, yMax, zMid, midXSouth, minYSouth, maxXSouth, maxYSouth, 0, offset);
				drawBox(tess, xMid, yMin, zMid, xMax, yMax, zMid, midXSouth, minYSouth, maxXSouth, maxYSouth, 0, -offset);

				if (!connectedPosZ && !connectedNegZ)
				{
					tess.addVertexWithUV(xMid, yMax, negSideXOffset, minXSide, minYSide);
					tess.addVertexWithUV(xMid, yMin, negSideXOffset, minXSide, maxYSide);
					tess.addVertexWithUV(xMid, yMin, posSideXOffset, maxXSide, maxYSide);
					tess.addVertexWithUV(xMid, yMax, posSideXOffset, maxXSide, minYSide);
				}

				if (renderTop)
				{
					tess.addVertexWithUV(xMid, yMax + vertSideXOffset, posSideXOffset, maxXSide, minYSide);
					tess.addVertexWithUV(xMax, yMax + vertSideXOffset, posSideXOffset, maxXSide, midYSide);
					tess.addVertexWithUV(xMax, yMax + vertSideXOffset, negSideXOffset, minXSide, midYSide);
					tess.addVertexWithUV(xMid, yMax + vertSideXOffset, negSideXOffset, minXSide, minYSide);
				}

				if (renderBottom)
				{
					tess.addVertexWithUV(xMid, yMin - vertSideXOffset, posSideXOffset, maxXSide, minYSide);
					tess.addVertexWithUV(xMax, yMin - vertSideXOffset, posSideXOffset, maxXSide, midYSide);
					tess.addVertexWithUV(xMax, yMin - vertSideXOffset, negSideXOffset, minXSide, midYSide);
					tess.addVertexWithUV(xMid, yMin - vertSideXOffset, negSideXOffset, minXSide, minYSide);
				}
			}
		}
		else
		{
			tess.setColorOpaque_F(red, green, blue);
			drawBox(tess, xMin, yMin, zMid, xMax, yMax, zMid, minXGlass, minYGlass, maxXGlass, maxYGlass, 0, offset);
			drawBox(tess, xMin, yMin, zMid, xMax, yMax, zMid, minXGlass, minYGlass, maxXGlass, maxYGlass, 0, -offset);

			tess.setColorOpaque_F(1, 1, 1);
			drawBox(tess, xMin, yMin, zMid, xMax, yMax, zMid, minXStreaks, minYStreaks, maxXStreaks, maxYStreaks, 0, offset);
			drawBox(tess, xMin, yMin, zMid, xMax, yMax, zMid, minXStreaks, minYStreaks, maxXStreaks, maxYStreaks, 0, -offset);
			
			drawBox(tess, xMin, yMin, zMid, xMax, yMax, zMid, minXSouth, minYSouth, maxXSouth, maxYSouth, 0, offset);
			drawBox(tess, xMin, yMin, zMid, xMax, yMax, zMid, minXSouth, minYSouth, maxXSouth, maxYSouth, 0, -offset);

			if (!connectedPosX && !connectedNegX)
			{
				drawBox(tess, xMin, yMin, negSideXOffset, xMin, yMax, posSideXOffset, minXSide, minYSide, maxXSide, maxYSide, 0, 0);
				
				drawBox(tess, xMax, yMin, negSideXOffset, xMax, yMax, posSideXOffset, minXSide, minYSide, maxXSide, maxYSide, 0, 0, true);
			}

			if (renderTop)
			{
				tess.addVertexWithUV(xMin, yMax + vertSideXOffset, posSideXOffset, maxXSide, maxYSide);
				tess.addVertexWithUV(xMax, yMax + vertSideXOffset, posSideXOffset, maxXSide, minYSide);
				tess.addVertexWithUV(xMax, yMax + vertSideXOffset, negSideXOffset, minXSide, minYSide);
				tess.addVertexWithUV(xMin, yMax + vertSideXOffset, negSideXOffset, minXSide, maxYSide);
			}

			if (renderBottom)
			{
				tess.addVertexWithUV(xMin, yMin - vertSideXOffset, posSideXOffset, maxXSide, maxYSide);
				tess.addVertexWithUV(xMax, yMin - vertSideXOffset, posSideXOffset, maxXSide, minYSide);
				tess.addVertexWithUV(xMax, yMin - vertSideXOffset, negSideXOffset, minXSide, minYSide);
				tess.addVertexWithUV(xMin, yMin - vertSideXOffset, negSideXOffset, minXSide, maxYSide);
			}
		}

		if ((!connectedNegZ || !connectedPosZ) && (connectedNegX || connectedPosX || connectedNegZ || connectedPosZ))
		{
			if (connectedNegZ && !connectedPosZ)
			{
				tess.setColorOpaque_F(red, green, blue);
				drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMid, minXGlass, minYGlass, midXGlass, maxYGlass, offset, 0);
				drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMid, minXGlass, minYGlass, midXGlass, maxYGlass, -offset, 0);

				tess.setColorOpaque_F(1, 1, 1);
				drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMid, minXStreaks, minYStreaks, midXStreaks, maxYStreaks, offset, 0);
				drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMid, minXStreaks, minYStreaks, midXStreaks, maxYStreaks, -offset, 0);
				
				drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMid, minXWest, minYWest, midXWest, maxYWest, offset, 0);
				drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMid, minXWest, minYWest, midXWest, maxYWest, -offset, 0);

				if (!connectedPosX && !connectedNegX)
				{
					tess.addVertexWithUV(negSideZOffset, yMax, zMid, minXSide, minYSide);
					tess.addVertexWithUV(negSideZOffset, yMin, zMid, minXSide, maxYSide);
					tess.addVertexWithUV(posSideZOffset, yMin, zMid, maxXSide, maxYSide);
					tess.addVertexWithUV(posSideZOffset, yMax, zMid, maxXSide, minYSide);
				}

				if (renderTop)
				{
					tess.addVertexWithUV(negSideZOffset, yMax + vertSideZOffset, zMin, maxXSide, minYSide);
					tess.addVertexWithUV(negSideZOffset, yMax + vertSideZOffset, zMid, maxXSide, midYSide);
					tess.addVertexWithUV(posSideZOffset, yMax + vertSideZOffset, zMid, minXSide, midYSide);
					tess.addVertexWithUV(posSideZOffset, yMax + vertSideZOffset, zMin, minXSide, minYSide);
				}

				if (renderBottom)
				{
					tess.addVertexWithUV(negSideZOffset, yMin - vertSideZOffset, zMin, maxXSide, minYSide);
					tess.addVertexWithUV(negSideZOffset, yMin - vertSideZOffset, zMid, maxXSide, midYSide);
					tess.addVertexWithUV(posSideZOffset, yMin - vertSideZOffset, zMid, minXSide, midYSide);
					tess.addVertexWithUV(posSideZOffset, yMin - vertSideZOffset, zMin, minXSide, minYSide);
				}
			}
			else if (!connectedNegZ && connectedPosZ)
			{
				tess.setColorOpaque_F(red, green, blue);
				drawBox(tess, xMid, yMin, zMid, xMid, yMax, zMax, midXGlass, minYGlass, maxXGlass, maxYGlass, offset, 0);
				drawBox(tess, xMid, yMin, zMid, xMid, yMax, zMax, midXGlass, minYGlass, maxXGlass, maxYGlass, -offset, 0);

				tess.setColorOpaque_F(1, 1, 1);
				drawBox(tess, xMid, yMin, zMid, xMid, yMax, zMax, midXStreaks, minYStreaks, maxXStreaks, maxYStreaks, offset, 0);
				drawBox(tess, xMid, yMin, zMid, xMid, yMax, zMax, midXStreaks, minYStreaks, maxXStreaks, maxYStreaks, -offset, 0);
				
				drawBox(tess, xMid, yMin, zMid, xMid, yMax, zMax, midXWest, minYWest, maxXWest, maxYWest, offset, 0);
				drawBox(tess, xMid, yMin, zMid, xMid, yMax, zMax, midXWest, minYWest, maxXWest, maxYWest, -offset, 0);

				if (!connectedPosX && !connectedNegX)
				{
					tess.addVertexWithUV(posSideZOffset, yMax, zMid, minXSide, minYSide);
					tess.addVertexWithUV(posSideZOffset, yMin, zMid, minXSide, maxYSide);
					tess.addVertexWithUV(negSideZOffset, yMin, zMid, maxXSide, maxYSide);
					tess.addVertexWithUV(negSideZOffset, yMax, zMid, maxXSide, minYSide);
				}

				if (renderTop)
				{
					tess.addVertexWithUV(negSideZOffset, yMax + vertSideZOffset, zMid, minXSide, midYSide);
					tess.addVertexWithUV(negSideZOffset, yMax + vertSideZOffset, zMax, minXSide, maxYSide);
					tess.addVertexWithUV(posSideZOffset, yMax + vertSideZOffset, zMax, maxXSide, maxYSide);
					tess.addVertexWithUV(posSideZOffset, yMax + vertSideZOffset, zMid, maxXSide, midYSide);
				}

				if (renderBottom)
				{
					tess.addVertexWithUV(negSideZOffset, yMin - vertSideZOffset, zMid, minXSide, midYSide);
					tess.addVertexWithUV(negSideZOffset, yMin - vertSideZOffset, zMax, minXSide, maxYSide);
					tess.addVertexWithUV(posSideZOffset, yMin - vertSideZOffset, zMax, maxXSide, maxYSide);
					tess.addVertexWithUV(posSideZOffset, yMin - vertSideZOffset, zMid, maxXSide, midYSide);
				}
			}
		}
		else
		{
			tess.setColorOpaque_F(red, green, blue);
			drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMax, minXGlass, minYGlass, maxXGlass, maxYGlass, offset, 0);
			drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMax, minXGlass, minYGlass, maxXGlass, maxYGlass, -offset, 0);

			tess.setColorOpaque_F(1, 1, 1);
			drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMax, minXStreaks, minYStreaks, maxXStreaks, maxYStreaks, offset, 0);
			drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMax, minXStreaks, minYStreaks, maxXStreaks, maxYStreaks, -offset, 0);
			
			drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMax, minXWest, minYWest, maxXWest, maxYWest, offset, 0);
			drawBox(tess, xMid, yMin, zMin, xMid, yMax, zMax, minXWest, minYWest, maxXWest, maxYWest, -offset, 0);

			if (!connectedPosZ && !connectedNegZ)
			{
				drawBox(tess, negSideZOffset, yMin, zMin, posSideZOffset, yMax, zMin, minXSide, minYSide, maxXSide, maxYSide, 0, 0);
				
				drawBox(tess, negSideZOffset, yMin, zMax, posSideZOffset, yMax, zMax, minXSide, minYSide, maxXSide, maxYSide, 0, 0, true);
			}

			if (renderTop)
			{
				tess.addVertexWithUV(posSideZOffset, yMax + vertSideZOffset, zMax, maxXSide, maxYSide);
				tess.addVertexWithUV(posSideZOffset, yMax + vertSideZOffset, zMin, maxXSide, minYSide);
				tess.addVertexWithUV(negSideZOffset, yMax + vertSideZOffset, zMin, minXSide, minYSide);
				tess.addVertexWithUV(negSideZOffset, yMax + vertSideZOffset, zMax, minXSide, maxYSide);
			}
			// TODO: fix slightly awkward rendering when pane on top/bottom doesn't connect on both sides
			if (renderBottom)
			{
				tess.addVertexWithUV(posSideZOffset, yMin - vertSideZOffset, zMax, maxXSide, maxYSide);
				tess.addVertexWithUV(posSideZOffset, yMin - vertSideZOffset, zMin, maxXSide, minYSide);
				tess.addVertexWithUV(negSideZOffset, yMin - vertSideZOffset, zMin, minXSide, minYSide);
				tess.addVertexWithUV(negSideZOffset, yMin - vertSideZOffset, zMax, minXSide, maxYSide);
			}
		}

		return true;
	}

	private void drawBox(Tessellator t, double xMin, double yMin, double zMin,
													double xMax, double yMax, double zMax,
													double uMin, double vMin,
													double uMax, double vMax,
													double xOff, double zOff)
	{
		drawBox(t, xMin, yMin, zMin, xMax, yMax, zMax, uMin, vMin, uMax, uMax, xOff, zOff, (xOff > 0) | zOff < 0);
	}
	
	private void drawBox(Tessellator t, double xMin, double yMin, double zMin,
													double xMax, double yMax, double zMax,
													double uMin, double vMin,
													double uMax, double vMax,
													double xOff, double zOff,
													boolean backwards)
	{
		if (!backwards)
		{
			t.addVertexWithUV(xMin + xOff, yMax, zMin + zOff, uMin, vMin);
			t.addVertexWithUV(xMin + xOff, yMin, zMin + zOff, uMin, vMax);
			t.addVertexWithUV(xMax + xOff, yMin, zMax + zOff, uMax, vMax);
			t.addVertexWithUV(xMax + xOff, yMax, zMax + zOff, uMax, vMin);
		}
		else
		{
			t.addVertexWithUV(xMin + xOff, yMin, zMin + zOff, uMin, vMin);
			t.addVertexWithUV(xMin + xOff, yMax, zMin + zOff, uMin, vMax);
			t.addVertexWithUV(xMax + xOff, yMax, zMax + zOff, uMax, vMax);
			t.addVertexWithUV(xMax + xOff, yMin, zMax + zOff, uMax, vMin);
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}

	@Override
	public int getRenderId()
	{
		return MineFactoryReloadedCore.renderIdFactoryGlassPane;
	}
}
