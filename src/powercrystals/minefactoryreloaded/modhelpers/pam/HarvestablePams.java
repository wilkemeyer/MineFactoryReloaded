package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.List;
import java.util.Map;
import java.util.Random;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;
public class HarvestablePams implements IFactoryHarvestable
{
	private int _sourceId;
	private Method getCrop;
    private Method getGrowthStage;
    private int _cropId;
    private Class[] _argTypes;
    final private Object[] dummyArgs= new Object[]{};
    protected Class<?> tec;
    public HarvestablePams(int sourceId) throws ClassNotFoundException
    {
    _argTypes = new Class[] {};
    _sourceId=sourceId;
    tec=Class.forName("assets.pamharvestcraft.TileEntityPamCrop");
    try
        {
        getCrop=tec.getDeclaredMethod("getCropID",_argTypes);
        }
        catch(NoSuchMethodException ex)
        {
          getCrop=null;
          FMLLog.warning("cannot get crop method, class is %s",Block.blocksList[_sourceId].getClass().toString());
        }
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
	public int getPlantId()
	{
		
        
        return _sourceId;
	}
	
	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}
	
	@Override
	public boolean breakBlock()
	{
    //I'm just basing this on what I could find
		return _cropId>28;
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
    TileEntity te=world.getBlockTileEntity(x,y,z); 
		 try
        {
        if(te!= null &&getGrowthStage!=null && (Integer)(getGrowthStage.invoke(te,dummyArgs)) > 2)
		{
			return true;
		}
        }
        catch (InvocationTargetException ex)
        {
        FMLLog.warning("Error with planting a crop from Pams mods");
        }
        catch (IllegalArgumentException ex)
        {
        FMLLog.warning("Error with planting a crop from Pams mods, this should not happen");
        }
        catch (IllegalAccessException ex)
        {
        FMLLog.warning("Error with planting a crop from Pams mods,this should not happen");
        }
        return false;
	}
	
	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return Block.blocksList[_sourceId].getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
	}
	
	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
		
        TileEntity te=world.getBlockTileEntity(x,y,z);
        try
        {
        if(getGrowthStage!=null &&  (Integer)(getGrowthStage.invoke(te,dummyArgs)) > 2)
		{
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}

        
            if(te!=null&&getCrop!=null)
            {
                _cropId=(Integer)(getCrop.invoke(te,dummyArgs));
               
            }
        }
        catch (InvocationTargetException ex)
        {
        FMLLog.warning("Error with planting a crop from Pams mods");
        }
        catch (IllegalArgumentException ex)
        {
        FMLLog.warning("Error with planting a crop from Pams mods, this should not happen");
        }
        catch (IllegalAccessException ex)
        {
        FMLLog.warning("Error with planting a crop from Pams mods,this should not happen");
        }
	}
	
	@Override
	public void postHarvest(World world, int x, int y, int z)
	{  
       
	}
}