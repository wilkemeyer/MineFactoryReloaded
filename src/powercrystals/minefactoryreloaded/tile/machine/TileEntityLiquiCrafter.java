package powercrystals.minefactoryreloaded.tile.machine;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.core.util.Util;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.core.RemoteInventoryCrafting;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiLiquiCrafter;
import powercrystals.minefactoryreloaded.gui.container.ContainerLiquiCrafter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// slots 0-8 craft grid, 9 craft grid template output, 10 output, 11-28 resources
public class TileEntityLiquiCrafter extends TileEntityFactoryInventory implements ITankContainerBucketable
{
	private boolean _lastRedstoneState;
	private boolean _resourcesChangedSinceLastFailedCraft = true;
	
	private FluidTank[] _tanks = new FluidTank[9];
	
	public TileEntityLiquiCrafter()
	{
		super(Machine.LiquiCrafter);
		for(int i = 0; i < 9; i++)
		{
			_tanks[i] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);
		}
		setManageFluids(true);
		setManageSolids(true);
	}
	
	@Override
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return slot > 9;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiLiquiCrafter(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerLiquiCrafter getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerLiquiCrafter(this, inventoryPlayer);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		boolean redstoneState = Util.isRedstonePowered(this);
		if(redstoneState && !_lastRedstoneState)
		{
			if(!worldObj.isRemote &&
					_resourcesChangedSinceLastFailedCraft &&
					_inventory[9] != null &&
					(_inventory[10] == null ||
					(_inventory[10].stackSize + _inventory[9].stackSize <= _inventory[9].getMaxStackSize() &&
					_inventory[9].itemID == _inventory[10].itemID &&
					_inventory[9].getItemDamage() == _inventory[10].getItemDamage())))
			{
				checkResources();
			}
		}
		
		_lastRedstoneState = redstoneState;
	}
	
	private void checkResources()
	{
		List<ItemResourceTracker> requiredItems = new LinkedList<ItemResourceTracker>();
		
inv:	for(int i = 0; i < 9; i++)
		{
			if(_inventory[i] != null)
			{
				liquid: if(FluidContainerRegistry.isFilledContainer(_inventory[i]))
				{
					FluidStack l = FluidContainerRegistry.getFluidForFilledItem(_inventory[i]);
					if (l == null)
						break liquid;
					for(ItemResourceTracker t : requiredItems)
					{
						if(t.isFluid && t.fluid.isFluidEqual(l))
						{
							t.required += l.amount;
							continue inv;
						}
					}
					requiredItems.add(new ItemResourceTracker(l, l.amount));
					continue inv;
				}

				for(ItemResourceTracker t : requiredItems)
				{
					if(t.id == _inventory[i].itemID && t.meta == _inventory[i].getItemDamage())
					{
						t.required++;
						continue inv;
					}
				}
				requiredItems.add(new ItemResourceTracker(_inventory[i].itemID, _inventory[i].getItemDamage(), _inventory[i].getTagCompound(), 1));
			}
		}
		
		for(int i = 11; i < 29; i++)
		{
			ItemStack item = _inventory[i];
			if(item != null)
			{
				for(ItemResourceTracker t : requiredItems)
				{
					if(t.id == item.itemID &&
							(t.meta == item.getItemDamage() || item.getItem().isDamageable()))
					{
						if(!item.getItem().hasContainerItem())
						{
							t.found += item.stackSize;
						}
						else
						{
							t.found += 1;
						}
						break;
					}
				}
			}
		}
		for(int i = 0; i < _tanks.length; i++)
		{
			FluidStack l = _tanks[i].getFluid();
			if(l == null || l.amount == 0)
			{
				continue;
			}
			for(ItemResourceTracker t : requiredItems)
			{
				if(t.isFluid && l.isFluidEqual(t.fluid))
				{
					t.found += l.amount;
					break;
				}
			}
		}
		
		for(ItemResourceTracker t : requiredItems)
		{
			if(t.found < t.required)
			{
				_resourcesChangedSinceLastFailedCraft = false;
				return;
			}
		}
		
		for(int i = 11; i < 29; i++)
		{
			if(_inventory[i] != null)
			{
				for(ItemResourceTracker t : requiredItems)
				{
					if(t.id == _inventory[i].itemID && (t.meta == _inventory[i].getItemDamage() || _inventory[i].getItem().isDamageable()))
					{
						int use;
						if(_inventory[i].getItem().hasContainerItem())
						{
							use = 1;
							ItemStack container = _inventory[i].getItem().getContainerItemStack(_inventory[i]);
							if(container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage())
							{
								_inventory[i] = null;
							}
							else
							{
								_inventory[i] = container;
							}
						}
						else
						{
							use = Math.min(t.required, _inventory[i].stackSize);
							_inventory[i].stackSize -= use;
						}
						t.required -= use;
						
						if(_inventory[i] != null && _inventory[i].stackSize == 0)
						{
							_inventory[i] = null;
						}
						
						if(t.required == 0)
						{
							requiredItems.remove(t);
						}
						break;
					}
				}
			}
		}
		for(int i = 0; i < _tanks.length; i++)
		{
			FluidStack l = _tanks[i].getFluid();
			if(l == null || l.amount == 0)
			{
				continue;
			}
			for(ItemResourceTracker t : requiredItems)
			{
				if(l.isFluidEqual(t.fluid))
				{
					int use = Math.min(t.required, l.amount);
					_tanks[i].drain(use, true);
					t.required -= use;
					
					if(t.required == 0)
					{
						requiredItems.remove(t);
					}
					break;
				}
			}
		}
		
		if(_inventory[10] == null)
		{
			_inventory[10] = _inventory[9].copy();
			_inventory[10].stackSize = _inventory[9].stackSize;
		}
		else
		{
			_inventory[10].stackSize += _inventory[9].stackSize;
		}
	}
	
	private void calculateOutput()
	{
		_inventory[9] = findMatchingRecipe();
	}
	
	@Override
	public int getSizeInventory()
	{
		return 29;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		_inventory[slot] = stack;
		if(slot < 9) calculateOutput();
		onFactoryInventoryChanged();
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int size)
	{
		ItemStack result = super.decrStackSize(slot, size);
		if(slot < 9) calculateOutput();
		onFactoryInventoryChanged();
		return result;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return player.getDistanceSq(xCoord, yCoord, zCoord) <= 64D;
	}
	
	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 10;
	}
	
	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 19;
		//if(side == ForgeDirection.UP || side == ForgeDirection.DOWN) return 1;
		//return 18;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if(slot > 10) return true;
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		if(slot == 10) return true;
		return false;
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_resourcesChangedSinceLastFailedCraft = true;
		super.onFactoryInventoryChanged();
	}
	
	@Override
	public boolean allowBucketFill()
	{
		return false;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		int quantity;
		int match = findFirstMatchingTank(resource);
		if(match >= 0)
		{
			quantity = _tanks[match].fill(resource, doFill);
			if(quantity > 0) _resourcesChangedSinceLastFailedCraft = true;
			return quantity;
		}
		match = findFirstEmptyTank();
		if(match >= 0)
		{
			quantity = _tanks[match].fill(resource, doFill);
			if(quantity > 0) _resourcesChangedSinceLastFailedCraft = true;
			return quantity;
		}
		return 0;
	}
	
	@Override
	public boolean allowBucketDrain()
	{
		return false;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		int match = findFirstNonEmptyTank();
		if(match >= 0) return _tanks[match].drain(maxDrain, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		int match = findFirstMatchingTank(resource);
		if(match >= 0) return _tanks[match].drain(resource.amount, doDrain);
		return null;
	}
	
	@Override
	public IFluidTank getTank(ForgeDirection direction, FluidStack type)
	{
		int match = findFirstMatchingTank(type);
		if(match >= 0) return _tanks[match];
		match = findFirstEmptyTank();
		if(match >= 0) return _tanks[match];
		return null;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		FluidTankInfo[] r = new FluidTankInfo[_tanks.length];
		for (int i = _tanks.length; i --> 0; )
			r[i] = _tanks[i].getInfo();
		return r;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IFluidTank[] getTanks()
	{
		return _tanks;
	}
	
	private int findFirstEmptyTank()
	{
		for(int i = 0; i < 9; i++)
		{
			if(_tanks[i].getFluid() == null || _tanks[i].getFluid().amount == 0)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private int findFirstNonEmptyTank()
	{
		for(int i = 0; i < 9; i++)
		{
			if(_tanks[i].getFluid() != null && _tanks[i].getFluid().amount > 0)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private int findFirstMatchingTank(FluidStack liquid)
	{
		if(liquid == null)
		{
			return -1;
		}
		
		for(int i = 0; i < 9; i++)
		{
			if(liquid.isFluidEqual(_tanks[i].getFluid()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private ItemStack findMatchingRecipe()
	{
		InventoryCrafting craft = new RemoteInventoryCrafting();
		for(int i = 0; i < 9; i++)
		{
			craft.setInventorySlotContents(i, (_inventory[i] == null ? null : _inventory[i].copy()));
		}
		
		return CraftingManager.getInstance().findMatchingRecipe(craft, worldObj);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		
		NBTTagList nbttaglist = nbttagcompound.getTagList("Tanks");
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Tank") & 0xff;
			if(j >= 0 && j < _tanks.length)
			{
				FluidStack l = FluidStack.loadFluidStackFromNBT(nbttagcompound1);
				if(l != null)
				{
					_tanks[j].setFluid(l);
				}
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		
		NBTTagList tanks = new NBTTagList();
		for(int i = 0; i < _tanks.length; i++)
		{
			if(_tanks[i].getFluid() != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Tank", (byte)i);
				
				FluidStack l = _tanks[i].getFluid();
				l.writeToNBT(nbttagcompound1);
				tanks.appendTag(nbttagcompound1);
			}
		}
		
		nbttagcompound.setTag("Tanks", tanks);
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
	
	private static class ItemResourceTracker
	{
		public ItemResourceTracker(int id, int meta, NBTTagCompound nbtdata, int required)
		{
			this.id = id;
			this.meta = meta;
			this.required = required;
			this.tag = nbtdata;
			isFluid = false;
		}
		public ItemResourceTracker(FluidStack resource, int required)
		{
			this.fluid = resource;
			this.required = required;
			isFluid = true;
		}
		public boolean isFluid;
		public FluidStack fluid;
		public int id;
		public int meta;
		@SuppressWarnings("unused")
		public NBTTagCompound tag; // TODO: check NBT data appropriately. via recipe?
		public int required;
		public int found;
	}
}
