package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ItemBlockVineScaffold extends ItemBlock
{
	public ItemBlockVineScaffold(int id)
	{
		super(id);
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
	{
		if (world.isRemote && !player.isSneaking() && 
				world.getBlockId(x, y, z) == MineFactoryReloadedCore.vineScaffoldBlock.blockID)
		{
			if (MineFactoryReloadedCore.vineScaffoldBlock.onBlockActivated(world, x, y, z,
						player, side, xOffset, yOffset, zOffset))
				player.swingItem();
		}
		return false;
	}
	
    @Override
	@SideOnly(Side.CLIENT)
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side,
    		EntityPlayer player, ItemStack stack)
    {
    	return (player.isSneaking() ||
    			world.getBlockId(x, y, z) != MineFactoryReloadedCore.vineScaffoldBlock.blockID) &&
    			super.canPlaceItemBlockOnSide(world, x, y, z, side, player, stack);
    }
}
