package powercrystals.minefactoryreloaded.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class EntityPinkSlimeRenderer extends RenderSlime {
	private static final ResourceLocation pinkSlimeTexture = new ResourceLocation(MineFactoryReloadedCore.mobTextureFolder + "pinkslime.png");
	
	public EntityPinkSlimeRenderer(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
	}

    @Override
	protected ResourceLocation getEntityTexture(EntitySlime par1EntitySlime)
    {
        return pinkSlimeTexture;
    }

}
