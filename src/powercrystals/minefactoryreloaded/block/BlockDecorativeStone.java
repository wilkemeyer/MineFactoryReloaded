package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.rednet.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockDecorativeStone extends Block implements IRedNetDecorative
{
	static String[] _names = new String [] { "black.smooth", "white.smooth", "black.cobble",
			"white.cobble", "black.brick.large", "white.brick.large", "black.brick.small",
			"white.brick.small", "black.gravel", "white.gravel", "black.paved", "white.paved" };
	private IIcon[] _icons = new IIcon[_names.length];
	
	public BlockDecorativeStone()
	{
		super(Material.rock);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(Blocks.stone.stepSound);
		setBlockName("mfr.decorativestone");
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
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
		return _icons[Math.min(meta, _icons.length)];
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
	{
		par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
	}

	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		if (!par1World.isRemote)
		{
			this.tryToFall(par1World, par2, par3, par4);
		}
	}
	
	private void tryToFall(World par1World, int par2, int par3, int par4)
	{
		int meta = par1World.getBlockMetadata(par2, par3, par4);
		if (meta != 8 & meta != 9)
			return;
		if (BlockFalling.func_149831_e(par1World, par2, par3 - 1, par4) && par3 >= 0)
		{
			byte b0 = 32;

			if (!BlockSand.fallInstantly && par1World.checkChunksExist(par2 - b0, par3 - b0, par4 - b0, par2 + b0, par3 + b0, par4 + b0))
			{
				if (!par1World.isRemote)
				{
					EntityFallingBlock entityfallingsand = new EntityFallingBlock(par1World, par2 + 0.5d, par3 + 0.5d, par4 + 0.5d, this, meta);
					par1World.spawnEntityInWorld(entityfallingsand);
				}
			}
			else
			{
				par1World.setBlockToAir(par2, par3, par4);

				while (BlockFalling.func_149831_e(par1World, par2, par3 - 1, par4) && par3 > 0)
				{
					--par3;
				}

				if (par3 > 0)
				{
					par1World.setBlock(par2, par3, par4, this, meta, 3);
				}
			}
		}
	}

	@Override
	public int tickRate(World par1World)
	{
		return 2;
	}

	/* TODO: step sounds for gravel?
	protected void updateStepSound(int meta)
	{
		switch(meta)
		{
		case 8:
		case 9:
			setStepSound(soundGravelFootstep);
			break;
		default:
			setStepSound(soundStoneFootstep);
		}
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta)
	{
		updateStepSound(meta);
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float distance)
	{
		updateStepSound(world.getBlockMetadata(x, y, z));
	}

	public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		// called after entity plays step sound
	}
	//*/
}
