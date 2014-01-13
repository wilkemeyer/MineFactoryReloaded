package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class ItemNeedlegunAmmoFire extends ItemNeedlegunAmmoBlock
{
	public ItemNeedlegunAmmoFire(int id)
	{
		super(id, Block.fire.blockID, 4);
	}

	@Override
	public boolean onHitEntity(EntityPlayer owner, Entity hit, double distance)
	{
		hit.attackEntityFrom(DamageSource.causePlayerDamage(owner), 2);
		hit.setFire(10);
		return true;
	}
	
	@Override
	public float getSpread()
	{
		return 2.0F;
	}
}
