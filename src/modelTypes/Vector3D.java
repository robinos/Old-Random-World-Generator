package modelTypes;

/**
 * The Vector3D class implements a 3D vector with the floating-point values
 * x, y, and z.
 * Vectors can be thought of either as a (x,y,z) point or as a vector from 
 * (0,0,0) to (x,y,z).
 *
 */
public class Vector3D implements Transformable {

    private float x;
    private float y;
    private float z;
    

    /**
     * No argument constructor creates a new Vector3D at (0,0,0).
     */
    public Vector3D()
    {
         this(0,0,0);
    }

    
    /**
     * Constructor creates a new Vector3D with the same values as the specified Vector3D.
     * 
     * @param vector : a Vector3D vector
     */
    public Vector3D(Vector3D vector)
    {
         this(vector.x, vector.y, vector.z);
    }


    /**
     * Constructor creates a new Vector3D with the specified (x,y,z) values.
     * 
     * @param x : a float representing an x-coordinate
     * @param y : a float representing a y-coordinate
     * @param z : a float representing a z-coordinate
     */
    public Vector3D(float x, float y, float z)
    {
         setByXYZ(x,y,z);
    }
    
    
	/**
	 * getX returns the float representing the x-coordinate
	 * 
	 * @return : a float representing the x-coordinate
	 */
	public float getX()
	{
		return x;
	}

	
	/**
	 * setX sets the float representing the x-coordinate
	 * 
	 * @param : a float representing the x-coordinate
	 */
	public void setX(float x)
	{
		this.x = x;
	}	
	
	
	/**
	 * getY returns the float representing the y-coordinate
	 * 
	 * @return : a float representing the y-coordinate
	 */
	public float getY()
	{
		return y;
	}

	
	/**
	 * setY sets the float representing the y-coordinate
	 * 
	 * @param : a float representing the y-coordinate
	 */
	public void setY(float y)
	{
		this.y = y;
	}
	
	
	/**
	 * getZ returns the float representing the z-coordinate
	 * 
	 * @return : a float representing the z-coordinate
	 */
	public float getZ()
	{
		return z;
	}

	
	/**
	 * setZ sets the float representing the z-coordinate
	 * 
	 * @param : a float representing the z-coordinate
	 */
	public void setZ(float z)
	{
		this.z = z;
	}	
    
    
    /**
     * Sets the vector to the same values as the specified Vector3D.
     * 
     * @param vector
     */
    public void setByVector(Vector3D vector)
    {
         setByXYZ(vector.x, vector.y, vector.z);
    }


    /**
     * Sets this vector to the specified (x, y, z) values.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setByXYZ(float x, float y, float z)
    {
         this.x = x;
         this.y = y;
         this.z = z;
    }


    /**
     * Adds the specified (x,y,z) values to this vector.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void addXYZ(float x, float y, float z)
    {
         this.x+=x;
         this.y+=y;
         this.z+=z;
    }


    /**
     * Subtracts the specified (x, y, z) values to this vector.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void subtractXYZ(float x, float y, float z)
    {
         addXYZ(-x,-y,-z);
    }

    
    /**
     * Adds the specified vector to this vector.
     * 
     * @param vector
     */
    public void addVector(Vector3D vector)
    {
         addXYZ(vector.x, vector.y, vector.z);
    }


    /**
     * Subtracts the specified vector from this vector.
     * 
     * @param vector
     */
    public void subtractVector(Vector3D vector)
    {
         addXYZ(-vector.x,-vector.y,-vector.z);
    }

    
    /**
     * Multiplies this vector by the specified value. The new length
     * of this vector will be length()*s.
     * 
     * @param s
     */
    public void multiplyXYZ(float s)
    {
         x*=s;
         y*=s;
         z*=s;
    }

    
     /**
      * Divides this vector by the specified value. The new length
      * of this vector will be length()/s.
      * 
      * @param s
      */
     public void divideXYZ(float s)
     {
         x/=s;
         y/=s;
         z/=s;
     }


    /**
     * Returns the length of this vector as a float.
     * 
     * @return
     */
    public float length()
    {
         return (float)Math.sqrt(x*x + y*y + z*z);
    }


    /**
     * Converts this Vector3D to a unit vector, or in other words, a vector
     * of length 1. Same as calling v.divide(v.length()).
     * 
     */
    public void normalize()
    {
        divideXYZ(length());
    }


/**
    
    
    
*/
    
    /**
     * Rotate this vector around the x axis the specified amount.
     * The specified angle is in radians. Use Math.toRadians() to
     * convert from degrees to radians.
     * 
     * @param angle
     */
    public void rotateX(float angle)
    {
        rotateX((float)Math.cos(angle), (float)Math.sin(angle));
    }


    /**
     * Rotate this vector around the y axis the specified amount.
     * The specified angle is in radians. Use Math.toRadians() to
     * convert from degrees to radians.
     * 
     * @param angle
     */
    public void rotateY(float angle)
    {
        rotateY((float)Math.cos(angle), (float)Math.sin(angle));
    }


