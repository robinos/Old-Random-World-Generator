package modelTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * The class ObjectWatcher watches the location status of objects of the GameObject class and
 * subclasses.  It is the primary world-level watcher, and watcher of other watchers.
 * 
 * @author Robin
 * @version : Version 0.1, 2012-03-07
 *
 */

public class ObjectWatcher implements Observer, Cloneable, Serializable
{
    private static final long serialVersionUID = 2L;      
    
    //A map of AreaWatchers by GridVector location in the world
	private HashMap<GridVector, AreaWatcher> areaWatcherMap;    
    
    /**
     * The constructor for ObjectWatcher
     */
    public ObjectWatcher()
    {
        super();	
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
        /**AreaWatcher update code**/
        if(o instanceof AreaWatcher && arg instanceof GridVector) {   
        	
        	AreaWatcher areaWatcher = (AreaWatcher) o;
        	GridVector v = (GridVector) arg;
        	       		
        	areaWatcherMap.put(v, areaWatcher);     		
        }
    }
    
}
