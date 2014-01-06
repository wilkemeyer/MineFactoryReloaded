package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import cpw.mods.fml.common.FMLLog;
class PamFertilizable implements IFactoryFertilizable
{
    private Method getGrowthStage;
    private Method _fertilize;
	private int _blockId;
    protected Class<?> tec;
	private Class[] _argTypes;
    final private Object[] dummyArgs= new Object[]{};
	public PamFertilizable(int blockId) throws ClassNotFoundException
	{
		_blockId = blockId;
        tec=Class.forName("assets.pamharvestcraft.TileEntityPamCrop");
        try
        {
        getGrowthStage=tec.getDeclaredMethod("getGrowthStage",_argTypes);
        }
        catch(NoSuchMethodException ex)
        {
          getGrowthStage=null;
          FMLLog.warning("cannot get stage method");
        }
	}
	
	@Override
	public int getFertilizableBlockId()
	{
		return _blockId;
	}
	
	@Override
	public boolean canFertilizeBlock(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
        int stage=0;
        TileEntity te=world.getBlockTileEntity(x,y,z); 
        if(getGrowthStage!=null&&te!=null)
        {
            try
            {
                stage=(Integer)(getGrowthStage.invoke(te,dummyArgs));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
		return stage < 2 && fertilizerType == FertilizerType.GrowPlant;
	}
	
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
        int stage=0;
        TileEntity te=world.getBlockTileEntity(x,y,z); 
        if(getGrowthStage!=null&&te!=null)
        {
            try
            {
                stage=(Integer)(getGrowthStage.invoke(te,dummyArgs));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
		try
		{
			_fertilize.invoke(Block.blocksList[_blockId], world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return stage >= 2;
	}
}