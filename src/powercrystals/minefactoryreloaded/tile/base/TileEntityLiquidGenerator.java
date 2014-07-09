package powercrystals.minefactoryreloaded.tile.base;

import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLiquidGenerator;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquidGenerator;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityLiquidGenerator extends TileEntityGenerator implements ITankContainerBucketable
{
	private int _liquidConsumedPerTick;
	private int _powerProducedPerConsumption;

	public TileEntityLiquidGenerator(Machine machine, int liquidConsumedPerTick,
			int ticksBetweenConsumption)
	{
		this(machine, liquidConsumedPerTick,
				machine.getActivationEnergy() * ticksBetweenConsumption,
				ticksBetweenConsumption);
	}

	public TileEntityLiquidGenerator(Machine machine, int liquidConsumedPerTick,
			int powerProducedPerConsumption, int ticksBetweenConsumption)
	{
		super(machine, ticksBetweenConsumption);
		_powerProducedPerConsumption = powerProducedPerConsumption;
		_liquidConsumedPerTick = liquidConsumedPerTick;
	}

	@Override
	protected boolean consumeFuel()
	{
		FluidStack drained = drain(ForgeDirection.UNKNOWN, _liquidConsumedPerTick, false);

		if (drained == null || drained.amount != _liquidConsumedPerTick)
			return false;

		drain(ForgeDirection.UNKNOWN, _liquidConsumedPerTick, true);
		return true;
	}
	
	@Override
	protected boolean canConsumeFuel(int space)
	{
		return space >= _powerProducedPerConsumption;
	}
	
	@Override
	protected int produceEnergy()
	{
		return _powerProducedPerConsumption;
	}

	protected abstract boolean isFluidFuel(FluidStack fuel);

	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[] {new FluidTankAdv(FluidContainerRegistry.BUCKET_VOLUME * 4)};
	}

	protected String getFluidName(FluidStack fluid)
	{
		if (fluid == null || fluid.getFluid() == null)
			return null;
		String name = fluid.getFluid().getName();
		if (name == null)
			return null;
		return name.trim().toLowerCase();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiLiquidGenerator(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerLiquidGenerator getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerLiquidGenerator(this, inventoryPlayer);
	}

	@Override
	public String getGuiBackground()
	{
		return "fluidgenerator.png";
	}

	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource != null && isFluidFuel(resource))
			for (FluidTankAdv _tank : getTanks())
				if (_tank.getFluidAmount() == 0 || resource.isFluidEqual(_tank.getFluid()))
					return _tank.fill(resource, doFill);
		return 0;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return drain(maxDrain, doDrain);
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return drain(resource, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
}
