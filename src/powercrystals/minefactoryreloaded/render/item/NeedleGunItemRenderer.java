package powercrystals.minefactoryreloaded.render.item;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class NeedleGunItemRenderer implements IItemRenderer
{
	private static final ResourceLocation needleGun =
			new ResourceLocation(MineFactoryReloadedCore.modelTextureFolder + "NeedleGun.png");
	private IModelCustom _model;
	
	public NeedleGunItemRenderer()
	{
		try
		{
			_model = AdvancedModelLoader.loadModel(new ResourceLocation(
					MineFactoryReloadedCore.modelFolder + "NeedleGun.obj"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return helper != ItemRendererHelper.EQUIPPED_BLOCK;
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		TextureManager renderengine = Minecraft.getMinecraft().renderEngine;
		
		if(renderengine != null)
		{
			renderengine.bindTexture(needleGun);
		}
		
		GL11.glPushMatrix();
		
		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glRotatef(270, 0, 1, 0);
			GL11.glRotatef(300, 1, 0, 0);
			GL11.glTranslatef(-0.2F, 0.5F, 0.2F);
		}
		else if(type == ItemRenderType.EQUIPPED)
		{
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glTranslatef(1.0F, 0, 0.2F);
		}
		else
		{
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glTranslatef(0, -0.4F, 0);
		}
		
		GL11.glScalef(0.03F, 0.03F, 0.03F);
		
		_model.renderAll();
		
		GL11.glPopMatrix();
	}
}
