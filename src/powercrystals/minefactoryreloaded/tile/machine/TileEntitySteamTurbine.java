package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class TileEntitySteamTurbine extends TileEntityLiquidGenerator
{
	public static final int liquidConsumedPerTick = Machine.SteamTurbine.getActivationEnergy() / 2;
	public static final int ticksBetweenConsumption = 1;

	public TileEntitySteamTurbine()
	{
		super(Machine.SteamTurbine, liquidConsumedPerTick, ticksBetweenConsumption);
	}
	
	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8)};
	}

	@Override
	protected boolean isFluidFuel(FluidStack fuel)
	{
		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return name.equals("steam");
	}
}
