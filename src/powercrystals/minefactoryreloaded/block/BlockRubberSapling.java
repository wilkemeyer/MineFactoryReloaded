package powercrystals.minefactoryreloaded.block;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.TerrainGen;

import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;
import powercrystals.minefactoryreloaded.world.WorldGenRubberTree;

public class BlockRubberSapling extends BlockSapling implements IRedNetNoConnection {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	private static WorldGenRubberTree treeGen = new WorldGenRubberTree(true);

	public BlockRubberSapling() {

		setHardness(0.0F);
		setSoundType(SoundType.GROUND);
		setUnlocalizedName("mfr.rubberwood.sapling");
		setCreativeTab(MFRCreativeTab.tab);
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {

		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

	@Override
	public IIcon getIcon(EnumFacing side, int metadata) {

		return blockIcon;
	}
*/

	@Override
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {

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
				if (treeGen.growTree(world, rand, pos))
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
}
