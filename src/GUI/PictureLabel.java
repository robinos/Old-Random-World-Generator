package GUI;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import modelTypes.Terrain;

/**
 * The DLabel class is an extension of JLabel to provide new functionality for
 * pictures and locating mouse clicks on the picture.
 * 
 */

public class PictureLabel extends JLabel
{
	//Instance variables
	private JPopupMenu menu = new JPopupMenu("Popup");
	private int x1, y1;
	private Terrain terrain;
    private static final long serialVersionUID = 7L;	
	
	/**
	 * PictureLabel Constructor
	 * 
	 * @param str : a String representing a picture filename path
	 */
    public PictureLabel(String path, Terrain terrain)
    {
	    this.setIcon(new ImageIcon(path));
	    addMouseListener(new PopupTriggerListener());
	    this.terrain = terrain;
    }
    
    
    /**
     * PopupTriggerListener is an internal class in DLabel that extends
     * MouseAdapter.
     */
    public class PopupTriggerListener extends MouseAdapter {
        public void mousePressed(MouseEvent ev) {
    	    x1 = ev.getX(); y1= ev.getY();

            if (ev.isPopupTrigger()) {
                menu.setLabel(terrain.getName());
                menu.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        }

        public void mouseReleased(MouseEvent ev) {
            if (ev.isPopupTrigger()) {
                menu.setLabel(terrain.getName());                
                menu.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        }

        public void mouseClicked(MouseEvent ev) {
        }
    
        public JPopupMenu menu (){
    	    return menu;
        }
    }
	
    
    /**
     * getX1 gets the x-coordinate clicked
     * 
     * @return : integer value x1
     */
    public int getX1(){
	    return x1;
    }
    
    
    /**
     * getY1 gets the y-coordinate clicked
     * 
     * @return : integer value y1
     */
    public int getY1(){
	    return y1;
    }
    
    
    /**
     * getMenu gets the pop-up menu
     * 
     * @return : JPopupMenu menu
     */
    public JPopupMenu getMenu(){
	    return menu;
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