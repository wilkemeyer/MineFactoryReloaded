package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class TileEntityBioFuelGenerator extends TileEntityLiquidGenerator
{
	public static final int liquidConsumedPerTick = 1;
	public static final int ticksBetweenConsumption = 5;
	
	public TileEntityBioFuelGenerator()
	{
		super(Machine.BioFuelGenerator, liquidConsumedPerTick, ticksBetweenConsumption);
	}
	
	@Override
	protected boolean isLiquidFuel(FluidStack fuel)
	{
		if (fuel == null || fuel.getFluid() == null)
			return false;
		String name = fuel.getFluid().getName();
		if (name == null)
			return false;
		name = name.trim().toLowerCase();
		return name.equals("biofuel") || name.equals("bioethanol");
	}
}
