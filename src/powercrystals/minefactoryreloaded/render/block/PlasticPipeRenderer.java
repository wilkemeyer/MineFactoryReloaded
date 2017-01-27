package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.lib.render.RenderHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlasticPipeRenderer extends TileEntitySpecialRenderer<TileEntityPlasticPipe> implements IItemRenderer, IPerspectiveAwareModel {
	
	protected static CCModel base;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] gripO = new CCModel[6];
	protected static CCModel[] gripI = new CCModel[6];
	protected static CCModel[] gripP = new CCModel[6];

	public static final ResourceLocation textureLocation = new ResourceLocation(MineFactoryReloadedCore.modId + ":blocks/tile.mfr.cable.plastic");
	public static IconTransformation uvt;

	private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformations;

	static {
		try {
			Map<String, CCModel> cableModels = CCOBJParser.parseObjModels(MineFactoryReloadedCore.class.
							getResourceAsStream("/powercrystals/minefactoryreloaded/models/PlasticPipe.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			gripO[5] = cableModels.get("gripO").backfacedCopy();
			calculateSidedModels(gripO, p);

			gripI[5] = cableModels.get("gripI").backfacedCopy();
			calculateSidedModels(gripI, p);

			gripP[5] = cableModels.get("gripP").backfacedCopy();
			calculateSidedModels(gripP, p);
		} catch (Throwable ex) { ex.printStackTrace(); }

		TRSRTransformation thirdPerson = TransformUtils.get(0, 2.5f, 1f, 90, 0, 0, 0.375f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.get(0, 0, 0, 30, 135, 0, 0.625f));
		builder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.get(0, 3, 0, 0, 0, 0, 0.25f));
		builder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.get(0, 0, 0, 0, 90, 0, 0.5f));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.leftify(thirdPerson));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.get(3, 1, 0, 60, 0, 0, 0.4f));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.get(3, 1, 0, 60, 0, 0, 0.4f));
		transformations = builder.build();
	}

	private static void calculateSidedModels(CCModel[] m, Vector3 p) {
		
		compute(m[4] = m[5].copy().apply(new Rotation(Math.PI * 1.0, 0, 1, 0)));
		compute(m[3] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 1, 0)));
		compute(m[2] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 1, 0)));
		compute(m[1] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 0, 1).with(new Rotation(Math.PI, 0, 1, 0))));
		compute(m[0] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 0, 1)));
		compute(m[5]);
	}

	private static void compute(CCModel m) {
		
		m.computeNormals();
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.computeLighting(LightModel.standardLightModel);
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void updateUVT(TextureAtlasSprite icon) {
		uvt = new IconTransformation(icon);
	}

	@Override
	public void renderTileEntityAt(TileEntityPlasticPipe te, double x, double y, double z, float partialTicks, int destroyStage) {

		GlStateManager.pushMatrix();

		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled())
		{
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		}
		else
		{
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
		ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		TextureUtils.bindBlockTexture();

		base.render(ccrs, uvt);
		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i --> 0; ) {
			EnumFacing f = dirs[i];
			if (te.isInterfacing(f))
			{
				int side = f.ordinal();
				switch (te.interfaceMode(f)) {
					case 2: // cable
						cable[side].render(ccrs, uvt);
						break;
					case 1: // IFluidHandler
						iface[side].render(ccrs, uvt);
						int state = te.getMode(side);
						if ((state & 2) == 2)
							if (te.isPowered())
								gripI[side].render(ccrs, uvt);
							else
								gripP[side].render(ccrs, uvt);
						else
							gripO[side].render(ccrs, uvt);
						break;
					default:
						break;
				}
			}
		}
		
		ccrs.draw();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

		GlStateManager.popMatrix();
		
		GlStateManager.popMatrix();
	}
	@Override
	public void renderItem(ItemStack item) {

		GlStateManager.pushMatrix();

		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		TextureUtils.bindBlockTexture();

		ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		base.render(ccrs, uvt);
		cable[2].render(ccrs, uvt);
		cable[3].render(ccrs, uvt);

		ccrs.draw();

		GlStateManager.popMatrix();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transformations, cameraTransformType);
	}

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
