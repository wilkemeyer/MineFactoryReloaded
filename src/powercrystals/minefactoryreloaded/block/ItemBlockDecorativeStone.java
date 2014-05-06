package powercrystals.minefactoryreloaded.block;

public class ItemBlockDecorativeStone extends ItemBlockFactory
{
	public ItemBlockDecorativeStone(net.minecraft.block.Block id)
	{
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNames(BlockDecorativeStone._names);
	}
}
