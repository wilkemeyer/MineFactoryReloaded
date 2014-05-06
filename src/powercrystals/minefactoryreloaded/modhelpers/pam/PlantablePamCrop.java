package powercrystals.minefactoryreloaded.modhelpers.pam;
import cpw.mods.fml.common.FMLLog;import java.lang.reflect.InvocationTargetException;import java.lang.reflect.Method;import net.minecraft.block.Block;import net.minecraft.init.Blocks;import net.minecraft.item.Item;import net.minecraft.item.ItemStack;import net.minecraft.tileentity.TileEntity;import net.minecraft.world.World;import net.minecraftforge.common.util.ForgeDirection;import net.minecraftforge.common.IPlantable;import powercrystals.minefactoryreloaded.api.IFactoryPlantable;import powercrystals.minefactoryreloaded.api.ReplacementBlock;
public class PlantablePamCrop implements IFactoryPlantable
{
	protected Block _blockId;
	protected Item _itemId;
	protected ReplacementBlock _plantableBlockId;
	protected int _cropId;
	protected Method _setCrop;
	protected Method _setStage;
	public PlantablePamCrop(Block blockId, Item itemId,int cropId)
	{
		_blockId = blockId;
		_itemId = itemId;
		_plantableBlockId = new ReplacementBlock(blockId);
		_cropId=cropId;
		_setCrop=Pam.pamTESetCropId;
		_setStage=Pam.pamTESetGrowthStage;
	}
	@Override
	public Item getSeed()
	{
		return _itemId;
	}	@Override	public boolean canBePlanted(ItemStack stack)	{		// Auto-generated method stub		return true;	}
	@Override
	public ReplacementBlock getPlantedBlock(World world, int x, int y, int z, ItemStack stack)
	{
		return _plantableBlockId;
	}
	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		Block groundId = world.getBlock(x, y - 1, z);
		if(!world.isAirBlock(x, y, z))
		{
			return false;
		}
		return (
				groundId == Blocks.dirt ||
				groundId == Blocks.grass ||
				groundId == Blocks.farmland ||
				_blockId instanceof IPlantable && 
				groundId.canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable)_blockId));
	}
	@Override
	public void prePlant(World world, int x, int y, int z, ItemStack stack)
	{
		Block groundId = world.getBlock(x, y - 1, z);
		if(groundId == Blocks.dirt || groundId == Blocks.grass)
		{
			world.setBlock(x, y - 1, z, Blocks.farmland);
		}
	}

	@Override
	public void postPlant(World world, int x, int y, int z, ItemStack stack)
	{
		TileEntity te=world.getTileEntity(x,y,z);
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
