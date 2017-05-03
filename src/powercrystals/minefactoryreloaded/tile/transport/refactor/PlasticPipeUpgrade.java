package powercrystals.minefactoryreloaded.tile.transport.refactor;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public enum PlasticPipeUpgrade {

	NONE(new ItemStack(Blocks.AIR), "none") {
		@Override
		public ItemStack getDrop() {

			return null;
		}
	},
	REDSTONE_TORCH(new ItemStack(Blocks.REDSTONE_TORCH), "chat.info.mfr.fluid.install.torch") {
		@Override
		public boolean getPowered(boolean redstonePowered) {

			return !redstonePowered;
		}
	},
	REDSTONE_BLOCK(new ItemStack(Blocks.REDSTONE_BLOCK), "chat.info.mfr.fluid.install.block") {
		@Override
		public boolean getPowered(boolean redstonePowered) {

			return true;
		}
	};

	private final ItemStack stack;
	private String chatMessageKey;

	PlasticPipeUpgrade(ItemStack stack, String chatMessageKey) {

		this.stack = stack;
		this.chatMessageKey = chatMessageKey;
	}

	public boolean getPowered(boolean redstonePowered) {

		return redstonePowered;
	}

	public ItemStack getDrop() {

		return stack.copy();
	}

	public String getChatMessageKey() {

		return chatMessageKey;
	}

	public static boolean isUpgradeItem(ItemStack stack) {

		for(PlasticPipeUpgrade plasticPipeUpgrade : values()) {
			if (plasticPipeUpgrade.stack.isItemEqual(stack))
				return true;
		}

		return false;
	}

	public static PlasticPipeUpgrade getUpgrade(ItemStack stack) {

		for(PlasticPipeUpgrade plasticPipeUpgrade : values()) {
			if (plasticPipeUpgrade.stack.isItemEqual(stack))
				return plasticPipeUpgrade;
		}

		return NONE;
	}
}
