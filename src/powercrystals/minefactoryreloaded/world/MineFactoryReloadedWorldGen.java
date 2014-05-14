package powercrystals.minefactoryreloaded.world;

import cpw.mods.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class MineFactoryReloadedWorldGen implements IWorldGenerator
{
	private static List<Integer> _blacklistedDimensions;
	
	public static boolean generateMegaRubberTree(World world, Random random, int x, int y, int z)
	{
		return new WorldGenMassiveTree(false).setTreeScale(4 + (random.nextInt(3)), 0.8, 0.7).
					setLeafAttenuation(0.6).setSloped(true).
					generate(world, random, x, y, z);
	}
	
	public static boolean generateSacredSpringRubberTree(World world, Random random, int x, int y, int z)
	{
		return new WorldGenMassiveTree(false).setTreeScale(6 + (random.nextInt(4)), 1, 0.9).
					setLeafAttenuation(0.35).setSloped(false).
					generate(world, random, x, y, z);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(_blacklistedDimensions == null)
		{
			_blacklistedDimensions = buildBlacklistedDimensions();
		}
		
		if(_blacklistedDimensions.contains(world.provider.dimensionId))
		{
			return;
		}
		
		int x = chunkX * 16 + random.nextInt(16);
		int z = chunkZ * 16 + random.nextInt(16);
		
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (b == null)
			return;
		
		String biomeName = b.biomeName;
		
		if(MFRConfig.rubberTreeWorldGen.getBoolean(true))
		{
			if(MFRRegistry.getRubberTreeBiomes().contains(biomeName))
			{
				if(random.nextInt(100) < 40)
				{
					String ln = b.biomeName.toLowerCase();
					if (random.nextInt(30) == 0)
					{
						if (ln.contains("mega"))
							generateMegaRubberTree(world, random, x, world.getHeightValue(x, z), z);
						else if (ln.contains("sacred"))
							generateSacredSpringRubberTree(world, random, x, world.getHeightValue(x, z), z);
					}
					new WorldGenRubberTree().generate(world, random, x, random.nextInt(3) + 4, z);
				}
			}
		}
		
		if(MFRConfig.mfrLakeWorldGen.getBoolean(true) && world.provider.canRespawnHere())
		{
			int rarity = MFRConfig.mfrLakeSludgeRarity.getInt();
			if(rarity > 0 && random.nextInt(rarity) == 0)
			{
				int lakeX = x - 8 + random.nextInt(16);
				int lakeY = random.nextInt(128);
				int lakeZ = z - 8 + random.nextInt(16);
				new WorldGenLakesMeta(MineFactoryReloadedCore.sludgeLiquid, 0).generate(world, random, lakeX, lakeY, lakeZ);
			}
			rarity = MFRConfig.mfrLakeSewageRarity.getInt();
			if(rarity > 0 && random.nextInt(rarity) == 0)
			{
				int lakeX = x - 8 + random.nextInt(16);
				int lakeY = random.nextInt(128);
				int lakeZ = z - 8 + random.nextInt(16);
				if(b.biomeName.toLowerCase().contains("mushroom"))
				{
					new WorldGenLakesMeta(MineFactoryReloadedCore.mushroomSoupLiquid, 0).generate(world, random, lakeX, lakeY, lakeZ);
				}
				else
				{
					new WorldGenLakesMeta(MineFactoryReloadedCore.sewageLiquid, 0).generate(world, random, lakeX, lakeY, lakeZ);
				}
			}
		}
	}
	
	private static List<Integer> buildBlacklistedDimensions()
	{
		String blacklist = MFRConfig.worldGenDimensionBlacklist.getString();
		List<Integer> dims = new ArrayList<Integer>();
		
		if(blacklist == null)
		{
			return dims;
		}
		blacklist = blacklist.trim();
		
		for(String dim : blacklist.split(","))
		{
			try
			{
				Integer dimId = Integer.parseInt(dim);
				dims.add(dimId);
			}
			catch(Exception x)
			{
			}
		}
		
		return dims;
	}
}
