package powercrystals.minefactoryreloaded.render.model;

import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.texture.SpriteSheetManager;
import codechicken.lib.util.TransformUtils;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
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

public class FactoryGlassModel implements IModel {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MineFactoryReloadedCore.modId + ":stained_glass", "normal");

	private static final ResourceLocation SPRITE_LOCATION = new ResourceLocation(MineFactoryReloadedCore.textureFolder + "blocks/tile.mfr.stainedglass.png");
	public static final int FULL_FRAME = 0;
	public static SpriteSheetManager.SpriteSheet spriteSheet = SpriteSheetManager.getSheet(8, 8, SPRITE_LOCATION);;

	static {

		for(int i=0; i < 64; i++)
			spriteSheet.setupSprite(i); //TODO shouldn't this really be done by CCL itself?
	}

	public static final IModel MODEL = new FactoryGlassModel();

	@Override
	public Collection<ResourceLocation> getDependencies() {

		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {

		return ImmutableList.of(SPRITE_LOCATION);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		return new FactoryGlassBakedModel();
	}

	@Override
	public IModelState getDefaultState() {

		return TransformUtils.DEFAULT_BLOCK;
	}

	static TextureAtlasSprite getSpriteByCTMValue(int ctmValue) {

		int index = (ctmValue & 15);
		ctmValue = ctmValue >> 4;
		int w;
		switch (index) {
			case 3: // bottom right connection
				index ^= ((ctmValue & 1) << 4); // bithack: add 16 if connection
				break;
			case 5: // top right connection
				index ^= ((ctmValue & 8) << 1); // bithack: add 16 if connection
				break;
			case 7: // left empty
				w = ctmValue & 9;
				index ^= ((w & (w << 3)) << 1); // bithack: add 16 if both connections
				if ((w == 1) | w == 8) // bottom right, top right
					index = 32 | (w >> 3);
				break;
			case 10: // bottom left connection
				index ^= ((ctmValue & 2) << 3); // bithack: add 16 if connection
				break;
			case 11: // top empty
				w = ctmValue & 3;
				index ^= ((w & (w << 1)) << 3); // bithack: add 16 if both connections
				if ((w == 1) | w == 2) // bottom right, bottom left
					index = 34 | (w >> 1);
				break;
			case 12: // top left connection
				index ^= ((ctmValue & 4) << 2); // bithack: add 16 if connection
				break;
			case 13: // bottom empty
				w = ctmValue & 12;
				index ^= ((w & (w << 1)) << 1); // bithack: add 16 if both connections
				if ((w == 4) | w == 8) // top left, top right
					index = 36 | (w >> 3);
				break;
			case 14: // right empty
				w = ctmValue & 6;
				index ^= ((w & (w << 1)) << 2); // bithack: add 16 if both connections
				if ((w == 2) | w == 4) // bottom left, top left
					index = 38 | (w >> 2);
				break;
			case 15: // all sides
				index = 40 + ctmValue;
			default:
		}
		return spriteSheet.getSprite(index);
	}

	private static class FactoryGlassBakedModel implements IBakedModel {

		private Cache<EnumDyeColor, Map<EnumFacing,List<BakedQuad>>> coreCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();
		private Cache<Integer, BakedQuad> frameCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {

			if (side == null) {
				return Collections.emptyList();
			}

			List<BakedQuad> quads = new ArrayList<>();
			quads.addAll(getCoreQuads(state.getValue(BlockFactoryGlass.COLOR), side));
			IExtendedBlockState extState = (IExtendedBlockState) state;
			quads.add(getFrameQuadForSide(side, extState.getValue(BlockFactoryGlass.CTM_VALUE[side.ordinal()])));
			return quads;
		}

		private BakedQuad getFrameQuadForSide(EnumFacing side, int ctmValue) {

			int key = (side.ordinal() << 8) + ctmValue;

			BakedQuad quad = frameCache.getIfPresent(key);
			if (quad == null) {
				quad = PlanarFaceBakery.bakeFace(side, getSpriteByCTMValue(ctmValue));

				frameCache.put(key, quad);
			}

			return quad;
		}

		private List<BakedQuad> getCoreQuads(EnumDyeColor color, EnumFacing side) {

			Map<EnumFacing, List<BakedQuad>> coreQuads = coreCache.getIfPresent(color);

			if (coreQuads == null) {
				coreQuads = new HashMap<>();
				int colorValue = (MFRUtil.COLORS[color.ordinal()] << 8) + 0xFF;
				for (EnumFacing facing : EnumFacing.VALUES) {
					List<BakedQuad> faceQuads = new ArrayList<>();
					faceQuads.add(PlanarFaceBakery.bakeFace(facing, spriteSheet.getSprite(63), DefaultVertexFormats.ITEM, colorValue));
					faceQuads.add(PlanarFaceBakery.bakeFace(facing, spriteSheet.getSprite(62)));
					coreQuads.put(facing, faceQuads);
				}

				coreCache.put(color, coreQuads);
			}
			return coreQuads.get(side);
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

			return spriteSheet.getSprite(FULL_FRAME);
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
