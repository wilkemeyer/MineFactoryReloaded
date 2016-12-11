package powercrystals.minefactoryreloaded.block.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockFactoryRail extends BlockRailBase {

	protected boolean canSlope;
	public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.<BlockRailBase.EnumRailDirection>create("shape", BlockRailBase.EnumRailDirection.class);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockFactoryRail(boolean par2, boolean slopes) {

		super(par2);
		setHardness(0.5F);
		setSoundType(SoundType.METAL);
		setCreativeTab(MFRCreativeTab.tab);
		canSlope = slopes;
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {

		return canSlope;
	}

	public boolean isPowered(World world, BlockPos pos) {

		return world.getBlockState(pos).getValue(POWERED);
	}

	@Override
	public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {

		return 0.4f;
	}

	@Override
	// correct argument naming
	public void onMinecartPass(World world, EntityMinecart minecart, BlockPos pos) {

	}

	@Override
	protected void updateState(IBlockState state, World world, BlockPos pos, Block neighor) {

		boolean newPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;
		boolean oldPowered = state.getValue(POWERED);
		if (newPowered != oldPowered) {
			world.setBlockState(pos, state.withProperty(POWERED, newPowered));
		}
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {

		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}

*/
	@Override
	public IProperty<EnumRailDirection> getShapeProperty() {
		return SHAPE;
	}
}
