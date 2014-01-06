package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import powercrystals.minefactoryreloaded.api.IFactoryPlantable;
import cpw.mods.fml.common.FMLLog;
public class PlantablePamFlower implements IFactoryPlantable
{
	private int _blockId;
	private int _itemId;
	private int _plantableBlockId;
    private int _cropId;
	private Method _setCrop;
    private Method _setStage;
	public PlantablePamFlower(int blockId, int itemId,int cropId) throws NoSuchMethodException,ClassNotFoundException
	{
		this(blockId, itemId,cropId, Block.tilledField.blockID);
	}
	
	public PlantablePamFlower(int blockId, int itemId,int cropId, int plantableBlockId) throws NoSuchMethodException,ClassNotFoundException
	{
		_blockId = blockId;
		_itemId = itemId;
		_plantableBlockId = plantableBlockId;
        _cropId=cropId;
        Class[] _argTypes = new Class[] { int.class };
        Class<?> pamTE=Class.forName("assets.pamharvestcraft.TileEntityPamFlowerCrop");
        _setCrop=pamTE.getDeclaredMethod("setCropID",_argTypes);
        _setStage=pamTE.getDeclaredMethod("setGrowthStage",_argTypes);
        FMLLog.info("seed id is %s",_itemId);
	}
	
	@Override
	public int getSeedId()
	{
		return _itemId;
	}
	
	@Override
	public int getPlantedBlockId(World world, int x, int y, int z, ItemStack stack)
	{
		return _blockId;
	}
	
	@Override
	public int getPlantedBlockMetadata(World world, int x, int y, int z, ItemStack stack)
	{
		return 0;
	}
	
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		return world.getBlockId(x, y - 1, z) == _plantableBlockId && world.getBlockId(x, y, z) == 0;
	}
	
	@Override
	public void prePlant(World world, int x, int y, int z, ItemStack stack)
	{
	}
	
	@Override
	public void postPlant(World world, int x, int y, int z, ItemStack stack)
	{
       TileEntity te=world.getBlockTileEntity(x,y,z);
        
        try
        {
            if(te!=null)
            {
                _setCrop.invoke(te,_cropId);
                _setStage.invoke(te,0);
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
}
