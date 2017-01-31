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

public class BlockFactoryGlass extends BlockGlass implements IRedNetDecorative
{
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
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
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		IExtendedBlockState extState = (IExtendedBlockState) state;

		for(EnumFacing facing : EnumFacing.VALUES) {
			extState = extState.withProperty(CTM_VALUE[facing.ordinal()], getCTMValue(facing, world, pos));
		}

		return extState;
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

	private int getCTMValue(EnumFacing side, IBlockAccess world, BlockPos pos) {

		BlockPos posToCheck;
		boolean[] sides = new boolean[8];
		if (side.getAxis() == EnumFacing.Axis.Y)
		{
			posToCheck = pos.offset(EnumFacing.EAST);
			sides[0] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.SOUTH);
			sides[4] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.WEST);
			sides[1] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.WEST);
			sides[5] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.NORTH);
			sides[3] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.NORTH);
			sides[6] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.EAST);
			sides[2] = world.getBlockState(posToCheck).getBlock().equals(this);
			posToCheck = posToCheck.offset(EnumFacing.EAST);
			sides[7] = world.getBlockState(posToCheck).getBlock().equals(this);
		}
		else
		{
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
		}
		return toInt(sides) & 255;
	}

	private static int toInt(boolean ...flags) {
		int ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}
}
