package powercrystals.minefactoryreloaded.setup;

import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ExistingSubstitutionException;
import net.minecraftforge.fml.common.registry.GameRegistry;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.fluid.BlockExplodingFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockFactoryFluid;
import powercrystals.minefactoryreloaded.block.fluid.BlockPinkSlimeFluid;
import powercrystals.minefactoryreloaded.core.FluidHandlerItemStackSimpleSingleFluid;
import powercrystals.minefactoryreloaded.item.ItemMFRBucketMilk;

import java.util.Locale;

public class MFRFluids {

	public static final MFRFluids INSTANCE = new MFRFluids();

	public static Fluid milk;
	public static Fluid sludge;
	public static Fluid sewage;
	public static Fluid essence;
	public static Fluid biofuel;
	public static Fluid meat;
	public static Fluid pinkSlime;
	public static Fluid chocolateMilk;
	public static Fluid mushroomSoup;
	public static Fluid steam;
	public static BlockFactoryFluid milkLiquid;
	public static BlockFactoryFluid sludgeLiquid;
	public static BlockFactoryFluid sewageLiquid;
	public static BlockFactoryFluid essenceLiquid;
	public static BlockFactoryFluid biofuelLiquid;
	public static BlockFactoryFluid meatLiquid;
	public static BlockFactoryFluid pinkSlimeLiquid;
	public static BlockFactoryFluid chocolateMilkLiquid;
	public static BlockFactoryFluid mushroomSoupLiquid;
	public static BlockFactoryFluid steamFluid;

	private MFRFluids() {

	}

	public static void preInit() {

		milk = registerFluid("milk", 1050, EnumRarity.COMMON);
		sludge = registerFluid("sludge", 1700, EnumRarity.COMMON);
		sewage = registerFluid("sewage", 1200, EnumRarity.COMMON);
		essence = registerFluid("mob_essence", 400, 9, 310, EnumRarity.EPIC);
		biofuel = registerFluid("biofuel", 800, EnumRarity.UNCOMMON);
		meat = registerFluid("meat", 2000, EnumRarity.COMMON);
		pinkSlime = registerFluid("pink_slime", 3000, EnumRarity.RARE);
		chocolateMilk = registerFluid("chocolate_milk", 1100, EnumRarity.COMMON);
		mushroomSoup = registerFluid("mushroom_soup", 1500, EnumRarity.COMMON);
		steam = registerFluid("steam", -100, 0, 673, EnumRarity.COMMON);

		FluidRegistry.addBucketForFluid(milk);
		FluidRegistry.addBucketForFluid(sludge);
		FluidRegistry.addBucketForFluid(sewage);
		FluidRegistry.addBucketForFluid(essence);
		FluidRegistry.addBucketForFluid(biofuel);
		FluidRegistry.addBucketForFluid(meat);
		FluidRegistry.addBucketForFluid(pinkSlime);
		FluidRegistry.addBucketForFluid(chocolateMilk);
		FluidRegistry.addBucketForFluid(mushroomSoup);

		milkLiquid = new BlockFactoryFluid("milk");
		sludgeLiquid = new BlockFactoryFluid("sludge");
		sewageLiquid = new BlockFactoryFluid("sewage");
		essenceLiquid = new BlockFactoryFluid("mob_essence");
		biofuelLiquid = new BlockExplodingFluid("biofuel");
		meatLiquid = new BlockFactoryFluid("meat");
		pinkSlimeLiquid = new BlockPinkSlimeFluid("pink_slime");
		chocolateMilkLiquid = new BlockFactoryFluid("chocolate_milk");
		mushroomSoupLiquid = new BlockFactoryFluid("mushroom_soup");
		steamFluid = new BlockFactoryFluid("steam", BlockFactoryFluid.material);

		MFRRegistry.registerBlock(milkLiquid, new ItemBlock(milkLiquid));
		MFRRegistry.registerBlock(sludgeLiquid, new ItemBlock(sludgeLiquid));
		MFRRegistry.registerBlock(sewageLiquid, new ItemBlock(sewageLiquid));
		MFRRegistry.registerBlock(essenceLiquid, new ItemBlock(essenceLiquid));
		MFRRegistry.registerBlock(biofuelLiquid, new ItemBlock(biofuelLiquid));
		MFRRegistry.registerBlock(meatLiquid, new ItemBlock(meatLiquid));
		MFRRegistry.registerBlock(pinkSlimeLiquid, new ItemBlock(pinkSlimeLiquid));
		MFRRegistry.registerBlock(chocolateMilkLiquid, new ItemBlock(chocolateMilkLiquid));
		MFRRegistry.registerBlock(mushroomSoupLiquid, new ItemBlock(mushroomSoupLiquid));
		MFRRegistry.registerBlock(steamFluid, new ItemBlock(steamFluid));

		if (MFRConfig.vanillaOverrideMilkBucket.getBoolean(true)) {
			try {
				GameRegistry.addSubstitutionAlias("minecraft:milk_bucket", GameRegistry.Type.ITEM, new ItemMFRBucketMilk());
			} catch (ExistingSubstitutionException e) {
				MineFactoryReloadedCore.log().error("Failed replacing milk bucket. Another mod must have already replaced it");
			}
		}
	}

