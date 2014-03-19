package modelTypes;

/**
 * The GridVector class is a subclass of Vector3D, adding velocity and
 * direction information for GameObjects, and the ability to perform
 * vector addition (as limited by 8 basic grid directions).
 * 
 * @author  Robin Osborne
 * @version 0.1, 2012-03-07
 *
 */
public class GridVector extends Vector3D
{
	//Instance variables
	private float velocity;
	private float direction;
	
	
	/**
	 * This is the Constructor for GridVector.  Error checking is not done inside
	 * the GridVector class.
	 * 
	 * Vector3D parameters (x, y, z)
	 * 
	 * @param velocity  : The velocity as a float value 0-100
	 * @param direction : The direction as a degree 0 to 359
	 *                    Recommended: (0, 45, 90, 135, 180, 225, 270, 315)
	 */
	public GridVector(float x, float y, float z, float velocity, float direction)
	{
		super(x,y,z);
		this.velocity = velocity;
		this.direction = direction;
	}

	
	/**
	 * This is the no argument Constructor for Vector3D.
	 */
	public GridVector()
	{
		super();
		velocity = 0;
		direction = 0;
	}	
	
	
	/**
	 * getVelocity returns the float representing the velocity
	 * 
	 * @return : a float representing the velocity
	 */
	public float getVelocity()
	{
		return velocity;
	}

	
	/**
	 * setVelocity sets the float representing the velocity
	 * 
	 * @param : a float representing the velocity
	 */
	public void setVelocity(float velocity)
	{
		this.velocity = velocity;
	}	
	
	
	/**
	 * getDirection returns the float representing the direction
	 * 
	 * @return : a float representing the direction
	 */
	public float getDirection()
	{
		return direction;
	}

	
	/**
	 * setDirection sets the float representing the direction 
	 * 
	 * @param : a float representing the direction
	 */
	public void setDirection(float direction)
	{
		this.direction = direction;
	}	
	

	/**
	 * setFromVector sets this vector to be the same values as a given GridVector
	 * 
	 * @param vector
	 */
    public void setFromVector(GridVector vector)
    {
        setX(vector.getX());
        setY(vector.getY());
        setZ(vector.getZ());
        this.velocity = vector.velocity;
        this.direction = vector.direction;
    }

    
    /**
     * Adds the specified velocity value to this vector
     * 
     * @param velocity
     */
    public void addVelocity(float velocity)
    {
        this.velocity+=velocity;
    }    


    /**
        Adds the specified vector to this vector.
    */
    public void addGridVector(GridVector vector)
    {
        addXYZ(vector.getX(), vector.getY(), vector.getZ());
        
        //Opposite Direction
        float oppositeDirection = direction+180;
        if(oppositeDirection > 359) {
        	oppositeDirection -= 360;
        }
        
        //Sideways +90 degrees
        float sideways90Direction = direction+90;
        if(sideways90Direction > 359) {
        	sideways90Direction -= 360;
        }        
        float sidewaysPlus45Alteration = direction+45; 
        if(sidewaysPlus45Alteration > 359) {
        	sidewaysPlus45Alteration -= 360;
        }        
        
        //Sideways +270 degrees        
        float sideways270Direction = direction+270;
        if(sideways270Direction > 359) {
        	sideways270Direction -= 360;
        }
        float sidewaysMinus45Alteration = direction-45; 
        if(sidewaysMinus45Alteration > 359) {
        	sidewaysMinus45Alteration -= 360;
        }         
             
        //Angled +45 degrees
        float angled45Direction = direction+45;
        if(angled45Direction > 359) {
        	angled45Direction -= 360;
        }               
        
        //Angled +135 degrees        
        float angled135Direction = direction+135;
        if(angled135Direction > 359) {
        	angled135Direction -= 360;
        }       

        //Angled +225 degrees
        float angled225Direction = direction+225;
        if(angled225Direction > 359) {
        	angled225Direction -= 360;
        }               
        
        //Angled +315 degrees        
        float angled315Direction = direction+315;
        if(angled315Direction > 359) {
        	angled315Direction -= 360;
        }         
        
        //Same direction
        if(vector.direction == direction) {
        	addVelocity(vector.velocity);
        }
        //Opposite direction
        else if(vector.direction == oppositeDirection) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);
        	}
        	else {
            	addVelocity(-1*vector.velocity);        		
        	}
        }
        //Sideways+90 direction
        else if(vector.direction == sideways90Direction) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);       		
        	}
        	else {
        		setDirection(sidewaysPlus45Alteration);
        	    addVelocity(vector.velocity/2);
        	}
        }
        //Sideways+270 direction
        else if(vector.direction == sideways270Direction) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);        		
        	}
        	else {
        		setDirection(sidewaysMinus45Alteration);
        	    addVelocity(vector.velocity/2);
        	}
        }         
        //Angled+45 direction
        else if(vector.direction == angled45Direction) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);        		
        	}
        	else {
        		//Direction is not altered
        	    addVelocity(vector.velocity/4);
        	}
        }
        //Angled+135 direction
        else if(vector.direction == angled135Direction) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);        		
        	}
        	else {
        		//Direction is not altered
        	    addVelocity(vector.velocity/4);
        	}
        }        
        //Angled+225 direction
        else if(vector.direction == angled225Direction) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);        		
        	}
        	else {
        		//Direction is not altered
        	    addVelocity(vector.velocity/4);
        	}
        }
        //Angled+135 direction
        else if(vector.direction == angled315Direction) {
        	if(vector.velocity > velocity) {
        		setDirection(vector.direction);
        		addVelocity(vector.velocity - velocity);        		
        	}
        	else {
        		//Direction is not altered
        	    addVelocity(vector.velocity/4);
        	}
        }
        else {
        	//This is not a vector used in this game, do nothing
        }
    }    
    
    
    /**
     * Multiplies the specified velocity value to this vector
     * 
     * @param velocity
     */
    public void multiplyVelocity(float velocity)
    {
        this.velocity*=velocity;
    }        
    
    
    /**
     * Divides the specified velocity value to this vector
     * 
     * @param velocity
     */
    public void divideVelocity(float velocity)
    {
        this.velocity/=velocity;
    } 
    
    
    /**
        Converts this Vector to a String representation.
    */
    public String toString() {
        return super.toString() + "(velocity: " + velocity + ", direction: " + direction + ")";
    }   

    
    /**
     *  This is the clone method for GridVector.  It overrides the clone()
     *  method of object, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public GridVector clone()
    {
       return(GridVector)super.clone();
    }
    
    
    /**
     * An equals method for Vector3D that compares x, y, and z coordinates,
     * as well as velocity and direction.
     * 
     * @param obj : an object to compare with
     */
    public boolean equals(Object obj) {
	    if(this == obj) {
		    return true;
	    }
	    else if(! (obj instanceof GridVector)) {
		    return false;
	    }
	    else {
            GridVector vector = (GridVector)obj;
            return (vector.getX() == this.getX() && vector.getY() == this.getY() &&
            		vector.getZ() == this.getZ() && vector.velocity == velocity &&
            		vector.direction == direction);
	    }
    }
  
    
    /**
     *  A basic hashCode method for GridVector, for the x, y, z, velocity, and
     *  direction fields compared in the equals method.
     *  
     *  @return : an integer value
     */
    public int hashCode()
    {     
    	int result = 37;
    	
    	result += super.hashCode(); 
    	result += 37 * result + velocity; 
    	result += 37 * result + direction;     	
    	
    	return result;
    }    
    
}
