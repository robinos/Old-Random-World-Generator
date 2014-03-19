package modelTypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Creature base class for all creatures.
 * After a time pulse in the main program, increment_hunger, increment_thirst, and
 * increment_sleepiness will increase these values.
 * Depending on personality and desire, a creature may decide to eat/drink/sleep before it absolutely
 * needs to.  Needs override desires and the greatest need takes action priority.  If there are no
 * needs, the greatest desire takes priority.
 * 
 * @author  : Robin Osborne
 * @version : 2012-08-14
 */
public class Creature extends GameObject
{
    private static final long serialVersionUID = 13L;	
	
    // instance variables - replace the example below with your own
    private int physical;
    private int strength;
    private int agility;
    private int mental;
    private int willpower;
    private int intelligence;
    private int spiritual;
    private int spirit;
    private int charisma;
    private int awareness;
    
    private int empathy = 50;     //vs callousness
    private int discipline = 50;  //vs instinct
    
    private int charity = 50;     //vs greed
    private int chastity = 50;    //vs lust 
    private int harmony = 50;     //vs envy
    private int humility = 50;    //vs pride
    private int industry = 50;    //vs sloth
    private int serenity = 50;    //vs wrath    
    private int temperance = 50;  //vs gluttony
    
    private int bravery = 50;     //vs cowardice
    private int kindness = 50;    //vs cruelty
    private int contentment = 50; //vs ambition
    
    //NEEDS - overrides goals and desires
    private int need_food = 0;
    private int need_drink = 0;
    private int need_sleep = 0;
    private int need_aid = 0;
    
    private int NF = 1;
    private int ND = 2;
    private int NS = 3;
    private int NA = 4;

    //DESIRES - overrides goals after 50
    private int desire_food = 0;
    private int desire_drink = 0;
    private int desire_sleep = 0;
    private int desire_aid = 0;
    private int desire_social = 0;
    private int desire_entertain = 0;    
    
    //Base desires    
    private int DF = 10;
    private int DD = 11;
    private int DS = 12;
    private int DA = 13;
    private int DO = 14;
    private int DE = 15;
    //Social/entertainment desires    
    private int DSF = 20; //Desire share food
    private int DSD = 21; //Desire share drink
    private int DSA = 22; //Desire aid other
    private int DSS = 23; //Desire sleep other    
    
    private int action = 0; //0 is no action
    
    //A map of relations with other creatures by integer -100 to +100
	private HashMap<Creature, Integer> relationMap; 
	private ArrayList<Creature> knownOthers;
	
	//Close relatives are noted
    private Creature mother = null;
    private Creature father = null;
    private ArrayList<Creature> childList = null;    
	
    /**
     * No input Constructor for objects of class Creature
     */
    public Creature()
    {
    	super();
    	
    	relationMap = new HashMap<Creature,Integer>();
    	knownOthers = new ArrayList<Creature>();
    	
        // initialise instance variables
        strength = 0;
        agility = 0;
        
        physical = 0; 
        
        willpower = 0;
        intelligence = 0;
        
        mental = 0;         
        
        spirit = 0;
        charisma = 0;
        
        spiritual = 0;        
        
        awareness = 0;       
    }
    

