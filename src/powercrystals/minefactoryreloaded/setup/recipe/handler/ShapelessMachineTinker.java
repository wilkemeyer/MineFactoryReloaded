package powercrystals.minefactoryreloaded.setup.recipe.handler;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class ShapelessMachineTinker extends ShapelessRecipes
{
	protected List<List<ItemStack>> _tinkerItems;
	protected ItemStack _machine;

	public ShapelessMachineTinker(Machine machine, String... tinkerItems)
	{
		super(null, null);
		_machine = machine.getItemStack();
		_tinkerItems = new LinkedList<List<ItemStack>>();
		for (String s : tinkerItems)
			_tinkerItems.add(OreDictionary.getOres(s));
	}

	public ShapelessMachineTinker(Machine machine, ItemStack... tinkerItems)
	{
		super(null, null);
		_machine = machine.getItemStack();
		_tinkerItems = new LinkedList<List<ItemStack>>();
		for (ItemStack s : tinkerItems)
		{
			List<ItemStack> l = new LinkedList<ItemStack>();
			l.add(s);
			_tinkerItems.add(l);
		}
	}
	
	protected abstract boolean isMachineTinkerable(ItemStack machine);
	
	protected abstract ItemStack getTinkeredMachine(ItemStack machine);

	@Override
	public boolean matches(InventoryCrafting grid, World world)
	{
		int size = grid.getSizeInventory();
		boolean foundMachine = false;
		
		List<List<ItemStack>> items = new LinkedList<List<ItemStack>>();
		items.addAll(_tinkerItems);
		
		while (size --> 0)
		{
			ItemStack gridItem = grid.getStackInSlot(size);
			if (gridItem == null)
				continue;
			
			if (UtilInventory.stacksEqual(_machine, gridItem, false))
				if (foundMachine || !isMachineTinkerable(gridItem))
					return false;
				else
					foundMachine = true;
			else
			lists: {
				if (foundMachine && items.isEmpty())
					return true;
				for (List<ItemStack> l : items)
					for (ItemStack i : l)
						if (UtilInventory.stacksEqual(gridItem, i))
						{
							items.remove(l);
							break lists;
						}
				return false;
			}
		}

		return foundMachine && items.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid)
	{
		int size = grid.getSizeInventory();
		
		while (size --> 0)
		{
			ItemStack gridItem = grid.getStackInSlot(size);
			if (UtilInventory.stacksEqual(_machine, gridItem, false))
				if (isMachineTinkerable(gridItem))
					return getTinkeredMachine(gridItem);
		}
		
		return null;
	}
}
