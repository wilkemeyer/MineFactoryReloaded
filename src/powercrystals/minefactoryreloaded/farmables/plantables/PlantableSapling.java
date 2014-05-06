package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class PlantableSapling extends PlantableStandard
{
	public PlantableSapling(Item seed, Block plant)
	{
		super(seed, plant);
	}
	
	public PlantableSapling(Block plant)
	{
		super(plant, plant);
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		Block ground = world.getBlock(x, y - 1, z);
		if(!world.isAirBlock(x, y, z))
		{
			return false;
		}
		return _block.canBlockStay(world, x, y, z) && (
					_block.canPlaceBlockAt(world, x, y, z) || (
						_block instanceof IPlantable &&
						ground.canSustainPlant(world, x, y, z,
								ForgeDirection.UP, (IPlantable)_block)));
	}

}
