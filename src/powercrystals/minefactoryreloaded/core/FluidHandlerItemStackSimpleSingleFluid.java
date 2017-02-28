package powercrystals.minefactoryreloaded.core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import javax.annotation.Nullable;

public class FluidHandlerItemStackSimpleSingleFluid extends FluidHandlerItemStackSimple.SwapEmpty {

	private Fluid fluid;

	public FluidHandlerItemStackSimpleSingleFluid(ItemStack container, ItemStack emptyContainer, Fluid fluid, int capacity) {
		super(container, emptyContainer, capacity);
		this.fluid = fluid;
	}

	@Nullable
	@Override
	public FluidStack getFluid() {

		return container.getItem() == container.getItem() ? new FluidStack(fluid, capacity) : null;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluidStack) {

		return fluidStack.getFluid().equals(fluid);
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluidStack) {

		return fluidStack.getFluid().equals(fluid);
	}
}
