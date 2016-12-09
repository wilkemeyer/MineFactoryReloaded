package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.block.BlockFactory;

public class BlockDecorativeBricks extends BlockFactory {

	/*	public static final String[] _names = new String [] { "ice", "glowstone", "lapis", "obsidian", "pavedstone", "snow",
		"ice_large", "glowstone_large", "lapis_large", "obsidian_large", "pavedstone_large", "snow_large",
		"meat.raw", "meat.cooked", "brick_large", "sugar_charcoal" };
	private IIcon[] _icons = new IIcon[_names.length];*/

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
	public IIcon getIcon(int side, int meta)
	{
		return _icons[Math.min(meta, _icons.length - 1)];
	}*/
}
