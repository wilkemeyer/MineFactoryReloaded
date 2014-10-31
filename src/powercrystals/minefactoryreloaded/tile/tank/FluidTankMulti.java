package powercrystals.minefactoryreloaded.tile.tank;

import cofh.core.util.fluid.FluidTankAdv;
import com.google.common.base.Throwables;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class FluidTankMulti implements IFluidTank {

	FluidTankAdv[] tanks = new FluidTankAdv[2];
	int length, index;
	private FluidStack fluid = null;
	private TankNetwork grid;

	public FluidTankMulti(TankNetwork network) {
		grid = network;
	}

	public void addTank(FluidTankAdv tank) {
		if (++length >= tanks.length) {
			FluidTankAdv[] old = tanks;
			tanks = new FluidTankAdv[length * 2];
			System.arraycopy(old, 0, tanks, 0, length - 1);
		}
		tanks[length - 1] = tank;
		fill(tank.drain(tank.getCapacity(), true), true);
	}

	public void removeTank(FluidTankAdv tank) {
		int i = length;
		while (i --> 0) if (tanks[i] == tank) break;
		if (i < 0) return;

		FluidStack r;
		try {
		r = drain(tank.drain(tank.getFluidAmount(), false), true);
		} catch (Throwable _) {
			System.out.format("index: %s, length: %s, tanks.length: %s, ", index, length, tanks.length);
			throw Throwables.propagate(_);
		}

		{
			FluidTankAdv[] old = tanks;
			if (--length != i) {
				System.arraycopy(old, i + 1, old, i, length - i + 1);
			}
			if (length <= old.length / 4) {
				tanks = new FluidTankAdv[old.length / 2];
				if (i > 0)
					System.arraycopy(old, 0, tanks, 0, i);
			}
		}

		tanks[length] = null;
		if (tanks[index] == null) --index;
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
			int i = index;
			while (i < length) {
				f += tanks[i].fill(resource, doFill);
				if (f >= resource.amount)
					break;
				++i;
			}
			if (i == length)
				 --i;
			if (doFill) {
				index = i;
				boolean u = false;
				if (fluid == null) {
					u = true;
					fluid = new FluidStack(resource, 0);
				}
				fluid.amount += f;
				if (u)
					grid.updateNodes();
			}
		}
		return f;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (fluid != null) {
			FluidStack r = new FluidStack(fluid, 0);
			int i = index;
			while (i >= 0) {
				FluidStack d = tanks[i].drain(maxDrain, doDrain);
				if (d != null) {
					r.amount += d.amount;
					maxDrain -= d.amount;
				}
				if (maxDrain <= 0)
					break;
				--i;
			}
			if (i == -1)
				 ++i;
			if (doDrain) {
				index = i;
				fluid.amount -= r.amount;
				if (fluid.amount <= 0) {
					fluid = null;
					grid.updateNodes();
				}
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
