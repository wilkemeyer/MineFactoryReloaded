package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.render.ModelHelper;

public class BlockDecorativeBricks extends BlockFactory {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	
	public BlockDecorativeBricks() {
		
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setUnlocalizedName("mfr.decorative.brick");
		providesPower = false;
		setRegistryName(MineFactoryReloadedCore.modId, "brick");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(VARIANT, Variant.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(VARIANT).meta;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		boolean ice = isIce(state);
		return (ice && layer == BlockRenderLayer.TRANSLUCENT) || (!ice && layer == BlockRenderLayer.SOLID);
	}

	private boolean isIce(IBlockState state) {
		
		Variant variant = state.getValue(VARIANT);
		return variant == Variant.ICE || variant == Variant.ICE_LARGE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		
		return !isIce(state);
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
		
		return isIce(state) ? 3 : 255;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {

		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();

		return block == this && isIce(blockState) && isIce(iblockstate) ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		Variant variant = state.getValue(VARIANT);
		return variant == Variant.GLOWSTONE || variant == Variant.GLOWSTONE_LARGE ? 15 : 0;
	}
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {

		Variant variant = world.getBlockState(pos).getValue(VARIANT);
		return variant == Variant.OBSIDIAN || variant == Variant.OBSIDIAN_LARGE ? Blocks.OBSIDIAN.getExplosionResistance(exploder) : getExplosionResistance(exploder);
	}

	@Override
	public boolean preInit() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this, Variant.UNLOC_NAMES));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "variant", Variant.NAMES);
	}

	public enum Variant implements IStringSerializable {

		ICE(0, "ice"),
		GLOWSTONE(1, "glowstone"),
		LAPIS(2, "lapis"),
		OBSIDIAN(3, "obsidian"),
		PAVEDSTONE(4, "pavedstone"),
		SNOW(5, "snow"),
		ICE_LARGE(6, "ice_large"),
		GLOWSTONE_LARGE(7, "glowstone_large"),
		LAPIS_LARGE(8, "lapis_large"),
		OBSIDIAN_LARGE(9, "obsidian_large"),
		PAVEDSTONE_LARGE(10, "pavedstone_large"),
		SNOW_LARGE(11, "snow_large"),
		MEAT_RAW(12, "meat_raw"),
		MEAT_COOKED(13, "meat_cooked"),
		BRICK_LARGE(14, "brick_large"),
		SUGAR_CHARCOAL(15, "sugar_charcoal");

		private final int meta;
		private final String name;

		public static final String[] NAMES;
		public static final String[] UNLOC_NAMES;

		Variant(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {
			
			return name;
		}

		public static Variant byMetadata(int meta) {

			return values()[meta];
		}
		
		static {
			NAMES = new String[values().length];
			UNLOC_NAMES = new String[values().length];
			for (Variant variant : values()) {
				NAMES[variant.meta] = variant.name;
				UNLOC_NAMES[variant.meta] = variant.name.replace("_", "");
			}
		}
	}
}
