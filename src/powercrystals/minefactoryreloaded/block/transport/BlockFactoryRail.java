package powercrystals.minefactoryreloaded.block.transport;

import cofh.core.util.core.IInitializer;
import cofh.core.render.IModelRegister;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockFactoryRail extends BlockRailBase implements IInitializer, IModelRegister {

	protected boolean canSlope;
	public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, 
			dir -> dir != EnumRailDirection.NORTH_EAST && dir != EnumRailDirection.NORTH_WEST 
					&& dir != EnumRailDirection.SOUTH_EAST && dir != EnumRailDirection.SOUTH_WEST);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockFactoryRail(boolean par2, boolean slopes) {

		super(par2);
		setHardness(0.5F);
		setSoundType(SoundType.METAL);
		setCreativeTab(MFRCreativeTab.tab);
		canSlope = slopes;
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer(this, SHAPE, POWERED);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, (meta & 8) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(SHAPE).getMetadata() | (state.getValue(POWERED) ? 0 : 8);
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

	@Override
	public IProperty<EnumRailDirection> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public boolean preInit() {

		MFRRegistry.registerBlock(this, new ItemBlock(this));
		return true;
	}

	@Override
	public boolean initialize() {
		
		return true;
	}

	@Override
	public boolean postInit() {
		
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		
	}

	@SideOnly(Side.CLIENT)
	static void registerRailModel(Block railBlock, final String typeVariant) {
		
		ModelLoader.setCustomStateMapper(railBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rail", "shape=" + state.getValue(BlockFactoryRail.SHAPE) + ",type=" + typeVariant);
			}
		});

		Item item = Item.getItemFromBlock(railBlock);
		if (item != null)
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rail_" + typeVariant, "inventory"));
	}
}
