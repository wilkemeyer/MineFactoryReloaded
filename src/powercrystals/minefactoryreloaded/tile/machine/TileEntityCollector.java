package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.core.IEntityCollidable;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityCollector extends TileEntityFactoryInventory implements IEntityCollidable
{
	protected boolean stuffedChests;
	
	public TileEntityCollector()
	{
		super(Machine.ItemCollector);
		setManageSolids(true);
		stuffedChests = false;
	}

	@Override
	public void onEntityCollided(Entity entity)
	{
		if (entity instanceof EntityItem && !entity.isDead)
			addToChests((EntityItem)entity);
	}

	protected void addToChests(EntityItem i)
	{
		if (i.isDead)
			return;
		
		ItemStack s = i.getEntityItem();
		s = UtilInventory.dropStack(this, s, MFRUtil.directionsWithoutConveyors(worldObj, xCoord, yCoord, zCoord), ForgeDirection.UNKNOWN);
		if(s == null)
		{
			stuffedChests = false;
			i.setDead();
			return;
		}
		stuffedChests = true;
		i.setEntityItemStack(s);
	}

	@Override
	public int getComparatorOutput(int side)
	{
		return stuffedChests ? 15 : 0;
	}
	
	@Override
	public boolean canUpdate()
	{
		return false;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
}
