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
			case FISHER:
			case ITEM_COLLECTOR:
				builder.add(BlockFactoryMachine.ACTIVE);
				break;
		}

		return new ModelResourceLocation(MineFactoryReloadedCore.modId + ":machine", builder.build());
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
