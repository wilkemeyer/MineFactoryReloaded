package powercrystals.minefactoryreloaded.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class WorldGenRubberTree extends WorldGenerator
{
	public WorldGenRubberTree()
	{
		super();
	}

	public WorldGenRubberTree(boolean doNotify)
	{
		super(doNotify);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int retries, int z)
	{
		for (int c = 0; c < retries; c++)
		{
			int y = world.getChunkFromBlockCoords(x, z).getTopFilledSegment() + 16;

			l: { 
				Block block;
				do {
					if (--y <= 0)
					{
						retries--;
						break l; // we're too low, skip trying to grow the tree
					}
					block = world.getBlock(x, y, z);
				} while (block.isAir(world, x, y, z) ||
						block.isLeaves(world, x, y, z) ||
						block.isReplaceable(world, x, y, z) ||
						block.canBeReplacedByLeaves(world, x, y, z));

				if (!growTree(world, rand, x, y + 1, z))
					retries--;
			}

			x += rand.nextInt(16) - 8;
			z += rand.nextInt(16) - 8;
		}

		return true;
	}

	public boolean growTree(World world, Random rand, int x, int y, int z)
	{
		int treeHeight = rand.nextInt(3) + 5,
				worldHeight = world.getHeight();
		Block block;

		if (y >= 1 && y + treeHeight + 1 <= worldHeight)
		{
			int xOffset;
			int yOffset;
			int zOffset;

			block = world.getBlock(x, y - 1, z);

			if ((block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP,
					MineFactoryReloadedCore.rubberSaplingBlock)) &&
					y < worldHeight - treeHeight - 1)
			{
				for (yOffset = y; yOffset <= y + 1 + treeHeight; ++yOffset)
				{
					byte radius;

					if (yOffset >= y + 1 + treeHeight - 2)
					{
						radius = 2;
					}
					else
					{
						radius = 0;
					}

					if (yOffset >= 0 & yOffset < worldHeight)
					{
						if (radius == 0)
						{
							block = world.getBlock(x, yOffset, z);
							if (!(block.isLeaves(world, x, yOffset, z) ||
									block.isAir(world, x, yOffset, z) ||
									block.isReplaceable(world, x, yOffset, z) ||
									block.canBeReplacedByLeaves(world, x, yOffset, z)))
							{
								return false;
							}

							if (yOffset >= y + 1)
							{
								radius = 1;
								for (xOffset = x - radius; xOffset <= x + radius; ++xOffset)
								{
									for (zOffset = z - radius; zOffset <= z + radius; ++zOffset)
									{
										block = world.getBlock(xOffset, yOffset, zOffset);

										if (block.getMaterial().isLiquid())
										{
											return false;
										}
									}
								}
							}
						}
						else for (xOffset = x - radius; xOffset <= x + radius; ++xOffset)
						{
							for (zOffset = z - radius; zOffset <= z + radius; ++zOffset)
							{
								block = world.getBlock(xOffset, yOffset, zOffset);

								if (!(block.isLeaves(world, xOffset, yOffset, zOffset) ||
										block.isAir(world, xOffset, yOffset, zOffset) ||
										block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset)))
								{
									return false;
								}
							}
						}
					}
					else
					{
						return false;
					}
				}

				block = world.getBlock(x, y - 1, z);
				if (!block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, 
						MineFactoryReloadedCore.rubberSaplingBlock))
				{ // this HAPPENS. wtf?
					return false; // abort, something went weird
				}
				block.onPlantGrow(world, x, y - 1, z, x, y, z);

				for (yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset)
				{
					int var12 = yOffset - (y + treeHeight),
							center = 1 - var12 / 2;

					for (xOffset = x - center; xOffset <= x + center; ++xOffset)
					{
						int xPos = xOffset - x, t = xPos >> 31;
						xPos = (xPos + t) ^ t;
	
						for (zOffset = z - center; zOffset <= z + center; ++zOffset)
						{
							int zPos = zOffset - z;
							zPos = (zPos + (t = zPos >> 31)) ^ t;
	
							block = world.getBlock(xOffset, yOffset, zOffset);
	
							if (((xPos != center | zPos != center) ||
									rand.nextInt(2) != 0 && var12 != 0) &&
									(block == null || block.isLeaves(world, xOffset, yOffset, zOffset) ||
									block.isAir(world, xOffset, yOffset, zOffset) ||
									block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset)))
							{
								this.setBlockAndNotifyAdequately(world, xOffset, yOffset, zOffset,
										MineFactoryReloadedCore.rubberLeavesBlock, 0);
							}
						}
					}
				}

				for (yOffset = 0; yOffset < treeHeight; ++yOffset)
				{
					block = world.getBlock(x, y + yOffset, z);

					if (block == null || block.isAir(world, x, y + yOffset, z)  ||
							block.isLeaves(world, x, y + yOffset, z) ||
							block.isReplaceable(world, x, y + yOffset, z)) // replace snow
					{
						this.setBlockAndNotifyAdequately(world, x, y + yOffset, z,
								MineFactoryReloadedCore.rubberWoodBlock, 1);
					}
				}

				return true;
			}
		}
		return false;
	}
}
