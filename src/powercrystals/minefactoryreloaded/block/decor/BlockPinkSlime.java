package powercrystals.minefactoryreloaded.block.decor;

import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

import javax.annotation.Nullable;

public class BlockPinkSlime extends BlockBreakable {

	private static final SoundEvent SOUND_BIG = new SoundEvent(new ResourceLocation("minefactoryreloaded:mob.slime.big"));
	private static final SoundEvent SOUND_SMALL = new SoundEvent(new ResourceLocation("minefactoryreloaded:mob.slime.small"));
	private static final SoundType SLIME = new SoundType(1f, 1f, SOUND_BIG, SOUND_BIG, SOUND_SMALL, SOUND_BIG, SOUND_BIG);

	private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.875D, 1D);
	
	public BlockPinkSlime() {

		super(Material.CLAY, false);
		setCreativeTab(MFRCreativeTab.tab);
		setUnlocalizedName("mfr.pinkslime.block");
//		setBlockTextureName("minefactoryreloaded:" + getUnlocalizedName());
		slipperiness = 0.8f;
		setHardness(0.5f);
		setHarvestLevel("shovel", 0);
		setSoundType(SLIME);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		
		return COLLISION_AABB;
	}

	@Override
	public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {

		if (entity.isSneaking())
			super.onFallenUpon(world, pos, entity, fallDistance);
		else {
			entity.fallDistance = 0;
			if (entity.motionY < 0) // FIXME: this has its own method in 1.8 (applies to non-living)
				entity.getEntityData().setDouble("mfr:slime", -entity.motionY);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {

		NBTTagCompound data = entity.getEntityData();
		if (data.hasKey("mfr:slime")) 
		{
			entity.motionY = data.getDouble("mfr:slime");
			data.removeTag("mfr:slime");
		}

		if (Math.abs(entity.motionY) < 0.1 && !entity.isSneaking()) 
		{
			double d = 0.4 + Math.abs(entity.motionY) * 0.2;
			entity.motionX *= d;
			entity.motionZ *= d;
		}
		super.onEntityCollidedWithBlock(world, pos, state, entity);
	}
}
