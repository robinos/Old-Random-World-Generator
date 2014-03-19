package gameLogic;

import modelTypes.*;

/**
 * 
 * @author Robin
 *
 */
public class RunSettlement
{

	public RunSettlement()
	{
		
	}

	public RunSettlement(String name)
	{
		Settlement village = new Settlement();
		
		//strength, agility, willpower, intelligence, spirit, charisma, awareness
		Creature bob = new Creature(4, 4, 1, 1, 1, 1, 2);
		//empathy, discipline, charity, chastity, serenity,harmony, humility, industry, temperance
		bob.setPersonality(40, 50, 40, 30, 40, 50, 30, 60, 40);
		bob.setID(0);
		bob.setName("Bob");
		
		//strength, agility, willpower, intelligence, spirit, charisma, awareness
		Creature jane = new Creature(2, 2, 2, 2, 2, 2, 2);
		//empathy, discipline, charity, chastity, serenity,harmony, humility, industry, temperance
		jane.setPersonality(70, 60, 60, 30, 50, 40, 60, 50, 60);
		jane.setID(1);
		jane.setName("Jane");		
		
		//strength, agility, willpower, intelligence, spirit, charisma, awareness
		Creature billy = new Creature(1, 1, 1, 1, 1, 1, 1);
		//empathy, discipline, charity, chastity, serenity, harmony, humility, industry, temperance
		billy.setPersonality(60, 30, 60, 40, 50, 40, 30, 40, 40);
		billy.setID(2);
		billy.setName("Billy");		
		
		//strength, agility, willpower, intelligence, spirit, charisma, awareness
		Creature mandy = new Creature(1, 1, 1, 1, 1, 1, 1);
		//empathy, discipline, charity, chastity, serenity, harmony, humility, industry, temperance
		mandy.setPersonality(40, 60, 60, 60, 60, 60, 60, 60, 60);
		mandy.setID(3);
		mandy.setName("Mandy");	
		
		bob.set_relation(jane,60);
		bob.set_relation(billy,50);	
		bob.set_relation(mandy,40);
		bob.add_child(billy);
		
		jane.set_relation(bob,50);
		jane.set_relation(billy,40);	
		jane.set_relation(mandy,60);
		jane.add_child(billy);
		
		billy.set_relation(bob,50);
		billy.set_relation(jane,40);	
		billy.set_relation(mandy,50);
		billy.set_father(bob);		
		billy.set_mother(jane);
		
		mandy.set_relation(bob,50);
		mandy.set_relation(billy,30);	
		mandy.set_relation(jane,60);	
		
		village.add_citizen(bob);
		village.add_citizen(jane);
		village.add_citizen(billy);
		village.add_citizen(mandy);	
		
		run();
	}
	
	void run()
	{
		
	}
	
}
