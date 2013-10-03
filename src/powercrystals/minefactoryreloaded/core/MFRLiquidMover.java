package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import powercrystals.core.position.BlockPosition;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public abstract class MFRLiquidMover
{
	/**
	 * Attempts to fill tank with the player's current item.
	 * @param	itcb			the tank the liquid is going into
	 * @param	entityplayer	the player trying to fill the tank
	 * @return	True if liquid was transferred to the tank.
	 */
	public static boolean manuallyFillTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(ci);
		if(liquid != null)
		{
			if(itcb.fill(ForgeDirection.UNKNOWN, liquid, false) == liquid.amount)
			{
				itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				if(!entityplayer.capabilities.isCreativeMode)
				{
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, UtilInventory.consumeItem(ci, entityplayer));					
				}
				return true;
			}
		}
		else if (ci != null && ci.getItem() instanceof IFluidContainerItem)
		{
			IFluidContainerItem fluidContainer = (IFluidContainerItem)ci.getItem();
			liquid = fluidContainer.getFluid(ci);
			if (itcb.fill(ForgeDirection.UNKNOWN, liquid, false) > 0) {
				int amount = itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				fluidContainer.drain(ci, amount, true);
			}
		}
		return false;
	}
	
	/**
	 * Attempts to drain tank into the player's current item.
	 * @param	itcb			the tank the liquid is coming from
	 * @param	entityplayer	the player trying to take liquid from the tank
	 * @return	True if liquid was transferred from the tank.
	 */
	public static boolean manuallyDrainTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		boolean isSmartContainer = false;
		IFluidContainerItem fluidContainer;
		if(ci!= null && (FluidContainerRegistry.isEmptyContainer(ci) ||
				(isSmartContainer = ci.getItem() instanceof IFluidContainerItem)))
		{
			for(FluidTankInfo tank : itcb.getTankInfo(ForgeDirection.UNKNOWN))
			{
				FluidStack tankLiquid = tank.fluid;
				if (tankLiquid == null || tankLiquid.amount == 0)
					continue;
				if (isSmartContainer)
				{
					fluidContainer = (IFluidContainerItem)ci.getItem();
					if (fluidContainer.fill(ci, tankLiquid, false) > 0) {
						int amount = fluidContainer.fill(ci, tankLiquid, true);
						itcb.drain(ForgeDirection.UNKNOWN, new FluidStack(tankLiquid.fluidID, amount), true);
					}
				}
				else
				{
					ItemStack filledBucket = FluidContainerRegistry.fillFluidContainer(tankLiquid, ci);
					if(FluidContainerRegistry.isFilledContainer(filledBucket))
					{
						FluidStack bucketLiquid = FluidContainerRegistry.getFluidForFilledItem(filledBucket);
						itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
						if(entityplayer.capabilities.isCreativeMode)
						{
							return true;
						}
						else if(ci.stackSize <= 1)
						{
							entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, filledBucket);
							return true;
						}
						else 
						{
							ci.stackSize -= 1;
							if(!entityplayer.inventory.addItemStackToInventory(filledBucket))
							{
								// TODO: config option, continue loop if false
								entityplayer.dropPlayerItem(filledBucket);
							}
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}
	
	public static void pumpLiquid(IFluidTank iFluidTank, TileEntityFactory from)
	{
		if(iFluidTank != null && iFluidTank.getFluid() != null && iFluidTank.getFluid().amount > 0)
		{
			FluidStack l = iFluidTank.getFluid().copy();
			l.amount = Math.min(l.amount, FluidContainerRegistry.BUCKET_VOLUME);
			for(BlockPosition adj : new BlockPosition(from).getAdjacent(true))
			{
				TileEntity tile = from.worldObj.getBlockTileEntity(adj.x, adj.y, adj.z);
				if(tile instanceof IFluidHandler)
				{
					int filled = ((IFluidHandler)tile).fill(adj.orientation.getOpposite(), l, true);
					iFluidTank.drain(filled, true);
					l.amount -= filled;
					if(l.amount <= 0)
					{
						break;
					}
				}
			}
		}
	}
	
}