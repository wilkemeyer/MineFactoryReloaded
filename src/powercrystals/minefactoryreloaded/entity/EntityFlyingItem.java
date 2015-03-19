package powercrystals.minefactoryreloaded.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityFlyingItem extends EntitySafariNet {

	public int canBePickedUp = 0;
	protected float damage = 2.0f;
	protected int knockbackStrength;

	public EntityFlyingItem(World world) {

		super(world);
	}

	public EntityFlyingItem(World world, double x, double y, double z, ItemStack stack) {

		super(world, x, y, z, stack);
	}

	public EntityFlyingItem(World world, EntityLivingBase owner, ItemStack stack) {

		super(world, owner, stack);
	}

	public void setDamage(float damage) {

		this.damage = damage;
	}

	public float getDamage() {

		return this.damage;
	}

	public void setKnockbackStrength(int p_70240_1_) {

		this.knockbackStrength = p_70240_1_;
	}

	@Override
	public boolean canAttackWithItem() {

		return false;
	}

	@Override
	protected boolean onHitBlock(ItemStack storedEntity, MovingObjectPosition mop) {

		if (canBePickedUp == 0 && worldObj.rand.nextInt(7) == 0) {
			dropAsStack(this.getStoredEntity());
			return false;
		}
		return true;
	}

	@Override
	protected boolean onHitEntity(ItemStack storedEntity, MovingObjectPosition mop) {

		DamageSource d = DamageSource.causeThrownDamage(this, getThrower() == null ? this : getThrower());
		if (mop.entityHit.attackEntityFrom(d, damage)) {
			if (this.knockbackStrength > 0) {
				float f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

				if (f4 > 0.0F) {
					mop.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6D /
							f4, 0.1D, this.motionZ * this.knockbackStrength * 0.6D / f4);
				}
			}
		}
		return true;
	}

	@Override
	protected void impact() {

		for (int j = 0; j < 8; ++j) {
			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}
	}

}
