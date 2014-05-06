package powercrystals.minefactoryreloaded.tile.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cofh.util.position.BlockPosition;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityBlockBreaker extends TileEntityFactoryPowered
{
	public TileEntityBlockBreaker()
	{
		super(Machine.BlockBreaker);
		setManageSolids(true);
		setCanRotate(true);
	}
	
	@Override
	public boolean activateMachine()
	{		
		BlockPosition bp = BlockPosition.fromFactoryTile(this);
		bp.moveForwards(1);
		Block block = worldObj.getBlock(bp.x, bp.y, bp.z);
		int blockMeta = worldObj.getBlockMetadata(bp.x, bp.y, bp.z);

		if (!block.isAir(worldObj, bp.x, bp.y, bp.z) &&
				!block.getMaterial().isLiquid() &&
				block.getBlockHardness(worldObj, bp.x, bp.y, bp.z) >= 0)
		{
			List<ItemStack> drops = block.getDrops(worldObj, bp.x, bp.y, bp.z, blockMeta, 0);
			if (worldObj.setBlockToAir(bp.x, bp.y, bp.z))
			{
				doDrop(drops);
				if(MFRConfig.playSounds.getBoolean(true))
					worldObj.playAuxSFXAtEntity(null, 2001, bp.x, bp.y, bp.z, 
							Block.getIdFromBlock(block) + (blockMeta << 12));
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}
	
	@Override
	public int getWorkMax()
	{
		return 1;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 20;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
}
