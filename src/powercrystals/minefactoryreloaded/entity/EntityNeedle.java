package powercrystals.minefactoryreloaded.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MFRRegistry;

public class EntityNeedle extends Entity implements IProjectile
{
	private String _owner;
	private int ticksInAir = 0;
	private ItemStack _ammoSource;
	private double distance;
	private boolean _falling;

	public EntityNeedle(World world)
	{
		super(world);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
	}

	public EntityNeedle(World world, double x, double y, double z)
	{
		this(world);
		this.setPosition(x, y, z);
		this.yOffset = 0.0F;
	}

	public EntityNeedle(World world, EntityPlayer owner, ItemStack ammoSource, float spread)
	{
		this(world);
		if (owner != null)
			_owner = owner.getCommandSenderName();
		_ammoSource = ammoSource;

		this.setLocationAndAngles(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ, owner.rotationYaw, owner.rotationPitch);
		this.posX -= (MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= 0.1D;
		this.posZ -= (MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 3.25F, spread);
		this.distance = 0;
	}

	@Override
	protected void entityInit()
	{
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float speedMult, float spreadConst)
	{
		double normal = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= normal;
		y /= normal;
		z /= normal;
		x += this.rand.nextGaussian() * 0.0075D * spreadConst;
		y += this.rand.nextGaussian() * 0.0075D * spreadConst;
		z += this.rand.nextGaussian() * 0.0075D * spreadConst;
		x *= speedMult;
		y *= speedMult;
		z *= speedMult;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float horizSpeed = MathHelper.sqrt_double(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, horizSpeed) * 180.0D / Math.PI);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int unknown)
	{
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;

		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(x * x + z * z);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, f) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / Math.PI);
		}

		++this.ticksInAir;
		Vec3 pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		Vec3 nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition hit = this.worldObj.func_147447_a(pos, nextPos, false, true, false);
		pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

		if(hit != null)
		{
			nextPos = Vec3.createVectorHelper(hit.hitVec.xCoord, hit.hitVec.yCoord,	hit.hitVec.zCoord);
		}

		Entity entityHit = null;
		List<?> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,	this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double closestRange = 0.0D;
		double collisionRange = 0.3D;
		EntityPlayer owner = _owner == null ? null : this.worldObj.getPlayerEntityByName(_owner);

		for(int l = 0; l < list.size(); ++l)
		{
			Entity e = (Entity)list.get(l);

			if((e != owner | this.ticksInAir >= 2) && e.canBeCollidedWith())
			{
				AxisAlignedBB entitybb = e.boundingBox.expand(collisionRange, collisionRange, collisionRange);
				MovingObjectPosition entityHitPos = entitybb.calculateIntercept(pos, nextPos);

				if(entityHitPos != null)
				{
					double range = pos.distanceTo(entityHitPos.hitVec);

					if(range < closestRange || closestRange == 0.0D)
					{
						entityHit = e;
						closestRange = range;
					}
				}
			}
		}

		if(entityHit != null)
		{
			hit = new MovingObjectPosition(entityHit);
		}

		if(hit != null && hit.entityHit != null && hit.entityHit instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer)hit.entityHit;

			if(entityplayer.capabilities.disableDamage || (owner != null && !owner.canAttackPlayer(entityplayer)))
			{
				hit = null;
			}
		}

		float speed = 0.0F;
		speed = MathHelper.sqrt_double(this.motionX * this.motionX +
				this.motionY * this.motionY +
				this.motionZ * this.motionZ);
		distance += speed;
		if(hit != null && !worldObj.isRemote)
		{
			if (MFRRegistry.getNeedleAmmoTypes().containsKey(_ammoSource.getItem()))
			{
				if(hit.entityHit != null)
				{
					MFRRegistry.getNeedleAmmoTypes().get(_ammoSource.getItem()).onHitEntity(_ammoSource,
							owner, hit.entityHit, distance);
				}
				else
				{
					MFRRegistry.getNeedleAmmoTypes().get(_ammoSource.getItem()).onHitBlock(_ammoSource,
							owner, worldObj, hit.blockX, hit.blockY, hit.blockZ, hit.sideHit, distance);
				}
			}
			setDead();
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

		for(this.rotationPitch = (float)(Math.atan2(this.motionY, speed) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
		{
			;
		}

		while(this.rotationPitch - this.prevRotationPitch >= 180.0F)
		{
			this.prevRotationPitch += 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw < -180.0F)
		{
			this.prevRotationYaw -= 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw >= 180.0F)
		{
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		float speedDropoff = 0.995F;
		collisionRange = 0.05F;

		if (_falling | speed < 0.05)
		{
			_falling = true;
			this.motionY -= 0.01;
			speedDropoff = 0.99F;
		}

		if(this.isInWater())
		{
			double particleOffset = 0.25D;
			for(int i = 0; i < 4; ++i)
			{
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * particleOffset, this.posY - this.motionY * particleOffset, this.posZ - this.motionZ * particleOffset, this.motionX, this.motionY, this.motionZ);
			}

			speedDropoff = 0.8F;
		}

		this.motionX *= speedDropoff;
		this.motionY *= speedDropoff;
		this.motionZ *= speedDropoff;
		this.setPosition(this.posX, this.posY, this.posZ);
		this.func_145775_I();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		tag.setTag("ammoSource", _ammoSource.writeToNBT(new NBTTagCompound()));
		tag.setDouble("distance", distance);
		tag.setBoolean("falling", _falling);
		if (_owner != null)
			tag.setString("owner", _owner);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		if (tag.hasKey("ammoSource"))
		{
			_ammoSource = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("ammoSource"));
			distance = tag.getDouble("distance");
			_falling = tag.getBoolean("falling");
			if (tag.hasKey("owner"))
				_owner = tag.getString("owner");
		}
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	@Override
	public boolean canAttackWithItem()
	{
		return false;
	}
}
