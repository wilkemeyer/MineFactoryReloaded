package powercrystals.minefactoryreloaded;

import cofh.lib.util.WeightedRandomItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import powercrystals.minefactoryreloaded.api.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizer;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.api.IFactoryPlantable;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;
import powercrystals.minefactoryreloaded.api.IMobEggHandler;
import powercrystals.minefactoryreloaded.api.IMobSpawnHandler;
import powercrystals.minefactoryreloaded.api.INeedleAmmo;
import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import powercrystals.minefactoryreloaded.core.UtilInventory;

public abstract class MFRRegistry
{
	private static Map<Item, IFactoryPlantable> _plantables = new HashMap<Item, IFactoryPlantable>();
	
	private static Map<Block, IFactoryHarvestable> _harvestables = new HashMap<Block, IFactoryHarvestable>();
	
	private static Map<Item, IFactoryFertilizer> _fertilizers = new HashMap<Item, IFactoryFertilizer>();
	
	private static Map<Block, IFactoryFertilizable> _fertilizables = new HashMap<Block, IFactoryFertilizable>();
	
	private static Map<Class<? extends EntityLivingBase>, IFactoryRanchable> _ranchables =
			new HashMap<Class<? extends EntityLivingBase>, IFactoryRanchable>();
	
	private static Map<String, ILiquidDrinkHandler> _liquidDrinkHandlers =
			new HashMap<String, ILiquidDrinkHandler>();
	
	private static Map<Item, INeedleAmmo> _needleAmmoTypes = new HashMap<Item, INeedleAmmo>();
	
	private static List<Block> _fruitLogBlocks = new ArrayList<Block>();
	private static Map<Block, IFactoryFruit> _fruitBlocks = new HashMap<Block, IFactoryFruit>();

	private static List<WeightedRandom.Item> _sludgeDrops  = new ArrayList<WeightedRandom.Item>();
	
	private static List<String> _rubberTreeBiomes = new ArrayList<String>();
	
	private static List<IRedNetLogicCircuit> _redNetLogicCircuits = new ArrayList<IRedNetLogicCircuit>();
	
	private static Map<Class<? extends EntityLivingBase>, IFactoryGrindable> _grindables =
			new HashMap<Class<? extends EntityLivingBase>, IFactoryGrindable>();
	private static List<Class<? extends EntityLivingBase>> _grindableBlacklist =
			new ArrayList<Class<? extends EntityLivingBase>>();
	
	private static List<Class<? extends EntityLivingBase>> _safariNetBlacklist =
			new ArrayList<Class<? extends EntityLivingBase>>();
	private static List<IMobEggHandler> _eggHandlers = new ArrayList<IMobEggHandler>();
	private static List<ISafariNetHandler> _safariNetHandlers = new ArrayList<ISafariNetHandler>();
	private static List<IRandomMobProvider> _randomMobProviders = new ArrayList<IRandomMobProvider>();
	
	private static Map<Class<? extends EntityLivingBase>, IMobSpawnHandler> _spawnHandlers =
			new HashMap<Class<? extends EntityLivingBase>, IMobSpawnHandler>();
	private static List<String> _autoSpawnerBlacklist = new ArrayList<String>();
	private static List<Class<? extends EntityLivingBase>> _autoSpawnerClassBlacklist =
			new ArrayList<Class<? extends EntityLivingBase>>();
	
	private static List<Class<? extends EntityLivingBase>> _slaughterhouseBlacklist =
			new ArrayList<Class<? extends EntityLivingBase>>();
	
	private static List<Class<? extends Entity>> _conveyerBlacklist =
			new ArrayList<Class<? extends Entity>>();
	
	private static Map<String, Boolean> _unifierBlacklist  = new TreeMap<String, Boolean>();

	private static List<WeightedRandom.Item> _laserOres  = new ArrayList<WeightedRandom.Item>();
	private static Map<Integer, List<ItemStack>> _laserPreferredOres = new HashMap<Integer, List<ItemStack>>(16);

