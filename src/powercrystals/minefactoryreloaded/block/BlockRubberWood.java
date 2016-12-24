package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import java.util.ArrayList;
import java.util.List;

public class BlockRubberWood extends BlockLog implements IRedNetDecorative
{
	public BlockRubberWood()
	{
		setUnlocalizedName("mfr.rubberwood.log");
		setCreativeTab(MFRCreativeTab.tab);
		setHarvestLevel("axe", 0);
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		_iconLogSide = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".side");
		_iconLogTop = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getSideIcon(int par1)
	{
		return _iconLogSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getTopIcon(int par1)
	{
		return _iconLogTop;
	}
*/

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		drops.add(new ItemStack(this, 1, 0));
		if((metadata & 3) == 1)
			drops.add(new ItemStack(MFRThings.rawRubberItem,
					fortune <= 0 ? 1 : 1 + world.rand.nextInt(fortune)));

		return drops;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return super.getFireSpreadSpeed(world, x, y, z, face) * ((world.getBlockMetadata(x, y, z) & 3) + 1);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return super.getFlammability(world, x, y, z, face) * ((world.getBlockMetadata(x, y, z) & 3) + 1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item blockId, CreativeTabs tab, List subBlocks)
	{
		subBlocks.add(new ItemStack(blockId, 1, 0));
		subBlocks.add(new ItemStack(blockId, 1, 1));
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}
}
