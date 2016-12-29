package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

import powercrystals.minefactoryreloaded.item.base.ItemFactoryColored;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemCeramicDye extends ItemFactoryColored {

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		Block block = world.getBlockState(pos).getBlock();
		if (!world.isRemote & block != null) {
			if (Blocks.GLASS.equals(block)) {
				if (world.setBlockState(pos, MFRThings.factoryGlassBlock.getStateFromMeta(stack.getItemDamage()), 3)) {
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
					return EnumActionResult.SUCCESS;
				}
			}
			if (Blocks.GLASS_PANE.equals(block)) {
				if (world.setBlockState(pos, MFRThings.factoryGlassPaneBlock.getStateFromMeta(stack.getItemDamage()), 3)) {
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
					return EnumActionResult.SUCCESS;
				}
			}
			if (block.recolorBlock(world, pos, side, EnumDyeColor.byMetadata(stack.getItemDamage()))) {
				if (!player.capabilities.isCreativeMode)
					stack.stackSize--;
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

}
