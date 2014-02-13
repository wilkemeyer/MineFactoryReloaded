package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.IUVTransformation;
import codechicken.lib.render.IconTransformation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

public class RedNetCableRenderer implements ISimpleBlockRenderingHandler {
	protected static CCModel base;
	protected static CCModel cage;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] band  = new CCModel[6];
	protected static CCModel[] plate = new CCModel[6];
	protected static CCModel[] platef = new CCModel[6];
	protected static CCModel[] grip  = new CCModel[6];
	protected static CCModel[] wire  = new CCModel[6];

	public static IUVTransformation uvt;
	public static boolean brightBand;

	static {
		try {
			Map<String, CCModel> cableModels = CCModel.parseObjModels(MineFactoryReloadedCore.class.
					getResourceAsStream(MineFactoryReloadedCore.modelFolder + "RedNetCable.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			cage = cableModels.get("cage").backfacedCopy();
			compute(cage);

			cable[5] = cableModels.get("cable").backfacedCopy();
			CCModel.generateSidedModels(cable, 5, p);
			calculateNormals(cable);

			iface[5] = cableModels.get("interface").backfacedCopy();
			CCModel.generateSidedModels(iface, 5, p);
			calculateNormals(iface);

			band[5] = cableModels.get("band").backfacedCopy();
			CCModel.generateSidedModels(band, 5, p);
			calculateNormals(band);

			plate[5] = cableModels.get("plate").backfacedCopy();
			CCModel.generateSidedModels(plate, 5, p);
			calculateNormals(plate);

			platef[5] = cableModels.get("plateface").backfacedCopy();
			CCModel.generateSidedModels(platef, 5, p);
			calculateNormals(platef);

			grip[5] = cableModels.get("grip").backfacedCopy();
			CCModel.generateSidedModels(grip, 5, p);
			calculateNormals(grip);

			wire[5] = cableModels.get("wire").backfacedCopy();
			CCModel.generateSidedModels(wire, 5, p);
			calculateNormals(wire);
		} catch (Throwable _) { _.printStackTrace(); }
	}
	private static void calculateNormals(CCModel[] _m) { for (CCModel m : _m) compute(m); }
	private static void compute(CCModel m) {
		m.computeNormals();
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.smoothNormals();
	}
	public static void updateUVT(Icon icon) {
		uvt = new IconTransformation(icon);
		brightBand = MFRConfig.brightRednetBand.getBoolean(true);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		CCRenderState.useNormals(true);
		Tessellator tess = Tessellator.instance;

		GL11.glTranslatef(-.5f, -.5f, -.5f);
		tess.startDrawingQuads();
		base.render(null, uvt);
		cable[2].render(null, uvt);
		cable[3].render(null, uvt);
		if (metadata == 3 | metadata == 2)
		{
			cage.render(null, uvt);
			wire[2].render(null, uvt);
			wire[3].render(null, uvt);
		}
		tess.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		CCRenderState.useNormals(false);
		TileEntityRedNetCable _cable = (TileEntityRedNetCable)world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
		int bandBrightness = brightBand ? 0xd00070 : brightness;

		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1,1,1);
		tess.setBrightness(brightness);

		base.render(x, y, z, uvt);
		if (meta == 3 | meta == 2)
			cage.render(x, y, z, uvt);

		for (ForgeDirection f : ForgeDirection.VALID_DIRECTIONS) {
			int side = f.ordinal();
			RedNetConnectionType state = _cable.getConnectionState(f);
			boolean container = Block.blocksList[world.getBlockId(x +
					f.offsetX, y + f.offsetY, z + f.offsetZ)] instanceof IRedNetNetworkContainer;
			switch (state.flags & 31) {
			case 11: // isCable, isSingleSubnet
				tess.setColorOpaque_I(_cable.getSideColorValue(f));
				tess.setBrightness(bandBrightness);
				band[side].render(x, y, z, uvt);
				tess.setColorOpaque_F(1,1,1);
				tess.setBrightness(brightness);
			case 19: // isCable, isAllSubnets
				if (!container) {
					iface[side].render(x, y, z, uvt);
					grip[side].render(x, y, z, uvt);
				} else {
					cable[side].render(x, y, z, uvt);
					if (meta == 3 | meta == 2);
						//wire[side].render()
				}
				break;
			case 13: // isPlate, isSingleSubnet
				tess.setColorOpaque_I(_cable.getSideColorValue(f));
				tess.setBrightness(bandBrightness);
				band[side].render(x, y, z, uvt);
				platef[side].render(x, y, z, uvt);
				tess.setColorOpaque_F(1,1,1);
				tess.setBrightness(brightness);
			case 21: // isPlate, isAllSubnets
				iface[side].render(x, y, z, uvt);
				plate[side].render(x, y, z, uvt);
				if (state.isAllSubnets)
					platef[side].render(x, y, z, uvt);
			default:
				break;
			}
		}

		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return MineFactoryReloadedCore.renderIdRedNet;
	}

}
