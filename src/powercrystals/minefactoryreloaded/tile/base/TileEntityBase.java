package powercrystals.minefactoryreloaded.tile.base;

public class TileEntityBase extends net.minecraft.tileentity.TileEntity
{
	@Override
	public void invalidate()
	{
		super.invalidate();
		this.onChunkUnload();
	}
	
	public void onNeighborTileChange(int x, int y, int z) {}

	public void onNeighborBlockChange() {}

	public void markChunkDirty()
	{
		worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
	}

	public void notifyNeighborTileChange()
	{
		if (getBlockType() != null)
		{
			worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		}
	}

	private static final long HASH_A = 0x1387D;
	private static final long HASH_C = 0x3A8F05C5;

	@Override
	public int hashCode() {
		final int xTransform = (int)((HASH_A * (xCoord ^ 0x1AFF2BAD) + HASH_C) & 0xFFFFFFFF);
		final int zTransform = (int)((HASH_A * (zCoord ^ 0x25C8B353) + HASH_C) & 0xFFFFFFFF);
		final int yTransform = (int)((HASH_A * (yCoord ^ 0x39531FCD) + HASH_C) & 0xFFFFFFFF);
		return xTransform ^ zTransform ^ yTransform;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TileEntityBase)
		{
			TileEntityBase te = (TileEntityBase)obj;
			return (te.xCoord == xCoord) & te.yCoord == yCoord & te.zCoord == zCoord &
					worldObj == te.worldObj && te.isInvalid() == isInvalid();
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(x="+xCoord+",y="+yCoord+",z="+zCoord+")@"+System.identityHashCode(this);
	}
}
