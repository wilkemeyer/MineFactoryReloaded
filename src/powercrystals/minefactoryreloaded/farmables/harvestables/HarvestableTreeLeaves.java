package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class HarvestableTreeLeaves extends HarvestableStandard
{
	public HarvestableTreeLeaves(Block block)
	{
		super(block, powercrystals.minefactoryreloaded.api.HarvestType.TreeLeaf);
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> settings, int x, int y, int z)
	{
		if (settings.get("silkTouch") == Boolean.TRUE)
		{
			Block block = world.getBlock(x, y, z);
			if (block instanceof IShearable)
			{
				ItemStack stack = new ItemStack(Items.shears, 1, 0);
				if (((IShearable)block).isShearable(stack, world, x, y, z))
				{
					return ((IShearable)block).onSheared(stack, world, x, y, z, 0);
				}
			}
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			int meta = block.getDamageValue(world, x, y, z);
			drops.add(new ItemStack(getPlant(), 1, meta));
			return drops;
		}
		else
		{
			return getPlant().getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		}
	}

	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
		Block id = getPlant();

		notifyBlock(world, x, y - 1, z, id);
		notifyBlock(world, x - 1, y, z, id);
		notifyBlock(world, x + 1, y, z, id);
		notifyBlock(world, x, y, z - 1, id);
		notifyBlock(world, x, y, z + 1, id);
		notifyBlock(world, x, y + 1, z, id);
	}

	protected void notifyBlock(World world, int x, int y, int z, Block id)
	{
		Block block = world.getBlock(x, y, z);
		if (!block.isLeaves(world, x, y, z))
			world.notifyBlockOfNeighborChange(x, y, z, id);
	}
}
