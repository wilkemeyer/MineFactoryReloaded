package powercrystals.minefactoryreloaded.farmables.plantables;

import cofh.lib.util.helpers.FluidHelper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PlantableSoil extends PlantableStandard
{
	public PlantableSoil(Block block)
	{
		super(Item.getItemFromBlock(block), block);
	}

	public PlantableSoil(Item block, Block plantedBlock)
	{
		super(block, plantedBlock);
	}

	public PlantableSoil(Item block, Block plantedBlock, int validMeta)
	{
		super(block, plantedBlock, validMeta);
	}

	@Override
	public boolean canBePlanted(ItemStack stack, boolean forFermenting)
	{
		return !forFermenting && super.canBePlanted(stack, forFermenting);
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
			} else {
				Block block = world.getBlock(x, y, z);
				return !block.getMaterial().isLiquid() && block.isReplaceable(world, x, y, z);
			}

		return true;
	}
}
