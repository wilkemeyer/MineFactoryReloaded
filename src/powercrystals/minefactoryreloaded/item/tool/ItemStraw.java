package powercrystals.minefactoryreloaded.item.tool;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;

public class ItemStraw extends ItemFactoryTool {

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
			if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
				Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
				Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
				if (fluid != null && MFRRegistry.getLiquidDrinkHandlers().containsKey(fluid.getName())) {
					MFRRegistry.getLiquidDrinkHandlers().get(fluid.getName()).onDrink(player);
					world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
				}
			}
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.drink;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
			Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
			if (fluid != null && MFRRegistry.getLiquidDrinkHandlers().containsKey(fluid.getName())) {
				player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
			}
		}
		return stack;
	}

}
