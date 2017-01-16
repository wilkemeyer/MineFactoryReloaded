package powercrystals.minefactoryreloaded.render;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactoryMachine;

public class MachineStateMapper extends StateMapperBase {

	private static final MachineStateMapper INSTANCE = new MachineStateMapper();

	public static MachineStateMapper getInstance() {
		return INSTANCE;
	}
	
	private MachineStateMapper() {}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		BlockFactoryMachine.Type type = state.getValue(BlockFactoryMachine.TYPE);

		VariantBuilder builder = new VariantBuilder(state);
		builder.add(BlockFactoryMachine.TYPE);

		switch(type) {
			case PLANTER:
				builder.add(BlockFactoryMachine.ACTIVE);
				builder.add(BlockFactoryMachine.CB);
				break;
			case HARVESTER:
			case RANCHER:
			case FERTILIZER:
			case VET:
			case BLOCK_BREAKER:
			case SLUDGE_BOILER:
			case BREEDER:
			case GRINDER:
			case CHRONOTYPER:
				builder.add(BlockFactoryMachine.FACING);
				builder.add(BlockFactoryMachine.ACTIVE);
				break;
			case FISHER:
			case ITEM_COLLECTOR:
			case DEEP_STORAGE_UNIT:
			case LIQUI_CRAFTER:
			case LAVA_FABRICATOR:
			case STEAM_BOILER:
				builder.add(BlockFactoryMachine.ACTIVE);
				break;
			case EJECTOR:
				builder.add(BlockFactoryMachine.FACING);
				break;
			case ITEM_ROUTER:
			case LIQUID_ROUTER:
				builder.add(BlockFactoryMachine.FACING);
				builder.add(BlockFactoryMachine.CB);
				break;
		}

		return new ModelResourceLocation(MineFactoryReloadedCore.modId + ":" + getModelName(type), builder.build());
	}

	public static String getModelName(BlockFactoryMachine.Type type) {

		String model = "machine";

		switch(type) {
			case PLANTER:
				model = "machine_active_cb";
				break;
			case HARVESTER:
			case RANCHER:
			case FERTILIZER:
			case VET:
			case BLOCK_BREAKER:
			case SLUDGE_BOILER:
			case BREEDER:
			case GRINDER:
			case CHRONOTYPER:
				model = "machine_facing_active";
				break;
			case FISHER:
			case ITEM_COLLECTOR:
			case DEEP_STORAGE_UNIT:
			case LIQUI_CRAFTER:
			case LAVA_FABRICATOR:
			case STEAM_BOILER:
				model = "machine_active";
				break;
			case EJECTOR:
				model = "machine_facing";
				break;
			case ITEM_ROUTER:
			case LIQUID_ROUTER:
				model = "machine_facing_cb";
				break;
		}

		return model;
	}

	class VariantBuilder {

		private StringBuilder stateString = new StringBuilder();
		private IBlockState state;

		private VariantBuilder(IBlockState state) {

			this.state = state;
		}

		<T extends Comparable<T>> VariantBuilder add(IProperty<T> property) {

			if (stateString.length() != 0)
			{
				stateString.append(",");
			}

			stateString.append(property.getName());
			stateString.append("=");
			stateString.append(property.getName(state.getValue(property)));

			return this;
		}

		String build() {

			return stateString.toString();
		}
	}
}