    /**
     * Constructor for objects of class Creature
     */
    public Creature(int strength, int agility, int willpower, int intelligence, int spirit,
        int charisma, int awareness)
    {
    	super();
    	
    	relationMap = new HashMap<Creature,Integer>(); 
    	knownOthers = new ArrayList<Creature>();    	
    	
        // initialise instance variables
        this.strength = strength;
        this.agility = agility;
        
        physical = ((strength+agility)/2); 
        
        this.willpower = willpower;
        this.intelligence = intelligence;
        
        mental = ((willpower+intelligence)/2);         
        
        this.spirit = spirit;
        this.charisma = charisma;
        
        spiritual = ((spirit+charisma)/2);        
        
        this.awareness = (((physical+mental+spiritual)/3) + awareness);       
    }
    
    
    public void setPersonality(int empathy, int discipline, int charity, int chastity, int serenity,
        int harmony, int humility, int industry, int temperance)
    {        
        this.empathy = empathy;        //vs callousness
        this.discipline = discipline;  //vs instinct
    
        this.charity = charity;        //vs greed
        this.chastity = chastity;      //vs lust
        this.harmony = harmony;       //vs envy
        this.humility = humility;     //vs pride
        this.industry = industry;     //vs sloth
        this.serenity = serenity;     //vs wrath         
        this.temperance = temperance; //vs gluttony
    
        if(empathy < 50) {                 //callousness gives
            bravery += (50-empathy);       //more brave
            kindness -= (50-empathy);      //less kind
            contentment -= (50-empathy);   //less content         
        }
        else if(empathy > 50) {            //empathy gives
            bravery -= (empathy-50);       //less brave 
            kindness += (empathy-50);      //more kind
            contentment += (empathy-50);   //more content          
        }
        
        if(discipline < 50) {              //instinct gives
            bravery -= (50-discipline);       //less brave
            kindness += (50-discipline);      //more kind 
            contentment -= (50-discipline);   //less content          
        }
        else if(discipline > 50) {         //discipline gives
            bravery += (discipline-50);       //more brave 
            kindness -= (discipline-50);      //less kind
            contentment += (discipline-50);   //more content          
        }              
        
        if(charity < 50) {                 //greed gives
            bravery -= (50-charity);       //less brave
            kindness -= (50-charity);      //less kind 
            contentment -= (50-charity);   //less content
            desire_food += 10*((50-charity)/10);
            desire_drink += 10*((50-charity)/10);          
        }
        else if(charity > 50) {            //charity gives
            bravery += (charity-50);       //more brave 
            kindness += (charity-50);      //more kind
            contentment += (charity-50);   //more content          
        }
        
        if(chastity < 50) {                //lust gives
            bravery += (50-chastity);       //more brave
            kindness -= (50-chastity);      //less kind 
            contentment -= (50-chastity);   //less content   
            desire_food += 10*((50-chastity)/10);
            desire_drink += 10*((50-chastity)/10);
        }
        else if(chastity > 50) {           //chastity gives
            bravery -= (chastity-50);       //less brave 
            kindness += (chastity-50);      //more kind
            contentment += (chastity-50);   //more content          
        }
        
        if(harmony < 50) {                 //envy gives
            bravery += (50-harmony);       //more brave
            kindness -= (50-harmony);      //less kind 
            contentment -= (50-harmony);   //less content          
        }
        else if(harmony > 50) {            //harmony gives
            bravery -= (harmony-50);       //less brave 
            kindness += (harmony-50);      //more kind
            contentment += (harmony-50);   //more content          
        }
        
        if(humility < 50) {                //pride gives
            bravery += (50-humility);       //more brave
            kindness -= (50-humility);      //less kind 
            contentment -= (50-humility);   //less content          
        }
        else if(humility > 50) {           //humility gives
            bravery -= (humility-50);       //less brave 
            kindness += (humility-50);      //more kind
            contentment += (humility-50);   //more content          
        }
        
        if(industry < 50) {                //sloth gives
            bravery -= (50-industry);       //less brave
            kindness += (50-industry);      //more kind 
            contentment += (50-industry);   //more content      
            desire_sleep += 10*((50-industry)/10);
        }
        else if(industry > 50) {           //industry gives
            bravery += (industry-50);       //more brave 
            kindness -= (industry-50);      //less kind
            contentment -= (industry-50);   //less content          
        }
        
        if(serenity < 50) {                //wrath gives
            bravery += (50-serenity);       //more brave
            kindness -= (50-serenity);      //less kind 
            contentment -= (50-serenity);   //less content          
        }
        else if(serenity > 50) {           //serenity gives
            bravery -= (serenity-50);       //less brave 
            kindness += (serenity-50);      //more kind
            contentment += (serenity-50);   //more content          
        }
        
        if(temperance < 50) {              //gluttony gives
            bravery += (50-temperance);       //more brave
            kindness -= (50-temperance);      //less kind 
            contentment -= (50-temperance);   //less content    
            desire_food += 10*((50-temperance)/10);
            desire_drink += 10*((50-temperance)/10);             
        }
        else if(temperance > 50) {         //temperance gives
            bravery -= (temperance-50);       //less brave 
            kindness -= (temperance-50);      //more kind
            contentment += (temperance-50);   //more content          
        }        
                
        
        if(bravery > 100) {     
            bravery = 100;                 //wrecklessly brave
        }
        else if(bravery < 0) {
            bravery = 0;                   //utter coward
        }
        
        if(kindness > 100) {
            kindness = 100;                //saintly kind
        }
        else if(kindness < 0) {
            kindness = 0;                  //cruel bastard
        }
        
        if(contentment > 100) {
            contentment = 100;            //utterly content
        }
        else if(contentment < 0) {
            contentment = 0;              //dangerously ambitious
        }  
        
        if(desire_food > 40) {
            desire_food = 40;
        }
        if(desire_drink > 40) {
            desire_drink = 40;
        }
        if(desire_sleep > 40) {
            desire_sleep = 40;
        }
        if(desire_aid > 40) {
            desire_aid = 40;
        }        
                
    }    
    
