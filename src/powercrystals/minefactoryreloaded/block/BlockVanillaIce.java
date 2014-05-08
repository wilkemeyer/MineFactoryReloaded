package powercrystals.minefactoryreloaded.block;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetDecorative;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class BlockVanillaIce extends BlockIce implements IRedNetDecorative
{
	public BlockVanillaIce()
	{
		setHardness(0.5F);
		setLightOpacity(3);
		setStepSound(soundTypeGlass);
		setBlockName("ice");
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if(world.getBlockMetadata(x, y, z) == 0)
		{
			super.updateTick(world, x, y, z, rand);
		}
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta)
	{
		player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
		player.addExhaustion(0.025F);
		
		if(this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(player))
		{
			ItemStack droppedStack = this.createStackedBlock(meta);
			
			if(droppedStack != null)
			{
				this.dropBlockAsItem(world, x, y, z, droppedStack);
			}
		}
		else
		{
			if(world.provider.isHellWorld)
			{
				return;
			}
			
			int fortune = EnchantmentHelper.getFortuneModifier(player);
			this.dropBlockAsItem(world, x, y, z, meta, fortune);
			Material var8 = world.getBlock(x, y - 1, z).getMaterial();
			
			if((var8.blocksMovement() || var8.isLiquid()) && meta == 0)
			{
				world.setBlock(x, y, z, Blocks.flowing_water);
			}
		}
	}
}
