package powercrystals.minefactoryreloaded;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.RenderPlayerEvent.SetArmorModel;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.Point;
import powercrystals.minefactoryreloaded.api.IMobEggHandler;
import powercrystals.minefactoryreloaded.block.BlockRubberLeaves;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.core.MFRDyeColor;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.entity.*;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.render.block.BlockTankRenderer;
import powercrystals.minefactoryreloaded.render.block.PlasticPipeRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityPinkSlimeRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityRocketRenderer;
import powercrystals.minefactoryreloaded.render.entity.RenderSafarinet;
import powercrystals.minefactoryreloaded.render.item.*;
import powercrystals.minefactoryreloaded.render.model.MFRModelLoader;
import powercrystals.minefactoryreloaded.render.model.PlasticCupModel;
import powercrystals.minefactoryreloaded.render.model.SyringeModel;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetLogicRenderer;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.transport.TileEntityConveyor;

import java.util.*;

import static powercrystals.minefactoryreloaded.setup.MFRThings.*;

@SideOnly(Side.CLIENT)
public class MineFactoryReloadedClient implements IResourceManagerReloadListener {

	public static MineFactoryReloadedClient instance;

	private static final ResourceLocation targetingBlue =
			new ResourceLocation(MineFactoryReloadedCore.hudFolder + "lockon_blue.png");
	private static final ResourceLocation targetingRed =
			new ResourceLocation(MineFactoryReloadedCore.hudFolder + "lockon_red.png");
	private static final int _lockonMax = 30;
	private static final int _lockonLostMax = 60;
	private int _lockonTicks = 0;
	private int _lockonLostTicks = 0;
	private Entity _lastEntityOver = null;
	@SuppressWarnings("unused")
	private static boolean gl14 = false;

	public static HashMap<BlockPos, Integer> prcPages = new HashMap<>();

	public static Set<IHarvestAreaContainer> _areaTileEntities = new LinkedHashSet<>();

	private static Random colorRand = new Random();

