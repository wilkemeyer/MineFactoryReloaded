package powercrystals.minefactoryreloaded.block;

public class ItemBlockFactoryLeaves extends ItemBlockFactory {

	public ItemBlockFactoryLeaves(int id) {
		super(id);
		setHasSubtypes(true);
		setNames(BlockRubberLeaves._names);
	}

	@Override
	public int getMetadata(int par1)
	{
		return par1 | 4;
	}

}
