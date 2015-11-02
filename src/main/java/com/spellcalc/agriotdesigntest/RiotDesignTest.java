package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.api.dto.BaseRiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.dto.staticdata.Champion;
import com.robrua.orianna.type.dto.staticdata.ChampionList;
import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aaron
 */
public class RiotDesignTest
{

	/**
	 * Create a program to calculate the most efficient spell in the game, given varying amounts of ability power,
	 * attack damage, and cooldown reduction. Options to calculate highest damage single cast spell, as well as highest
	 * DPS spell over a provided unit of time (seconds).
	 */
	public static void main(String args[])
	{
		SetupAPI();
		List<ChampionSpell> spells = getSpellList();
		spells = removeNonDamageSpells(spells);

//		for dev purposes
//		printSpells(spells);
//		printProblemSpells(spells);
		String options[] =
		{
			"Modify Bonus Attack Damage", "Modify Base Attack Damage", "Modify Ability Power", "Modify Cooldown Reduction", "Calculate highest single cast damage", "Calculate highest DPS spell over 10 seconds", "Exit"
		};
		MainMenu menu = new MainMenu(spells, options);
		while (true)
		{
			try
			{
				menu.displayOptions();
			}
			catch (IOException ex)
			{
				Logger.getLogger(RiotDesignTest.class.getName()).log(Level.SEVERE, "UH OH GUY", ex);
			}
		}

	}

	/**
	 * Function will print out a list of champion spells to a text file, for dev purposes
	 *
	 * @param args
	 */
	public static void printSpells(List<ChampionSpell> spells, String fileName)
	{
		try
		{
			FileWriter fileWriter
				= new FileWriter(fileName);

			BufferedWriter bufferedWriter
				= new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			for (ChampionSpell spell : spells)
			{
				bufferedWriter.write(spell.toJSON());
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			}

			// Always close files.
			bufferedWriter.close();
		}
		catch (IOException ex)
		{
			System.out.println(
				"Error writing to file '"
				+ fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}

	/**
	 * Calls Riot API to retrieve a list of all the champions in the game. Extracts ChampionSpells
	 */
	private static List<ChampionSpell> getSpellList()
	{
		//get a list of all the champions in the game
		ChampionList champs = BaseRiotAPI.getChampions();
		List<Champion> champions = new ArrayList<>(champs.getData().values());
		//use the list of champions to get a list of all the spells in the game
		List<ChampionSpell> spells = extractSpells(champions);
		return spells;
	}

	/**
	 * Function to initialize Riot API usage through Orianna library. Orianna source: https://github.com/robrua/Orianna
	 */
	private static void SetupAPI()
	{
		BaseRiotAPI.setMirror(Region.NA);
		BaseRiotAPI.setRegion(Region.NA);
		//YOUR API KEY HERE!
		BaseRiotAPI.setAPIKey("XXX");
	}

	/**
	 * Function that removes non-damaging spells from the a list of ChampionSpell objects
	 *
	 * @param spells a list of ChampionSpell objects
	 * @return a list of ChampionSpell objects without damaging spells
	 */
	private static List<ChampionSpell> removeNonDamageSpells(List<ChampionSpell> spells)
	{
		for (ChampionSpell spell : spells)
		{
			List<String> labels = spell.getLeveltip().getLabel();
			boolean damage = false;
			for (String label : labels)
			{
				if (label.contains("damage") | label.contains("Damage"))
				{
					damage = true;
					break;
				}
			}
			if (!damage)
			{
				spells.remove(spell);
			}
		}
		return spells;
	}

	/**
	 * Function that extracts ChampionSpell objects from a list of Champion objects
	 *
	 * @param champList a list of Champion objects
	 * @return a list of ChampionSpell objects
	 */
	private static List<ChampionSpell> extractSpells(List<Champion> champList)
	{
		List<ChampionSpell> spells = new ArrayList<>();
		for (Champion champion : champList)
		{
			spells.addAll(champion.getSpells());
		}
		return spells;
	}

	/**
	 * Function that will print all damaging spells that don't calculate their damage using e1 + a1
	 *
	 * @param spells
	 */
	public static void printProblemSpells(List<ChampionSpell> spells)
	{
		for (ChampionSpell spell : spells)
		{
			if (spell.getSanitizedTooltip().contains("{{ e1 }} (+{{ a1 }})"))
			{
				spells.remove(spell);
			}
		}
		printSpells(spells, "problemSpells.txt");
	}
}
