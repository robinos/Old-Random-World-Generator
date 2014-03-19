package modelTypes;
import java.util.ArrayList;

/**
 * Terrain is a model class holding information for areas of terrain.  This
 * represents all terrain types and is the superclass for Water.
 * 
 * @author  : Robin Osborne
 * @version : 0.1, 2012-03-07
 *
 */

public class Terrain extends GameObject
{
	//Subclasses of Terrain should have a serialVersionUID of 11xL where x
	//is a value 1-9.		
    private static final long serialVersionUID = 11L;	
    
    //Instance variables
	private String subtypeName;
	private StringBuffer vegetation;
	private int hinderance;
	private int iNumSeasons;
	private float[] temperature;
    private float[] iPressure;
    private ArrayList<GridVector> iWind;
    private float[] iRainfall;
    private String riverName;
    private int[] riverEntrance;
    private int[] riverExit;
    private String settlement;
    boolean inhabited;
  
	/**
	 * This is the constructor for terrain.
	 * 
	 * See GameObject for id, location, name, and picture
	 * @param subtypName
	 * @param vegation
	 * @param hinderance
	 * 
	 */
	public Terrain(int id, GridVector location, String name, String picture, String subtypeName, StringBuffer vegetation, int hinderance)
	{
		super(id, location, name, picture);
		this.subtypeName = subtypeName;
		this.vegetation = vegetation;
		this.hinderance = hinderance;
		this.iNumSeasons = 4;
		temperature = new float[iNumSeasons];
        iPressure = new float[iNumSeasons];
        iWind = new ArrayList<GridVector>();
        iRainfall = new float[iNumSeasons];
        riverName = "";
        riverEntrance = new int[10];
        riverExit = new int[10];
        settlement = "";
        inhabited = false;        
	}
	
	
	/**
	 * This is the no argument constructor for terrain.
	 */
	public Terrain()
	{
		super();
		subtypeName = "";
		vegetation = new StringBuffer(100);
		hinderance = 0;
		iNumSeasons = 4;
		temperature = new float[iNumSeasons];
        iPressure = new float[iNumSeasons];
        iWind = new ArrayList<GridVector>();
        iRainfall = new float[iNumSeasons];	
        riverName = "";        
	}
	
	
	/**
	 * getSubtypeName returns the String subtype name of the Terrain.
	 * 
	 * @return : a String representing the terrain subtype name
	 */
	public String getSubtypeName()
	{
		return subtypeName;
	}
	
	
	/**
	 * setSubtypeName sets the Terrain subtype name.
	 * 
	 * @param subtypeName : a String representing the desired terrain subtype name
	 */
	public void setSubtypeName(String subtypeName)
	{
		this.subtypeName = subtypeName;
	}		
	
	
	/**
	 * getVegetation returns the String array of vegetation types for the Terrain.
	 * 
	 * @return : a String array representing the terrain vegetation types
	 */
	public StringBuffer getVegetation()
	{
		return vegetation;
	}
	
	
	/**
	 * setVegetation sets the vegetation type String array.
	 * 
	 * @param subtypeName : a String array representing the vegetation types for
	 * the Terrain
	 */
	public void setVegetation(StringBuffer vegetation)
	{
		this.vegetation = vegetation;
	}	
	
	
	/**
	 * addVegetation adds the vegetation type to the BufferedString vegetation.
	 * 
	 * @param subtypeName : a String array representing the vegetation types for
	 * the Terrain
	 */
	public void addVegetation(String veg)
	{
	    vegetation.append(", "+veg);
	}
	
	
	/**
	 * getHinderance returns the integer hinderance of the Terrain.
	 * 
	 * @return : hinderance as an integer
	 */
	public int getHinderance()
	{
		return hinderance;
	}
	
	
	/**
	 * setHinderance sets the Terrain hinderance integer.
	 * 
	 * @param hinderance : an integer representing hinderance
	 */
	public void setHinderance(int hinderance)
	{
		this.hinderance = hinderance;
	}	
	
	
	/**
	 * getTemperature returns the float temperature array of the Terrain.
	 * 
	 * @param  : iSeason, the season for the temperature
	 * @return : temperature as a float
	 */
	public float getTemperature(int iSeason)
	{
		return temperature[iSeason];
	}
	
	
	/**
	 * setTemperatire sets the Terrain temperature float array.
	 *
	 * @param  : iSeason, the season for the temperature
	 * @param temperature : an integer representing temperature
	 */
	public void setTemperature(int iSeason, float temperature)
	{
		this.temperature[iSeason] = temperature;
	}

	
	/**
	 * getSeasons returns the number of seasons as an integer.
	 * 
	 * @return : number of seasons as integer
	 */
	public int getSeasons()
	{
		return iNumSeasons;
	}
	
	
	/**
	 * setSeasons sets the number of seasons.
	 *
	 * @param  : iNumSeasons, the number of seasons
	 */
	public void setSeasons(int iNumSeasons)
	{
	    this.iNumSeasons = iNumSeasons;
		this.temperature = new float[iNumSeasons];
	}
	
	
	/**
	 * getAvgTemperature
	 * 
	 * @return a float representing the average temperature
	 */
    public float getAvgTemperature()
    {
        float fAvgTemp = 0.0f;
        
        for (int i = 0; i < iNumSeasons; i++)  {
            fAvgTemp += getTemperature(i);
        }
        
        fAvgTemp /= iNumSeasons;
        fAvgTemp = ((fAvgTemp / 10.0f) - 273.0f) * 1.8f + 32.0f;
        
        return fAvgTemp;
    }

  
    /**
     * getPressure
     * 
     * @param iSeason :
     * @return        : a float
     */
    public float getPressure(int iSeason)
    {
        if (iSeason >= 0 && iSeason < iNumSeasons)  {
            return (iPressure[iSeason]);
        }
        else  {
            return 0;
        }
    }

    
    /**
     * setPressure
     * 
     * @param : iSeason
     * @param : bPres
     */
    public void setPressure (int iSeason, float fPres)
    {
        if (iSeason >= 0 && iSeason < iNumSeasons)  {
            iPressure[iSeason] = fPres;
        }
    }

    
    /**
     * getWind
     * 
     * @param iSeason :
     * @return        : a short
     */
    public GridVector getWind(int iSeason)
    {
        if (iSeason >= 0 && iSeason < iNumSeasons)  {
            return (iWind.get(iSeason));
        }
        else  {
            return null;
        }
    }

    
    /**
     * setWind
     * 
     * @param iSeason : 
     * @param wVec    :
     */
    public void setWind (int iSeason, GridVector wVec)
    {
        if (iSeason >= 0 && iSeason < iNumSeasons)  {
            iWind.add(iSeason, wVec);
        }
    }

    
    /**
     * getRainfall
     * 
     * @param iSeason :
     * @return        : short
     */
    public float getRainfall(int iSeason)
    {
        if (iSeason >= 0 && iSeason < iNumSeasons)  {
            return (iRainfall[iSeason]);
        }
        else  {
            return 0;
        }
    }

    
    /**
     * getAvgRainfall
     * 
     * @return : float
     */
    public float getAvgRainfall ()
    {
        float fAvgRain = 0.0f;
        
        for (int i = 0; i < iNumSeasons; i++)  {
            fAvgRain += getRainfall(i);
        }
        fAvgRain /= iNumSeasons;
        
        return fAvgRain;
    }

    
    /**
     * setRainfall
     * 
     * @param iSeason : 
     * @param iRF     : 
     */
    public void setRainfall (int iSeason, float iRF)
    {
        if (iSeason >= 0 && iSeason < iNumSeasons)  {
            iRainfall[iSeason] = iRF;
        }
    }	

	
	/**
	 * getRiverName returns the String river name of the Terrain.
	 * 
	 * @return : a String representing the terrain subtype name
	 */
	public String getRiverName()
	{
		return riverName;
	}
	
	
	/**
	 * setRiverName sets the Terrain river name.
	 * 
	 * @param riverName : a String representing the desired terrain river name
	 */
	public void setRiverName(String riverName)
	{
		this.riverName = riverName;
	}	

	
	/**
	 * getRiverName returns the String river name of the Terrain.
	 * 
	 * @return : true if the entrance exists, otherwise false
	 */
	public boolean getRiverEntrance(int entrance)
	{
	    if(entrance > 0 && entrance <= 9) {
		    if(riverEntrance[entrance] == 1) {
		        return true;
		    }		 
		}
		
		return false;
	}
	
	
	/**
	 * setRiverEntrance sets the river entrance name.
	 * 
	 * @param riverEntrance : an integer representing the river entrnace
	 */
	public void setRiverEntrance(int entrance)
	{
	    if(entrance > 0 && entrance <= 9) {	    
		    this.riverEntrance[entrance] = 1;
		}
    }	

    
	/**
	 * eraseRiverEntrance remove the river entrance.
	 * 
	 * @param riverEntrance : an integer representing the river entrance
	 */
	public void eraseRiverEntrance(int entrance)
	{
	    if(entrance > 0 && entrance <= 9) {	    
		    this.riverEntrance[entrance] = 0;
		}
    }    

    
	/**
	 * eraseRiverExit remove the river exit.
	 * 
	 * @param riverEntrance : an integer representing the river exit
	 */
	public void eraseRiverExit(int exit)
	{
	    if(exit > 0 && exit <= 9) {	    
		    this.riverExit[exit] = 0;
		}
    }    

	
	/**
	 * getRiverName returns the String river name of the Terrain.
	 * 
	 * @return : true if the entrance exists, otherwise false
	 */
	public boolean getRiverExit(int exit)
	{
	    if(exit > 0 && exit <= 9) {
		    if(riverExit[exit] == 1) {
		        return true;
		    }		 
		}
		
		return false;
	}
	
	
	/**
	 * setRiverEntrance sets the river entrance name.
	 * 
	 * @param riverEntrance : an integer representing the river entrnace
	 */
	public void setRiverExit(int exit)
	{
	    if(exit > 0 && exit <= 9) {	    
		    this.riverExit[exit] = 1;
		}
    }

    
	/**
	 * getSettlementName gets the Terrain settlement name.
	 * 
	 * @return : a String representing the settlement name
	 */
	public String getSettlementName()
	{
		return settlement;
    }
		
	
	/**
	 * setSettlementName sets the Terrain settlement name.
	 * 
	 * @param settlementName : a String representing the desired terrain settlement name
	 */
	public void setSettlementName(String settlementName)
	{
		this.settlement = settlementName;
	}
	
