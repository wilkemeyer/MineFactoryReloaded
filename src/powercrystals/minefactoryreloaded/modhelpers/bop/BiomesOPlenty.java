package powercrystals.minefactoryreloaded.modhelpers.bop;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import java.lang.reflect.Method;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizableSapling;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;
import com.google.common.base.Optional;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "MineFactoryReloaded|CompatBiomesOPlenty", name = "MFR Compat: Biomes O' Plenty", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:BiomesOPlenty")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class BiomesOPlenty
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("BiomesOPlenty"))
		{
			FMLLog.warning("BiomesOPlenty missing - MFR Biomes O' Plenty Compat not loading");
			return;
		}
		try
		{
			registerRubberTreeBiomes();
			registerFarmables();
			registerSludgeDrops();
		}
		catch(Throwable x)
		{
			x.printStackTrace();
		}
	}
	
	private static void registerRubberTreeBiomes() throws Throwable
	{

		String[] biomeFields = new String[]{"bayou", "birchForest", "bog", "borealForest",
				"deciduousForest", "forestNew", "grove", "jungleNew", "lushSwamp", "mapleWoods",
				"rainforest", "seasonalForest", "shield", "swamplandNew", "temperateRainforest", 
				"thicket", "tropicalRainforest", "woodland"};
		Class<?> clazz = Class.forName("biomesoplenty.api.Biomes");
		for (String field : biomeFields)
		{
			BiomeGenBase biome = (BiomeGenBase)clazz.getDeclaredField(field).get(null);
			MFRRegistry.registerRubberTreeBiome(biome.biomeName);
		}
	}

	private static void registerFarmables() throws Throwable
	{
		String[] bopLeaves = { "leaves1",  "leaves2",  "leavesColorized",  "treeMoss",  "willow",  "ivy",  "moss" };
		String[] bopFruitLeaves = { "leavesFruit" };
		String[] bopLogs = { "logs1",  "logs2",  "logs3",  "logs4",  "bamboo" };
		String[] bopMiscStandardHarvestables = { "flowers",  "plants",  "foliage",  "mushrooms" };
		String[] bopSaplings = { "saplings",  "colorizedSaplings" };

		Optional<? extends Block> block;
		Class<?> clazz = Class.forName("biomesoplenty.api.Blocks");
		for(String leaves : bopLeaves)
		{
			block = (Optional<? extends Block>)clazz.getDeclaredField(leaves).get(null);
			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(block.get().blockID));
		}

		for(String log : bopLogs)
		{
			block = (Optional<? extends Block>)clazz.getDeclaredField(log).get(null);
			MFRRegistry.registerHarvestable(new HarvestableStandard(block.get().blockID, HarvestType.Tree));
		}

		for(String harvestable : bopMiscStandardHarvestables)
		{
			block = (Optional<? extends Block>)clazz.getDeclaredField(harvestable).get(null);
			MFRRegistry.registerHarvestable(new HarvestableStandard(block.get().blockID, HarvestType.Normal));
		}

		for(String sapling : bopSaplings)
		{
			block = (Optional<? extends Block>)clazz.getDeclaredField(sapling).get(null);
			MFRRegistry.registerPlantable(new PlantableStandard(block.get().blockID, block.get().blockID));
			MFRRegistry.registerFertilizable(new FertilizableSapling(block.get().blockID));
		}

		for(String leaves : bopFruitLeaves)
		{
			block = (Optional<? extends Block>)clazz.getDeclaredField(leaves).get(null);
			MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(block.get().blockID));
			MFRRegistry.registerFruit(new FruitLeaves(block.get().blockID));
		}
	}

	private static void registerSludgeDrops() throws Throwable
	{
		Class<?> clazz = Class.forName("biomesoplenty.api.BlockReferences");
		Method m = clazz.getDeclaredMethod("getBlockItemStack", String.class);
		MFRRegistry.registerSludgeDrop(15, (ItemStack)m.invoke(null, "driedDirt"));
		MFRRegistry.registerSludgeDrop(15, (ItemStack)m.invoke(null, "hardSand"));
		MFRRegistry.registerSludgeDrop(15, (ItemStack)m.invoke(null, "hardDirt"));
		clazz = Class.forName("biomesoplenty.api.Items");
		MFRRegistry.registerSludgeDrop(15, new ItemStack(((Optional<? extends Item>)clazz.getDeclaredField("miscItems").get(null)).get(), 4, 1));
		MFRRegistry.registerSludgeDrop(25, new ItemStack(((Optional<? extends Item>)clazz.getDeclaredField("mudBall").get(null)).get(), 4));
	}
}
