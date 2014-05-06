package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PamFruitCinnamon extends PamFruit
{
	private Item _cinnamonItemId;

	public PamFruitCinnamon(Block sourceId, Item itemId)
	{
		super(sourceId);
		_cinnamonItemId = itemId;
	}

	@Override
	public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z)
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(_cinnamonItemId, 1, 0));
		return drops;
	}
}
