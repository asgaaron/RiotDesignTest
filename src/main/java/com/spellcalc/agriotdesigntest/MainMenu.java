package com.spellcalc.agriotdesigntest;

import com.robrua.orianna.type.dto.staticdata.ChampionSpell;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MainMenu
{

	DamageCalculator calculator;

	String options[];

	MainMenu(List<ChampionSpell> spells, String[] options)
	{
		this.options = options;
		calculator = new DamageCalculator(spells);
	}

	/**
	 * Function to display all of the options to the user, then asks for input
	 */
	void displayOptions() throws IOException
	{
		System.out.println("\nCurrent Stats:" + calculator.getBonusAD() + " Bonus Attack Damage, " + calculator.getBaseAD()
			+ " Base Attack Damage, " + calculator.getAbilityPower() + " Ability Power, " + calculator.getcDR() + "% Cooldown Reduction");
		for (int i = 0; i < options.length; i++)
		{
			System.out.println(i + ": " + options[i]);
		}
		getInput();
	}

	/**
	 * Function to get input from the user, then does an action based off the user's input
	 *
	 */
	void getInput() throws IOException
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter an option: ");
		int option = sc.nextInt();

		// test for bad input?
		switch (option)
		{
			case 1:
			{
				System.out.println("Enter new bonus attack damage: ");
				double attackDamage = sc.nextDouble();
				System.out.println("");
				calculator.setAttackDamage(attackDamage);
				break;
			}
			case 2:
			{
				System.out.println("Enter new base attack damage: ");
				double attackDamage = sc.nextDouble();
				System.out.println("");
				calculator.setAttackDamage(attackDamage);
				break;
			}
			case 3:
			{
				System.out.println("Enter new Ability Power: ");
				double abilityPower = sc.nextDouble();
				System.out.println("");
				calculator.setAbilityPower(abilityPower);
				break;
			}
			case 4:
			{
				System.out.println("Enter new cooldown reduction (% value): ");
				double cdr = sc.nextDouble();
				System.out.println("");
				calculator.setcDR(cdr);
				break;
			}
			case 5:
			{
				Spell spell = calculator.calculateSingle();
				System.out.println("With the provided stats, the highest damage single cast spell is: " + spell.getSpell().getName() + ", doing " + spell.getDamage() + " damage!");
				break;
			}
			case 6:
			{
				Spell spell = calculator.calculateDPS();
				System.out.println("With the provided stats, the highest damage spell over 10 seconds is: " + spell.getSpell().getName() + ", doing " + spell.getDamage() + " damage!");
				break;
			}
			case 7:
			{
				System.exit(1);
			}
			default:
			{
				System.out.println("Oops! Please enter an option number!");
				getInput();
			}
		}
	}
}
