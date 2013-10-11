package powercrystals.minefactoryreloaded.tile.base;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered.MFRPerdition;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeTile.PipeType;

public abstract class TileEntityGenerator extends TileEntityFactoryInventory implements IPowerReceptor, IPowerEmitter
{
	private PowerHandler _powerProvider;
	
	protected TileEntityGenerator(Machine machine)
	{
		super(machine);
		_powerProvider = new PowerHandler(this, PowerHandler.Type.GATE);
		_powerProvider.configure(0, 0, 0, 0);
		_powerProvider.setPerdition(MFRPerdition.DEFAULT);
	}
	
	protected final int producePower(int mj)
	{
		BlockPosition ourbp = BlockPosition.fromFactoryTile(this);
		
		for(BlockPosition bp : ourbp.getAdjacent(true))
		{
			TileEntity te = worldObj.getBlockTileEntity(bp.x, bp.y, bp.z);
			if(te == null || !(te instanceof IPowerReceptor))
			{
				continue;
			}
			
			IPowerReceptor ipr = ((IPowerReceptor)te);
			PowerReceiver pp = ipr.getPowerReceiver(bp.orientation);
			if(pp != null && pp.powerRequest() > 0 && pp.getMinEnergyReceived() <= mj)
			{
				float mjUsed = Math.min(Math.min(pp.getMaxEnergyReceived(), mj), pp.getMaxEnergyStored() - pp.getEnergyStored());
				pp.receiveEnergy(_powerProvider.getPowerReceiver().getType(), mjUsed, bp.orientation);
				
				mj -= mjUsed;
				if(mj <= 0)
				{
					return 0;
				}
			}
		}
		
		return mj;
	}
	
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side)
	{
		return _powerProvider.getPowerReceiver();
	}
	
	@Override
	public void doWork(PowerHandler workProvider)
	{
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return ConnectOverride.DEFAULT;
	}

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return true;
	}
}
