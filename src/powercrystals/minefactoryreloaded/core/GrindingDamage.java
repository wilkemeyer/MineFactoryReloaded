package powercrystals.minefactoryreloaded.core;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

public class GrindingDamage extends DamageSource {

	protected int _msgCount;
	protected Random _rand;

	public GrindingDamage() {

		this(null, 1);
	}

	public GrindingDamage(String type) {

		this(type, 1);
	}

	public GrindingDamage(String type, int deathMessages) {

		super(type == null ? "mfr.grinder" : type);
		setDamageIsAbsolute();
		setDamageBypassesArmor();
		setDamageAllowedInCreativeMode();
		_msgCount = Math.max(deathMessages, 1);
		_rand = new Random();
	}

	@Override
	public ITextComponent getDeathMessage(EntityLivingBase entity) {

		EntityLivingBase entityliving1 = entity.getAttackingEntity();
		String s = "death.attack." + this.damageType;
		if (_msgCount > 1) {
			int msg = _rand.nextInt(_msgCount);
			if (msg != 0) {
				s += "." + msg;
			}
		}
		String s1 = s + ".player";
		if (entityliving1 != null && I18n.canTranslate(s1))
			return new TextComponentTranslation(s1, entity.getName(), entityliving1.getName());
		return new TextComponentTranslation(s, entity.getName());
	}
}
