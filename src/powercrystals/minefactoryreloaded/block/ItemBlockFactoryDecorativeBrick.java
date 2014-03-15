package powercrystals.minefactoryreloaded.block;

public class ItemBlockFactoryDecorativeBrick extends ItemBlockFactory
{
	public ItemBlockFactoryDecorativeBrick(int id)
	{
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNames(BlockFactoryDecorativeBricks._names);
	}
}
