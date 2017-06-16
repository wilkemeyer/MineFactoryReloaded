package powercrystals.minefactoryreloaded.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;

public class RenderSafarinet extends RenderSnowball<EntitySafariNet> {

	public RenderSafarinet(RenderManager renderManagerIn, RenderItem itemRendererIn)
	{
		super(renderManagerIn, Items.POTATO, itemRendererIn);
	}

	public ItemStack getStackToRender(EntitySafariNet entityIn)
	{
		return entityIn.getStoredEntity();
	}

}