    public int getBravery()
    {
        return bravery;
    }
    
    
    public int getKindness()
    {
        return kindness;
    }    
    
    
    public int getContentment()
    {
        return contentment;
    }
    
    public void increment_hunger()
    {
        need_food += 10;
        if(need_food > 100) {
            need_food = 100;  //later can add additional consequences instead of cap
        }         
        desire_food += 10;
        if(desire_food > 100) {
            desire_food = 100;
        }
        
        check_hunger();
    }
    
    public void increment_thirst()
    {
        need_drink += 10;
        if(need_drink > 100) {
            need_drink = 100;  //later can add additional consequences instead of cap
        }         
        desire_drink += 10;    
        if(desire_drink > 100) {
            desire_drink = 100;
        }        
        check_thirst();        
    } 
    
    public void increment_sleepiness()
    {
        need_sleep += 10;
        if(need_sleep > 100) {
            need_sleep = 100;  //later can add additional consequences instead of cap
        }        
        desire_sleep += 10; 
        if(desire_sleep > 100) {
            desire_sleep = 100;
        }        
        check_sleepiness();         
    }     
      
    public void increment_pain()
    {
        need_aid += 10;
        if(need_aid > 100) {
            need_aid = 100;  //later can add additional consequences instead of cap
        }         
        desire_aid += 10;
        if(desire_aid > 100) {
            desire_aid = 100;
        }        
        check_pain();         
    }  
    
    public void increment_isolation()
    {
        desire_social += 10;
        if(desire_social > 100) {
        	desire_social = 100;  //later can add additional consequences instead of cap
        }        
        
        check_social();         
    }     
      
    public void increment_boredom()
    {
        desire_entertain += 10;
        if(desire_entertain > 100) {
        	desire_entertain = 100;  //later can add additional consequences instead of cap
        }         
        
        check_entertain();         
    }
    
    public void decrement_hunger()
    {
        need_food -= 10;
        if(need_food < 0) {
            need_food = 0;
        }         
        desire_food -= 10;
        if(desire_food < 0) {
            desire_food = 0;
        }
        
        check_hunger();
    }
    
    public void decrement_thirst()
    {
        need_drink -= 10;
        if(need_drink < 0) {
            need_drink = 0;
        }         
        desire_drink -= 10;    
        if(desire_drink < 0) {
            desire_drink = 0;
        }        
        check_thirst();        
    } 
    
