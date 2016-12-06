package powercrystals.minefactoryreloaded.block;

import cofh.api.block.IDismantleable;
import cofh.core.render.hitbox.ICustomHitBox;
import cofh.core.render.hitbox.RenderHitbox;
import cofh.lib.util.position.IRotateableTile;
import cofh.repack.codechicken.lib.raytracer.IndexedCuboid6;
import cofh.repack.codechicken.lib.raytracer.RayTracer;
import cofh.repack.codechicken.lib.vec.BlockCoord;
import cofh.repack.codechicken.lib.vec.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

import net.minecraftforge.fml.common.eventhandler.Event.Result;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.ITraceable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;

import javax.annotation.Nullable;

public class BlockFactory extends Block implements IRedNetConnection, IDismantleable
{
	protected boolean providesPower;

	protected BlockFactory(float hardness)
	{
		super(Machine.MATERIAL);
		setHardness(hardness);
		setSoundType(SoundType.METAL);
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
	}

	protected BlockFactory(Material material)
	{
		super(material);
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("pickaxe", 0);
	}

	protected static final TileEntity getTile(IBlockAccess world, BlockPos pos)
	{
		return MFRUtil.getTile(world, pos);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{ // HACK: called before block is destroyed by the player prior to the player getting the drops. destroy block here.
		// hack is needed because the player sets the block to air *before* getting the drops. woo good logic from mojang.
		if (!player.capabilities.isCreativeMode)
		{
			//TODO verify that this logic doesn't conflict with vanilla one now
			harvesters.set(player);
			dropBlockAsItem(world, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getActiveItemStack()));
			harvesters.set(null);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 7);
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
	{
		if (world.isRemote)
		{
			return false;
		}
		TileEntity te = getTile(world, pos);
		if (te instanceof IRotateableTile)
		{
			IRotateableTile tile = ((IRotateableTile)te);
			if (tile.canRotate(axis))
			{
				tile.rotate(axis);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack)
	{
		TileEntity te = getTile(world, pos);

		if (te instanceof TileEntityBase && stack.getTagCompound() != null)
		{
			te.readFromNBT(stack.getTagCompound());
		}
	}

	@Override
	public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent.RightClickBlock e = new PlayerInteractEvent.RightClickBlock(player, hand, heldItem, pos, side, new Vec3d(xOffset, yOffset, zOffset));
		if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.getUseBlock() == Result.DENY)
			return false;

		activationOffsets(xOffset, yOffset, zOffset);
		return activated(world, pos, player, side);
	}

	protected void activationOffsets(float xOffset, float yOffset, float zOffset) {}

	protected boolean activated(World world, BlockPos pos, EntityPlayer player, EnumFacing side)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
		{
			return false;
		}
		ItemStack ci = player.inventory.getCurrentItem();
		if (ci != null && te instanceof ITankContainerBucketable)
		{
			boolean isFluidContainer = ci.getItem() instanceof IFluidContainerItem;
			if ((isFluidContainer || FluidContainerRegistry.isEmptyContainer(ci)) &&
					((ITankContainerBucketable)te).allowBucketDrain(ci))
			{
				if (MFRLiquidMover.manuallyDrainTank((ITankContainerBucketable)te, player))
				{
					return true;
				}
			}
			if ((isFluidContainer || FluidContainerRegistry.isFilledContainer(ci)) &&
					((ITankContainerBucketable)te).allowBucketFill(ci))
			{
				if (MFRLiquidMover.manuallyFillTank((ITankContainerBucketable)te, player))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos)
	{
		return true;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos,
			boolean returnBlock)
	{
		ArrayList<ItemStack> list = getDrops(world, pos, world.getBlockState(pos), 0);

		world.setBlockToAir(pos);
		if (!returnBlock)
            for (ItemStack item : list) {
				float f = 0.3F;
				double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem itemEntity = new EntityItem(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, item);
				itemEntity.setPickupDelay(10);
				world.spawnEntityInWorld(itemEntity);
			}
		return list;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		ItemStack machine = new ItemStack(getItemDropped(state, rand, fortune), 1,
				damageDropped(state));

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityBase)
		{
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBase)te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);
		}

