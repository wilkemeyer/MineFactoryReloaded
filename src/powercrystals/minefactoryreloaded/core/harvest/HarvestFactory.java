package powercrystals.minefactoryreloaded.core.harvest;

import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.HarvestMode;

import java.util.Map;

public class HarvestFactory {

	public static IHarvestManager getHarvestManager(HarvestType harvestType, World world, Area area, HarvestMode harvestMode, Map<String, Boolean> settings) {

		switch (harvestType) {
		case Normal:
		case TreeFruit:
			return SingleBlockHarvestManager.Normal.getInstance();
		case Gourd:
			return SingleBlockHarvestManager.Adjacent.getInstance();
		case Column:
		case LeaveBottom:
			return SingleBlockHarvestManager.Vertical.getInstance();
		case Tree:
		case TreeFlipped:
		case TreeLeaf:
			return new TreeHarvestManager(world, area, harvestMode, settings);
		case Chorus:
			return new ChorusHarvestManager(world, area);
		}
		return null;
	}
}
