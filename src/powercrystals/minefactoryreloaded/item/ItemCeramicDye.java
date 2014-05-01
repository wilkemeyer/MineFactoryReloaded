package powercrystals.minefactoryreloaded.item;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemCeramicDye extends ItemMulti
{
	public ItemCeramicDye(int itemId)
	{
		super(itemId);
		setNames("white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black");
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if (!world.isRemote & block != null)
		{
			if (block.blockID == Block.glass.blockID)
			{
				if (world.setBlock(x, y, z,
						MineFactoryReloadedCore.factoryGlassBlock.blockID, stack.getItemDamage(), 3))
				{
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
					return true;
				}
			}
			if (block.blockID == Block.thinGlass.blockID)
			{
				if (world.setBlock(x, y, z,
						MineFactoryReloadedCore.factoryGlassPaneBlock.blockID, stack.getItemDamage(), 3))
				{
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
					return true;
				}
			}
			if (block.recolourBlock(world, x, y, z, ForgeDirection.getOrientation(side), stack.getItemDamage()))
			{
				if (!player.capabilities.isCreativeMode)
					stack.stackSize--;
				return true;
			}
		}
		return false;
	}
}
