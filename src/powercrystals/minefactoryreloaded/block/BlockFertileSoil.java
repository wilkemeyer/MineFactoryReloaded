package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IIcon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nullable;

public class BlockFertileSoil extends Block implements IGrowable
{
	public static final AxisAlignedBB SOIL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

	public BlockFertileSoil()
	{
		super(Material.GROUND);
		setHardness(0.6F);
		setLightOpacity(255);
		setTickRandomly(false);
		setHarvestLevel("hoe", 0);
		setHarvestLevel("shovel", 0);
		setUnlocalizedName("mfr.farmland");
		setSoundType(SoundType.GROUND);
		setCreativeTab(MFRCreativeTab.tab);
		useNeighborBrightness = true; // THIS IS SET IN THE DUMBEST DAMN WAY ON FARMLAND
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		
		if (source.getBlockState(pos.up()).getMaterial().isSolid()) {
			return FULL_BLOCK_AABB;
		} else {
			return SOIL_AABB;
		}
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		if (direction != EnumFacing.UP)
			return false;
		EnumPlantType plantType = plantable.getPlantType(world, pos.up());

		switch (plantType)
		{
		case Cave:
		case Crop:
		case Beach:
		case Desert:
		case Nether:
		case Plains:
			return true;
		default:
			return false;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			@Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!canGrow(world, pos, state, world.isRemote))
			return false;

		if (heldItem != null)
		{
			if (MFRRegistry.getFertilizers().get(heldItem.getItem()) != null)
			{
				if (canUseBonemeal(world, world.rand, pos, state))
				{
					grow(world, world.rand, pos, state);
					world.playEvent(null, 2005, pos, 0);
				}
				if (!player.capabilities.isCreativeMode) {
					EntityEquipmentSlot slot = hand == EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
					player.setItemStackToSlot(slot, UtilInventory.consumeItem(heldItem, player));
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFertile(World world, BlockPos pos)
	{
		return world.getBlockMetadata(x, y, z) > 0;
	}

	protected void alterMetadata(World world, BlockPos pos, int d)
	{
		world.setBlockMetadataWithNotify(x, y, z, Math.min(15, Math.max(0, world.getBlockMetadata(x, y, z) + d)), 6);
	}

	@Override
	public void onPlantGrow(World world, BlockPos pos, int sourceX, int sourceY, int sourceZ)
	{
		if (!isFertile(world, x, y, z))
			world.setBlock(x, y, z, Blocks.sand);
		else
			alterMetadata(world, x, y, z, -1);
	}

	@SuppressWarnings("unused")
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, Block block)
	{
		super.onNeighborBlockChange(world, x, y, z, block);
		if (true) return; // TODO: if neighborChange becomes sided, remove
		Block above = world.getBlock(x, y + 1, z);
		if (block != above)
			return;
		Material material = above.getMaterial();
		boolean flag = material == Material.gourd || isFertile(world, x, y, z);

		if (!flag && material.isSolid())
		{
			world.setBlock(x, y, z, Blocks.dirt);
		}
		else
		{
			alterMetadata(world, x, y, z, -1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, Random rand) { ; }

	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity ent, float distance)
	{
		if (distance > 10)
		{
			if (!world.isRemote && world.rand.nextFloat() * 20 < distance)
			{
				if (!(ent instanceof EntityPlayer) && !world.getGameRules().getGameRuleBooleanValue("mobGriefing"))
				{
					return;
				}

				world.setBlock(x, y, z, Blocks.farmland);
				if (world.rand.nextFloat() >= 0.25)
					dropBlockAsItem(world, x, y, z, new ItemStack(MFRThings.fertilizerItem));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(EnumFacing side, int meta)
	{
		return side == 1 ? blockIcon : Blocks.dirt.getIcon(side, 2);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return side != EnumFacing.UP;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, BlockPos pos, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		ret.add(new ItemStack(Blocks.dirt));
		if (world.rand.nextFloat() >= 0.5)
			ret.add(new ItemStack(MFRThings.fertilizerItem));

		return ret;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean func_149851_a(World world, BlockPos pos, boolean worthlessBoolean)
	{ // canFertilize
		return world.getBlockMetadata(x, y, z) < 15;
	}

	@Override
	public boolean func_149852_a(World world, Random rand, BlockPos pos)
	{ // shouldFertilize
		return world.getBlockMetadata(x, y, z) < 15 && rand.nextInt(3) == 0;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos)
	{ // fertilize
		alterMetadata(world, x, y, z, 1 + (int)(rand.nextFloat() * 1.5f));
	}
}
