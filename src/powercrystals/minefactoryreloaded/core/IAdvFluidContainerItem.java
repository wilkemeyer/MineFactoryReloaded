package powercrystals.minefactoryreloaded.core;

import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IAdvFluidContainerItem extends IFluidHandler
{
	boolean canBeFilledFromWorld();
	boolean canPlaceInWorld();
	boolean shouldReplaceWhenFilled();
}
