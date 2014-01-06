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
	protected Method getCrop;
    protected Method getGrowthStage;
    private int _cropId;
    final private Object[] dummyArgs=new Object[]{};
    public HarvestablePams(int sourceId) throws ClassNotFoundException
    {
    _sourceId=sourceId;
    
        getCrop=Pam.PamTEGetCropId;
        getGrowthStage=Pam.PamTEGetGrowthStage;
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
	
    //I need a better way of getting the cropID for the TileEntity;
	@Override
	public boolean breakBlock()
	{
		return _cropId<=28;
	}
	
	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
    
    TileEntity te=world.getBlockTileEntity(x,y,z); 
		 try
        {
        if(te==null)
        FMLLog.info("ERR:TE is null");
        if(te!= null &&getGrowthStage!=null && (Integer)(getGrowthStage.invoke(te,dummyArgs)) >= 2)
		{
            _cropId=(Integer)(getCrop.invoke(te,dummyArgs));
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
        List<ItemStack> outStack=new List<ItemStack>();
        TileEntity te=world.getBlockTileEntity(x, y, z);
        if(te!=null)
        {
        try
        {
            int cropID=(Integer)(getCrop.invoke(te,new Object[]{}));
            int cropDrops = rand.nextInt(3) + 2;
            int seedDrops = rand.nextInt(2) + 1;
                    if(Pam.PamSeedFromCrop)
                    {
                        outStack.add(new ItemStack(PamHarvestCraft.PamCropItems[cropID], 1, 0));
                        outStack.add(new ItemStack(PamHarvestCraft.PamSeeds[cropID], seedDrops, 0));
                    } else
                    {
                        outStack.add(new ItemStack(PamHarvestCraft.PamCropItems[i], cropDrops, 0));
                    }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        }
		return outStack;
	}
	
	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
		
        TileEntity te=world.getBlockTileEntity(x,y,z);
        try
        {
        if(getGrowthStage!=null &&  (Integer)(getGrowthStage.invoke(te,dummyArgs)) > 2)
		{
			//world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}

        
            if(te!=null&&getCrop!=null)
            {
                _cropId=(Integer)(getCrop.invoke(te,dummyArgs));
            }
            
            
        }
        catch (InvocationTargetException ex)
        {
        FMLLog.warning("Error with harvesting a crop from Pams mods");
        }
        catch (IllegalArgumentException ex)
        {
        FMLLog.warning("Error with harvesting a crop from Pams mods, this should not happen");
        }
        catch (IllegalAccessException ex)
        {
        FMLLog.warning("Error with harvesting a crop from Pams mods,this should not happen");
        }
	}
	
	@Override
	public void postHarvest(World world, int x, int y, int z)
	{  
       
	}
}