package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface IUseHandler {
	public boolean canUse(ItemStack item, EntityLivingBase entity, EnumHand hand);
	public ItemStack onTryUse(ItemStack item, World world, EntityLivingBase entity, EnumHand hand);
	public int getMaxUseDuration(ItemStack item);
	public boolean isUsable(ItemStack item);
	public EnumAction useAction(ItemStack item);
	public ItemStack onUse(ItemStack item, EntityLivingBase entity, EnumHand hand);
}