    /**
     * Rotate this vector around the z axis the specified amount.
     * The specified angle is in radians. Use Math.toRadians() to
     * convert from degrees to radians.
     * 
     * @param angle
     */
    public void rotateZ(float angle)
    {
        rotateZ((float)Math.cos(angle), (float)Math.sin(angle));
    }


    /**
     * Rotate this vector around the x axis the specified amount,
     * using pre-computed cosine and sine values of the angle to
     * rotate.
     * 
     * @param cosAngle
     * @param sinAngle
     */
    public void rotateX(float cosAngle, float sinAngle)
    {
        float newY = y*cosAngle - z*sinAngle;
        float newZ = y*sinAngle + z*cosAngle;
        y = newY;
        z = newZ;
    }

    
    /**
     * Rotate this vector around the y axis the specified amount,
     * using pre-computed cosine and sine values of the angle to
     * rotate.
     * 
     * @param cosAngle
     * @param sinAngle
     */
    public void rotateY(float cosAngle, float sinAngle)
    {
        float newX = z*sinAngle + x*cosAngle;
        float newZ = z*cosAngle - x*sinAngle;
        x = newX;
        z = newZ;
    }


    /**
     * Rotate this vector around the y axis the specified amount,
     * using pre-computed cosine and sine values of the angle to
     * rotate.
     * 
     * @param cosAngle
     * @param sinAngle
     */
    public void rotateZ(float cosAngle, float sinAngle)
    {
        float newX = x*cosAngle - y*sinAngle;
        float newY = x*sinAngle + y*cosAngle;
        x = newX;
        y = newY;
    }


    /**
     * Adds the specified transform to this vector. This vector
     * is first rotated, then translated.
     * 
     * @param xform 
     */
    public void add(Transform3D xform)
    {
        // rotate
        addRotation(xform);

        // translate
        addVector(xform.getLocation());
    }


    /**
     * Subtracts the specified transform to this vector. This
     * vector translated, then rotated.
     * 
     * @param xform 
     */
    public void subtract(Transform3D xform)
    {
        // translate
        subtractVector(xform.getLocation());

        // rotate
        subtractRotation(xform);
    }


    /**
     * Rotates this vector with the angle of the specified transform.
     * 
     * @param xform
     */
    public void addRotation(Transform3D xform)
    {
        rotateX(xform.getCosAngleX(), xform.getSinAngleX());
        rotateZ(xform.getCosAngleZ(), xform.getSinAngleZ());
        rotateY(xform.getCosAngleY(), xform.getSinAngleY());
    }


    /**
     * Rotates this vector with the opposite angle of the
     * specified transform.
     * 
     * @param xform
     */
    public void subtractRotation(Transform3D xform)
    {
        // note that sin(-x) == -sin(x) and cos(-x) == cos(x)
        rotateY(xform.getCosAngleY(), -xform.getSinAngleY());
        rotateZ(xform.getCosAngleZ(), -xform.getSinAngleZ());
        rotateX(xform.getCosAngleX(), -xform.getSinAngleX());
    }


    /**
     * Returns the dot product of this vector and the specified vector.
     * 
     * @param v
     * @return
     */
    public float getDotProduct(Vector3D vector)
    {
        return x*vector.x + y*vector.y + z*vector.z;
    }

    
    /**
     * Sets this vector to the cross product of the two
     * specified vectors. Either of the specified vectors can
     * be this vector
     * 
     * @param u
     * @param v
     */
    public void setToCrossProduct(Vector3D u, Vector3D v)
    {
        // assign to local vars first in case u or v is 'this'
        float x = u.y * v.z - u.z * v.y;
        float y = u.z * v.x - u.x * v.z;
        float z = u.x * v.y - u.y * v.x;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    
    /**
     *  Converts this Vector to a String representation.
     */
    public String toString()
    {
        return "(x:" + x + ", y: " + y + ", z: " + z + ")";
    }   


    /**
     *  This is the clone method for Vector3D.  It overrides the clone()
     *  method of object, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public Vector3D clone()
    {
       try
       {
           return(Vector3D)super.clone();
       }
       catch(CloneNotSupportedException e)
       {
           throw new InternalError();
       }
    }


    /**
     * An equals method for Vector3D that compares x, y, and z coordinates,
     * as well as velocity and direction.
     * 
     * @param obj : an object to compare with
     */
    public boolean equals(Object obj)
    {
        if(this == obj) {
	        return true;
        }
        else if(! (obj instanceof Vector3D)) {
	        return false;
        }
        else {
    	    Vector3D vector = (Vector3D)obj;
            return (vector.x == x && vector.y == y && vector.z == z );
        }
    }


    /**
     *  A basic hashCode method for Vector3D, for the x, y, and z fields
     *  compared in the equals method.
     *  
     *  @return : an integer value
     */
    public int hashCode()
    {     
	    int result = 37;
	
	    result += 37 * result + x;   	  	
	    result += 37 * result + y; 
	    result += 37 * result + z;   	
	
	    return result;
    }    

}
