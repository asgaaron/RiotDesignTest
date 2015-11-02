package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import java.util.List;

public class DamageCalculator
{

	private double abilityPower, baseAD, health, cDR, bonusAD;
	List<ChampionSpell> spells;

	DamageCalculator(List<ChampionSpell> spells)
	{
		this.spells = spells;
		abilityPower = 0;
		baseAD = 0;
		health = 0;
		cDR = 0;
		bonusAD = 0;
	}

	Spell calculateSingle()
	{
		ChampionSpell highest = null;
		double highestDamage = 0;
		for (ChampionSpell spell : spells)
		{
			double damage = spell.getEffect().get(1).get(spell.getMaxrank());
			if (null != spell.getVars().get(1).getLink())
				switch (spell.getVars().get(1).getLink())
				{
					case "spelldamage":
						damage += spell.getVars().get(1).getCoeff().get(0) * abilityPower;
						break;
					case "attackdamage":
						damage += spell.getVars().get(1).getCoeff().get(0) * (baseAD + bonusAD);
						break;
					case "bonusattackdamage":
						damage += spell.getVars().get(1).getCoeff().get(0) * bonusAD;
						break;
				}

			if (damage > highestDamage)
			{
				highest = spell;
			}
		}
		Spell spell = new Spell(highest, highestDamage);
		return spell;
	}

	void calculateDPS()
	{
		ChampionSpell highest = null;
		double highestDPS = 0;
		for (ChampionSpell spell : spells)
		{
			double damage = spell.getEffect().get(1).get(spell.getMaxrank());
			if (null != spell.getVars().get(1).getLink())
				switch (spell.getVars().get(1).getLink())
				{
					case "spelldamage":
						damage += spell.getVars().get(1).getCoeff().get(0) * abilityPower;
						break;
					case "attackdamage":
						damage += spell.getVars().get(1).getCoeff().get(0) * (baseAD + bonusAD);
						break;
					case "bonusattackdamage":
						damage += spell.getVars().get(1).getCoeff().get(0) * bonusAD;
						break;
				}

			double damageOverTen = damage * 10 / spell.getCooldown().get(spell.getMaxrank());

			if (damageOverTen > highestDPS)
			{
				highest = spell;
			}
		}
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * @return the abilityPower
	 */
	public double getAbilityPower()
	{
		return abilityPower;
	}

	/**
	 * @param abilityPower the abilityPower to set
	 */
	public void setAbilityPower(double abilityPower)
	{
		this.abilityPower = abilityPower;
	}

	/**
	 * @return the baseAD
	 */
	public double getBaseAD()
	{
		return baseAD;
	}

	/**
	 * @param attackDamage the baseAD to set
	 */
	public void setAttackDamage(double attackDamage)
	{
		this.baseAD = attackDamage;
	}

	/**
	 * @return the health
	 */
	public double getHealth()
	{
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(double health)
	{
		this.health = health;
	}

	/**
	 * @return the cDR
	 */
	public double getcDR()
	{
		return cDR;
	}

	/**
	 * @param cDR the cDR to set
	 */
	public void setcDR(double cDR)
	{
		this.cDR = cDR;
	}

	/**
	 * @return the bonusAD
	 */
	public double getBonusAD()
	{
		return bonusAD;
	}

	/**
	 * @param bonusAD the bonusAD to set
	 */
	public void setBonusAD(double bonusAD)
	{
		this.bonusAD = bonusAD;
	}

	/**
	 * @return total attack damage
	 */
	public double getTotalAD()
	{
		return baseAD + bonusAD;
	}

	private static class Spell
	{
		ChampionSpell spell;
		double damage;

		public Spell(ChampionSpell spell, double damage)
		{
			this.spell = spell;
			this.damage = damage;
		}

		ChampionSpell getSpell()
		{
			return spell;
		}

		void setSpell(ChampionSpell spell)
		{
			this.spell = spell;
		}

		double getDamage()
		{
			return damage;
		}

		void setDamage(double damage)
		{
			this.damage = damage;
		}
	}
}
