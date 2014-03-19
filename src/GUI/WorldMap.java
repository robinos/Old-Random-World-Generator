package GUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import modelTypes.World;
import modelTypes.Terrain;
import modelTypes.GridVector;
import java.awt.Toolkit;

/**
 * WorldMap is the class that puts together all tile information for display.
 * 
 * @author Robin
 *
 */

public class WorldMap extends JFrame //implements MouseListener
{
    //Instance variables
	private String path;
	private String waterPath;
	private String oceanPath;
	private String icePath;	
	private String mountainPath;
    private String hillPath;	
	private String plainsPath;
    private String forestPath;	 
	private String desertPath;
    private String dryhillPath;	  
	private String snowmountainPath;
    private String snowhillPath;	
	private String tundraPath;
    private String snowforestPath;	 
	private String snowdesertPath; 
	
	private String lightPath;
	private String brightlightPath;	
	private String glowplainsPath;
	private String waterplainsPath;
	private String airplainsPath;
	private String verdantforestPath;	
	private String glowforestPath;
	
	private String darknessPath;
	private String deepdarknessPath;	
	private String wastelandPath;
	private String fireplainsPath;
	private String earthmountainPath;
	private String firehillPath;	
	private String toxicPath;	
	
    private ArrayList<Tile> tileList;
    private JPanel mapPane;
    private JPanel infoPane;
    private TextArea textArea;
    
    
    /**
     * The constructor for WorldMap
     */
    public WorldMap()
    {
        path = setPath();       
        
		File waterDIR = new File("tiles\\water");
		waterPath = waterDIR.getAbsolutePath();  
		waterPath = waterPath + "\\";
		File oceanDIR = new File("tiles\\ocean");
		oceanPath = oceanDIR.getAbsolutePath();
		oceanPath = oceanPath + "\\";		
		File iceDIR = new File("tiles\\ice");        
		icePath = iceDIR.getAbsolutePath();        
		icePath = icePath + "\\";		
		
		File mountainDIR = new File("tiles\\mountain");
		mountainPath = mountainDIR.getAbsolutePath();
		mountainPath = mountainPath + "\\";		
		File hillDIR = new File("tiles\\hill");
		hillPath = hillDIR.getAbsolutePath();
		hillPath = hillPath + "\\";			
		File plainsDIR = new File("tiles\\plains");
		plainsPath = plainsDIR.getAbsolutePath();
		plainsPath = plainsPath + "\\";			
		File forestDIR = new File("tiles\\forest");
		forestPath = forestDIR.getAbsolutePath();
		forestPath = forestPath + "\\";			
		
		File desertDIR = new File("tiles\\desert");
		desertPath = desertDIR.getAbsolutePath();
		desertPath = desertPath + "\\";			
		File dryhillDIR = new File("tiles\\dryhill");
		dryhillPath = dryhillDIR.getAbsolutePath();		
		dryhillPath = dryhillPath + "\\";			
		
		File snowmountainDIR = new File("tiles\\snowmountain");
		snowmountainPath = snowmountainDIR.getAbsolutePath();
		snowmountainPath = snowmountainPath + "\\";			
		File snowhillDIR = new File("tiles\\snowhill");
		snowhillPath = snowhillDIR.getAbsolutePath();
		snowhillPath = snowhillPath + "\\";			
		File tundraDIR = new File("tiles\\tundra");
		tundraPath = tundraDIR.getAbsolutePath();
		tundraPath = tundraPath + "\\";			
		File snowforestDIR = new File("tiles\\snowforest");
		snowforestPath = snowforestDIR.getAbsolutePath();
		snowforestPath = snowforestPath + "\\";			
		File snowdesertDIR = new File("tiles\\snowdesert");
		snowdesertPath = snowdesertDIR.getAbsolutePath();
		snowdesertPath = snowdesertPath + "\\";		
		
		File lightDIR = new File("tiles\\light");
		lightPath = lightDIR.getAbsolutePath();  
		lightPath = lightPath + "\\";
		File brightlightDIR = new File("tiles\\brightlight");
		brightlightPath = brightlightDIR.getAbsolutePath();
		brightlightPath = brightlightPath + "\\";		
		File airplainsDIR = new File("tiles\\airplains");
		airplainsPath = airplainsDIR.getAbsolutePath();
		airplainsPath = airplainsPath + "\\";		
		File glowplainsDIR = new File("tiles\\glowplains");
		glowplainsPath = glowplainsDIR.getAbsolutePath();
		glowplainsPath = glowplainsPath + "\\";			
		File waterplainsDIR = new File("tiles\\waterplains");
		waterplainsPath = waterplainsDIR.getAbsolutePath();
		waterplainsPath = waterplainsPath + "\\";			
		File glowforestDIR = new File("tiles\\glowforest");
		glowforestPath = glowforestDIR.getAbsolutePath();
		glowforestPath = glowforestPath + "\\";	
		File verdantforestDIR = new File("tiles\\verdantforest");
		verdantforestPath = verdantforestDIR.getAbsolutePath();
		verdantforestPath = verdantforestPath + "\\";	
		
		
		File darknessDIR = new File("tiles\\darkness");
		darknessPath = darknessDIR.getAbsolutePath();  
		darknessPath = darknessPath + "\\";
		File deepdarknessDIR = new File("tiles\\deepdarkness");
		deepdarknessPath = deepdarknessDIR.getAbsolutePath();
		deepdarknessPath = deepdarknessPath + "\\";		
		File earthmountainDIR = new File("tiles\\earthmountain");
		earthmountainPath = earthmountainDIR.getAbsolutePath();
		earthmountainPath = earthmountainPath + "\\";		
		File wastelandDIR = new File("tiles\\wasteland");
		wastelandPath = wastelandDIR.getAbsolutePath();
		wastelandPath = wastelandPath + "\\";			
		File fireplainsDIR = new File("tiles\\fireplains");
		fireplainsPath = fireplainsDIR.getAbsolutePath();
		fireplainsPath = fireplainsPath + "\\";			
		File toxicDIR = new File("tiles\\toxic");
		toxicPath = toxicDIR.getAbsolutePath();
		toxicPath = toxicPath + "\\";	
		File firehillDIR = new File("tiles\\firehill");
		firehillPath = firehillDIR.getAbsolutePath();
		firehillPath = firehillPath + "\\";		
    }
	
    
    public void showMap(World aWorld)
    {
        HashMap<Integer,Terrain> terrainMap = aWorld.getTerrainMap();
        tileList = new ArrayList<Tile>();
        
        String name = aWorld.getName();
        if(name != null) {
            setTitle(name);
        }

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();         
        JPanel contentPane = (JPanel)getContentPane();         
        //Border Layout
        contentPane.setBorder(new EmptyBorder(6, 6, 6, 6));
        contentPane.setSize(dim);
        
        /**Map Frame**/
        JFrame mapFrame = new JFrame();
        
        Dimension mapdim = new Dimension(aWorld.getWidth(), aWorld.getHeight());
        mapFrame.setSize(mapdim);        
        mapPane = (JPanel)mapFrame.getContentPane();  
        mapPane.setSize(mapdim);         
        
        //Grid layout
        JPanel picturePanel = new JPanel();
        picturePanel.setSize(mapdim);                  
        picturePanel.setLayout(new GridLayout(aWorld.getWidth(), aWorld.getHeight()));
        
		//Cyclethrough terrain
		for(Integer k=0;k<terrainMap.size();k++){
		    
		    if(terrainMap.containsKey(k)) {
		        
		        Terrain terrain = terrainMap.get(k);
		        Tile tile;
		        
		        if(terrain.getPicture().equals("water")) {
		            tile = new Tile(waterPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getPicture().equals("light")) { 
		            tile = new Tile(lightPath+terrain.getPicture()+".png",terrain);		            
		        }	
		        else if(terrain.getPicture().equals("darkness")) { 
		            tile = new Tile(darknessPath+terrain.getPicture()+".png",terrain);		            
		        }		        
		        else if(terrain.getSubtypeName().equals("sea")) {
		            tile = new Tile(oceanPath+terrain.getPicture()+".png",terrain);		            
		        }	
		        else if(terrain.getSubtypeName().equals("ice")) {
		            tile = new Tile(icePath+terrain.getPicture()+".png",terrain);		            
		        }		        
		        else if(terrain.getSubtypeName().equals("mountain")) {
		            tile = new Tile(mountainPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("hill")) {
		            tile = new Tile(hillPath+terrain.getPicture()+".png",terrain);		            
		        }	
		        else if(terrain.getSubtypeName().equals("plains")) {
		            tile = new Tile(plainsPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("forest")) {
		            tile = new Tile(forestPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("desert")) {
		            tile = new Tile(desertPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("dryhill")) {
		            tile = new Tile(dryhillPath+terrain.getPicture()+".png",terrain);		            
		        }		        
		        else if(terrain.getSubtypeName().equals("snowmountain")) {
		            tile = new Tile(snowmountainPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("snowhill")) {
		            tile = new Tile(snowhillPath+terrain.getPicture()+".png",terrain);		            
		        }	
		        else if(terrain.getSubtypeName().equals("tundra")) {
		            tile = new Tile(tundraPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("snowforest")) {
		            tile = new Tile(snowforestPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("snowdesert")) {
		            tile = new Tile(snowdesertPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("light")) {
		            tile = new Tile(lightPath+terrain.getPicture()+".png",terrain);		            
		        }		        
		        else if(terrain.getSubtypeName().equals("brightlight")) {
		            tile = new Tile(brightlightPath+terrain.getPicture()+".png",terrain);		            
		        }			        
		        else if(terrain.getSubtypeName().equals("airplains")) {
		            tile = new Tile(airplainsPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("glowplains")) {
		            tile = new Tile(glowplainsPath+terrain.getPicture()+".png",terrain);		            
		        }	
		        else if(terrain.getSubtypeName().equals("waterplains")) {
		            tile = new Tile(waterplainsPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("glowforest")) {
		            tile = new Tile(glowforestPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("verdantforest")) {
		            tile = new Tile(verdantforestPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("darkness")) {
		            tile = new Tile(darknessPath+terrain.getPicture()+".png",terrain);		            
		        }		        
		        else if(terrain.getSubtypeName().equals("deepdarkness")) {
		            tile = new Tile(deepdarknessPath+terrain.getPicture()+".png",terrain);		            
		        }			        
		        else if(terrain.getSubtypeName().equals("earthmountain")) {
		            tile = new Tile(earthmountainPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("wasteland")) {
		            tile = new Tile(wastelandPath+terrain.getPicture()+".png",terrain);		            
		        }	
		        else if(terrain.getSubtypeName().equals("fireplains")) {
		            tile = new Tile(fireplainsPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("toxic")) {
		            tile = new Tile(toxicPath+terrain.getPicture()+".png",terrain);		            
		        }
		        else if(terrain.getSubtypeName().equals("firehill")) {
		            tile = new Tile(firehillPath+terrain.getPicture()+".png",terrain);		            
		        }		        
		        else {
		            tile = new Tile(path+"empty.png",terrain);
		        }
		        
		        tile.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                for(int i = 0; i < tileList.size();i++) {
		                    Tile tempTile = tileList.get(i);
			                if(e.getSource() == tempTile) {
			                    Terrain tempTerrain = tempTile.getTerrain();
			                    if(tempTerrain != null) {
		                            showInfo(tempTerrain);
		                        }
		                     }
		                }
		            }
		        });
		            
		        tile.setToolTipText(terrain.getName());
		        tileList.add(tile);
		        picturePanel.add(tile);
		    }
		}
		
        JScrollPane mapScroller = new JScrollPane(picturePanel);
        mapScroller.setPreferredSize(new Dimension(aWorld.getWidth(), aWorld.getHeight()));
        mapScroller.setMinimumSize(new Dimension(aWorld.getWidth(),aWorld.getHeight()));
        mapScroller.setHorizontalScrollBarPolicy(mapScroller.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapScroller.setVerticalScrollBarPolicy(mapScroller.VERTICAL_SCROLLBAR_AS_NEEDED);      			
        mapPane.add(mapScroller);

                
        /**Info Frame**/
        JFrame infoFrame = new JFrame();	        
        infoFrame.setLayout(new BorderLayout());
        JPanel infoPane = (JPanel)infoFrame.getContentPane();                
        
        textArea = new TextArea();
        textArea.setEditable(false);    
        textArea.setPreferredSize(new Dimension(200, 200));
        textArea.setMinimumSize(new Dimension(200,200));        
        JScrollPane listScroller = new JScrollPane(textArea);
        listScroller.setPreferredSize(new Dimension(200, 200));
        listScroller.setMinimumSize(new Dimension(200,200));
        listScroller.setHorizontalScrollBarPolicy(listScroller.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setVerticalScrollBarPolicy(listScroller.VERTICAL_SCROLLBAR_AS_NEEDED);      
        infoPane.add(listScroller, BorderLayout.WEST);
        
        add(infoPane, BorderLayout.WEST);                
        add(mapPane, BorderLayout.CENTER);
        
        
        // add some event listeners to some components
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });        
        
		pack();
		setVisible(true);
		
        setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);		
    }
    
    
	/**
	 * setThePath sets the filepath for pictures for the game.
	 * 
	 * @param p : a string representing the absolute path
	 * @return  : a string representing the file path for use by other classes
	 */
	 public String setPath()
	 {
		 File bildmap = new File("tiles");
		 String p = bildmap.getAbsolutePath();
		 return (p+"\\");
	 }
	 
	 
	 private String riverEntrance(Terrain terrain)
	 {
	     if(terrain.getRiverEntrance(8)) {
	         return "north";
	     }
	     else if(terrain.getRiverEntrance(9)) {
	         return "northeast";
	     }
	     else if(terrain.getRiverEntrance(6)) {
	         return "east";
	     }
	     else if(terrain.getRiverEntrance(3)) {
	         return "southeast";
	     }
	     else if(terrain.getRiverEntrance(2)) {
	         return "south";
	     }	     
	     else if(terrain.getRiverEntrance(1)) {
	         return "southwest";
	     }
	     else if(terrain.getRiverEntrance(4)) {
	         return "west";
	     }
	     else if(terrain.getRiverEntrance(7)) {
	         return "northwest";
	     }
	     else if(terrain.getRiverEntrance(5)) {
	         return "source point";
	     }
	     else {
	         return "error";
	     }	     
	 }

	 private String riverExit(Terrain terrain)
	 {
	     if(terrain.getRiverExit(8)) {
	         return "north";
	     }
	     else if(terrain.getRiverExit(9)) {
	         return "northeast";
	     }
	     else if(terrain.getRiverExit(6)) {
	         return "east";
	     }
	     else if(terrain.getRiverExit(3)) {
	         return "southeast";
	     }
	     else if(terrain.getRiverExit(2)) {
	         return "south";
	     }	     
	     else if(terrain.getRiverExit(1)) {
	         return "southwest";
	     }
	     else if(terrain.getRiverExit(4)) {
	         return "west";
	     }
	     else if(terrain.getRiverExit(7)) {
	         return "northwest";
	     }
	     else if(terrain.getRiverExit(5)) {
	         return "end point";
	     }
	     else {
	         return "error";
	     }	     
	 }
	 
	 /**
	  * showInfo
	  */
	 private void showInfo(Terrain terrain)
	 {
	     GridVector vector = null;
	     
	     if(terrain != null) {
	         textArea.append( "Name: "+terrain.getName()+"\n");
	         textArea.append( "Type: "+terrain.getSubtypeName() + "\n\n" );
	         textArea.append( "Picture: "+terrain.getPicture() + "\n\n" );
	         if(terrain.getInhabited()) {
	             textArea.append( "Settlement: " + terrain.getSettlementName() + "\n\n" );	             
	         }	         
	         if(terrain.getRiverName() != null) {
	             if(terrain.getRiverName() != "") {
	                 textArea.append( "River: "+terrain.getRiverName() + "\n" );
	                 textArea.append( "River entance: "+riverEntrance(terrain) +"\n" );
	                 textArea.append( "River exit: "+riverExit(terrain) +"\n\n" );	                 
	             }
	         }
	         textArea.append( "Height: "+terrain.getLocation().getZ() + "\n\n" );	         
	         textArea.append( "Spring Temperature: "+terrain.getTemperature(0) + "\n" );	         
	         textArea.append( "Summer Temperature: "+terrain.getTemperature(1) + "\n" );
	         textArea.append( "Fall Temperature: "+terrain.getTemperature(2) + "\n" );
	         textArea.append( "Winter Temperature: "+terrain.getTemperature(3) + "\n\n" );
	         textArea.append( "Spring Pressure: "+(int)terrain.getPressure(0) + "\n" );
	         textArea.append( "Summer Pressure: "+(int)terrain.getPressure(1) + "\n" );
	         textArea.append( "Fall Pressure: "+(int)terrain.getPressure(2) + "\n" );
	         textArea.append( "Winter Pressure: "+(int)terrain.getPressure(3) + "\n\n" );
	         vector = terrain.getWind(0);
	         textArea.append( "Spring Wind\nvelocity: "+(int)vector.getVelocity() +
	                          ", direction: " +getDirection(vector.getDirection()) + "\n" );
	         vector = terrain.getWind(1);	                          
	         textArea.append( "Summer Wind\nvelocity: "+(int)vector.getVelocity() +
	                          ", direction: " +getDirection(vector.getDirection()) + "\n" );
	         vector = terrain.getWind(2);	                          
	         textArea.append( "Fall Wind\nvelocity: "+(int)vector.getVelocity() +
	                          ", direction: " +getDirection(vector.getDirection()) + "\n" );
	         vector = terrain.getWind(3);	                          
	         textArea.append( "Winter Wind\nvelocity: "+(int)vector.getVelocity() +
	                          ", direction: " +getDirection(vector.getDirection()) + "\n\n" );
	         textArea.append( "Spring Rainfall: "+(int)terrain.getRainfall(0) + "\n" );
	         textArea.append( "Summer Rainfall: "+(int)terrain.getRainfall(1) + "\n" );
	         textArea.append( "Fall Rainfall: "+(int)terrain.getRainfall(2) + "\n" );
	         textArea.append( "Winter Rainfall: "+(int)terrain.getRainfall(3) + "\n\n" );
	     }
	 }
	 
	 private String getDirection(float fDirection)
	 {
	     String direction = null;
	     
	     if(fDirection == 0) {
	         direction = "east";
	     }
	     else if(fDirection == 45) {
	         direction = "northeast";	         
	     }
	     else if(fDirection == 90) {
	         direction = "north";	         
	     }
	     else if(fDirection == 135) {
	         direction = "northwest";	         
	     }
	     else if(fDirection == 180) {
	         direction = "west";	         
	     }
	     else if(fDirection == 225) {
	         direction = "southhwest";	         
	     }	     
	     else if(fDirection == 270) {
	         direction = "south";	         
	     }
	     else if(fDirection == 315) {
	         direction = "southeast";	         
	     }
	     
	     return direction;
	 }
	 
    /**
     * MouseEvents are defined below.
     *
	@Override    
    public void mouseClicked(MouseEvent e)
    {	
    }
    
	@Override    
	public void mouseEntered(MouseEvent e)
	{	
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{	
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{	
	}*/	 
}
