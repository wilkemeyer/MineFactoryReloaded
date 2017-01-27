package powercrystals.minefactoryreloaded.render.model;

import codechicken.lib.util.TransformUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PlasticCupModel implements IModel, IModelCustomData {


	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final IModel MODEL = new PlasticCupModel();

	private static final ResourceLocation BASE = new ResourceLocation("minefactoryreloaded:items/item.mfr.plastic.cup");
	private static final ResourceLocation MASK = new ResourceLocation("minefactoryreloaded:items/item.mfr.plastic.cup.fill");

	private final Fluid fluid;

	public PlasticCupModel() {

		this(null);
	}

	public PlasticCupModel(Fluid fluid) {

		this.fluid = fluid;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {

		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {

		return ImmutableList.of(BASE, MASK);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

		TRSRTransformation transform = state.apply(Optional.absent()).or(TRSRTransformation.identity());
		TextureAtlasSprite fluidSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (fluid != null) {
			fluidSprite = bakedTextureGetter.apply(fluid.getStill());
		}

		// build base (insidest)
		IBakedModel model = (new ItemLayerModel(ImmutableList.of(BASE))).bake(state, format, bakedTextureGetter);
		builder.addAll(model.getQuads(null, null, 0));

		if (fluidSprite != null) {
			TextureAtlasSprite liquid = bakedTextureGetter.apply(MASK);
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
		}

		return new BakedPlasticCup(this, builder.build(), fluidSprite, format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel>newHashMap());
	}

	@Override
	public IModelState getDefaultState() {

		return TransformUtils.DEFAULT_ITEM;
	}

	@Override
	public PlasticCupModel process(ImmutableMap<String, String> customData) {

		String fluidName = customData.get("fluid");
		Fluid fluid = FluidRegistry.getFluid(fluidName);

		if (fluid == null) {
			fluid = this.fluid;
		}

		// create new model with correct liquid
		return new PlasticCupModel(fluid);
	}

	public enum LoaderPlasticCup implements ICustomModelLoader {
		INSTANCE;

		@Override
		public boolean accepts(ResourceLocation modelLocation) {

			return modelLocation.getResourceDomain().equals("minefactoryreloaded") && modelLocation.getResourcePath().equals("plastic_cup");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) {

			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			// no need to clear cache since we create a new model instance
		}
	}

	private static final class BakedPlasticCupOverrideHandler extends ItemOverrideList {

		public static final BakedPlasticCupOverrideHandler INSTANCE = new BakedPlasticCupOverrideHandler();

		private BakedPlasticCupOverrideHandler() {

			super(ImmutableList.of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {

			BakedPlasticCup model = (BakedPlasticCup) originalModel;

			String name = "";

			if (stack.getTagCompound() != null) {
				name = stack.getTagCompound().getCompoundTag("fluid").getString("FluidName");
			}

			if (name.isEmpty()) {
				return originalModel;
			}

			if (!model.cache.containsKey(name)) {
				IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter 
						= location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

				IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, textureGetter);
				model.cache.put(name, bakedModel);
				return bakedModel;
			}

			return model.cache.get(name);
		}
	}

	private static final class BakedPlasticCup implements IPerspectiveAwareModel {

		private final PlasticCupModel parent;
		// FIXME: guava cache?
		private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private final ImmutableList<BakedQuad> quads;
		private final TextureAtlasSprite particle;
		private final VertexFormat format;

		public BakedPlasticCup(PlasticCupModel parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {

			this.quads = quads;
			this.particle = particle;
			this.format = format;
			this.parent = parent;
			this.transforms = transforms;
			this.cache = cache;
		}

		@Override
		public ItemOverrideList getOverrides() {

			return BakedPlasticCupOverrideHandler.INSTANCE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {

			return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {

			if (side == null) {
				return quads;
			}
			return ImmutableList.of();
		}

		public boolean isAmbientOcclusion() {

			return true;
		}

		public boolean isGui3d() {

			return false;
		}

		public boolean isBuiltInRenderer() {

			return false;
		}

		public TextureAtlasSprite getParticleTexture() {

			return particle;
		}

		public ItemCameraTransforms getItemCameraTransforms() {

			return ItemCameraTransforms.DEFAULT;
		}
	}
}