		drops.add(machine);
		return drops;
	}

	public void getBlockInfo(IBlockAccess world, BlockPos pos, EnumFacing side,
			EntityPlayer player, List<ITextComponent> info, boolean debug)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityBase)
			((TileEntityBase)tile).getTileInfo(info, side, player, debug);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		onNeighborChange(world, pos, pos);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(world, pos, neighbor);
		if (world.isRemote)
		{
			return;
		}

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityBase)
		{
			if (blockId != this)
				((TileEntityBase)te).onNeighborBlockChange();
			else
				((TileEntityBase)te).onMatchedNeighborBlockChange();
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
		TileEntity te = world instanceof World ? getTile((World)world, x, y, z) : world.getTileEntity(x, y, z);

		if (te instanceof TileEntityBase)
		{
			((TileEntityBase)te).onNeighborTileChange(tileX, tileY, tileZ);
		}
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof IEntityCollidable)
		{
			float shrinkAmount = 0.125F;
			return AxisAlignedBB.getBoundingBox(x + shrinkAmount, y + shrinkAmount, z + shrinkAmount,
					x + 1 - shrinkAmount, y + 1 - shrinkAmount, z + 1 - shrinkAmount);
		}
		else
		{
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (world.isRemote)
			return;

		TileEntity te = getTile(world, x, y, z);
		if (te instanceof IEntityCollidable)
			((IEntityCollidable)te).onEntityCollided(entity);

		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList,
			Entity entity)
	{
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof ITraceable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
			((ITraceable)te).addTraceableCuboids(cuboids, false, false);
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
		if (world.isRemote) {
			harvesters.set(MineFactoryReloadedCore.proxy.getPlayer());
		}
		MovingObjectPosition r = collisionRayTrace((IBlockAccess)world, x, y, z, start, end);
		if (world.isRemote) {
			harvesters.set(null);
		}
		return r;
	}

	public MovingObjectPosition collisionRayTrace(IBlockAccess world, int x, int y, int z, Vec3 start, Vec3 end)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof ITraceable)
		{
			List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
			((ITraceable)te).addTraceableCuboids(cuboids, true, MFRUtil.isHoldingUsableTool(harvesters.get(), x, y, z));
			return RayTracer.instance().rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids,
				new BlockCoord(x, y, z), this);
		}
		else if (world instanceof World)
		{
			return super.collisionRayTrace((World)world, x, y, z, start, end);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		EntityPlayer player = event.player;
		World world = player.worldObj;
		MovingObjectPosition omop = event.target;
		harvesters.set(player);
		MovingObjectPosition mop = omop;//RayTracer.reTrace(world, player);
		harvesters.set(null);
		if (mop == null)
			return;
		if (mop.typeOfHit != MovingObjectType.BLOCK || omop.typeOfHit != MovingObjectType.BLOCK)
			return;
		int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
		TileEntity te = getTile(world, x, y, z);
		if (te instanceof ITraceable) {
			int subHit = mop.subHit;
			if (te instanceof ICustomHitBox)
			{
				ICustomHitBox tile = ((ICustomHitBox)te);
				if (tile.shouldRenderCustomHitBox(subHit, player))
				{
					event.setCanceled(true);
					RenderHitbox.drawSelectionBox(player, mop, event.partialTicks, tile.getCustomHitBox(subHit, player));
					return;
				}
			}
			event.context.drawSelectionBox(player, mop, 0, event.partialTicks);
			event.setCanceled(true);
		}
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, EnumFacing side)
	{
		return true;
	}

	@Override
	public boolean isNormalCube()
	{
		return !providesPower;
	}

	@Override
	public boolean canProvidePower()
	{
		return providesPower;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, EnumFacing side)
	{
		if (providesPower)
			return RedNetConnectionType.DecorativeSingle;
		else
			return RedNetConnectionType.ForcedDecorativeSingle;
	}
}
