package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemNeedlegunAmmoStandard extends ItemNeedlegunAmmo
{
	protected int damage;
	protected float spread;

	public ItemNeedlegunAmmoStandard(int damage, float spread, int shots)
	{
		this.damage = damage;
		this.spread = spread;
		setMaxDamage(shots - 1);
	}

	public ItemNeedlegunAmmoStandard(int damage, float spread)
	{
		this(damage, spread, 12);
	}

	public ItemNeedlegunAmmoStandard(int damage, int shots)
	{
		this(damage, 2f, shots);
	}

	public ItemNeedlegunAmmoStandard(int damage)
	{
		this(damage, 2f);
	}

	public ItemNeedlegunAmmoStandard()
	{
		this(8);
	}

	@Override
	public boolean onHitEntity(ItemStack stack, EntityPlayer owner, Entity hit, double distance)
	{
		hit.attackEntityFrom(DamageSource.causePlayerDamage(owner), damage);
		return true;
	}

	@Override
	public void onHitBlock(ItemStack stack, EntityPlayer owner, World world, int x, int y, int z, int side, double distance)
	{
	}

	@Override
	public float getSpread(ItemStack stack)
	{
		return spread;
	}
}
