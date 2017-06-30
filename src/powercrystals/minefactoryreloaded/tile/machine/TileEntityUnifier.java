package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.OreDictionaryArbiter;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUnifier;
import powercrystals.minefactoryreloaded.gui.container.ContainerUnifier;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityUnifier extends TileEntityFactoryInventory {

	private boolean ignoreChange = false;
	private static FluidStack _biofuel;
	private static FluidStack _ethanol;
	private int _roundingCompensation;

	private Map<String, ItemStack> _preferredOutputs = new HashMap<String, ItemStack>();

	public TileEntityUnifier() {

		super(Machine.Unifier);
		_roundingCompensation = 1;
		setManageSolids(true);
	}

	public static void updateUnifierLiquids() {

		_biofuel = FluidRegistry.getFluidStack("biofuel", 1);
		_ethanol = FluidRegistry.getFluidStack("bio.ethanol", 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiUnifier(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerUnifier getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerUnifier(this, inventoryPlayer);
	}

	@Override
	public void update() {
		//TODO again this TE isn't supposed to be tickable so needs a non tickable base to inherit from
	}

	private void unifyInventory() {

		if (worldObj != null && !worldObj.isRemote) {
			ItemStack output;
			if (_inventory[0] != null) {
				List<String> names = OreDictionaryArbiter.getAllOreNames(_inventory[0]);
				// tracker does *not* also check the wildcard meta,
				// avoiding issues with saplings and logs, etc.

				if (names == null || names.size() != 1 || MFRRegistry.getUnifierBlacklist().containsKey(names.get(0))) {
					output = _inventory[0].copy();
				} else if (_preferredOutputs.containsKey(names.get(0))) {
					output = _preferredOutputs.get(names.get(0)).copy();
					output.stackSize = _inventory[0].stackSize;
				} else {
					output = OreDictionaryArbiter.getOres(names.get(0)).get(0).copy();
					output.stackSize = _inventory[0].stackSize;
				}

				if (_inventory[0].getItem().equals(output.getItem()))
					output = _inventory[0].copy();

				moveItemStack(output);
			}
		}
	}

	private void moveItemStack(ItemStack source) {

		if (source == null) {
			return;
		}

		int amt = source.stackSize;

		if (_inventory[1] == null) {
			amt = Math.min(Math.min(getInventoryStackLimit(), source.getMaxStackSize()),
					source.stackSize);
		} else if (!UtilInventory.stacksEqual(source, _inventory[1], false)) {
			return;
		} else if (source.getTagCompound() != null || _inventory[1].getTagCompound() != null) {
			return;
		} else {
			amt = Math.min(source.stackSize,
					_inventory[1].getMaxStackSize() - _inventory[1].stackSize);
		}

		if (_inventory[1] == null) {
			_inventory[1] = source.copy();
			_inventory[1].stackSize = amt;
			_inventory[0].stackSize -= amt;
		} else {
			_inventory[1].stackSize += amt;
			_inventory[0].stackSize -= amt;
		}

		if (_inventory[0].stackSize == 0) {
			_inventory[0] = null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		_inventory[slot] = stack;
		if (slot > 1)
			updatePreferredOutput();
		if (stack != null && stack.stackSize <= 0)
			_inventory[slot] = null;
		unifyInventory();
		ignoreChange = true;
		markDirty();
		ignoreChange = false;
	}

	protected void updatePreferredOutput() {

		_preferredOutputs.clear();
		for (int i = 2; i < 11; i++) {
			if (_inventory[i] == null) {
				continue;
			}
			List<String> names = OreDictionaryArbiter.getAllOreNames(_inventory[i]);
			if (names != null) {
				for (String name : names) {
					_preferredOutputs.put(name, _inventory[i].copy());
				}
			}
		}
	}

	@Override
	protected void onFactoryInventoryChanged() {

		super.onFactoryInventoryChanged();
		if (!ignoreChange) {
			updatePreferredOutput();
			unifyInventory();
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		// TODO: save/read items
	}

	@Override
	public int getSizeInventory() {

		return 11;
	}

	@Override
	public boolean shouldDropSlotWhenBroken(int slot) {

		return slot < 2;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public int getSizeInventorySide(EnumFacing side) {

		return 2;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {

		return slot == 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {

		return slot == 1;
	}

	private FluidStack unifierTransformLiquid(FluidStack resource, boolean doFill) {

		if (_ethanol != null & _biofuel != null) {
			if (_ethanol.isFluidEqual(resource))
				return new FluidStack(_biofuel, resource.amount);
			else if (_biofuel.isFluidEqual(resource))
				return new FluidStack(_ethanol, resource.amount);
		}
		return null;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, ItemStack stack) {

		return true;
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, ItemStack stack) {

		return true;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		if (resource == null || resource.amount == 0)
			return 0;

		FluidStack converted = unifierTransformLiquid(resource, doFill);

		if (converted == null || converted.amount == 0)
			return 0;

		int filled = _tanks[0].fill(converted, doFill);

		if (filled == converted.amount) {
			return resource.amount;
		} else {
			return filled * resource.amount / converted.amount +
					(resource.amount & _roundingCompensation);
		}
	}

}
