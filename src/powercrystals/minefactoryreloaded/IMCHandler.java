package powercrystals.minefactoryreloaded;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.relauncher.ReflectionHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;

import org.apache.logging.log4j.Logger;

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
import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.api.ValuedItem;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class IMCHandler
{
	@SuppressWarnings("unchecked")
	public static void processIMC(List<IMCMessage> l)
	{
		Logger _log = MineFactoryReloadedCore.instance().getLogger();
		for (IMCMessage m : l)
		{
			try
			{
				String k = m.key;
				/*
				 * Laser Prefered Ores
				 */
				if ("addLaserPreferedOre".equals(k))
				{
					ValuedItem item = (ValuedItem)getValue(m);
					MFRRegistry.addLaserPreferredOre(item.value, item.item);
				}
				/*
				 * AutoSpawner Blacklist
				 */
				else if ("registerAutoSpawnerBlacklist".equals(k))
				{
					if (m.isStringMessage())
						MFRRegistry.registerAutoSpawnerBlacklist(m.getStringValue());
					else
						MFRRegistry.registerAutoSpawnerBlacklistClass((Class<? extends EntityLivingBase>)
								getValue(m));
				}
				/*
				 * Fertilizables
				 */
				else if ("registerFertilizable".equals(k))
				{
					MFRRegistry.registerFertilizable((IFactoryFertilizable) getValue(m));
				}
				/*
				 * Fertilizers 
				 */
				else if ("registerFertilizer".equals(k))
				{
					MFRRegistry.registerFertilizer((IFactoryFertilizer) getValue(m));
				}
				/*
				 * Fruit logs
				 */
				else if ("registerFruitLog".equals(k))
				{
					if (m.isStringMessage())
						MFRRegistry.registerFruitLogBlock(Block.getBlockFromName(m.getStringValue()));
					else
						MFRRegistry.registerFruitLogBlock((Block)getValue(m));
				}
				/*
				 * Grinding handlers
				 */
				else if ("registerGrindable".equals(k))
				{
					MFRRegistry.registerGrindable((IFactoryGrindable) getValue(m));
				}
				/*
				 * Grinder blacklist
				 */
				else if ("registerGrinderBlacklist".equals(k))
				{
					MFRRegistry.registerGrinderBlacklist((Class<? extends EntityLivingBase>) getValue(m));
				}
				/*
				 * Harvestables
				 */
				else if ("registerHarvestable".equals(k))
				{
					MFRRegistry.registerHarvestable((IFactoryHarvestable) getValue(m));
				}
				/*
				 * Laser ores drops
				 */
				else if ("registerLaserOre".equals(k))
				{
					ValuedItem item = (ValuedItem)getValue(m);
					MFRRegistry.registerLaserOre(item.value, item.item);
				}
				/*
				 * Liquid Drinking Handlers
				 */
				else if ("registerLiquidDrinkHandler".equals(k))
				{
					ValuedItem item = (ValuedItem)getValue(m);
					MFRRegistry.registerLiquidDrinkHandler(item.key, (ILiquidDrinkHandler) item.object);
				}
				/*
				 * Mob egg handlers
				 */
				else if ("registerMobEggHandler".equals(k))
				{
					MFRRegistry.registerMobEggHandler((IMobEggHandler) getValue(m));
				}
				/*
				 * Fruit Handlers
				 */
				else if ("registerPickableFruit".equals(k))
				{
					MFRRegistry.registerFruit((IFactoryFruit) getValue(m));
				}
				/*
				 * Plantables
				 */
				else if ("registerPlantable".equals(k))
				{
					MFRRegistry.registerPlantable((IFactoryPlantable) getValue(m));
				}
				/*
				 * Ranching Handlers
				 */
				else if ("registerRanchable".equals(k))
				{
					MFRRegistry.registerRanchable((IFactoryRanchable) getValue(m));
				}
				/*
				 * RedNet Logic Circuits
				 */
				else if ("registerRedNetLogicCircuit".equals(k))
				{
					MFRRegistry.registerRedNetLogicCircuit((IRedNetLogicCircuit) getValue(m));
				}
				/*
				 * Rubber tree biome whitelisting
				 */
				else if ("registerRubberTreeBiome".equals(k))
				{
					MFRRegistry.registerRubberTreeBiome(m.getStringValue());
				}
				/*
				 * SafariNet Blacklist
				 */
				else if ("registerSafariNetBlacklist".equals(k))
				{
					MFRRegistry.registerSafariNetBlacklist((Class<? extends EntityLivingBase>)
							getValue(m));
				}
				/*
				 * SafariNet Information Handler
				 */
				else if ("registerSafariNetHandler".equals(k))
				{
					MFRRegistry.registerSafariNetHandler((ISafariNetHandler) getValue(m));
				}
				/*
				 * Sludge drop list
				 */
				else if ("registerSludgeDrop".equals(k))
				{
					ValuedItem item = (ValuedItem)getValue(m);
					MFRRegistry.registerSludgeDrop(item.value, item.item);
				}
				/*
				 * Mob Spawning handlers
				 */
				else if ("registerSpawnHandler".equals(k))
				{
					MFRRegistry.registerSpawnHandler((IMobSpawnHandler) getValue(m));
				}
				/*
				 * Random mob providers
				 */
				else if ("registerVillagerTradeMob".equals(k))
				{
					MFRRegistry.registerRandomMobProvider((IRandomMobProvider) getValue(m));
				}
				/**
				 * Unknown IMC message
				 */
				else
					_log.debug("Unknown IMC message (%s) from %s", k, m.getSender());
			}
			catch (Throwable _)
			{
				_log.error("Bad IMC message (%s) from %s", m.key, m.getSender(), _);
			}
		}
	}

	private static Object getValue(IMCMessage m)
	{
		return ReflectionHelper.getPrivateValue(IMCMessage.class, m, "value");
	}
}
