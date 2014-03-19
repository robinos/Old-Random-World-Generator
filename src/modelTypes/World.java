package modelTypes;
import java.util.HashMap;
import java.util.Set;

/**
 * World is the main game object.  
 * 
 * @author  : Robin Osborne
 * @version : Version 0.1, 2012-03-07
 *
 */

public class World extends GameObject
{
	//Subclasses of World should have a serialVersionUID of 12xL where x
	//is a value 1-9.		
    private static final long serialVersionUID = 12L;	
	
	private int width;
	private int height;
	private HashMap<Integer, Terrain> terrainMap;	
	
	/**
	 * No argument constructor for a World.
	 */
	public World()
	{
		super();
		//800x600 world map with 10x10 pixel terrain squares
		width = 128;
		height = 128;
		terrainMap = new HashMap<Integer, Terrain>();
	}
	
	
	/**
	 * Constructor for a World.
	 */
	public World(int id, GridVector location, String name, String picture, int width, int height)
	{
		super(id,location,name,picture);
		//800x600 world map with 10x10 pixel terrain squares
		this.width = width;
		this.height = height;
		terrainMap = new HashMap<Integer, Terrain>();
	}
	
	
	/**
	 * getWidth returns the integer width of the World.
	 * 
	 * @return : width as an integer
	 */
	public int getWidth()
	{
		return width;
	}
	
	
	/**
	 * setWidth sets the World's width integer.
	 * 
	 * @param width : an integer representing width
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}	
	
	
	/**
	 * getHeight returns the integer height of the World.
	 * 
	 * @return : height as an integer
	 */
	public int getHeight()
	{
		return height;
	}
	
	
	/**
	 * setHeight sets the World's height integer.
	 * 
	 * @param width : an integer representing height
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}	
	
	
	/**
	 * getTerrainMap returns the terrain HashMap of the World.
	 * 
	 * @return : terrainMap as a HashMap
	 */
	public HashMap<Integer,Terrain> getTerrainMap()
	{
		return terrainMap;
	}
	
	
	/**
	 * setTerrainMAp sets the World's terrain HashMap.
	 * 
	 * @param terrainMap : a HashMap of Terrain keyed by an id integer, representing a terrain map
	 */
	public void setTerrainMap(HashMap<Integer,Terrain> terrainMap)
	{
		this.terrainMap = terrainMap;
	}
	
	
	/**
	 * addTerrain adds Terrain to the World's terrain HashMap.  It overwrites any current
	 * terrain in that position.
	 * 
	 * @param loc : an integer location
	 * @param terrain : a Terrain object
	 */
	public void addTerrain(Integer loc, Terrain terrain)
	{
		terrainMap.put(loc, terrain);
	}
	
	
	/**
	 * switchTerrain switches the position of two pieces of Terrain in the World's
	 * terrain HashMap.
	 * 
	 * @param vector1 : a GridVector
	 * @param vector2 : a GridVector
	 */
	public void switchTerrain(Integer id1, Integer id2)
	{
		Terrain terrain1 = terrainMap.get(id1);
		Terrain terrain2 = terrainMap.get(id2);
		
		if(terrain1 == null || terrain2 == null) {
			return;
		}
		
		terrainMap.put(id1, terrain2);
		terrainMap.put(id2, terrain1);		
	}
	
	
    /**
     *  This is the clone method for World.  It overrides the clone()
     *  method of GameObject, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public World clone()
    {
    	World copy = (World)super.clone();
        	     	     
        //new itemMap HashMap
        copy.terrainMap = new HashMap<Integer, Terrain>(); 
         
        Set<Integer> terrainKeys = terrainMap.keySet();        
        
        //copy the mapped elements of terrainMap
        for(Integer iLoc : terrainKeys) {
           //put the element of terrainMap in the copy's terrainMap
           copy.terrainMap.put(iLoc,terrainMap.get(iLoc));
        }
        //End for loop for terrainMap elements    	
    	
        return copy;
    } 
    
    
    /**
     *  A toString method for World that returns the name, id, picture, and
     *  location of the World.
     *  
     *  @return : String with information on the name, id, picture, and location of the
     *            World
     */
    public String toString()
    { 
    	return ("World: " + this.getName() + " with ID: " + this.getID() + " , picture: " +
                 this.getPicture() + " , location " + this.getLocation().toString() + " ");
    }
    
    
    /**
     *  An equals method for World that compares id, name, and location values.
     *  Name should ensure that World objects of differing types will not be equated as equal,
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
    	if(!(obj instanceof World)) {
    		return false;  //Not of the same type
    	}
    	
    	World other = (World)obj;
    	
    	//Compare the id and location of GameObject to the id, name, and location
    	//of the other GameObject and returns true or false
    	return( ( other.getID() == this.getID() ) && ( other.getName() == this.getName() ) &&
    			( other.getLocation().equals(this.getLocation() ) ) );
    }    
    
    
    /**
     *  A basic hashCode method for World, for the id, name, and location
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
