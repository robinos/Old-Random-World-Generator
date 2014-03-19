/**
 * The Tile class is an extension of JButton that allows picture representation
 * instead of text.
 */

package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import modelTypes.Terrain;

public class Tile extends JButton
{	
    //Instance variables
    private static final long serialVersionUID = 6L;		
	private Terrain terrain;

    /**
     * Constructor for Tile
     * 	
     * @param path    : String representing path for the image icon
     * @param terrain : Terrain object for the related terrain
     */
    public Tile(String path, Terrain terrain)
    {
	    setIcon(new ImageIcon(path));
	    setPreferredSize(new Dimension(10, 10));	
	    setDisabledIcon(new ImageIcon(path));	    
        setBackground(Color.BLACK);
        
	    this.terrain = terrain;        
    }  
    
    
    /**
     * getTerrain gets the associated Terrain
     * 
     * @return : Terrain terrain
     */
    public Terrain getTerrain(){
	    return terrain;
    }         
    
}