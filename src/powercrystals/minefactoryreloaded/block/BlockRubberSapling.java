package powercrystals.minefactoryreloaded.block;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.TerrainGen;

import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

public class BlockRubberSapling extends BlockBush implements IRedNetNoConnection, IGrowable {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	private static WorldGenRubberTree treeGen = new WorldGenRubberTree(true);

	public BlockRubberSapling() {

		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		setUnlocalizedName("mfr.rubberwood.sapling");
		setCreativeTab(MFRCreativeTab.tab);
		this.setDefaultState(blockState.getBaseState().withProperty(TYPE, Type.REGULAR).withProperty(STAGE, 0));
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		
		for (Type type : Type.values())	{
			list.add(new ItemStack(item, 1, type.getMetadata()));
		}
	}

	private void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {

		if (world.isRemote || !TerrainGen.saplingGrowTree(world, rand, pos))
			return;

		Type type = state.getValue(TYPE);
		world.setBlockToAir(pos);

		switch (type) {
			case SACRED_SPRING:
				if (MineFactoryReloadedWorldGen.generateSacredSpringRubberTree(world, rand, pos))
					return;
				break;
			case MEGA:
				if (MineFactoryReloadedWorldGen.generateMegaRubberTree(world, rand, pos, true))
					return;
				break;
			case MASSIVE:
				if (new WorldGenMassiveTree().setSloped(true).generate(world, rand, pos))
					return;
				break;
			default:
			case REGULAR:
				Biome b = world.getBiome(pos);
				if (b != null && b.getBiomeName().toLowerCase(Locale.US).contains("mega"))
					if (rand.nextInt(50) == 0)
						if (MineFactoryReloadedWorldGen.generateMegaRubberTree(world, rand, pos, true))
							return;
				if (treeGen.growTree(world, rand, pos.getX(), pos.getY(), pos.getZ()))
					return;
				break;
		}
		world.setBlockState(pos, getDefaultState().withProperty(TYPE, type), 4);
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(TYPE).getMetadata();
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, Type.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
	}

	public int getMetaFromState(IBlockState state) {
		byte i = 0;
		int i1 = i | (state.getValue(TYPE)).getMetadata();
		i1 |= state.getValue(STAGE) << 3;
		return i1;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, STAGE);
	}
	
	private enum Type implements IStringSerializable {
		
		REGULAR(0, "regular"),
		SACRED_SPRING(1, "sacred_spring"),
		MEGA(2, "mega"),
		MASSIVE(3, "massive");

		private int meta;
		private String name;
		private static final Type[] META_LOOKUP = new Type[values().length];

		Type(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return meta;
		}
		
		public static Type byMetadata(int meta) {

			if(meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		static {
			for(int i = 0; i < values().length; ++i) {
				Type variant = values()[i];
				META_LOOKUP[variant.getMetadata()] = variant;
			}

		}
	}
	
	////// Vanilla BlockSapling fuctionality (because it has call in constructor because of which it can't be inherited) //////
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SAPLING_AABB;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!worldIn.isRemote)
		{
			super.updateTick(worldIn, pos, state, rand);

			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
			{
				this.grow(worldIn, pos, state, rand);
			}
		}
	}

	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (state.getValue(STAGE) == 0)
		{
			worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
		}
		else
		{
			this.generateTree(worldIn, pos, state, rand);
		}
	}

	/**
	 * Whether this IGrowable can grow
	 */
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
	{
		return true;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
	{
		return (double)world.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state)
	{
		this.grow(world, pos, state, rand);
	}
}
