package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemNeedlegunAmmoStandard extends ItemNeedlegunAmmo
{
	public ItemNeedlegunAmmoStandard()
	{
		setMaxDamage(11);
		setHasSubtypes(false);
	}

	@Override
	public boolean onHitEntity(ItemStack stack, EntityPlayer owner, Entity hit, double distance)
	{
		hit.attackEntityFrom(DamageSource.causePlayerDamage(owner), 8);
		return true;
	}

	@Override
	public void onHitBlock(ItemStack stack, EntityPlayer owner, World world, int x, int y, int z, int side, double distance)
	{
	}

	@Override
	public float getSpread(ItemStack stack)
	{
		return 2.0F;
	}
}
