package powercrystals.minefactoryreloaded.block;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.renderIdPPipe;
import static powercrystals.minefactoryreloaded.block.BlockRedNetCable._subSideMappings;

import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import cofh.api.block.IBlockInfo;
import cofh.api.block.IDismantleable;
import cofh.render.hitbox.ICustomHitBox;
import cofh.render.hitbox.RenderHitbox;
import cofh.util.position.BlockPosition;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
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

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.block.PlasticPipeRenderer;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityPlasticPipe;

public class BlockPlasticPipe extends BlockContainer
							implements IBlockInfo, IDismantleable
{
	public BlockPlasticPipe()
	{
		super(Machine.MATERIAL);

		setBlockName("mfr.cable.plastic");
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
		if (te instanceof TileEntityPlasticPipe)
		{
			TileEntityPlasticPipe cable = (TileEntityPlasticPipe)te;

			MovingObjectPosition part = collisionRayTrace(world, x, y, z,
					RayTracer.getStartVec(player), RayTracer.getEndVec(player));
			if (part == null)
				return false;
			
			int subHit = part.subHit;
			int oldSide = side;
			side = _subSideMappings[subHit];
			
			ItemStack s = player.getCurrentEquippedItem();
			
			l2: if (cable.onPartHit(player, side, subHit))
			{
				;
			}
			else if (s != null && s.isItemEqual(new ItemStack(Blocks.redstone_torch)))
			{
				int t = cable.getUpgrade();
				if (t != 0) {
					if (t == 1) break l2;
					if (t == 2)
						dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.redstone_block));
				}
				if (!world.isRemote) {
					cable.setUpgrade(1);
					onNeighborBlockChange(world, x, y, z, this);
					player.addChatMessage(new ChatComponentTranslation(
							"chat.info.mfr.fluid.install.torch"));
				}
				return true;
			}
			else if (s != null && s.isItemEqual(new ItemStack(Blocks.redstone_block)))
			{
				int t = cable.getUpgrade();
				if (t != 0) {
					if (t == 2) break l2;
					if (t == 1)
						dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.redstone_torch));
				}
				if (!world.isRemote) {
					cable.setUpgrade(2);
					onNeighborBlockChange(world, x, y, z, this);
					player.addChatMessage(new ChatComponentTranslation(
							"chat.info.mfr.fluid.install.block"));
				}
				return true;
			}
			else if (subHit >= 0 && subHit < (2 + 6 * 2))
			{
				l: if (MFRUtil.isHoldingUsableTool(player, x, y, z))
				{
					byte mode = cable.getMode(side);
					mode++;
					if (mode == 2) ++mode;
					if (!world.isRemote)
					{
						if (side == 6)
						{
							te = BlockPosition.getAdjacentTileEntity(cable, ForgeDirection.getOrientation(oldSide));
							if (te instanceof TileEntityPlasticPipe &&
									!cable.isInterfacing(ForgeDirection.getOrientation(oldSide).getOpposite()) &&
									cable.couldInterface((TileEntityPlasticPipe)te))
								{
									cable.mergeWith((TileEntityPlasticPipe)te);
									((TileEntityPlasticPipe)te).notifyNeighborTileChange();
									cable.notifyNeighborTileChange();
									break l;
								}

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
									"chat.info.mfr.fluid.connection.disabled"));
							break;
						case 1:
							player.addChatMessage(new ChatComponentTranslation(
									"chat.info.mfr.fluid.connection.output"));
							break;
						case 3:
							player.addChatMessage(new ChatComponentTranslation(
									"chat.info.mfr.fluid.connection.extract"));
							break;
						default:
						}
					}
				}
			}
		}
		return false;
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
		return true;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{ // HACK: called before block is destroyed by the player prior to the player getting the drops. destroy block here.
		// hack is needed because the player sets the block to air *before* getting the drops. woo good logic from mojang.
		if (!player.capabilities.isCreativeMode)
		{
			dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlock(x, y, z, Blocks.air, 0, 4);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata,
			int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		ItemStack machine = new ItemStack(getItemDropped(metadata, world.rand, fortune), 1,
				damageDropped(metadata));
			drops.add(machine);
		

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityPlasticPipe)
		{
			switch (((TileEntityPlasticPipe)te).getUpgrade())
			{
			case 1:
				drops.add(new ItemStack(Blocks.redstone_torch));
				break;
			case 2:
				drops.add(new ItemStack(Blocks.redstone_block));
				break;
			}
			
		}
		
		return drops;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList, Entity entity)
	{
		TileEntity cable = world.getTileEntity(x, y, z);
		if (cable instanceof TileEntityPlasticPipe)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
			((TileEntityPlasticPipe)cable).addTraceableCuboids(cuboids, false, false);
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
		if (te instanceof TileEntityPlasticPipe)
			((TileEntityPlasticPipe)te).addTraceableCuboids(cuboids, true, false);
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
	public void getBlockInfo(IBlockAccess world, int x, int y, int z,
			ForgeDirection side, EntityPlayer player, List<String> info, boolean debug)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityPlasticPipe)
			((TileEntityPlasticPipe)tile).getTileInfo(info, side, player, debug);
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
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
	public int getRenderType()
	{
		return renderIdPPipe;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		PlasticPipeRenderer.updateUVT(blockIcon);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
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

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityPlasticPipe)
		{
			((TileEntityPlasticPipe)te).onNeighborBlockChange();
		}
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te instanceof TileEntityPlasticPipe)
		{
			((TileEntityPlasticPipe)te).onNeighborTileChange(tileX, tileY, tileZ);
		}
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityPlasticPipe();
	}
}
