package powercrystals.minefactoryreloaded.gui.slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInvisible extends Slot {
	protected final int _slot;

	public SlotInvisible(IInventory inv, int index, int x, int y, int slot)
	{
		super(inv, index, x, y);
		_slot = slot;
	}

	@Override
	public void putStack(ItemStack stack)
	{
		this.inventory.setInventorySlotContents(_slot, stack);
		this.onSlotChanged();
	}

	@Override
	public ItemStack getStack()
	{
		return null;
	}

	@Override
	public ItemStack decrStackSize(int par1)
	{
		return null;
	}

	@Override
	public boolean canTakeStack(EntityPlayer p)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_111238_b()
	{
		return false;
	}
}
