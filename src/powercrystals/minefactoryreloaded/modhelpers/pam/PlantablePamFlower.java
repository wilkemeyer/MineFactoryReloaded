package powercrystals.minefactoryreloaded.modhelpers.pam;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class PlantablePamFlower extends PlantablePamCrop
{

	public PlantablePamFlower(Block blockId, Item itemId,int cropId)
	{
		super(blockId, itemId,cropId);
		_setCrop=Pam.pamTEFlowerSetCropId;
		_setStage=Pam.pamTEFlowerSetGrowthStage;
	}
}
