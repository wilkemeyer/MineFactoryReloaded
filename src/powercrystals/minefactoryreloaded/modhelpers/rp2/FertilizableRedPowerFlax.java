package powercrystals.minefactoryreloaded.modhelpers.rp2;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class FertilizableRedPowerFlax implements IFactoryFertilizable
{
	private Block _blockId;
	
	public FertilizableRedPowerFlax(Block blockId)
	{
		_blockId = blockId;
	}
	
	@Override
	public Block getPlant()
	{
		return _blockId;
	}
	
	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		Block id = world.getBlock(x, y + 1, z);
		return id.isAir(world, x, y + 1, z);
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		if(world.getBlockMetadata(x, y, z) < 4)
		{
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}
		world.setBlock(x, y + 1, z, _blockId, 5, 2);
		return true;
	}
}