	public static void registerPlantable(IFactoryPlantable plantable)
	{
		_plantables.put(plantable.getSeed(), plantable);
	}

	public static Map<Item, IFactoryPlantable> getPlantables()
	{
		return _plantables;
	}

	public static void registerHarvestable(IFactoryHarvestable harvestable)
	{
		_harvestables.put(harvestable.getPlant(), harvestable);
	}

	public static Map<Block, IFactoryHarvestable> getHarvestables()
	{
		return _harvestables;
	}

	public static void registerFertilizable(IFactoryFertilizable fertilizable)
	{
		_fertilizables.put(fertilizable.getPlant(), fertilizable);
	}

	public static Map<Block, IFactoryFertilizable> getFertilizables()
	{
		return _fertilizables;
	}

	public static void registerFertilizer(IFactoryFertilizer fertilizer)
	{
		_fertilizers.put(fertilizer.getFertilizer(), fertilizer);
	}

	public static Map<Item, IFactoryFertilizer> getFertilizers()
	{
		return _fertilizers;
	}

	public static void registerRanchable(IFactoryRanchable ranchable)
	{
		_ranchables.put(ranchable.getRanchableEntity(), ranchable);
	}

	public static Map<Class<? extends EntityLivingBase>, IFactoryRanchable> getRanchables()
	{
		return _ranchables;
	}

	public static void registerGrindable(IFactoryGrindable grindable)
	{
		_grindables.put(grindable.getGrindableEntity(), grindable);
	}

	public static Map<Class<? extends EntityLivingBase>, IFactoryGrindable> getGrindables()
	{
		return _grindables;
	}

	public static void registerGrinderBlacklist(Class<? extends EntityLivingBase> ungrindable)
	{
		_grindableBlacklist.add(ungrindable);
		if (MFRRegistry._safariNetBlacklist.contains(ungrindable))
			_slaughterhouseBlacklist.add(ungrindable);
	}

	public static List<Class<? extends EntityLivingBase>> getGrinderBlacklist()
	{
		return _grindableBlacklist;
	}

	public static List<Class<? extends EntityLivingBase>> getSlaughterhouseBlacklist()
	{
		return _slaughterhouseBlacklist;
	}

	public static void registerSludgeDrop(int weight, ItemStack drop)
	{
		_sludgeDrops.add(new WeightedRandomItemStack(drop.copy(), weight));
	}

	public static List<WeightedRandom.Item> getSludgeDrops()
	{
		return _sludgeDrops;
	}

	public static void registerMobEggHandler(IMobEggHandler handler)
	{
		_eggHandlers.add(handler);
	}

	public static List<IMobEggHandler> getModMobEggHandlers()
	{
		return _eggHandlers;
	}

	public static void registerSafariNetHandler(ISafariNetHandler handler)
	{
		_safariNetHandlers.add(handler);
	}

	public static List<ISafariNetHandler> getSafariNetHandlers()
	{
		return _safariNetHandlers;
	}

	public static void registerRubberTreeBiome(String biome)
	{
		_rubberTreeBiomes.add(biome);
	}

	public static List<String> getRubberTreeBiomes()
	{
		return _rubberTreeBiomes;
	}

	public static void registerSafariNetBlacklist(Class<? extends EntityLivingBase> entityClass)
	{
		_safariNetBlacklist.add(entityClass);
		if (MFRRegistry._grindableBlacklist.contains(entityClass))
			_slaughterhouseBlacklist.add(entityClass);
	}

	public static List<Class<? extends EntityLivingBase>> getSafariNetBlacklist()
	{
		return _safariNetBlacklist;
	}

	public static void registerRandomMobProvider(IRandomMobProvider mobProvider)
	{
		_randomMobProviders.add(mobProvider);
	}

	public static List<IRandomMobProvider> getRandomMobProviders()
	{
		return _randomMobProviders;
	}

	public static void registerLiquidDrinkHandler(String liquidId, ILiquidDrinkHandler liquidDrinkHandler)
	{
		_liquidDrinkHandlers.put(liquidId, liquidDrinkHandler);
	}

