package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;

public class PamFertilizableSapling implements IFactoryFertilizable
{
	private int _blockId;
	
	public PamFertilizableSapling(int blockId)
	{
		_blockId = blockId;
	}
	
	@Override
	public int getFertilizableBlockId()
	{
		return _blockId;
	}
	
	@Override
	public boolean canFertilizeBlock(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		return fertilizerType == FertilizerType.GrowPlant;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
        Object obj= Block.blocksList[world.getBlockId(x, y, z)];
        int meta=world.getBlockMetadata(x,y,z);
        try
        {
            Pam.pamBlockSaplingFertilize.invoke(obj,world,x,y,z,rand,meta);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
		return world.getBlockId(x, y, z) != _blockId;
	}
}
