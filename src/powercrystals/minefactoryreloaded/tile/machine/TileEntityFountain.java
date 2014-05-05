package powercrystals.minefactoryreloaded.tile.machine;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import cofh.util.position.Area;
import cofh.util.position.BlockPosition;
import powercrystals.minefactoryreloaded.api.IUpgrade;
import powercrystals.minefactoryreloaded.api.IUpgrade.UpgradeType;
import powercrystals.minefactoryreloaded.core.FluidFillingManager;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestManager;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUpgradable;
import powercrystals.minefactoryreloaded.gui.container.ContainerFountain;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityFountain extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private IHarvestManager _fillingManager;
	public TileEntityFountain()
	{
		super(Machine.Fountain);
		_areaManager = new HarvestAreaManager(this, 0, 0, 0, 1.0f, false);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
		_areaManager.setUpgradeVertical(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiUpgradable(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFountain getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFountain(this, inventoryPlayer);
	}

	@Override
	protected boolean activateMachine()
	{
		int idleTicks = 5;
		l: {
			if (_tanks[0].getFluidAmount() >= BUCKET_VOLUME &&
					_tanks[0].getFluid().getFluid().canBePlacedInWorld())
			{
				int x = xCoord, y = yCoord + 1, z = zCoord;
				if (_fillingManager != null)
				{
					if (_fillingManager.getIsDone())
						onFactoryInventoryChanged();
					BlockPosition bp = _fillingManager.getNextBlock();
					x = bp.x; y = bp.y; z = bp.z;
					_fillingManager.moveNext();
				}
				Block block = worldObj.getBlock(x, y, z);
				if (block == null || block.isReplaceable(worldObj, x, y, z))
				{
					if (block != null && block.getMaterial().isLiquid())
						if (block instanceof BlockFluidClassic)
						{
							if (((BlockFluidClassic)block).isSourceBlock(worldObj, x, y, z))
								break l;
						}
						else if (block instanceof BlockLiquid)
						{
							if (worldObj.getBlockMetadata(x, y, z) == 0)
								break l;
						}
					block = _tanks[0].getFluid().getFluid().getBlock();
					if (worldObj.setBlock(x, y, z, block))
					{// TODO: when forge supports NBT fluid blocks, adapt this
						worldObj.notifyBlockOfNeighborChange(x, y, z, block);
						drain(_tanks[0], BUCKET_VOLUME, true);
						setIdleTicks(1);
						return true;
					}
				}
			}
			if (_fillingManager != null)
				_fillingManager.free();
			idleTicks = getIdleTicksMax();
		}
		setIdleTicks(idleTicks);
		return false;
	}

	@Override
	protected FluidTank[] createTanks()
	{
		return new FluidTank[] {new FluidTank(BUCKET_VOLUME * 32)};
	}
	
	@Override
	public void onFactoryInventoryChanged()
	{
		if (_inventory[0] != null && _inventory[0].getItem() instanceof IUpgrade)
		{
			IUpgrade upgrade = (IUpgrade)_inventory[0].getItem();
			if (upgrade.isApplicableFor(UpgradeType.RADIUS, _inventory[0]))
			{
				int r = upgrade.getUpgradeLevel(UpgradeType.RADIUS, _inventory[0]);
				if (r > 0)
				{
					_areaManager.setUpgradeLevel(r);
					Area area = new Area(new BlockPosition(xCoord, yCoord + 1, zCoord), r, 0, r * 2);
					if (_fillingManager == null)
						_fillingManager = new FluidFillingManager(worldObj, area);
					else
						_fillingManager.reset(worldObj, area, null);
				}
			}
		}
		else
			_fillingManager = null;
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
		return 10;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if (stack != null)
		{
			if (slot == 0)
			{
				return stack.getItem() instanceof IUpgrade;
			}
		}
		return false;
	}
}
