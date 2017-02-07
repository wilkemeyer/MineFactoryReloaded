package powercrystals.minefactoryreloaded.block.decor;

import codechicken.lib.texture.SpriteSheetManager;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BlockFactoryGlass extends BlockGlass implements IRedNetDecorative
{
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class); //TODO move properties to one place
	public static final IUnlistedProperty<Integer>[] CTM_VALUE = new IUnlistedProperty[6];

	static {
		for (int i = 0; i < 6; i++)
			CTM_VALUE[i] = Properties.toUnlisted(PropertyInteger.create("ctm_value_" + i, 0, 255));
	}

	public static final SpriteSheetManager.SpriteSheet spriteSheet = SpriteSheetManager.getSheet(8, 8, new ResourceLocation(MineFactoryReloadedCore.textureFolder + "blocks/tile.mfr.stainedglass.png"));

	static {
		for(int i=0; i < 64; i++) 
			spriteSheet.setupSprite(i);
	}
	
	public static final String[] _names = { "white", "orange", "magenta", "lightblue", "yellow", "lime",
		"pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" }; //TODO change to EnumDyeColor

	public BlockFactoryGlass()
	{
		super(Material.GLASS, false);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		setUnlocalizedName("mfr.stainedglass.block");
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		IExtendedBlockState extState = (IExtendedBlockState) state;

		Map<Integer, Boolean> connections = getConnections(world, pos);

		for(EnumFacing facing : EnumFacing.VALUES) {
			extState = extState.withProperty(CTM_VALUE[facing.ordinal()], getCTMValue(connections, facing));
		}

		return extState;
	}

	private Map<Integer, Boolean> getConnections(IBlockAccess world, BlockPos pos)
	{
		Map<Integer, Boolean> connections = new HashMap<>();

		for(EnumFacing facing : EnumFacing.VALUES) {
			updateSideConnection(world, pos, connections, facing);
		}

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			updateDiagonalConnection(world, pos, connections, facing, EnumFacing.UP, 6);
			updateDiagonalConnection(world, pos, connections, facing, EnumFacing.HORIZONTALS[(facing.getHorizontalIndex() + 1) % 4], 10);
			updateDiagonalConnection(world, pos, connections, facing, EnumFacing.DOWN, 14);
		}

		return connections;
	}

	private void updateDiagonalConnection(IBlockAccess world, BlockPos pos, Map<Integer, Boolean> connections, EnumFacing facing,
			EnumFacing secondFacing, int initialIndex)
	{
		if(connections.get(secondFacing.ordinal()) && connections.get(facing.ordinal())) {
			IBlockState neighborState = world.getBlockState(pos.offset(secondFacing).offset(facing));
			connections.put(initialIndex + facing.getHorizontalIndex(),	neighborState.getBlock() == this);
		} else {
			connections.put(initialIndex + facing.getHorizontalIndex(), false);
		}
	}

	private void updateSideConnection(IBlockAccess world, BlockPos pos, Map<Integer, Boolean> connections, EnumFacing facing)
	{
		IBlockState stateNeigbor = world.getBlockState(pos.offset(facing));
		connections.put(facing.ordinal(), stateNeigbor.getBlock() == this);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		builder.add(COLOR);
		for (int i = 0; i < 6; i++) {
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
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state);
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		boolean r = super.shouldSideBeRendered(state, world, pos, side);
		
		IBlockState neighborState = world.getBlockState(pos.offset(side));
		if (!r) {
			return getMetaFromState(state) != neighborState.getBlock().getMetaFromState(neighborState);
		}
		return r;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		
		return BlockRenderLayer.TRANSLUCENT;
	}

	private int getCTMValue(Map<Integer, Boolean> connections, EnumFacing side)
	{
		boolean[] sides = new boolean[8];
		if (connections.get(side.ordinal())) {
			//connected to stained glass on this side - make it a no overlay texture (only displayed if connected to different glass color)
			Arrays.fill(sides, true);
		}
		else if (side.getAxis() == EnumFacing.Axis.Y)
		{
			sides[0] = connections.get(EnumFacing.EAST.ordinal());
			sides[4] = connections.get(10 + EnumFacing.EAST.getHorizontalIndex()); //SE
			sides[1] = connections.get(EnumFacing.SOUTH.ordinal());
			sides[5] = connections.get(10 + EnumFacing.SOUTH.getHorizontalIndex()); //SW
			sides[3] = connections.get(EnumFacing.WEST.ordinal());
			sides[6] = connections.get(10 + EnumFacing.WEST.getHorizontalIndex()); //NW
			sides[2] = connections.get(EnumFacing.NORTH.ordinal());
			sides[7] = connections.get(10 + EnumFacing.NORTH.getHorizontalIndex()); //NE
		}
		else
		{
			EnumFacing left = EnumFacing.HORIZONTALS[(side.getHorizontalIndex() + 1) % 4];
			EnumFacing right = left.getOpposite();

			sides[0] = connections.get(right.ordinal()); //right
			sides[4] = connections.get(14 + right.getHorizontalIndex()); //right down
			sides[1] = connections.get(EnumFacing.DOWN.ordinal()); //down
			sides[5] = connections.get(14 + left.getHorizontalIndex()); //left down
			sides[3] = connections.get(left.ordinal()); //left
			sides[6] = connections.get(6 + left.getHorizontalIndex()); //left up
			sides[2] = connections.get(EnumFacing.UP.ordinal()); //up
			sides[7] = connections.get(6 + right.getHorizontalIndex()); //right up
		}
		//TODO simplify this so that it directly returns index on sprite sheet
		return toInt(sides) & 255;
	}

	private static int toInt(boolean ...flags) {
		int ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}
}
