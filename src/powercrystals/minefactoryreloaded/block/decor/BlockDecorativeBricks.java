package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockDecorativeBricks extends BlockFactory {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	
	public BlockDecorativeBricks() {
		
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setUnlocalizedName("mfr.decorative.brick");
		providesPower = false;
	}

	//TODO likely replace with something better than meta checks - properties
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		
		int meta = getMetaFromState(world.getBlockState(pos));
		return meta == 1 | meta == 7 ? 15 : 0;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		
		int meta = getMetaFromState(world.getBlockState(pos));
		return meta == 3 | meta == 9 ? Blocks.OBSIDIAN.getExplosionResistance(exploder) : getExplosionResistance(exploder);
	}
	public enum Type implements IStringSerializable {
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

		Type(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {
			
			return name;
		}
		
		static {
			NAMES = new String[values().length];
			for (Type type : values()) {
				NAMES[type.meta] = type.name;
			}
		}
	}
/*	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		for(int i = 0; i < _icons.length; i++)
		{
			_icons[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + "." + _names[i]);
		}
	}

	@Override
	public IIcon getIcon(EnumFacing side, int meta)
	{
		return _icons[Math.min(meta, _icons.length - 1)];
	}*/
}
