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

		private Cache<Long, List<BakedQuad>> cache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();
		private CCModel post;
		private Map<EnumFacing, CCModel> sideModels = new HashMap<>();
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

			long key = getCacheKey(state);
			List<BakedQuad> quads = cache.getIfPresent(key);

			if (quads == null) {
				BakingVertexBuffer buffer = BakingVertexBuffer.create();
				buffer.begin(7, DefaultVertexFormats.ITEM);
				CCRenderState ccrs = CCRenderState.instance();
				ccrs.reset();
				ccrs.bind(buffer);

				renderPane(state, ccrs);

				buffer.finishDrawing();
				quads = buffer.bake();
				cache.put(key, quads);
			}
			return quads;
		}

		private long getCacheKey(IBlockState state) {

			IExtendedBlockState exState = (IExtendedBlockState) state;

			long key = 0;

			key |= exState.getValue(BlockFactoryGlassPane.COLOR).getMetadata();
			key = key << 14;
			key |= exState.getValue(BlockFactoryGlassPane.FACES);
			key = key << 8;
			key |= exState.getValue(BlockFactoryGlassPane.CTM_VALUE[0]);
			key = key << 8;
			key |= exState.getValue(BlockFactoryGlassPane.CTM_VALUE[1]);
			key = key << 8;
			key |= exState.getValue(BlockFactoryGlassPane.CTM_VALUE[2]);
			key = key << 8;
			key |= exState.getValue(BlockFactoryGlassPane.CTM_VALUE[3]);
			key = key << 1;
			key |= exState.getValue(BlockPane.NORTH) ? 1 : 0;
			key = key << 1;
			key |= exState.getValue(BlockPane.SOUTH) ? 1 : 0;
			key = key << 1;
			key |= exState.getValue(BlockPane.WEST) ? 1 : 0;
			key = key << 1;
			key |= exState.getValue(BlockPane.EAST) ? 1 : 0;

			return key;
		}

		private void renderPane(@Nullable IBlockState state, CCRenderState ccrs) {

			IExtendedBlockState exState = (IExtendedBlockState) state;

			int color = (MFRUtil.COLORS[state.getValue(BlockFactoryGlassPane.COLOR).getMetadata()] << 8) + 0xFF;
			Map<EnumFacing, TextureAtlasSprite> overlayTextures = getOverlayTextures(exState);
			int ctmValueSouth = exState.getValue(BlockFactoryGlassPane.CTM_VALUE[0]);
			int ctmValueWest = exState.getValue(BlockFactoryGlassPane.CTM_VALUE[1]);
			Map<EnumFacing, Boolean> connections = getConnections(ctmValueSouth, ctmValueWest);

			renderPaneParts(exState, ccrs, color, overlayTextures, connections);
		}

		private void renderPaneParts(@Nullable IExtendedBlockState state, CCRenderState ccrs, int color, Map<EnumFacing, TextureAtlasSprite> overlayTextures,
				Map<EnumFacing, Boolean> connections) {

			int facesToShow = state.getValue(BlockFactoryGlassPane.FACES);
			renderPaneSide(ccrs, EnumFacing.NORTH, color, overlayTextures, connections, state.getValue(BlockPane.NORTH), facesToShow);
			renderPaneSide(ccrs, EnumFacing.SOUTH, color, overlayTextures, connections, state.getValue(BlockPane.SOUTH), facesToShow);
			renderPaneSide(ccrs, EnumFacing.WEST, color, overlayTextures, connections, state.getValue(BlockPane.WEST), facesToShow);
			renderPaneSide(ccrs, EnumFacing.EAST, color, overlayTextures, connections, state.getValue(BlockPane.EAST), facesToShow);

			boolean renderUp = shouldRenderFace(facesToShow, EnumFacing.UP);
			if (!connections.get(EnumFacing.UP) || renderUp) {
				renderPostFace(ccrs, EnumFacing.UP, FactoryGlassModel.spriteSheet.getSprite(61), color, !renderUp);
			}

			boolean renderDown = shouldRenderFace(facesToShow, EnumFacing.DOWN);
			if (!connections.get(EnumFacing.DOWN) || renderDown) {
				renderPostFace(ccrs, EnumFacing.DOWN, FactoryGlassModel.spriteSheet.getSprite(61), color, !renderDown);
			}
		}

		private Map<EnumFacing, TextureAtlasSprite> getOverlayTextures(IExtendedBlockState exState) {
			ImmutableMap.Builder<EnumFacing, TextureAtlasSprite> builder = ImmutableMap.builder();

			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				builder.put(facing, FactoryGlassModel.getSpriteByCTMValue(exState.getValue(BlockFactoryGlassPane.CTM_VALUE[facing.getHorizontalIndex()])));
			}
			return builder.build();
		}

		private Map<EnumFacing, Boolean> getConnections(int ctmValueSouth, int ctmValueWest) {

			Map<EnumFacing, Boolean> connections = new HashMap<>();
			connections.put(EnumFacing.UP, ((ctmValueSouth >> 2) & 1) == 1);
			connections.put(EnumFacing.DOWN, ((ctmValueSouth >> 1) & 1) == 1);
			connections.put(EnumFacing.WEST, ((ctmValueSouth >> 3) & 1) == 1);
			connections.put(EnumFacing.EAST, (ctmValueSouth & 1) == 1);
			connections.put(EnumFacing.NORTH, ((ctmValueWest >> 3) & 1) == 1);
			connections.put(EnumFacing.SOUTH, (ctmValueWest & 1) == 1);
			return connections;
		}

		private boolean shouldRenderFace(int facesToShow, EnumFacing side) {

			return ((facesToShow >> side.ordinal()) & 1) == 1;
		}

		private boolean shouldRenderBorder(int facesToShow, EnumFacing upDownSide, EnumFacing horizontalSide) {

			int offset = upDownSide == EnumFacing.UP ? 6 : 10;
			return ((facesToShow >> (offset + horizontalSide.getHorizontalIndex())) & 1) == 1;
		}

		private void renderPaneSide(CCRenderState ccrs, EnumFacing facing, int color, Map<EnumFacing, TextureAtlasSprite> overlayTextures,
				Map<EnumFacing, Boolean> connections, boolean renderPart, int facesToShow) {

			if (renderPart) {
				renderPanePart(ccrs, facing, overlayTextures, color, connections, facesToShow);
			} else {
				renderPostFace(ccrs, facing, overlayTextures.get(facing), color, true);
			}
		}

		private void renderPostFace(CCRenderState ccrs, EnumFacing facing, TextureAtlasSprite texture, int color, boolean renderOverlay) {

			renderModelFace(ccrs, post, facing, glassTexture, color);
			renderModelFace(ccrs, post, facing, glassStreaksTexture, color);
			if (renderOverlay)
				renderModelFace(ccrs, post, facing, texture);
		}

		private void renderPanePart(CCRenderState ccrs,	@Nullable EnumFacing side, Map<EnumFacing, TextureAtlasSprite> overlayTextures, int color,
				Map<EnumFacing, Boolean> connections, int facesToShow) {

			CCModel model = sideModels.get(side).copy();

			boolean renderFaceOnSide = shouldRenderFace(facesToShow, side);
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				//skip the face that's next to post and the face that's connected to another pane and is not supposed to be rendered (same color pane)
				if (facing == side.getOpposite() || (facing == side && connections.get(side) && !renderFaceOnSide))
					continue;

				renderGlassFaces(ccrs, model, facing, overlayTextures.get(facing), color, side != facing || !renderFaceOnSide);
			}

			boolean renderUp = shouldRenderFace(facesToShow, EnumFacing.UP);
			boolean renderBorderUp = shouldRenderBorder(facesToShow, EnumFacing.UP, side);
			if (!connections.get(EnumFacing.UP) || renderUp || renderBorderUp) {
				renderGlassFaces(ccrs, model, EnumFacing.UP, FactoryGlassModel.spriteSheet.getSprite(61), color, !renderUp || renderBorderUp);
			}

			boolean renderDown = shouldRenderFace(facesToShow, EnumFacing.DOWN);
			boolean renderBorderDown = shouldRenderBorder(facesToShow, EnumFacing.DOWN, side);
			if (!connections.get(EnumFacing.DOWN) || renderDown) {
				renderGlassFaces(ccrs, model, EnumFacing.DOWN, FactoryGlassModel.spriteSheet.getSprite(61), color, !renderDown || renderBorderDown);
			}
		}

		private void renderGlassFaces(CCRenderState ccrs, CCModel model, EnumFacing facing, TextureAtlasSprite overlayTexture, int color, boolean renderOverlay) {

			renderModelFace(ccrs, model, facing, glassTexture, color);
			renderModelFace(ccrs, model, facing, glassStreaksTexture);
			if (renderOverlay)
				renderModelFace(ccrs, model, facing, overlayTexture);
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