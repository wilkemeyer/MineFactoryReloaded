package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraftforge.fluids.FluidStack;

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
	protected boolean isFluidFuel(FluidStack fuel)
	{
		String name = getFluidName(fuel);
		if (name == null)
			return false;
		return name.equals("steam");
	}
}
