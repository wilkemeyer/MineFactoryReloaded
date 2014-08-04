package powercrystals.minefactoryreloaded.entity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.FishingHooks;

import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class EntityFishingRod extends EntityThrowable
{
	public int fuse;
	public Random rand = new Random();

	public EntityFishingRod(World world)
	{
		super(world);
		this.setSize(0.10F, 0.25F);
	}

	public EntityFishingRod(World world, EntityLivingBase entity)
	{
		super(world, entity);
		setSize(0.10F, 0.25F);
		fuse = 40;
	}

	@Override
	protected float func_70182_d()
	{
		return 0.6F;
	}

	@Override
	protected float func_70183_g()
	{
		return 0.0F;
	}

	@Override
	public void onUpdate()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.03999999910593033D;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (onGround)
		{
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
			motionY *= -0.5D;
		}

		if (worldObj.isRemote)
		{
			worldObj.spawnParticle("smoke", posX, posY + 0.25, posZ, 0, 0, 0);
		}
		else if (fuse-- <= 0)
		{
			setDead();
			explode();
		}
	}

	private void explode()
	{
		float f = 2.5F;
		worldObj.createExplosion(this, this.posX, this.posY, this.posZ, f, true);
		int rate = MFRConfig.fishingDropRate.getInt();
		for (float x = (float)(posX - f); x < posX + f; ++x)
			for (float y = (float)(posY - f); y < posY + f; ++y)
				for (float z = (float)(posZ - f); z < posZ + f; ++z)
				{
					Block block = worldObj.getBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
					if (block.isAssociatedBlock(Blocks.water) || block.isAssociatedBlock(Blocks.flowing_water))
						if (rand.nextInt(rate) == 0)
						{
							EntityItem e = new EntityItem(worldObj, x, y, z);
							e.motionX = rand.nextGaussian() / 2;
							e.motionZ = rand.nextGaussian() / 2;
							e.motionY = 0.4 + (rand.nextDouble() - 0.4) / 2;
							ItemStack stack = FishingHooks.getRandomFishable(rand, 1), s;
							if (rand.nextInt(30) == 0 && (s = FurnaceRecipes.smelting().getSmeltingResult(stack)) != null)
							{
								stack = s;
							}
							e.setEntityItemStack(stack);
							worldObj.spawnEntityInWorld(e);
						}
				}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		super.writeEntityToNBT(tag);
		tag.setByte("Fuse", (byte)fuse);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		super.readEntityFromNBT(tag);
		fuse = tag.getByte("Fuse");
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) { }

	// TODO: override moveEntity, handle water movement

}
