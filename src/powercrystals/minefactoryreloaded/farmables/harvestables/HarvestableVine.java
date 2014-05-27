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

public class HarvestableVine implements IFactoryHarvestable
{
	protected Block vine;
	public HarvestableVine() { this(Blocks.vine); }
	public HarvestableVine(Block vine)
	{
		this.vine = vine;
	}
	
	@Override
	public Block getPlant()
	{
		return vine;
	}
	
	@Override
	public HarvestType getHarvestType()
	{
		return HarvestType.TreeFruit;
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
		drops.add(new ItemStack(vine));
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
