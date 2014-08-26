package powercrystals.minefactoryreloaded.tile.machine;

import cofh.core.util.fluid.FluidTankAdv;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoAnvil;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoAnvil;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityAutoAnvil extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private float maximumCost;
	private int stackSizeToBeUsedInRepair;
	private boolean repairOnly;

	private ItemStack _output;

	public TileEntityAutoAnvil()
	{
		super(Machine.AutoAnvil);
		setManageSolids(true);
		_tanks[0].setLock(FluidRegistry.getFluid("mobessence"));
	}

	@Override
	public int getSizeInventory()
	{
		return 3;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if (stack == null) return false;
		if (repairOnly)
		{
			if (slot == 0) return stack.getItem().isRepairable();
			if (slot == 1 && _inventory[0] != null && stack.getItem().isRepairable()) 
				return _inventory[0].getItem().equals(stack);
			return false;
		}
		if (slot == 0) return stack.isItemStackDamageable() || stack.getItem().equals(Items.enchanted_book);
		if (slot == 1 && _inventory[0] != null)
		{
			if (stack.getItem().equals(Items.enchanted_book) &&
					Items.enchanted_book.func_92110_g(stack).tagCount() > 0)
				return true;
			return 	(stack.getItem().equals(_inventory[0].getItem()) &&
					stack.isItemStackDamageable() && stack.getItem().isRepairable()) ||
					_inventory[0].getItem().getIsRepairable(_inventory[0], stack);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		if(slot == 2) return true;
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiAutoAnvil(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerAutoAnvil(this, inventoryPlayer);
	}

	@Override
	protected boolean activateMachine()
	{
		if(_output == null || _inventory[2] != null)
		{
			return false;
		}
		else
		{
			if (repairOnly)
			{
				if (_inventory[0] != null && _inventory[1] != null &&
						_inventory[0].getItem().equals(_inventory[1].getItem()) &&
						_inventory[0].getItem().isRepairable())
				{
					setWorkDone(getWorkDone() + 1);

					if(getWorkDone() >= getWorkMax())
					{
						_inventory[0] = null;
						_inventory[1] = null;
						_inventory[2] = _output;

						setWorkDone(0);
						_output = null;
					}
					return true;
				}

				return false;
			}

			if(drain(_tanks[0], 4, false) != 4)
			{
				return false;
			}
			if(stackSizeToBeUsedInRepair > 0 && (_inventory[1] == null || _inventory[1].stackSize < stackSizeToBeUsedInRepair))
			{
				return false;
			}

			drain(_tanks[0], 4, true);
			setWorkDone(getWorkDone() + 1);

			if(getWorkDone() >= getWorkMax())
			{
				_inventory[0] = null;
				_inventory[2] = _output;

				if(stackSizeToBeUsedInRepair > 0 && _inventory[1].stackSize > stackSizeToBeUsedInRepair)
				{
					_inventory[1].stackSize -= stackSizeToBeUsedInRepair;
				}
				else
				{
					_inventory[1] = null;
				}

				setWorkDone(0);
				_output = null;
			}

			return true;
		}
	}

	@Override
	protected void onFactoryInventoryChanged()
	{
		super.onFactoryInventoryChanged();

		_output = getAnvilOutput();
		setWorkDone(0);
	}

	public ItemStack getRepairOutput()
	{
		return _output;
	}

	private ItemStack getAnvilOutput()
	{
		ItemStack startingItem = _inventory[0];
		this.maximumCost = 0;
		int totalEnchCost = 0;

		if (startingItem == null)
		{
			return null;
		}
		else if (repairOnly)
		{
			this.stackSizeToBeUsedInRepair = 0;
			ItemStack addedItem = _inventory[1];
			Item item = startingItem.getItem();

			if (addedItem != null && item.isRepairable() &&
					startingItem.getItem() == addedItem.getItem())
			{
				int d = item.getMaxDamage();
				int k = startingItem.getItemDamageForDisplay();
				int l = addedItem.getItemDamageForDisplay();
				int i1 = (d - k) + (d - l) + d * 5 / 100;
				int j1 = Math.max(d - i1, 0);

				this.maximumCost = ((k + l) / 2 - j1) / 100f;

				return new ItemStack(startingItem.getItem(), 1, j1);
			}
			return null;
		}
		else
		{
			ItemStack outputItem = startingItem.copy();
			ItemStack addedItem = _inventory[1];

			@SuppressWarnings("unchecked")
			Map<Integer, Integer> existingEnchantments = EnchantmentHelper.getEnchantments(outputItem);

			boolean enchantingWithBook = false;
			int repairCost = outputItem.getRepairCost() + (addedItem == null ? 0 : addedItem.getRepairCost());
			this.stackSizeToBeUsedInRepair = 0;

			if (addedItem != null)
			{
				{ // anvil event (canceled: don't anvil; output != null: item & cost calculated by listener)
					AnvilUpdateEvent e = new AnvilUpdateEvent(outputItem.copy(), addedItem.copy(), "", repairCost);
					if (MinecraftForge.EVENT_BUS.post(e)) return null;
					if (e.output != null)
					{
						maximumCost = e.cost;
						return e.output;
					}
				}
				enchantingWithBook = addedItem.getItem().equals(Items.enchanted_book) &&
						Items.enchanted_book.func_92110_g(addedItem).tagCount() > 0;

						if (outputItem.isItemStackDamageable() &&
								outputItem.getItem().getIsRepairable(outputItem, addedItem))
						{
							int currentDamage = Math.min(outputItem.getItemDamageForDisplay(), outputItem.getMaxDamage() / 4);

							if(currentDamage <= 0)
							{
								return null;
							}

							int repairStackSize = 0;
							for (; currentDamage > 0 && repairStackSize < addedItem.stackSize; repairStackSize++)
							{
								outputItem.setItemDamage(outputItem.getItemDamageForDisplay() - currentDamage);
								totalEnchCost += Math.max(1, currentDamage / 100) + existingEnchantments.size();
								currentDamage = Math.min(outputItem.getItemDamageForDisplay(), outputItem.getMaxDamage() / 4);
							}

							this.stackSizeToBeUsedInRepair = repairStackSize;
						}
						else
						{
							if (!enchantingWithBook && (!outputItem.getItem().equals(addedItem.getItem()) ||
									!outputItem.isItemStackDamageable()))
							{
								return null;
							}

							if (outputItem.isItemStackDamageable() && !enchantingWithBook)
							{
								int currentDamage = outputItem.getMaxDamage() - outputItem.getItemDamageForDisplay();
								int addedItemDamage = addedItem.getMaxDamage() - addedItem.getItemDamageForDisplay();
								int newDamage = addedItemDamage + outputItem.getMaxDamage() * 12 / 100;
								int leftoverDamage = currentDamage + newDamage;
								int repairedDamage = outputItem.getMaxDamage() - leftoverDamage;

								if (repairedDamage < 0)
								{
									repairedDamage = 0;
								}

								if (repairedDamage < outputItem.getItemDamage())
								{
									outputItem.setItemDamage(repairedDamage);
									totalEnchCost += Math.max(1, newDamage / 100);
								}
							}

							@SuppressWarnings("unchecked")
							Map<Integer, Integer> addedEnchantments = EnchantmentHelper.getEnchantments(addedItem);

							for (Integer addedEnchId : addedEnchantments.keySet())
							{
								Enchantment enchantment = Enchantment.enchantmentsList[addedEnchId];
								if (enchantment == null)
									continue;
								int existingEnchLevel = existingEnchantments.containsKey(addedEnchId) ? existingEnchantments.get(addedEnchId) : 0;
								int addedEnchLevel = addedEnchantments.get(addedEnchId);
								int newEnchLevel;

								if (existingEnchLevel == addedEnchLevel)
								{
									++addedEnchLevel;
									newEnchLevel = addedEnchLevel;
								}
								else
								{
									newEnchLevel = Math.max(addedEnchLevel, existingEnchLevel);
								}

								addedEnchLevel = newEnchLevel;
								int levelDifference = addedEnchLevel - existingEnchLevel;
								boolean canEnchantmentBeAdded = enchantment.canApply(outputItem);

								if (outputItem.getItem().equals(Items.enchanted_book))
								{
									canEnchantmentBeAdded = true;
								}

								for (Integer existingEnchId : existingEnchantments.keySet())
								{
									if (existingEnchId != addedEnchId && !enchantment.canApplyTogether(Enchantment.enchantmentsList[existingEnchId]))
									{
										canEnchantmentBeAdded = false;
										totalEnchCost += levelDifference;
									}
								}

								if (canEnchantmentBeAdded)
								{
									if (newEnchLevel > enchantment.getMaxLevel())
									{
										newEnchLevel = enchantment.getMaxLevel();
									}

									existingEnchantments.put(Integer.valueOf(addedEnchId), Integer.valueOf(newEnchLevel));
									int enchCost = 0;

									switch (enchantment.getWeight())
									{
									case 1:
										enchCost = 8;
										break;
									case 2:
										enchCost = 4;
									case 3:
									case 4:
									case 6:
									case 7:
									case 8:
									case 9:
									default:
										break;
									case 5:
										enchCost = 2;
										break;
									case 10:
										enchCost = 1;
									}

									if (enchantingWithBook)
									{
										enchCost = Math.max(1, enchCost / 2);
									}

									totalEnchCost += enchCost * levelDifference;
								}
							}
						}
			}

			int enchCount = 0;

			for (Integer existingEnchId : existingEnchantments.keySet())
			{
				Enchantment enchantment = Enchantment.enchantmentsList[existingEnchId];
				int existingEnchLevel = existingEnchantments.get(existingEnchId);
				int enchCost = 0;
				++enchCount;

				switch (enchantment.getWeight())
				{
				case 1:
					enchCost = 8;
					break;
				case 2:
					enchCost = 4;
				case 3:
				case 4:
				case 6:
				case 7:
				case 8:
				case 9:
				default:
					break;
				case 5:
					enchCost = 2;
					break;
				case 10:
					enchCost = 1;
				}

				if (enchantingWithBook)
				{
					enchCost = Math.max(1, enchCost / 2);
				}
				repairCost += enchCount + existingEnchLevel * enchCost;
			}

			if (enchantingWithBook)
			{
				repairCost = Math.max(1, repairCost / 2);
			}

			if (enchantingWithBook && !outputItem.getItem().isBookEnchantable(outputItem, addedItem))
			{
				outputItem = null;
			}

			this.maximumCost = repairCost + totalEnchCost;

			if (totalEnchCost <= 0)
			{
				outputItem = null;
			}

			if (outputItem != null)
			{
				EnchantmentHelper.setEnchantments(existingEnchantments, outputItem);
			}

			return outputItem;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("repairOnly", repairOnly);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		repairOnly = tag.getBoolean("repairOnly");
	}

	public boolean getRepairOnly()
	{
		return repairOnly;
	}

	public void setRepairOnly(boolean v)
	{
		repairOnly = v;
		onFactoryInventoryChanged();
	}

	@Override
	public int getWorkMax()
	{
		return (int)(100f * maximumCost);
	}

	@Override
	public int getIdleTicksMax()
	{
		return 1;
	}

	@Override
	public boolean allowBucketFill(ItemStack stack)
	{
		return true;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack type, boolean doFill)
	{
		return fill(type, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return null;
	}

	@Override
	protected FluidTankAdv[] createTanks()
	{
		return new FluidTankAdv[]{new FluidTankAdv(4 * BUCKET_VOLUME)};
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}
}
