package powercrystals.minefactoryreloaded.world;

import static powercrystals.minefactoryreloaded.MineFactoryReloadedCore.*;

import com.google.common.primitives.Ints;
import cpw.mods.fml.common.IWorldGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class MineFactoryReloadedWorldGen implements IWorldGenerator
{
	private static List<Integer> _blacklistedDimensions;
	private static List<String> _sludgeBiomeList, _sewageBiomeList, _rubberTreeBiomeList;
	private static boolean _sludgeLakeMode, _sewageLakeMode, _rubberTreesEnabled;
	private static int _sludgeLakeRarity, _sewageLakeRarity;

	public static boolean generateMegaRubberTree(World world, Random random, int x, int y, int z, boolean safe)
	{
		return new WorldGenMassiveTree(false).setTreeScale(4 + (random.nextInt(3)), 0.8f, 0.7f).
				setLeafAttenuation(0.6f).setSloped(true).setSafe(safe).
				generate(world, random, x, y, z);
	}

	public static boolean generateSacredSpringRubberTree(World world, Random random, int x, int y, int z)
	{
		return new WorldGenMassiveTree(false).setTreeScale(6 + (random.nextInt(4)), 1f, 0.9f).
				setLeafAttenuation(0.35f).setSloped(false).setMinTrunkSize(4).
				generate(world, random, x, y, z);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(_blacklistedDimensions == null)
		{
			buildBlacklistedDimensions();
		}

		if (_blacklistedDimensions.contains(world.provider.dimensionId))
		{
			return;
		}

		int x = chunkX * 16 + random.nextInt(16);
		int z = chunkZ * 16 + random.nextInt(16);

		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (b == null)
			return;

		String biomeName = b.biomeName, ln = biomeName.toLowerCase();

		if (_rubberTreesEnabled)
		{
			if (_rubberTreeBiomeList.contains(biomeName))
			{
				if (random.nextInt(100) < 40)
				{
					if (random.nextInt(30) == 0)
					{
						if (ln.contains("mega"))
							generateMegaRubberTree(world, random, x, world.getHeightValue(x, z), z, false);
						else if (ln.contains("sacred") && random.nextInt(20) == 0)
							generateSacredSpringRubberTree(world, random, x, world.getHeightValue(x, z), z);
					}
					new WorldGenRubberTree(false).generate(world, random, x, random.nextInt(3) + 4, z);
				}
			}
		}

		if (MFRConfig.mfrLakeWorldGen.getBoolean(true) && world.provider.canRespawnHere())
		{
			int rarity = _sludgeLakeRarity;
			if (rarity > 0 &&
					_sludgeBiomeList.contains(biomeName) == _sludgeLakeMode &&
					random.nextInt(rarity) == 0)
			{
				int lakeX = x - 8 + random.nextInt(16);
				int lakeY = random.nextInt(world.getActualHeight());
				int lakeZ = z - 8 + random.nextInt(16);
				new WorldGenLakesMeta(sludgeLiquid, 0).generate(world, random, lakeX, lakeY, lakeZ);
			}

			rarity = _sewageLakeRarity;
			if (rarity > 0 &&
					_sewageBiomeList.contains(biomeName) == _sewageLakeMode &&
					random.nextInt(rarity) == 0)
			{
				int lakeX = x - 8 + random.nextInt(16);
				int lakeY = random.nextInt(world.getActualHeight());
				int lakeZ = z - 8 + random.nextInt(16);
				if (ln.contains("mushroom"))
				{
					new WorldGenLakesMeta(mushroomSoupLiquid, 0).generate(world, random, lakeX, lakeY, lakeZ);
				}
				else
				{
					new WorldGenLakesMeta(sewageLiquid, 0).generate(world, random, lakeX, lakeY, lakeZ);
				}
			}
		}
	}

	private static void buildBlacklistedDimensions()
	{
		_blacklistedDimensions = Ints.asList(MFRConfig.worldGenDimensionBlacklist.getIntList());

		_rubberTreeBiomeList = MFRRegistry.getRubberTreeBiomes();
		_rubberTreesEnabled = MFRConfig.rubberTreeWorldGen.getBoolean(true);

		_sludgeLakeRarity = MFRConfig.mfrLakeSludgeRarity.getInt();
		_sludgeBiomeList = Arrays.asList(MFRConfig.mfrLakeSludgeBiomeList.getStringList());
		_sludgeLakeMode = MFRConfig.mfrLakeSludgeBiomeListToggle.getBoolean(false);

		_sewageLakeRarity = MFRConfig.mfrLakeSewageRarity.getInt();
		_sewageBiomeList = Arrays.asList(MFRConfig.mfrLakeSewageBiomeList.getStringList());
		_sewageLakeMode = MFRConfig.mfrLakeSewageBiomeListToggle.getBoolean(false);
	}
}