	public static void preInit() {
		
		//fluids
		ModelHelper.registerModel(MFRThings.milkLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "milk"));
		ModelHelper.registerModel(MFRThings.sludgeLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "sludge"));
		ModelHelper.registerModel(MFRThings.sewageLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "sewage"));
		ModelHelper.registerModel(MFRThings.essenceLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "mob_essence"));
		ModelHelper.registerModel(MFRThings.biofuelLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "biofuel"));
		ModelHelper.registerModel(MFRThings.meatLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "meat"));
		ModelHelper.registerModel(MFRThings.pinkSlimeLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "pink_slime"));
		ModelHelper.registerModel(MFRThings.chocolateMilkLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "chocolate_milk"));
		ModelHelper.registerModel(MFRThings.mushroomSoupLiquid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "mushroom_soup"));
		ModelHelper.registerModel(MFRThings.steamFluid, new ModelResourceLocation(MineFactoryReloadedCore.modId + ":fluid", "steam"));
		ModelHelper.registerModel(MFRThings.milkBottleItem, "milk_bottle");

		//syringes
		ModelHelper.registerModel(MFRThings.syringeEmptyItem, "syringe", "variant=empty");
		ModelHelper.registerModel(MFRThings.syringeHealthItem, "syringe", "variant=health");
		ModelHelper.registerModel(MFRThings.syringeGrowthItem, "syringe", "variant=growth");		
		ModelHelper.registerModel(MFRThings.syringeZombieItem, "syringe", "variant=zombie");
		ModelHelper.registerModel(MFRThings.syringeSlimeItem, "syringe", "variant=slime");
		ModelHelper.registerModel(MFRThings.syringeCureItem, "syringe", "variant=cure");
		MFRModelLoader.registerModel(SyringeModel.MODEL_LOCATION, SyringeModel.MODEL);

		ModelHelper.registerModel(MFRThings.rednetMemoryCardItem, "memory_card");

		ModelHelper.registerModel(MFRThings.factoryHammerItem, "tool", "variant=hammer");
		ModelHelper.registerModel(MFRThings.fishingRodItem, "tool", "variant=fishing_rod");
		ModelHelper.registerModel(MFRThings.rednetMeterItem, "tool", "variant=rednet_meter");
		ModelHelper.registerModel(MFRThings.rednetMeterItem, 1, "tool", "variant=rednet_meter_info");
		ModelHelper.registerModel(MFRThings.rednetMeterItem, 2, "tool", "variant=rednet_meter_debug");
		ModelHelper.registerModel(MFRThings.rulerItem, "tool", "variant=ruler");
		ModelHelper.registerModel(MFRThings.spyglassItem, "tool", "variant=spyglass");
		ModelHelper.registerModel(MFRThings.strawItem, "tool", "variant=straw");

		ModelHelper.registerModel(MFRThings.xpExtractorItem, "xp_extractor_1");

		ModelHelper.registerModel(plasticGlasses, "armor", "type=glass_helm");
		ModelHelper.registerModel(plasticHelmetItem, "armor", "type=helm");
		ModelHelper.registerModel(plasticChestplateItem, "armor", "type=chest");
		ModelHelper.registerModel(plasticLeggingsItem, "armor", "type=legs");
		ModelHelper.registerModel(plasticBootsItem, "armor", "type=boots");

		registerColoredItemModels(MFRThings.ceramicDyeItem, "ceramic_dye");
		registerColoredItemModels(MFRThings.laserFocusItem, "laser_focus");
		
		ModelHelper.registerModel(MFRThings.plasticBagItem, "plastic_bag");
		ModelHelper.registerModel(MFRThings.pinkSlimeItem, "pink_slime", "variant=ball");
		ModelHelper.registerModel(MFRThings.pinkSlimeItem, 1, "pink_slime", "variant=gem");
		ModelHelper.registerModel(MFRThings.portaSpawnerItem, "porta_spawner");
		
		registerSafariNetModel(MFRThings.safariNetItem, "reusable");
		registerSafariNetModel(MFRThings.safariNetJailerItem, "jailer");
		registerSafariNetModel(MFRThings.safariNetSingleItem, "single_use");
		registerSafariNetModel(MFRThings.safariNetFancyJailerItem, "jailer_fancy");
		ModelHelper.registerModel(MFRThings.safariNetLauncherItem, "safari_net_launcher");
		ModelHelper.registerModel(MFRThings.safariNetLauncherItem, 1, "safari_net_launcher");
		
		for(int i : MFRThings.upgradeItem.getMetadataValues()) {
			ModelHelper.registerModel(MFRThings.upgradeItem, i, "upgrade", "variant=" + MFRThings.upgradeItem.getName(i));
		}

		//food
		ModelHelper.registerModel(MFRThings.meatIngotRawItem, "food", "variant=meat_ingot_raw");
		ModelHelper.registerModel(MFRThings.meatIngotCookedItem, "food", "variant=meat_ingot_cooked");
		ModelHelper.registerModel(MFRThings.meatNuggetRawItem, "food", "variant=meat_nugget_raw");
		ModelHelper.registerModel(MFRThings.meatNuggetCookedItem, "food", "variant=meat_nugget_cooked");

		ModelHelper.registerModel(MFRThings.rawRubberItem, "material", "type=rubber_raw");
		ModelHelper.registerModel(MFRThings.rubberBarItem, "material", "type=rubber_bar");
		ModelHelper.registerModel(MFRThings.rawPlasticItem, "material", "type=plastic_raw");
		ModelHelper.registerModel(MFRThings.plasticSheetItem, "material", "type=plastic_sheet");
		ModelHelper.registerModel(MFRThings.sugarCharcoalItem, "material", "type=sugar_charcoal");
		ModelHelper.registerModel(MFRThings.fertilizerItem, "material", "type=fertilizer");
		ModelHelper.registerModel(MFRThings.blankRecordItem, "material", "type=blank_record");
		
		ModelHelper.registerModel(MFRThings.needlegunItem, "needle_gun");
		ModelHelper.registerModel(MFRThings.potatoLauncherItem, "potato_launcher");
		ModelHelper.registerModel(MFRThings.rocketLauncherItem, "rocket_launcher");
		ModelRegistryHelper.register(new ModelResourceLocation(MineFactoryReloadedCore.modId + ":needle_gun", "inventory"), new NeedleGunItemRenderer());
		ModelRegistryHelper.register(new ModelResourceLocation(MineFactoryReloadedCore.modId + ":potato_launcher", "inventory"), new PotatoLauncherItemRenderer());
		ModelRegistryHelper.register(new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rocket_launcher", "inventory"), new RocketLauncherItemRenderer());

		ModelHelper.registerModel(MFRThings.needlegunAmmoAnvilItem, "needle_gun_ammo", "variant=anvil");
		ModelHelper.registerModel(MFRThings.needlegunAmmoEmptyItem, "needle_gun_ammo", "variant=empty");
		ModelHelper.registerModel(MFRThings.needlegunAmmoFireItem, "needle_gun_ammo", "variant=fire");
		ModelHelper.registerModel(MFRThings.needlegunAmmoLavaItem, "needle_gun_ammo", "variant=lava");
		ModelHelper.registerModel(MFRThings.needlegunAmmoPierceItem, "needle_gun_ammo", "variant=pierce");
		ModelHelper.registerModel(MFRThings.needlegunAmmoSewageItem, "needle_gun_ammo", "variant=sewage");
		ModelHelper.registerModel(MFRThings.needlegunAmmoSludgeItem, "needle_gun_ammo", "variant=sludge");
		ModelHelper.registerModel(MFRThings.needlegunAmmoStandardItem, "needle_gun_ammo", "variant=standard");

		ModelHelper.registerModel(rocketItem, "rocket");
		ModelHelper.registerModel(rocketItem, 1, "rocket");
		ModelRegistryHelper.register(new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rocket", "inventory"), new RocketItemRenderer());

		ModelResourceLocation rednetCard = new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rednet_card", "inventory");
		ModelLoader.setCustomMeshDefinition(logicCardItem, stack -> rednetCard);
		ModelLoader.registerItemVariants(logicCardItem, rednetCard);
		ModelRegistryHelper.register(rednetCard, new RedNetCardItemRenderer());
				
		RenderingRegistry.registerEntityRenderingHandler(EntityFishingRod.class,
				manager -> new RenderSnowball<>(manager, fishingRodItem, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySafariNet.class,
				manager -> new RenderSafarinet(manager, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingItem.class,
				manager -> new RenderSafarinet(manager, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, EntityRocketRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPinkSlime.class, 
				manager -> new EntityPinkSlimeRenderer(manager, new ModelSlime(16), 0.25F));
		
		ModelHelper.registerModel(plasticCupItem, "plastic_cup");
		MFRModelLoader.registerModel(PlasticCupModel.MODEL_LOCATION, PlasticCupModel.MODEL);

		ModelLoaderRegistry.registerLoader(MFRModelLoader.INSTANCE);
	}

	private static void registerSafariNetModel(Item item, String variant) {

		//TODO cache values
		ModelResourceLocation empty = new ModelResourceLocation(MineFactoryReloadedCore.modId + ":safari_net", "variant=" + variant + "_empty");
		ModelResourceLocation full = new ModelResourceLocation(MineFactoryReloadedCore.modId + ":safari_net", "variant=" + variant);

		ModelLoader.setCustomMeshDefinition(item, stack -> {
			
			if (ItemSafariNet.isEmpty(stack))
				return empty;
			return full; 
		});
		ModelLoader.registerItemVariants(item, empty, full);
	}
	
	private static void registerColoredItemModels(Item item, String modelName) {
		
		for (MFRDyeColor color : MFRDyeColor.values()) {
			ModelLoader.setCustomModelResourceLocation(item, color.ordinal(), new ModelResourceLocation(MineFactoryReloadedCore.modId + ":" + modelName, "color=" + color.getName()));
		}
	}

	public static void init() {

		instance = new MineFactoryReloadedClient();

		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
		
		itemColors.registerItemColorHandler(new IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {

				if (stack.getItemDamage() == 0 && (stack.getTagCompound() == null)) {
					return 16777215;
				}
				if (stack.getTagCompound() != null && stack.getTagCompound().getBoolean("hide")) {
					World world = Minecraft.getMinecraft().theWorld;
					colorRand.setSeed(world.getSeed() ^ (world.getTotalWorldTime() / (7 * 20)) * tintIndex);
					if (tintIndex == 2)
						return colorRand.nextInt();
					else if (tintIndex == 1)
						return colorRand.nextInt();
					else
						return 16777215;
				}
				EntityList.EntityEggInfo egg = getEgg(stack);

				if (egg == null) {
					return 16777215;
				} else if (tintIndex == 2) {
					return egg.primaryColor;
				} else if (tintIndex == 1) {
					return egg.secondaryColor;
				} else {
					return 16777215;
				}
			}
			
			private EntityList.EntityEggInfo getEgg(ItemStack safariStack) {

				if (safariStack.getTagCompound() == null) {
					return null;
				}

				for (IMobEggHandler handler : MFRRegistry.getModMobEggHandlers()) {
					EntityList.EntityEggInfo egg = handler.getEgg(safariStack);
					if (egg != null) {
						return egg;
					}
				}

				return null;
			}
		}, MFRThings.safariNetFancyJailerItem, MFRThings.safariNetJailerItem, MFRThings.safariNetItem, MFRThings.safariNetSingleItem);

		itemColors.registerItemColorHandler((stack, tintIndex) -> {

			if (tintIndex != 0 || stack.getMetadata() > 15 || stack.getMetadata() < 0)
				return 0xFFFFFF;

			return MFRDyeColor.byMetadata(stack.getMetadata()).getColor();
		}, factoryGlassBlock, factoryGlassPaneBlock);
		
/*

		if (syringeEmptyItem instanceof IFluidContainerItem)
			MinecraftForgeClient.registerItemRenderer(syringeEmptyItem,
				new RenderFluidOverlayItem(false));
		//MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.plasticCellItem.itemID,
		//		new FactoryFluidOverlayRenderer());

*/
		
		MinecraftForge.EVENT_BUS.register(instance);
		gl14 = GLContext.getCapabilities().OpenGL14; //TODO what is this used for? doesn't seem to have anything referring to it

		IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		manager.registerReloadListener(instance);
	}

	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_) {

	}

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre evt) {

		registerFluidSprites(evt.getMap(), milkLiquid.getFluid());
		registerFluidSprites(evt.getMap(), sludgeLiquid.getFluid());
		registerFluidSprites(evt.getMap(), sewageLiquid.getFluid());
		registerFluidSprites(evt.getMap(), essenceLiquid.getFluid());
		registerFluidSprites(evt.getMap(), biofuelLiquid.getFluid());
		registerFluidSprites(evt.getMap(), meatLiquid.getFluid());
		registerFluidSprites(evt.getMap(), pinkSlimeLiquid.getFluid());
		registerFluidSprites(evt.getMap(), chocolateMilkLiquid.getFluid());
		registerFluidSprites(evt.getMap(), mushroomSoupLiquid.getFluid());
		registerFluidSprites(evt.getMap(), steamFluid.getFluid());

		evt.getMap().registerSprite(BlockTankRenderer.BOTTOM_TEXTURE_LOCATION);

/* TODO add code to gen GUI background
		SlotAcceptReusableSafariNet.background = e.map.registerIcon("minefactoryreloaded:gui/reusablenet");
		ContainerAutoDisenchanter.background = e.map.registerIcon("minefactoryreloaded:gui/book");
		ContainerAutoJukebox.background = e.map.registerIcon("minefactoryreloaded:gui/record");
		SlotAcceptLaserFocus.background = e.map.registerIcon("minefactoryreloaded:gui/laserfocus");
		ContainerAutoBrewer.ingredient = e.map.registerIcon("minefactoryreloaded:gui/netherwart");
		ContainerAutoBrewer.bottle = e.map.registerIcon("minefactoryreloaded:gui/bottle");
		ContainerFisher.background = e.map.registerIcon("minefactoryreloaded:gui/fishingrod");
*/
	}

	@SubscribeEvent
	public void onPostTextureStitch(TextureStitchEvent.Post evt) {

		PlasticPipeRenderer.updateUVT(evt.getMap().getAtlasSprite(PlasticPipeRenderer.textureLocation.toString()));
		RedNetLogicRenderer.updateUVT(evt.getMap().getAtlasSprite(RedNetLogicRenderer.textureLocation.toString()));
		BlockTankRenderer.updateSprites(evt.getMap());
	}

	private void registerFluidSprites(TextureMap textureMap, Fluid fluid) {
		if (fluid != null) {
			textureMap.registerSprite(fluid.getStill());
			textureMap.registerSprite(fluid.getFlowing());
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(Unload world) {

		if (world.getWorld().provider == null ||
				Minecraft.getMinecraft().thePlayer == null ||
				Minecraft.getMinecraft().thePlayer.worldObj == null ||
				Minecraft.getMinecraft().thePlayer.worldObj.provider == null) {
			return;
		}
		if (world.getWorld().provider.getDimension() == Minecraft.getMinecraft().thePlayer.worldObj.provider.getDimension()) {
			_areaTileEntities.clear();
			prcPages.clear();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void tickEnd(RenderTickEvent evt) {

		if (evt.phase != Phase.END)
			return;
		renderHUD(evt.renderTickTime);
	}

	@SubscribeEvent
	public void tickStart(PlayerTickEvent evt) {

		if (evt.side != Side.CLIENT | evt.phase != Phase.START)
			return;

		if (holdsRocketLauncher(evt.player)) {
			Entity e = rayTrace();
			if (_lastEntityOver != null && _lastEntityOver.isDead) {
				_lastEntityOver = null;
				_lockonTicks = 0;
			} else if ((e == null || e != _lastEntityOver) && _lockonLostTicks > 0) {
				_lockonLostTicks--;
			} else if (e == null && _lockonLostTicks == 0) {
				_lockonTicks = 0;
				_lastEntityOver = null;
			} else if (_lastEntityOver == null) {
				_lastEntityOver = e;
			} else if (_lockonTicks < _lockonMax) {
				_lockonTicks++;
				if (_lockonTicks >= _lockonMax) {
					_lockonLostTicks = _lockonLostMax;
				}
			} else if (e != null && e == _lastEntityOver) {
				_lockonLostTicks = _lockonLostMax;
			}
		}
	}

	private void renderHUD(float partialTicks) {

		Minecraft mc = Minecraft.getMinecraft();

		if (mc.gameSettings.hideGUI)
			return;

		if (!mc.isGamePaused() && mc.currentScreen == null && holdsRocketLauncher(mc.thePlayer)) {
			ScaledResolution sr = new ScaledResolution(mc);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);

			if (MineFactoryReloadedClient.instance.getLockedEntity() != Integer.MIN_VALUE) {
				mc.renderEngine.bindTexture(targetingBlue);
			} else {
				mc.renderEngine.bindTexture(targetingRed);
			}

			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(center.getX(), center.getY(), 0);
			GL11.glRotatef(((mc.theWorld.getTotalWorldTime() & 511) * 4) % 360 + partialTicks, 0, 0, 1);

			float distance = MineFactoryReloadedClient.instance.getLockTimeRemaining();

			drawLockonPart(center, distance, 0);
			drawLockonPart(center, distance, 90);
			drawLockonPart(center, distance, 180);
			drawLockonPart(center, distance, 270);

			GL11.glPopMatrix();
		}
	}

	private boolean holdsRocketLauncher(EntityPlayer player) {

		return player != null && ((player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == MFRThings.rocketLauncherItem) ||
				(player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == MFRThings.rocketLauncherItem));
	}

	private void drawLockonPart(Point center, float distanceFromCenter, int rotation) {

		GL11.glPushMatrix();

		GL11.glRotatef(rotation, 0, 0, 1);
		GL11.glTranslatef(-8, -13, 0);
		GL11.glTranslatef(0, -distanceFromCenter, 0);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2i(0, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2i(0, 16);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2i(16, 16);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2i(16, 0);
		GL11.glEnd();

		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void setArmorModel(SetArmorModel e) {

/* TODO fix armor model
		ItemStack itemstack = e.getStack();

		if (itemstack != null) {
			Item item = itemstack.getItem();
			int par2 = 3 - e.getSlot();
			//if (item.isValidArmor(itemstack, e.slot, e.entity))
			if (item == plasticCupItem) {
				Minecraft.getMinecraft().renderEngine.
						bindTexture(new ResourceLocation(item.getArmorTexture(itemstack, e.getEntity(), par2, null)));
				ModelBiped modelbiped = new ModelBiped(1.0F);
				modelbiped.bipedHead.showModel = par2 == 0;
				modelbiped.bipedHeadwear.showModel = par2 == 0;
				modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
				modelbiped.bipedRightArm.showModel = par2 == 1;
				modelbiped.bipedLeftArm.showModel = par2 == 1;
				modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
				modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
				e.getRenderer().setRenderPassModel(modelbiped);
				modelbiped.onGround = e.entityLiving.getSwingProgress(e.partialRenderTick);
				modelbiped.isRiding = e.entity.isRiding();
				modelbiped.isChild = e.entityLiving.isChild();
				float f1 = 1.0F;
				GL11.glColor3f(f1, f1, f1);

				if (itemstack.isItemEnchanted()) {
					e.result = 15;
					return;
				}

				e.result = 1;
			}
		}
*/
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	// first to render, so everything else is overlayed
	public void renderWorldLast(RenderWorldLastEvent e) {

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player.inventory.getCurrentItem() == null ||
				!player.inventory.getCurrentItem().getItem().equals(factoryHammerItem)) {
			return;
		}

		float playerOffsetX = -(float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * e.getPartialTicks());
		float playerOffsetY = -(float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * e.getPartialTicks());
		float playerOffsetZ = -(float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * e.getPartialTicks());

		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);

		for (IHarvestAreaContainer c : _areaTileEntities) {
			if (((TileEntity) c).isInvalid())
				continue;

			float r = colorFromCoord(c.getHAM().getOriginX(), 0xF8525888);
			float g = colorFromCoord(c.getHAM().getOriginY(), 0x85BDBD8C);
			float b = colorFromCoord(c.getHAM().getOriginZ(), 0x997696BF);

			GL11.glPushMatrix();
			GL11.glColor4f(r, g, b, 0.4F);
			GL11.glTranslatef(playerOffsetX, playerOffsetY, playerOffsetZ);
			renderAABB(c.getHAM().getHarvestArea().toAxisAlignedBB());
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private float colorFromCoord(int c, long h) {

		h = (h * c) + 0xBA;
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);
		return ((h & 255) / 319f) + 0.1f; // odd values bound to 0.1 <= x < 0.9
	}

	public static void addTileToAreaList(IHarvestAreaContainer tile) {

		_areaTileEntities.add(tile);
	}

	public static void removeTileFromAreaList(IHarvestAreaContainer tile) {

		_areaTileEntities.remove(tile);
	}

	public int getLockedEntity() {

		if (_lastEntityOver != null && _lockonTicks >= _lockonMax) {
			return _lastEntityOver.getEntityId();
		}

		return Integer.MIN_VALUE;
	}

	public int getLockTimeRemaining() {

		if (_lastEntityOver != null && _lockonTicks >= _lockonMax) {
			return _lockonLostMax - _lockonLostTicks;
		} else {
			return (_lockonMax - _lockonTicks) * 2;
		}
	}

	private Entity rayTrace() {

		if (Minecraft.getMinecraft().getRenderViewEntity() == null || Minecraft.getMinecraft().theWorld == null) {
			return null;
		}

		double range = 64;
		Vec3d playerPos = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(1);

		Vec3d playerLook = Minecraft.getMinecraft().getRenderViewEntity().getLook(1);
		Vec3d playerLookRel = playerPos.addVector(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range);
		List<?> list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(
			Minecraft.getMinecraft().getRenderViewEntity(),
			Minecraft.getMinecraft().getRenderViewEntity().getEntityBoundingBox().addCoord(playerLook.xCoord * range, playerLook.yCoord * range,
				playerLook.zCoord * range)
					.expand(1, 1, 1));

		double entityDistTotal = range;
		Entity pointedEntity = null;
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);

			if (entity.canBeCollidedWith()) {
				double entitySize = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(entitySize, entitySize, entitySize);
				RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(playerPos, playerLookRel);

				if (axisalignedbb.isVecInside(playerPos)) {
					if (0.0D < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = 0.0D;
					}
				} else if (movingobjectposition != null) {
					double entityDist = playerPos.distanceTo(movingobjectposition.hitVec);

					if (entityDist < entityDistTotal || entityDistTotal == 0.0D) {
						pointedEntity = entity;
						entityDistTotal = entityDist;
					}
				}
			}
		}

		if (pointedEntity != null) {
			return pointedEntity;
		}
		return null;
	}

	public static void renderAABB(AxisAlignedBB par0AxisAlignedBB) {

		double eps = 0.006;

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION);
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps).endVertex();

		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps).endVertex();

		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps).endVertex();

		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps).endVertex();

		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps).endVertex();

		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		buffer.pos(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps).endVertex();
		tessellator.draw();
	}

}
