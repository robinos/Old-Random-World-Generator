package modelTypes;

/**
 * Water is a model class holding information for areas of water.  This
 * represents all water types and is a subclass of Terrain.
 * 
 * @author  : Robin Osborne
 * @version : 0.1, 2012-03-07
 *
 */

public class Water extends Terrain
{
	//Subclasses of Water should have a serialVersionUID of 111xL where x
	//is a value 1-9.		
    private static final long serialVersionUID = 111L;	
 
    //Instance variables    
    private float depth;
    
	
	/**
	 * This is the constructor for water.
	 * 
	 * See GameObject for id, location, name, and picture
	 * See Terrain for subtypeName, vegetation, and hinderance
	 * 
	 * @param depth
	 * 
	 */
	public Water(int id, GridVector location, String name, String picture, String subtypeName,
			     StringBuffer vegetation, int hinderance, float depth)
	{
		super(id, location, name, picture, subtypeName, vegetation, hinderance);
		this.depth = depth;
	}
	
	
	/**
	 * This is the no argument constructor for water.
	 */
	public Water()
	{
		super();
		depth = 0;
	}    
    
	
	/**
	 * getDepth returns the depth integer of the Water.
	 * 
	 * @return : an integer representing the water depth
	 */
	public float getDepth()
	{
		return depth;
	}
	
	
	/**
	 * setDepth sets the Water depth integer.
	 * 
	 * @param depth : an integer representing the desired water depth in meters
	 */
	public void setDepth(float depth)
	{
		this.depth = depth;
	}
	
	
	
    /**
     *  This is the clone method for Water.  It overrides the clone()
     *  method of Terrain, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public Water clone()
    {	  
          return (Water)super.clone();
    } 
    
    
    /**
     *  A toString method for Water that returns the name, id, picture, location,
     *  subtypeName, and depth of the Water object.
     *  
     *  @return : String with information on the name, id, picture, location,
     *            subtypeName, and depth of the Water object
     */
    public String toString()
    { 
    	return (super.toString() + ", Depth: " + depth + " ");
    }
    
    
    /**
     *  An equals method for Water objects that compares id, name, location, 
     *  subtypeName, and depth values.
     *  
     *  @param obj : the object for comparison 
     *  @return    : a boolean value true or false 
     */
    public boolean equals(Object obj)
    { 
    	if(this == obj) {
    		return true;  //The same object
    	}
    	if(!(obj instanceof Water)) {
    		return false;  //Not of the same type
    	}
    	
    	Water other = (Water)obj;
    	
    	//Compare the id and location of the Water object to the id, name, location,
    	//subtypeName, and depth of the other Water object and returns true or false
    	return( ( other.getID() == this.getID() ) && ( other.getName() == this.getName() ) &&
    			( other.getLocation().equals(this.getLocation()) ) &&
    			( other.getSubtypeName() == this.getSubtypeName() ) &&
    			( other.depth == depth) );
    }    
    
    
    /**
     *  A basic hashCode method for Water, for the id, name, location, subtypeName,
     *  and depth fields that are compared in the equals method.
     *  
     *  @return : an integer value
     */
    public int hashCode()
    {     
    	int result = 37;
    	Float dFloat = new Float(depth);
    	
    	result += super.hashCode();
    	result += dFloat.hashCode();    	   	
    	
    	return result;
    }
}
