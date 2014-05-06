package powercrystals.minefactoryreloaded.modhelpers.pam;import java.lang.reflect.Method;import java.util.Random;import net.minecraft.block.Block;import net.minecraft.tileentity.TileEntity;import net.minecraft.world.World;import powercrystals.minefactoryreloaded.api.FertilizerType;import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
class PamFertilizable implements IFactoryFertilizable
{
	protected Method getGrowthStage;
	protected Method _fertilize;
	private Block _blockId;
	final private Object[] dummyArgs = new Object[]{};
	public PamFertilizable(Block blockId) throws ClassNotFoundException
	{
		_blockId = blockId;
		getGrowthStage=Pam.pamTEGetGrowthStage;
		_fertilize=Pam.pamBlockFertilize;
	}
	@Override
	public Block getPlant()
	{
		return _blockId;
	}

	@Override
	public boolean canFertilize(World world, int x, int y, int z, FertilizerType fertilizerType)
	{
		int stage=0;
		TileEntity te=world.getTileEntity(x,y,z);
		if(getGrowthStage!=null&&te!=null)
		{
			try
			{
				stage=(Integer)(getGrowthStage.invoke(te,dummyArgs));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return stage < 2 && fertilizerType == FertilizerType.GrowPlant;
	}
	@Override
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType)
	{
		int stage=0;
		TileEntity te=world.getTileEntity(x,y,z);
		if(getGrowthStage!=null&&te!=null)
		{
			try
			{
				stage=(Integer)(getGrowthStage.invoke(te,dummyArgs));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (stage>=2 )
			return false;
		try
		{
			_fertilize.invoke(_blockId, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}}