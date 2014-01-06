package powercrystals.minefactoryreloaded.modhelpers.pam;

class PamFertilizableFlower extends PamFertilizable
{
	public PamFertilizableFlower(int blockID) throws ClassNotFoundException
    {
        super(blockID);
        getGrowthStage=Pam.PamTEFlowerGetGrowthStage;
        _fertilize=Pam.PamBlockFlowerFertilize;
    }
}