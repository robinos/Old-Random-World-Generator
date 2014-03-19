package modelTypes;

/**
 * Person base class for all civilised creatures.
 * Adds goals.
 * 
 * @author  : Robin Osborne
 * @version : 2012-08-14
 */
public class Person extends Creature
{
    private static final long serialVersionUID = 14L;
        
    //GOALS - overrides desires up to threshhold, but never needs
    private int goal_food = 0;
    private int goal_drink = 0;
    private int goal_sleep = 0;
    private int goal_aid = 0;
    private int goal_social = 0;
    private int goal_entertain = 0;   
    
    private int NF = 1;
    private int ND = 2;
    private int NS = 3;
    private int NA = 4;   
    
    //Base desires
    private int DF = 10;
    private int DD = 11;
    private int DS = 12;
    private int DA = 13;
    private int DO = 14;  //default social
    private int DE = 15;  //default entertainment
    //Person social/entertainment desires
    private int DH = 30;  //Desire hug other
    private int DC = 31;  //Desire chat other  
    private int DJ = 32;  //Desire joke with other
    private int DP = 33;  //Desire play with other    
    private int DY = 34;  //Desire solitary play
    private int DR = 35;  //Desire relax
    //Complex desires
    
    //Base goals in case needed
    private int GF = 101; //Goal Food
    private int GD = 102; //Goal Drink
    private int GS = 103; //Goal Sleep
    private int GA = 104; //Goal Demand Aid
    private int GO = 105; //Goal Social Contact
    private int GE = 106; //Goal Entertainment
    //Person basic goals
    private int GH = 120; //Goal Hunt
    private int GG = 121; //Goal Gather
    private int GC = 122; //Goal Cook
    
    private int action = 0; //0 is no action    
    private String name = "Creature";
    
    /**
     * No input Constructor for objects of class Person
     */
    public Person()
    {
    	super();
    }
    
    protected void check_goal()
    {
        if( (get_need_food() > 50) || (get_need_drink() > 50) || (get_need_sleep() > 50) ||
            (get_need_aid() > 50)) {
        }
        else if( (get_desire_food() > 50) || (get_desire_drink() > 50) || (get_desire_sleep() > 50) ||
                (get_desire_aid() > 50) || (get_desire_social() > 50) || (get_desire_entertain() > 50) ) {
        }
        else {
            
        }
    }   
    
    /**
     * Constructor for objects of class Person
     */
    public Person(int strength, int agility, int willpower, int intelligence, int spirit,
        int charisma, int awareness)
    {
    	super();    	
    }    
    
    
    /**
     *  This is the clone method for Creature.  It overrides the clone()
     *  method of GameObject, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public Person clone()
    {
    	Person copy = (Person)super.clone();   	
    	
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
    	return ("Person: " + this.getName() + " with ID: " + this.getID() + " , picture: " +
                 this.getPicture() + " , location " + this.getLocation().toString() + " ");
    }
    
    
    /**
     *  An equals method for Person that compares id, name, and location values.
     *  Name should ensure that Person objects of differing types will not be equated as equal,
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
    	if(!(obj instanceof Person)) {
    		return false;  //Not of the same type
    	}
    	
    	Person other = (Person)obj;
    	
    	//Compare the id and location of Person to the id, name, and location
    	//of the other Person and returns true or false
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
