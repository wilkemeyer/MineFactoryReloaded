package powercrystals.minefactoryreloaded.render.model;

import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.SpriteSheetManager;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlass;
import powercrystals.minefactoryreloaded.core.MFRUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FactoryGlassPaneModel implements IModel {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MineFactoryReloadedCore.modId + ":stained_glass_pane", "normal");

	public static final int FULL_FRAME = 0;

	public static final IModel MODEL = new FactoryGlassPaneModel();

	@Override
	public Collection<ResourceLocation> getDependencies() {

		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {

		return Collections.emptyList();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		return new FactoryGlassPaneBakedModel();
	}

	@Override
	public IModelState getDefaultState() {

		return TransformUtils.DEFAULT_BLOCK;
	}

	private static class FactoryGlassPaneBakedModel implements IBakedModel {

		private CCModel post;
		private Map<EnumFacing, CCModel> sideModels = new HashMap<>();
		//TODO all this color stuff is only required because PlanarFaceBakery doesn't support setting tintindexes - review and either change or keep
		private Cache<EnumDyeColor, Map<EnumFacing, CCModel>> coreCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();
		private Cache<Integer, CCModel> frameCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

		public FactoryGlassPaneBakedModel() {

			post = CCModel.quadModel(24);
			post.generateBlock(0, 0.4375, 0, 0.4375, 0.5625, 1, 0.5625).computeNormals();

			CCModel north = CCModel.quadModel(24).generateBlock(0, 0.4375, 0, 0, 0.5625, 1, 0.4375).computeNormals();
			CCModel south = CCModel.quadModel(24).generateBlock(0, 0.4375, 0, 0.5625, 0.5625, 1, 1).computeNormals();
			CCModel west = CCModel.quadModel(24).generateBlock(0, 0, 0, 0.4375, 0.4375, 1, 0.5625).computeNormals();
			CCModel east = CCModel.quadModel(24).generateBlock(0, 0.5625, 0, 0.4375, 1, 1, 0.5625).computeNormals();

			sideModels.put(EnumFacing.NORTH, north);
			sideModels.put(EnumFacing.SOUTH, south);
			sideModels.put(EnumFacing.WEST, west);
			sideModels.put(EnumFacing.EAST, east);
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {

			if (side != null) {
				return Collections.emptyList();
			}

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			//TODO color support
			//TODO textures
			//TODO don't return model faces that are not to be displayed

			post.render(ccrs, new IconTransformation(FactoryGlassModel.spriteSheet.getSprite(FULL_FRAME)));

			if (state.getValue(BlockPane.NORTH)) {
				sideModels.get(EnumFacing.NORTH).render(ccrs, new IconTransformation(FactoryGlassModel.spriteSheet.getSprite(FULL_FRAME)));
			}

			if (state.getValue(BlockPane.SOUTH)) {
				sideModels.get(EnumFacing.SOUTH).render(ccrs, new IconTransformation(FactoryGlassModel.spriteSheet.getSprite(FULL_FRAME)));
			}

			if (state.getValue(BlockPane.WEST)) {
				sideModels.get(EnumFacing.WEST).render(ccrs, new IconTransformation(FactoryGlassModel.spriteSheet.getSprite(FULL_FRAME)));
			}

			if (state.getValue(BlockPane.EAST)) {
				sideModels.get(EnumFacing.EAST).render(ccrs, new IconTransformation(FactoryGlassModel.spriteSheet.getSprite(FULL_FRAME)));
			}

			buffer.finishDrawing();
			return buffer.bake();
		}

		@Override
		public boolean isAmbientOcclusion() {

			return true;
		}

		@Override
		public boolean isGui3d() {

			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {

			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {

			return FactoryGlassModel.spriteSheet.getSprite(FULL_FRAME);
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
}
