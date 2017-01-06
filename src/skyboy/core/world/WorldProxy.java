package skyboy.core.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class WorldProxy extends World {

	protected World proxiedWorld;

	private static String getWorldName(World world) {

		return world.getWorldInfo().getWorldName();
	}

	private static WorldSettings getWorldSettings(World world) {

		return new WorldSettings(world.getWorldInfo());
	}

	public WorldProxy(World world) {

		super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler, world.isRemote);
		this.proxiedWorld = world;
		// perWorldStorage = world.perWorldStorage; // final, set in super; requires reflection
		ReflectionHelper.setPrivateValue(World.class, this, world.getPerWorldStorage(), new String[] { "perWorldStorage" }); // forge-added, no reobf
		scheduledUpdatesAreImmediate = world.scheduledUpdatesAreImmediate;
		ReflectionHelper.setPrivateValue(World.class, this, world.loadedEntityList, "field_72996_f", "loadedEntityList");
		ReflectionHelper.setPrivateValue(World.class, this, world.loadedTileEntityList, "field_147482_g", "loadedTileEntityList");
		ReflectionHelper.setPrivateValue(World.class, this, world.playerEntities, "field_73010_i", "playerEntities");
		ReflectionHelper.setPrivateValue(World.class, this, world.weatherEffects, "field_73007_j", "weatherEffects");
		skylightSubtracted = world.skylightSubtracted;
		prevRainingStrength = world.prevRainingStrength;
		rainingStrength = world.rainingStrength;
		prevThunderingStrength = world.prevThunderingStrength;
		thunderingStrength = world.thunderingStrength;
		lastLightningBolt = world.lastLightningBolt;
		getWorldInfo().setDifficulty(proxiedWorld.getDifficulty());
		ReflectionHelper.setPrivateValue(World.class, this, world.rand, "field_75169_l", "rand");
		ReflectionHelper.setPrivateValue(World.class, this, world.provider, "field_73011_w", "provider");
		//findingSpawnPoint = proxiedWorld.findingSpawnPoint; //protected field that only ever gets assigned to and is not accessed anywhere
		mapStorage = world.getMapStorage();
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		ReflectionHelper.setPrivateValue(World.class, this, world.isRemote, "field_72995_K", "isRemote");
	}

	@Override
	public IChunkProvider createChunkProvider() {

		return null;
	}

	@Override
	public Entity getEntityByID(int id) {

		return null;
	}
}
