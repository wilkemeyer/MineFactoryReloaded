package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFactoryFluid extends BlockFluidClassic implements IRedNetNoConnection
{ // TODO: convert to BlockFluidFinite
	private Icon _iconFlowing;
	private Icon _iconStill;
	protected String fluidName;
	
	public BlockFactoryFluid(int id, String liquidName)
	{
		super(id, FluidRegistry.getFluid(liquidName), Material.water);
		setUnlocalizedName("mfr.liquid." + liquidName + ".still");
		setHardness(100.0F);
		setLightOpacity(3);
		fluidName = liquidName;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (world.isRemote)
		{
			return;
		}
		
		if(entity instanceof EntityPlayer || entity instanceof EntityMob && !((EntityLivingBase)entity).isEntityUndead())
		{
			EntityLivingBase ent = (EntityLivingBase)entity;
			if(blockID == MineFactoryReloadedCore.sludgeLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.poison.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.weakness.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.confusion.id, 12 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.sewageLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.hunger.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.poison.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 12 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.essenceLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.nightVision.id, 60 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.milkLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 6 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.biofuelLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 12 * 20, 0));
			}
		}
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "fluid." + this.unlocalizedName;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		_iconStill = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		_iconFlowing = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName().replace(".still", ".flowing"));
	}
	
	@Override
	public Icon getIcon(int side, int meta)
	{
		return side <= 1 ? _iconStill : _iconFlowing;
	}
}
