package powercrystals.minefactoryreloaded.tile.machine;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradable;
import powercrystals.minefactoryreloaded.gui.container.ContainerUpgradable;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFountain extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	public TileEntityFountain()
	{
		super(Machine.Fountain);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiUpgradable(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerUpgradable getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerUpgradable(this, inventoryPlayer, 0);
	}

	@Override
	protected boolean activateMachine()
	{ // TODO: use upgrade slot
		int x = xCoord, y = yCoord + 1, z = zCoord;
		Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
		if (block == null || (!block.blockMaterial.isLiquid() && block.isBlockReplaceable(worldObj, x, y, z)))
			if (_tanks[0].getFluidAmount() >= BUCKET_VOLUME)
			{
				int blockid = _tanks[0].getFluid().getFluid().getBlockID();
				if (blockid > 0 && worldObj.setBlock(x, y, z, blockid))
				{// TODO: when forge supports NBT fluid blocks, adapt this
					drain(_tanks[0], BUCKET_VOLUME, true);
					return true;
				}
			}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(BUCKET_VOLUME * 32)};
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource != null)
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (_tank.getFluidAmount() == 0 || resource.isFluidEqual(_tank.getFluid()))
					return _tank.fill(resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		for (FluidTank _tank : (FluidTank[])getTanks())
			if (_tank.getFluidAmount() > 0)
				return _tank.drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (resource != null)
			for (FluidTank _tank : (FluidTank[])getTanks())
				if (resource.isFluidEqual(_tank.getFluid()))
					return _tank.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public boolean allowBucketDrain()
	{
		return true;
	}

	@Override
	public boolean allowBucketFill()
	{
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}

	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public int getWorkMax()
	{
		return 1;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 5;
	}
}
