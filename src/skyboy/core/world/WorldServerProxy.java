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
		ReflectionHelper.setPrivateValue(World.class, this, world.loadedEntityList, "field_72996_f", "loadedEntityList");
		ReflectionHelper.setPrivateValue(World.class, this, world.loadedTileEntityList, "field_147482_g", "loadedTileEntityList");
		ReflectionHelper.setPrivateValue(World.class, this, world.playerEntities, "field_73010_i", "playerEntities");
		ReflectionHelper.setPrivateValue(World.class, this, world.weatherEffects, "field_73007_j", "weatherEffects");
		ReflectionHelper.setPrivateValue(World.class, this, world.provider, "field_73011_w", "provider");
		ReflectionHelper.setPrivateValue(World.class, this, world.isRemote, "field_72995_K", "isRemote");
		ReflectionHelper.setPrivateValue(World.class, this, world.rand, "field_75169_l", "rand");
		mapStorage = world.getMapStorage();
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		customTeleporters = world.customTeleporters;
		cofh_updateProps();
	}

	protected void cofh_updateProps() {

		scheduledUpdatesAreImmediate = proxiedWorld.scheduledUpdatesAreImmediate; //as far as I can see in 1.10 it's not important to keep the value in field as immediateBlockTick is used instead
		skylightSubtracted = proxiedWorld.skylightSubtracted;
		prevRainingStrength = proxiedWorld.prevRainingStrength;
		rainingStrength = proxiedWorld.rainingStrength;
		prevThunderingStrength = proxiedWorld.prevThunderingStrength;
		thunderingStrength = proxiedWorld.thunderingStrength;
		getWorldInfo().setDifficulty(proxiedWorld.getDifficulty());
		lastLightningBolt = proxiedWorld.lastLightningBolt;
		allPlayersSleeping = proxiedWorld.allPlayersSleeping;
		//findingSpawnPoint = proxiedWorld.findingSpawnPoint; //protected field that only ever gets assigned to and is not accessed anywhere
		chunkProvider = proxiedWorld.getChunkProvider();
		disableLevelSaving = proxiedWorld.disableLevelSaving;
	}

}