    public void decrement_sleepiness()
    {
        need_sleep -= 10;
        if(need_sleep < 0) {
            need_sleep = 0;
        }        
        desire_sleep -= 10; 
        if(desire_sleep < 0) {
            desire_sleep = 0;
        }        
        check_sleepiness();         
    }     
      
    public void decrement_pain()
    {
        need_aid -= 10;
        if(need_aid < 0) {
            need_aid = 0; 
        }         
        desire_aid -= 10;
        if(desire_aid < 0) {
            desire_aid = 0;
        }        
        check_pain();         
    }
    
    public void decrement_isolation()
    {
        desire_social -= 10;
        if(desire_social < 0) {
        	desire_social = 0;  
        }        
        
        check_social();         
    }     
      
    public void decrement_boredom()
    {
        desire_entertain -= 10;
        if(desire_entertain < 0) {
        	desire_entertain = 0;  
        }         
        
        check_entertain();         
    }    
    
    protected void check_hunger()
    {
        if(need_food > 50) {
            if( (need_drink <= need_food) && (need_sleep <= need_food) && (need_aid <= need_food) ) {
                action = NF;
                System.out.println(this.getName()+" needs food.");                 
            }
        }
        else if( (need_drink > 50) || (need_sleep > 50) || (need_aid > 50) ) {
        }
        else if(desire_food > 50) {
            if( (desire_drink <= desire_food) && (desire_sleep <= desire_food)
               && (desire_aid <= desire_food) && (desire_social <= desire_food)
               && (desire_entertain <= desire_food)) {
                action = DF;
                System.out.println(this.getName()+" desires food.");                 
            }            
        }
    }
    
    protected void check_thirst()
    {
        if(need_drink > 50) {
            if( (need_food <= need_drink) && (need_sleep <= need_drink) && (need_aid <= need_drink) ) {
                action = ND;
                System.out.println(this.getName()+" needs drink.");                 
            }
        }
        else if( (need_food > 50) || (need_sleep > 50) || (need_aid > 50) ) {
        }        
        else if(desire_drink > 50) {
            if( (desire_food <= desire_drink) && (desire_sleep <= desire_drink)
               && (desire_aid <= desire_drink) && (desire_social <= desire_drink)
               && (desire_entertain <= desire_drink)) {
                action = DD;
                System.out.println(this.getName()+" desires drink.");                 
            }            
        }        
    }
    
    protected void check_sleepiness()
    {
        if(need_sleep > 50) {
            if( (need_food <= need_sleep) && (need_drink <= need_sleep) && (need_aid <= need_sleep) ) {
                action = NS;
                System.out.println(this.getName()+" needs sleep.");                  
            }
        }
        else if( (need_food > 50) || (need_drink > 50) || (need_aid > 50) ) {
        }        
        else if(desire_sleep > 50) {
            if( (desire_food <= desire_sleep) && (desire_drink <= desire_sleep)
               && (desire_aid <= desire_sleep) && (desire_social <= desire_sleep)
               && (desire_entertain <= desire_sleep)) {
                action = DS;
                System.out.println(this.getName()+" desires sleep.");                 
            }            
        }         
    }
    
    protected void check_pain()
    {
        if(need_aid > 50) {
            if( (need_food <= need_aid) && (need_drink <= need_aid) && (need_sleep <= need_aid) ) {
                action = NA;
                System.out.println(this.getName()+" needs aid.");                  
            }
        }
        else if( (need_food > 50) || (need_drink > 50) || (need_sleep > 50) ) {
        }         
        else if(desire_aid > 50) {
            if( (desire_food <= desire_aid) && (desire_drink <= desire_aid)
               && (desire_sleep <= desire_aid) && (desire_social <= desire_aid)
               && (desire_entertain <= desire_aid)) {
                action = DA;
                System.out.println(this.getName()+" desires aid.");                 
            }            
        }        
    }
       
