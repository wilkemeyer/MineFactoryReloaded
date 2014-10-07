package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import powercrystals.minefactoryreloaded.tile.tank.TileEntityTank;

public class ItemBlockTank extends ItemBlockFactory implements IFluidContainerItem
{
	public ItemBlockTank(Block p_i45328_1_)
	{
		super(p_i45328_1_);
	}

	@Override
	public FluidStack getFluid(ItemStack container)
	{
		if (container.stackTagCompound != null && container.stackTagCompound.hasKey("tank"))
			return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("tank"));
		return null;
	}

	@Override
	public int getCapacity(ItemStack container)
	{
		return TileEntityTank.CAPACITY;
	}

	@Override
	public int fill(ItemStack stack, FluidStack resource, boolean doFill)
	{
		if (resource == null || stack.stackSize != 1)
			//|| resource.getFluid().getTemperature(resource) > MELTING_POINT)
			return 0;
		int fillAmount = 0, capacity = getCapacity(stack);
		NBTTagCompound tag = stack.stackTagCompound, fluidTag = null;
		FluidStack fluid = null;
		if (tag == null || !tag.hasKey("tank") ||
				(fluidTag = tag.getCompoundTag("tank")) == null ||
				(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null)
			fillAmount = Math.min(capacity, resource.amount);
		if (fluid == null)
		{
			if (doFill)
			{
				fluid = resource.copy();
				fluid.amount = 0;
			}
		}
		else if (!fluid.isFluidEqual(resource))
			return 0;
		else
			fillAmount = Math.min(capacity - fluid.amount, resource.amount);
		fillAmount = Math.max(fillAmount, 0);
		if (doFill)
		{
			if (tag == null)
				tag = stack.stackTagCompound = new NBTTagCompound();
			fluid.amount += fillAmount;
			tag.setTag("tank", fluid.writeToNBT(fluidTag == null ? new NBTTagCompound() : fluidTag));
		}
		return fillAmount;
	}

	@Override
	public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain)
	{
		NBTTagCompound tag = stack.stackTagCompound, fluidTag = null;
		FluidStack fluid = null;
		if (tag == null || !tag.hasKey("tank") ||
				(fluidTag = tag.getCompoundTag("tank")) == null ||
				(fluid = FluidStack.loadFluidStackFromNBT(fluidTag)) == null) {
			if (fluidTag != null)
				tag.removeTag("tank");
			return null;
		}
		int drainAmount = Math.min(maxDrain, fluid.amount);
		if (doDrain)
		{
			tag.removeTag("tank");
			fluid.amount -= drainAmount;
			if (fluid.amount > 0)
				fill(stack, fluid, true);
		}
		fluid.amount = drainAmount;
		return fluid;
	}

}
