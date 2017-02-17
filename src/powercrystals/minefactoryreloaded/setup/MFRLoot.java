package powercrystals.minefactoryreloaded.setup;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

import java.util.List;

public class MFRLoot {

	private static final List<String> CHEST_TABLES = ImmutableList.of("abandoned_mineshaft", "desert_pyramid", "jungle_temple", "jungle_temple_dispenser", "simple_dungeon");

	private static final MFRLoot INSTANCE = new MFRLoot();
	private MFRLoot() {}

	public static void init() {
		for(String s : CHEST_TABLES) {
			LootTableList.register(new ResourceLocation(MineFactoryReloadedCore.modId, "inject/chests/" + s));
		}

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent evt) {
		String chests_prefix = "minecraft:chests/";
		String name = evt.getName().toString();

		if(name.startsWith(chests_prefix) && ((CHEST_TABLES.contains(name.substring(chests_prefix.length()))) ||
				(MFRConfig.enableMassiveTree.getBoolean(true) && "stronghold_library".equals(name.substring(chests_prefix.length()))))) {
			String file = name.substring("minecraft:".length());
			evt.getTable().addPool(getInjectPool(file));
		}
	}

	private LootPool getInjectPool(String entryName) {
		return new LootPool(new LootEntry[] {getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "mfr_inject_pool");
	}

	private LootEntryTable getInjectEntry(String name, int weight) {
		return new LootEntryTable(new ResourceLocation(MineFactoryReloadedCore.modId, "inject/" + name), weight, 0, new LootCondition[0], "mfr_inject_entry");
	}
}
