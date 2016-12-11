package powercrystals.minefactoryreloaded.block.decor;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.block.BlockFactory;
import powercrystals.minefactoryreloaded.core.UtilInventory;

public class BlockDecorativeStone extends BlockFactory {

/*	public static final String[] _names = new String[] { "black.smooth", "white.smooth", "black.cobble",
			"white.cobble", "black.brick.large", "white.brick.large", "black.brick.small",
			"white.brick.small", "black.gravel", "white.gravel", "black.paved", "white.paved" };
	private IIcon[] _icons = new IIcon[_names.length];*/

	public BlockDecorativeStone() {

		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setUnlocalizedName("mfr.decorative.stone");
		providesPower = false;
	}

/*	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < _icons.length; i++) {
			_icons[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + "." + _names[i]);
		}
	}*/

	@Override
	public int damageDropped(IBlockState state) {

		int meta = getMetaFromState(state);
		if (meta == 0 | meta == 1) {
			meta += 2; // smooth -> cobble
		}
		return meta;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnBlock) {

		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		IBlockState state = world.getBlockState(pos);
		int meta = getMetaFromState(state);
		list.add(new ItemStack(getItemDropped(state, world.rand, 0), quantityDropped(world.rand), meta)); // persist metadata

		world.setBlockToAir(pos);
		if (!returnBlock)
			for (ItemStack item : list) {
				UtilInventory.dropStackInAir(world, pos, item);	
			}
		return list;
	}

/*	@Override
	public IIcon getIcon(int side, int meta) {

		return _icons[Math.min(meta, _icons.length)];
	}*/

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

		world.scheduleBlockUpdate(pos, this, tickRate(world), 1);
	}

	@Override
	public void onNeighborChange(IBlockAccess blockAccess, BlockPos pos, BlockPos neighbor) {

		if (blockAccess instanceof World)
		{
			World world = (World) blockAccess;
			world.scheduleBlockUpdate(pos, this, tickRate(world), 1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {

		if (!world.isRemote) {
			tryToFall(world, pos, state);
		}
	}

	private void tryToFall(World world, BlockPos pos, IBlockState state) {

		int meta = getMetaFromState(state);
		if (meta != 8 & meta != 9)
			return;
		if (BlockFalling.canFallThrough(world.getBlockState(new BlockPos(pos.down()))) && pos.getY() >= 0) {
			if (!BlockSand.fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				if (!world.isRemote) {
					EntityFallingBlock entityfallingsand = new EntityFallingBlock(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, state);
					world.spawnEntityInWorld(entityfallingsand);
				}
			} else {
				world.setBlockToAir(pos);
				BlockPos blockpos;

				for (blockpos = pos.down(); (world.isAirBlock(blockpos) || BlockFalling.canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down())
				{
				}

				if (blockpos.getY() > 0)
				{
					world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
				}
			}
		}
	}

	@Override
	public int tickRate(World world) {

		return 2;
	}

	// TODO: step sounds require forge hook

}
