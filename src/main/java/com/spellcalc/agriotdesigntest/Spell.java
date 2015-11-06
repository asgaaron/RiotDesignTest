package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.type.dto.staticdata.ChampionSpell;

/**
 *
 * @author asgaaron
 */
class Spell {

    ChampionSpell spell;
    double damage;

    public Spell(ChampionSpell spell, double damage) {
        this.spell = spell;
        this.damage = damage;
    }

    ChampionSpell getSpell() {
        return spell;
    }

    void setSpell(ChampionSpell spell) {
        this.spell = spell;
    }

    double getDamage() {
        return damage;
    }

    void setDamage(double damage) {
        this.damage = damage;
    }
}
