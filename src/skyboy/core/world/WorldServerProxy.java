package skyboy.core.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class WorldServerProxy extends WorldServerShim {

	protected WorldServer proxiedWorld;

	private static String getWorldName(World world) {

		return world.getWorldInfo().getWorldName();
	}

	private static WorldSettings getWorldSettings(World world) {

		return new WorldSettings(world.getWorldInfo());
	}

	public WorldServerProxy(WorldServer world) {

		super(world.getMinecraftServer(), world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler);
		this.proxiedWorld = world;
		// perWorldStorage = world.perWorldStorage; // final, set in super; requires reflection
		ReflectionHelper.setPrivateValue(World.class, this, world.getPerWorldStorage(), "perWorldStorage"); // forge-added, no reobf
		loadedEntityList = world.loadedEntityList;
		loadedTileEntityList = world.loadedTileEntityList;
		playerEntities = world.playerEntities;
		weatherEffects = world.weatherEffects;
		rand = world.rand;
		// provider = world.provider; // handled by super
		mapStorage = world.getMapStorage();
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		isRemote = world.isRemote;
		customTeleporters = world.customTeleporters;
		cofh_updateProps();
	}

	protected void cofh_updateProps() {

		//scheduledUpdatesAreImmediate = proxiedWorld.scheduledUpdatesAreImmediate; //as far as I can see in 1.10 it's not important to keep the value in field
		skylightSubtracted = proxiedWorld.getSkylightSubtracted();
		prevRainingStrength = proxiedWorld.prevRainingStrength;
		rainingStrength = proxiedWorld.rainingStrength;
		prevThunderingStrength = proxiedWorld.prevThunderingStrength;
		thunderingStrength = proxiedWorld.thunderingStrength;
		lastLightningBolt = proxiedWorld.getLastLightningBolt();
		difficulty = proxiedWorld.getDifficulty();
		findingSpawnPoint = proxiedWorld.findingSpawnPoint;
		theChunkProviderServer = proxiedWorld.theChunkProviderServer;
		allPlayersSleeping = proxiedWorld.allPlayersSleeping;
		levelSaving = proxiedWorld.levelSaving;
	}

}
