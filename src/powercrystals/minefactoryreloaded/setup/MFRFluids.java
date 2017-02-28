package powercrystals.minefactoryreloaded.setup;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.FluidHandlerItemStackSimpleSingleFluid;

public class MFRFluids {

	public static final MFRFluids INSTANCE = new MFRFluids();

	private MFRFluids() {
	}

	@SubscribeEvent
	public void onItemStackConstruct(AttachCapabilitiesEvent.Item evt) {

		Item item = evt.getItemStack().getItem();
		if((item == Items.GLASS_BOTTLE && PotionUtils.getEffectsFromStack(evt.getItemStack()).isEmpty()) || item == MFRThings.milkBottleItem) {
			evt.addCapability(new ResourceLocation(MineFactoryReloadedCore.modId + ":milk_bottle_cap"),
					new FluidHandlerItemStackSimpleSingleFluid(new ItemStack(MFRThings.milkBottleItem), new ItemStack(Items.GLASS_BOTTLE),
							FluidRegistry.getFluid("milk"), Fluid.BUCKET_VOLUME));
		} else if(item == Items.BOWL || item == Items.MUSHROOM_STEW) {
			evt.addCapability(new ResourceLocation(MineFactoryReloadedCore.modId + ":mushroom_soup_cap"),
					new FluidHandlerItemStackSimpleSingleFluid(new ItemStack(Items.MUSHROOM_STEW), new ItemStack(Items.BOWL),
							FluidRegistry.getFluid("mushroom_soup"), Fluid.BUCKET_VOLUME));
		}
	}
}
