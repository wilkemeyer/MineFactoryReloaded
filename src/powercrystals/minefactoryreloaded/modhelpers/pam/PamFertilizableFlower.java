package powercrystals.minefactoryreloaded.modhelpers.pam;

import net.minecraft.block.Block;

class PamFertilizableFlower extends PamFertilizable
{
	public PamFertilizableFlower(Block blockID) throws ClassNotFoundException
	{
		super(blockID);
		getGrowthStage=Pam.pamTEFlowerGetGrowthStage;
		_fertilize=Pam.pamBlockFlowerFertilize;
	}
}