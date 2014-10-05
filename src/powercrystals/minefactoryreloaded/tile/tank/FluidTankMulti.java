package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.util.fluid.FluidTankAdv;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class FluidTankMulti implements IFluidTank {

	private FluidTankAdv[] tanks = new FluidTankAdv[2];
	private int length;
	private FluidStack fluid = null;

	public void addTank(FluidTankAdv tank) {
		if (++length >= tanks.length) {
			FluidTankAdv[] old = tanks;
			tanks = new FluidTankAdv[length * 2];
			System.arraycopy(old, 0, tanks, 0, length);
		}
		tanks[length - 1] = tank;
		fill(tank.drain(tank.getCapacity(), true), true);
	}

	public void removeTank(FluidTankAdv tank) {
		int i = length;
		while (i --> 0) if (tanks[i] == tank) break;
		if (i < 0) return;

		FluidStack r = drain(tank.drain(tank.getFluidAmount(), false), true);

		if (--length != i)
			System.arraycopy(tanks, i + 1, tanks, i, length + 1);

		tanks[length] = null;
		tank.setFluid(r);
	}

	public void empty() {
		for (int i = length; i --> 0;)
			tanks[i] = null;
		length = 0;
	}

	@Override
	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public int getFluidAmount() {
		return fluid == null ? 0 : fluid.amount;
	}

	@Override
	public int getCapacity() {
		return length * TileEntityTank.CAPACITY;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		int f = 0;
		if (resource != null && (fluid == null || fluid.isFluidEqual(resource))) {
			int i = 0;
			while (i < length && f < resource.amount) {
				f += tanks[i].fill(resource, doFill);
				++i;
			}
			if (i == length)
				 --i;
			if (doFill) {
				if (fluid == null)
					fluid = new FluidStack(resource, 0);
				fluid.amount += f;
			}
		}
		return f;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (fluid != null) {
			FluidStack r = new FluidStack(fluid, 0);
			int i = length - 1;
			while (i >= 0 && maxDrain > 0) {
				FluidStack d = tanks[i].drain(maxDrain, doDrain);
				if (d != null) {
					r.amount += d.amount;
					maxDrain -= d.amount;
				}
				--i;
			}
			if (i == -1)
				 ++i;
			if (doDrain) {
				fluid.amount -= r.amount;
				if (fluid.amount <= 0)
					fluid = null;
			}
			return r;
		}
		return null;
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (fluid != null && fluid.isFluidEqual(resource)) {
			return drain(resource.amount, doDrain);
		}
		return null;
	}

}
