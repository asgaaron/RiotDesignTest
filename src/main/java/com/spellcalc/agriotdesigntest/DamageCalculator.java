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
    Spell calculateSingleHighest() throws IOException {
        ChampionSpell highest = null;
        double highestDamage = 0;

        //parse through sanitized tooltip to find damage calculation
        for (ChampionSpell spell : spells) {
            double damage = getSpellDamage(spell);

            //is this spell's damage the new highest?
            if (damage > highestDamage) {
                //if so, crown the new highest damage spell
                highestDamage = damage;
                highest = spell;
            }
        }
        Spell spell = new Spell(highest, highestDamage);
        return spell;
    }

    private double getSpellDamage(ChampionSpell spell) {
        double damage = 0;
        int maxRankIndex = spell.getMaxrank() - 1;

        if (checkEffect(spell)) {
            if (checkVars(spell)) {
                if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) magic damage"))
                {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += spell.getVars().get(1).getCoeff().get(1) * abilityPower;
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += spell.getVars().get(1).getCoeff().get(1) * bonusAD;
                }
            }
        }
        return damage;
    }

    private boolean sanitizedContains(ChampionSpell spell, String text) {
        if (spell.getSanitizedTooltip().toLowerCase().contains(text)) {
            return true;
        }
        return false;
    }

    private boolean checkVars(ChampionSpell spell) {
        if (null == spell.getVars()) {
            System.out.println("Uh oh, it looks like " + spell.getName() + " doesn't have a var field...scaling damages are missing!");
            return false;
        }
        return true;
    }

    private boolean checkEffect(ChampionSpell spell) {
        if (null == spell.getEffect()) {
            System.out.println("Uh oh, it looks like " + spell.getName() + "'s base damages are missing!");
            return false;
        }
        return true;
    }

    private double geta1Coeff(ChampionSpell spell) {
        return spell.getVars().get(1).getCoeff().get(1);
    }

    //need to update this with work from above
    Spell calculateDPS() {
        ChampionSpell highest = null;
        double highestDPS = 0;
        for (ChampionSpell spell : spells) {
            int maxRankIndex = spell.getMaxrank() - 1;
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

            double damagePerTen = damage * 10 / spell.getCooldown().get(spell.getMaxrank() - 1);

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
