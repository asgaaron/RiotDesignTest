/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author asgaaron
 */
public class SpellPrinter {

    /**
     * Function will print out a list of champion spells to a text file, for dev
     * purposes
     *
     * @param spells the list of spells to be printed
     * @param fileName name of the text file that will be produced
     */
    public static void printSpells(List<ChampionSpell> spells, String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            try (final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                for (ChampionSpell spell : spells) {
                    bufferedWriter.write(spell.toJSON());
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException ex) {
            System.out.println("Error writing to file '" + fileName + "'");
        }
    }

    /**
     * Function that will print all damaging spells that don't calculate their
     * damage using e1 + a1
     *
     * @param spells
     */
    public static void printProblemSpells(List<ChampionSpell> spells) {
        Iterator<ChampionSpell> i = spells.iterator();
        while (i.hasNext()) {
            ChampionSpell spell = i.next();
            if (spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) magic damage")
                    | spell.getName().equals("Neurotoxin / Venomous Bite")
                    | spell.getName().equals("Infected Cleaver")
                    | spell.getName().equals("Venomous Bite / Neurotoxin")
                    | spell.getName().equals("Furious Bite / Tunnel")
                    | spell.getName().equals("Riftwalk")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("dealing Magic Damage up to a total of {{ e1 }} (+{{ a1 }})".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f3 }}) (+{{ a2 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a2 }}) (+{{ a1 }}) plus 8%")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a2 }})")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e4 }} (+{{ f2 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) true damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} magic damage plus {{ e1 }} (+{{ a1 }})% of their maximum Health each second".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e3 }} (+{{ a2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} physical damage plus {{ e1 }} (+{{ a1 }}) (+{{ f1 }}) per stack of Deadly Venom".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) plus 15% of target")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ f3 }} physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e5 }} (+{{ f1 }}) (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e3 }} (+{{ f1 }} [15% of bonus Health]) magic damage".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e3 }} (+{{ f1 }} [{{ e4 }}% of bonus Health]) physical damage".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) plus {{ e3 }}% of the target")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) ({{ e2 }}% of bonus Attack Damage) physical damage".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }})% of the target")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) true damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) [2.5% of Braum".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }})(+{{ a1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e4 }} (+{{ f2 }}) (+{{ a2 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e3 }} (+{{ a2 }}).")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ f1 }}) (+{{ f2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) and {{ e2 }}% (+{{ a1 }}) of the target")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) (+{{ a2 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e3 }} (+{{ a1 }}) ")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) [6% of Gnar".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ f1 }} (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) (+{{ a2 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e6 }} (+{{ a2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e4 }} damage per second")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e5 }} (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f2 }}) (+{{ f1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ a1 }} plus {{ e2 }}% of all Magic and Physical Damage dealt to the target by Zed and his shadows while the mark was active".toLowerCase())
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) (+{{ f1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} {{ f3 }} (+{{ a1 }}) area magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) plus 80% of his target")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) plus {{ e4 }}% of the target")
                    | spell.getSanitizedTooltip().toLowerCase().contains("s maximum health (+{{ a1 }}) as magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) (+{{ a1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e3 }} (+{{ f1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e7 }} (+{{ a2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e4 }} (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ f2 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ f1 }}) true damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ f1 }}) physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e4 }} (+{{ a2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) (+{{ a2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a2 }}) (+{{ a1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a2 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) (+{{ f1 }}) magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) (+{{ f1 }}) magic damage")) {
                i.remove();
            }
        }
        printSpells(spells, "problemSpells.txt");
    }
    
}
