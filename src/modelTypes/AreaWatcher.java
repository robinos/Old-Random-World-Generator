package modelTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * There is an AreaWatcher for each grid square of the world map.  An AreaWatcher
 * keeps track of terrain, creatures, objects, and items within an area.  A special exception
 * is the Settlement subclass, which keeps adds functionality for areas with settlements.
 * @author  : Robin Osborne
 * @version : 2012-08-16
 */
public class AreaWatcher extends GameObject implements Observer
{
    private static final long serialVersionUID = 21L;	
	
    //A submap of terrain for a unique location on the world map, with a GridVector for the 3D location
    //of different terrain types within an area
	private HashMap<GridVector, Terrain> terrainMap; 
	
    //A map of creatures by GridVector location within the area
	private HashMap<GridVector, Creature> creatureMap;    
	
    /**
     * No input Constructor for objects of class AreaWatcher
     */
    public AreaWatcher()
    {
        super();	
    }
    
    /**
     * Constructor for objects of class AreaWatcher
     */    
	public AreaWatcher(int id, GridVector location, String name, String picture)
	{
		super(id,location,name,picture);
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
        /**Terrain update code - used with changing terrain**/
        if(o instanceof Terrain && arg instanceof GridVector) {   
        	
        	Terrain terrain = (Terrain) o;
        	GridVector v = (GridVector) arg;
        	
        	if(!terrainMap.containsKey(v)) {
        		
        		terrainMap.put(v, terrain);       		
        	}
        } 
        
        /**Creature update code**/
        if(o instanceof Creature && arg instanceof GridVector) {
        	
        	Creature creature = (Creature) o;
        	GridVector v = (GridVector) arg;
        	
        	if(!creatureMap.containsKey(v)) {
        		
        		creatureMap.put(v, creature);       		
        	}
        }        
    }

    
    /**
     *  This is the clone method for AreaWatcher.  It overrides the clone()
     *  method of GameObject, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public AreaWatcher clone()
    {
    	AreaWatcher copy = (AreaWatcher)super.clone();
        	     	     
        //new terrainMap HashMap
        copy.terrainMap = new HashMap<GridVector, Terrain>(); 
         
        Set<GridVector> terrainKeys = terrainMap.keySet();        
        
        //copy the mapped elements of terrainMap
        for(GridVector vLoc : terrainKeys) {
           //put the element of terrainMap in the copy's terrainMap
           copy.terrainMap.put(vLoc.clone(),terrainMap.get(vLoc).clone());
        }
        //End for loop for terrainMap elements    	
    	
        //new creatureMap HashMap
        copy.creatureMap = new HashMap<GridVector, Creature>(); 
         
        Set<GridVector> creatureKeys = creatureMap.keySet();        
        
        //copy the mapped elements of terrainMap
        for(GridVector vLoc : creatureKeys) {
           //put the element of creatureMap in the copy's creatureMap
           copy.creatureMap.put(vLoc.clone(),creatureMap.get(vLoc).clone());
        }
        //End for loop for creatureMap elements         
        
        return copy;
    } 
    
    
    /**
     *  A toString method for AreaWatcher that returns the name, id, picture, and
     *  location of the AreaWatcher.
     *  
     *  @return : String with information on the name, id, picture, and location of the
     *            AreaWatcher
     */
    public String toString()
    { 
    	return ("AreaWatcher: " + this.getName() + " with ID: " + this.getID() + " , picture: " +
                this.getPicture() + " , location " + this.getLocation().toString() + " ");
    }
    
    
    /**
     *  An equals method for AreaWatcher that compares id, name, and location values.
     *  Name should ensure that AreaWatchers of differing types will not be equated as equal,
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
    	if(!(obj instanceof AreaWatcher)) {
    		return false;  //Not of the same type
    	}
    	
    	AreaWatcher other = (AreaWatcher)obj;
    	
    	//Compare the id and location of AreaWatcher to the id, name, and location
    	//of the other AreaWatcher and returns true or false
    	return( ( other.getID() == this.getID() ) && ( other.getName() == this.getName() ) &&
    			( other.getLocation().equals(this.getLocation() ) ) );
    }    
    
    
    /**
     *  A basic hashCode method for AreaWatcher, for the id, name, and location
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
