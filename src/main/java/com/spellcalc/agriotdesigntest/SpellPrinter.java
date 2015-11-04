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
 * @author Aaron
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
                    |spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) physical damage")
                    |spell.getSanitizedTooltip().toLowerCase().contains("{{ e2 }} (+{{ a1 }}) magic damage")
                    |spell.getSanitizedTooltip().toLowerCase().contains("{{ e1 }} (+{{ a1 }}) (+{{ a2 }}) magic damage")) {
                i.remove();
            }
        }
        printSpells(spells, "problemSpells.txt");
    }

}
