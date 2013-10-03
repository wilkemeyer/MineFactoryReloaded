package powercrystals.minefactoryreloaded.setup.village;

import java.util.List;
import java.util.Random;

import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureVillagePieceWeight;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageCreationHandler implements IVillageCreationHandler
{
	@Override
	public StructureVillagePieceWeight getVillagePieceWeight(Random random, int i)
	{
		return new StructureVillagePieceWeight(ComponentZoologistHouse.class, 20, random.nextInt(1) + i);
	}
	
	@Override
	public Class<?> getComponentClass()
	{
		// TODO: remove this if in 1.7
		if (new CallableMinecraftVersion(null).minecraftVersion().charAt(4) == '4')
		MapGenStructureIO.func_143031_a(ComponentZoologistHouse.class, "minefactoryreloaded:ZoologistHouseStructure");
		return ComponentZoologistHouse.class;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object buildComponent(StructureVillagePieceWeight villagePiece, ComponentVillageStartPiece startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
	{
		return ComponentZoologistHouse.buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, p4, p5);
	}
}
