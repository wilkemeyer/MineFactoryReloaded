package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.util.ResourceLocation;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class SlotAcceptLaserFocus extends Slot {

	public static ResourceLocation background;

	public SlotAcceptLaserFocus(IInventory inv, int index, int x, int y) {

		super(inv, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return stack != null && stack.getItem().equals(MFRThings.laserFocusItem);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getBackgroundLocation() {

		return background;
	}
}
