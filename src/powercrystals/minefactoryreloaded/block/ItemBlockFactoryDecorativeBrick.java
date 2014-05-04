package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;

public class ItemBlockFactoryDecorativeBrick extends ItemBlockFactory
{
	public ItemBlockFactoryDecorativeBrick(Block block)
	{
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNames(BlockFactoryDecorativeBricks._names);
	}
}
