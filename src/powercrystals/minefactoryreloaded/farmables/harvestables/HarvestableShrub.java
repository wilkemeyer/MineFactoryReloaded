package powercrystals.minefactoryreloaded.farmables.harvestables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

public class HarvestableShrub implements IFactoryHarvestable
{
	private Block _block;

	public HarvestableShrub(Block block)
	{
		_block = block;
	}

	@Override
	public Block getPlant()
	{
		return _block;
	}

	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.Normal;
	}

	@Override
	public boolean breakBlock()
	{
		return true;
	}

	@Override
	public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		return true;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();

		int meta = world.getBlockMetadata(x, y, z);
		if (!harvesterSettings.get("silkTouch") && (
				(_block == Blocks.tallgrass && meta == 1) ||
				(_block == Blocks.double_plant && meta == 2)))
		{
			drops.addAll(_block.getDrops(world, x, y, z, meta, 0));
		}
		else
		{
			drops.add(new ItemStack(_block, 1, meta));
		}

		return drops;
	}

	@Override
	public void preHarvest(World world, int x, int y, int z)
	{
	}

	@Override
	public void postHarvest(World world, int x, int y, int z)
	{
	}
}
