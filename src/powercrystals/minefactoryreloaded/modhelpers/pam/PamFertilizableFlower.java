package powercrystals.minefactoryreloaded.modhelpers.pam;

class PamFertilizableFlower extends PamFertilizable
{
	public PamFertilizableFlower(int blockID) throws ClassNotFoundException
    {
        super(blockID);
        tec=Class.forName("TileEntityPamFlowerCrop");
    }
}