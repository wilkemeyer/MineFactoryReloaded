package powercrystals.minefactoryreloaded.core.harvest;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.HarvestMode;

public interface IHarvestManager
{
	BlockPos getOrigin();
	void writeToNBT(NBTTagCompound tag);
	void readFromNBT(NBTTagCompound tag);
	void free();
	BlockPos getNextHarvest(World world, BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings);
	boolean supportsType(HarvestType type);
}
