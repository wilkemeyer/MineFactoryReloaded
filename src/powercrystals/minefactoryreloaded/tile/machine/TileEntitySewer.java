package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.position.Area;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiSewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerSewer;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntitySewer extends TileEntityFactoryInventory implements ITankContainerBucketable
{
	private int _tick;
	private long _nextSewerCheckTick;
	private boolean _jammed;
	
	public TileEntitySewer()
	{
		super(Machine.Sewer);
		createHAM(this, 0, 1, 0, false);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
		_tanks[0].setLock(FluidRegistry.getFluid("sewage"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiSewer(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerSewer getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerSewer(this, inventoryPlayer);
	}
	
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_areaManager.updateUpgradeLevel(_inventory[0]);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(worldObj.isRemote)
		{
			return;
		}
		_tick++;
		
		if (_nextSewerCheckTick <= worldObj.getTotalWorldTime())
		{
			Area a = new Area(BlockPosition.fromRotateableTile(this), _areaManager.getRadius(), 0, 0);
			_jammed = false;
			for (BlockPosition bp : a.getPositionsBottomFirst())
			{
				if (worldObj.getBlock(bp.x, bp.y, bp.z).equals(MineFactoryReloadedCore.machineBlocks.get(0)) &&
						worldObj.getBlockMetadata(bp.x, bp.y, bp.z) == Machine.Sewer.getMeta() &&
						!(bp.x == xCoord && bp.y == yCoord && bp.z == zCoord))
				{
					_jammed = true;
					break;
				}
			}
			
			_nextSewerCheckTick = worldObj.getTotalWorldTime() + 800 + worldObj.rand.nextInt(800);
		}
		
		if (_tick >= 31 && !_jammed)
		{
			_tick = 0;
			List<?> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());
			double massFound = 0;
			for (Object o : entities)
			{
				if (o instanceof EntityAnimal || o instanceof EntityVillager)
				{
					massFound += Math.pow(((EntityLivingBase)o).boundingBox.getAverageEdgeLength(), 2);
				}
				else if (o instanceof EntityPlayer && ((EntityPlayer)o).isSneaking())
				{
					massFound += Math.pow(((EntityLivingBase)o).boundingBox.getAverageEdgeLength(), 2);
				}
			}
			if (massFound > 0)
			{
				_tanks[0].fill(FluidRegistry.getFluidStack("sewage", (int)(25 * massFound)), true);
			}
			int maxAmount = _tanks[1].getSpace();
			if (maxAmount <= 0)
			{
				return;
			}
			entities = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, _areaManager.getHarvestArea().toAxisAlignedBB());
			for (Object o : entities)
			{
				Entity e = (Entity)o;
				if (e != null & e instanceof EntityXPOrb && !e.isDead)
				{
					EntityXPOrb orb = (EntityXPOrb)o;
					if (MFRLiquidMover.fillTankWithXP(_tanks[1], orb) == 0)
						break;
				}
			}
		}
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
		return drain(maxDrain, doDrain);
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return drain(resource, doDrain);
	}
	
	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[] { new FluidTankAdv(BUCKET_VOLUME),
				new FluidTankAdv(BUCKET_VOLUME * 4) };
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}
	
	@Override
	public int getUpgradeSlot()
	{
		return 0;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return slot == 0 && isUsableAugment(itemstack);
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return false;
	}
}
