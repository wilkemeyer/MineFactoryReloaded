package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerSludge implements ILiquidDrinkHandler
{
	@Override
	public void onDrink(EntityLivingBase player)
	{
		player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 40 * 20, 0));
	}
}
