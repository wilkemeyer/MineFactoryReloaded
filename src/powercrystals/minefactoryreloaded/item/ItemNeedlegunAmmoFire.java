package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemNeedlegunAmmoFire extends ItemNeedlegunAmmoBlock
{
	public ItemNeedlegunAmmoFire()
	{
		super(Blocks.fire, 4);
	}

	@Override
	public boolean onHitEntity(ItemStack stack, EntityPlayer owner, Entity hit, double distance)
	{
		hit.setFire(10);
		hit.attackEntityFrom(DamageSource.causePlayerDamage(owner), 2);
		return true;
	}
	
	@Override
	public float getSpread(ItemStack stack)
	{
		return 2.0F;
	}
}
