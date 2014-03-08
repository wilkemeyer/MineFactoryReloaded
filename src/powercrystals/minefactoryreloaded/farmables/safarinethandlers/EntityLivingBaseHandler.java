package powercrystals.minefactoryreloaded.farmables.safarinethandlers;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ISafariNetHandler;
import powercrystals.minefactoryreloaded.core.MFRUtil;

public class EntityLivingBaseHandler implements ISafariNetHandler
{
	@Override
	public Class<?> validFor()
	{
		return EntityLivingBase.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack safariNetStack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		NBTTagCompound tag = safariNetStack.getTagCompound();
		float abs = tag.getFloat("AbsorptionAmount");
		if (abs > 0)
			infoList.add("Absorption: " + abs);

		if (tag.hasKey("HealF"))
			infoList.add("Health: " + (tag.getFloat("HealF")));
		else
			infoList.add("Health: " + (tag.getShort("Health")));

		if (advancedTooltips)
		{
			if (MFRUtil.isShiftKeyDown())
			{
				if (tag.hasKey("ActiveEffects"))
				{
					NBTTagList l = tag.getTagList("ActiveEffects");

					for (int i = 0, e = l.tagCount(); i < e; ++i)
					{
						NBTTagCompound t = (NBTTagCompound)l.tagAt(i);
						PotionEffect f = PotionEffect.readCustomPotionEffectFromNBT(t);
						if (f.getAmplifier() > 0)
						{
							infoList.add("\t" + f.getEffectName() + " x" +
									(f.getAmplifier() + 1) + ", Duration: " +
									f.getDuration());
						}
						else
						{
							infoList.add("\t" + f.getEffectName() +
									", Duration: " + f.getDuration());
						}
					}
				}
			}
			else
				infoList.add(MFRUtil.shiftForInfo());
		}
	}
}
