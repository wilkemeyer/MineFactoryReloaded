package powercrystals.minefactoryreloaded.item.gun.ammo;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ItemNeedlegunAmmoFire extends ItemNeedlegunAmmoBlock {

	public ItemNeedlegunAmmoFire() {
		super(Blocks.fire, 0, 2f);
		this.damage = 6;
	}

	@Override
	public boolean onHitEntity(ItemStack stack, EntityPlayer owner, Entity hit, double distance) {
		hit.setFire(10);
		super.onHitEntity(stack, owner, hit, distance);
		return true;
	}

}