    protected void check_social()
    {
        if( (need_food > 50) || (need_drink > 50) || (need_sleep > 50) || (need_aid > 50)) {
        }  
        else if(desire_social > 50) {
            if( (desire_food <= desire_social) && (desire_drink <= desire_social)
               && (desire_sleep <= desire_social) && (desire_aid <= desire_social)
               && (desire_entertain <= desire_social)) {
                action = DO;
                System.out.println(this.getName()+" desires social contact.");                 
            }            
        }        
    }    
        
    protected void check_entertain()
    {
        if( (need_food > 50) || (need_drink > 50) || (need_sleep > 50) || (need_aid > 50)) {
        }  
        else if(desire_entertain > 50) {
            if( (desire_food <= desire_entertain) && (desire_drink <= desire_entertain)
               && (desire_sleep <= desire_entertain) && (desire_aid <= desire_entertain)
               && (desire_social <= desire_entertain)) {
                action = DE;
                System.out.println(this.getName()+" desires entertainment.");                 
            }            
        }
    }          
    
    public int get_need_food()
    {
        return need_food;
    }
    
    public int get_need_drink()
    {
        return need_drink;
    }
    
    public int get_need_sleep()
    {
        return need_sleep;
    }
    
    public int get_need_aid()
    {
        return need_aid;
    }      
    
    public int get_desire_food()
    {
        return desire_food;
    }
    
    public int get_desire_drink()
    {
        return desire_drink;
    }
    
    public int get_desire_sleep()
    {
        return desire_sleep;
    }
    
    public int get_desire_aid()
    {
        return desire_aid;
    }    
    
    public int get_desire_social()
    {
        return desire_sleep;
    }
    
    public int get_desire_entertain()
    {
        return desire_aid;
    }     
      
    public Creature get_mother()
    {
        return mother;
    }    
    
    public Creature get_father()
    {
        return father;
    }
    
    public ArrayList<Creature> get_children()
    {
        return childList;
    }    
    
    public void set_mother(Creature mother)
    {
        this.mother = mother;
        knownOthers.add(mother);
    }    
    
    public void set_father(Creature father)
    {
        this.father = father;
        knownOthers.add(father);        
    }
    
    public void add_child(Creature child)
    {
    	if(childList != null) {
            childList.add(child);            
    	}
    	else {
    		childList = new ArrayList<Creature>();
            childList.add(child);              
    	}
        knownOthers.add(child);    	
    }    
    
    public void set_relation(Creature creature, int value) 
    {
    	if(relationMap != null) {    	
            relationMap.put(creature, value);            
    	}
    	else {
            relationMap = new HashMap<Creature,Integer>();
            relationMap.put(creature, value);            
    	}
        knownOthers.add(creature);     	
    }
    
    public void add_relation(Creature creature, int value) 
    {
    	int temp;
    	
    	if(relationMap.containsKey(creature)) {  
    		temp = relationMap.get(creature) + value;
    		if(temp > 100) {
    			temp = 100;
    		}
            relationMap.put(creature, value);
    	}     	
    }    
    
    
    public void subtract_relation(Creature creature, int value) 
    {
    	int temp;
    	
    	if(relationMap.containsKey(creature)) {  
    		temp = relationMap.get(creature) - value;
    		if(temp < 0) {
    			temp = 0;
    		}
            relationMap.put(creature, value);
    	}
    }    
    
    public String get_current_action()
    {
        String state = "";
        
        if(action == NF) {
            state = "Need Food Action";
        }
        else if(action == ND) {
            state = "Need Drink Action";
        }  
        else if(action == NS) {
            state = "Need Sleep Action";
        } 
        else if(action == NA) {
            state = "Need Aid Action";
        } 
        else if(action == DF) {
            state = "Desire Food Action";
        }
        else if(action == DD) {
            state = "Desire Drink Action";
        }         
        else if(action == DS) {
            state = "Desire Sleep Action";
        } 
        else if(action == DA) {
            state = "Desire Aid Action";
        }
        else {
            state = "Other Action";
        }
        
        return state;
    }
    
