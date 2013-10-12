package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

/**
 * NO-OP
 * @deprecated for FactoryRegistry
 */
@Deprecated
public class FarmingRegistry
{
	public static void registerPlantable(IFactoryPlantable plantable){}
	public static void registerHarvestable(IFactoryHarvestable harvestable){}
	public static void registerFertilizable(IFactoryFertilizable fertilizable){}
	public static void registerFertilizer(IFactoryFertilizer fertilizer){}
	public static void registerRanchable(IFactoryRanchable ranchable){}
	public static void registerGrindable(IFactoryGrindable grindable){}
	public static void registerSludgeDrop(int weight, ItemStack drop){}
	public static void registerBreederFood(Class<?> entityToBreed, ItemStack food){}
	public static void registerSafariNetHandler(ISafariNetHandler handler){}
	public static void registerMobEggHandler(IMobEggHandler handler){}
	public static void registerRubberTreeBiome(String biome){}
	public static void registerSafariNetBlacklist(Class<?> blacklistedEntity){}
	public static void registerVillagerTradeMob(IRandomMobProvider mobProvider){}
	public static void registerLiquidDrinkHandler(int liquidId, ILiquidDrinkHandler liquidDrinkHandler){}
	public static void registerLaserOre(int weight, ItemStack drop){}
	public static void setLaserPreferredOre(int color, ItemStack ore){}
	public static void registerFruitLogBlockId(Integer fruitLogBlockId){}
	public static void registerFruit(IFactoryFruit fruit){}
	public static void registerAutoSpawnerBlacklist(String entityString){}
	public static void registerRedNetLogicCircuit(IRedNetLogicCircuit circuit){}
}
