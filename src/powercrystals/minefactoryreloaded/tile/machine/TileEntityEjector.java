package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraft.api.transport.IPipeTile.PipeType;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

import powercrystals.core.inventory.IInventoryManager;
import powercrystals.core.inventory.InventoryManager;
import powercrystals.core.util.Util;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiEjector;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerEjector;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class TileEntityEjector extends TileEntityFactoryInventory
{
	protected boolean _lastRedstoneState;
	protected boolean _whitelist = false;
	protected boolean _matchNBT = true;
	protected boolean _ignoreDamage = true;
	
	protected boolean _hasItems = false;
	
	public TileEntityEjector()
	{
		super(Machine.Ejector);
		setManageSolids(true);
		setCanRotate(true);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(worldObj.isRemote)
		{
			return;
		}
		boolean redstoneState = Util.isRedstonePowered(this);
		if (redstoneState & !_lastRedstoneState & (!_whitelist | (_whitelist == _hasItems)))
		{
			Map<ForgeDirection, IInventory> chests = UtilInventory.
					findChests(worldObj, xCoord, yCoord, zCoord);
			inv: for (Entry<ForgeDirection, IInventory> chest : chests.entrySet())
			{
				if(chest.getKey() == getDirectionFacing())
				{
					continue;
				}
				
				IInventoryManager inventory = InventoryManager.create(chest.getValue(),
						chest.getKey().getOpposite());
				Map<Integer, ItemStack> contents = inventory.getContents();
				
				set: for (Entry<Integer, ItemStack> stack : contents.entrySet())
				{
					if (stack == null || stack.getValue() == null)
					{
						continue;
					}
					ItemStack itemstack = stack.getValue();
					
					if (chest.getValue() instanceof ISidedInventory)
					{ // TODO: expose canRemoveItem in IInventoryManager
						ISidedInventory sided = (ISidedInventory)chest.getValue();
						if(!sided.canExtractItem(stack.getKey(), itemstack,
								chest.getKey().getOpposite().ordinal()))
						{
							continue;
						}
					}
					
					boolean hasMatch = false;
					
					for (int i = getSizeItemList(); i --> 0; )
						if (itemMatches(_inventory[i], itemstack))
							hasMatch = true;
					
					if (_whitelist != hasMatch) continue set;
					
					ItemStack stackToDrop = itemstack.copy();
					stackToDrop.stackSize = 1;
					ItemStack remaining = UtilInventory.dropStack(this, stackToDrop,
							this.getDirectionFacing(), this.getDirectionFacing());
					
					// remaining == null if dropped successfully.
					if (remaining == null)
					{
						inventory.removeItem(1, stackToDrop);
						break inv;
					}
				}
			}
		}
		_lastRedstoneState = redstoneState;
	}
	
	protected boolean itemMatches(ItemStack itemA, ItemStack itemB)
	{
		if (itemA == null | itemB == null)
			return false;
		
		if (itemA.itemID != itemB.itemID)
			return false;
		
		if (!_ignoreDamage)
			if (!itemA.isItemEqual(itemB))
				return false;
		
		if (_matchNBT)
		{
			if(itemA.getTagCompound() == null && itemB.getTagCompound() == null) return true;
			if(itemA.getTagCompound() == null || itemB.getTagCompound() == null) return false;
			return itemA.getTagCompound().equals(itemB.getTagCompound());
		}
		
		return true;
	}
	
	@Override
	public void onFactoryInventoryChanged()
	{
		for (int i = getSizeItemList(); i --> 0; )
			if (_inventory[i] != null)
			{
				_hasItems = true;
				return;
			}
	}

	public int getSizeItemList()
	{
		return 9;
	}

	@Override
	public int getSizeInventory()
	{
		return getSizeItemList();
	}
	
	@Override
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return false;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiEjector(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFactoryInventory getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerEjector(this, inventoryPlayer);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		_lastRedstoneState = tag.getBoolean("redstone");
		_whitelist = tag.getBoolean("whitelist");
		_matchNBT = !tag.hasKey("matchNBT") || tag.getBoolean("matchNBT");
		_ignoreDamage = tag.getBoolean("ignoreDamage");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("redstone", _lastRedstoneState);
		tag.setBoolean("whitelist", _whitelist);
		tag.setBoolean("matchNBT", _matchNBT);
		tag.setBoolean("ignoreDamage", _ignoreDamage);
	}
	
	public boolean getIsWhitelist() { return _whitelist; }
	
	public boolean getIsNBTMatch() { return _matchNBT; }
	
	public boolean getIsIDMatch() { return _ignoreDamage; }
	
	public void setIsWhitelist(boolean whitelist) { _whitelist = whitelist; }
	
	public void setIsNBTMatch(boolean matchNBT) { _matchNBT = matchNBT; }
	
	public void setIsIDMatch(boolean idMatch) { _ignoreDamage = idMatch; }

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (type == PipeType.STRUCTURE)
			return ConnectOverride.CONNECT;
		if (with == getDirectionFacing())
			return super.overridePipeConnection(type, with);
		return ConnectOverride.DISCONNECT;
	}

	@Override
	public boolean canConduitConnect(ForgeDirection side)
	{
		if (side == getDirectionFacing())
			return super.canConduitConnect(side);
		return false;
	}
}