    public void set_current_action(int action)
    {        
    	this.action = action;
    }    
    
    public void eat()
    {
        need_food -= 20;
        if(need_food < 0) {
            need_food = 0;
        }        
        desire_food -= 20;
        if(desire_food < 0) {
            desire_food = 0;
        }         
        System.out.println(this.getName()+" eats.");
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();        
    }    
    
    
    public void share_food(Creature friend)
    {
        need_food -= 10;
        if(need_food < 0) {
            need_food = 0;
        }        
        desire_food -= 10;
        if(desire_food < 0) {
            desire_food = 0;
        }         
        desire_social -= 10;
        if(desire_social < 0) {
            desire_social = 0;
        }       
        
        friend.decrement_hunger();
        friend.decrement_isolation();
        
        friend.add_relation(this, 5);
        add_relation(friend, 5);        
        
        System.out.println(this.getName()+" shares food with "+friend.getName());
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();        
    }    
    
    public void drink()
    {
        need_drink -= 20;
        if(need_drink < 0) {
            need_drink = 0;
        }        
        desire_drink -= 20; 
        if(desire_drink < 0) {
            desire_drink = 0;
        }        
        System.out.println(this.getName()+" drinks.");        
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();            
    }    
    
    public void share_drink(Creature friend)
    {
        need_drink -= 10;
        if(need_drink < 0) {
        	need_drink = 0;
        }        
        desire_drink -= 10;
        if(desire_drink < 0) {
        	desire_drink = 0;
        }         
        desire_social -= 10;
        if(desire_social < 0) {
            desire_social = 0;
        }       
        
        friend.decrement_thirst();
        friend.decrement_isolation();
        
        friend.add_relation(this, 5);
        add_relation(friend, 5);        
        
        System.out.println(this.getName()+" shares a drink with "+friend.getName());
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();        
    }
    
    public void nap()
    {
        need_sleep -= 10;
         if(need_sleep < 0) {
            need_sleep = 0;
        }        
        desire_sleep -= 10;
        if(desire_sleep < 0) {
            desire_sleep = 0;
        }       
        
        need_food += 2;
        if(need_food > 100) {
            need_food = 100;
        }       
        desire_food += 2;
        
        need_drink += 2;
        if(need_drink > 100) {
            need_drink = 100;
        }       
        desire_drink += 2;        
        
        int desire_adjust = 0;
        if(charity < 50) {                 //greed gives
        	desire_adjust += 1*((50-charity)/10);          
        }
        if(chastity < 50) {                //lust gives  
        	desire_adjust += 1*((50-chastity)/10);           
        }        
        if(temperance < 50) {              //gluttony gives  
        	desire_adjust += 1*((50-temperance)/10);            
        }    
        
        if(desire_adjust > 15) {
        	desire_adjust = 15;
        }        
        desire_food += desire_adjust;
        desire_drink += desire_adjust;        
        
        if(desire_food > 100) {
            desire_food = 100;
        }
        if(desire_drink > 100) {
            desire_drink = 100;
        }        
        
        System.out.println(this.getName()+" naps.");         
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();          
    }
 
    public void share_nap(Creature friend)
    {
        need_sleep -= 10;
        if(need_sleep < 0) {
        	need_sleep = 0;
        }                
        desire_social -= 10;
        if(desire_social < 0) {
            desire_social = 0;
        } 
        desire_entertain -= 10;
        if(desire_entertain < 0) {
        	desire_entertain = 0;
        }        
        
        friend.decrement_sleepiness();
        friend.decrement_isolation();
        friend.decrement_boredom();        
        
        friend.add_relation(this, 10);
        add_relation(friend, 10);         
        
        System.out.println(this.getName()+" sleeps with "+friend.getName());
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();        
    }    
    
