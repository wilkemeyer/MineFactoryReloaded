package powercrystals.minefactoryreloaded.block;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import cofh.api.block.IBlockInfo;
import cofh.api.block.IDismantleable;
import cofh.render.hitbox.ICustomHitBox;
import cofh.render.hitbox.RenderHitbox;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.render.block.RedNetCableRenderer;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.rednet.RedstoneNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class BlockRedNetCable extends BlockContainer
implements IRedNetNetworkContainer, IBlockInfo, IDismantleable
{
	private static float _wireSize   =  4.0F / 16.0F;
	private static float _cageSize   =  6.0F / 16.0F;
	private static float _gripWidth  =  8.0F / 16.0F;
	private static float _plateWidth = 14.0F / 16.0F;
	private static float _plateDepth =  2.0F / 16.0F;
	private static float _bandWidth  =  5.0F / 16.0F;
	private static float _bandOffset =  2.0F / 16.0F;
	private static float _bandDepth  =  1.0F / 16.0F;

	private static float _wireStart      = 0.5F - _wireSize   / 2.0F;
	private static float _wireEnd        = 0.5F + _wireSize   / 2.0F;
	private static float _cageStart      = 0.5F - _cageSize   / 2.0F;
	private static float _cageEnd        = 0.5F + _cageSize   / 2.0F;
	private static float _gripStart      = 0.5F - _gripWidth  / 2.0F;
	private static float _gripEnd        = 0.5F + _gripWidth  / 2.0F;
	private static float _plateStart     = 0.5F - _plateWidth / 2.0F;
	private static float _plateEnd       = 0.5F + _plateWidth / 2.0F;
	private static float _bandWidthStart = 0.5F - _bandWidth  / 2.0F;
	private static float _bandWidthEnd   = 0.5F + _bandWidth  / 2.0F;

	private static float _bandDepthStart = _bandOffset;
	private static float _bandDepthEnd   = _bandOffset + _bandDepth;

	static int[] _subSideMappings = new int[] { 6, 6,
		0, 1, 2, 3, 4, 5,
		0, 1, 2, 3, 4, 5,
		0, 1, 2, 3, 4, 5,
		0, 1, 2, 3, 4, 5,
		0, 1, 2, 3, 4, 5,
		0, 1, 2, 3, 4, 5 };

	public static Cuboid6[] subSelection = new Cuboid6[2 + 6 * 6];

	static {
		int i = 0;
		// ** 0 **  cable hitbox
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _wireStart, _wireEnd, _wireEnd, _wireEnd);
		// ** 1 **  wire cage hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd);

		// ** 6[0] ** grip hitbox
		subSelection[i++] = new Cuboid6(_gripStart, 0, _gripStart, _gripEnd, _plateDepth, _gripEnd);
		subSelection[i++] = new Cuboid6(_gripStart, 1 - _plateDepth, _gripStart, _gripEnd, 1, _gripEnd);
		subSelection[i++] = new Cuboid6(_gripStart, _gripStart, 0, _gripEnd, _gripEnd, _plateDepth);
		subSelection[i++] = new Cuboid6(_gripStart, _gripStart, 1 - _plateDepth, _gripEnd, _gripEnd, 1);
		subSelection[i++] = new Cuboid6(0, _gripStart, _gripStart, _plateDepth, _gripEnd, _gripEnd);
		subSelection[i++] = new Cuboid6(1 - _plateDepth, _gripStart, _gripStart, 1, _gripEnd, _gripEnd);

		// ** 6[1] ** plate hitbox
		subSelection[i++] = new Cuboid6(_plateStart, 0, _plateStart, _plateEnd, _plateDepth, _plateEnd);
		subSelection[i++] = new Cuboid6(_plateStart, 1 - _plateDepth, _plateStart, _plateEnd, 1, _plateEnd);
		subSelection[i++] = new Cuboid6(_plateStart, _plateStart, 0, _plateEnd, _plateEnd, _plateDepth);
		subSelection[i++] = new Cuboid6(_plateStart, _plateStart, 1 - _plateDepth, _plateEnd, _plateEnd, 1);
		subSelection[i++] = new Cuboid6(0, _plateStart, _plateStart, _plateDepth, _plateEnd, _plateEnd);
		subSelection[i++] = new Cuboid6(1 - _plateDepth, _plateStart, _plateStart, 1, _plateEnd, _plateEnd);

		// ** 6[2] ** color band hitbox
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandDepthStart, _bandWidthStart, _bandWidthEnd, _bandDepthEnd, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, 1 - _bandDepthEnd, _bandWidthStart, _bandWidthEnd, 1 - _bandDepthStart, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandWidthStart, _bandDepthStart, _bandWidthEnd, _bandWidthEnd, _bandDepthEnd);
		subSelection[i++] = new Cuboid6(_bandWidthStart, _bandWidthStart, 1 - _bandDepthEnd, _bandWidthEnd, _bandWidthEnd, 1 - _bandDepthStart);
		subSelection[i++] = new Cuboid6(_bandDepthStart, _bandWidthStart, _bandWidthStart, _bandDepthEnd, _bandWidthEnd, _bandWidthEnd);
		subSelection[i++] = new Cuboid6(1 - _bandDepthEnd, _bandWidthStart, _bandWidthStart, 1 - _bandDepthStart, _bandWidthEnd, _bandWidthEnd);

		// ** 6[3] ** cable connection hitbox
		subSelection[i++] = new Cuboid6(_wireStart, _plateDepth, _wireStart, _wireEnd, _wireStart, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireStart, _wireEnd, _wireStart, _wireEnd, 1 - _plateDepth, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _plateDepth, _wireEnd, _wireEnd, _wireStart);
		subSelection[i++] = new Cuboid6(_wireStart, _wireStart, _wireEnd, _wireEnd, _wireEnd, 1 - _plateDepth);
		subSelection[i++] = new Cuboid6(_plateDepth, _wireStart, _wireStart, _wireStart, _wireEnd, _wireEnd);
		subSelection[i++] = new Cuboid6(_wireEnd, _wireStart, _wireStart, 1 - _plateDepth, _wireEnd, _wireEnd);
		
		// ** 6[4] ** wire cage minus band connection hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _bandDepthEnd, _cageStart, _cageEnd, _cageStart, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageEnd, _cageStart, _cageEnd, 1 - _bandDepthEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _bandDepthEnd, _cageEnd, _cageEnd, _cageStart);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd, 1 - _bandDepthEnd);
		subSelection[i++] = new Cuboid6(_bandDepthEnd, _cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageEnd, _cageStart, _cageStart, 1 - _bandDepthEnd, _cageEnd, _cageEnd);
		
		// ** 6[5] ** wire cage connection hitbox
		subSelection[i++] = new Cuboid6(_cageStart, _plateDepth, _cageStart, _cageEnd, _cageStart, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageEnd, _cageStart, _cageEnd, 1 - _plateDepth, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _plateDepth, _cageEnd, _cageEnd, _cageStart);
		subSelection[i++] = new Cuboid6(_cageStart, _cageStart, _cageEnd, _cageEnd, _cageEnd, 1 - _plateDepth);
		subSelection[i++] = new Cuboid6(_plateDepth, _cageStart, _cageStart, _cageStart, _cageEnd, _cageEnd);
		subSelection[i++] = new Cuboid6(_cageEnd, _cageStart, _cageStart, 1 - _plateDepth, _cageEnd, _cageEnd);
	}

	public BlockRedNetCable()
	{
		super(Machine.MATERIAL);

		setBlockName("mfr.cable.redstone");
		setHardness(0.8F);

		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent e = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side, world);
		if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.useBlock == Result.DENY)
		{
			return false;
		}

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
		{
			TileEntityRedNetCable cable = (TileEntityRedNetCable)te;

			MovingObjectPosition part = collisionRayTrace(world, x, y, z,
					RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			if (part == null)
				return false;
			
			int subHit = part.subHit;
			side = _subSideMappings[subHit];

			ItemStack s = player.inventory.getCurrentItem();
			
			if (cable.onPartHit(player, side, subHit))
			{
				;
			}
			else if (subHit >= (2 + 6 * 2) && subHit < (2 + 6 * 3))
			{
				if (MFRUtil.isHoldingUsableTool(player, x, y, z))
				{
					if (!world.isRemote)
					{
						int nextColor;
						if(!player.isSneaking())
						{
							nextColor = cable.getSideColor(ForgeDirection.getOrientation(side)) + 1;
							if(nextColor > 15) nextColor = 0;
						}
						else
						{
							nextColor = cable.getSideColor(ForgeDirection.getOrientation(side)) - 1;
							if(nextColor < 0) nextColor = 15;
						}
						cable.setSideColor(ForgeDirection.getOrientation(side), nextColor);
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
				else if (s != null && s.getItem().equals(rednetMeterItem))
				{
					// TODO: move to client-side when forge fixes player.getEyeHeight on client
					if (!world.isRemote)
					{
						// TODO: localize
						player.addChatMessage(new ChatComponentText("Side is ").appendText(
								ItemRedNetMeter._colorNames[cable.
								                            getSideColor(ForgeDirection.getOrientation(side))]));
					}
				}
				else if (s != null && s.getItem().equals(Items.dye))
				{
					if (!world.isRemote)
					{
						cable.setSideColor(ForgeDirection.getOrientation(side), 15 - s.getItemDamage());
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
			}
			else if (subHit >= 0 && subHit < (2 + 6 * 2))
			{
				l: if (MFRUtil.isHoldingUsableTool(player, x, y, z))
				{
					byte mode = cable.getMode(side);
					mode++;
					if (!world.isRemote)
					{
						if (side == 6)
						{
							if (mode > 1)
								mode = 0;
							cable.setMode(side, mode);
							world.markBlockForUpdate(x, y, z);
							switch (mode)
							{
							case 0:
								player.addChatMessage(new ChatComponentTranslation(
										"chat.info.mfr.rednet.tile.standard"));
								break;
							case 1:
								player.addChatMessage(new ChatComponentTranslation(
										"chat.info.mfr.rednet.tile.cableonly"));
								break;
							default:
							}
							break l;
						}
						if (mode > 3)
						{
							mode = 0;
						}
						cable.setMode(side, mode);
						world.markBlockForUpdate(x, y, z);
						switch (mode)
						{
						case 0:
							player.addChatMessage(new ChatComponentTranslation(
									"chat.info.mfr.rednet.connection.standard"));
							break;
						case 1:
							player.addChatMessage(new ChatComponentTranslation(
									"chat.info.mfr.rednet.connection.forced"));
							break;
						case 2:
							player.addChatMessage(new ChatComponentTranslation(
									"chat.info.mfr.rednet.connection.forcedstrong"));
							break;
						case 3:
							player.addChatMessage(new ChatComponentTranslation(
									"chat.info.mfr.rednet.connection.cableonly"));
							break;
						default:
						}
					}
				}
				else if (s != null && s.getItem().equals(rednetMeterItem))
				{
					// TODO: move to client-side when forge fixes player.getEyeHeight on client
					if (!world.isRemote)
					{
						// TODO: localize
						player.addChatMessage(new ChatComponentText("Side is ").appendText(
								ItemRedNetMeter._colorNames[cable.
								                            getSideColor(ForgeDirection.getOrientation(side))]));
					}
				}
				else if (s != null && s.getItem().equals(Items.dye))
				{
					if (!world.isRemote)
					{
						cable.setSideColor(ForgeDirection.getOrientation(side), 15 - s.getItemDamage());
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList, Entity entity)
	{
		TileEntity cable = world.getTileEntity(x, y, z);
		if (cable instanceof TileEntityRedNetCable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
			((TileEntityRedNetCable)cable).addTraceableCuboids(cuboids, false);
			for (IndexedCuboid6 c : cuboids)
			{
				AxisAlignedBB aabb = c.toAABB();
				if (collisionTest.intersectsWith(aabb))
					collisionBoxList.add(aabb);
			}
		}
		else
		{
			super.addCollisionBoxesToList(world, x, y, z, collisionTest, collisionBoxList, entity);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
	{
		List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
			((TileEntityRedNetCable)te).addTraceableCuboids(cuboids, true);
		return RayTracer.instance().rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(x, y, z), this);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		MovingObjectPosition mop = event.target;
		World world = event.player.worldObj;
		int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
		if (mop.typeOfHit == MovingObjectType.BLOCK && world.getBlock(x, y, z).equals(this)) {
			MovingObjectPosition part = RayTracer.retraceBlock(world, event.player, x, y, z);
			if (part == null)
				return;
			int subHit = part.subHit;
			ICustomHitBox tile = ((ICustomHitBox) world.getTileEntity(x, y, z));
			if (tile.shouldRenderCustomHitBox(subHit, event.player))
			{
				event.setCanceled(true);
				RenderHitbox.drawSelectionBox(event.player, mop, event.partialTicks, tile.getCustomHitBox(subHit, event.player));
			}
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId)
	{
		super.onNeighborBlockChange(world, x, y, z, blockId);
		if (world.isRemote)
		{
			return;
		}
		RedstoneNetwork.log("Cable block at %d, %d, %d got update from ID %d", x, y, z, blockId);

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
		{
			((TileEntityRedNetCable)te).onNeighborBlockChange();
		}
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te instanceof TileEntityRedNetCable)
		{
			((TileEntityRedNetCable)te).onNeighborTileChange(tileX, tileY, tileZ);
		}
    }

	@Override
	public void breakBlock(World world, int X, int Y, int Z, Block id, int meta)
	{
		super.breakBlock(world, X, Y, Z, id, meta);
		for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
		{
			int x = X + d.offsetX, y = Y + d.offsetY, z = Z + d.offsetZ;
			if (world.blockExists(x, y, z) && !world.getBlock(x, y, z).equals(rednetCableBlock))
			{
				world.notifyBlockOfNeighborChange(x, y, z, rednetCableBlock);
				for (ForgeDirection d2 : ForgeDirection.VALID_DIRECTIONS)
				{
					if (d2.getOpposite() == d)
						continue;
					int x2 = x + d2.offsetX, y2 = y + d2.offsetY, z2 = z + d2.offsetZ;
					if (world.blockExists(x2, y2, z2) && !world.getBlock(x2, y2, z2).equals(rednetCableBlock))
						world.notifyBlockOfNeighborChange(x2, y2, z2, rednetCableBlock);
				}
			}
		}
	}

	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock)
	{
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack machine = new ItemStack(getItemDropped(meta, world.rand, 0), 1, damageDropped(meta));
		world.setBlockToAir(x, y, z);
		if (!returnBlock)
			dropBlockAsItem(world, x, y, z, machine);
		return machine;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		MovingObjectPosition part = collisionRayTrace(world, x, y, z,
				RayTracer.getStartVec(player), RayTracer.getEndVec(player));
		if (part == null)
			return false;
		int subHit = part.subHit;
		return subHit < (2 + 6 * 2) | (subHit > (2 + 6 * 3));
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		int power = 0;
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			power = ((TileEntityRedNetCable)te).getWeakPower(ForgeDirection.getOrientation(side).getOpposite());
		}
		return power;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		int power = 0;
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			power = ((TileEntityRedNetCable)te).getStrongPower(ForgeDirection.getOrientation(side).getOpposite());
		}
		return power;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
			return ((TileEntityRedNetCable)te).isSolidOnSide(side.ordinal());
		
		return false;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		switch (meta)
		{
		default:
		case 0:
			return new TileEntityRedNetCable();
		case 2:
			return new TileEntityRedNetEnergy();
		}
	}

	@Override
	public int getRenderType()
	{
		return renderIdRedNet;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		RedNetCableRenderer.updateUVT(blockIcon);
	}

	@Override
	public void updateNetwork(World world, int x, int y, int z, ForgeDirection from)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
		{
			((TileEntityRedNetCable)te).updateNearbyNode(from);
		}
	}

	@Override
	public void updateNetwork(World world, int x, int y, int z, int subnet, ForgeDirection from)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
		{
			((TileEntityRedNetCable)te).updateNearbyNode(subnet, from);
		}
	}

	@Override
	public void getBlockInfo(IBlockAccess world, int x, int y, int z,
			ForgeDirection side, EntityPlayer player, List<String> info, boolean debug)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityRedNetCable)
			((TileEntityRedNetCable)tile).getTileInfo(info, side, player, debug);
	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}
}
