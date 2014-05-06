package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public class PamFruit implements IFactoryFruit
{
	private Block _sourceId;

	public PamFruit(Block sourceId)
	{
		_sourceId = sourceId;
	}

	@Override
	public Block getPlant()
	{
		return _sourceId;
	}

	@Override
	public boolean canBePicked(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) >= 2;
	}

	@Override
	public boolean breakBlock()
	{
		return false;
	}

	@Override
	public ReplacementBlock getReplacementBlock(World world, int x, int y, int z)
	{
		return new ReplacementBlock(getPlant());
	}

	@Override
	public void prePick(World world, int x, int y, int z)
	{
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return id.getDrops(world, x, y, z, meta, 0);
	}

	@Override
	public void postPick(World world, int x, int y, int z)
	{
	}

}
