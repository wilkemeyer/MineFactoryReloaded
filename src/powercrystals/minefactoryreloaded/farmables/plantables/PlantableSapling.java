package powercrystals.minefactoryreloaded.farmables.plantables;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class PlantableSapling extends PlantableStandard
{
	public PlantableSapling(int sourceId, int plantedBlockId)
	{
		super(sourceId, plantedBlockId);
	}

	public PlantableSapling(int sourceId, int plantedBlockId, int validMeta)
	{
		super(sourceId, plantedBlockId, validMeta);
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		int groundId = world.getBlockId(x, y - 1, z);
		if(!world.isAirBlock(x, y, z) || (_validMeta != WILDCARD && stack.getItemDamage() != _validMeta))
		{
			return false;
		}
		return Block.blocksList[_plantedBlockId].canBlockStay(world, x, y, z) && (
					Block.blocksList[_plantedBlockId].canPlaceBlockAt(world, x, y, z) || (
						Block.blocksList[_plantedBlockId] instanceof IPlantable &&
						Block.blocksList[groundId] != null &&
						Block.blocksList[groundId].canSustainPlant(world, x, y, z,
								ForgeDirection.UP, ((IPlantable)Block.blocksList[_plantedBlockId]))));
	}

}
