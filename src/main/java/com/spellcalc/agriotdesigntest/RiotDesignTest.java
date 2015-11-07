package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.api.dto.BaseRiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.dto.staticdata.Champion;
import com.robrua.orianna.type.dto.staticdata.ChampionList;
import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import java.awt.SystemColor;
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
 * @author asgaaron
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
//        SpellPrinter.printSpells(spells, "spellList.txt");
        spells = removeNonDamageSpells(spells);
//        SpellPrinter.printSpells(spells, "filteredSpellList.txt");
//        SpellPrinter.printProblemSpells(spells);

        DamageCalculator calculator = new DamageCalculator(spells);

        final String options[]
                = {
                    "Modify stats",
                    "Calculate highest single cast damage spell",
                    "Calculate highest DPS spell",
                    "Calculate specific spell damage",
                    "Exit"
                };
        MainMenu menu = new MainMenu(options);

        run(calculator, menu);
    }

    private static void run(DamageCalculator calculator, MainMenu menu) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                String header = "\nCurrent Stats: "
                        + calculator.getBonusAD() + " Bonus Attack Damage, "
                        + calculator.getBaseAD() + " Base Attack Damage, "
                        + calculator.getAbilityPower() + " Ability Power, "
                        + calculator.getcDR() + "% Cooldown Reduction, "
                        + calculator.getHealth() + " HP, "
                        + calculator.getBonusHealth() + " Bonus HP, "
                        + calculator.getArmor() + " Armor, "
                        + calculator.getMana() + " Mana";
                System.out.println(header);
                menu.displayOptions();
                switch (menu.getInput()) {
                    case 1: {
                        modifyStats(calculator, menu, sc);
                        break;
                    }
                    case 2: {
                        Spell spell = calculator.calculateSingleHighest();
                        System.out.println("The highest damage single cast spell is: "
                                + spell.getSpell().getName() + ", doing " + spell.getDamage() + " damage!");
                        break;
                    }
                    case 3: {
                        System.out.println("Enter period of time (in seconds): ");
                        int period = sc.nextInt();
                        System.out.println("");
                        Spell spell = calculator.calculateDPS(period);
                        System.out.println("With the provided stats, the highest dps spell over " + period + " seconds is: "
                                + spell.getSpell().getName() + ", doing " + spell.getDamage() + " damage!");
                        break;
                    }
                    case 4: {
                        System.out.println("Enter spell name: ");
                        String spellName = sc.nextLine();
                        System.out.println("");
                        List<ChampionSpell> spells = calculator.getSpells();
                        double damage = 0;
                        for (ChampionSpell spell : spells) {
                            if (spell.getName().equals(spellName)) {
                                damage = calculator.getSpellDamage(spell);
                            }
                        }
                        if (damage==0) {
                            System.out.println("I don't think you entered that  spell name correctly...try again!");
                        }
                        System.out.println("With the provided stats, "
                                + spellName + "will do: " + damage + " damage!");
                        break;
                    }
                    case 5: {
                        System.exit(1);
                    }
                    default: {
                        System.out.println("Oops! Please enter an option number!");
                        menu.getInput();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(RiotDesignTest.class.getName()).log(Level.SEVERE, "UH OH GUY", ex);
            }
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
     * Function that removes spells from the a list of ChampionSpell objects in
     * an attempt to return a list of single-cast damage spells
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
            if (spell.getRangeBurn().toLowerCase().contains("self")) {
                i.remove();
            } else if (spell.getSanitizedTooltip().toLowerCase().contains("bonus magic damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("bonus physical damage")
                    | spell.getSanitizedTooltip().toLowerCase().contains("3rd attack")
                    | spell.getName().equals("Living Shadow")
                    | spell.getName().equals("Unbreakable")
                    | spell.getName().equals("Children of the Grave")
                    | spell.getName().equals("Consume")
                    | spell.getName().equals("Sentinel")
                    | spell.getName().equals("Deceive")
                    | spell.getName().equals("Omen of Death")
                    | spell.getName().equals("Cutthroat")
                    | spell.getName().equals("Mounting Dread")
                    | spell.getName().equals("Hyper Charge")
                    | spell.getName().equals("Mocking Shout")
                    | spell.getName().equals("Paragon of Demacia")
                    | spell.getName().equals("Diplomatic Immunity")
                    | spell.getName().equals("Valor")
                    | spell.getName().equals("Inspire")
                    | spell.getName().equals("Bloodlust")) {
                i.remove();
            } else {
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

    private static void modifyStats(DamageCalculator calculator, MainMenu menu, Scanner sc) throws IOException {
        final String[] options = {
            "Initialize stats",
            "Modify bonus attack damage",
            "Modify base attack damage",
            "Modify base ability power",
            "Modify cooldown reduction",
            "Modify health",
            "Modify bonus health",
            "Modify armor",
            "Modify mana",
            "Go back"
        };
        for (int i = 1; i <= options.length; i++) {
            System.out.println(i + ": " + options[i - 1]);
        }
        switch (menu.getInput()) {
            case 1: {
                System.out.println("Enter new bonus attack damage: ");
                double attackDamage = sc.nextDouble();
                System.out.println("");
                calculator.setBonusAD(attackDamage);
                System.out.println("Enter new base attack damage: ");
                attackDamage = sc.nextDouble();
                System.out.println("");
                calculator.setBaseAD(attackDamage);
                System.out.println("Enter new Ability Power: ");
                double abilityPower = sc.nextDouble();
                System.out.println("");
                calculator.setAbilityPower(abilityPower);
                System.out.println("Enter new cooldown reduction (% value): ");
                double cdr = sc.nextDouble();
                System.out.println("");
                calculator.setcDR(cdr);
                System.out.println("Enter new health value: ");
                double health = sc.nextDouble();
                System.out.println("");
                calculator.setHealth(health);
                System.out.println("Enter new bonus health value: ");
                double bonusHp = sc.nextDouble();
                System.out.println("");
                calculator.setBonusHealth(bonusHp);
                System.out.println("Enter new armor value: ");
                double input = sc.nextDouble();
                System.out.println("");
                calculator.setArmor(input);
                System.out.println("Enter new mana value: ");
                double mana = sc.nextDouble();
                System.out.println("");
                calculator.setMana(mana);
                break;
            }
            case 2: {
                System.out.println("Enter new bonus attack damage: ");
                double attackDamage = sc.nextDouble();
                System.out.println("");
                calculator.setBonusAD(attackDamage);
                break;
            }
            case 3: {
                System.out.println("Enter new base attack damage: ");
                double attackDamage = sc.nextDouble();
                System.out.println("");
                calculator.setBaseAD(attackDamage);
                break;
            }
            case 4: {
                System.out.println("Enter new Ability Power: ");
                double abilityPower = sc.nextDouble();
                System.out.println("");
                calculator.setAbilityPower(abilityPower);
                break;
            }
            case 5: {
                System.out.println("Enter new cooldown reduction (% value): ");
                double cdr = sc.nextDouble();
                System.out.println("");
                calculator.setcDR(cdr);
                break;
            }
            case 6: {
                System.out.println("Enter new health value: ");
                double health = sc.nextDouble();
                System.out.println("");
                calculator.setHealth(health);
                break;
            }
            case 7: {
                System.out.println("Enter new bonus health value: ");
                double bonusHp = sc.nextDouble();
                System.out.println("");
                calculator.setBonusHealth(bonusHp);
                break;
            }
            case 8: {
                System.out.println("Enter new armor value: ");
                double input = sc.nextDouble();
                System.out.println("");
                calculator.setArmor(input);
                break;
            }
            case 9: {
                System.out.println("Enter new mana value: ");
                double mana = sc.nextDouble();
                System.out.println("");
                calculator.setMana(mana);
                break;
            }
            default:
                break;
        }
    }
}
