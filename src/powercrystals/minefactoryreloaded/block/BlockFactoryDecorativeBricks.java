package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFactoryDecorativeBricks extends Block implements IRedNetDecorative
{
	static String[] _names = new String [] { "ice", "glowstone", "lapis", "obsidian", "pavedstone", "snow",
			"glowstone_large", "ice_large", "lapis_large", "obsidian_large", "snow_large", "prc", "meat.raw",
			"meat.cooked", "pavedstone_large", "brick_large" };
	private IIcon[] _icons = new IIcon[_names.length];
	
	public BlockFactoryDecorativeBricks()
	{
		super(Material.rock);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(Blocks.stone.stepSound);
		setBlockName("mfr.decorativebrick");
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 1 | meta == 6 ? 15 : 0;
	}
	
	@Override
    public float getExplosionResistance(Entity e, World world, int x, int y, int z, double eX, double eY, double eZ)
    {
		int meta = world.getBlockMetadata(x, y, z);
        return meta == 3 | meta == 9 ? Blocks.obsidian.getExplosionResistance(e) : getExplosionResistance(e);
    }
	
	@SideOnly(Side.CLIENT)
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
	}
}
