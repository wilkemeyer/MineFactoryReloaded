package skyboy.core.container;


import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public final class BehaviourDispenseCarbonContainer extends BehaviorDefaultDispenseItem {

	@Override
	public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		CarbonContainer item = (CarbonContainer)stack.getItem();
		World world = source.getWorld();
		EnumFacing enumfacing = BlockDispenser.func_149937_b(source.getBlockMetadata());
		int x = source.getXInt() + enumfacing.getFrontOffsetX(),
				y = source.getYInt() + enumfacing.getFrontOffsetY(),
				z = source.getZInt() + enumfacing.getFrontOffsetZ();
		Block id = world.getBlock(x, y, z);
		ItemStack r = item.tryPlaceContainedLiquid(world, stack, x, y, z, enumfacing.ordinal());

		if (id != world.getBlock(x, y, z)) {
			if (--stack.stackSize > 0) {
				IInventory inv = (IInventory)source.getBlockTileEntity();
				if (inv != null && r != null) {
					int i = inv.getSizeInventory(), slot = -1, pSlot = -1,
							l = Math.min(inv.getInventoryStackLimit(), r.getMaxStackSize());
					ItemStack t;
					while (i --> 0) {
						t = inv.getStackInSlot(i);
						if (t == null)
							slot = i;
						else if (r.isItemEqual(t) &&
								t.stackSize < l &&
								ItemStack.areItemStackTagsEqual(t, stack))
							pSlot = i;
					}
					if (pSlot >= 0)
						slot = pSlot;
					if (slot >= 0) {
						t = inv.getStackInSlot(slot);
						if (t != null) t.stackSize += r.stackSize;
						else t = r;
						inv.setInventorySlotContents(slot, t);
						return stack;
					}
				}
				if (r != null) super.dispenseStack(source, r);
				return stack;
			}
			return r;
		}
		return super.dispenseStack(source, stack);
	}
}
