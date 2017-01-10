package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockFactoryDecoration extends BlockFactory
{
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	
	public BlockFactoryDecoration() {
		
		super(0.5f);
		setUnlocalizedName("mfr.machineblock");
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

	public enum Variant implements IStringSerializable {
		MACHINE(0, "machine"),
		PRC(1, "prc");

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