	public static Map<String, ILiquidDrinkHandler> getLiquidDrinkHandlers()
	{
		return _liquidDrinkHandlers;
	}

	public static void registerRedNetLogicCircuit(IRedNetLogicCircuit circuit)
	{
		_redNetLogicCircuits.add(circuit);
	}

	public static List<IRedNetLogicCircuit> getRedNetLogicCircuits()
	{
		return _redNetLogicCircuits;
	}

	public static void registerLaserOre(int weight, ItemStack ore)
	{
		for (WeightedRandom.Item item : _laserOres)
			if (UtilInventory.stacksEqual(((WeightedRandomItemStack)item).getStack(), ore))
			{
				item.itemWeight += weight;
				item.itemWeight /= 2;
				return;
			}
		_laserOres.add(new WeightedRandomItemStack(ore.copy(), weight));
	}

	public static List<WeightedRandom.Item> getLaserOres()
	{
		return _laserOres;
	}

	public static void registerFruitLogBlock(Block fruitLogBlock)
	{
		_fruitLogBlocks.add(fruitLogBlock);
	}

	public static List<Block> getFruitLogBlocks()
	{
		return _fruitLogBlocks;
	}

	public static void registerFruit(IFactoryFruit fruit)
	{
		_fruitBlocks.put(fruit.getPlant(), fruit);
	}

	public static Map<Block, IFactoryFruit> getFruits()
	{
		return _fruitBlocks;
	}
	
	public static void registerAutoSpawnerBlacklistClass(Class<? extends EntityLivingBase> entityClass)
	{
		_autoSpawnerClassBlacklist.add(entityClass);
	}

	public static List<Class<? extends EntityLivingBase>> getAutoSpawnerClassBlacklist()
	{
		return _autoSpawnerClassBlacklist;
	}

	public static void registerAutoSpawnerBlacklist(String entityString)
	{
		_autoSpawnerBlacklist.add(entityString);
	}

	public static List<String> getAutoSpawnerBlacklist()
	{
		return _autoSpawnerBlacklist;
	}
	
	public static void registerSpawnHandler(IMobSpawnHandler spawnHandler)
	{
		_spawnHandlers.put(spawnHandler.getMobClass(), spawnHandler);
	}
	
	public static Map<Class<? extends EntityLivingBase>, IMobSpawnHandler> getSpawnHandlers()
	{
		return _spawnHandlers;
	}

	public static void registerUnifierBlacklist(String string)
	{
		_unifierBlacklist.put(string, null);
	}

	public static Map<String, Boolean> getUnifierBlacklist()
	{
		return _unifierBlacklist;
	}
	
	public static void registerConveyerBlacklist(Class<? extends Entity> entityClass)
	{
		_conveyerBlacklist.add(entityClass);
	}

	public static List<Class<? extends Entity>> getConveyerBlacklist()
	{
		return _conveyerBlacklist;
	}

	public static void addLaserPreferredOre(int color, ItemStack ore)
	{
		if(color < 0 || 16 <= color) return;
		
		List<ItemStack> oresForColor = _laserPreferredOres.get(color);
		
		if(oresForColor == null)
		{
			List<ItemStack> oresList = new ArrayList<ItemStack>();
			oresList.add(ore);
			_laserPreferredOres.put(color, oresList);
		}
		else
		{	
			for(ItemStack registeredOre : oresForColor)
			{
				if(UtilInventory.stacksEqual(registeredOre, ore))
				{
					return;
				}
			}
			oresForColor.add(ore);
		}
	}

	public static List<ItemStack> getLaserPreferredOres(int color)
	{
		return _laserPreferredOres.get(color);
	}
	
	public static void registerNeedleAmmoType(Item item, INeedleAmmo ammo)
	{
		_needleAmmoTypes.put(item, ammo);
	}
	
	public static Map<Item, INeedleAmmo> getNeedleAmmoTypes()
	{
		return _needleAmmoTypes;
	}
}
