package powercrystals.minefactoryreloaded.render.model;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import powercrystals.minefactoryreloaded.block.decor.BlockFactoryGlassPane;
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

		private TextureAtlasSprite glassTexture;
		private TextureAtlasSprite glassStreaksTexture;

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

			glassTexture = FactoryGlassModel.spriteSheet.getSprite(63);
			glassStreaksTexture = FactoryGlassModel.spriteSheet.getSprite(62);
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

			//TODO get rid of faces when two panes connect to each other
			//TODO fix top bottom textures to check for diagonal connecting (in ctm bits) before hiding them

			int color = (MFRUtil.COLORS[state.getValue(BlockFactoryGlassPane.COLOR).getMetadata()] << 8) + 0xFF;
			IExtendedBlockState exState = (IExtendedBlockState) state;
			ImmutableMap.Builder<EnumFacing, TextureAtlasSprite> builder = ImmutableMap.builder();

			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				builder.put(facing, FactoryGlassModel.getSpriteByCTMValue(exState.getValue(BlockFactoryGlassPane.CTM_VALUE[facing.getHorizontalIndex()])));
			}
			Map<EnumFacing, TextureAtlasSprite> sideTextures = builder.build();

			int ctmValue = exState.getValue(BlockFactoryGlassPane.CTM_VALUE[0]);
			boolean connectedUp = ((ctmValue >> 2) & 1) == 1;
			boolean connectedDown = ((ctmValue >> 1) & 1) == 1;

			if (state.getValue(BlockPane.NORTH)) {
				renderPaneSide(ccrs, EnumFacing.NORTH, sideTextures, color, connectedUp, connectedDown);
			} else {
				renderPostFace(ccrs, EnumFacing.NORTH, sideTextures.get(EnumFacing.NORTH), color);
			}

			if (state.getValue(BlockPane.SOUTH)) {
				renderPaneSide(ccrs, EnumFacing.SOUTH, sideTextures, color, connectedUp, connectedDown);
			} else {
				renderPostFace(ccrs, EnumFacing.SOUTH, sideTextures.get(EnumFacing.SOUTH), color);
			}

			if (state.getValue(BlockPane.WEST)) {
				renderPaneSide(ccrs, EnumFacing.WEST, sideTextures, color, connectedUp, connectedDown);
			} else {
				renderPostFace(ccrs, EnumFacing.WEST, sideTextures.get(EnumFacing.WEST), color);
			}

			if (state.getValue(BlockPane.EAST)) {
				renderPaneSide(ccrs, EnumFacing.EAST, sideTextures, color, connectedUp, connectedDown);
			} else {
				renderPostFace(ccrs, EnumFacing.EAST, sideTextures.get(EnumFacing.EAST), color);
			}

			if (!connectedUp) {
				renderPostFace(ccrs, EnumFacing.UP, FactoryGlassModel.spriteSheet.getSprite(61), color);
			}

			if (!connectedDown) {
				renderPostFace(ccrs, EnumFacing.DOWN, FactoryGlassModel.spriteSheet.getSprite(61), color);
			}

			buffer.finishDrawing();
			return buffer.bake();
		}

		private void renderPostFace(CCRenderState ccrs, EnumFacing facing, TextureAtlasSprite texture, int color) {

			renderModelFace(ccrs, post, facing, glassTexture, color);
			renderModelFace(ccrs, post, facing, glassStreaksTexture, color);
			renderModelFace(ccrs, post, facing, texture);
		}

		private void renderPaneSide(CCRenderState ccrs,
				@Nullable EnumFacing side, Map<EnumFacing, TextureAtlasSprite> sideTextures, int color, boolean connectedUp, boolean connectedDown) {

			CCModel model = sideModels.get(side).copy();

			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				//exclude side that's next to post
				if (facing != side.getOpposite()) {
					renderModelFace(ccrs, model, facing, glassTexture, color);
					renderModelFace(ccrs, model, facing, glassStreaksTexture, color);
					renderModelFace(ccrs, model, facing, sideTextures.get(facing));
				}
			}

			if (!connectedUp) {
				renderModelFace(ccrs, model, EnumFacing.UP, glassTexture, color);
				renderModelFace(ccrs, model, EnumFacing.UP, glassStreaksTexture, color);
				renderModelFace(ccrs, model, EnumFacing.UP, FactoryGlassModel.spriteSheet.getSprite(61));
			}

			if (!connectedDown) {
				renderModelFace(ccrs, model, EnumFacing.DOWN, glassTexture, color);
				renderModelFace(ccrs, model, EnumFacing.DOWN, glassStreaksTexture, color);
				renderModelFace(ccrs, model, EnumFacing.DOWN, FactoryGlassModel.spriteSheet.getSprite(61));
			}
		}

		private void renderModelFace(CCRenderState ccrs, CCModel model, EnumFacing facing, TextureAtlasSprite texture) {
			model.render(ccrs, facing.ordinal() * 4, (facing.ordinal() + 1) * 4, new IconTransformation(texture));
		}

		private void renderModelFace(CCRenderState ccrs, CCModel model, EnumFacing facing, TextureAtlasSprite texture, int color) {
			model.copy().setColour(color).render(ccrs, facing.ordinal() * 4, (facing.ordinal() + 1) * 4, new IconTransformation(texture));
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
