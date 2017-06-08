package powercrystals.minefactoryreloaded.core.harvest;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.HarvestMode;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

import java.util.Map;

public abstract class SingleBlockHarvestManager implements IHarvestManager {

	protected World world;

	public void moveNext() {

		//noop
	}

	public BlockPos getNextBlock() {

		//noop
		return null;
	}

	public BlockPos getOrigin() {

		//noop
		return null;
	}

	public void reset(World world, Area area, HarvestMode harvestMode, Map<String, Boolean> settings) {

		//noop
	}

	public void setWorld(World world) {

		this.world = world;
	}

	public boolean getIsDone() {

		return true;
	}

	public void writeToNBT(NBTTagCompound tag) {

		//noop
	}

	public void readFromNBT(NBTTagCompound tag) {

		//noop
	}

	public void free() {

		//noop
	}

	@Override
	public abstract BlockPos getNextHarvest(BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings);

	public static class Adjacent extends SingleBlockHarvestManager {

		private static final Adjacent INSTANCE = new Adjacent();

		private Adjacent() {}

		public static Adjacent getInstance() {

			return INSTANCE;
		}

		@Override
		public BlockPos getNextHarvest(BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings) {

			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.offset(side);
				if (world.isBlockLoaded(offsetPos) && harvestable.canBeHarvested(world, settings, offsetPos))
					return offsetPos;
			}
			return null;
		}

		@Override
		public boolean supportsType(HarvestType type) {

			return type == HarvestType.Gourd;
		}
	}

	public static class Vertical extends SingleBlockHarvestManager {

		private static final Vertical INSTANCE = new Vertical();

		private Vertical() {}

		public static Vertical getInstance() {

			return INSTANCE;
		}

		@Override
		public BlockPos getNextHarvest(BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings) {

			int highestBlockOffset = -1;
			int maxBlockOffset = MFRConfig.verticalHarvestSearchMaxVertical.getInt();

			Block plant = harvestable.getPlant();
			int startOffset = harvestable.getHarvestType() == HarvestType.Column ? 0 : 1;
			for (int currentYoffset =  startOffset; currentYoffset < maxBlockOffset; ++currentYoffset) {
				BlockPos offsetPos = pos.offset(EnumFacing.UP, currentYoffset);
				Block block = world.getBlockState(offsetPos).getBlock();
				if (!block.equals(plant) ||
						!harvestable.canBeHarvested(world, settings, offsetPos))
					break;

				highestBlockOffset = currentYoffset;
			}

			if (highestBlockOffset >= 0)
				return pos.offset(EnumFacing.UP, highestBlockOffset);

			return null;
		}

		@Override
		public boolean supportsType(HarvestType type) {

			return type == HarvestType.Column || type == HarvestType.LeaveBottom;
		}
	}

	public static class Normal extends SingleBlockHarvestManager {

		private static final Normal INSTANCE = new Normal();

		private Normal() {}

		public static Normal getInstance() {

			return INSTANCE;
		}

		@Override
		public BlockPos getNextHarvest(BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings) {

			return pos;
		}

		@Override
		public boolean supportsType(HarvestType type) {

			return type == HarvestType.Normal || type == HarvestType.TreeFruit;
		}
	}
}
