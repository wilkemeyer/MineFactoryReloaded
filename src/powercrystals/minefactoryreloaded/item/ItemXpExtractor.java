package powercrystals.minefactoryreloaded.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ItemXpExtractor extends ItemFactory
{
	private IIcon _icon1;
	private IIcon _icon2;
	private IIcon _icon3;
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.bow;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (player.experienceLevel > 0 && player.inventory.hasItem(Items.bucket))
		{
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}
		
		return stack;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
			return stack;
		if (player.experienceLevel > 0 && player.inventory.hasItem(Items.bucket))
		{
			if (player.inventory.consumeInventoryItem(Items.bucket))
			{
				player.addExperienceLevel(-1);
				if (!player.inventory.addItemStackToInventory(new ItemStack(MineFactoryReloadedCore.mobEssenceBucketItem)))
				{
					player.dropItem(MineFactoryReloadedCore.mobEssenceBucketItem, 1);
				}
			}
		}
		return stack;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		if (usingItem != null && usingItem.equals(this))
		{
			if(useRemaining > 24) return _icon1;
			if(useRemaining > 12) return _icon2;
			return _icon3;
		}
		return _icon1;
	}
	
	@Override
	public void registerIcons(IIconRegister ir)
	{
		_icon1 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".1");
		_icon2 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".2");
		_icon3 = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".3");
		
		itemIcon = _icon1;
	}
}
