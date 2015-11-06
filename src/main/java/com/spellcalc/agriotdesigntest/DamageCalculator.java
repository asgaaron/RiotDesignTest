package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import com.robrua.orianna.type.dto.staticdata.SpellVars;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author asgaaron
 */
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

        if (checkEffectExist(spell)) {
            if (checkVarsExist(spell)) {
                if (spell.getName().equals("Neurotoxin / Venomous Bite")
                        | spell.getName().equals("Venomous Bite / Neurotoxin")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += spell.getEffect().get(2).get(maxRankIndex);
                } else if (spell.getName().equals("Twin Fang")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += .55 * abilityPower;
                } else if (spell.getName().equals("Contaminate")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    double stackDamage = spell.getEffect().get(2).get(maxRankIndex);
                    stackDamage += getScalingStat(spell, "a1");
                    stackDamage += getScalingStat(spell, "f1");
                    stackDamage *= 5;
                    damage += stackDamage;
                } else if (spell.getName().equals("Riftwalk")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f2");
                    damage += getScalingStat(spell, "a1");
                    double stackDamage = spell.getEffect().get(3).get(maxRankIndex);
                    stackDamage += getScalingStat(spell, "f1");
                    stackDamage += getScalingStat(spell, "f3");
                    stackDamage *= spell.getEffect().get(6).get(maxRankIndex);
                    damage += stackDamage;
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) magic damage")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) true damage")
                        | sanitizedContains(spell, "dealing Magic Damage up to a total of {{ e1 }} (+{{ a1 }})")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (spell.getName().equals("Infected Cleaver")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                } else if (spell.getName().equals("Furious Bite / Tunnel")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex) * 2 * getTotalAD();
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) physical damage")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) true damage")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) [6%")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ f3 }} physical damage")) {
                    damage = getScalingStat(spell, "f3");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f3 }}) (+{{ a2 }}) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f3");
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e2 }} magic damage plus {{ e1 }} (+{{ a1 }})% of their maximum Health each second")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) plus 15% of target")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) [2.5% of Braum")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) and {{ e2 }}% (+{{ a1 }}) of the target")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) ({{ e2 }}% of bonus Attack Damage) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e5 }} (+{{ a1 }}) magic damage")) {
                    damage = spell.getEffect().get(5).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e3 }} (+{{ a2 }}).")
                        | sanitizedContains(spell, "{{ e3 }} (+{{ a2 }}) magic damage")) {
                    damage = spell.getEffect().get(3).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ f1 }}) (+{{ f2 }}) magic damage")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                    damage += getScalingStat(spell, "f2");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ a1 }}) (+{{ a2 }}) physical damage")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e5 }} (+{{ f1 }}) (+{{ a1 }}) magic damage")) {
                    damage = spell.getEffect().get(5).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e3 }} (+{{ f1 }} [{{ e4 }}% of bonus Health]) physical damage")) {
                    damage = spell.getEffect().get(3).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e4 }} (+{{ a1 }}) magic damage")) {
                    damage = spell.getEffect().get(4).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ f2 }}) physical damage")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "f2");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ a2 }})")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }})(+{{ a1 }}) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                    damage += getScalingStat(spell, "a1");
                    if (spell.getName().equals("Bullet Time")) {
                        damage *= 8;
                    }
                } else if (sanitizedContains(spell, "{{ e4 }} (+{{ f2 }}) (+{{ a2 }}) physical damage")) {
                    damage = spell.getEffect().get(4).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                    damage += getScalingStat(spell, "f2");
                    if (spell.getName().equals("Double Up")) {
                        damage *= 1.5;
                    }
                } else if (sanitizedContains(spell, "{{ e4 }} damage per second")) {
                    damage = spell.getEffect().get(4).get(maxRankIndex) * 4;
                } else if (sanitizedContains(spell, "{{ e3 }} (+{{ f1 }}) physical damage")
                        | sanitizedContains(spell, "{{ e3 }} (+{{ f1 }} [15% of bonus Health]) magic damage")) {
                    damage = spell.getEffect().get(3).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                    if (spell.getName().equals("Gatling Gun")) {
                        damage *= 4;
                    }
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) (+{{ a2 }}) magic damage")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) (+{{ a2 }}) physical damage")
                        | sanitizedContains(spell, "{{ e2 }} (+{{ a2 }}) (+{{ a1 }}) plus 8% of her target\\u0027s maximum Health")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ a1 }}) magic damage")
                        | sanitizedContains(spell, "{{ e2 }} (+{{ a1 }}) physical damage")
                        | sanitizedContains(spell, "{{ e2 }} (+{{ a1 }}) true damage")
                        | sanitizedContains(spell, "{{ e2 }} (+{{ a1 }}) plus {{ e4 }}% of the target")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) (+{{ a1 }}) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e1 }} magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                } else if (sanitizedContains(spell, "{{ e4 }}% of the enemy\\u0027s maximum health (+{{ a1 }}) as magic damage")) {
                    damage = getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) (+{{ a1 }}) physical damage")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ f1 }}) (+{{ a1 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f2 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f2");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ f1 }}) physical damage")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a2 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                    if (spell.getName().equals("The Equalizer")) {
                        damage *= 5;
                    }
                } else if (sanitizedContains(spell, "{{ e4 }} (+{{ f2 }}) physical damage")) {
                    damage = spell.getEffect().get(4).get(maxRankIndex);
                    damage += getScalingStat(spell, "f2");
                } else if (sanitizedContains(spell, "{{ e4 }} (+{{ a2 }}) magic damage")) {
                    damage = spell.getEffect().get(4).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e7 }} (+{{ a2 }}) magic damage")) {
                    damage = spell.getEffect().get(7).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }})% of the target")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                } else if (sanitizedContains(spell, "{{ a1 }} plus {{ e2 }}% of all Magic and Physical Damage dealt to the target by Zed and his shadows while the mark was active")) {
                    damage = getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e3 }} (+{{ a1 }}) ")) {
                    damage = spell.getEffect().get(3).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) (+{{ f1 }}) physical damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a2 }}) (+{{ a1 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "a2");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ f2 }}) (+{{ f1 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "f2");
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) plus {{ e3 }}% of the target\\u0027s maximum Health as magic damage")
                        | sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) plus 80% of his target")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ f1 }} (+{{ a1 }}) magic damage")) {
                    damage = getScalingStat(spell, "f1");
                    damage += getScalingStat(spell, "a1");
                } else if (sanitizedContains(spell, "{{ e1 }} {{ f3 }} (+{{ a1 }}) area magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "f3");
                } else if (sanitizedContains(spell, "{{ e6 }} (+{{ a2 }}) magic damage")) {
                    damage = spell.getEffect().get(6).get(maxRankIndex);
                    damage += getScalingStat(spell, "a2");
                    if (spell.getName().equals("Tormented Soil")) {
                        damage *= 5;
                    }
                } else if (sanitizedContains(spell, "{{ e1 }} (+{{ a1 }}) (+{{ f1 }}) magic damage")) {
                    damage = spell.getEffect().get(1).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "f1");
                } else if (sanitizedContains(spell, "{{ e2 }} (+{{ a1 }}) (+{{ f1 }}) magic damage")) {
                    damage = spell.getEffect().get(2).get(maxRankIndex);
                    damage += getScalingStat(spell, "a1");
                    damage += getScalingStat(spell, "f1");
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

    private boolean checkVarsExist(ChampionSpell spell) {
        if (null == spell.getVars()) {
            System.out.println("Uh oh, it looks like " + spell.getName() + " doesn't have a var field...scaling damages are missing!");
            return false;
        }
        return true;
    }

    private boolean checkEffectExist(ChampionSpell spell) {
        if (null == spell.getEffect()) {
            System.out.println("Uh oh, it looks like " + spell.getName() + "'s base damages are missing!");
            return false;
        }
        return true;
    }

    private double getScalingStat(ChampionSpell spell, String key) {
        boolean found = false;
        List<SpellVars> vars = spell.getVars();
        for (int i = 0; i < vars.size(); i++) {
            if (vars.get(i).getKey().equals(key)) {
                switch (spell.getVars().get(i).getLink()) {
                    case "bonusattackdamage":
                        return vars.get(i).getCoeff().get(vars.get(i).getCoeff().size() - 1) * bonusAD;
                    case "spelldamage":
                        return vars.get(i).getCoeff().get(vars.get(i).getCoeff().size() - 1) * abilityPower;
                    case "attackdamage":
                        return vars.get(i).getCoeff().get(vars.get(i).getCoeff().size() - 1) * getTotalAD();
                    case "health":
                        return vars.get(i).getCoeff().get(vars.get(i).getCoeff().size() - 1) * health;
                    default:
                        return 0;
                }
            }
        }
        if (!found) {
            System.out.println("Couldn't find a scaling stat for " + spell.getName() + ". The key is: " + key);
            return 0;
        }
        //this should never get hit but is needed to satisfy return statement requirement
        return 0;
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
