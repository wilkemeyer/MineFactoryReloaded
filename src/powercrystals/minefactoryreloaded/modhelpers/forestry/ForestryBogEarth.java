package powercrystals.minefactoryreloaded.modhelpers.forestry;

import cofh.lib.util.helpers.FluidHelper;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSoil;

public class ForestryBogEarth extends PlantableSoil implements IFactoryFertilizable, IFactoryHarvestable
{
	public ForestryBogEarth(Block block)
	{
		super(block);
		_plantedBlock.setMeta(true);
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
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == FertilizerType.GrowPlant && world.getBlockMetadata(x, y, z) == 1;
	}

	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if (!world.isAirBlock(x, y, z))
			if (FluidHelper.lookupFluidForBlock(world.getBlock(x, y, z)) == FluidHelper.WATER_FLUID) {
				if (FluidHelper.lookupFluidForBlock(world.getBlock(x, y + 1, z)) == FluidHelper.WATER_FLUID)
					return false;
				else
					return world.getBlockMetadata(x, y, z) != 0;
			} else
				return false;

		return true;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> settings, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) == 13;
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		return world.setBlockMetadataWithNotify(x, y, z, 13, 3);
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, int x, int y, int z)
	{
		return world.getBlock(x, y, z).getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
	}

	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
	}

	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
	}
}
