package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockFactoryGlassPane extends BlockPane implements IRedNetDecorative
{
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class); //TODO move properties to one place
	public static final IUnlistedProperty<Integer>[] CTM_VALUE = new IUnlistedProperty[4];

	static {
		for (int i = 0; i < 4; i++)
			CTM_VALUE[i] = Properties.toUnlisted(PropertyInteger.create("ctm_value_" + i, 0, 255));
	}

	public BlockFactoryGlassPane()
	{
		this(true);
	}

	public BlockFactoryGlassPane(boolean mfr)
	{
		super(Material.GLASS, false);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		if (mfr)
		{
			setCreativeTab(MFRCreativeTab.tab);
			setUnlocalizedName("mfr.stainedglass.pane");
		}
		else
			setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		IExtendedBlockState extState = (IExtendedBlockState) state;

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			extState = extState.withProperty(CTM_VALUE[facing.getHorizontalIndex()], getCTMValue(facing, world, pos));
		}

		return extState;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		builder.add(COLOR);
		builder.add(NORTH);
		builder.add(SOUTH);
		builder.add(EAST);
		builder.add(WEST);
		for (int i = 0; i < 4; i++) {
			builder.add(CTM_VALUE[i]);
		}
		return builder.build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(COLOR).getMetadata();
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		
		return BlockRenderLayer.TRANSLUCENT;
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir)
	{
		// This space intentionally left blank.
	}

	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public int getRenderColor(int meta)
	{
		return MFRUtil.COLORS[Math.min(Math.max(meta, 0), 15)];
	}

	@Override
	public boolean recolourBlock(World world, BlockPos pos, EnumFacing side, int colour)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta != colour)
		{
			return world.setBlockMetadataWithNotify(x, y, z, colour, 3);
		}
		return false;
	}

	@Override
	public IIcon getIcon(EnumFacing side, int meta)
	{
		meta /= 16;
		switch (meta)
		{
		case 2:
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 0, 0);
		case 1:
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 6, 7);
		case 0:
		default:
			return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 7, 7);
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		BlockPos bp = new BlockPos(x, y, z, EnumFacing.VALID_DIRECTIONS[side]);
		boolean[] sides = new boolean[8];
		bp.moveRight(1);
		sides[0] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveDown(1);
		sides[4] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveLeft(1);
		sides[1] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveLeft(1);
		sides[5] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveUp(1);
		sides[3] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveUp(1);
		sides[6] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveRight(1);
		sides[2] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		bp.moveRight(1);
		sides[7] = world.getBlock(bp.x,bp.y,bp.z).equals(this);
		return new IconOverlay(BlockFactoryGlass._texture, 8, 8, sides);
	}

	@Override
	public IIcon func_150097_e()
	{
		return new IconOverlay(BlockFactoryGlass._texture, 8, 8, 5, 7);
	}
*/

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return !(canPaneConnectTo(world, pos, side) ||
				!super.shouldSideBeRendered(state, world, pos, side));
	}

/*	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdFactoryGlassPane;
	}*/

	private int getCTMValue(EnumFacing side, IBlockAccess world, BlockPos pos) {

		BlockPos posToCheck;
		boolean[] sides = new boolean[8];
		EnumFacing right = side.getAxis() == EnumFacing.Axis.Z ? EnumFacing.VALUES[(side.ordinal() + 2)] : EnumFacing.VALUES[(side.ordinal() - 2) ^ 1];
		EnumFacing left = side.getAxis() == EnumFacing.Axis.Z ? EnumFacing.VALUES[(side.ordinal() + 2) ^ 1] : EnumFacing.VALUES[(side.ordinal() - 2)];

		posToCheck = pos.offset(right);
		sides[0] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(EnumFacing.DOWN);
		sides[4] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(left);
		sides[1] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(left);
		sides[5] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(EnumFacing.UP);
		sides[3] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(EnumFacing.UP);
		sides[6] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(right);
		sides[2] = world.getBlockState(posToCheck).getBlock().equals(this);
		posToCheck = posToCheck.offset(right);
		sides[7] = world.getBlockState(posToCheck).getBlock().equals(this);

		return toInt(sides) & 255;
	}

	private static int toInt(boolean ...flags) {
		int ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}
}
