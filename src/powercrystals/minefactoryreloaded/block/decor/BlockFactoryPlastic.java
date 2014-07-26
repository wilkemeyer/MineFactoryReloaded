package powercrystals.minefactoryreloaded.block.decor;

import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockFactoryPlastic extends BlockFactory
{
	public BlockFactoryPlastic()
	{
		super(0.3f);
		slipperiness = 1f / 0.9801f;
		setBlockName("mfr.plastic");
		setHarvestLevel("axe", 0);
		setHarvestLevel("shovel", 0);
		providesPower = false;
	}
}
