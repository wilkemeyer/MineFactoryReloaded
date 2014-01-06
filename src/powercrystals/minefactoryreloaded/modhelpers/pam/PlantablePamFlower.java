package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import powercrystals.minefactoryreloaded.api.IFactoryPlantable;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ForgeDirection;
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
        _setCrop=Pam.PamTEFlowerSetCropId;
        _setStage=Pam.PamTEFlowerSetGrowthStage;
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
		int groundId = world.getBlockId(x, y - 1, z);
		if(!world.isAirBlock(x, y, z))
		{
			return false;
		}
		return (
				groundId == Block.dirt.blockID ||
				groundId == Block.grass.blockID ||
				groundId == Block.tilledField.blockID ||
				(Block.blocksList[_blockId] instanceof IPlantable && Block.blocksList[groundId] != null &&
				Block.blocksList[groundId].canSustainPlant(world, x, y, z, ForgeDirection.UP, ((IPlantable)Block.blocksList[_blockId]))));
	}
	
	@Override
	public void prePlant(World world, int x, int y, int z, ItemStack stack)
	{
    int groundId = world.getBlockId(x, y - 1, z);
		if(groundId == Block.dirt.blockID || groundId == Block.grass.blockID)
		{
			world.setBlock(x, y - 1, z, Block.tilledField.blockID);
		}
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
                 //world.setBlockMetadataWithNotify(x,y,z,_cropId,2);
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
