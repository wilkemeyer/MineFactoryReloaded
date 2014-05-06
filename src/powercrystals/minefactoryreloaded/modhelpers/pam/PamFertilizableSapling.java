package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class PamFertilizableSapling implements IFactoryFertilizable
{
	private Block _blockId;

	public PamFertilizableSapling(Block blockId)
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
		return fertilizerType == FertilizerType.GrowPlant;
	}

	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		Object obj= world.getBlock(x, y, z);
		int meta=world.getBlockMetadata(x,y,z);
		try
		{
			Pam.pamBlockSaplingFertilize.invoke(obj,world,x,y,z,rand,meta);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return world.getBlock(x, y, z) != _blockId;
	}
}
