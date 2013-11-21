package powercrystals.minefactoryreloaded.render.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class FactoryFluidOverlayRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return (type.ordinal() < ItemRenderType.FIRST_PERSON_MAP.ordinal());
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return handleRenderType(item, type) & helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Item iconItem = item.getItem();
		IFluidContainerItem fluidItem = (IFluidContainerItem)iconItem;
		Icon icon = iconItem.getIcon(item, 0);
		Icon mask = iconItem.getIcon(item, 1);
		FluidStack liquid = fluidItem.getFluid(item);
		boolean hasLiquid = liquid != null;
		Icon fluid = hasLiquid ? liquid != null ? liquid.getFluid().getIcon(liquid) : null : mask;
		int liquidSheet = hasLiquid & liquid != null ? liquid.getFluid().getSpriteNumber() : 0;
		int colorMult = hasLiquid & liquid != null ? liquid.getFluid().getColor(liquid) : 0xFFFFFF;
		if (fluid == null) {
			fluid = Block.lavaMoving.getIcon(2, 0);
			liquidSheet = 0;
			colorMult = 0x3F3F3F;
		}

		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
		GL11.glPushMatrix();

		Tessellator tessellator = Tessellator.instance;

		float iconMinX = icon.getMinU();
		float iconMaxX = icon.getMaxU();
		float iconMinY = icon.getMinV();
		float iconMaxY = icon.getMaxV();

		float maskMinX = mask.getMinU();
		float maskMaxX = mask.getMaxU();
		float maskMinY = mask.getMinV();
		float maskMaxY = mask.getMaxV();

		float fluidMinX = fluid.getMinU();
		float fluidMaxX = fluid.getMaxU();
		float fluidMinY = fluid.getMinV();
		float fluidMaxY = fluid.getMaxV();

		if (type == ItemRenderType.INVENTORY) {
			GL11.glDisable(GL11.GL_LIGHTING);

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0,  16, 0, iconMinX, iconMaxY);
			tessellator.addVertexWithUV(16, 16, 0, iconMaxX, iconMaxY);
			tessellator.addVertexWithUV(16,  0, 0, iconMaxX, iconMinY);
			tessellator.addVertexWithUV(0,   0, 0, iconMinX, iconMinY);
			tessellator.draw();

			if (hasLiquid) {
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(0,  16, 0.001, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(16, 16, 0.001, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(16,  0, 0.001, maskMaxX, maskMinY);
				tessellator.addVertexWithUV(0,   0, 0.001, maskMinX, maskMinY);
				tessellator.draw();

				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDepthMask(false);
				bindTexture(renderEngine, liquidSheet);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);

				tessellator.startDrawingQuads();
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0,  16, 0.001, fluidMinX, fluidMaxY);
				tessellator.addVertexWithUV(16, 16, 0.001, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(16,  0, 0.001, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(0,   0, 0.001, fluidMinX, fluidMinY);
				tessellator.draw();

				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		} else {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);

			if (type == ItemRenderType.ENTITY) {
				GL11.glTranslatef(0.5f, 4 / -16f, 0);
				GL11.glRotatef(180, 0, 1, 0);
			}

			ItemRenderer.renderItemIn2D(tessellator, iconMaxX, iconMinY, iconMinX, iconMaxY, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

			if (hasLiquid) {
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
				tessellator.addVertexWithUV(0, 0,  0.001, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(1, 0,  0.001, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(1, 1,  0.001, maskMinX, maskMinY);
				tessellator.addVertexWithUV(0, 1,  0.001, maskMaxX, maskMinY);
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
				tessellator.addVertexWithUV(0, 1, -0.0635, maskMaxX, maskMinY);
				tessellator.addVertexWithUV(1, 1, -0.0635, maskMinX, maskMinY);
				tessellator.addVertexWithUV(1, 0, -0.0635, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(0, 0, -0.0635, maskMaxX, maskMaxY);
				tessellator.draw();

				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDepthMask(false);
				bindTexture(renderEngine, liquidSheet);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);

				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 0,  0.001, fluidMinX, fluidMaxY);
				tessellator.addVertexWithUV(1, 0,  0.001, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(1, 1,  0.001, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(0, 1,  0.001, fluidMinX, fluidMinY);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 1, -0.0635, fluidMinX, fluidMinY);
				tessellator.addVertexWithUV(1, 1, -0.0635, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(1, 0, -0.0635, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(0, 0, -0.0635, fluidMinX, fluidMaxY);
				tessellator.draw();

				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
		GL11.glPopMatrix();
	}

	protected void bindTexture(TextureManager renderEngine, int spriteNumber)
	{
		if (spriteNumber == 0)
			renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		else
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, spriteNumber);
	}

}
