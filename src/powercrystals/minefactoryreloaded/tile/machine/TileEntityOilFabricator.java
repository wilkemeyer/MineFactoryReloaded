package powercrystals.minefactoryreloaded.tile.machine;

import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidFabricator;

public class TileEntityOilFabricator extends TileEntityLiquidFabricator
{
	public TileEntityOilFabricator()
	{
		super(FluidRegistry.getFluidID(FluidRegistry.isFluidRegistered("oil") ? "oil" : "water"), 1, Machine.OilFabricator);
	}
}
