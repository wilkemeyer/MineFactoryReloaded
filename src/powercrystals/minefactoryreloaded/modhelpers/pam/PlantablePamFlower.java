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
public class PlantablePamFlower extends PlantablePamCrop
{

	public PlantablePamFlower(int blockId, int itemId,int cropId) throws NoSuchMethodException,ClassNotFoundException
	{
		this(blockId, itemId,cropId, Block.tilledField.blockID);
	}
	
	public PlantablePamFlower(int blockId, int itemId,int cropId, int plantableBlockId) throws NoSuchMethodException,ClassNotFoundException
	{
        super(blockId, itemId,cropId, plantableBlockId);
        _setCrop=Pam.PamTEFlowerSetCropId;
        _setStage=Pam.PamTEFlowerSetGrowthStage;
	}
}
