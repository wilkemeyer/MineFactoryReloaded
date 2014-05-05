package powercrystals.minefactoryreloaded.setup.village;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;

public class VillageTradeHandler implements IVillageTradeHandler
{
	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
	{
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(MineFactoryReloadedCore.safariNetSingleItem)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 3), new ItemStack(MineFactoryReloadedCore.safariNetItem)));
		
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(MineFactoryReloadedCore.safariNetSingleItem), getHiddenNetStack()));
		
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(Blocks.sapling, 8, 0), new ItemStack(MineFactoryReloadedCore.rubberSaplingBlock, 8, 0)));
	}
	
	public static ItemStack getHiddenNetStack()
	{
		ItemStack s = new ItemStack(MineFactoryReloadedCore.safariNetSingleItem);
		return ItemSafariNet.makeMysteryNet(s);
	}
}
