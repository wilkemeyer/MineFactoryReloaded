package powercrystals.minefactoryreloaded.core;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class GrindingDamage extends DamageSource
{
	protected int _msgCount;
	protected Random _rand;
	public GrindingDamage()
	{
		this(null, 1);
	}
	
	public GrindingDamage(String type)
	{
		this(type, 1);
	}

	public GrindingDamage(String type, int deathMessages)
	{
		super(type == null ? "mfr.grinder" : type);
		setDamageBypassesArmor();
		setDamageAllowedInCreativeMode();
		_msgCount = Math.max(deathMessages, 1);
		_rand = new Random();
	}
	
	@Override
	public IChatComponent func_151519_b(EntityLivingBase entity)
    {
        EntityLivingBase entityliving1 = entity.func_94060_bK();
        String s = "death.attack." + this.damageType;
        if (_msgCount > 1)
        {
        	int msg = _rand.nextInt(_msgCount);
        	if (msg != 0)
        	{
        		s += "." + msg;
        	}
        }
        String s1 = s + ".player";
        // TODO: change to localized chat message?
        return new ChatComponentText("").appendText(entityliving1 != null && StatCollector.canTranslate(s1) ? StatCollector.translateToLocalFormatted(s1, new Object[] {entity.getCommandSenderName(), entityliving1.getCommandSenderName()}): StatCollector.translateToLocalFormatted(s, new Object[] {entity.getCommandSenderName()}));
    }
}