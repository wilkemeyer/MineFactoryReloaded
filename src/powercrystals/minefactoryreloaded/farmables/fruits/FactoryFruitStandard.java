package powercrystals.minefactoryreloaded.farmables.fruits;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public abstract class FactoryFruitStandard implements IFactoryFruit  {

	private Block _block;
	private ReplacementBlock replBlock;
	
	public FactoryFruitStandard(Block block, ReplacementBlock replacement)
	{
		if (block.equals(Blocks.AIR))
			throw new IllegalArgumentException("Passed air FactoryFruitStandard");

		_block = block;
		replBlock = replacement;
	}
	
	public FactoryFruitStandard(Block block, Block replacement, int meta)
	{
		this(block, replacement == null ? null : new ReplacementBlock(replacement).setMeta(meta));
	}
	
	public FactoryFruitStandard(Block block, Block replacement)
	{
		this(block, replacement, 0);
	}
	
	public FactoryFruitStandard(Block block)
	{
		this(block, (ReplacementBlock)null);
	}
	
	@Override
	public Block getPlant()
	{
		return _block;
	}
	
	@Override
	public abstract boolean canBePicked(World world, BlockPos pos);

	@Override
	public boolean breakBlock()
	{
		return replBlock == null;
	}
	
	@Override
	public ReplacementBlock getReplacementBlock(World world, BlockPos pos)
	{
		return replBlock;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().getDrops(world, pos, state, 0);
	}

}
