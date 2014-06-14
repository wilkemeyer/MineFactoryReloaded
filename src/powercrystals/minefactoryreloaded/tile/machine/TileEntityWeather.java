package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityWeather extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	
	public TileEntityWeather()
	{
		super(Machine.WeatherCollector);
		setManageSolids(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}
	
	@Override
	public int getWorkMax()
	{
		return 50;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 600;
	}
	
	@Override
	public boolean activateMachine()
	{
		if (worldObj.getWorldInfo().isRaining() && canSeeSky())
		{
			BiomeGenBase bgb = worldObj.getBiomeGenForCoords(xCoord, zCoord);
			
			if (!bgb.canSpawnLightningBolt() && !bgb.getEnableSnow())
			{
				setIdleTicks(getIdleTicksMax());
				return false;
			}
			setWorkDone(getWorkDone() + 1);
			if (getWorkDone() >= getWorkMax())
			{
				if (bgb.getFloatTemperature(xCoord, yCoord, zCoord) >= 0.15F)
				{
					if(_tanks[0].fill(FluidRegistry.getFluidStack("water", FluidContainerRegistry.BUCKET_VOLUME), true) > 0)
					{
						setWorkDone(0);
						return true;
					}
					else
					{
						setWorkDone(getWorkMax());
						return false;
					}
				}
				else
				{
					doDrop(new ItemStack(Items.snowball));
					setWorkDone(0);
				}
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}
	
	@Override
	public ForgeDirection getDropDirection()
	{
		return ForgeDirection.DOWN;
	}
	
	private boolean canSeeSky()
	{
		for(int y = yCoord + 1; y < 256; y++)
		{
			Block block = worldObj.getBlock(xCoord, y, zCoord);
			if (!block.isAir(worldObj, xCoord, y, zCoord))
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}
	
	@Override
	public boolean allowBucketDrain(ItemStack stack)
	{
		return true;
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
	protected FluidTank[] createTanks()
	{
		return new FluidTank[]{new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME)};
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}
}
