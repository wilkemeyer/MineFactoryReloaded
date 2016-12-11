package powercrystals.minefactoryreloaded.block.transport;

import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;
import cofh.lib.util.position.BlockPosition;

import java.util.Map.Entry;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.core.UtilInventory;

public class BlockRailCargoDropoff extends BlockFactoryRail
{
	public BlockRailCargoDropoff()
	{
		super(true, false);
		setUnlocalizedName("mfr.rail.cargo.dropoff");
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entity, BlockPos pos)
	{
		if (world.isRemote || !(entity instanceof IInventory))
			return;

		IInventoryManager minecart = InventoryManager.create(entity, null);

		for (Entry<Integer, ItemStack> contents : minecart.getContents().entrySet())
		{
			if (contents.getValue() == null)
			{
				continue;
			}

			ItemStack stackToAdd = contents.getValue().copy();
			ItemStack remaining = UtilInventory.dropStack(world, pos, contents.getValue(), EnumFacing.values(), null);

			if (remaining != null)
			{
				stackToAdd.stackSize -= remaining.stackSize;
			}

			minecart.removeItem(stackToAdd.stackSize, stackToAdd);
		}
	}
}
