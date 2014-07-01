package powercrystals.minefactoryreloaded.farmables.plantables;

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
		return world.isAirBlock(x, y, z);
	}
}
