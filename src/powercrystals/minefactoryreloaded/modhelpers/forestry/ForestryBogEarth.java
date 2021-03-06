package powercrystals.minefactoryreloaded.modhelpers.forestry;

import java.util.List;
import java.util.Map;
import java.util.Random;

import forestry.core.blocks.BlockBogEarth;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSoil;

public class ForestryBogEarth extends PlantableSoil implements IFactoryFertilizable, IFactoryHarvestable, IFactoryFruit
{
	private ReplacementBlock repl;
	private Item dirt;

	public ForestryBogEarth(Block block)
	{
		super(block);
		_plantedBlock.setMeta(true);
		repl = new ReplacementBlock(Blocks.DIRT);
		dirt = Item.getItemFromBlock(Blocks.DIRT);
	}

	@Override
	public boolean breakBlock()
	{
		return true;
	}

	@Override
	public Block getPlant()
	{
		return _block;
	}

	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType)
	{
		return fertilizerType == FertilizerType.GrowPlant && 
				BlockBogEarth.SoilType.fromMaturity(world.getBlockState(pos).getValue(BlockBogEarth.MATURITY)) != BlockBogEarth.SoilType.PEAT;
	}

	@Override
	public boolean canBePicked(World world, BlockPos pos)
	{
		return BlockBogEarth.SoilType.fromMaturity(world.getBlockState(pos).getValue(BlockBogEarth.MATURITY)) == BlockBogEarth.SoilType.PEAT;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> settings, BlockPos pos)
	{
		return BlockBogEarth.SoilType.fromMaturity(world.getBlockState(pos).getValue(BlockBogEarth.MATURITY)) == BlockBogEarth.SoilType.PEAT;
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType)
	{
		return world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockBogEarth.MATURITY, 3), 3);
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getDrops(world, pos, state, 0);
	}

	@Override
	public ReplacementBlock getReplacementBlock(World world, BlockPos pos)
	{
		return repl;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		List<ItemStack> list = state.getBlock().getDrops(world, pos, state, 0);
		for (ItemStack a : list)
			if (a.getItem() == dirt) {
				list.remove(a);
				break;
			}
		return list;
	}

	@Override
	public void preHarvest(World world, BlockPos pos)
	{
	}

	@Override
	public void postHarvest(World world, BlockPos pos)
	{
	}
}
