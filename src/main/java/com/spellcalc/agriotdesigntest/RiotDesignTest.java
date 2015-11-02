package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.api.dto.BaseRiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.dto.staticdata.Champion;
import com.robrua.orianna.type.dto.staticdata.ChampionList;
import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aaron
 */
public class RiotDesignTest {

    /**
     * Create a program to calculate the most efficient spell in the game, given
     * varying amounts of ability power, attack damage, and cooldown reduction.
     * Options to calculate highest damage single cast spell, as well as highest
     * DPS spell over a provided unit of time (seconds).
     *
     * @param args no args are used
     */
    public static void main(String args[]) {
        try {
            SetupAPI();
        } catch (IOException ex) {
            Logger.getLogger(RiotDesignTest.class.getName()).log(Level.SEVERE, "Error reading API Key", ex);
        }
        List<ChampionSpell> spells = getSpellList();
        printSpells(spells, "spellList.txt");
        spells = removeNonDamageSpells(spells);
        DamageCalculator calculator = new DamageCalculator(spells);

//		for dev purposes
        printSpells(spells, "filteredSpellList.txt");
        printProblemSpells(spells);
        final String options[]
                = {
                    "Modify Bonus Attack Damage", "Modify Base Attack Damage", "Modify Ability Power",
                    "Modify Cooldown Reduction", "Calculate highest single cast damage",
                    "Calculate highest DPS spell over 10 seconds", "Exit"
                };
        final String header = "\nCurrent Stats:" + calculator.getBonusAD() + " Bonus Attack Damage, "
                + calculator.getBaseAD()
                + " Base Attack Damage, " + calculator.getAbilityPower() + " Ability Power, " + calculator.getcDR()
                + "% Cooldown Reduction";
        MainMenu menu = new MainMenu(options);
        Scanner sc = new Scanner(System.in);
//		while (true)
//		{
//			try
//			{
//
//				System.out.println(header);
//				menu.displayOptions();
//				switch (menu.getInput())
//				{
//					case 1:
//					{
//						System.out.println("Enter new bonus attack damage: ");
//						double attackDamage = sc.nextDouble();
//						System.out.println("");
//						calculator.setAttackDamage(attackDamage);
//						break;
//					}
//					case 2:
//					{
//						System.out.println("Enter new base attack damage: ");
//						double attackDamage = sc.nextDouble();
//						System.out.println("");
//						calculator.setAttackDamage(attackDamage);
//						break;
//					}
//					case 3:
//					{
//						System.out.println("Enter new Ability Power: ");
//						double abilityPower = sc.nextDouble();
//						System.out.println("");
//						calculator.setAbilityPower(abilityPower);
//						break;
//					}
//					case 4:
//					{
//						System.out.println("Enter new cooldown reduction (% value): ");
//						double cdr = sc.nextDouble();
//						System.out.println("");
//						calculator.setcDR(cdr);
//						break;
//					}
//					case 5:
//					{
//						Spell spell = calculator.calculateSingle();
//						System.out.println("With the provided stats, the highest damage single cast spell is: "
//							+ spell.getSpell().getName() + ", doing " + spell.getDamage() + " damage!");
//						break;
//					}
//					case 6:
//					{
//						Spell spell = calculator.calculateDPS();
//						System.out.println("With the provided stats, the highest damage spell over 10 seconds is: "
//							+ spell.getSpell().getName() + ", doing " + spell.getDamage() + " damage!");
//						break;
//					}
//					case 7:
//					{
//						System.exit(1);
//					}
//					default:
//					{
//						System.out.println("Oops! Please enter an option number!");
//						menu.getInput();
//					}
//				}
//			}
//			catch (IOException ex)
//			{
//				Logger.getLogger(RiotDesignTest.class.getName()).log(Level.SEVERE, "UH OH GUY", ex);
//			}
//		}

    }

    /**
     * Function will print out a list of champion spells to a text file, for dev
     * purposes
     *
     * @param spells the list of spells to be printed
     * @param fileName name of the text file that will be produced
     */
    public static void printSpells(List<ChampionSpell> spells, String fileName) {
        try {
            FileWriter fileWriter
                    = new FileWriter(fileName);

			// Note that write() does not automatically
            // append a newline character.
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
				// Note that write() does not automatically
                // append a newline character.
                for (ChampionSpell spell : spells) {
                    bufferedWriter.write(spell.toJSON());
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                }

                // Always close files.
            }
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                    + fileName + "'");
			// Or we could just do this:
            // ex.printStackTrace();
        }
    }

    /**
     * Calls Riot API to retrieve a list of all the champions in the game.
     * Extracts ChampionSpells
     */
    private static List<ChampionSpell> getSpellList() {
        //get a list of all the champions in the game
        ChampionList champs = BaseRiotAPI.getChampions();
        List<Champion> champions = new ArrayList<>(champs.getData().values());
        //use the list of champions to get a list of all the spells in the game
        List<ChampionSpell> spells = extractSpells(champions);
        return spells;
    }

    /**
     * Function to initialize Riot API usage through Orianna library. Orianna
     * source: https://github.com/robrua/Orianna
     */
    private static void SetupAPI() throws FileNotFoundException, IOException {
        BaseRiotAPI.setMirror(Region.NA);
        BaseRiotAPI.setRegion(Region.NA);
        
        FileReader filereader = new FileReader("apikey");
        BufferedReader bufferedreader = new BufferedReader(filereader);
        
        
        BaseRiotAPI.setAPIKey(bufferedreader.readLine());
    }

    /**
     * Function that removes non-damaging spells from the a list of
     * ChampionSpell objects
     *
     * @param spells a list of ChampionSpell objects
     * @return a list of ChampionSpell objects without damaging spells
     */
    private static List<ChampionSpell> removeNonDamageSpells(List<ChampionSpell> spells) {
        Iterator<ChampionSpell> i = spells.iterator();
        while (i.hasNext()) {
            ChampionSpell spell = i.next();
            List<String> labels = spell.getLeveltip().getLabel();
            boolean damage = false;
            for (String label : labels) {
                if (label.contains("damage") | label.contains("Damage")) {
                    damage = true;
                    break;
                }
            }
            if (!damage) {
                i.remove();
            }
        }
        return spells;
    }

    /**
     * Function that extracts ChampionSpell objects from a list of Champion
     * objects
     *
     * @param champList a list of Champion objects
     * @return a list of ChampionSpell objects
     */
    private static List<ChampionSpell> extractSpells(List<Champion> champList) {
        List<ChampionSpell> spells = new ArrayList<>();
        for (Champion champion : champList) {
            spells.addAll(champion.getSpells());
        }
        return spells;
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
            if (spell.getSanitizedTooltip().contains("{{ e1 }} (+{{ a1 }})")) {
                i.remove();
            }
        }
        printSpells(spells, "problemSpells.txt");
    }
}
