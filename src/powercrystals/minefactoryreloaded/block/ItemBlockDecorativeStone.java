package powercrystals.minefactoryreloaded.block;

public class ItemBlockDecorativeStone extends ItemBlockFactory
{
	public ItemBlockDecorativeStone(int id)
	{
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNames(BlockDecorativeStone._names);
	}
}
