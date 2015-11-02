package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import com.robrua.orianna.type.dto.staticdata.SpellVars;
import java.io.IOException;
import java.util.List;

public class DamageCalculator {

    private double abilityPower, baseAD, health, cDR, bonusAD;
    List<ChampionSpell> spells;

    DamageCalculator(List<ChampionSpell> spells) {
        this.spells = spells;
        abilityPower = 0;
        baseAD = 0;
        health = 0;
        cDR = 0;
        bonusAD = 0;
    }

    /**
     * Function that calculates the highest damage single cast spell at the
     * given levels of AD and AP
     *
     * @return Spell spell - a wrapper containing the ChampionSpell and its
     * damage
     * @throws IOException
     */
    Spell calculateSingle() throws IOException {
        ChampionSpell highest = null;
        double highestDamage = 0;
        for (ChampionSpell spell : spells) {
            double damage = 0;
            if (null == spell.getEffect()) {
                System.out.println("Uh oh, it looks like " + spell.getName() + " doesn't have an effect field...results may be skewed!");
            } else if (spell.getEffect().get(1) == null) {
                System.out.println("This effect list is blank, add it to the problem spells!");
            } else {
                damage = spell.getEffect().get(1).get(spell.getMaxrank() - 1);
            }
            if (null != spell.getVars()) {
                for (SpellVars var : spell.getVars()) {
                    switch (var.getLink()) {
                        case "spelldamage":
                            damage += spell.getVars().get(0).getCoeff().get(0) * abilityPower;
                            break;
                        case "attackdamage":
                            damage += spell.getVars().get(0).getCoeff().get(0) * (baseAD + bonusAD);
                            break;
                        case "bonusattackdamage":
                            damage += spell.getVars().get(0).getCoeff().get(0) * bonusAD;
                            break;
                    }
                }
            }

            if (damage > highestDamage) {
                highestDamage = damage;
                highest = spell;
            }
        }
        Spell spell = new Spell(highest, highestDamage);
        return spell;
    }

    Spell calculateDPS() {
        ChampionSpell highest = null;
        double highestDPS = 0;
        for (ChampionSpell spell : spells) {
            double damage = spell.getEffect().get(0).get(spell.getMaxrank() - 1);
            if (null != spell.getVars().get(0).getLink()) {
                switch (spell.getVars().get(0).getLink()) {
                    case "spelldamage":
                        damage += spell.getVars().get(0).getCoeff().get(0) * abilityPower;
                        break;
                    case "attackdamage":
                        damage += spell.getVars().get(0).getCoeff().get(0) * (baseAD + bonusAD);
                        break;
                    case "bonusattackdamage":
                        damage += spell.getVars().get(0).getCoeff().get(0) * bonusAD;
                        break;
                }
            }

            double damagePerTen = damage * 10 / spell.getCooldown().get(spell.getMaxrank());

            if (damagePerTen > highestDPS) {
                highestDPS = damagePerTen;
                highest = spell;
            }
        }
        Spell spell = new Spell(highest, highestDPS);
        return spell;
    }

    /**
     * @return the abilityPower
     */
    public double getAbilityPower() {
        return abilityPower;
    }

    /**
     * @param abilityPower the abilityPower to set
     */
    public void setAbilityPower(double abilityPower) {
        this.abilityPower = abilityPower;
    }

    /**
     * @return the baseAD
     */
    public double getBaseAD() {
        return baseAD;
    }

    /**
     * @param attackDamage the baseAD to set
     */
    public void setBaseAD(double attackDamage) {
        this.baseAD = attackDamage;
    }

    /**
     * @return the health
     */
    public double getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * @return the cDR
     */
    public double getcDR() {
        return cDR;
    }

    /**
     * @param cDR the cDR to set
     */
    public void setcDR(double cDR) {
        this.cDR = cDR;
    }

    /**
     * @return the bonusAD
     */
    public double getBonusAD() {
        return bonusAD;
    }

    /**
     * @param bonusAD the bonusAD to set
     */
    public void setBonusAD(double bonusAD) {
        this.bonusAD = bonusAD;
    }

    /**
     * @return total attack damage
     */
    public double getTotalAD() {
        return baseAD + bonusAD;
    }

}
