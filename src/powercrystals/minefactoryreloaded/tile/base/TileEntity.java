package powercrystals.minefactoryreloaded.tile.base;

public class TileEntity extends net.minecraft.tileentity.TileEntity
{
	public void markChunkDirty()
	{
		worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
	}

	public void callNeighborTileChange()
	{
		if (getBlockType() != null)
		{
			worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		}
	}
}
