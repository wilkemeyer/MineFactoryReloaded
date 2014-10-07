package powercrystals.minefactoryreloaded.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class BlockTankRenderer implements ISimpleBlockRenderingHandler, IItemRenderer
{

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return type.ordinal() < ItemRenderType.FIRST_PERSON_MAP.ordinal();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		RenderBlocks renderer = (RenderBlocks)data[0];

		Block block = Block.getBlockFromItem(item.getItem());

		GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		switch (type)
		{
		case EQUIPPED_FIRST_PERSON:
		case EQUIPPED:
			GL11.glTranslated(0.5, 0.5, 0.5);
			break;
		default:
		}

		renderTank(block, item, renderer);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderTank(Block block, ItemStack stack, RenderBlocks renderer)
	{
        FluidStack fluid = ((IFluidContainerItem)stack.getItem()).drain(stack, 1, false);
		int color = fluid == null ? 0xFFFFFF : fluid.getFluid().getColor(fluid);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		IIcon iconFluid = fluid != null ? fluid.getFluid().getIcon(fluid) :
			renderer.getBlockIconFromSideAndMetadata(block, 2, 3);
		double minXFluid = iconFluid.getMinU();
		double maxXFluid = iconFluid.getMaxU();
		double minYFluid = iconFluid.getMinV();
		double maxYFluid = iconFluid.getMaxV();

        Tessellator tessellator = Tessellator.instance;

		final double xMin = 0, xMax = 1;
		final double yMin = 0, yMax = 1;
		final double zMin = 0, zMax = 1;
		final double offset = 0.003;

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, 0));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.setColorOpaque_F(1, 1, 1);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, 0));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMax, yMax, zMin + offset, maxXFluid, minYFluid);
		tessellator.addVertexWithUV(xMax, yMin, zMin + offset, maxXFluid, maxYFluid);
		tessellator.addVertexWithUV(xMin, yMin, zMin + offset, minXFluid, maxYFluid);
		tessellator.addVertexWithUV(xMin, yMax, zMin + offset, minXFluid, minYFluid);
		tessellator.setColorOpaque_F(1, 1, 1);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, 0));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin, yMax, zMax - offset, minXFluid, minYFluid);
		tessellator.addVertexWithUV(xMin, yMin, zMax - offset, minXFluid, maxYFluid);
		tessellator.addVertexWithUV(xMax, yMin, zMax - offset, maxXFluid, maxYFluid);
		tessellator.addVertexWithUV(xMax, yMax, zMax - offset, maxXFluid, minYFluid);
		tessellator.setColorOpaque_F(1, 1, 1);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, 0));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMin + offset, yMin, zMax, maxXFluid, maxYFluid);
		tessellator.addVertexWithUV(xMin + offset, yMax, zMax, maxXFluid, minYFluid);
		tessellator.addVertexWithUV(xMin + offset, yMax, zMin, minXFluid, minYFluid);
		tessellator.addVertexWithUV(xMin + offset, yMin, zMin, minXFluid, maxYFluid);
		tessellator.setColorOpaque_F(1, 1, 1);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, 0));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.setColorOpaque_F(red, green, blue);
		tessellator.addVertexWithUV(xMax - offset, yMin, zMin, minXFluid, maxYFluid);
		tessellator.addVertexWithUV(xMax - offset, yMax, zMin, minXFluid, minYFluid);
		tessellator.addVertexWithUV(xMax - offset, yMax, zMax, maxXFluid, minYFluid);
		tessellator.addVertexWithUV(xMax - offset, yMin, zMax, maxXFluid, maxYFluid);
		tessellator.setColorOpaque_F(1, 1, 1);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, 0));
		tessellator.draw();
	}

	@Override public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer){}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (renderer.hasOverrideBlockTexture())
		{ // usually: block is being broken
			renderer.renderFaceYNeg(block, x, y, z, null);
			renderer.renderFaceYPos(block, x, y, z, null);
			renderer.renderFaceZNeg(block, x, y, z, null);
			renderer.renderFaceZPos(block, x, y, z, null);
			renderer.renderFaceXNeg(block, x, y, z, null);
			renderer.renderFaceXPos(block, x, y, z, null);
			return true;
		}
        Tessellator tessellator = Tessellator.instance;
		int b = block.getLightValue(blockAccess, x, y, z);
		int worldHeight = blockAccess.getHeight();

		int color = block.colorMultiplier(blockAccess, x, y, z);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		IIcon iconFluid = block.getIcon(blockAccess, x, y, z, 3);
		double minXFluid = iconFluid.getMinU();
		double maxXFluid = iconFluid.getMaxU();
		double minYFluid = iconFluid.getMinV();
		double maxYFluid = iconFluid.getMaxV();

		final double xMin = x, xMax = x + 1;
		final double yMin = y, yMax = y + 1;
		final double zMin = z, zMax = z + 1;
		final double offset = 0.003;

		boolean render = renderer.renderAllFaces;

		boolean[] renderSide = {
				render || y <= 0 || block.shouldSideBeRendered(blockAccess, x, y - 1, z, 0),
						render || y >= worldHeight || block.shouldSideBeRendered(blockAccess, x, y + 1, z, 1),
						render || block.shouldSideBeRendered(blockAccess, x, y, z - 1, 2),
						render || block.shouldSideBeRendered(blockAccess, x, y, z + 1, 3),
						render || block.shouldSideBeRendered(blockAccess, x - 1, y, z, 4),
						render || block.shouldSideBeRendered(blockAccess, x + 1, y, z, 5),
		};
		render = false;

		if (renderSide[0])
		{
			tessellator.setBrightness(Math.max(b, block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z)));
			tessellator.setColorOpaque_F(1, 1, 1);
			renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
			render = true;
		}

		if (renderSide[1])
		{
			tessellator.setBrightness(Math.max(b, block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z)));
			tessellator.setColorOpaque_F(1, 1, 1);
			renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, 0));
			render = true;
		}

		if (renderSide[2])
		{
			tessellator.setBrightness(Math.max(b, block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1)));
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMax, yMax, zMin + offset, maxXFluid, minYFluid);
			tessellator.addVertexWithUV(xMax, yMin, zMin + offset, maxXFluid, maxYFluid);
			tessellator.addVertexWithUV(xMin, yMin, zMin + offset, minXFluid, maxYFluid);
			tessellator.addVertexWithUV(xMin, yMax, zMin + offset, minXFluid, minYFluid);

			tessellator.setColorOpaque_F(1, 1, 1);
			renderer.renderFaceZNeg(block, x, y, z, block.getIcon(2, 0));
			render = true;
		}

		if (renderSide[3])
		{
			tessellator.setBrightness(Math.max(b, block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1)));
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMin, yMax, zMax - offset, minXFluid, minYFluid);
			tessellator.addVertexWithUV(xMin, yMin, zMax - offset, minXFluid, maxYFluid);
			tessellator.addVertexWithUV(xMax, yMin, zMax - offset, maxXFluid, maxYFluid);
			tessellator.addVertexWithUV(xMax, yMax, zMax - offset, maxXFluid, minYFluid);

			tessellator.setColorOpaque_F(1, 1, 1);
			renderer.renderFaceZPos(block, x, y, z, block.getIcon(3, 0));
			render = true;
		}

		if (renderSide[4])
		{
			tessellator.setBrightness(Math.max(b, block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z)));
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMin + offset, yMin, zMax, maxXFluid, maxYFluid);
			tessellator.addVertexWithUV(xMin + offset, yMax, zMax, maxXFluid, minYFluid);
			tessellator.addVertexWithUV(xMin + offset, yMax, zMin, minXFluid, minYFluid);
			tessellator.addVertexWithUV(xMin + offset, yMin, zMin, minXFluid, maxYFluid);

			tessellator.setColorOpaque_F(1, 1, 1);
			renderer.renderFaceXNeg(block, x, y, z, block.getIcon(4, 0));
			render = true;
		}

		if (renderSide[5])
		{
			tessellator.setBrightness(Math.max(b, block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z)));
			tessellator.setColorOpaque_F(red, green, blue);
			tessellator.addVertexWithUV(xMax - offset, yMin, zMin, minXFluid, maxYFluid);
			tessellator.addVertexWithUV(xMax - offset, yMax, zMin, minXFluid, minYFluid);
			tessellator.addVertexWithUV(xMax - offset, yMax, zMax, maxXFluid, minYFluid);
			tessellator.addVertexWithUV(xMax - offset, yMin, zMax, maxXFluid, maxYFluid);

			tessellator.setColorOpaque_F(1, 1, 1);
			renderer.renderFaceXPos(block, x, y, z, block.getIcon(5, 0));
			render = true;
		}

        return render;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}

	@Override
	public int getRenderId()
	{
		return MineFactoryReloadedCore.renderIdFluidTank;
	}

}