    public void sleep()
    {
        need_sleep = 0;       
        desire_sleep = 0;
   
        if(industry < 50) {                //sloth gives    
            desire_sleep += 5*((50-industry)/10);
        }       
        
        need_aid -= 10;
        if(need_aid < 0) {
            need_aid = 0;
        }       
        desire_aid -= 10; 
        if(desire_aid < 0) {
            desire_aid = 0;
        }       
        
        need_food += 10;
        if(need_food > 100) {
            need_food = 100;
        }       
        desire_food += 10;
        
        need_drink += 10;
        if(need_drink > 100) {
            need_drink = 100;
        }       
        desire_drink += 10;        
        
        int desire_adjust = 0;
        if(charity < 50) {                 //greed gives
        	desire_adjust += 5*((50-charity)/10);          
        }
        if(chastity < 50) {                //lust gives  
        	desire_adjust += 5*((50-chastity)/10);           
        }        
        if(temperance < 50) {              //gluttony gives  
        	desire_adjust += 5*((50-temperance)/10);            
        }    
        
        if(desire_adjust > 30) {
        	desire_adjust = 30;
        }        
        desire_food += desire_adjust;
        desire_drink += desire_adjust;        
        
        if(desire_food > 100) {
            desire_food = 100;
        }
        if(desire_drink > 100) {
            desire_drink = 100;
        }        
        
        System.out.println(this.getName()+" sleeps.");         
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();
    }    
    
    public void aid_self()
    {
        need_aid -= 20;
        if(need_aid < 0) {
            need_aid = 0;
        }       
        desire_aid -= 20; 
        if(desire_aid < 0) {
            desire_aid = 0;
        }        
        System.out.println(this.getName()+" heals.");         
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();        
    }
    
    public void aid_other(Creature friend)
    {
        desire_social -= 10;
        if(desire_social < 0) {
            desire_social = 0;
        }    	
    	
        friend.decrement_pain();
        friend.decrement_pain(); 
        friend.decrement_isolation();    
        
        friend.add_relation(this, 15);
        add_relation(friend, 5);        
        
        System.out.println(this.getName()+" aids "+friend.getName());         
        
        action = 0;
        check_social();
        check_entertain();        
        check_sleepiness();       
        check_hunger();      
        check_thirst();    
        check_pain();        
    }    
	
    /**
     *  This is the clone method for Creature.  It overrides the clone()
     *  method of GameObject, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public Creature clone()
    {
    	Creature copy = (Creature)super.clone();   	
    	
        return copy;
    } 
    
    
    /**
     *  A toString method for Creature that returns the name, id, picture, and
     *  location of the Creature.
     *  
     *  @return : String with information on the name, id, picture, and location of the
     *            World
     */
    public String toString()
    { 
    	return ("Creature: " + this.getName() + " with ID: " + this.getID() + " , picture: " +
                 this.getPicture() + " , location " + this.getLocation().toString() + " ");
    }
    
    
    /**
     *  An equals method for Creature that compares id, name, and location values.
     *  Name should ensure that Creature objects of differing types will not be equated as equal,
     *  even if there is id overlap between subclasses.
     *  
     *  @param obj : the object for comparison 
     *  @return    : a boolean value true or false 
     */
    public boolean equals(Object obj)
    { 
    	if(this == obj) {
    		return true;  //The same object
    	}
    	if(!(obj instanceof Creature)) {
    		return false;  //Not of the same type
    	}
    	
    	Creature other = (Creature)obj;
    	
    	//Compare the id and location of Creature to the id, name, and location
    	//of the other Creature and returns true or false
    	return( ( other.getID() == this.getID() ) && ( other.getName() == this.getName() ) &&
    			( other.getLocation().equals(this.getLocation() ) ) );
    }    
    
    
    /**
     *  A basic hashCode method for Creature, for the id, name, and location
     *  field that are compared in the equals method.
     *  
     *  @return : an integer value
     */
    public int hashCode()
    {     
    	int result = 37;
    	
    	result += super.hashCode();  	
    	
    	return result;
    }
}

