package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelState;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import cofh.lib.render.RenderHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedNetCableRenderer extends TileEntitySpecialRenderer implements IItemRenderer, IPerspectiveAwareModel {

	protected static CCModel base;
	protected static CCModel cage;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] band  = new CCModel[6];
	protected static CCModel[] plate = new CCModel[6];
	protected static CCModel[] platef = new CCModel[6];
	protected static CCModel[] grip  = new CCModel[6];
	protected static CCModel[] wire  = new CCModel[6];
	protected static CCModel[] caps  = new CCModel[6];

	private static ResourceLocation textureLocation = new ResourceLocation(MineFactoryReloadedCore.textureFolder + "blocks/tile.mfr.cable.redstone.png");

	private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformations;

	private static boolean brightBand;
	
	static {
		try {
			Map<String, CCModel> cableModels = CCOBJParser.parseObjModels(MineFactoryReloadedCore.class.
							getResourceAsStream("/powercrystals/minefactoryreloaded/models/RedNetCable.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			cage = cableModels.get("cage").backfacedCopy();
			compute(cage);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			band[5] = cableModels.get("band").backfacedCopy();
			calculateSidedModels(band, p);

			plate[5] = cableModels.get("plate").backfacedCopy();
			calculateSidedModels(plate, p);

			platef[5] = cableModels.get("plateface").backfacedCopy();
			calculateSidedModels(platef, p);

			grip[5] = cableModels.get("grip").backfacedCopy();
			calculateSidedModels(grip, p);

			wire[5] = cableModels.get("wire").backfacedCopy();
			calculateSidedModels(wire, p);

			caps[5] = cableModels.get("cap").backfacedCopy();
			calculateSidedModels(caps, p);
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
		
		brightBand = MFRConfig.brightRednetBand.getBoolean(true);
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
		
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.computeNormals();
		m.computeLighting(LightModel.standardLightModel);
		m.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {

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
		ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		ccrs.setBrightness(getWorld(), te.getPos());

		TextureUtils.changeTexture(textureLocation);

		renderCable((TileEntityRedNetCable) te, ccrs);

		ccrs.draw();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();
	}

	private boolean renderCable(TileEntityRedNetCable cable, CCRenderState ccrs) {
		
		TileEntityRedNetEnergy _cond = null;

		int brightness = ccrs.brightness;
		int bandBrightness = brightBand ? 0xd00070 : brightness;

		base.render(ccrs);
		if (cable instanceof TileEntityRedNetEnergy) {
			cage.render(ccrs);
			_cond = (TileEntityRedNetEnergy)cable;
		}

		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i --> 0; ) {
			EnumFacing f = dirs[i];
			RedNetConnectionType state = cable.getCachedConnectionState(f);
			switch (state.flags & 31) {
				case 11: // isCable, isSingleSubnet
					ccrs.brightness = bandBrightness;
					band[i].setColour(cable.getSideColorValue(f));
					band[i].render(ccrs);
					ccrs.brightness = brightness;
				case 19: // isCable, isAllSubnets
					if (state.isSingleSubnet) {
						iface[i].render(ccrs);
						grip[i].render(ccrs);
					} else
						RedNetCableRenderer.cable[i].render(ccrs);
					break;
				case 13: // isPlate, isSingleSubnet
					ccrs.brightness = bandBrightness;
					band[i].setColour(cable.getSideColorValue(f));
					band[i].render(ccrs);
					platef[i].setColour(cable.getSideColorValue(f));
					platef[i].render(ccrs);
					ccrs.brightness = brightness;
				case 21: // isPlate, isAllSubnets
					iface[i].render(ccrs);
					plate[i].render(ccrs);
					if (state.isAllSubnets) {
						platef[i].setColour(-1);
						platef[i].render(ccrs);
					}
				default:
					break;
			}
			if (_cond != null && _cond.isInterfacing(f.getOpposite())) {
				wire[i].render(ccrs);
				if (_cond.interfaceMode(f.getOpposite()) != 4)
					caps[i].render(ccrs);
			}
		}

		return true;
	}

	@Override
	public void renderItem(ItemStack item) {


		GlStateManager.pushMatrix();

		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();

		TextureUtils.changeTexture(textureLocation);

		ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		
		base.render(ccrs);
		cable[2].render(ccrs);
		cable[3].render(ccrs);
		if (item.getMetadata() == 3 | item.getMetadata() == 2)
		{
			cage.render(ccrs);
			wire[2].render(ccrs);
			wire[3].render(ccrs);
			caps[2].render(ccrs);
			caps[3].render(ccrs);
		}
		
		ccrs.draw();

		GlStateManager.popMatrix();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {

		return MapWrapper.handlePerspective(this, transformations, cameraTransformType);
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
