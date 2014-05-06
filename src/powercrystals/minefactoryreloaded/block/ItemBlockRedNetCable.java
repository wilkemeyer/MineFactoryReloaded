package powercrystals.minefactoryreloaded.block;

public class ItemBlockRedNetCable extends ItemBlockFactory
{
	public ItemBlockRedNetCable(net.minecraft.block.Block id)
	{
		super(id);
		setHasSubtypes(true);
		setNames(new String[]{null, "glass", "energy", "energyglass"});
	}
}
