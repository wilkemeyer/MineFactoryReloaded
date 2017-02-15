package powercrystals.minefactoryreloaded.item.gun;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.item.base.ItemFactoryGun;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.entity.EntityRocketRenderer;
import powercrystals.minefactoryreloaded.render.item.RocketLauncherItemRenderer;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class ItemRocketLauncher extends ItemFactoryGun {

	public ItemRocketLauncher() {

		setUnlocalizedName("mfr.rocketlauncher");
		setMaxStackSize(1);
	}

	@Override
	protected boolean hasGUI(ItemStack stack) {
		return false;
	}

	@Override
	protected boolean fire(ItemStack stack, World world, EntityPlayer player) {
		int slot = -1;
		Item rocket = MFRThings.rocketItem;
		ItemStack[] mainInventory = player.inventory.mainInventory;
		for (int j = 0, e = mainInventory.length; j < e; ++j)
			if (mainInventory[j] != null && mainInventory[j].getItem() == rocket) {
				slot = j;
				break;
			}
		if (slot > 0) {
			int damage = mainInventory[slot].getItemDamage();
			if (!player.capabilities.isCreativeMode)
				if (--mainInventory[slot].stackSize <= 0)
					mainInventory[slot] = null;

			if (world.isRemote) {
				MFRPacket.sendRocketLaunchToServer(player.getEntityId(), 
						damage == 0 ? MineFactoryReloadedClient.instance.getLockedEntity() : Integer.MIN_VALUE);
			} else if (!player.addedToChunk) {
				EntityRocket r = new EntityRocket(world, player, null);
				world.spawnEntityInWorld(r);
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getDelay(ItemStack stack, boolean fired) {
		return fired ? 100 : 40;
	}

	@Override
	protected String getDelayTag(ItemStack stack) {
		return "mfr:SPAMRLaunched";
	}

	@Override
	public boolean preInit() {

		super.preInit();
		EntityRegistry.registerModEntity(EntityRocket.class, "Rocket", 3, MineFactoryReloadedCore.instance(), 160, 1, true);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this, "rocket_launcher");
		ModelRegistryHelper.register(new ModelResourceLocation(MineFactoryReloadedCore.modId + ":rocket_launcher", "inventory"), new RocketLauncherItemRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, EntityRocketRenderer::new);
	}
}
