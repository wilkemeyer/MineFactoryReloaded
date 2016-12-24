package powercrystals.minefactoryreloaded.item.tool;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryTool;

public class ItemFishingRod extends ItemFactoryTool {

	public ItemFishingRod() {
		setUnlocalizedName("mfr.fishingrod");
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode)
			--par1ItemStack.stackSize;

		world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote) {
			EntityFishingRod entity = new EntityFishingRod(world, player);
			entity.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.6F, 1.0F);
			world.spawnEntityInWorld();
			
		}

		return par1ItemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}

}
