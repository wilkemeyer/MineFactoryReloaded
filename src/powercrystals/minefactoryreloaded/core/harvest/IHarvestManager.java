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
	public void moveNext();
	public BlockPos getNextBlock();
	public BlockPos getOrigin();
	public void reset(World world, Area area, HarvestMode harvestMode, Map<String, Boolean> settings);
	public void setWorld(World world);
	public boolean getIsDone();
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
	public void free();
	BlockPos getNextHarvest(BlockPos pos, IFactoryHarvestable harvestable, Map<String, Boolean> settings);
	boolean supportsType(HarvestType type);
}