	/**
	 * 
	 */
	public boolean getInhabited()
	{
	    return inhabited;
	}

	
	/**
	 * 
	 */
	public void setInhabited(boolean inhabited)
	{
	    this.inhabited = inhabited;
	}	
    
	
    /**
     *  This is the clone method for Terrain.  It overrides the clone()
     *  method of GameObject, and is meant to be overridden where needed by
     *  subclass methods. 
     */
    @Override      
    public Terrain clone()
    {
    	  Terrain copy = (Terrain)super.clone();
    	  copy.subtypeName = new String(subtypeName);    	  
    	  
          return copy;
    } 
    
    
    /**
     *  A toString method for Terrain that returns the name, id, picture, location,
     *  and subtypeName of the Terrain object.
     *  
     *  @return : String with information on the name, id, picture, location and
     *            subtypeName of the Terrain object
     */
    public String toString()
    { 
    	return (super.toString() + ", Subtype name: " + subtypeName + " ");
    }
    
    
    /**
     *  An equals method for Terrain object that compares id, name, location, and
     *  subtypeName values.
     *  
     *  @param obj : the object for comparison 
     *  @return    : a boolean value true or false 
     */
    public boolean equals(Object obj)
    { 
    	if(this == obj) {
    		return true;  //The same object
    	}
    	if(!(obj instanceof Terrain)) {
    		return false;  //Not of the same type
    	}
    	
    	Terrain other = (Terrain)obj;
    	
    	//Compare the id and location of the Terrain object to the id, name, location, and
    	//subtypeName of the other Terrain object and returns true or false
    	return( ( other.getID() == this.getID() ) && ( other.getName() == this.getName() ) &&
    			( other.getLocation().equals(this.getLocation()) ) && ( other.subtypeName == subtypeName ) );
    }    
    
    
    /**
     *  A basic hashCode method for Terrain, for the id, name, location, and
     *  subtypeName field that are compared in the equals method.
     *  
     *  @return : an integer value
     */
    public int hashCode()
    {     
    	int result = 37;
    	
    	result += super.hashCode();
    	result += 37 * result + subtypeName.hashCode();    	   	
    	
    	return result;
    }
}
