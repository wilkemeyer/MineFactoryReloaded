package powercrystals.minefactoryreloaded.farmables.ranchables;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.core.inventory.IInventoryManager;
import powercrystals.core.inventory.InventoryManager;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.RanchedItem;

public class RanchableCow implements IFactoryRanchable
{
	@Override
	public Class<?> getRanchableEntity()
	{
		return EntityCow.class;
	}
	
	@Override
	public List<RanchedItem> ranch(World world, EntityLiving entity, IInventory rancher)
	{
		List<RanchedItem> drops = new LinkedList<RanchedItem>();
		IInventoryManager manager = InventoryManager.create(rancher, ForgeDirection.UP);
		int bucketIndex = manager.findItem(new ItemStack(Item.bucketEmpty));
		if(bucketIndex >= 0)
		{
			drops.add(new RanchedItem(Item.bucketMilk));
			rancher.decrStackSize(bucketIndex, 1);
		}
		else
		{
			FluidStack milk = FluidRegistry.getFluidStack("milk", FluidContainerRegistry.BUCKET_VOLUME);
			drops.add(new RanchedItem(milk));
		}
		
		return drops;
	}
}
