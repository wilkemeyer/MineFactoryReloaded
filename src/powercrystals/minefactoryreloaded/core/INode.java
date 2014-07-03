package powercrystals.minefactoryreloaded.core;

public interface INode
{
	public boolean isNotValid();
	public void updateInternalTypes(IGridController grid);
	public void firstTick(IGridController grid);
}
