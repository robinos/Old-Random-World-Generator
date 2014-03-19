package modelTypes;
import java.io.Serializable;
import java.util.Observable;

/**
 * GameObject is the superclass of all game objects in the game world.
 * 
 * @author  : Robin Osborne
 * @version : Version 0.1, 2012-03-07
 *
 */

public class GameObject extends Observable implements Cloneable, Serializable 
{
	//Subclasses of GameObject should have a serialVersionUID of 1xL where x
	//is a value 1-9.		
    private static final long serialVersionUID = 1L;	
    
    //Instance variables
	private int id;
	private GridVector location;
	private String name;
	private String picture;
	
	
	/**
	 * This is the constructor for GameObject.
	 * 
	 * @param id
	 * @param location
	 * @param name
	 * @param picture
	 */
	public GameObject(int id, GridVector location, String name, String picture)
	{
		this.id = id;
		this.location = location;
		this.name = name;
		this.picture = picture;
	}
	
	
	/**
	 * This is the no argument constructor for GameObject.
	 */
	public GameObject()
	{
		id = 0;
		location = new GridVector(0,0,0,0,0);
		name = "";
		picture = "";
	}
	
	
	/**
	 * getID returns the integer id of the GameObject.
	 * 
	 * @return : an id as an integer
	 */
	public int getID()
	{
		return id;
	}
	
	
	/**
	 * setID sets the GameObjects ID integer.
	 * 
	 * @param id : an integer representing an id
	 */
	public void setID(int id)
	{
		this.id = id;
	}		
	
	
	/**
	 * getLocation returns the Terrain location GridVector.
	 * 
	 * @return : a location as a GridVector
	 */
	public GridVector getLocation()
	{
		return location;
	}
	
	
	/**
	 * setLocationXYZ sets the GameObject location based on x y and z coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setLocationXYZ(int x, int y, int z)
	{
		//x,y,z with 0 velocity, 0 direction
		GridVector location = new GridVector(x,y,z,0,0);
		this.location = location;
	}	
	
	
	/**
	 * setLocationVector sets the GameObjects location to be the same as a given GridVector.
	 * 
	 * @param id : a GridVector representing the GameObject's location
	 */
	public void setLocationVector(GridVector location)
	{
		this.location = location;
	}	

		
	/**
	 * getName returns the String name of the GameObject.
	 * 
	 * @return : a String representing the name of the GameObject
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * setName sets the GameObject name.
	 * 
	 * @param name : a String representing the desired name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	
	/**
	 * getPicture returns the String picture filename of the GameObject.
	 * 
	 * @return : a String representing the picture filename of the GameObject
	 */
	public String getPicture()
	{
		return picture;
	}
	
	
	/**
	 * setPicture sets the GameObject picture.
	 * 
	 * @param picture : a String representing the pictures filename
	 */
	public void setPicture(String picture)
	{
		this.picture = picture;
	}
    
    
    /**
     * moveObject is the base method for adding or removing a GameObject from a location.
     * Note this only handles the logical location of the game object, not
     * its actual disappearance as a sprite or graphic from the GUI
     * 
     * @param newLocation : the new location for the game object in question
     */
    public void moveObject(GridVector newLocation)
    {               	   
    	   //Save the old location
           GridVector oldLocation = location.clone(); 
           
           //Set the item to the new location            
           location = newLocation;
           
           //Notify the ObjectWatcher of the change in location           
           setChanged();
           
           //Notify observers of the change (This notifies the ObjectWatcher set to
           //observe GameObjects);
           notifyObservers(oldLocation);
    }    

    
    /**
     *  This is the clone method for GameObject.  It overrides the clone()
     *  method of object, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public GameObject clone()
    {
       try
       {
    	  GameObject copy = (GameObject)super.clone();
    	  copy.location = location.clone();
    	  copy.name = new String(name);
    	  copy.picture = new String(picture);    	  
    	  
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    } 
    
    
    /**
     *  A toString method for GameObject that returns the name, id, picture, and
     *  location of the GameObject.
     *  
     *  @return : String with information on the name, id, picture, and location of the
     *            GameObject
     */
    public String toString()
    { 
    	return ("Game Object " + name + " with ID: " + id + " , picture: " + picture + " , location " + location.toString() + " ");
    }
    
    
    /**
     *  An equals method for GameObject that compares id, name, and location values.
     *  Name should ensure that GameObjects of differing types will not be equated as equal,
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
    	if(!(obj instanceof GameObject)) {
    		return false;  //Not of the same type
    	}
    	
    	GameObject other = (GameObject)obj;
    	
    	//Compare the id and location of GameObject to the id, name, and location
    	//of the other GameObject and returns true or false
    	return( ( other.id == id ) && ( other.name == name ) && ( other.location.equals(location) ) );
    }    
    
    
    /**
     *  A basic hashCode method for GameObject, for the id, name, and location
     *  field that are compared in the equals method.
     *  
     *  @return : an integer value
     */
    public int hashCode()
    {     
    	int result = 37;
    	
    	result += 37 * result + name.hashCode();    	
    	result += 37 * result + id;
        result += 37 * result + location.hashCode();    	
    	
    	return result;
    }
    
}