	public static Fluid registerFluid(String name, int density, EnumRarity rarity) {

		return registerFluid(name, density, -1, -1, rarity);
	}

	public static Fluid registerFluid(String name, int density, int lightValue, int temp, EnumRarity rarity) {

		name = name.toLowerCase(Locale.ENGLISH);
		Fluid fluid = new Fluid(name, new ResourceLocation("minefactoryreloaded:blocks/fluid/fluid.mfr." + name + ".still"), new ResourceLocation("minefactoryreloaded:blocks/fluid/fluid.mfr." + name + ".flowing"));
		if (!FluidRegistry.registerFluid(fluid))
			fluid = FluidRegistry.getFluid(name);
		if (density != 0) {
			fluid.setDensity(density);
			fluid.setViscosity(Math.abs(density)); // works for my purposes
		}
		if (lightValue >= 0)
			fluid.setLuminosity(lightValue);
		if (temp >= 0)
			fluid.setTemperature(temp);
		fluid.setUnlocalizedName("mfr." + name + ".still.name");
		fluid.setRarity(rarity);
		return fluid;
	}

	private static final ItemStack MILK_BOTTLE = new ItemStack(MFRThings.milkBottleItem);
	private static final ItemStack GLASS_BOTTLE = new ItemStack(Items.GLASS_BOTTLE);
	private static final ItemStack MUSHROOM_STEW = new ItemStack(Items.MUSHROOM_STEW);
	private static final ItemStack BOWL = new ItemStack(Items.BOWL);

	@SubscribeEvent
	public void onItemStackConstruct(AttachCapabilitiesEvent.Item evt) {

		Item item = evt.getItem();
		if ((item == Items.GLASS_BOTTLE && PotionUtils.getEffectsFromStack(evt.getItemStack()).isEmpty()) ||
				item == MFRThings.milkBottleItem) {
			evt.addCapability(new ResourceLocation(MineFactoryReloadedCore.modId + ":milk_bottle_cap"),
					new FluidHandlerItemStackSimpleSingleFluid(evt.getItemStack(), MILK_BOTTLE, GLASS_BOTTLE,
							FluidRegistry.getFluid("milk"), Fluid.BUCKET_VOLUME));
		} else if (item == Items.BOWL || item == Items.MUSHROOM_STEW) {
			evt.addCapability(new ResourceLocation(MineFactoryReloadedCore.modId + ":mushroom_soup_cap"),
					new FluidHandlerItemStackSimpleSingleFluid(evt.getItemStack(), MUSHROOM_STEW, BOWL,
							FluidRegistry.getFluid("mushroom_soup"), Fluid.BUCKET_VOLUME));
		}
	}
}
