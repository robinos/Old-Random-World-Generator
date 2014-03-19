package modelTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

/**
 * Settlement is a subclass of AreaWatcher, used to keep track of creatures, objects, and items
 * within a settlement.  It further keeps track of buildings and citizens.
 * @author  : Robin Osborne
 * @version : 2012-08-16
 */
public class Settlement extends AreaWatcher
{
    private static final long serialVersionUID = 211L;	
	
    // instance variables    
	
    //A map of creatures "home" locations by GridVector location within the area - this is where they
    //will sleep
	private HashMap<GridVector, Creature> homeMap; 
	//A list of citizens
	private ArrayList<Creature> citizens;
	
    /**
     * No input Constructor for objects of class Settlement
     */
    public Settlement()
    {
        super();	
		citizens = new ArrayList<Creature>();        
    }
    
    /**
     * Constructor for objects of class Settlement
     */    
	public Settlement(int id, GridVector location, String name, String picture)
	{
		super(id,location,name,picture);
		citizens = new ArrayList<Creature>();
	} 
    
	/**
	 * 
	 */
	public void add_citizen(Creature person)
	{
		citizens.add(person);
	}
	
	/**
	 * 
	 */
	public void remove_citizen(Creature person)
	{
		citizens.remove(person);
	}	
	
    /**
     * update is an overwritten method of Observer, used in reaction to a change happening
     * in an observed class.  
     * 
     * @param o   : an Observable object (which should be an GameObject object of some sort)
     * @param arg : an Object (which should be a Location object) 
     */
    public void update(Observable o, Object arg)
    {                       
    }

    
    /**
     *  This is the clone method for Settlement.  It overrides the clone()
     *  method of AreaWatcher, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public Settlement clone()
    {
    	Settlement copy = (Settlement)super.clone();       
        
        return copy;
    } 
    
    
    /**
     *  A toString method for Settlement that returns the name, id, picture, and
     *  location of the Settlement.
     *  
     *  @return : String with information on the name, id, picture, and location of the
     *            Settlement
     */
    public String toString()
    { 
    	return ("Settlement: " + this.getName() + " with ID: " + this.getID() + " , picture: " +
                this.getPicture() + " , location " + this.getLocation().toString() + " ");
    }
    
    
    /**
     *  An equals method for Settlement that compares id, name, and location values.
     *  Name should ensure that Settlements of differing types will not be equated as equal,
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
    	if(!(obj instanceof Settlement)) {
    		return false;  //Not of the same type
    	}
    	
    	Settlement other = (Settlement)obj;
    	
    	//Compare the id and location of Settlement to the id, name, and location
    	//of the other Settlement and returns true or false
    	return( ( other.getID() == this.getID() ) && ( other.getName() == this.getName() ) &&
    			( other.getLocation().equals(this.getLocation() ) ) );
    }    
    
    
    /**
     *  A basic hashCode method for Settlement, for the id, name, and location
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
