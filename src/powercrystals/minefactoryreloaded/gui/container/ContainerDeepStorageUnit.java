package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotAcceptValid;
import cofh.lib.gui.slot.SlotInvisible;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityDeepStorageUnit;

public class ContainerDeepStorageUnit extends ContainerFactoryInventory {

	private TileEntityDeepStorageUnit _dsu;
	private int _tempQuantity;

	public ContainerDeepStorageUnit(TileEntityDeepStorageUnit dsu, InventoryPlayer inventoryPlayer) {

		super(dsu, inventoryPlayer);
		_dsu = dsu;
	}

	@Override
	protected void addSlots() {

		addSlotToContainer(new SlotAcceptValid(_te, 0, 134, 16));
		addSlotToContainer(new SlotAcceptValid(_te, 1, 152, 16));
		addSlotToContainer(new SlotRemoveOnly(_te, 2, 152, 49));
		for (int i = 34; i-- > 0;)
			addSlotToContainer(new SlotInvisible(_te, 3 + i, 170, 16, 0));
	}

	@Override
	protected boolean performMerge(int slot, ItemStack stackInSlot) {

		if (slot < 37) {
			return mergeItemStack(stackInSlot, 37, inventorySlots.size(), true);
		}
		if (_dsu.isItemValidForSlot(0, stackInSlot) && mergeItemStack(stackInSlot, 0, 36, false)) {
			sendSlots(0, 3);
			return true;
		}
		return false;
	}

	@Override
	protected boolean supportsShiftClick(EntityPlayer player, int slot) {

		return !player.worldObj.isRemote ? true : slot > 36;
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 200, _dsu.getQuantity());
			((ICrafting) crafters.get(i)).sendProgressBarUpdate(this, 201, _dsu.getQuantity() >> 16);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value) {

		super.updateProgressBar(var, value);

		if (var == 200) _tempQuantity = value & 65535;
		if (var == 201) _dsu.setQuantity(_tempQuantity | (value << 16));
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 124;
	}
}
