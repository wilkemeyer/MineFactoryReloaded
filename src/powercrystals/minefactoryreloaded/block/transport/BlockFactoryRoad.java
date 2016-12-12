package powercrystals.minefactoryreloaded.block.transport;

import cofh.core.util.CoreUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockFactoryRoad extends Block {

	private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D - 1/128D, 1D);
	private static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	
	public BlockFactoryRoad() {

		super(Material.ROCK);
		setHardness(2.0F);
		setUnlocalizedName("mfr.road");
		setResistance(25.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(MFRCreativeTab.tab);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {

        return COLLISION_AABB;
    }

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity e) {

		if (!e.canTriggerWalking())
			return;
		if (e.getEntityData().getInteger("mfr:r") == e.ticksExisted)
			return;
		e.getEntityData().setInteger("mfr:r", e.ticksExisted);

		final double boost = .99 * slipperiness;
		final double minSpeed = 1e-9;

		if (Math.abs(e.motionX) > minSpeed || Math.abs(e.motionZ) > minSpeed) {
			e.motionX += e.motionX * boost;
			e.motionZ += e.motionZ * boost;
		}
	}

/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {

		_iconRoad = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		_iconRoadOff = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".light.off");
		_iconRoadOn = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".light.on");
	}

	@Override
	public IIcon getIcon(EnumFacing side, int meta) {

		switch (meta) {
		case 1:
		case 3:
			return _iconRoadOff;
		case 2:
		case 4:
			return _iconRoadOn;
		default:
			return _iconRoad;
		}
	}
*/

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {

		if (!world.isRemote) {
			Type type = state.getValue(TYPE);
			boolean isPowered = CoreUtils.isRedstonePowered(world, pos);
			Type newType = null;

			if (type == Type.LIGHT_OFF && isPowered) {
				newType = Type.LIGHT_ON;
			}
			else if (type == Type.LIGHT_ON && !isPowered) {
				newType = Type.LIGHT_OFF;
			}
			else if (type == Type.LIGHT_INVERTED_OFF && !isPowered) {
				newType = Type.LIGHT_INVERTED_ON;
			}
			else if (type == Type.LIGHT_INVERTED_ON && isPowered) {
				newType = Type.LIGHT_INVERTED_OFF;
			}

			if (newType != null) {
				world.setBlockState(pos, state.withProperty(TYPE, newType));
			}
		}
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}

	@Override
	public int damageDropped(IBlockState state) {

		Type type = state.getValue(TYPE);

		switch (type) {
			case LIGHT_OFF:
			case LIGHT_ON:
				return Type.LIGHT_OFF.getMetadata();
			case LIGHT_INVERTED_ON:
			case LIGHT_INVERTED_OFF:
				return Type.LIGHT_INVERTED_ON.getMetadata();
			default:
				return Type.DEFAULT.getMetadata();
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		Type type = state.getValue(TYPE);
		return type == Type.LIGHT_ON || type == Type.LIGHT_INVERTED_ON ? 15 : 0;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

		neighborChanged(state, world, pos, this);
	}

	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {

		if (entity instanceof EntityDragon) {
			return false;
		}

		return true;
	}
	
	public enum Type implements IStringSerializable {
		
		DEFAULT(0, "default"),
		LIGHT_OFF(1, "light_off"),
		LIGHT_ON(2, "light_on"),
		LIGHT_INVERTED_OFF(3, "light_inverted_off"),
		LIGHT_INVERTED_ON(4, "light_inverted_on");

		private final int meta;
		private final String name;

		private static final Type[] META_LOOKUP = new Type[values().length];
		
		Type(int meta, String name) {

			this.meta = meta;
			this.name = name;
		}

		public Type byMetadata(int meta) {
			return META_LOOKUP[meta];
		}
		
		@Override
		public String getName() {
			return name;
		}

		private int getMetadata() {
			
			return meta;
		}

		static {
			for (Type type : values()) {
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}
}
