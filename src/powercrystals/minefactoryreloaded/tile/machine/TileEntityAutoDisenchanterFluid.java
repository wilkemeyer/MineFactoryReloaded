package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.Locale;

public class TileEntityAutoDisenchanterFluid extends TileEntityAutoDisenchanter
{
	public TileEntityAutoDisenchanterFluid()
	{
		_tanks[0].setLock(FluidRegistry.getFluid("mob_essence"));
	}

	@Override
	protected boolean incrementWorkDone()
	{
		if (fluidHandler.drain(4, false, _tanks[0]) != 4)
			return false;
		fluidHandler.drain(4, true, _tanks[0]);
		return super.incrementWorkDone();
	}

	@Override
	public String getGuiBackground()
	{
		if (_machine == null)
			return null;
		return _machine.getName().toLowerCase(Locale.US) + "2";
	}

	@Override
	protected FluidTankCore[] createTanks()
	{
		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FactoryBucketableFluidHandler() {

				@Override
				public boolean allowBucketFill(ItemStack stack) {

					return true;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return null;
				}
			});
		}

		return super.getCapability(capability, facing);
	}
}
