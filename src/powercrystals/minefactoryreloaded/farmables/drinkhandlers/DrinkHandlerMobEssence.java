package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;

import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerMobEssence implements ILiquidDrinkHandler {

	@Override
	public void onDrink(EntityLivingBase player, FluidStack fluid) {

		player.worldObj.spawnEntityInWorld(new EntityXPOrb(player.worldObj,
				player.posX, player.posY, player.posZ,
				player.worldObj.rand.nextInt(6) + 10));
	}

}
