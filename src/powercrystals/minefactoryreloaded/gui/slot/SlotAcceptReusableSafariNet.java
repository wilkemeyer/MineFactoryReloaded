package powercrystals.minefactoryreloaded.gui.slot;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;

public class SlotAcceptReusableSafariNet extends Slot
{
	public static TextureAtlasSprite background;
	
	public SlotAcceptReusableSafariNet(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}

	@Override
	public TextureAtlasSprite getBackgroundSprite() {
		
		return background;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !ItemSafariNet.isEmpty(stack) && !ItemSafariNet.isSingleUse(stack);
	}
}
