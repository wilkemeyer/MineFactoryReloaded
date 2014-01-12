package powercrystals.minefactoryreloaded.modhelpers.pam;

import net.minecraft.block.Block;

public class PlantablePamFlower extends PlantablePamCrop
{

	public PlantablePamFlower(int blockId, int itemId,int cropId) throws NoSuchMethodException,ClassNotFoundException
	{
		this(blockId, itemId,cropId, Block.tilledField.blockID);
	}

	public PlantablePamFlower(int blockId, int itemId,int cropId, int plantableBlockId) throws NoSuchMethodException,ClassNotFoundException
	{
		super(blockId, itemId,cropId, plantableBlockId);
		_setCrop=Pam.pamTEFlowerSetCropId;
		_setStage=Pam.pamTEFlowerSetGrowthStage;
	}
}
