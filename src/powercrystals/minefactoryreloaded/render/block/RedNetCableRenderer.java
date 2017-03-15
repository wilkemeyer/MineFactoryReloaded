package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.model.blockbakery.ISimpleBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.lib.util.helpers.RenderHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.block.transport.BlockRedNetCable;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RedNetCableRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rednet_cable", "normal");

	public static final RedNetCableRenderer INSTANCE = new RedNetCableRenderer();
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

	public static TextureAtlasSprite sprite;

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

	private RedNetCableRenderer() {
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
	public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {

		if (face != null) {
			return Collections.emptyList();
		}

		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);
		ccrs.sprite = sprite;

		renderCable(state, ccrs);

		buffer.finishDrawing();
		return buffer.bake();

	}

	private void renderCable(IExtendedBlockState state, CCRenderState ccrs) {

		int brightness = ccrs.brightness;
		int bandBrightness = brightBand ? 0xd00070 : brightness;
		IconTransformation texture = new IconTransformation(sprite);

		base.render(ccrs, texture);

		BlockRedNetCable.Variant variant = state.getValue(BlockRedNetCable.VARIANT);
		if (variant == BlockRedNetCable.Variant.ENERGY || variant == BlockRedNetCable.Variant.ENERGY_GLASS) {
			cage.render(ccrs, texture);
		}

		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i --> 0; ) {
			boolean showBand = state.getValue(BlockRedNetCable.BAND[i]);
			boolean showPlateFace = state.getValue(BlockRedNetCable.PLATE_FACE[i]);

			if (showBand || showPlateFace) {
				int color = state.getValue(BlockRedNetCable.COLOR[i]) == 16 ? -1 :
						(MFRDyeColor.byMetadata(state.getValue(BlockRedNetCable.COLOR[i]) & 15).getColor() << 8) | 0xFF;

				ccrs.brightness = bandBrightness;
				if (showBand) {
					band[i].setColour(color);
					band[i].render(ccrs, texture);
				}
				if (showPlateFace) {
					platef[i].setColour(color);
					platef[i].render(ccrs, texture);
				}
				ccrs.brightness = brightness;
			}

			if (state.getValue(BlockRedNetCable.IFACE[i]))
				iface[i].render(ccrs, texture);

			if (state.getValue(BlockRedNetCable.GRIP[i]))
				grip[i].render(ccrs, texture);

			if (state.getValue(BlockRedNetCable.CABLE_CONNECTION[i]))
				cable[i].render(ccrs, texture);

			if (state.getValue(BlockRedNetCable.PLATE[i]))
				plate[i].render(ccrs, texture);

			if (state.getValue(BlockRedNetCable.WIRE[i]))
				wire[i].render(ccrs, texture);

			if (state.getValue(BlockRedNetCable.CAPS[i]))
				caps[i].render(ccrs, texture);
		}
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState blockState, TileEntity tileEntity) {

		TileEntityRedNetCable cable = (TileEntityRedNetCable) tileEntity;
		TileEntityRedNetEnergy energyCable = null;

		if (cable instanceof TileEntityRedNetEnergy) {
			energyCable = (TileEntityRedNetEnergy)cable;
		}

		EnumFacing[] dirs = EnumFacing.VALUES;

		for (int i = dirs.length; i --> 0; ) {
			EnumFacing f = dirs[i];
			blockState = blockState.withProperty(BlockRedNetCable.COLOR[i], cable.getSideColor(f));
			blockState = blockState.withProperty(BlockRedNetCable.BAND[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.IFACE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.GRIP[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.CABLE_CONNECTION[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.PLATE_FACE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.PLATE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.WIRE[i], false);
			blockState = blockState.withProperty(BlockRedNetCable.CAPS[i], false);

			RedNetConnectionType state = cable.getCachedConnectionState(f);
			switch (state.flags & 31) {
			case 11: // isCable, isSingleSubnet
				blockState = blockState.withProperty(BlockRedNetCable.BAND[i], true);
			case 19: // isCable, isAllSubnets
				if (state.isSingleSubnet) {
					blockState = blockState.withProperty(BlockRedNetCable.IFACE[i], true);
					blockState = blockState.withProperty(BlockRedNetCable.GRIP[i], true);
				} else
					blockState = blockState.withProperty(BlockRedNetCable.CABLE_CONNECTION[i], true);
				break;
			case 13: // isPlate, isSingleSubnet
				blockState = blockState.withProperty(BlockRedNetCable.BAND[i], true);
				blockState = blockState.withProperty(BlockRedNetCable.PLATE_FACE[i], true);
			case 21: // isPlate, isAllSubnets
				blockState = blockState.withProperty(BlockRedNetCable.IFACE[i], true);
				blockState = blockState.withProperty(BlockRedNetCable.PLATE[i], true);
				if (state.isAllSubnets) {
					blockState = blockState.withProperty(BlockRedNetCable.PLATE_FACE[i], true);
					blockState = blockState.withProperty(BlockRedNetCable.COLOR[i], -1);
				}
			default:
				break;
			}
			if (energyCable != null && energyCable.isInterfacing(f.getOpposite())) {
				blockState = blockState.withProperty(BlockRedNetCable.WIRE[i], true);
				if (energyCable.interfaceMode(f.getOpposite()) != 4)
					blockState = blockState.withProperty(BlockRedNetCable.CAPS[i], true);
			}
		}

		return blockState;
	}

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		base.render(ccrs);
		cable[2].render(ccrs);
		cable[3].render(ccrs);
		if (stack.getMetadata() == 3 | stack.getMetadata() == 2)
		{
			cage.render(ccrs);
			wire[2].render(ccrs);
			wire[3].render(ccrs);
			caps[2].render(ccrs);
			caps[3].render(ccrs);
		}

		buffer.finishDrawing();
		return buffer.bake();
	}
}
