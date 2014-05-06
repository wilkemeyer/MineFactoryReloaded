package powercrystals.minefactoryreloaded.block;

public class ItemBlockFactoryGlassPane extends ItemBlockFactory
{
	public ItemBlockFactoryGlassPane(net.minecraft.block.Block blockId)
	{
		super(blockId);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNames(new String[] { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" });
	}
}
