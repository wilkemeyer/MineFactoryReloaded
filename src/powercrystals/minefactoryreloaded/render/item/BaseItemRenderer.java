package powercrystals.minefactoryreloaded.render.item;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemRenderer implements IItemRenderer, IPerspectiveAwareModel {

	@Override
	public void renderItem(ItemStack stack) {

		GlStateManager.pushMatrix();
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(-90 * (2 + 2), 0, 1, 0);

		drawModel(ccrs, stack);

		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

	protected abstract void drawModel(CCRenderState ccrs, ItemStack stack);
	
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {

		return new ArrayList<>();
	}

	@Override
	public boolean isAmbientOcclusion() {

		return false;
	}

	@Override
	public boolean isGui3d() {

		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {

		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {

		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {

		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {

		return ItemOverrideList.NONE;
	}
}
