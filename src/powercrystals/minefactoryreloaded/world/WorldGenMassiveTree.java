package powercrystals.minefactoryreloaded.world;

import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class WorldGenMassiveTree extends WorldGenerator
{
	/**
	 * Contains three sets of two values that provide complimentary indices for a given 'major' index - 1 and 2 for 0, 0
	 * and 2 for 1, and 0 and 1 for 2.
	 */
	private static final byte[] otherCoordPairs = new byte[] {(byte)2, (byte)0, (byte)0, (byte)1, (byte)2, (byte)1};

	/** random seed for GenBigTree */
	private Random rand = new Random();

	/** Reference to the World object. */
	private World worldObj;
	private int[] basePos = new int[] {0, 0, 0};
	private int heightLimit = 0;
	private int minHeight = -1;
	private int height;
	private double heightAttenuation = 0.45D;
	private double branchSlope = 0.45D;
	private double scaleWidth = 4.0D;
	private double branchDensity = 3.0D;
	private boolean slopeTrunk = false;
	private boolean safeGrowth = false;

	private static class ChunkComp implements Comparator<Chunk>
	{
		@Override
		public int compare(Chunk o1, Chunk o2)
		{
			if (o1.xPosition != o2.xPosition)
				return o1.xPosition - o2.xPosition;
			return o1.zPosition - o2.zPosition;
		}
	}

	private TreeSet<Chunk> modifiedChunks = new TreeSet<Chunk>(new ChunkComp());

	private int trunkSize = 11;

	/**
	 * Sets the limit of the random value used to initialize the height limit.
	 */
	private int heightLimitLimit = 250;

	/**
	 * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
	 */
	private int leafDistanceLimit = 4;

	/** Contains a list of a points at which to generate groups of leaves. */
	private int[][] leafNodes;

	public WorldGenMassiveTree(boolean par1)
	{
		super(par1);
	}

	public WorldGenMassiveTree()
	{
		super();
	}

	/**
	 * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
	 */
	private void generateLeafNodeList()
	{
		int var1 = (int)(1.382D + Math.pow(branchDensity * heightLimit / 13.0D, 2.0D));

		if (var1 < 1)
		{
			var1 = 1;
		}

		int[][] var2 = new int[var1 * heightLimit][4];
		int var3 = basePos[1] + heightLimit - leafDistanceLimit;
		int var4 = 1;
		int var5 = basePos[1] + height;
		int var6 = var3 - basePos[1];
		var2[0][0] = basePos[0];
		var2[0][1] = var3;
		var2[0][2] = basePos[2];
		var2[0][3] = var5;
		--var3;

		while (var6 >= 0)
		{
			int var7 = 0;
			float var8 = this.layerSize(var6);

			if (var8 > 0.0F)
				for (double var9 = 0.5D; var7 < var1; ++var7)
				{
					double var11 = scaleWidth * var8 * (rand.nextFloat() + 0.328D);
					double var13 = rand.nextFloat() * 2.0D * Math.PI;
					int var15 = MathHelper.floor_double(var11 * Math.sin(var13) + basePos[0] + var9);
					int var16 = MathHelper.floor_double(var11 * Math.cos(var13) + basePos[2] + var9);
					int[] var17 = new int[] {var15, var3, var16};
					int[] var18 = new int[] {var15, var3 + leafDistanceLimit, var16};

					if (this.checkBlockLine(var17, var18) == -1)
					{
						int[] var19 = new int[] {basePos[0], basePos[1], basePos[2]};
						int t;
						double var20 = Math.sqrt((t=basePos[0] - var17[0])*t + (t=basePos[2] - var17[2])*t);
						int var22 = (int)(var20 * branchSlope);

						var19[1] = Math.min(var17[1] - var22, var5);

						if (this.checkBlockLine(var19, var17) == -1)
						{
							var2[var4][0] = var15;
							var2[var4][1] = var3;
							var2[var4][2] = var16;
							var2[var4][3] = var19[1];
							++var4;
						}
					}
				}
			--var3;
			--var6;
		}

		leafNodes = new int[var4][4];
		System.arraycopy(var2, 0, leafNodes, 0, var4);
	}

	private void genTreeLayer(int x, int y, int z, float par4, byte par5, Block par6)
	{
		int t;
		int var7 = (int)(par4 + 0.618D);
		byte var8 = otherCoordPairs[par5];
		byte var9 = otherCoordPairs[par5 + 3];
		int[] var10 = new int[] {x, y, z};
		int[] var11 = new int[] {0, 0, 0};
		int var12 = -var7;
		int var13;
		double par4a = par4 * par4;

		for (var11[par5] = var10[par5]; var12 <= var7; ++var12)
		{
			var11[var8] = var10[var8] + var12;
			int var12a = var12*var12+(((t=var12>>31)^var12)-t);

			for (var13 = 0; var13 <= var7; )
			{
				double var15 = var12a + var13*var13+var13 + 0.5;

				if (var15 > par4a)
				{
					break;
				}
				else
				{
					t = -1;
					for (int i = 2; i --> 0; t = 1)
					{
						var11[var9] = var10[var9] + var13 * t;
						x = var11[0]; y = var11[1]; z = var11[2];
						Block var14 = worldObj.getBlock(x, y, z);

						if (safeGrowth ? (var14.isAir(worldObj, x, y, z) ||
								var14.isLeaves(worldObj, x, y, z) ||
								var14.canBeReplacedByLeaves(worldObj, x, y, z)) :
									!var14.equals(Blocks.bedrock))
						{
							this.setBlockAndNotifyAdequately(worldObj, x, y, z, par6, 0);
						}
					}
					++var13;
				}
			}
		}
	}

	/**
	 * Gets the rough size of a layer of the tree.
	 */
	private float layerSize(int par1)
	{
		if (par1 < (heightLimit) * heightAttenuation)
			return -1.618F;
		else
		{
			float var2 = heightLimit / 2.0F;
			float var3 = heightLimit / 2.0F - par1;
			float var4;

			if (var3 == 0.0F)
			{
				var4 = var2;
			}
			else if (Math.abs(var3) >= var2)
			{
				var4 = 0.0F;
			}
			else
			{
				var4 = (float)Math.sqrt(Math.pow(var2, 2.0D) - Math.pow(var3, 2.0D));
			}

			var4 *= 0.5F;
			return var4;
		}
	}

	private float leafSize(int par1)
	{
		return par1 >= 0 && par1 < leafDistanceLimit ?
				(par1 != 0 && par1 != leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
	}

	/**
	 * Generates the leaves surrounding an individual entry in the leafNodes list.
	 */
	private void generateLeafNode(int par1, int par2, int par3)
	{
		int var4 = par2;

		for (int var5 = par2 + leafDistanceLimit; var4 < var5; ++var4)
		{
			float var6 = this.leafSize(var4 - par2);
			if (var6 > 0)
				genTreeLayer(par1, var4, par3, var6, (byte)1, MineFactoryReloadedCore.rubberLeavesBlock);
		}
	}
	
	private void placeBlockLine(int[] par1, int[] par2, Block par3, int meta)
	{
		int t;
		int[] var4 = new int[] {0, 0, 0};
		byte var5 = 0;
		byte var6;

		for (var6 = 0; var5 < 3; ++var5)
		{
			int a = par2[var5] - par1[var5],
					b = ((t=a>>31)^a)-t;
			var4[var5] = a;
			if (b > ((a=var4[var6])^(t=a>>31))-t)
				var6 = var5;
		}

		if (var4[var6] != 0)
		{
			byte var7 = otherCoordPairs[var6];
			byte var8 = otherCoordPairs[var6 + 3];
			byte var9;

			if (var4[var6] > 0)
			{
				var9 = 1;
			}
			else
			{
				var9 = -1;
			}

			float var10 = (float)var4[var7] / (float)var4[var6];
			float var12 = (float)var4[var8] / (float)var4[var6];
			int[] var14 = new int[] {0, 0, 0};
			int var15 = 0;

			for (int var16 = var4[var6] + var9; var15 != var16; var15 += var9)
			{
				var14[var6] = MathHelper.floor_float(par1[var6] + var15 + 0.5F);
				var14[var7] = MathHelper.floor_float(par1[var7] + var15 * var10 + 0.5F);
				var14[var8] = MathHelper.floor_float(par1[var8] + var15 * var12 + 0.5F);
				byte var17 = 0;
				int var18 = var14[0] - par1[0]; var18 = ((t=var18>>31)^var18)-t;
				int var19 = var14[2] - par1[2]; var19 = ((t=var19>>31)^var19)-t;
				int var20 = Math.max(var18, var19);

				if (var20 > 0)
				{
					if (var18 == var20)
					{
						var17 = 4;
					}
					else if (var19 == var20)
					{
						var17 = 8;
					}
				}

				this.setBlockAndNotifyAdequately(worldObj, var14[0], var14[1], var14[2], par3, meta | var17);
			}
		}
	}

	/**
	 * Generates the leaf portion of the tree as specified by the leafNodes list.
	 */
	private void generateLeaves()
	{
		int var1 = 0;

		for (int var2 = leafNodes.length; var1 < var2; ++var1)
		{
			int var3 = leafNodes[var1][0];
			int var4 = leafNodes[var1][1];
			int var5 = leafNodes[var1][2];
			this.generateLeafNode(var3, var4, var5);
		}
	}

	/**
	 * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
	 */
	private boolean leafNodeNeedsBase(int par1)
	{
		return par1 >= heightLimit * 0.2D;
	}

	/**
	 * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
	 * field that is always 1 to 2.
	 */
	private void generateTrunk()
	{
		int var1 = basePos[0];
		int var2 = basePos[1];
		int var3 = basePos[1] + height;
		int var4 = basePos[2];

		int[] var5 = new int[] {var1, var2, var4};
		int[] var6 = new int[] {var1, var3, var4};
		
		double lim = 400f / trunkSize;
		
		for (int i = -trunkSize; i <= trunkSize; i++ )
		{
			var5[0] = var1 + i;
			var6[0] = var1 + i;

			for (int j = -trunkSize; j <= trunkSize; j++ )
			{
				if ((j*j + i*i) * 4 < trunkSize * trunkSize * 5)
				{
					var5[2] = var4 + j;
					var6[2] = var4 + j;
					
					if (slopeTrunk)
						var6[1] = var2 + sinc2(lim * i, lim * j, height) - (rand.nextInt(3) - 1);
					
					this.placeBlockLine(var5, var6,
							MineFactoryReloadedCore.rubberWoodBlock, 1);
					this.setBlockAndNotifyAdequately(worldObj, var6[0], var6[1], var6[2],
							MineFactoryReloadedCore.rubberWoodBlock, 12 | 1);
					worldObj.getBlock(var5[0], var5[1], var5[2]).
							onPlantGrow(worldObj, var5[0], var5[1], var5[2], var1, var2, var4);
				}
			}
		}
	}

	private static final int sinc2(final double x, final double z, final int y)
	{
		final double pi = Math.PI, pi2 = pi / 1.5;
		double r;
		r = Math.sqrt((r=(x/pi))*r+(r=(z/pi))*r)*pi/180;
		if (r == 0) return y;
		return (int)Math.round(y * (((Math.sin(r)/r) + (Math.sin(r*pi2)/(r*pi2)))/2));
	}

	/**
	 * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
	 */
	void generateLeafNodeBases()
	{
		int var1 = 0;
		int var2 = leafNodes.length;

		for (int[] var3 = new int[] {basePos[0], basePos[1], basePos[2]}; var1 < var2; ++var1)
		{
			int[] var4 = leafNodes[var1];
			int[] var5 = new int[] {var4[0], var4[1], var4[2]};
			var3[1] = var4[3];
			int var6 = var3[1] - basePos[1];

			if (this.leafNodeNeedsBase(var6))
			{
				this.placeBlockLine(var3, var5, MineFactoryReloadedCore.rubberWoodBlock, 12 | 1);
			}
		}
	}

	/**
	 * Checks a line of blocks in the world from the first coordinate to triplet to the second, returning the distance
	 * (in blocks) before a non-air, non-leaf block is encountered and/or the end is encountered.
	 */
	private int checkBlockLine(int[] par1, int[] par2)
	{
		int t;
		int[] var3 = new int[] {0, 0, 0};
		byte var4 = 0;
		byte var5;

		for (var5 = 0; var4 < 3; ++var4)
		{
			int a = par2[var4] - par1[var4],
					b = ((t=a>>31)^a)-t;
			var3[var4] = a;
			if (b > ((a=var3[var5])^(t=a>>31))-t)
				var5 = var4;
		}

		if (var3[var5] == 0)
			return -1;
		else
		{
			byte var6 = otherCoordPairs[var5];
			byte var7 = otherCoordPairs[var5 + 3];
			byte var8;

			if (var3[var5] > 0)
			{
				var8 = 1;
			}
			else
			{
				var8 = -1;
			}

			double var9 = (double)var3[var6] / (double)var3[var5];
			double var11 = (double)var3[var7] / (double)var3[var5];
			int[] var13 = new int[] {0, 0, 0};
			int var14 = 0;
			int var15;

			for (var15 = var3[var5] + var8; var14 != var15; var14 += var8)
			{
				var13[var5] = par1[var5] + var14;
				var13[var6] = MathHelper.floor_double(par1[var6] + var14 * var9);
				var13[var7] = MathHelper.floor_double(par1[var7] + var14 * var11);
				int x = var13[0], y = var13[1], z = var13[2];
				Block var16 = worldObj.getBlock(x, y, z);

				if (safeGrowth ? !(var16.isAir(worldObj, x, y, z) ||
						var16.isReplaceable(worldObj, x, y, z) ||
						var16.canBeReplacedByLeaves(worldObj, x, y, z) ||
						var16.isLeaves(worldObj, x, y, z) ||
						var16.isWood(worldObj, x, y, z) ||
						var16 instanceof BlockSapling) :
							var16.equals(Blocks.bedrock))
					break;
			}

			return var14 == var15 ? -1 : ((t=var14>>31)^var14)-t;
		}
	}

	/**
	 * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
	 * limit, is valid.
	 */
	private boolean validTreeLocation()
	{
		int newHeight = Math.min(heightLimit + basePos[1], 255) - basePos[1];
		if (newHeight < minHeight)
			return false;
		heightLimit = newHeight;
		
		Block block = worldObj.getBlock(basePos[0], basePos[1] - 1, basePos[2]);

		if (!block.canSustainPlant(worldObj, basePos[0], basePos[1] - 1, basePos[2],
				ForgeDirection.UP, MineFactoryReloadedCore.rubberSaplingBlock))
			return false;
		else
		{
			int[] var5 = new int[] {basePos[0], basePos[1], basePos[2]};
			int[] var6 = new int[] {basePos[0], basePos[1] + heightLimit - 1, basePos[2]};
			
			newHeight = this.checkBlockLine(var5, var6);
			
			if (newHeight == -1) newHeight = heightLimit;
			if (newHeight < minHeight)
				return false;
			
			heightLimit = Math.min(newHeight, heightLimitLimit);
			height = (int)(heightLimit * heightAttenuation);
			if (height >= heightLimit)
				height = heightLimit - 1;
			height += rand.nextInt(heightLimit - height);
			
			if (safeGrowth)
			{
				int var1 = basePos[0];
				int var2 = basePos[1];
				int var3 = basePos[1] + height;
				int var4 = basePos[2];
	
				var5 = new int[] {var1, var2, var4};
				var6 = new int[] {var1, var3, var4};
				
				double lim = 400f / trunkSize;
				
				for (int i = -trunkSize; i <= trunkSize; i++ )
				{
					var5[0] = var1 + i;
					var6[0] = var1 + i;
	
					for (int j = -trunkSize; j <= trunkSize; j++ )
					{
						if ((j*j + i*i) * 4 < trunkSize * trunkSize * 5)
						{
							var5[2] = var4 + j;
							var6[2] = var4 + j;
							
							if (slopeTrunk)
								var6[1] = var2 + sinc2(lim * i, lim * j, height);
							
							int t = checkBlockLine(var5, var6);
							if (t != -1)
								return false;
						}
					}
				}
			}
			
			return true;
		}
	}

	/**
	 * Rescales the generator settings, only used in WorldGenBigTree
	 */
	@Override
	public void setScale(double par1, double par3, double par5)
	{
		setTreeScale(par1, par3, par5);
	}
	
	public WorldGenMassiveTree setTreeScale(double height, double width, double leaves)
	{
		heightLimitLimit = (int)(height * 12.0D);
		minHeight = heightLimitLimit / 2;
		trunkSize = (int)Math.round((height / 2D));

		if (minHeight > 30)
			leafDistanceLimit = 5;
		else
			leafDistanceLimit = minHeight / 8;

		scaleWidth = width;
		branchDensity = leaves;
		return this;
	}
	
	public WorldGenMassiveTree setMinTrunkSize(int radius)
	{
		trunkSize = Math.max(radius, trunkSize);
		return this;
	}
	
	public WorldGenMassiveTree setLeafAttenuation(double a)
	{
		heightAttenuation = a;
		return this;
	}
	
	public WorldGenMassiveTree setSloped(boolean s)
	{
		slopeTrunk = s;
		return this;
	}
	
	public WorldGenMassiveTree setSafe(boolean s)
	{
		safeGrowth = s;
		return this;
	}

	@Override
	public boolean generate(World world, Random par2Random, int x, int y, int z)
	{
		worldObj = world;
		long var6 = par2Random.nextLong();
		rand.setSeed(var6);
		basePos[0] = x;
		basePos[1] = y;
		basePos[2] = z;

		if (heightLimit == 0)
		{
			heightLimit = heightLimitLimit;
		}
		if (minHeight == -1)
		{
			minHeight = 80;
		}

		if (!this.validTreeLocation())
			return false;
		else
		{
			this.generateLeafNodeList();
			this.generateLeaves();
			this.generateLeafNodeBases();
			this.generateTrunk();
			while (modifiedChunks.size() > 0)
				MineFactoryReloadedCore.proxy.relightChunk(modifiedChunks.pollFirst());
			return true;
		}
	}

	@Override
	public void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block block, int meta)
	{
		if (y < 0 || y > 255)
			return;
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		modifiedChunks.add(chunk);
		chunk.removeTileEntity(x & 15, y, z & 15);
		ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
		ExtendedBlockStorage subChunk = storage[y >> 4];
		if (subChunk == null)
			storage[y >> 4] = subChunk = new ExtendedBlockStorage(y & ~15, !world.provider.hasNoSky);
		subChunk.func_150818_a(x & 15, y & 15, z & 15, block);
		subChunk.setExtBlockMetadata(x & 15, y & 15, z & 15, meta);
		subChunk.setExtBlocklightValue(x & 15, y & 15, z & 15, 0);
	}
}
