package powercrystals.minefactoryreloaded.item.gun.ammo;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ItemNeedlegunAmmoFire extends ItemNeedlegunAmmoBlock {

	public ItemNeedlegunAmmoFire() {
		super(Blocks.FIRE.getDefaultState());
		setShots(8);
		setDamage(10);
	}

	@Override
	public boolean onHitEntity(ItemStack stack, EntityPlayer owner, Entity hit, double distance) {
		hit.setFire(10);
		super.onHitEntity(stack, owner, hit, distance);
		return true;
	}

}
