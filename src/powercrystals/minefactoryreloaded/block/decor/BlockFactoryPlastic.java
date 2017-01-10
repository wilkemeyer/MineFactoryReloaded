package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockFactoryPlastic extends BlockFactory {

	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockFactoryPlastic() {

		super(0.3f);
		slipperiness = 1f / 0.9801f;
		setUnlocalizedName("mfr.plastic");
		setHarvestLevel("axe", 0);
		providesPower = false;
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

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < _icons.length; i++) {
			String name = getName(unlocalizedName, _names[i]);
			_icons[i] = ir.registerIcon("minefactoryreloaded:tile." + name);
		}
	}

	@Override
	public IIcon getIcon(EnumFacing side, int meta) {

		if (side < 2 & meta == 2) {
			--meta;
		}
		return _icons[Math.min(meta, _icons.length - 1)];
	}
*/
	public enum Variant implements IStringSerializable {

		REGULAR(0, "regular"),
		PRC(1, "paver"),
		COLUMN(2, "column"),
		BRICKS_LARGE(3, "bricks_large"),
		CHISELED(4, "chiseled"),
		ROAD(5, "road"),
		BRICKS(6, "bricks");

		private final int meta;
		private final String name;

		public static final String[] NAMES;

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
			for (Variant variant : values()) {
				NAMES[variant.meta] = variant.name;
			}
		}
	}

}
