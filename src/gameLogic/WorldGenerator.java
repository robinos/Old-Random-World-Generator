package gameLogic;

//Imported java classes
//import java.util.Collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Random;

//Imported local classes
import modelTypes.GridVector;
import modelTypes.World;
import modelTypes.Terrain;
import modelTypes.Water;


/**
 * WorldGenerator
 * 
 * @author  : Robin Osborne
 * @version : Version 0.1, 2012-03-07
 *
 */

public class WorldGenerator
{
    private float fRoughness; 
    private float elevationHighest;
    private float elevationLowest;
    private float actualHighest;
    private float actualLowest;
    private int iDimensions;    
    private int range;
    private int iNumSeasons;
    private short iMaxPressure = 255;   
    
    private double dAxialTilt = 23.0;   
    private double dOrbitalEccentricity = 0.0;  
    private double dEccentricityPhase = 0.0;
    private double dBaseLandTemp = 275.0;  //In Kelvin 
    private double dLandTempVariance = 45.0;    
    private double dLandTempTilt = 1.0;  
    private double dLandSmooth = 0.6f;    
    private double dLandDiv = 180.0;  
    private double dBaseOceanTemp = 275.0;    
    private double dOceanTempVariance = 30.0;
    private double dOceanTempTilt = 0.2f;
    private double dOceanSmooth = 0.2f;
    private double dOceanDiv = 250.0;
  
    public byte bMaxRange = 15;
    
    private int TEMPSCALE = 10;    
    private byte OLTHRESH = 1;
    private byte OOTHRESH = 5;
    private byte OLMIN = 40;
    private byte OLMAX = 65;
    private short OHMIN = 130;
    private short OHMAX = 180;
    private short LOTHRESH = 3;
    private short LLTHRESH = 7;
    private short LLMIN = 220;
    private short LLMAX = 255;
    private byte LHMIN = 0;   
    private byte LHMAX = 20;
    private byte BARSEP = 16;
    private byte MAXFETCH = 5;
    private byte RAINCONST = 32;
    private byte LANDEL = 10;
    private byte MOUNTDEL = 32;
    private byte FETCHDEL = 4;
    private byte HEQDEL = 32;
    private byte NRHEQDEL = 24;
    private byte FLANKDEL = -24;
    private byte NRFDEL = 3;
    private short ICEBERGK = 263;
    
    private static final byte PRESSURE_LOW = 1;
    private static final byte PRESSURE_HIGH = 2;
    private static final byte PRESSURE_HEQ = 3;    
    private static final byte WIND_N = 1;
    private static final byte WIND_S = 2;
    private static final byte WIND_E = 4;
    private static final byte WIND_W = 8;
  
    private ArrayList<GridVector> heightList;    
    private HashMap<Integer,GridVector> heightMap;
    private DisjointSets disjointSet;
    
    /**
     * No argument constructor for WorldGenerator, standard world and 4 divine realms.
     */
    public WorldGenerator()
    {                       
        heightMap = new HashMap<Integer,GridVector>();
    }   
    
    
    /**
     * 
     */
    public World generateStandardTerrain()
    {           
        return generateTerrain(3,0f,0f,0f,0f,0f,"The Mundane World","",128,4,1f);
    }

    
    /**
     * 
     */
    public World generateCelestialTerrain()
    {           
        return generateTerrain(0,2f,2f,2f,0f,0f,"The Heavens","",128,4,1f);
    }
    
    
    /**
     * 
     */
    public World generateInfernalTerrain()
    {           
        return generateTerrain(1,-2f,-2f,-2f,0f,0f,"The Hells","",128,4,1f);
    }    
    
    
    /**
     * 
     */
    public World generateNeutralTerrain()
    {           
        return generateTerrain(2,1f,1f,1f,0f,0f,"Purgatory","",128,4,1f);
    }    

    
    /**
     * 
     */
    public World generateTerrain(int id, float x, float y, float z, float velocity, float direction,
                                 String name, String picture, int iDimensions, int iNumSeasons,
                                 float fRoughness)
    {   
        this.iDimensions = iDimensions;
        this.fRoughness = fRoughness;
        this.iNumSeasons = iNumSeasons;
        elevationHighest = iDimensions/8;
        elevationLowest = -1*elevationHighest;      
        float tHeight = elevationHighest;           
        disjointSet = new DisjointSets(iDimensions*iDimensions);
        
        World newWorld = new World(id,new GridVector(x,y,z,velocity,direction),name,picture,iDimensions,iDimensions);       
        
        generateElevations();
        
        //System.out.println("World size: "+heightMap.size());          
        
        //Collection<GridVector> values = heightMap.values();
        //int counter = 0;
        //StringBuffer strBuf = new StringBuffer(400);
        Terrain terrain = null;
        
        //Look at progress
        for(Integer k=0;k<heightMap.size();k++){
            if(heightMap.containsKey(k)) {
                GridVector g = heightMap.get(k);
                
                if(id == 3) {
                    if(g.getZ() <= 0) {
                        if(g.getZ() <= (elevationLowest/4)) {
                            //strBuf.append(" VV ");
                            terrain = new Water(k,g,"","ocean","sea",new StringBuffer(100),6,g.getZ()*-1);
                        }
                        else {
                            //strBuf.append(" __ ");
                            terrain = new Water(k,g,"","water","sea",new StringBuffer(100),4,g.getZ()*-1);                       
                        }
                    }
                    else if(g.getZ() >= elevationHighest) {
                        //strBuf.append(" AA ");     
                        terrain = new Terrain(k,g,"","mountain","mountain",new StringBuffer(100),7);                   
                    }                                 
                    else if(g.getZ() >= (elevationHighest/4)) {
                        //strBuf.append(" ^^ ");
                        terrain = new Terrain(k,g,"","hill","hill",new StringBuffer(100),2);                    
                    }
                    else {
                        //strBuf.append(" .. ");   
                        terrain = new Terrain(k,g,"","plains","plains",new StringBuffer(100),0);                   
                    }
                }
                else if(id == 0) { //celestial
                    if(g.getZ() <= 0) {
                        if(g.getZ() <= (elevationLowest/4)) {
                            //strBuf.append(" VV ");
                            terrain = new Water(k,g,"","brightlight","brightlight",new StringBuffer(100),6,g.getZ()*-1);
                        }
                        else {
                            //strBuf.append(" __ ");
                            terrain = new Water(k,g,"","light","brightlight",new StringBuffer(100),4,g.getZ()*-1);                       
                        }
                    }
                    else if(g.getZ() >= elevationHighest) {
                        //strBuf.append(" AA ");     
                        terrain = new Terrain(k,g,"","airplains","airplains",new StringBuffer(100),7);                   
                    }                                 
                    else if(g.getZ() >= (elevationHighest/4)) {
                        //strBuf.append(" ^^ ");
                        terrain = new Terrain(k,g,"","glowplains","glowplains",new StringBuffer(100),2);                    
                    }
                    else {
                        //strBuf.append(" .. ");   
                        terrain = new Terrain(k,g,"","waterplains","waterplains",new StringBuffer(100),0);                   
                    }                    
                }
                else if(id == 1) { //infernal
                    if(g.getZ() <= 0) {
                        if(g.getZ() <= (elevationLowest/4)) {
                            //strBuf.append(" VV ");
                            terrain = new Water(k,g,"","deepdarkness","deepdarkness",new StringBuffer(100),6,g.getZ()*-1);
                        }
                        else {
                            //strBuf.append(" __ ");
                            terrain = new Water(k,g,"","darkness","deepdarkness",new StringBuffer(100),4,g.getZ()*-1);                       
                        }
                    }
                    else if(g.getZ() >= elevationHighest) {
                        //strBuf.append(" AA ");     
                        terrain = new Terrain(k,g,"","earthmountain","earthmountain",new StringBuffer(100),7);                   
                    }                                 
                    else if(g.getZ() >= (elevationHighest/4)) {
                        //strBuf.append(" ^^ ");
                        terrain = new Terrain(k,g,"","wasteland","wasteland",new StringBuffer(100),2);                    
                    }
                    else {
                        //strBuf.append(" .. ");   
                        terrain = new Terrain(k,g,"","fireplains","fireplains",new StringBuffer(100),0);                   
                    }                    
                }
                else if(id == 2) { //neutral
                }
                
                //counter++;
                //if(counter == iDimensions) {
                    //System.out.println(strBuf.toString()+"\n");
                    //strBuf = new StringBuffer(400);
                //    counter = 0;
                //}
                
                newWorld.addTerrain(k, terrain);                
            }
        }
                        
        newWorld = computeTemperature(newWorld);
        newWorld = computePressure(newWorld);
        newWorld = computeWind(newWorld);
        newWorld = computeRainfall(newWorld);
        
        //newWorld = calculateNeighbours(newWorld); //preliminary names         
        newWorld = calculateLakes(newWorld); //add more fresh water lakes        
        newWorld = computeRivers(newWorld);
        newWorld = computeClimates(newWorld);          
        //newWorld = calculateNeighbours(newWorld); //fix names  
        
        newWorld = fixPictures(newWorld); //fix pictures and river silt        
        
        //one more time for extra rivers now that rainfall has increased
        newWorld = computeRivers(newWorld);
        newWorld = computeClimates(newWorld);              
        
        newWorld = fixPictures(newWorld); //fix pictures and river silt      
        
        newWorld = createBigLakes(newWorld);                
        newWorld = calculateNeighbours(newWorld); //fix names         
        
        newWorld = seedSettlements(newWorld, 0);
        
        return newWorld;    
    }
    
    
    /**
     This function initializes the terrain in the map.  It uses
     the dimaond square algorithm described in the desription of
     the Map class.  The roughness paramater indicates the roughness
     of the terrain ( values close to 0.0 mean rougher, values close
     to or above 1.0 mean smoother).  The iterration paramater indicates
     how many Iterations of the algorithm are supposed to be used
     purely for initialization.
   
     The algorithm used to generate the terrain is called the "diamond
     square" algorithm.  It subdivides the map like this:
   
     Square  Diamond Square  Dimaond Square
   
     1---1   1---1   1-3-1   1-3-1   15351
     -----   -----   -----   -4-4-   54545
     -----   --2--   3-2-3   3-2-3   35253
     -----   -----   -----   -4-4-   54545
     1---1   1---1   1-3-1   1-3-1   15351
   
     In the dimaond step the four corners of the squares are taken and
     averaged to that we add a random amount to generate a value at the
     center of the squares.  This generates diamonds.  In the square step
     the four corners of the dimaond are average to generate a value at
     the center of the dimaonds.  Again a random amount is added to the
     new value.  This generates squares.  This is repeated until the entire
     map is filled.  With each iterration the range of the random value
     generator is multiplied by 2^H (where H is the roughness value).
     */
    private void generateElevations ()
    {
         int x, y, iStep;
         float fRange, fAvg;
         heightList = new ArrayList<GridVector>();
         int tID = 0;
         int iIterations = 2;         
         
         try  {
            // Initializations
            fRange = elevationHighest;   // So that we get values between highest and lowest
            iStep = iDimensions >> 1;
            GridVector vector;
            float stepY;

            //Initialisation of the first elevations
            vector = new GridVector(0,0,Random(fRange),0,0);
            heightList.add(vector);         
            vector = new GridVector(0,(iDimensions/4),Random(fRange),0,0);
            heightList.add(vector);         
            vector = new GridVector(0,(iDimensions/2),Random(fRange),0,0);
            heightList.add(vector);         
            vector = new GridVector(0,((iDimensions/2)+(iDimensions/4)),Random(fRange),0,0);
            heightList.add(vector);         
            vector = new GridVector((iDimensions/2),(iDimensions/4),Random(fRange),0,0);
            heightList.add(vector);         
            vector = new GridVector((iDimensions/2),((iDimensions/2)+(iDimensions/4)),Random(fRange),0,0);          
            heightList.add(vector);              

            // Initialize the map matrix with random elevations

            while (iIterations > 0 && iStep > 0)  {
            // Step through all the squares and dimaonds
                y = iStep;
                while (y < iDimensions)  {
                    x = iStep;
                    while (x < iDimensions)  {
                        // Generate squares
                        vector = new GridVector((float)x,(float)y,Random(fRange),0,0);
                        heightList.add(vector);                     
                        // Generate diamonds
                        vector = new GridVector((float)x,(float)((y+iStep) % iDimensions),Random(fRange),0,0);
                        heightList.add(vector);                         
                        x += iStep << 1;   // Go to the next square
                    }
                    y += iStep << 1;
                }

                // Go to the next iteration
                iStep = iStep >> 1;
                iIterations -= 1;
            }

            
            // Now generate the terrain again using the diamond square step
            while (iStep > 0)  {
                 // Diamond Step
                 y = iStep;
                 while (y < iDimensions)  {
                     x = iStep;
                     while (x < iDimensions)  {
                         fAvg  = squareAverage ((float)x, (float)y, iStep);
                         fAvg += Random (fRange);
                         vector = new GridVector((float)x,(float)y,fAvg,0,0);
                         heightList.add(vector);                             
                         x += iStep << 1;
                     }
                     y += iStep << 1;
                 }

                 // Square step
                 y = iStep; 

                 while (y < iDimensions)  {
                     x = iStep;
                     while (x < iDimensions)  {
                         fAvg  = diamondAverage ((float)x, (float)(y - iStep), iStep);
                         fAvg += Random (fRange);
                         vector = new GridVector((float)x,(float)(y-iStep),fAvg,0,0);
                         heightList.add(vector);                         
                         fAvg = diamondAverage ((float)(x - iStep), (float)y, iStep);
                         fAvg += Random (fRange);
                         vector = new GridVector((float)(x-iStep),(float)y,fAvg,0,0);
                         heightList.add(vector);                         
                         fAvg = diamondAverage ((float)((x + iStep) % iDimensions), (float)y, iStep);
                         fAvg += Random (fRange);
                         vector = new GridVector((float)((x+iStep) % iDimensions),(float)y,fAvg,0,0);
                         heightList.add(vector);                         
                         fAvg = diamondAverage ((float)x, (float)((y + iStep) % iDimensions), iStep);
                         fAvg += Random (fRange);
                         vector = new GridVector((float)x,(float)((y+iStep) % iDimensions),fAvg,0,0);
                         heightList.add(vector);                             
                         x += iStep << 1;
                     }
                     y += iStep << 1;
                 }

                 // Reduce the range, and go to the next iteration
                 fRange = fRange * (float)(Math.pow (2, -fRoughness));
                 iStep  = iStep >> 1;
            }
            //End outer while
        }
        catch (NullPointerException e)  {
            System.out.println("NullPointerException");
            e.printStackTrace();
        }
        
        // Normalize all Elevations to [0,1]
        // then cube them (to flatten)
        calcHighLowElevations();
        float fNormElHigh, fNormElFactor = 0.0f;            
        GridVector v;
        
        if (actualLowest < 0.0f)  {
            fNormElFactor = 0.0f - actualLowest;
        }
        fNormElHigh = actualHighest + fNormElFactor;
    
        for (float i = 0; i < iDimensions; i++) {
            for (float j = 0; j < iDimensions; j++) {
                v = locateVector(i,j);
                if(v != null){
                    //float fCurEl = locateVector(i,j).getZ();
                    //if(fCurEl >= 0) {
                    //    fCurEl = (float) Math.pow ((double)((fCurEl + fNormElFactor) / fNormElHigh), 2);
                    //}
                    //else {
                    //    fCurEl = (float) Math.pow ((double)((fCurEl + fNormElFactor) / fNormElHigh), 2);
                    //    fCurEl=fCurEl*(-1);
                    //}
                    //int newHeight = (int)(fCurEl*10);
                    //v.setByXYZ(i,j,(float)newHeight);
                    heightMap.put(tID,v);
                    tID++;                      
                }
                else {
                    System.out.println("Null vector error!  No (" + i + ", " + j + ")");
                }   
            }
        }
        
        calcHighLowElevations();
    }   
    
    
    /**
     * Searches the heightList ArrayList for a GridVector with the given x, y
     * coordinates.
     * Due to the way the vectors are created, this is necessary until they are
     * later referencable by position in the heightMap HashMap.
     *
     *@param x : the x-coordinate
     *@param y : the y-coordinate
     *@return  : a gridvector in the height list with the given x and y coordinates
     */
    private GridVector locateVector(float x, float y)
    {
        Iterator<GridVector> it = heightList.iterator();               
        GridVector v;
    
        //While there is another element
        while(it.hasNext()) {     
            v = it.next();
            if(v.getX()==x && v.getY()==y) {
                return v;                       
            }
        }
        //End while
        
        return null;
    }
    
    
    /**
     * Uses locateVector to Searches the heightList ArrayList for a GridVector
     * with the given x, y coordinates, and returns its z value.
     *
     *@param x : the x-coordinate
     *@param y : the y-coordinate
     *@return  : a float representing height
     */
    private float getHeight(float x, float y)
    {
        GridVector v = locateVector(x,y);
        
        if(v != null) {
            return v.getZ();
        }

        return 0;
    }
    
    
    /**
      Takes the average of the four corners of the square.
      The corners are given by the location and the step size.
    
      1---2  1 = < x - step, y - step>
      -----  2 = < x + step, y - step>
      --*--  3 = < x - step, y + step>
      -----  4 = < x + step, y + step>
      3---4

      @param x the x location of the center of the square
      @param y the y location of the center of the square
      @param sStep the step size of the sub division.
      @return the average of the elevations of the square.
    */
    private float squareAverage (float x, float y, float iStep) throws NullPointerException
    {
        float fSum = 0;
        GridVector v;
        
        // Surrounding component
        fSum = getHeight(x - iStep, y - iStep);
        fSum += getHeight((x - iStep) % iDimensions, y - iStep);       
        fSum += getHeight((x + iStep) % iDimensions, (y + iStep) % iDimensions);
        fSum += getHeight(x - iStep, (y + iStep) % iDimensions);                

        // Return average
        return (fSum / 4.0f);
    }


    /**
        Takes the average of the four corners of the diamond.
        The corners are given by the location and the step size.

        --1--  1 = < x, y - step>
        -----  2 = < x + step, y>
        4-*-2  3 = < x, y + step>
        -----  4 = < x - step, y>
        --3--

        @param x the x location of the center of the diamond
        @param y the y location of the center of the diamond
        @param iStep the step size of the sub division.
        @return the average of the elevations of the diamond.
    */
    private float diamondAverage (float x, float y, float iStep) throws NullPointerException
    {
        float fSum;

        // Surrounding component
        fSum = getHeight(x, ((y - iStep + iDimensions) % iDimensions));        
        fSum += getHeight((x - iStep + iDimensions) % iDimensions, y);         
        fSum += getHeight((x + iStep) % iDimensions, y);         
        fSum += getHeight(x, ((y + iStep) % iDimensions));  
        
        // Return average
        return (fSum / 4.0f);
     }


     /**
         Generates a random value in the range from  - range / 2 to + range /2.

         @param fRange the range of the random number.
         @return the random number ( - range/2 .. + range/2 ).
     */
     private float Random (float fRange)
     {          
        Random random = new Random();
        int randomHeight = ( random.nextInt((int)(fRange*2)) + 1 - (int)fRange);               
        
        return (float)randomHeight;
      
        //return (float)((Main.pRandomizer.raw() * (double)fRange) - ((double)fRange / 2.0d));      
     }
     
       
    /**
     * 
     */
    private void calcHighLowElevations ()
    {
        actualHighest = Float.MIN_VALUE;
        actualLowest = Float.MAX_VALUE;
        float fCur = 0;    
        GridVector v;
        
        for (float i = 0; i < iDimensions; i++) {
            for (float j = 0; j < iDimensions; j++) {
                v = locateVector(i,j);
                if(v != null){
                    fCur = v.getZ();
                    
                    if(fCur < actualLowest) {
                        actualLowest = fCur;
                    }
                    if(fCur > actualHighest) {
                        actualHighest = fCur;
                    }
                }
                else {
                    System.out.println("Null vector error in calculate!");
                }   
            }
            //End inner for
        }
        //End outer for
    }
     

    /**
     * calculateNeighbours
     * 
     */
    private World calculateNeighbours(World newWorld)
    {
        GridVector vector;
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();
        
        //name seeds
        //findNeighbours(terrainMap, iDimensions/2);
        findNeighbours(terrainMap, iDimensions);
        findNeighbours(terrainMap, iDimensions-1);         
        //findNeighbours(terrainMap, (iDimensions+iDimensions)/2); 
        //findNeighbours(terrainMap, iDimensions+iDimensions);        
        //findNeighbours(terrainMap, iDimensions*iDimensions/2);
        //findNeighbours(terrainMap, (iDimensions*iDimensions)-iDimensions);
      
        //  
        for (int id = 0; id < (iDimensions*iDimensions); id++) {               
            //Find and names surrounding terrain
            findNeighbours(terrainMap, id);
        }
        //End outer for
        
        newWorld.setTerrainMap(terrainMap);
        
        return newWorld;
    }


    /**
     * calculateLakes
     * 
     */
    private World calculateLakes(World newWorld)
    {
        GridVector vector;
        Terrain terrain;
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();
        
        //
        for (int id = 0; id < (iDimensions*iDimensions); id++) {               
            //Find and names surrounding terrain
            terrain = terrainMap.get(id);
            
            if(terrain.getSubtypeName().equals("sea")) {
                if( getLeftNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getAboveNeighbourMatches(terrainMap, id) !=  null ) {
                }   
                else if( getBelowNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getLeftAboveNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightAboveNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getLeftBelowNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightBelowNeighbourMatches(terrainMap, id) != null ) {
                }            
                else { //surrounded by land
                    if(terrain != null) {
                        terrain.setSubtypeName("plains");
                        terrain.setPicture(terrain.getSubtypeName()+"end");                        
                    }
                }
            }
            
            if(terrain.getSubtypeName().equals("brightlight")) {
                if( getLeftNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getAboveNeighbourMatches(terrainMap, id) !=  null ) {
                }   
                else if( getBelowNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getLeftAboveNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightAboveNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getLeftBelowNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightBelowNeighbourMatches(terrainMap, id) != null ) {
                }            
                else { //surrounded by land
                    if(terrain != null) {
                        terrain.setSubtypeName("glowplains");
                        terrain.setPicture(terrain.getSubtypeName()+"end");                        
                    }
                }
            }
            
            if(terrain.getSubtypeName().equals("deepdarkness")) {
                if( getLeftNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getAboveNeighbourMatches(terrainMap, id) !=  null ) {
                }   
                else if( getBelowNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getLeftAboveNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightAboveNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getLeftBelowNeighbourMatches(terrainMap, id) != null ) {
                }
                else if( getRightBelowNeighbourMatches(terrainMap, id) != null ) {
                }            
                else { //surrounded by land
                    if(terrain != null) {
                        terrain.setSubtypeName("wasteland");
                        terrain.setPicture(terrain.getSubtypeName()+"end");                        
                    }
                }
            }
            
        }
        //End outer for
        
        newWorld.setTerrainMap(terrainMap);
        
        return newWorld;
    }
    
    
    /**
     * findNeighbours
     * 
     */
    private void findNeighbours(HashMap<Integer,Terrain> terrainMap, Integer id)
    {
        GridVector vector = heightMap.get(id);
        Terrain terrain = terrainMap.get(id);
        Terrain otherTerrain = null;
        String name = null;
        int count = 0;
        

           
        //initial test for surrounding terrain to get name
        //If the terrain doesn't already have a name 
        
        //look left 
        if(terrain.getName().equals("")) {         
           otherTerrain = getLeftNeighbourMatches(terrainMap, id);           
           compareAndSetTerrainName(terrain, otherTerrain);
        }
                   
        //look up
        if(terrain.getName().equals("")) {            
            otherTerrain = getAboveNeighbourMatches(terrainMap, id);                 
            compareAndSetTerrainName(terrain, otherTerrain); 
        }

        //look left and up (diagonal)   
        if(terrain.getName().equals("")) {         
            otherTerrain = getLeftAboveNeighbourMatches(terrainMap, id);
            compareAndSetTerrainName(terrain, otherTerrain); 
        }
           
        //look right and up (diagonal) 
        if(terrain.getName().equals("")) {        
            otherTerrain = getRightAboveNeighbourMatches(terrainMap, id);               
            compareAndSetTerrainName(terrain, otherTerrain); 
        }
           
        //look right
        if(terrain.getName().equals("")) {        
            otherTerrain = getRightNeighbourMatches(terrainMap, id);                 
            compareAndSetTerrainName(terrain, otherTerrain);
        }

        //look left and down (diagonal)  
        if(terrain.getName().equals("")) {        
            otherTerrain = getLeftBelowNeighbourMatches(terrainMap, id);
            compareAndSetTerrainName(terrain, otherTerrain);
        }
         
        //look down
        if(terrain.getName().equals("")) {        
            otherTerrain = getBelowNeighbourMatches(terrainMap, id);                 
            compareAndSetTerrainName(terrain, otherTerrain);  
        }        
        
        //look right and down (diagonal) 
        if(terrain.getName().equals("")) {        
            otherTerrain = getRightBelowNeighbourMatches(terrainMap, id);               
            compareAndSetTerrainName(terrain, otherTerrain);   
        }
                
        //If the terrain still has no name, create a new one
        if(terrain.getName().equals("")) {                       
            name = generateName(terrain.getSubtypeName());
            //System.out.println(name);
            terrain.setName(name);
        }
        
        //Otherwise use the name the terrain already had
        if(! (terrain.getName().equals("")) ) {        
           name = terrain.getName();
        }
               
        
        //Begin checking and renaming neighbours
        
        //LEFT
        Integer testID = id;
           
        //Don't let it go farther left if in the first column
        while( (testID % iDimensions) != 0 ) {
           Terrain leftTerrain = getLeftNeighbour(terrainMap, testID);
           
           if(leftTerrain == null) {
               break;
           }
           
           if(terrain.getSubtypeName()==leftTerrain.getSubtypeName()) {
                terrain = getLeftNeighbour(terrainMap, testID);                  
                if(terrain.getName().equals("")) {
                    //vector = heightMap.get(testID-1);
                    //System.out.println("(Left: "+vector.getX()+","+vector.getY()+") ");                         
                    terrain.setName(name);
                }
           }
           else {
               break;
           }
           //End if
           
           testID--;                
        }
        //End while 
        
        
        //UP
        testID = id;
        
        //If not the top row element, go up
        while(testID >= iDimensions)  {
            Terrain aboveTerrain = getAboveNeighbour(terrainMap, testID);
            
            if(aboveTerrain == null) {
               break;
            } 
            
            if(terrain.getSubtypeName()==aboveTerrain.getSubtypeName()) {
                terrain = getAboveNeighbour(terrainMap, testID);
                if(terrain.getName().equals("")) {
                    //vector = heightMap.get(testID-iDimensions);
                    //System.out.println("(Up: "+vector.getX()+","+vector.getY()+") ");                         
                    terrain.setName(name);
                }
            }
            else {
                break;
            }   
            
            testID=testID-iDimensions;                
            //End inner if                
        }
        //End while          
 
        
        //LEFT AND UP
        testID = id;
        
        //If not the top row or first column, go left and up
        while((testID >= iDimensions) && ((testID % iDimensions) != 0))  {
            Terrain leftaboveTerrain = getLeftAboveNeighbour(terrainMap, testID);
            
            if(leftaboveTerrain == null) {
               break;
            } 
            
            if(terrain.getSubtypeName()==leftaboveTerrain.getSubtypeName()) {
                terrain = getLeftAboveNeighbour(terrainMap, testID);
                if(terrain.getName().equals("")) {
                    //vector = heightMap.get(testID-iDimensions);
                    //System.out.println("(Up: "+vector.getX()+","+vector.getY()+") ");                         
                    terrain.setName(name);
                }
            }
            else {
                break;
            }   
            
            testID=testID-iDimensions-1;                
            //End inner if                
        }
        //End while
 
        
        //RIGHT AND UP
        testID = id;
        
        //If not the top row or last column, go right and up
        while((testID >= iDimensions) && ((testID+1 % iDimensions) != 0))  {
            Terrain rightaboveTerrain = getRightAboveNeighbour(terrainMap, testID);
            
            if(rightaboveTerrain == null) {
               break;
            } 
            
            if(terrain.getSubtypeName()==rightaboveTerrain.getSubtypeName()) {
                terrain = getRightAboveNeighbour(terrainMap, testID);
                if(terrain.getName().equals("")) {
                    //vector = heightMap.get(testID-iDimensions);
                    //System.out.println("(Up: "+vector.getX()+","+vector.getY()+") ");                         
                    terrain.setName(name);
                }
            }
            else {
                break;
            }   
            
            testID=testID-iDimensions+1;                
            //End inner if                
        }
        //End while
        
        
        //RIGHT
        testID = id;          
        
        //Don't let it go farther right if in the last column
        while( (testID+1 % iDimensions) != 0 ) {
            Terrain rightTerrain = getRightNeighbour(terrainMap, testID);
            
            if(rightTerrain == null) {
               break;
            } 
            
            if(terrain.getSubtypeName()==rightTerrain.getSubtypeName()) {
                terrain = getRightNeighbour(terrainMap, testID);                  
                if(terrain.getName().equals("")) {
                    //vector = heightMap.get(testID+1);
                    //System.out.println("(Right: "+vector.getX()+","+vector.getY()+") ");                           
                    terrain.setName(name);
                }
            }
            else {
                break;
            }  
            
            testID++;
        }
        //End while                                  

         
        /*
        //LEFT AND DOWN
        testID = id;
       
        //If not the bottom row or first column, go down and left
        while( (testID < ((iDimensions*iDimensions)-(iDimensions))) && ((testID % iDimensions) != 0) ) {
            Terrain leftbelowTerrain = getLeftBelowNeighbour(terrainMap, testID);
            
            if(leftbelowTerrain == null) {
               break;
            }      
                    
            if(terrain.getSubtypeName()==leftbelowTerrain.getSubtypeName()) {
                    terrain = getLeftBelowNeighbour(terrainMap, testID);
                    if(terrain.getName().equals("")) {
                        //vector = heightMap.get(testID+iDimensions);
                        //System.out.println("(Down: "+vector.getX()+","+vector.getY()+") ");                          
                        terrain.setName(name);
                    }
            }
            else {
                break;
            }
            
            testID=testID+iDimensions-1;                                
        }
        //End while         
        

        //DOWN
        testID = id;
       
        //If not the bottom row, go down
        while(testID < ((iDimensions*iDimensions)-(iDimensions)) )  {
            Terrain belowTerrain = getBelowNeighbour(terrainMap, testID);
            
            if(belowTerrain == null) {
               break;
            }      
                    
            if(terrain.getSubtypeName()==belowTerrain.getSubtypeName()) {
                    terrain = getBelowNeighbour(terrainMap, testID);
                    if(terrain.getName().equals("")) {
                        //vector = heightMap.get(testID+iDimensions);
                        //System.out.println("(Down: "+vector.getX()+","+vector.getY()+") ");                          
                        terrain.setName(name);
                    }
            }
            else {
                break;
            }
            
            testID=testID+iDimensions;                                
        }
        //End while  

        
        //RIGHT AND DOWN
        testID = id;
       
        //If not the bottom row or last column, go down and right
        while( (testID < ((iDimensions*iDimensions)-(iDimensions))) && ((testID+1 % iDimensions) != 0) ) {
            Terrain rightbelowTerrain = getRightBelowNeighbour(terrainMap, testID);
            
            if(rightbelowTerrain == null) {
               break;
            }      
                    
            if(terrain.getSubtypeName()==rightbelowTerrain.getSubtypeName()) {
                    terrain = getRightBelowNeighbour(terrainMap, testID);
                    if(terrain.getName().equals("")) {
                        //vector = heightMap.get(testID+iDimensions);
                        //System.out.println("(Down: "+vector.getX()+","+vector.getY()+") ");                          
                        terrain.setName(name);
                    }
            }
            else {
                break;
            }
            
            testID=testID+iDimensions+1;                                
        }
        //End while
        */
        
    }
    
    
    /**
     * getLeftNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the left of same subtype (null if none)
     */
     private Terrain getLeftNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {
              Terrain terrain = terrainMap.get(id);
              Terrain leftTerrain = getLeftNeighbour(terrainMap, id);
              if(leftTerrain != null) {
                  if(terrain.getSubtypeName()==leftTerrain.getSubtypeName()) {                  
                      return leftTerrain;
                  }
              }
              //End if
          }
          
          return null;
     }

     
    /**
     * getRightNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the right of same subtype (null if none)
     */
     private Terrain getRightNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {        
              Terrain terrain = terrainMap.get(id);
              Terrain rightTerrain = getRightNeighbour(terrainMap, id);
              if(rightTerrain != null) {
                  if(terrain.getSubtypeName()==rightTerrain.getSubtypeName()) {                  
                      return rightTerrain;
                  }
              }
              //End if
          }
        
          return null;
     }
     
     
    /**
     * getAboveNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found above of same subtype (null if none)
     */
     private Terrain getAboveNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {         
              Terrain terrain = terrainMap.get(id);
              Terrain aboveTerrain = getAboveNeighbour(terrainMap, id);
              if(aboveTerrain != null) {
                  if(terrain.getSubtypeName()==aboveTerrain.getSubtypeName()) {                  
                      return aboveTerrain;
                  }
              }
              //End if
          }
        
          return null;
     }
     
     
    /**
     * getBelowNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found below of same subtype (null if none)
     */
     private Terrain getBelowNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {          
              Terrain terrain = terrainMap.get(id);
              Terrain belowTerrain = getBelowNeighbour(terrainMap, id);
              if(belowTerrain != null) {
                  if(terrain.getSubtypeName()==belowTerrain.getSubtypeName()) {                  
                      return belowTerrain;
                  }
              }
          }
          //End if
        
          return null;
     }
     

    
    
    /**
     * getLeftAboveNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the left and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getLeftAboveNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {          
              Terrain terrain = terrainMap.get(id);
              Terrain leftaboveTerrain = getLeftAboveNeighbour(terrainMap, id);
              if(leftaboveTerrain != null) {
                  if(terrain.getSubtypeName()==leftaboveTerrain.getSubtypeName()) {                  
                      return leftaboveTerrain;
                  }
              }
          }
          //End if
        
          return null;
     }

     
    /**
     * getRightAboveNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the right and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getRightAboveNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {          
              Terrain terrain = terrainMap.get(id);
              Terrain rightaboveTerrain = getRightAboveNeighbour(terrainMap, id);
              if(rightaboveTerrain != null) {
                  if(terrain.getSubtypeName()==rightaboveTerrain.getSubtypeName()) {                  
                      return rightaboveTerrain;
                  }
              }
          }
          //End if
        
          return null;
     }

    
    
    /**
     * getLeftBelowNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the left and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getLeftBelowNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {          
              Terrain terrain = terrainMap.get(id);
              Terrain leftbelowTerrain = getLeftBelowNeighbour(terrainMap, id);
              if(leftbelowTerrain != null) {
                  if(terrain.getSubtypeName()==leftbelowTerrain.getSubtypeName()) {                  
                      return leftbelowTerrain;
                  }
              }
          }
          //End if
        
          return null;
     }

     
    /**
     * getRightBelowNeighbourMatches
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the right and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getRightBelowNeighbourMatches(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
          if(terrainMap.containsKey(id)) {          
              Terrain terrain = terrainMap.get(id);
              Terrain rightbelowTerrain = getRightBelowNeighbour(terrainMap, id);
              if(rightbelowTerrain != null) {
                  if(terrain.getSubtypeName()==rightbelowTerrain.getSubtypeName()) {                  
                      return rightbelowTerrain;
                  }
              }
          }
          //End if
        
          return null;
     }
 
     
    /**
     * compareAndSetTerrainName
     * 
     * @param terrain    : The terrain with the name to be set
     * @param terrain    : A neighbouring terrain
     */
     private void compareAndSetTerrainName(Terrain terrain, Terrain otherTerrain)
     {
         if(terrain != null) {
             if(terrain.getName().equals("")) {          
                 if(otherTerrain != null) {
                     if(otherTerrain.getName().equals("")) {
                     }
                     else { 
                         terrain.setName(otherTerrain.getName());
                     }
                 }
             }
         }
     }
     
     
    /**
     * generateName
     * 
     * @param type : a String defining terrain type
     * @return     : a String name
     */
    private String generateName(String type)
    {
        Random random = new Random();
        int randomNumber = random.nextInt(10);
        
        StringBuffer name = new StringBuffer(100);
        
        name.append("The ");
 
         switch(randomNumber) {
            case 0:
                name.append("windy ");
            break;

            case 1:
                name.append("grey ");
                break;
            
            case 2:
                name.append("gloomy ");
                break;
            
            case 3:
                name.append("ringing ");
                break;            

            case 4:
                name.append("whistling ");
                break;   
            
            case 5:
                name.append("rolling ");
                break;   
            
            case 6:
                name.append("endless ");
                break;   
            
            case 7:
                name.append("cursed ");
                break;               

            case 8:
                name.append("wild ");
                break;              
            
            case 9:
                name.append("eternal ");
                break;              

            case 10:
                name.append("wandering ");
                break;                 
                
            default:
                break;
        }       
        
        
        randomNumber = random.nextInt(10);
        
        if(type.equals("mountain") || type.equals("snowmountain") || type.equals("earthmountain")) {
            switch(randomNumber) {
                case 0: case 1: case 3:
                    name.append("peaks ");
                    break;
            
                case 4: case 5:
                    name.append("stairs ");
                    break;                    

                case 6: case 7:
                    name.append("teeth ");
                    break;                                         
                    
                default:
                    name.append("mountains ");                
                    break;
            }
        }
        else if(type.equals("hill") || type.equals("snowhill") || type.equals("dryhill") || type.equals("firehill")) {
            switch(randomNumber) {
                case 0: case 1:
                    name.append("mounds");
                    break;
            
                case 2: case 3: case 4:
                    name.append("foothills");
                    break;                    

                case 5: case 6:
                    name.append("heights");
                    break;                       
                    
                default:
                    name.append("hills");                
                    break;
            }
        }
        else if(type.equals("forest") || type.equals("snowforest") || type.equals("verdantforest")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("woods");
                    break;
            
                case 3: case 4:
                    name.append("greenwood");
                    break;                    

                case 5: case 6:
                    name.append("grove");
                    break;                       
                    
                default:
                    name.append("forest");                
                    break;
            }
        }
         else if(type.equals("glowforest")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("woods");
                    break;
            
                case 3: case 4:
                    name.append("goldenwood");
                    break;                    

                case 5: case 6:
                    name.append("grove");
                    break;                       
                    
                default:
                    name.append("forest");                
                    break;
            }
        }
         else if(type.equals("glowplains")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("fields");
                    break;
            
                case 3: case 4:
                    name.append("shimmerwalk");
                    break;                    

                case 5: case 6:
                    name.append("goldensteppe");
                    break;                       
                    
                default:
                    name.append("plains");                
                    break;
            }
        } 
         else if(type.equals("fireplains")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("redfields");
                    break;
            
                case 3: case 4:
                    name.append("lavawalk");
                    break;                    

                case 5: case 6:
                    name.append("firesteppe");
                    break;                       
                    
                default:
                    name.append("burning plains");                
                    break;
            }
        }
         else if(type.equals("waterplains")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("lagoon");
                    break;
            
                case 3: case 4:
                    name.append("sea");
                    break;                    

                case 5: case 6:
                    name.append("waves");
                    break;                       
                    
                default:
                    name.append("waters");                
                    break;
            }
        }
         else if(type.equals("airplains")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("skywalk");
                    break;
            
                case 3: case 4:
                    name.append("mists");
                    break;                    

                case 5: case 6:
                    name.append("clouds");
                    break;                       
                    
                default:
                    name.append("air plains");                
                    break;
            }
        }         
         else if(type.equals("toxic")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("wormwood");
                    break;
            
                case 3: case 4:
                    name.append("fronds");
                    break;                    

                case 5: case 6:
                    name.append("tangle");
                    break;                       
                    
                default:
                    name.append("bloodwood");                
                    break;
            }
        }        
        else if(type.equals("desert") || type.equals("snowdesert") || type.equals("wasteland")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("sands");
                    break;
            
                case 3: case 4:
                    name.append("siltwalk");
                    break;                    

                case 5: case 6:
                    name.append("wastes");
                    break;                       
                    
                default:
                    name.append("desert");                
                    break;
            }
        } 
        else if(type.equals("tundra")) {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("steppes");
                    break;
            
                case 3: case 4:
                    name.append("cold plains");
                    break;                    

                case 5: case 6:
                    name.append("cold fields");
                    break;                       
                    
                default:
                    name.append("tundra");                
                    break;
            }
        }        
        else if(type.equals("sea")) {
            switch(randomNumber) {
                case 0: case 1: case 2: case 3:
                    name.append("ocean");
                    break;
            
                case 4: case 5:
                    name.append("shaouls");
                    break;                    

                case 6: case 7:
                    name.append("waves");
                    break;                       
                    
                default:
                    name.append("sea");                
                    break;
            }
        }
        else if(type.equals("lake")) {
            switch(randomNumber) {
                case 0: case 1: case 2: case 3:
                    name.append("waters");
                    break;
            
                case 4: case 5:
                    name.append("inner sea");
                    break;                    

                case 6: case 7:
                    name.append("lagoon");
                    break;                       
                    
                default:
                    name.append("lake");                
                    break;
            }
        } 
        else if( type.equals("light") || type.equals("brightlight") ) {
            switch(randomNumber) {
                case 0: case 1: case 2: case 3:
                    name.append("sun waves");
                    break;
            
                case 4: case 5:
                    name.append("light");
                    break;                    

                case 6: case 7:
                    name.append("energies");
                    break;                       
                    
                default:
                    name.append("brightway");                
                    break;
            }
        }
        else if( type.equals("darkness") || type.equals("deepdarkness") ) {
            switch(randomNumber) {
                case 0: case 1: case 2: case 3:
                    name.append("emptiness");
                    break;
            
                case 4: case 5:
                    name.append("darkness");
                    break;                    

                case 6: case 7:
                    name.append("inkwell");
                    break;                       
                    
                default:
                    name.append("blackness");                
                    break;
            }
        }        
        else if(type.equals("ice")) {
            switch(randomNumber) {
                case 0: case 1: case 2: case 3:
                    name.append("white");
                    break;
            
                case 4: case 5:
                    name.append("emptiness");
                    break;                    

                case 6: case 7:
                    name.append("chill");
                    break;                       
                    
                default:
                    name.append("ice");                
                    break;
            }
        }        
        else {
            switch(randomNumber) {
                case 0: case 1: case 2:
                    name.append("fields");
                    break;
            
                case 3: case 4:
                    name.append("praries");
                    break;                    

                case 5: case 6:
                    name.append("savannah");
                    break;                       
                    
                default:
                    name.append("plains");                
                    break;
            }
        }         
        
        return name.toString();        
    }

        
    /**
     * getLeftNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the left of same subtype (null if none)
     */
     private Terrain getLeftNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                Terrain leftTerrain;
                
                //If first column element, also go down one row
                if( id % iDimensions == 0 ) { 
                    leftTerrain = terrainMap.get( id+iDimensions-1 );
                }
                else { //otherwise just move left
                    leftTerrain = terrainMap.get( id-1 );                 
                    return leftTerrain;             
                }
                //End if
            }
        }
        
        return null;
     }
     
     
    /**
     * getRightNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the right of same subtype (null if none)
     */
     private Terrain getRightNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                Terrain rightTerrain;
                
                //If the last column element move right and up one
                if( ( (id+1) % iDimensions ) == 0 ) {  
                    rightTerrain = terrainMap.get( id-iDimensions+1 ); 
                }        
                else { //otherwise just right
                    rightTerrain = terrainMap.get(id+1);                 
                    return rightTerrain;
                }
                //End if
            }
        }
        
        return null;
     }     
     
     
    /**
     * getAboveNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found above of same subtype (null if none)
     */
     private Terrain getAboveNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                
                //If not a top element, get above
                if( id >= iDimensions ) {  
                    Terrain aboveTerrain = terrainMap.get( id-iDimensions );               
                    return aboveTerrain;             
                }
                //End if
            }
        }
        
        return null;
     }     

     
    
    /**
     * getLeftAboveNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the left and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getLeftAboveNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                Terrain leftaboveTerrain;
                
                //If top row element
                if(id < iDimensions) {  
                   //return null;
                }                                               
                //If other first column element, move left (auto moves up)
                else if( id % iDimensions == 0 ) { 
                    leftaboveTerrain = terrainMap.get( id-1 );
                    return leftaboveTerrain;                     
                }
                else { //otherwise move left and up
                    leftaboveTerrain = terrainMap.get( id-iDimensions-1 );  
                    return leftaboveTerrain;                      
                }
                //End if               
            }      
        }
        
        return null;
     }     

     
    /**
     * getRightAboveNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the right and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getRightAboveNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                Terrain rightaboveTerrain;
                
                //If top row element
                if(id < iDimensions) {  
                   //return null;
                }                                               
                //If other last column element, move right and up 2
                else if( ( id+1 ) % iDimensions == 0 ) { 
                    rightaboveTerrain = terrainMap.get( id-(iDimensions+iDimensions)+1 );
                    return rightaboveTerrain;                     
                }
                else { //otherwise move right and up
                    rightaboveTerrain = terrainMap.get( id-iDimensions+1 );  
                    return rightaboveTerrain;                      
                }
                //End if                 
            }
        }
        
        return null;
     }        
          
     
    /**
     * getBelowNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found below of same subtype (null if none)
     */
     private Terrain getBelowNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
         
                //If not a last row element, get below
                if(id < ( ( iDimensions*iDimensions )-( iDimensions ) ) ) {  
                    Terrain belowTerrain = terrainMap.get(id+iDimensions);                 
                    return belowTerrain;
                }
                //End if
            }
        }
            
        return null;
     }     
     
    
    /**
     * getLeftBelowNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the left and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getLeftBelowNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                Terrain leftbelowTerrain;

                //If bottom row element
                if(id >= ( ( iDimensions*iDimensions )-( iDimensions ) ) ) {  
                   //return null;
                }                                               
                //If other first column element, move left and down two
                else if( id % iDimensions == 0 ) { 
                    leftbelowTerrain = terrainMap.get( id+(iDimensions+iDimensions)-1 );
                    return leftbelowTerrain;                     
                }
                else { //otherwise move left and down
                    leftbelowTerrain = terrainMap.get( id+iDimensions-1 );  
                    return leftbelowTerrain;                      
                }
                //End if
            }
        }
            
        return null;
     }

     
    /**
     * getRightBelowNeighbour
     * 
     * @param terrainMap : The terrain map
     * @param id         : The id of the current terrain square
     * @return Terrain   : The terrain object found to the right and up (diagonal)
     *                     of same subtype (null if none)
     */
     private Terrain getRightBelowNeighbour(HashMap<Integer,Terrain> terrainMap, Integer id)
     {
        if(terrainMap != null) {         
            if(terrainMap.containsKey(id)) {         
                Terrain terrain = terrainMap.get(id); 
                Terrain rightbelowTerrain;
                
                //If bottom row element
                if(id >= ( ( iDimensions*iDimensions )-( iDimensions ) ) ) {  
                   //return null;
                }                                               
                //If other last column element, move right (auto moves down)
                else if( ( id+1 ) % iDimensions == 0 ) { 
                    rightbelowTerrain = terrainMap.get( id+1 );
                    return rightbelowTerrain;                     
                }
                else { //otherwise move right and down
                    rightbelowTerrain = terrainMap.get( id+iDimensions+1 );  
                    return rightbelowTerrain;                      
                }
                //End if                  
            }
        }
        
        return null;
     }
 

    /**
     * convertXYtoGrid
     * 
     * @param x : x-coordinate
     * @param y : y-coordinate
     * @return  : an int representing position on an x,y grid, numbered from 0
     *            to (x+1)*(y+1)-1 (eg. 0 to 15 for a 0-3, 0-3 x and y axis)
     */
    private int convertXYtoGrid(int x, int y)
    {
        if(y < iDimensions) {
            return x;
        }
        else if (x == 0) {
            return ((iDimensions+1)*y);
        }
        else {
            return ( ( (iDimensions+1)*y ) + x );
        }        
    }
  
    
    /**
     * calculateTemperature calculates a base temperature for a square of
     * terrain based on its location (polar, equator, or other), the amount
     * of land surrounding the square, whether it is a water square or not,
     * the season, and the elevation.  Kelvin is used here.
     */
    private float calculateTemperature(Terrain terrain, int iSeason)
    {               
        float fTemp = 280.0f;  //287.2K is about 14 celcius
        
        //Get equatorial zone and polar zone
        float fHalf = (float)(iDimensions*iDimensions) / 2;
        float fTenth = (float)(iDimensions*iDimensions) / 10;
        
        float fEquatorTop = fHalf - fTenth;
        float fEquatorBottom = fHalf + fTenth;
        
        float fPolarNorth = fTenth;
        float fPolarSouth = (float)(iDimensions*iDimensions) - fTenth; 
        
        String subtype = terrain.getSubtypeName();
            
        float z = terrain.getLocation().getZ();
        int id = terrain.getID();         
        
        int landSum = countland(terrain);
                
        //Water
        if ( subtype.equals("sea") || subtype.equals("lake") ) {
            fTemp -= 5;
        }
        else {
            fTemp += 5;         
        }
        
        
        //Poles and equator
        if(id < fPolarNorth || id > fPolarSouth) {
            fTemp -= 20;
        }
        else if(id > fEquatorTop && id < fEquatorBottom) {
            fTemp += 20;                
        }           
            
        
        //Land ratio    
        if(landSum > 75) { //very mountainous
            fTemp -= 20;            
        }                
        else if(landSum > 55) { //lots o land
            fTemp -= 10;            
        }
        else if(landSum > 25) { //land
        }
        else { //water dominates
            fTemp += 5;            
        }

        
        //Seasons
        if(id <= (int)fHalf) { //northern hemisphere
            if(landSum > 55) { 
                if(iSeason == 1) { //summer
                    fTemp += 20;            
                }
                else if(iSeason == 3) { //winter
                    if(id > fEquatorTop && id < fEquatorBottom) {
                        fTemp -=20;
                    }
                    else {
                        fTemp -= 30;
                    }
                }
            }
            else if(landSum > 25) {          
                if(iSeason == 1) { //summer
                    fTemp += 10;            
                }
                else if(iSeason == 3) { //winter
                    if(id > fEquatorTop && id < fEquatorBottom) {
                        fTemp -=10;
                    }
                    else {
                        fTemp -= 20;
                    }
                }
            }
            else {          
                if(iSeason == 1) { //summer
                    fTemp += 5;            
                }
                else if(iSeason == 3) { //winter
                    if(id > fEquatorTop && id < fEquatorBottom) {
                        fTemp -=5;
                    }
                    else {
                        fTemp -= 10;
                    }
                }
            }
        }
        else { //southern hemisphere
            if(landSum > 55) { 
                if(iSeason == 3) { //winter
                    fTemp += 20;            
                }
                else if(iSeason == 1) { //summer
                    if(id > fEquatorTop && id < fEquatorBottom) {
                        fTemp -=20;
                    }
                    else {
                        fTemp -= 30;
                    }
                }
            }
            else if(landSum > 25) {          
                if(iSeason == 3) { //winter
                    fTemp += 10;            
                }
                else if(iSeason == 1) { //summer
                    if(id > fEquatorTop && id < fEquatorBottom) {
                        fTemp -=10;
                    }
                    else {
                        fTemp -= 20;
                    }
                }
            }
            else {          
                if(iSeason == 3) { //winter
                    fTemp += 5;            
                }
                else if(iSeason == 1) { //summer
                    if(id > fEquatorTop && id < fEquatorBottom) {
                        fTemp -=5;
                    }
                    else {
                        fTemp -= 10;
                    }
                }
            }            
        }
        
        //Elevations
        if(z >= elevationHighest) {
            fTemp -= 20;            
        }
        else if(z >= elevationHighest/4) {
            fTemp -= 10;            
        }
        else if(z >= elevationLowest/4) {
             fTemp += 5;
        }
        else { 
            fTemp -= 5;             
        }
        
        return fTemp;
    }

    
    /**
     * scaledTemperature averages out the temperature of a terrain square
     * based on surrounding squares (looking 2 over in each direction).
     * Celcius is used here.
     */
    private float scaledTemperature(HashMap<Integer,Terrain> terrainMap, int id, int iSeason)
    {  
        float fTemp = 10; //default
        float fNum = 9;
        float fSum = 0;
        Terrain terrain = terrainMap.get(id);        
        
        if(terrain != null) {
            fSum = terrain.getTemperature(iSeason);
            fTemp = fSum;
            
            Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
            if(leftNeighbour != null) {
                float fTotal = leftNeighbour.getTemperature(iSeason);
                leftNeighbour = getLeftNeighbour(terrainMap, leftNeighbour.getID());
                if(leftNeighbour != null) {
                    fTotal += leftNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }                          
            Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
            if(rightNeighbour != null) {
                float fTotal = rightNeighbour.getTemperature(iSeason);
                rightNeighbour = getRightNeighbour(terrainMap, rightNeighbour.getID());
                if(rightNeighbour != null) {
                    fTotal += rightNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }
            Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
            if(aboveNeighbour != null) {
                float fTotal = aboveNeighbour.getTemperature(iSeason);
                aboveNeighbour = getAboveNeighbour(terrainMap, aboveNeighbour.getID());
                if(aboveNeighbour != null) {
                    fTotal += aboveNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }            
            Terrain aboveleftNeighbour = getLeftAboveNeighbour(terrainMap, id);
            if(aboveleftNeighbour != null) {
                float fTotal = aboveleftNeighbour.getTemperature(iSeason);
                aboveleftNeighbour = getLeftAboveNeighbour(terrainMap, aboveleftNeighbour.getID());
                if(aboveleftNeighbour != null) {
                    fTotal += aboveleftNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }
            Terrain aboverightNeighbour = getRightAboveNeighbour(terrainMap, id);
            if(aboverightNeighbour != null) {
                float fTotal = aboverightNeighbour.getTemperature(iSeason);
                aboverightNeighbour = getRightAboveNeighbour(terrainMap, aboverightNeighbour.getID());
                if(aboverightNeighbour != null) {
                    fTotal += aboverightNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }
            Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
            if(belowNeighbour != null) {
                float fTotal = belowNeighbour.getTemperature(iSeason);
                belowNeighbour = getBelowNeighbour(terrainMap, belowNeighbour.getID());
                if(belowNeighbour != null) {
                    fTotal += belowNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }            
            Terrain belowleftNeighbour = getLeftBelowNeighbour(terrainMap, id);
            if(belowleftNeighbour != null) {
                float fTotal = belowleftNeighbour.getTemperature(iSeason);
                belowleftNeighbour = getLeftBelowNeighbour(terrainMap, belowleftNeighbour.getID());
                if(belowleftNeighbour != null) {
                    fTotal += belowleftNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }
            Terrain belowrightNeighbour = getRightBelowNeighbour(terrainMap, id);
            if(belowrightNeighbour != null) {
                float fTotal = belowrightNeighbour.getTemperature(iSeason);
                belowrightNeighbour = getRightBelowNeighbour(terrainMap, belowrightNeighbour.getID());
                if(belowrightNeighbour != null) {
                    fTotal += belowrightNeighbour.getTemperature(iSeason);
                    fSum += (fTotal/2);
                }
                else {
                    fSum += fTotal;
                }
            }
            else {
                fSum += fTemp;
            }            
        }
        else {
            fSum = fTemp*fNum;
        }
        
        return (fSum / fNum);
    }
    
    
    /**
     * computeTemperature calls calculateTemperature to calculate the base
     * temperatures for each terrain square, and then scaledTemperature to
     * make the changes more gradual from square to square.
     * 
     * @param newWorld
     */
    private World computeTemperature(World newWorld)
    {
        float MAX_TEMP = 340.0f; //331K is about 57,8 celcius, so this allows near-earth variation
        float MIN_TEMP = 180.0f; //184K is about -89.2 celcius
        float tmax = 180.0f, tmin = 340.0f, tmean; //For calculating actual max, min, mean         
        int iSeason;
        double [] tland;
        double [] tsea;
        float fTemp;
        tland = new double [iNumSeasons];
        tsea = new double [iNumSeasons];
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();
        Set<Integer> terrainSet = terrainMap.keySet();        
        
        
        //Go through the terrain
        for (iSeason = 0; iSeason < iNumSeasons; iSeason++) {
            for(Integer iTemp : terrainSet) {
                Terrain terrain = terrainMap.get(iTemp);
                fTemp = calculateTemperature(terrain, iSeason);
                fTemp -= 274; //273.15 Kelvin till celsius                
                terrain.setTemperature(iSeason, fTemp);                
            }
        }
        
        //Go through again and scale the temperatures
        for (iSeason = 0; iSeason < iNumSeasons; iSeason++) {
            for(Integer iTemp : terrainSet) {
                Terrain terrain = terrainMap.get(iTemp);
                if(terrain != null) {
                    int id = terrain.getID();
                    fTemp = scaledTemperature(terrainMap, id, iSeason);                
                    terrain.setTemperature(iSeason, fTemp);
                }
            }
        }        
         
        //avgScaledTemp /= (iDimensions * iDimensions);          
         
        newWorld.setTerrainMap(terrainMap);
        return newWorld;
    }

  
    /**
      Called by calculateTemperatures for each Terrain, this function looks
      in a 11 wide by 5 high box and counts the number of land squares 
      found there.  It compensates for y values off the map, and wraps x
      values around.
      */
    private int countland (Terrain terrain)
    {
        int sum=0;
        float jmin, jmax, j1, i0, i1;
        float x = terrain.getLocation().getX();
        float y = terrain.getLocation().getY();
        
        jmin = y - 2;
        
        if (jmin < 0)  {
            jmin = 0;
        }
        
        jmax = y + 2;
  
        if (jmax >= iDimensions)  {
            jmax = iDimensions-1;
        }
        
        for (j1 = jmin; j1 <= jmax; j1++)  {
            for (i0 = -5; i0 < 6; i0++) {
                
                i1 = i0 + x;
                
                if (i1 < 0)  {
                    i1 += iDimensions;
                }
                if (i1 >= iDimensions)  {
                    i1 -= iDimensions;
                }
                
                
                if(terrain != null) {
                    if( (terrain.getSubtypeName().equals("sea")) || (terrain.getSubtypeName().equals("lake")) || (terrain.getSubtypeName().equals("light"))
                        || (terrain.getSubtypeName().equals("brightlight")) || (terrain.getSubtypeName().equals("darkness")) || (terrain.getSubtypeName().equals("deepdarkness")) )  {
                        sum += 0;
                    }
                    else if(terrain.getSubtypeName().equals("mountain") || terrain.getSubtypeName().equals("earthmountain") || terrain.getSubtypeName().equals("airplains")) {
                        sum += 2;
                    }
                    else {
                        sum += 1;
                    }
                }
            }
            //End inner for
        }
        //End outer for
        
        return sum;
    }
    
    
    /**
     * computePressure is a basic method to calculate pressure zones, used later
     * for wind, rain, and climate.
     */
    private World computePressure(World newWorld)
    {
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();          

        //Get equatorial zone and polar zone
        float fHalf = (float)(iDimensions*iDimensions) / 2;
        float fTenth = (float)(iDimensions*iDimensions) / 10;
        
        float fEquatorTop = fHalf - fTenth;
        float fEquatorBottom = fHalf + fTenth;
        
        float fPolarNorth = fTenth;
        float fPolarSouth = (float)(iDimensions*iDimensions) - fTenth; 
        
        //Get desert zone 1 & 2
        float fDesertZone1 = fEquatorTop - fTenth;
        float fDesertZone2 = fEquatorBottom + fTenth;
        
        Set<Integer> terrainSet = terrainMap.keySet();         
        
        //Go through the terrain
        for (int iSeason = 0; iSeason < iNumSeasons; iSeason++) {        
            for(Integer iTemp : terrainSet) {
                Terrain terrain = terrainMap.get(iTemp);
                int id = terrain.getID();
                float fHeight = terrain.getLocation().getZ();
            
                if(fHeight <= 0) {
                    fHeight += elevationHighest;
                }
                else {
                    fHeight += elevationLowest;                
                }            
            
                //Adjust high zones over the desert zones
                if(id >= fDesertZone1 && id < fEquatorTop) {
                    if(fHeight <= 0) {
                        fHeight += elevationHighest;
                    }
                }
                else if(id > fEquatorBottom && id <= fDesertZone2) {
                    if(fHeight <= 0) {
                        fHeight += elevationHighest;
                    }
                }

                //Adjust high zones over the polar areas (mostly)
                if(id >= fPolarSouth || id <= fPolarNorth) {
                    if(fHeight < 0) {
                        fHeight += elevationHighest;
                    }
                }           
            
                //Adjust low zones over the equator (mostly)
                if(id >= fEquatorTop && id <= fEquatorBottom) {
                    if(fHeight > 0) {
                        fHeight += elevationLowest;
                    }                
                }
            
                if(iSeason == 1) { //add to lows in summer
                    fHeight += (elevationLowest / 4);
                }
                else if(iSeason == 3) { //add to highs in winter
                    fHeight += (elevationHighest / 4);                
                }            
            
                terrain.setPressure(iSeason, fHeight);
            }
        }
    
        return newWorld;
    }

    
    /**
     * calculateWind
     */
    private GridVector calculateWind(HashMap<Integer,Terrain> terrainMap, Terrain terrain, int iSeason)
    {
         int startVelocity = 0;
         int velocity = 0; //largest difference
         float direction = 0; //clockwise >=
         GridVector vector = null;     
         
         if(terrain != null) {
             startVelocity = (int)terrain.getPressure(iSeason);
             velocity = startVelocity;
             int id = terrain.getID();

             Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
             if(rightaboveNeighbour != null) {
                 int temp = (int)rightaboveNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 45;
                 }
             }             
             Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
             if(aboveNeighbour != null) {
                 int temp = (int)aboveNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 90;
                 }
             }             
             Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
             if(leftaboveNeighbour != null) {
                 int temp = (int)leftaboveNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 135;
                 }
             }             
             Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
             if(leftNeighbour != null) {
                 int temp = (int)leftNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 180;
                 }
             }             
             Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
             if(leftbelowNeighbour != null) {
                 int temp = (int)leftbelowNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 225;
                 }
             }
             Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
             if(belowNeighbour != null) {
                 int temp = (int)belowNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 270;
                 }
             }             
             Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
             if(rightbelowNeighbour != null) {
                 int temp = (int)rightbelowNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 315;
                 }
             }             
             Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
             if(rightNeighbour != null) {
                 int temp = (int)rightNeighbour.getPressure(iSeason);
                 if(temp <= velocity) {
                     velocity = temp;
                     direction = 0;
                 }
             }
             
             int result = (startVelocity - velocity);
             
             vector = new GridVector(terrain.getLocation().getX(),terrain.getLocation().getY(),terrain.getLocation().getZ(),
                                     (float)result, direction);             
         }
         
         return vector;
    }
    
    
    /**
     * computeWind is a basic method for computing wind patterns.
     */
    private World computeWind(World newWorld)
    {
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();
        GridVector vector = null;
        
        Set<Integer> terrainSet = terrainMap.keySet();         
        
        //Go through the terrain
        for (int iSeason = 0; iSeason < iNumSeasons; iSeason++) {        
            for(Integer iTemp : terrainSet) {
                Terrain terrain = terrainMap.get(iTemp);                
                vector = calculateWind(terrainMap, terrain, iSeason);
                if(terrain != null) {
                    terrain.setWind(iSeason, vector);
                }
            }
        }
        
        return newWorld;
    }
    
    
    /**
     * calculateRainfall calculates the first base values for rainfall with wind
     */
    private void calculateRainfallWithWind(HashMap<Integer,Terrain> terrainMap, Terrain terrain, int iSeason)
    {         
         if(terrain != null) {
             GridVector wind = terrain.getWind(iSeason);
             int velocity = (int)wind.getVelocity();
             
             if(velocity <= 0) {
                 return;
             }
             
             float direction = wind.getDirection();
             
             int id = terrain.getID();

             if(direction == 45) {
                 Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
                 if(rightaboveNeighbour != null) {                   
                     if(rightaboveNeighbour.getSubtypeName().equals("mountain") || rightaboveNeighbour.getSubtypeName().equals("earthmountain")
                       || rightaboveNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;
                         /*
                         if(rain > 0) {
                             rain += velocity*2;
                         }
                         else {
                             rain += velocity;
                         }*/
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)rightaboveNeighbour.getRainfall(iSeason);
                         rain += velocity;                         
                         rightaboveNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }
             if(direction == 90) {
                 Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
                 if(aboveNeighbour != null) {                   
                     if(aboveNeighbour.getSubtypeName().equals("mountain") || aboveNeighbour.getSubtypeName().equals("earthmountain")
                       || aboveNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;                          
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)aboveNeighbour.getRainfall(iSeason);
                         rain += velocity;                           
                         aboveNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }
             if(direction == 135) {
                 Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
                 if(leftaboveNeighbour != null) {                   
                     if(leftaboveNeighbour.getSubtypeName().equals("mountain") || leftaboveNeighbour.getSubtypeName().equals("earthmountain")
                       || leftaboveNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;                          
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)leftaboveNeighbour.getRainfall(iSeason);
                         rain += velocity;                          
                         leftaboveNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }
             if(direction == 180) {
                 Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
                 if(leftNeighbour != null) {                   
                     if(leftNeighbour.getSubtypeName().equals("mountain") || leftNeighbour.getSubtypeName().equals("earthmountain")
                       || leftNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;                          
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)leftNeighbour.getRainfall(iSeason);
                         rain += velocity;                           
                         leftNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }             
             if(direction == 225) {
                 Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
                 if(leftbelowNeighbour != null) {                   
                     if(leftbelowNeighbour.getSubtypeName().equals("mountain") || leftbelowNeighbour.getSubtypeName().equals("earthmountain")
                       || leftbelowNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);                         
                         rain += velocity;                          
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)leftbelowNeighbour.getRainfall(iSeason);
                         rain += velocity;                          
                         leftbelowNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }              
             if(direction == 270) {
                 Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
                 if(belowNeighbour != null) {                   
                     if(belowNeighbour.getSubtypeName().equals("mountain") || belowNeighbour.getSubtypeName().equals("earthmountain")
                       || belowNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;                           
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)belowNeighbour.getRainfall(iSeason);
                         rain += velocity;                          
                         belowNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }            
             if(direction == 315) {
                 Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
                 if(rightbelowNeighbour != null) {                   
                     if(rightbelowNeighbour.getSubtypeName().equals("mountain") || rightbelowNeighbour.getSubtypeName().equals("earthmountain")
                       || rightbelowNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;                          
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)rightbelowNeighbour.getRainfall(iSeason);
                         rain += velocity;                         
                         rightbelowNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }
             if(direction == 0) {
                 Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
                 if(rightNeighbour != null) {                   
                     if(rightNeighbour.getSubtypeName().equals("mountain") || rightNeighbour.getSubtypeName().equals("earthmountain")
                       || rightNeighbour.getSubtypeName().equals("airplains") ) {
                         int rain = (int)terrain.getRainfall(iSeason);
                         rain += velocity;                          
                         terrain.setRainfall(iSeason, (float)rain);
                     }
                     else {
                         int rain = (int)rightNeighbour.getRainfall(iSeason);
                         rain += velocity;                           
                         rightNeighbour.setRainfall(iSeason, (float)rain);
                     }
                 }
             }             
             
         }
         //End if terrain not null
    }
    
    
    /**
     * computeRainfall
     */
    private World computeRainfall(World newWorld)
    {
        //Get equatorial zone and polar zone
        float fHalf = (float)(iDimensions*iDimensions) / 2;
        float fTenth = (float)(iDimensions*iDimensions) / 10;
        
        float fEquatorTop = fHalf - fTenth;
        float fEquatorBottom = fHalf + fTenth;
        
        float fPolarNorth = fTenth;
        float fPolarSouth = (float)(iDimensions*iDimensions) - fTenth; 
        
        //Get desert zone 1 & 2
        float fDesertZone1 = fEquatorTop - fTenth;
        float fDesertZone2 = fEquatorBottom + fTenth;        
        
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap(); 
        
        Set<Integer> terrainSet = terrainMap.keySet();
        
        //Go through the terrain
        for (int iSeason = 0; iSeason < iNumSeasons; iSeason++) {        
            for(Integer iTemp : terrainSet) {
                Terrain terrain = terrainMap.get(iTemp);
                int iRain = (int)terrain.getPressure(iSeason);
                iRain = iRain*(-1);  //reverse sign                
                
                terrain.setRainfall(iSeason, (float)iRain);
            }
        }       
        
        //Go through the terrain
        for (int iSeason = 0; iSeason < iNumSeasons; iSeason++) {        
            for(Integer iTemp : terrainSet) {
                Terrain terrain = terrainMap.get(iTemp);              
                calculateRainfallWithWind(terrainMap, terrain, iSeason);
                if( (terrain.getSubtypeName().equals("sea")) || (terrain.getSubtypeName().equals("lake")) || (terrain.getSubtypeName().equals("light"))
                        || (terrain.getSubtypeName().equals("brightlight")) || (terrain.getSubtypeName().equals("darkness")) || (terrain.getSubtypeName().equals("deepdarkness")) )  {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain += 100;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }
                //Just to re-adjust mountains for river making
                if(terrain.getSubtypeName().equals("mountain") || terrain.getSubtypeName().equals("earthmountain") || terrain.getSubtypeName().equals("airplains")) { 
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain += 10;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }                
                int landSum = countland(terrain);
                if(landSum <= 55) {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain += 25;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }               
                if(landSum <= 25) {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain += 50;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }
                int id = terrain.getID();
                if(id >= fEquatorTop && id <= fEquatorBottom) {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain += 25;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }
                if(id < fEquatorTop && id >= fDesertZone1) {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain -= 20;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }
                if(id > fEquatorBottom && id <= fDesertZone2) {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain -= 20;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }                
                if(id <= fPolarNorth || id >= fPolarSouth) {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain -= 20;
                    terrain.setRainfall(iSeason, (float)iRain);                    
                }
                if(iSeason == 1 || iSeason == 3) { //summer or winter
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain -= 20;
                    terrain.setRainfall(iSeason, (float)iRain);                     
                }
                else {
                    int iRain = (int)terrain.getRainfall(iSeason);
                    iRain += 20;
                    terrain.setRainfall(iSeason, (float)iRain);                       
                }
            }
        }        
        
        
        return newWorld;        
    }
    
    
    /**
     * validDirection determines if a randomly generated direction from the
     * initial starting point is valid. 
     */
    private Terrain validDirection(int randomNumber, HashMap<Integer,Terrain> terrainMap, HashSet<Terrain> riverSet, Terrain terrain)
    {     
        if(terrain != null) {
            int id = terrain.getID();         
            int iHeight = (int)terrain.getLocation().getZ();
            
            Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);            
            Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
            Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);            
            Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);            
            Terrain rightNeighbour = getRightNeighbour(terrainMap, id);            
            Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
            Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
            Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);                
            
            //Tests each direction by random number
            //1 = SW
            if((randomNumber == 0) && (leftbelowNeighbour != null && unionPossible(terrain,leftbelowNeighbour))
                && (iHeight > (int)leftbelowNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(1))) 
                && (!(leftbelowNeighbour.getSubtypeName().equals("sea"))) && ( !(leftbelowNeighbour.getSubtypeName().equals("lake")))
                && (!( leftbelowNeighbour.getSubtypeName().equals("ice"))) && (!( leftbelowNeighbour.getSubtypeName().equals("light")))
                && (!( leftbelowNeighbour.getSubtypeName().equals("brightlight"))) && (!( leftbelowNeighbour.getSubtypeName().equals("darkness")))
                && (!( leftbelowNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(leftbelowNeighbour.getRiverName().length() > 0))) {
                    return leftbelowNeighbour;
            }
            //2 = S
            else if((randomNumber == 1) && (belowNeighbour != null && unionPossible(terrain,belowNeighbour))
                && (iHeight > (int)belowNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(2)))  
                && (!(belowNeighbour.getSubtypeName().equals("sea"))) && ( !(belowNeighbour.getSubtypeName().equals("lake")))
                && (!( belowNeighbour.getSubtypeName().equals("ice"))) && (!( belowNeighbour.getSubtypeName().equals("light")))
                && (!( belowNeighbour.getSubtypeName().equals("brightlight"))) && (!( belowNeighbour.getSubtypeName().equals("darkness")))
                && (!( belowNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(belowNeighbour.getRiverName().length() > 0))) {                            
                     return belowNeighbour; 
            }
            //3 = SE
            else if((randomNumber == 2) && (rightbelowNeighbour != null && unionPossible(terrain,rightbelowNeighbour)) 
                && (iHeight > (int)rightbelowNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(3))) 
                && (!(rightbelowNeighbour.getSubtypeName().equals("sea"))) && ( !(rightbelowNeighbour.getSubtypeName().equals("lake")))
                && (!( rightbelowNeighbour.getSubtypeName().equals("ice"))) && (!( rightbelowNeighbour.getSubtypeName().equals("light")))
                && (!( rightbelowNeighbour.getSubtypeName().equals("brightlight"))) && (!( rightbelowNeighbour.getSubtypeName().equals("darkness")))
                && (!( rightbelowNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(rightbelowNeighbour.getRiverName().length() > 0))) {                            
                     return rightbelowNeighbour;  
            }
            //4 = W
            else if((randomNumber == 3) && (leftNeighbour != null && unionPossible(terrain,leftNeighbour)) 
                &&(iHeight > (int)leftNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(4))) 
                && (!(leftNeighbour.getSubtypeName().equals("sea"))) && ( !(leftNeighbour.getSubtypeName().equals("lake")))
                && (!( leftNeighbour.getSubtypeName().equals("ice"))) && (!( leftNeighbour.getSubtypeName().equals("light")))
                && (!( leftNeighbour.getSubtypeName().equals("brightlight"))) && (!( leftNeighbour.getSubtypeName().equals("darkness")))
                && (!( leftNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(leftNeighbour.getRiverName().length() > 0))) {                             
                     return leftNeighbour;  
            }
            //6 = E
            else if((randomNumber == 4) && (rightNeighbour != null && unionPossible(terrain,rightNeighbour))
                && (iHeight > (int)rightNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(6)))  
                && (!(rightNeighbour.getSubtypeName().equals("sea"))) && ( !(rightNeighbour.getSubtypeName().equals("lake")))
                && (!( rightNeighbour.getSubtypeName().equals("ice"))) && (!( rightNeighbour.getSubtypeName().equals("light")))
                && (!( rightNeighbour.getSubtypeName().equals("brightlight"))) && (!( rightNeighbour.getSubtypeName().equals("darkness")))
                && (!( rightNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(rightNeighbour.getRiverName().length() > 0))) {                            
                     return rightNeighbour; 
            } 
            //7 = NW
            else if((randomNumber == 5) && (leftaboveNeighbour != null && unionPossible(terrain,leftaboveNeighbour)) 
                && (iHeight > (int)leftaboveNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(7)))  
                && (!(leftaboveNeighbour.getSubtypeName().equals("sea"))) && ( !(leftaboveNeighbour.getSubtypeName().equals("lake")))
                && (!( leftaboveNeighbour.getSubtypeName().equals("ice"))) && (!( leftaboveNeighbour.getSubtypeName().equals("light")))
                && (!( leftaboveNeighbour.getSubtypeName().equals("brightlight"))) && (!( leftaboveNeighbour.getSubtypeName().equals("darkness")))
                && (!( leftaboveNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(leftaboveNeighbour.getRiverName().length() > 0))) {                          
                     return leftaboveNeighbour;  
            } 
            //8 = N
            else if((randomNumber == 6) && (aboveNeighbour != null && unionPossible(terrain,aboveNeighbour)) 
                && (iHeight > (int)aboveNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(8)))  
                && (!(aboveNeighbour.getSubtypeName().equals("sea"))) && ( !(aboveNeighbour.getSubtypeName().equals("lake")))
                && (!( aboveNeighbour.getSubtypeName().equals("ice"))) && (!( aboveNeighbour.getSubtypeName().equals("light")))
                && (!( aboveNeighbour.getSubtypeName().equals("brightlight"))) && (!( aboveNeighbour.getSubtypeName().equals("darkness")))
                && (!( aboveNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(aboveNeighbour.getRiverName().length() > 0))) {                              
                     return aboveNeighbour;  
            } 
            //9 = NE
            else if((randomNumber == 7) && (rightaboveNeighbour != null && unionPossible(terrain,rightaboveNeighbour)) 
                && (iHeight > (int)rightaboveNeighbour.getLocation().getZ()-1)
                && (! (terrain.getRiverEntrance(9)))  
                && (!(rightaboveNeighbour.getSubtypeName().equals("sea"))) && ( !(rightaboveNeighbour.getSubtypeName().equals("lake")))
                && (!( rightaboveNeighbour.getSubtypeName().equals("ice"))) && (!( rightaboveNeighbour.getSubtypeName().equals("light")))
                && (!( rightaboveNeighbour.getSubtypeName().equals("brightlight"))) && (!( rightaboveNeighbour.getSubtypeName().equals("darkness")))
                && (!( rightaboveNeighbour.getSubtypeName().equals("deepdarkness"))) && (!(rightaboveNeighbour.getRiverName().length() > 0))) {                              
                     return rightaboveNeighbour;
            }         
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    
    /**
     * hasValidExit determines if a square on the grid has a valid exit and
     * returns true when it does.
     */
    private Terrain hasValidExit(HashMap<Integer,Terrain> terrainMap, HashSet<Terrain> riverSet, Terrain terrain)
    {
        //Test NE = 9 = random 7
        Terrain neighbour = validDirection(7,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;   
        //Test E = 6 = random 4
        neighbour = validDirection(4,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;                  
        //Test NW = 7 = random 5
        neighbour = validDirection(5,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;         
        //Test N = 8 = random 6          
        neighbour = validDirection(6,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;         
        //Test SE = 3 = random 2             
        neighbour = validDirection(2,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;    
        //Test SW = 1 = random 0              
        neighbour = validDirection(0,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;  
        //Test W = 4 = random 3        
        neighbour = validDirection(3,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;       
        //Test S = 2 = random 1             
        neighbour = validDirection(1,terrainMap,riverSet,terrain);
        if(neighbour != null) return neighbour;            
        
        return null;
    }    

    
    /**
     * unionPossible checks if it is possible to join the roots of two points
     * with the disjointedSet.  If it is possible, the method returns true,
     * otherwise false.
     * 
     * @param point    : the first point
     * @param newPoint : the second point
     * @return         : true if the union was possible, otherwise false
     */
     private boolean unionPossible(Terrain terrain, Terrain otherTerrain) 
     {
        Integer here = terrain.getID();    
        int next = otherTerrain.getID();     
        int hereRoot = 0;
        int nextRoot = 0;
        
        //find the root of here       
        try {
            hereRoot = disjointSet.find(here);
        }
        catch (IllegalArgumentException e) {    
        }
               
        //find the root of next        
        try {
            nextRoot = disjointSet.find(next);
        }
        catch (IllegalArgumentException e) {    
        }                        
        
        //If they do not have the same root, return true
        if(hereRoot != nextRoot) {
            return true;
        }
        
        return false;
    }
     
    
    /**
     * calculateUnion determines the roots involved and performs a union between
     * two cell values (representing two points in the maze).
     * On a success it returns a Pair object with the information necessary to later
     * pass on to observers (a point, and a direction).  On a failure (which shouldn't
     * happen) it returns null.
     * 
     * @param point     : the point to exit from
     * @param newPoint  : the new point the exit leads to
     * @return          : an object of the Pair class, with Integer and Point.Direction values,
     *                    or null on failure
     */
    private void calculateUnion(Terrain terrain, Terrain otherTerrain)
    {           
        //Variables
        Integer here = terrain.getID();    
        int next = otherTerrain.getID();   
        int hereRoot = 0;
        int nextRoot = 0;    
          
        //find the root of here
        try {
            hereRoot = disjointSet.find(here);
        }
        catch (IllegalArgumentException e) {    
        }
             
        //find the root of next
        try {
            nextRoot = disjointSet.find(next);
        }
        catch (IllegalArgumentException e) {    
        }                        
            
        //attempt union
        try {
            disjointSet.union(nextRoot, hereRoot);                                        
        }
        catch (IllegalArgumentException e) {  
            System.out.println("Union error: "+here+" "+next);
        }        
    }
    
    
    /**
     * calculatePath
     */
    private void calculatePath(HashMap<Integer,Terrain> terrainMap, Terrain terrain, String riverName)
    {               
        HashSet<Terrain> riverSet = new HashSet<Terrain>();       
        riverSet.add(terrain);
        int factor = 1;
        
        if( (terrain.getSubtypeName().equals("lake")) || (terrain.getSubtypeName().equals("sea")) ||
              (terrain.getSubtypeName().equals("ice")) || (terrain.getSubtypeName().equals("light")) ||
              (terrain.getSubtypeName().equals("brightlight")) || (terrain.getSubtypeName().equals("darkness")) ||
              (terrain.getSubtypeName().equals("deepdarkness")) || (terrain.getRiverName().length() > 0) || (terrain == null) )            
        {
            return;
        }
            
        if(riverName == null || riverName == "") {
            Random random = new Random();
            int randomNumber = random.nextInt(10);
        
            StringBuffer name = new StringBuffer(100);
        
            name.append("The ");
 
            switch(randomNumber) {
                case 0:
                    name.append("windy ");
                break;

                case 1:
                    name.append("grey ");
                    break;
            
                case 2:
                    name.append("gloomy ");
                    break;
             
                case 3:
                    name.append("ringing ");
                    break;            

                case 4:
                    name.append("whistling ");
                    break;   
            
                case 5:
                    name.append("rolling ");
                    break;   
            
                case 6:
                    name.append("endless ");
                    break;   
            
                case 7:
                    name.append("cursed ");
                    break;               

                case 8:
                    name.append("wild ");
                    break;              
            
                case 9:
                    name.append("eternal ");
                    break;              

                case 10:
                    name.append("wandering ");
                    break;                 
                
                default:
                    break;
            }       
               
            randomNumber = random.nextInt(10);
        
            switch(randomNumber) {
                case 0: case 1: case 3:
                    name.append("waters");
                    break;
            
                case 4: case 5:
                    name.append("rapids");
                    break;                    

                case 6: case 7:
                    name.append("torrent");
                        break;                                         
                    
                default:
                    name.append("river");                
                    break;
            }
            riverName = name.toString();
        }        
        
        while( (!(terrain.getRiverName().length() > 0)) && (!(terrain.getSubtypeName().equals("lake"))) && (!(terrain.getSubtypeName().equals("sea")))
            && (!(terrain.getSubtypeName().equals("ice"))) && (!(terrain.getSubtypeName().equals("light"))) && (!(terrain.getSubtypeName().equals("brightlight")))
            && (!(terrain.getSubtypeName().equals("darkness"))) && (!(terrain.getSubtypeName().equals("deepdarkness"))) 
            && (!(terrain.getPicture().equals("water"))) && (!(terrain.getPicture().equals("ocean"))) && (!(terrain.getPicture().equals("light")))
            && (!(terrain.getPicture().equals("brightlight"))) && (!(terrain.getPicture().equals("darkness"))) && (!(terrain.getPicture().equals("deepdarkness")))
            && (!(terrain.getPicture().equals("ice"))) && (!(terrain.getPicture().equals(terrain.getSubtypeName()+"end"))) )
        {
            int iRain = (int)terrain.getRainfall(0) + 20;
            terrain.setRainfall(0, (float)iRain);
            iRain = (int)terrain.getRainfall(1) + 20;
            terrain.setRainfall(1, (float)iRain); 
            iRain = (int)terrain.getRainfall(2) + 20;
            terrain.setRainfall(2, (float)iRain);
            iRain = (int)terrain.getRainfall(3) + 20;
            terrain.setRainfall(3, (float)iRain);
            
            for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                calculateRainfallWithWind(terrainMap, terrain, iSeason);
            }
                  
            Random random = new Random();
            int randomNumber = random.nextInt(8); 
            Terrain neighbour = validDirection( randomNumber, terrainMap, riverSet, terrain );          
            
            //if(neighbour == null) {
            //    neighbour = hasValidExit( terrainMap, riverSet, terrain );
            //}
            
            //Find a random direction that is valid (if one exists)
            while(neighbour == null && hasValidExit( terrainMap, riverSet, terrain ) != null) {
                randomNumber = random.nextInt(8); 
                neighbour = validDirection( randomNumber, terrainMap, riverSet, terrain );                   
            }            
            
            if(neighbour != null) {
                if(randomNumber == 0) { //1 = SW = leftbelow
                    terrain.setRiverExit(1); 
                    calculateUnion(terrain,neighbour);
                    neighbour.setRiverEntrance(9);                         
                    riverSet.add(neighbour);
                    terrain = neighbour;                    
                }                 
                else if(randomNumber == 1) { //2 = S = below
                    terrain.setRiverExit(2);
                    calculateUnion(terrain,neighbour);                    
                    neighbour.setRiverEntrance(8);                        
                    riverSet.add(neighbour);                
                    terrain = neighbour;                    
                }
                else if(randomNumber == 2) { //3 = SE = rightBelow
                    terrain.setRiverExit(3);  
                    calculateUnion(terrain,neighbour);                    
                    neighbour.setRiverEntrance(7);                         
                    riverSet.add(neighbour);                     
                    terrain = neighbour;                    
                }
                else if(randomNumber == 3) { //4 = W = left
                    terrain.setRiverExit(4);      
                    calculateUnion(terrain,neighbour);                    
                    neighbour.setRiverEntrance(6);                        
                    riverSet.add(neighbour);                    
                    terrain = neighbour;                    
                }
                else if(randomNumber == 4) { //6 = E = right
                    terrain.setRiverExit(6); 
                    calculateUnion(terrain,neighbour);                    
                    neighbour.setRiverEntrance(4);                        
                    riverSet.add(neighbour);                    
                    terrain = neighbour;                   
                }
                else if(randomNumber == 5) { //7 = NW = leftAbove
                    terrain.setRiverExit(7); 
                    calculateUnion(terrain,neighbour);                    
                    neighbour.setRiverEntrance(3);                        
                    riverSet.add(neighbour);
                    terrain = neighbour;                    
                }
                else if(randomNumber == 6) { //8 = N = above
                    terrain.setRiverExit(8);
                    calculateUnion(terrain,neighbour);                     
                    neighbour.setRiverEntrance(2);                        
                    riverSet.add(neighbour);  
                    terrain = neighbour;                    
                }
                else if(randomNumber == 7) { //9 = NE = rightAbove
                    terrain.setRiverExit(9);  
                    calculateUnion(terrain,neighbour);                    
                    neighbour.setRiverEntrance(1);                        
                    riverSet.add(neighbour);                       
                    terrain = neighbour;                    
                }
                else {
                   terrain.setPicture(terrain.getSubtypeName()+"end");
                   //terrain.setSubtypeName("lake");
                   //in either case, add rain around it
                   for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                       adjustRainfall(terrainMap, neighbour, factor);
                   }                    
                   break;
                }
            }
            else {
               terrain.setPicture(terrain.getSubtypeName()+"end");
               //terrain.setSubtypeName("lake");
               //in either case, add rain around it
               for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                    adjustRainfall(terrainMap, neighbour, factor);
               }                
               break;
            }                
            
            //in either case, add rain around it
            for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                adjustRainfall(terrainMap, neighbour, factor);
            }             
        }
        //End while      
        
        terrain.setRiverExit(5);
        
        for(Terrain temp : riverSet) {
            if(temp.getRiverName().equals("")) {
                temp.setRiverName(riverName);
            }
        }
    }
    
    
    /**
     * calculatePath
     *
    private void calculatePath(HashMap<Integer,Terrain> terrainMap, Terrain terrain, String riverName)
    {               
        HashSet<Terrain> riverSet = new HashSet<Terrain>();       
        riverSet.add(terrain);
        int factor = 1;
        
        if( (terrain.getSubtypeName().equals("lake")) || (terrain.getSubtypeName().equals("sea")) ||
              (terrain.getSubtypeName().equals("ice")) || (terrain.getRiverName().length() > 0) || (terrain == null) )
        {
            return;
        }
            
        if(riverName == null || riverName == "") {
            Random random = new Random();
            int randomNumber = random.nextInt(10);
        
            StringBuffer name = new StringBuffer(100);
        
            name.append("The ");
 
            switch(randomNumber) {
                case 0:
                    name.append("windy ");
                break;

                case 1:
                    name.append("grey ");
                    break;
            
                case 2:
                    name.append("gloomy ");
                    break;
             
                case 3:
                    name.append("ringing ");
                    break;            

                case 4:
                    name.append("whistling ");
                    break;   
            
                case 5:
                    name.append("rolling ");
                    break;   
            
                case 6:
                    name.append("endless ");
                    break;   
            
                case 7:
                    name.append("cursed ");
                    break;               

                case 8:
                    name.append("wild ");
                    break;              
            
                case 9:
                    name.append("eternal ");
                    break;              

                case 10:
                    name.append("wandering ");
                    break;                 
                
                default:
                    break;
            }       
               
            randomNumber = random.nextInt(10);
        
            switch(randomNumber) {
                case 0: case 1: case 3:
                    name.append("waters");
                    break;
            
                case 4: case 5:
                    name.append("rapids");
                    break;                    

                case 6: case 7:
                    name.append("torrent");
                        break;                                         
                    
                default:
                    name.append("river");                
                    break;
            }
            riverName = name.toString();
        }        
        
        while( (!(terrain.getRiverName().length() > 0)) && (!(terrain.getSubtypeName().equals("lake"))) && (!(terrain.getSubtypeName().equals("sea")))
            && (!(terrain.getSubtypeName().equals("ice"))) && (!(terrain.getPicture().equals("water"))) && (!(terrain.getPicture().equals("ocean")))
            && (!(terrain.getPicture().equals("ice"))) && (!(terrain.getPicture().equals(terrain.getSubtypeName()+"end"))) )
        {
            int iRain = (int)terrain.getRainfall(0) + 20;
            terrain.setRainfall(0, (float)iRain);
            iRain = (int)terrain.getRainfall(1) + 20;
            terrain.setRainfall(1, (float)iRain); 
            iRain = (int)terrain.getRainfall(2) + 20;
            terrain.setRainfall(2, (float)iRain);
            iRain = (int)terrain.getRainfall(3) + 20;
            terrain.setRainfall(3, (float)iRain);
            
            for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                calculateRainfallWithWind(terrainMap, terrain, iSeason);
            }
                  
            Random random = new Random();
            int randomNumber = random.nextInt(8); 
            Terrain neighbour = validDirection( randomNumber, terrainMap, riverSet, terrain );          
            
            if(neighbour == null) {
                neighbour = hasValidExit( terrainMap, riverSet, terrain );
            }
            
            //Find a random direction that is valid (if one exists)
            //while(neighbour == null && hasValidExit( terrainMap, riverSet, terrain ) != null) {
            //    randomNumber = random.nextInt(8); 
            //    neighbour = validDirection( randomNumber, terrainMap, riverSet, terrain );                   
            //}            
            
            if(neighbour != null) {
                if(randomNumber == 0) { //1 = SW = leftbelow
                    terrain.setRiverExit(1);                        
                    terrain = neighbour;
                    terrain.setRiverEntrance(9);                         
                    riverSet.add(terrain);                   
                }                 
                else if(randomNumber == 1) { //2 = S = below
                    terrain.setRiverExit(2);                         
                    terrain = neighbour;
                    terrain.setRiverEntrance(8);                        
                    riverSet.add(terrain);                
                }
                else if(randomNumber == 2) { //3 = SE = rightBelow
                    terrain.setRiverExit(3);                        
                    terrain = neighbour;
                    terrain.setRiverEntrance(7);                         
                    riverSet.add(terrain);                     
                }
                else if(randomNumber == 3) { //4 = W = left
                    terrain.setRiverExit(4);                         
                    terrain = neighbour;
                    terrain.setRiverEntrance(6);                        
                    riverSet.add(terrain);                    
                }
                else if(randomNumber == 4) { //6 = E = right
                    terrain.setRiverExit(6);                         
                    terrain = neighbour;
                    terrain.setRiverEntrance(4);                        
                    riverSet.add(terrain);                    
                }
                else if(randomNumber == 5) { //7 = NW = leftAbove
                    terrain.setRiverExit(7);                         
                    terrain = neighbour;
                    terrain.setRiverEntrance(3);                        
                    riverSet.add(terrain);                     
                }
                else if(randomNumber == 6) { //8 = N = above
                    terrain.setRiverExit(8);                         
                    terrain = neighbour;
                    terrain.setRiverEntrance(2);                        
                    riverSet.add(terrain);                     
                }
                else if(randomNumber == 7) { //9 = NE = rightAbove
                    terrain.setRiverExit(9);                         
                    terrain = neighbour;
                    terrain.setRiverEntrance(1);                        
                    riverSet.add(terrain);                       
                }
                else {
                   terrain.setPicture(terrain.getSubtypeName()+"end");
                   //terrain.setSubtypeName("lake");
                   //in either case, add rain around it
                   for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                       adjustRainfall(terrainMap, neighbour, factor);
                   }                    
                   break;
                }
            }
            else {
               terrain.setPicture(terrain.getSubtypeName()+"end");
               //terrain.setSubtypeName("lake");
               //in either case, add rain around it
               for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                    adjustRainfall(terrainMap, neighbour, factor);
               }                
               break;
            }                
            
            //in either case, add rain around it
            for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
                adjustRainfall(terrainMap, neighbour, factor);
            }             
        }
        //End while      
        
        terrain.setRiverExit(5);
        
        for(Terrain temp : riverSet) {
            if(temp.getRiverName().equals("")) {
                temp.setRiverName(riverName);
            }
        }
    }*/
    
    
    /**
     * 
     */
    private void adjustRainfall(HashMap<Integer,Terrain> terrainMap, Terrain terrain, int factor)
    {
        if(terrain != null) {
            int id = terrain.getID();
            
            for(int iSeason = 0; iSeason < iNumSeasons; iSeason++) {
            
                int iRain = (int)terrain.getRainfall(iSeason) + factor*2;
                terrain.setRainfall(iSeason, (float)iRain);
            
                Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
                if(leftbelowNeighbour != null) {
                    iRain = (int)leftbelowNeighbour.getRainfall(iSeason) + factor;
                    leftbelowNeighbour.setRainfall(iSeason, (float)iRain);            
                }             
                Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
                if(leftNeighbour != null) {
                    iRain = (int)leftNeighbour.getRainfall(iSeason) + factor;
                    leftNeighbour.setRainfall(iSeason, (float)iRain);  
                }             
                Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
                if(leftaboveNeighbour != null) {
                    iRain = (int)leftaboveNeighbour.getRainfall(iSeason) + factor;
                    leftaboveNeighbour.setRainfall(iSeason, (float)iRain);  
                }            
                Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
                if(aboveNeighbour != null) {
                    iRain = (int)aboveNeighbour.getRainfall(iSeason) + factor;
                    aboveNeighbour.setRainfall(iSeason, (float)iRain);
                }            
                Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
                if(rightaboveNeighbour != null) {
                    iRain = (int)rightaboveNeighbour.getRainfall(iSeason) + factor;
                    rightaboveNeighbour.setRainfall(iSeason, (float)iRain);
                }            
                Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
                if(rightNeighbour != null) {
                    iRain = (int)rightNeighbour.getRainfall(iSeason) + factor;
                    rightNeighbour.setRainfall(iSeason, (float)iRain);
                }
                Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
                if(rightbelowNeighbour != null) {
                    iRain = (int)rightbelowNeighbour.getRainfall(iSeason) + factor;
                    rightbelowNeighbour.setRainfall(iSeason, (float)iRain);
                }
                Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
                if(belowNeighbour != null) {
                    iRain = (int)belowNeighbour.getRainfall(iSeason) + factor;
                    belowNeighbour.setRainfall(iSeason, (float)iRain);
                }
            }
        }
    }
    
    
    /**
     * computeRivers
     */
    private World computeRivers(World newWorld)
    {
        
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap(); 
        
        Set<Integer> terrainSet = terrainMap.keySet();
        
        //Go through the terrain the first time      
        for(Integer iTemp : terrainSet) {
            Terrain terrain = terrainMap.get(iTemp);
            int id = terrain.getID();
            int avg = (int)(terrain.getRainfall(0)+terrain.getRainfall(1)+terrain.getRainfall(2)+terrain.getRainfall(3))/4;
            boolean valid = true;
            
            Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);            
            Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
            Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);            
            Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);            
            Terrain rightNeighbour = getRightNeighbour(terrainMap, id);            
            Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
            Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
            Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);             
            
            if(leftbelowNeighbour != null) {
                if(leftbelowNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }
            if(belowNeighbour != null && valid == true) {
                if(belowNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }            
            if(rightbelowNeighbour != null && valid == true) {
                if(rightbelowNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }  
            if(leftNeighbour != null && valid == true) {
                if(leftNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }  
            if(rightNeighbour != null && valid == true) {
                if(rightNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }  
            if(leftaboveNeighbour != null && valid == true) {
                if(leftaboveNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }  
            if(aboveNeighbour != null && valid == true) {
                if(aboveNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }              
            if(rightaboveNeighbour != null && valid == true) {
                if(rightaboveNeighbour.getRiverName().length() > 0) {
                    valid = false;
                }
            }
            
            //If NOT sea, lake, ice, or already with a river
            if(!(terrain.getSubtypeName().equals("sea") || terrain.getSubtypeName().equals("lake") || terrain.getSubtypeName().equals("ice")
                 || terrain.getRiverName().length() > 0) && (valid == true)) {
                if(terrain.getSubtypeName().equals("mountain") || terrain.getSubtypeName().equals("snowmountain") || terrain.getSubtypeName().equals("airplains")
                  || terrain.getSubtypeName().equals("earthmountain")) {
                    if(avg > 5) {
                        if(terrain.getPicture().equals("mountain") || terrain.getPicture().equals("snowmountain") || terrain.getPicture().equals("airplains")
                          || terrain.getPicture().equals("earthmountain")) {
                            terrain.setRiverEntrance(5);
                            calculatePath(terrainMap, terrain, "");
                        }
                    }
                }
                else if(terrain.getSubtypeName().equals("hill") || terrain.getSubtypeName().equals("snowhill") || terrain.getSubtypeName().equals("wasteland") ||
                  terrain.getSubtypeName().equals("glowplains")) {                   
                    if(avg >= 75) {
                        if(terrain.getPicture().equals("hill") || terrain.getPicture().equals("snowhill") || terrain.getPicture().equals("wasteland")
                          || terrain.getPicture().equals("glowplains")) {
                            terrain.setRiverEntrance(5);                            
                            calculatePath(terrainMap, terrain, "");
                        }
                    }
                }
            /*
            else if(terrain.getSubtypeName().equals("plains") || terrain.getSubtypeName().equals("tundra") || terrain.getSubtypeName().equals("forest")
                                                              || terrain.getPicture().equals("snowforest") ) {                   
                if(avg >= 400) {
                    if(terrain.getPicture().equals("plains") || terrain.getPicture().equals("tundra") || terrain.getPicture().equals("forest")
                                                             || terrain.getPicture().equals("snowhill")) {                    
                        calculatePath(terrainMap, terrain, "");
                    }
                }
            }*/
            }
        }        
        
        
        //Go through the terrain the second time for larger freshwater lakes     
        for(Integer iTemp : terrainSet) {
            Terrain terrain = terrainMap.get(iTemp);                        
            int avg = (int)((terrain.getRainfall(0)+terrain.getRainfall(1)+terrain.getRainfall(2)+terrain.getRainfall(3))/4);
            //If NOT sea, lake, or ice
            if(! (terrain.getSubtypeName().equals("sea") || terrain.getSubtypeName().equals("lake") || terrain.getSubtypeName().equals("ice")
              || terrain.getSubtypeName().equals("light") || terrain.getSubtypeName().equals("brightlight") || terrain.getSubtypeName().equals("darkness")
              || terrain.getSubtypeName().equals("deepdarkness") ) ) {
                if(terrain.getSubtypeName().equals("plains") || terrain.getSubtypeName().equals("tundra") || terrain.getSubtypeName().equals("forest")
                   || terrain.getSubtypeName().equals("snowforest") || terrain.getSubtypeName().equals("verdantforest") || terrain.getSubtypeName().equals("glowforest")
                   || terrain.getSubtypeName().equals("toxic") ) {                   
                    if(avg > 500) {
                        terrain.setPicture(terrain.getSubtypeName()+"end");                       
                    }
                }
                if(terrain.getSubtypeName().equals("hill") || terrain.getSubtypeName().equals("snowhill") || terrain.getSubtypeName().equals("wasteland")
                  || terrain.getSubtypeName().equals("glowplains")) {                   
                    if(avg > 500) {
                        terrain.setPicture(terrain.getSubtypeName()+"end");                         
                    }
                }
                if(terrain.getSubtypeName().equals("mountain") || terrain.getSubtypeName().equals("airplains") || terrain.getSubtypeName().equals("earthmountain")) { //but not snowmountain                  
                    if(avg > 500) {
                        terrain.setPicture(terrain.getSubtypeName()+"end");                        
                    }
                }
            }
        }
        
        
        //Go through one more time for rainfall surrounding water (non-river)
        for(Integer iTemp : terrainSet) {
            Terrain terrain = terrainMap.get(iTemp);
            int id = terrain.getID();
            String subtype = terrain.getSubtypeName();
            int factor = 5;
            
            if(subtype.equals("lake") || terrain.getPicture().equals("water") || terrain.getPicture().equals(subtype+"end") || subtype.equals("waterplains") ||
              terrain.getPicture().equals("light") ||  terrain.getPicture().equals("darkness") ) {
            
                adjustRainfall(terrainMap, terrain, factor);  
            
                Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
                if(leftbelowNeighbour != null) {
                    adjustRainfall(terrainMap, leftbelowNeighbour, factor);            
                }             
                Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
                if(leftNeighbour != null) {
                    adjustRainfall(terrainMap, leftNeighbour, factor); 
                }             
                Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
                if(leftaboveNeighbour != null) {
                    adjustRainfall(terrainMap, leftaboveNeighbour, factor);   
                }            
                Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
                if(aboveNeighbour != null) {
                    adjustRainfall(terrainMap, aboveNeighbour, factor);  
                }             
                Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
                if(rightaboveNeighbour != null) {
                    adjustRainfall(terrainMap, rightaboveNeighbour, factor); 
                }            
                Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
                if(rightNeighbour != null) {
                    adjustRainfall(terrainMap, rightNeighbour, factor); 
                }
                Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
                if(rightbelowNeighbour != null) {
                    adjustRainfall(terrainMap, rightbelowNeighbour, factor); 
                }
                Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
                if(belowNeighbour != null) {
                    adjustRainfall(terrainMap, belowNeighbour, factor); 
                }          
            }
            //End if subtype
        }
        //End for terrain        
        
        return newWorld;    
    }

    
    /**
     * choosePicture
     */
    private void choosePicture(Terrain terrain)
    {
        if(terrain != null) {
            String subtype = terrain.getSubtypeName();
            String picture = terrain.getPicture();
            //int last = picture.length()-1;
            //int secondLast = last-1;
            
            if(terrain.getPicture().equals(subtype+"end")) {
                return;
            }
            
            //if(subtype.equals("forest")) {
                //N entrance
            if(terrain.getRiverEntrance(5) || terrain.getRiverExit(5)) {
                terrain.setPicture(subtype+"end");                
            }
            else if(terrain.getRiverEntrance(8)) {
                //south exit
                if(terrain.getRiverExit(2) ) {
                    terrain.setPicture("N"+subtype+"S");
                }
                //west exit
                else if(terrain.getRiverExit(4) ) {
                    terrain.setPicture("N"+subtype+"W");
                }
                //east exit
                else if(terrain.getRiverExit(6) ) {
                    terrain.setPicture("N"+subtype+"E");
                }
                //northwest exit
                else if(terrain.getRiverExit(7) ) {
                    terrain.setPicture("N"+subtype+"NW");
                }
                //northeast exit
                else if(terrain.getRiverExit(9) ) {
                    terrain.setPicture("N"+subtype+"NE");
                }
                //southwest exit
                else if(terrain.getRiverExit(1) ) {
                    terrain.setPicture("N"+subtype+"SW");
                }
                //southeast exit
                else if(terrain.getRiverExit(3) ) {
                    terrain.setPicture("N"+subtype+"SE");
                } 
                //N exit
                else if(terrain.getRiverExit(8) ) {
                    terrain.setPicture("N"+subtype+"NW");
                }                 
                else {
                    terrain.setPicture("forest");                    
                }                    
            } 
            //S entrance                 
            else if(terrain.getRiverEntrance(2)) {
                    //south exit
                    if(terrain.getRiverExit(8) ) {
                        terrain.setPicture("N"+subtype+"S");
                    }
                    //west exit
                    else if(terrain.getRiverExit(4) ) {
                        terrain.setPicture("S"+subtype+"W");
                    }
                    //east exit
                    else if(terrain.getRiverExit(6) ) {
                        terrain.setPicture("S"+subtype+"E");
                    }
                    //northwest exit
                    else if(terrain.getRiverExit(7) ) {
                        terrain.setPicture("S"+subtype+"NW");
                    }
                    //northeast exit
                    else if(terrain.getRiverExit(9) ) {
                        terrain.setPicture("S"+subtype+"NE");
                    }
                    //southwest exit
                    else if(terrain.getRiverExit(1) ) {
                        terrain.setPicture("S"+subtype+"SW");
                    }
                    //southeast exit
                    else if(terrain.getRiverExit(3) ) {
                        terrain.setPicture("S"+subtype+"SE");
                    }
                    //S exit
                    else if(terrain.getRiverExit(2) ) {
                       terrain.setPicture("S"+subtype+"SE");
                    }                     
                    else {
                        terrain.setPicture(subtype);                    
                    }
                }
                //W entrance                
                 else if(terrain.getRiverEntrance(4)) {
                    //north exit
                    if(terrain.getRiverExit(8) ) {
                        terrain.setPicture("N"+subtype+"W");
                    }
                    //south exit
                    else if(terrain.getRiverExit(2) ) {
                        terrain.setPicture("S"+subtype+"W");
                    }
                    //east exit
                    else if(terrain.getRiverExit(6) ) {
                        terrain.setPicture("W"+subtype+"E");
                    }
                    //northwest exit
                    else if(terrain.getRiverExit(7) ) {
                        terrain.setPicture("W"+subtype+"NW");
                    }
                    //northeast exit
                    else if(terrain.getRiverExit(9) ) {
                        terrain.setPicture("W"+subtype+"NE");
                    }
                    //southwest exit
                    else if(terrain.getRiverExit(1) ) {
                        terrain.setPicture("W"+subtype+"SW");
                    }
                    //southeast exit
                    else if(terrain.getRiverExit(3) ) {
                        terrain.setPicture("W"+subtype+"SE");
                    }
                    //W exit
                    else if(terrain.getRiverExit(4) ) {
                       terrain.setPicture("W"+subtype+"SW");
                    }                    
                    else {
                        terrain.setPicture(subtype);                    
                    } 
                } 
                //E entrance                
                else if(terrain.getRiverEntrance(6)) {
                    //north exit
                    if(terrain.getRiverExit(8) ) {
                        terrain.setPicture("N"+subtype+"E");
                    }
                    //south exit
                    else if(terrain.getRiverExit(2) ) {
                        terrain.setPicture("S"+subtype+"E");
                    }
                    //west exit
                    else if(terrain.getRiverExit(4) ) {
                        terrain.setPicture("W"+subtype+"E");
                    }
                    //northwest exit
                    else if(terrain.getRiverExit(7) ) {
                        terrain.setPicture("E"+subtype+"NW");
                    }
                    //northeast exit
                    else if(terrain.getRiverExit(9) ) {
                        terrain.setPicture("E"+subtype+"NE");
                    }
                    //southwest exit
                    else if(terrain.getRiverExit(1) ) {
                        terrain.setPicture("E"+subtype+"SW");
                    }
                    //southeast exit
                    else if(terrain.getRiverExit(3) ) {
                        terrain.setPicture("E"+subtype+"SE");
                    } 
                    //E exit
                    else if(terrain.getRiverExit(6) ) {
                       terrain.setPicture("E"+subtype+"NE");
                    }                     
                    else {
                        terrain.setPicture(subtype);                    
                    }
                }                
                //NE entrance                
                else if(terrain.getRiverEntrance(9)) {
                    //north exit
                    if(terrain.getRiverExit(8) ) {
                        terrain.setPicture("N"+subtype+"NE");
                    }
                    //south exit
                    else if(terrain.getRiverExit(2) ) {
                        terrain.setPicture("S"+subtype+"NE");
                    }
                    //west exit
                    else if(terrain.getRiverExit(4) ) {
                        terrain.setPicture("W"+subtype+"NE");
                    }
                    //east exit
                    else if(terrain.getRiverExit(6) ) {
                        terrain.setPicture("E"+subtype+"NE");
                    }                    
                    //NW exit
                    else if(terrain.getRiverExit(7) ) {
                        terrain.setPicture("NE"+subtype+"NW");
                    }                     
                    //SW Exit
                    else if(terrain.getRiverExit(1) ) {
                        terrain.setPicture("NE"+subtype+"SW");
                    }                   
                    //SE exit
                    else if(terrain.getRiverExit(3) ) {
                        terrain.setPicture("NE"+subtype+"SE");
                    } 
                    //NE exit
                    else if(terrain.getRiverExit(9) ) {
                       terrain.setPicture("N"+subtype+"NE");
                    }                     
                    else {
                        terrain.setPicture(subtype);                    
                    }                    
                }                
                //SW entrance
                else if(terrain.getRiverEntrance(1)) {
                    //north exit
                    if(terrain.getRiverExit(8) ) {
                        terrain.setPicture("N"+subtype+"SW");
                    }
                    //south exit
                    else if(terrain.getRiverExit(2) ) {
                        terrain.setPicture("S"+subtype+"SW");
                    }
                    //west exit
                    else if(terrain.getRiverExit(4) ) {
                        terrain.setPicture("W"+subtype+"SW");
                    }
                    //east exit
                    else if(terrain.getRiverExit(6) ) {
                        terrain.setPicture("E"+subtype+"SW");
                    }                    
                    //NW exit
                    else if(terrain.getRiverExit(7) ) {
                        terrain.setPicture("NW"+subtype+"SW");
                    }                     
                    //NE Exit
                    else if(terrain.getRiverExit(9) ) {
                        terrain.setPicture("NE"+subtype+"SW");
                    }                   
                    //SE exit
                    else if(terrain.getRiverExit(1) ) {
                        terrain.setPicture("SE"+subtype+"SW");
                    }
                    //SW exit
                    else if(terrain.getRiverExit(1) ) {
                       terrain.setPicture("S"+subtype+"SW");
                    }                     
                    else {
                        terrain.setPicture(subtype);                    
                    } 
                }
                //NW entrance                
                else if(terrain.getRiverEntrance(7)) {
                    //north exit
                    if(terrain.getRiverExit(8) ) {
                        terrain.setPicture("N"+subtype+"NW");
                    }
                    //south exit
                    else if(terrain.getRiverExit(2) ) {
                        terrain.setPicture("S"+subtype+"NW");
                    }
                    //west exit
                    else if(terrain.getRiverExit(4) ) {
                        terrain.setPicture("W"+subtype+"NW");
                    }
                    //east exit
                    else if(terrain.getRiverExit(6) ) {
                        terrain.setPicture("E"+subtype+"NW");
                    }                    
                    //SW exit
                    else if(terrain.getRiverExit(1) ) {
                        terrain.setPicture("NW"+subtype+"SW");
                    }                     
                    //NE Exit
                    else if(terrain.getRiverExit(9) ) {
                        terrain.setPicture("NE"+subtype+"NW");
                    }                   
                    //SE exit
                    else if(terrain.getRiverExit(3) ) {
                        terrain.setPicture("NW"+subtype+"SE");
                    } 
                    //NW exit
                    else if(terrain.getRiverExit(7) ) {
                       terrain.setPicture("N"+subtype+"NW");
                    }                     
                    else {
                        terrain.setPicture(subtype);                    
                    } 
                } 
             //SE entrance                
             else if(terrain.getRiverEntrance(3)) {
                //north exit
                if(terrain.getRiverExit(8) ) {
                    terrain.setPicture("N"+subtype+"SE");
                }
                //south exit
                else if(terrain.getRiverExit(2) ) {
                    terrain.setPicture("S"+subtype+"SE");
                }
                //west exit
                else if(terrain.getRiverExit(4) ) {
                    terrain.setPicture("W"+subtype+"SE");
                }
                //east exit
                else if(terrain.getRiverExit(6) ) {
                    terrain.setPicture("E"+subtype+"SE");
                }                    
                //NW exit
                else if(terrain.getRiverExit(7) ) {
                    terrain.setPicture("NW"+subtype+"SE");
                }                     
                //NE Exit
                else if(terrain.getRiverExit(9) ) {
                    terrain.setPicture("NE"+subtype+"SE");
                }                   
                //SW exit
                else if(terrain.getRiverExit(1) ) {
                    terrain.setPicture("SE"+subtype+"SW");
                } 
                //SE exit
                else if(terrain.getRiverExit(3) ) {
                    terrain.setPicture("S"+subtype+"SE");
                }                 
                else {
                    terrain.setPicture(subtype);                    
                }
            } 
            else {
                terrain.setPicture(subtype);                    
            }
                
            
            if(subtype.equals("sea"))
            {
                GridVector g = terrain.getLocation();                
                if(terrain.getRiverName().length() > 0) {
                    terrain.setPicture("plains");
                    terrain.setSubtypeName("plains");                    
                    terrain.setName("Silt Plain");
                    choosePicture(terrain);
                    if(g.getZ() < 0) {
                        g.setZ(0);
                    }
                }
                else {                
                    if(g.getZ() <= (elevationLowest/4)) {
                        terrain.setPicture("ocean");                      
                    }
                    else { 
                        terrain.setPicture("water");                    
                    }
                }
            }
            
            if(subtype.equals("ice")) {
                terrain.setPicture("ice");               
            }                                
            
            if(subtype.equals("brightlight"))
            {
                GridVector g = terrain.getLocation();                
                if(terrain.getRiverName().length() > 0) {
                    terrain.setPicture("waterplains");
                    terrain.setSubtypeName("waterplains");                    
                    terrain.setName("Water Plain");
                    choosePicture(terrain);
                    if(g.getZ() < 0) {
                        g.setZ(0);
                    }
                }
                else {                
                    if(g.getZ() <= (elevationLowest/4)) {
                        terrain.setPicture("brightlight");                      
                    }
                    else { 
                        terrain.setPicture("light");                    
                    }
                }
            }
            
            if(subtype.equals("deepdarkness"))
            {
                GridVector g = terrain.getLocation();                
                if(terrain.getRiverName().length() > 0) {
                    terrain.setPicture("wasteland");
                    terrain.setSubtypeName("wasteland");                    
                    terrain.setName("Wasteland");
                    choosePicture(terrain);
                    if(g.getZ() < 0) {
                        g.setZ(0);
                    }
                }
                else {                
                    if(g.getZ() <= (elevationLowest/4)) {
                        terrain.setPicture("deepdarkness");                      
                    }
                    else { 
                        terrain.setPicture("darkness");                    
                    }
                }
            }
            
        }
    }
    
    
    /**
     * computeClimates
     */
    private World computeClimates(World newWorld)
    {
        //Get equatorial zone and polar zone
        float fHalf = (float)(iDimensions*iDimensions) / 2;
        float fTenth = (float)(iDimensions*iDimensions) / 10;
        
        float fEquatorTop = fHalf - fTenth;
        float fEquatorBottom = fHalf + fTenth;
        
        float fPolarNorth = fTenth;
        float fPolarSouth = (float)(iDimensions*iDimensions) - fTenth; 
        
        //Get desert zone 1 & 2
        float fDesertZone1 = fEquatorTop - fTenth;
        float fDesertZone2 = fEquatorBottom + fTenth;        
        
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap(); 
        
        Set<Integer> terrainSet = terrainMap.keySet();
        
        //Go through the terrain     
        for(Integer iTemp : terrainSet) {
            Terrain terrain = terrainMap.get(iTemp);
            
            int id = terrain.getID();
            String subtype = terrain.getSubtypeName();
            int avgRain = (int)(terrain.getRainfall(0)+terrain.getRainfall(1)+terrain.getRainfall(2)+terrain.getRainfall(3))/4;
            float springTemp = terrain.getTemperature(0);
            float summerTemp = terrain.getTemperature(1);
            float fallTemp = terrain.getTemperature(2);
            float winterTemp = terrain.getTemperature(3);            
            
            //Forests
            if(subtype.equals("hill")) {
                if(avgRain >= 80) {
                    if(id <= (int)fHalf) { //northern hemisphere                    
                        if(summerTemp >=15 && winterTemp > 5) {
                            terrain.setSubtypeName("forest"); 
                            choosePicture(terrain);
                            terrain.setName("");                    
                        }
                        else if(summerTemp >=5) {
                            terrain.setSubtypeName("snowforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }
                    }
                    else { //southern hemisphere
                        if(winterTemp >=15 && summerTemp > 5) {
                            terrain.setSubtypeName("forest"); 
                            choosePicture(terrain);
                            terrain.setName("");                    
                        }
                        else if(winterTemp >=5) {
                            terrain.setSubtypeName("snowforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }                        
                    }
                }
            }
            else if(subtype.equals("plains")) {
                if(avgRain >= 150) {
                    if(id <= (int)fHalf) { //northern hemisphere                    
                        if(summerTemp >=15 && winterTemp > 5) {
                            terrain.setSubtypeName("forest"); 
                            choosePicture(terrain);
                            terrain.setName("");                    
                        }
                        else if(summerTemp >=5) {
                            terrain.setSubtypeName("snowforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }
                    }
                    else { //southern hemisphere
                        if(winterTemp >=15 && summerTemp > 5) {
                            terrain.setSubtypeName("forest"); 
                            choosePicture(terrain);
                            terrain.setName("");                    
                        }
                        else if(winterTemp >=5) {
                            terrain.setSubtypeName("snowforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }                        
                    }
                }
            }
            else if(subtype.equals("snowhill")) {
                if(avgRain >= 80 && (summerTemp >=5 || winterTemp >=5)) {
                    terrain.setSubtypeName("snowforest");  
                    choosePicture(terrain);
                    terrain.setName("");                    
                }
            }                   
             else if(subtype.equals("tundra")) {
                if(avgRain >= 150 && (summerTemp >=5 || winterTemp >=5)) {
                    terrain.setSubtypeName("snowforest");  
                    choosePicture(terrain);
                    terrain.setName("");                    
                }
            }
            else if(subtype.equals("wasteland")) {
                if(avgRain >= 80) {
                    if(id <= (int)fHalf) { //northern hemisphere                    
                        if(summerTemp >=5) {
                            terrain.setSubtypeName("toxic"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }
                    }
                    else { //southern hemisphere
                        if(winterTemp >=5) {
                            terrain.setSubtypeName("toxic"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }                        
                    }
                }
            }
            else if(subtype.equals("fireplains")) {
                if(avgRain >= 150) {
                    if(id <= (int)fHalf) { //northern hemisphere                    
                        if(summerTemp >=5) {
                            terrain.setSubtypeName("toxic"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }
                    }
                    else { //southern hemisphere
                        if(winterTemp >=5) {
                            terrain.setSubtypeName("toxic"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }                        
                    }
                }
            }            
            else if(subtype.equals("glowplains")) {
                if(avgRain >= 150) {
                    if(id <= (int)fHalf) { //northern hemisphere                    
                        if(summerTemp >=15 && winterTemp > 5) {
                            terrain.setSubtypeName("glowforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                    
                        }
                        else if(summerTemp >=5) {
                            terrain.setSubtypeName("verdantforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }
                    }
                    else { //southern hemisphere
                        if(winterTemp >=15 && summerTemp > 5) {
                            terrain.setSubtypeName("glowforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                    
                        }
                        else if(winterTemp >=5) {
                            terrain.setSubtypeName("verdantforest"); 
                            choosePicture(terrain);
                            terrain.setName("");                         
                        }                        
                    }
                }
            }            
            
            
            //Deserts
            if(subtype.equals("hill")) {
                if(avgRain <= 30) {
                    terrain.setSubtypeName("dryhill"); 
                    choosePicture(terrain);
                    terrain.setName("");                    
                }                
            }
            else if(subtype.equals("plains")) {
                if(avgRain <= 30) {
                    terrain.setSubtypeName("desert"); 
                    choosePicture(terrain);
                    terrain.setName("");                    
                }
            }            
            else if(subtype.equals("forest")) {
                if(avgRain <= 30) {
                    if(terrain.getLocation().getZ() >= (elevationHighest/4) && terrain.getLocation().getZ() < elevationHighest) {
                        terrain.setSubtypeName("dryhill");                        
                    }
                    else {
                        terrain.setSubtypeName("desert");
                    }
                    choosePicture(terrain);
                    terrain.setName("");                    
                }
            }
            else if(subtype.equals("wasteland")) {
                if(avgRain <= 30) {
                    if(terrain.getLocation().getZ() >= (elevationHighest/4) && terrain.getLocation().getZ() < elevationHighest) {
                        terrain.setSubtypeName("firehill");                        
                    }
                    else {
                        terrain.setSubtypeName("fireplains");
                    }
                    choosePicture(terrain);
                    terrain.setName("");                    
                }                
            }            
            else if(subtype.equals("toxic")) {
                if(avgRain <= 30) {
                    if(terrain.getLocation().getZ() >= (elevationHighest/4) && terrain.getLocation().getZ() < elevationHighest) {
                        terrain.setSubtypeName("firehill");                        
                    }
                    else {
                        terrain.setSubtypeName("fireplains");
                    }
                    choosePicture(terrain);
                    terrain.setName("");                    
                }
            }
            else if(subtype.equals("glowforest") || subtype.equals("verdantforest") ) {
                if(avgRain <= 30) {
                    terrain.setSubtypeName("glowplains");                        
                    choosePicture(terrain);
                    terrain.setName("");                    
                }
            }            
            
            //Deserts reverting
            if(subtype.equals("desert")) {
                if(avgRain > 30) {
                    terrain.setSubtypeName("plains"); 
                    choosePicture(terrain);
                    terrain.setName("");                    
                }                
            }
            if(subtype.equals("dryhill")) {
                if(avgRain > 30) {
                    terrain.setSubtypeName("hill"); 
                    choosePicture(terrain);
                    terrain.setName("");                    
                }                
            }            
            if(subtype.equals("firehill")) {
                if(avgRain > 30) {
                    terrain.setSubtypeName("wasteland"); 
                    choosePicture(terrain);
                    terrain.setName("");                    
                }                
            }            
            if(subtype.equals("fireplains")) {
                if(avgRain > 80) {
                    terrain.setSubtypeName("wasteland"); 
                    choosePicture(terrain);
                    terrain.setName("");                    
                }                
            }            
            
            //Temperature    
            if(summerTemp > 0 || winterTemp > 0) {
                if(id <= (int)fHalf) { //northern hemisphere
                    if(summerTemp < 10) { //summer temp < 10
                        if(subtype.equals("hill")) {
                            terrain.setSubtypeName("snowhill");                              
                            choosePicture(terrain);  
                            terrain.setName("");                            
                        }                           
                        else if(subtype.equals("plains")) {
                            terrain.setSubtypeName("tundra");
                            choosePicture(terrain);   
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("desert")) {
                            terrain.setSubtypeName("snowdesert");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if((subtype.equals("forest") && (summerTemp >= 5))) {
                            terrain.setSubtypeName("snowforest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("forest")) { //& not >= 5
                            terrain.setSubtypeName("tundra");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }                        
                        else if((subtype.equals("glowforest") && (summerTemp >= 5))) {
                            terrain.setSubtypeName("verdantforest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("glowforest")) { //& not >= 5
                            terrain.setSubtypeName("glowplains");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }                        
                    }
                    else { // >= 10
                        if(subtype.equals("snowhill")) {
                            terrain.setSubtypeName("hill");                               
                            choosePicture(terrain);  
                            terrain.setName("");                            
                        }                           
                        else if(subtype.equals("tundra")) {
                            terrain.setSubtypeName("plains");
                            choosePicture(terrain);   
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("snowdesert")) {
                            terrain.setSubtypeName("desert");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("snowforest") && (summerTemp >= 15) ) {
                            terrain.setSubtypeName("forest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("verdantforest") && (summerTemp >= 15 && winterTemp > 0)) {
                            terrain.setSubtypeName("glowforest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }                         
                    }
                }
                else { //southern hemisphere
                    if(winterTemp < 10) { //winter temp < 10
                        if(subtype.equals("hill")) {
                            terrain.setSubtypeName("snowhill");
                            choosePicture(terrain);  
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("plains")) {
                            terrain.setSubtypeName("tundra");
                            choosePicture(terrain);
                            terrain.setName("");                             
                        }
                        else if(subtype.equals("desert")) {
                            terrain.setSubtypeName("snowdesert");
                            choosePicture(terrain);     
                            terrain.setName("");                             
                        }
                        else if((subtype.equals("forest") && (winterTemp >= 5))) {
                            terrain.setSubtypeName("snowforest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("forest")) { //& not >= 5
                            terrain.setSubtypeName("tundra");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }                        
                        else if((subtype.equals("glowforest") && (winterTemp >= 5))) {
                            terrain.setSubtypeName("verdantforest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("glowforest")) { //& not >= 5
                            terrain.setSubtypeName("glowplains");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }                        
                    }
                    else { // >= 10
                        if(subtype.equals("snowhill")) {
                            terrain.setSubtypeName("hill");
                            choosePicture(terrain);  
                            terrain.setName("");                            
                        }
                        else if(subtype.equals("tundra")) {
                            terrain.setSubtypeName("plains");
                            choosePicture(terrain);
                            terrain.setName("");                             
                        }
                        else if(subtype.equals("snowdesert")) {
                            terrain.setSubtypeName("desert");
                            choosePicture(terrain);     
                            terrain.setName("");                             
                        }
                        else if(subtype.equals("snowforest") && (winterTemp >= 15)) {
                            terrain.setSubtypeName("forest");
                            choosePicture(terrain);  
                            terrain.setName("");                             
                        }
                        else if(subtype.equals("verdantforest") && (winterTemp >= 15 && summerTemp > 0)) {
                            terrain.setSubtypeName("glowforest");
                            choosePicture(terrain); 
                            terrain.setName("");                            
                        }                        
                    }
                }
            }
            else if(summerTemp <= 0 && winterTemp <= 0) { //summer and winter 0 or less than
                if(subtype.equals("mountain")) {
                    terrain.setSubtypeName("snowmountain");
                    choosePicture(terrain);    
                    terrain.setName("");                     
                }
                else if(subtype.equals("hill")) {
                    terrain.setSubtypeName("snowhill");
                    choosePicture(terrain);
                    terrain.setName("");                     
                }                   
                else if(subtype.equals("plains") || subtype.equals("forest")) {
                    terrain.setSubtypeName("snowdesert");
                    choosePicture(terrain);
                    terrain.setName("");                     
                }
                else if(subtype.equals("glowforest") || subtype.equals("verdantforest")) {
                    terrain.setSubtypeName("glowplains");
                    choosePicture(terrain);
                    terrain.setName("");                     
                }                
                else if(subtype.equals("lake") || subtype.equals("sea")) {
                    terrain.setSubtypeName("ice");
                    choosePicture(terrain);
                    terrain.setName("");                     
                }                     
            }           
            
            //Polar landscape extra
            if(id <= fPolarNorth || id >= fPolarSouth) {
                if(id <= (int)fHalf) { //northern hemisphere
                    if(summerTemp >= 5) { //summer temp >= 10
                        if(subtype.equals("forest")) {
                            terrain.setSubtypeName("snowforest");
                            choosePicture(terrain);  
                            terrain.setName("");                             
                        }
                        else if(subtype.equals("glowforest")) {
                            terrain.setSubtypeName("verdantforest");
                            choosePicture(terrain);  
                            terrain.setName("");                             
                        }                        
                    }
                }
                else { //southern hemisphere
                    if(winterTemp >= 5) { //winter temp >= 10
                        if(subtype.equals("forest")) {
                            terrain.setSubtypeName("snowforest");
                            choosePicture(terrain);  
                            terrain.setName("");                             
                        }
                        else if(subtype.equals("glowforest")) {
                            terrain.setSubtypeName("verdantforest");
                            choosePicture(terrain);  
                            terrain.setName("");                             
                        }                        
                    }
                }                 
            }             
            
            //Snow Deserts
            if(subtype.equals("tundra") || subtype.equals("snowforest")) {
                if(avgRain < 25 || (summerTemp <= 0 && winterTemp <= 0)) {
                    terrain.setSubtypeName("snowdesert"); 
                    choosePicture(terrain);
                    terrain.setName("");                     
                }
            }    
            
            if(subtype.equals("waterplains") || subtype.equals("verdantforest") || subtype.equals("glowforest")) {
                if(avgRain < 25 || (summerTemp <= 0 && winterTemp <= 0)) {
                    terrain.setSubtypeName("glowplains"); 
                    choosePicture(terrain);
                    terrain.setName("");                     
                }
            }

            if(subtype.equals("toxic")) {
                if(avgRain < 25 || (summerTemp <= 0 && winterTemp <= 0)) {
                    terrain.setSubtypeName("wasteland"); 
                    choosePicture(terrain);
                    terrain.setName("");                     
                }
            }            
            
            //Adjust snowforest
             if(subtype.equals("snowforest")) {
                if((id <= (int)fHalf && summerTemp < 5) || (id > (int)fHalf && winterTemp < 5)) {
                    if(terrain.getLocation().getZ() >= (elevationHighest/4) && terrain.getLocation().getZ() < elevationHighest) {
                        terrain.setSubtypeName("snowhill");                        
                    }
                    else {
                        terrain.setSubtypeName("tundra");
                    }                    
                    choosePicture(terrain);
                    terrain.setName("");                     
                }
            }
            
            if(subtype.equals("verdantforest")) {
                if((id <= (int)fHalf && summerTemp < 5) || (id > (int)fHalf && winterTemp < 5)) {
                    terrain.setSubtypeName("glowplains");                    
                    choosePicture(terrain);
                    terrain.setName("");                     
                }
            }            
            
        }        
        
        return newWorld;
    }
    
    
    /**
     * A fix for pictures and silt plains
     */
    private World fixPictures(World newWorld) {        
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap(); 
        
        Set<Integer> terrainSet = terrainMap.keySet();
        
        //Go through the terrain     
        for(Integer iTemp : terrainSet) {
            Terrain terrain = terrainMap.get(iTemp);             
            
            if(terrain.getSubtypeName().equals("sea"))
            {
                GridVector g = terrain.getLocation();
                int id = terrain.getID();
                boolean riverNeighbour = false;
                String riverName = "River";
                
                Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
                Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);                
                Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
                Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
                Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
                Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
                Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
                Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);                                   
                
                if( leftbelowNeighbour != null ) {
                    if( ( ((leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end")) && (leftbelowNeighbour.getRiverEntrance(1)))
                      || leftbelowNeighbour.getRiverExit(9)) ) {                    
                        if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                                                    
                            riverName = leftbelowNeighbour.getRiverName();
                            if(riverName == null || riverName == "")
                               riverName = "River Mouth";
                            riverNeighbour = true;
                            leftbelowNeighbour.setPicture("NE"+leftbelowNeighbour.getSubtypeName()+"SW");                        
                            leftbelowNeighbour.eraseRiverExit(5);                        
                            leftbelowNeighbour.setRiverExit(9);                       
                            terrain.setRiverEntrance(1);
                            terrain.setRiverExit(9);
                        }
                        else
                            g.setZ(g.getZ()+2);                        
                    }
                }       
                
                if(riverNeighbour == false) {
                    if(leftNeighbour != null) {                       
                       if( ( ((leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end")) && (leftNeighbour.getRiverEntrance(4)))
                         || leftNeighbour.getRiverExit(6)) ) {
                           if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {
                               riverName = leftNeighbour.getRiverName(); 
                               if(riverName == null || riverName == "")
                                   riverName = "River Mouth";                           
                               riverNeighbour = true;
                               leftNeighbour.setPicture("W"+leftNeighbour.getSubtypeName()+"E");                            
                               leftNeighbour.eraseRiverExit(5);                           
                               leftNeighbour.setRiverExit(6);                          
                               terrain.setRiverEntrance(4);
                               terrain.setRiverExit(6);
                           }
                           else
                               g.setZ(g.getZ()+2);                           
                        }
                    }
                }
                if(riverNeighbour == false) {                
                    if(leftaboveNeighbour != null) { 
                       if( ( ((leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end")) && (leftaboveNeighbour.getRiverEntrance(7)))
                         || leftaboveNeighbour.getRiverExit(3)) ) { 
                           if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                             
                               riverName = leftaboveNeighbour.getRiverName();  
                               if(riverName == null || riverName == "")
                                   riverName = "River Mouth";                            
                               riverNeighbour = true;
                               leftaboveNeighbour.setPicture("NW"+leftaboveNeighbour.getSubtypeName()+"SE");                           
                               leftaboveNeighbour.eraseRiverExit(5);                            
                               leftaboveNeighbour.setRiverExit(3);
                               terrain.setRiverEntrance(7);
                               terrain.setRiverExit(3);
                           }
                           else
                               g.setZ(g.getZ()+2);                               
                        }   
                    }            
                }
                if(riverNeighbour == false) {                
                    if(aboveNeighbour != null) { 
                       if( ( ((aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end")) && (aboveNeighbour.getRiverEntrance(8))) 
                         || aboveNeighbour.getRiverExit(2)) ) {    
                           if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                               
                               riverName = aboveNeighbour.getRiverName(); 
                               if(riverName == null || riverName == "")
                                   riverName = "River Mouth";                            
                               riverNeighbour = true;
                               aboveNeighbour.setPicture("N"+aboveNeighbour.getSubtypeName()+"S");                           
                               aboveNeighbour.eraseRiverExit(5);                           
                               aboveNeighbour.setRiverExit(2);                            
                               terrain.setRiverEntrance(8);
                               terrain.setRiverExit(2);
                           }
                           else
                               g.setZ(g.getZ()+2);                               
                        } 
                    } 
                }
                if(riverNeighbour == false) {                  
                    if(rightaboveNeighbour != null) {
                       if( ( ((rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end")) && (rightaboveNeighbour.getRiverEntrance(9)))
                         || rightaboveNeighbour.getRiverExit(1)) ) {   
                           if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                              
                               riverName = rightaboveNeighbour.getRiverName();
                               if(riverName == null || riverName == "")
                                   riverName = "River Mouth";                            
                               riverNeighbour = true;
                               rightaboveNeighbour.setPicture("NE"+rightaboveNeighbour.getSubtypeName()+"SW");                           
                               rightaboveNeighbour.eraseRiverExit(5);                            
                               rightaboveNeighbour.setRiverExit(1);                             
                               terrain.setRiverEntrance(9);
                               terrain.setRiverExit(1);
                           }
                           else
                               g.setZ(g.getZ()+2);                                
                        }
                    }     
                }
                if(riverNeighbour == false) {                  
                    if(rightNeighbour != null) {                          
                       if( ( ((rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end")) && (rightNeighbour.getRiverEntrance(6)))
                         || rightNeighbour.getRiverExit(4)) ) {    
                           if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                              
                               riverName = rightNeighbour.getRiverName();
                               if(riverName == null || riverName == "")
                                   riverName = "River Mouth";                           
                               riverNeighbour = true;
                               rightNeighbour.setPicture("W"+rightNeighbour.getSubtypeName()+"E");                           
                               rightNeighbour.eraseRiverExit(5);                           
                               rightNeighbour.setRiverExit(4);                          
                               terrain.setRiverEntrance(6);
                               terrain.setRiverExit(4);
                           }
                           else
                               g.setZ(g.getZ()+2);                                
                        }
                    }
                }
                if(riverNeighbour == false) {                  
                    if(rightbelowNeighbour != null) {
                       if( ( ((rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end")) && (rightbelowNeighbour.getRiverEntrance(3)))
                         || rightbelowNeighbour.getRiverExit(7)) ) {    
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                               
                                riverName = rightbelowNeighbour.getRiverName();  
                                if(riverName == null || riverName == "")
                                    riverName = "River Mouth";                             
                                riverNeighbour = true;
                                rightbelowNeighbour.setPicture("NW"+rightbelowNeighbour.getSubtypeName()+"SE");                           
                                rightbelowNeighbour.eraseRiverExit(5);                             
                                rightbelowNeighbour.setRiverExit(7);                             
                                terrain.setRiverEntrance(3);
                                terrain.setRiverExit(7);
                            }
                            else
                               g.setZ(g.getZ()+2);                                 
                        }
                    }
                }
                if(riverNeighbour == false) {                  
                    if(belowNeighbour != null) {
                       if( ( ((belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end")) && (belowNeighbour.getRiverEntrance(2)))
                         || belowNeighbour.getRiverExit(8)) ) {          
                           if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                             
                               riverName = belowNeighbour.getRiverName();   
                               if(riverName == null || riverName == "")
                                   riverName = "River Mouth";                            
                               riverNeighbour = true;
                               belowNeighbour.setPicture("N"+belowNeighbour.getSubtypeName()+"S");                           
                               belowNeighbour.eraseRiverExit(5);                            
                               belowNeighbour.setRiverExit(8);                           
                               terrain.setRiverEntrance(2);
                               terrain.setRiverExit(8);
                           }
                           else
                               g.setZ(g.getZ()+2);                           
                        }
                    }
                }
                
                
                if(riverNeighbour && newWorld.getID()>=3)
                {            
                    terrain.setRiverName(riverName);                       
                    terrain.setPicture("plains");
                    terrain.setSubtypeName("plains");                    
                    terrain.setName("Silt Plain");
                    if(g.getZ() < 0) {
                        g.setZ(0);
                    }
                    
                    //If the river now forms a land bridge, end the river
                    if(terrain.getRiverExit(7)) {
                        
                        if((leftaboveNeighbour != null) && (!leftaboveNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!leftaboveNeighbour.getPicture().equals("water"))
                          && (!leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end"))
                          && (!leftaboveNeighbour.getPicture().equals("settlement")) && (!(leftaboveNeighbour.getRiverName().length() > 0)) ) {
                            leftaboveNeighbour.setPicture(leftaboveNeighbour.getSubtypeName()+"end");
                            leftaboveNeighbour.setRiverEntrance(3);
                            leftaboveNeighbour.setRiverExit(5);
                            leftaboveNeighbour.setRiverName(riverName);                               
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("sea") || rightaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("plains");
                                rightaboveNeighbour.setSubtypeName("plains");                    
                                rightaboveNeighbour.setName("Silt Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);                              
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("sea") || rightNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("plains");
                                rightNeighbour.setSubtypeName("plains");                    
                                rightNeighbour.setName("Silt Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("sea") || belowNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("plains");
                                belowNeighbour.setSubtypeName("plains");                    
                                belowNeighbour.setName("Silt Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("sea") || leftbelowNeighbour.getPicture().equals("water")) ) {                       
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("plains");
                                leftbelowNeighbour.setSubtypeName("plains");                    
                                leftbelowNeighbour.setName("Silt Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                    
                        }
                    }        
                    
                    if(terrain.getRiverExit(8)) {
                        
                        if((aboveNeighbour != null) && (!aboveNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!aboveNeighbour.getPicture().equals("water"))
                          && (!aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end"))
                          && (!aboveNeighbour.getPicture().equals("settlement")) && (!(aboveNeighbour.getRiverName().length() > 0)) ) {
                            aboveNeighbour.setPicture(aboveNeighbour.getSubtypeName()+"end");
                            aboveNeighbour.setRiverEntrance(2);
                            aboveNeighbour.setRiverExit(5);
                            aboveNeighbour.setRiverName(riverName); 
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("sea") || leftNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("plains");
                                leftNeighbour.setSubtypeName("plains");                    
                                leftNeighbour.setName("Silt Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("sea") || rightNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("plains");
                                rightNeighbour.setSubtypeName("plains");                    
                                rightNeighbour.setName("Silt Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("sea") || rightbelowNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("plains");
                                rightbelowNeighbour.setSubtypeName("plains");                    
                                rightbelowNeighbour.setName("Silt Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("sea") || leftbelowNeighbour.getPicture().equals("water")) ) {                       
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("plains");
                                leftbelowNeighbour.setSubtypeName("plains");                    
                                leftbelowNeighbour.setName("Silt Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                      
                        }                                                    
                    }
                    if(terrain.getRiverExit(9)) {
                        
                        if((rightaboveNeighbour != null) && (!rightaboveNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!rightaboveNeighbour.getPicture().equals("water"))
                          && (!rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end"))
                          && (!rightaboveNeighbour.getPicture().equals("settlement")) && (!(rightaboveNeighbour.getRiverName().length() > 0)) ) {
                            rightaboveNeighbour.setPicture(rightaboveNeighbour.getSubtypeName()+"end");
                            rightaboveNeighbour.setRiverEntrance(1);
                            rightaboveNeighbour.setRiverExit(5);
                            rightaboveNeighbour.setRiverName(riverName);
                        }                       
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("sea") || leftaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("plains");
                                leftaboveNeighbour.setSubtypeName("plains");                    
                                leftaboveNeighbour.setName("Silt Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("sea") || leftNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("plains");
                                leftNeighbour.setSubtypeName("plains");                    
                                leftNeighbour.setName("Silt Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("sea") || rightbelowNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("plains");
                                rightbelowNeighbour.setSubtypeName("plains");                    
                                rightbelowNeighbour.setName("Silt Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                          
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("sea") || belowNeighbour.getPicture().equals("water")) ) {                       
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("plains");
                                belowNeighbour.setSubtypeName("plains");                    
                                belowNeighbour.setName("Silt Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                     
                        }                        
                    }
                    if(terrain.getRiverExit(4)) {
                        
                        if((leftNeighbour != null) && (!leftNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!leftNeighbour.getPicture().equals("water"))
                          && (!leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end"))
                          && (!leftNeighbour.getPicture().equals("settlement")) && (!(leftNeighbour.getRiverName().length() > 0)) ) {
                            leftNeighbour.setPicture(leftNeighbour.getSubtypeName()+"end");
                            leftNeighbour.setRiverEntrance(6);
                            leftNeighbour.setRiverExit(5);
                            leftNeighbour.setRiverName(riverName); 
                        }
                       
                        //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("sea") || aboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("plains");
                                aboveNeighbour.setSubtypeName("plains");                    
                                aboveNeighbour.setName("Silt Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("sea") || rightaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("plains");
                                rightaboveNeighbour.setSubtypeName("plains");                    
                                rightaboveNeighbour.setName("Silt Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("sea") || rightbelowNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("plains");
                                rightbelowNeighbour.setSubtypeName("plains");                    
                                rightbelowNeighbour.setName("Silt Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("sea") || belowNeighbour.getPicture().equals("water")) ) {                       
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("plains");
                                belowNeighbour.setSubtypeName("plains");                    
                                belowNeighbour.setName("Silt Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                     
                        }                        
                    }
                    
                    if(terrain.getRiverExit(6)) {
                        
                        if((rightNeighbour != null) && (!rightNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!rightNeighbour.getPicture().equals("water"))
                          && (!rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end"))
                          && (!rightNeighbour.getPicture().equals("settlement")) && (!(rightNeighbour.getRiverName().length() > 0)) ) {
                            rightNeighbour.setPicture(rightNeighbour.getSubtypeName()+"end");
                            rightNeighbour.setRiverEntrance(4);
                            rightNeighbour.setRiverExit(5);
                            rightNeighbour.setRiverName(riverName); 
                        }
                        
                         //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("sea") || aboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("plains");
                                aboveNeighbour.setSubtypeName("plains");                    
                                aboveNeighbour.setName("Silt Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("sea") || leftaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("plains");
                                leftaboveNeighbour.setSubtypeName("plains");                    
                                leftaboveNeighbour.setName("Silt Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                          
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("sea") || leftbelowNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("plains");
                                leftbelowNeighbour.setSubtypeName("plains");                    
                                leftbelowNeighbour.setName("Silt Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("sea") || belowNeighbour.getPicture().equals("water")) ) {                       
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("plains");
                                belowNeighbour.setSubtypeName("plains");                    
                                belowNeighbour.setName("Silt Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                      
                        }                       
                    }
                    
                    if(terrain.getRiverExit(1)) {
                        
                        if((leftbelowNeighbour != null) && (!leftbelowNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!leftbelowNeighbour.getPicture().equals("water"))
                          && (!leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end"))
                          && (!leftbelowNeighbour.getPicture().equals("settlement")) && (!(leftbelowNeighbour.getRiverName().length() > 0)) ) {
                            leftbelowNeighbour.setPicture(leftbelowNeighbour.getSubtypeName()+"end");
                            leftbelowNeighbour.setRiverEntrance(9);
                            leftbelowNeighbour.setRiverExit(5);
                            leftbelowNeighbour.setRiverName(riverName); 
                        }
                            
                        //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("sea") || aboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("plains");
                                aboveNeighbour.setSubtypeName("plains");                    
                                aboveNeighbour.setName("Silt Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("sea") || leftaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("plains");
                                leftaboveNeighbour.setSubtypeName("plains");                    
                                leftaboveNeighbour.setName("Silt Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("sea") || rightNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("plains");
                                rightNeighbour.setSubtypeName("plains");                    
                                rightNeighbour.setName("Silt Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("sea") || rightbelowNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("plains");
                                rightbelowNeighbour.setSubtypeName("plains");                    
                                rightbelowNeighbour.setName("Silt Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                           
                        }                        
                    }
                    if(terrain.getRiverExit(2)) {
                        
                        if((belowNeighbour != null) && (!belowNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!belowNeighbour.getPicture().equals("water"))
                          && (!belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end"))
                          && (!belowNeighbour.getPicture().equals("settlement")) && (!(belowNeighbour.getRiverName().length() > 0)) ) {
                            belowNeighbour.setPicture(belowNeighbour.getSubtypeName()+"end");
                            belowNeighbour.setRiverEntrance(8);
                            belowNeighbour.setRiverExit(5);
                            belowNeighbour.setRiverName(riverName); 
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("sea") || rightaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("plains");
                                rightaboveNeighbour.setSubtypeName("plains");                    
                                rightaboveNeighbour.setName("Silt Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("sea") || leftaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("plains");
                                leftaboveNeighbour.setSubtypeName("plains");                    
                                leftaboveNeighbour.setName("Silt Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("sea") || leftNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("plains");
                                leftNeighbour.setSubtypeName("plains");                    
                                leftNeighbour.setName("Silt Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("sea") || rightNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("plains");
                                rightNeighbour.setSubtypeName("plains");                    
                                rightNeighbour.setName("Silt Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                            
                        }                                                       
                    }
                    
                    if(terrain.getRiverExit(3)) {
                        
                        if((rightbelowNeighbour != null) && (!rightbelowNeighbour.getSubtypeName().equals("sea")) && (!leftaboveNeighbour.getSubtypeName().equals("ice"))
                          && (!rightbelowNeighbour.getPicture().equals("water"))
                          && (!rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end"))
                          && (!rightbelowNeighbour.getPicture().equals("settlement")) && (!(rightbelowNeighbour.getRiverName().length() > 0)) ) {
                            rightbelowNeighbour.setPicture(rightbelowNeighbour.getSubtypeName()+"end");
                            rightbelowNeighbour.setRiverEntrance(7);
                            rightbelowNeighbour.setRiverExit(5);
                            rightbelowNeighbour.setRiverName(riverName);                             
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("sea") || rightaboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("plains");
                                rightaboveNeighbour.setSubtypeName("plains");                    
                                rightaboveNeighbour.setName("Silt Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);  
                        }
                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("sea") || aboveNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("plains");
                                aboveNeighbour.setSubtypeName("plains");                    
                                aboveNeighbour.setName("Silt Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("sea") || leftNeighbour.getPicture().equals("water")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("plains");
                                leftNeighbour.setSubtypeName("plains");                    
                                leftNeighbour.setName("Silt Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("sea") || leftbelowNeighbour.getPicture().equals("water")) ) {                       
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("plains");
                                leftbelowNeighbour.setSubtypeName("plains");                    
                                leftbelowNeighbour.setName("Silt Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                      
                        }                       
                    }
                    //End exit ifs
                    
                }

                //Celestial plane
                if(riverNeighbour && newWorld.getID()==0)
                {            
                    terrain.setRiverName(riverName);                       
                    terrain.setPicture("waterplains");
                    terrain.setSubtypeName("waterplains");                    
                    terrain.setName("Water Plain");
                    if(g.getZ() < 0) {
                        g.setZ(0);
                    }
                    
                    //If the river now forms a land bridge, end the river
                    if(terrain.getRiverExit(7)) {
                        
                        if((leftaboveNeighbour != null) && (!leftaboveNeighbour.getSubtypeName().equals("bright light")) && (!leftaboveNeighbour.getPicture().equals("light"))
                          && (!leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end"))
                          && (!leftaboveNeighbour.getPicture().equals("settlement")) && (!(leftaboveNeighbour.getRiverName().length() > 0)) ) {
                            leftaboveNeighbour.setPicture(leftaboveNeighbour.getSubtypeName()+"end");
                            leftaboveNeighbour.setRiverEntrance(3);
                            leftaboveNeighbour.setRiverExit(5);
                            leftaboveNeighbour.setRiverName(riverName);                               
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("brightlight") || rightaboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("waterplains");
                                rightaboveNeighbour.setSubtypeName("waterplains");                    
                                rightaboveNeighbour.setName("Water Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);                              
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("brightlight") || rightNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("waterplains");
                                rightNeighbour.setSubtypeName("waterplains");                    
                                rightNeighbour.setName("Water Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("brightlight") || belowNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("waterplains");
                                belowNeighbour.setSubtypeName("waterplains");                    
                                belowNeighbour.setName("Water Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("brightlight") || leftbelowNeighbour.getPicture().equals("light")) ) {                      
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("waterplains");
                                leftbelowNeighbour.setSubtypeName("waterplains");                    
                                leftbelowNeighbour.setName("Water Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                    
                        }
                    }        
                    
                    if(terrain.getRiverExit(8)) {
                        
                        if((aboveNeighbour != null) && (!aboveNeighbour.getSubtypeName().equals("bright light")) && (!aboveNeighbour.getPicture().equals("light"))
                          && (!aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end"))
                          && (!aboveNeighbour.getPicture().equals("settlement")) && (!(aboveNeighbour.getRiverName().length() > 0)) ) {
                            aboveNeighbour.setPicture(aboveNeighbour.getSubtypeName()+"end");
                            aboveNeighbour.setRiverEntrance(2);
                            aboveNeighbour.setRiverExit(5);
                            aboveNeighbour.setRiverName(riverName); 
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("brightlight") || leftNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("waterplains");
                                leftNeighbour.setSubtypeName("waterplains");                    
                                leftNeighbour.setName("Water Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("brightlight") || rightNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("waterplains");
                                rightNeighbour.setSubtypeName("waterplains");                    
                                rightNeighbour.setName("Water Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("brightlight") || rightbelowNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("waterplains");
                                rightbelowNeighbour.setSubtypeName("waterplains");                    
                                rightbelowNeighbour.setName("Water Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("brightlight") || leftbelowNeighbour.getPicture().equals("light")) ) {                       
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("waterplains");
                                leftbelowNeighbour.setSubtypeName("waterplains");                    
                                leftbelowNeighbour.setName("Water Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                      
                        }                                                    
                    }
                    if(terrain.getRiverExit(9)) {
                        
                        if((rightaboveNeighbour != null) && (!rightaboveNeighbour.getSubtypeName().equals("bright light")) && (!rightaboveNeighbour.getPicture().equals("light"))
                          && (!rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end"))
                          && (!rightaboveNeighbour.getPicture().equals("settlement")) && (!(rightaboveNeighbour.getRiverName().length() > 0)) ) {
                            rightaboveNeighbour.setPicture(rightaboveNeighbour.getSubtypeName()+"end");
                            rightaboveNeighbour.setRiverEntrance(1);
                            rightaboveNeighbour.setRiverExit(5);
                            rightaboveNeighbour.setRiverName(riverName);
                        }                       
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("brightlight") || leftaboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("waterplains");
                                leftaboveNeighbour.setSubtypeName("waterplains");                    
                                leftaboveNeighbour.setName("Water Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("brightlight") || leftNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("waterplains");
                                leftNeighbour.setSubtypeName("waterplains");                    
                                leftNeighbour.setName("Water Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("brightlight") || rightbelowNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("waterplains");
                                rightbelowNeighbour.setSubtypeName("waterplains");                    
                                rightbelowNeighbour.setName("Water Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                          
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("brightlight") || belowNeighbour.getPicture().equals("light")) ) {                      
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("waterplains");
                                belowNeighbour.setSubtypeName("waterplains");                    
                                belowNeighbour.setName("Water Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                     
                        }                        
                    }
                    if(terrain.getRiverExit(4)) {
                        
                        if((leftNeighbour != null) && (!leftNeighbour.getSubtypeName().equals("bright light")) && (!leftNeighbour.getPicture().equals("light"))
                          && (!leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end"))
                          && (!leftNeighbour.getPicture().equals("settlement")) && (!(leftNeighbour.getRiverName().length() > 0)) ) {
                            leftNeighbour.setPicture(leftNeighbour.getSubtypeName()+"end");
                            leftNeighbour.setRiverEntrance(6);
                            leftNeighbour.setRiverExit(5);
                            leftNeighbour.setRiverName(riverName); 
                        }
                       
                        //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("brightlight") || aboveNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("waterplains");
                                aboveNeighbour.setSubtypeName("waterplains");                    
                                aboveNeighbour.setName("Water Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("brightlight") || rightaboveNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("waterplains");
                                rightaboveNeighbour.setSubtypeName("waterplains");                    
                                rightaboveNeighbour.setName("Water Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("brightlight") || rightbelowNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("waterplains");
                                rightbelowNeighbour.setSubtypeName("waterplains");                    
                                rightbelowNeighbour.setName("Water Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("brightlight") || belowNeighbour.getPicture().equals("light")) ) {                      
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("waterplains");
                                belowNeighbour.setSubtypeName("waterplains");                    
                                belowNeighbour.setName("Water Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                     
                        }                        
                    }
                    
                    if(terrain.getRiverExit(6)) {
                        
                        if((rightNeighbour != null) && (!rightNeighbour.getSubtypeName().equals("bright light")) && (!rightNeighbour.getPicture().equals("light"))
                          && (!rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end"))
                          && (!rightNeighbour.getPicture().equals("settlement")) && (!(rightNeighbour.getRiverName().length() > 0)) ) {
                            rightNeighbour.setPicture(rightNeighbour.getSubtypeName()+"end");
                            rightNeighbour.setRiverEntrance(4);
                            rightNeighbour.setRiverExit(5);
                            rightNeighbour.setRiverName(riverName); 
                        }
                        
                         //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("brightlight") || aboveNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("waterplains");
                                aboveNeighbour.setSubtypeName("waterplains");                    
                                aboveNeighbour.setName("Water Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("brightlight") || leftaboveNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("waterplains");
                                leftaboveNeighbour.setSubtypeName("waterplains");                    
                                leftaboveNeighbour.setName("Water Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                          
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("brightlight") || leftbelowNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("waterplains");
                                leftbelowNeighbour.setSubtypeName("waterplains");                    
                                leftbelowNeighbour.setName("Water Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("brightlight") || belowNeighbour.getPicture().equals("light")) ) {                       
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("waterplains");
                                belowNeighbour.setSubtypeName("waterplains");                    
                                belowNeighbour.setName("Water Plain");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                      
                        }                       
                    }
                    
                    if(terrain.getRiverExit(1)) {
                        
                        if((leftbelowNeighbour != null) && (!leftbelowNeighbour.getSubtypeName().equals("bright light")) && (!leftbelowNeighbour.getPicture().equals("light"))
                          && (!leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end"))
                          && (!leftbelowNeighbour.getPicture().equals("settlement")) && (!(leftbelowNeighbour.getRiverName().length() > 0)) ) {
                            leftbelowNeighbour.setPicture(leftbelowNeighbour.getSubtypeName()+"end");
                            leftbelowNeighbour.setRiverEntrance(9);
                            leftbelowNeighbour.setRiverExit(5);
                            leftbelowNeighbour.setRiverName(riverName); 
                        }
                            
                        //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("brightlight") || aboveNeighbour.getPicture().equals("light")) ) { 
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("waterplains");
                                aboveNeighbour.setSubtypeName("waterplains");                    
                                aboveNeighbour.setName("Water Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("brightlight") || leftaboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("waterplains");
                                leftaboveNeighbour.setSubtypeName("waterplains");                    
                                leftaboveNeighbour.setName("Water Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("brightlight") || rightNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("waterplains");
                                rightNeighbour.setSubtypeName("waterplains");                    
                                rightNeighbour.setName("Water Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("brightlight") || rightbelowNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("waterplains");
                                rightbelowNeighbour.setSubtypeName("waterplains");                    
                                rightbelowNeighbour.setName("Water Plain");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                           
                        }                        
                    }
                    if(terrain.getRiverExit(2)) {
                        
                        if((belowNeighbour != null) && (!belowNeighbour.getSubtypeName().equals("bright light")) && (!belowNeighbour.getPicture().equals("light"))
                          && (!belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end"))
                          && (!belowNeighbour.getPicture().equals("settlement")) && (!(belowNeighbour.getRiverName().length() > 0)) ) {
                            belowNeighbour.setPicture(belowNeighbour.getSubtypeName()+"end");
                            belowNeighbour.setRiverEntrance(8);
                            belowNeighbour.setRiverExit(5);
                            belowNeighbour.setRiverName(riverName); 
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("brightlight") || rightaboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("waterplains");
                                rightaboveNeighbour.setSubtypeName("waterplains");                    
                                rightaboveNeighbour.setName("Water Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("brightlight") || leftaboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("waterplains");
                                leftaboveNeighbour.setSubtypeName("waterplains");                    
                                leftaboveNeighbour.setName("Water Plain");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("brightlight") || leftNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("waterplains");
                                leftNeighbour.setSubtypeName("waterplains");                    
                                leftNeighbour.setName("Water Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("brightlight") || rightNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("waterplains");
                                rightNeighbour.setSubtypeName("waterplains");                    
                                rightNeighbour.setName("Water Plain");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                            
                        }                                                       
                    }
                    
                    if(terrain.getRiverExit(3)) {
                        
                        if((rightbelowNeighbour != null) && (!rightbelowNeighbour.getSubtypeName().equals("bright light")) && (!rightbelowNeighbour.getPicture().equals("light"))
                          && (!rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end"))
                          && (!rightbelowNeighbour.getPicture().equals("settlement")) && (!(rightbelowNeighbour.getRiverName().length() > 0)) ) {
                            rightbelowNeighbour.setPicture(rightbelowNeighbour.getSubtypeName()+"end");
                            rightbelowNeighbour.setRiverEntrance(7);
                            rightbelowNeighbour.setRiverExit(5);
                            rightbelowNeighbour.setRiverName(riverName);                             
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("brightlight") || rightaboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("waterplains");
                                rightaboveNeighbour.setSubtypeName("waterplains");                    
                                rightaboveNeighbour.setName("Water Plain");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);  
                        }
                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("brightlight") || aboveNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("waterplains");
                                aboveNeighbour.setSubtypeName("waterplains");                    
                                aboveNeighbour.setName("Water Plain");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("brightlight") || leftNeighbour.getPicture().equals("light")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("waterplains");
                                leftNeighbour.setSubtypeName("waterplains");                    
                                leftNeighbour.setName("Water Plain");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("brightlight") || leftbelowNeighbour.getPicture().equals("light")) ) {                      
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("waterplains");
                                leftbelowNeighbour.setSubtypeName("waterplains");                    
                                leftbelowNeighbour.setName("Water Plain");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                      
                        }                       
                    }
                    //End exit ifs
                    
                }
 
                 if(riverNeighbour && newWorld.getID()==1)
                {            
                    terrain.setRiverName(riverName);                       
                    terrain.setPicture("wasteland");
                    terrain.setSubtypeName("wasteland");                    
                    terrain.setName("Wasteland");
                    if(g.getZ() < 0) {
                        g.setZ(0);
                    }
                    
                    //If the river now forms a land bridge, end the river
                    if(terrain.getRiverExit(7)) {
                        
                        if((leftaboveNeighbour != null) && (!leftaboveNeighbour.getSubtypeName().equals("darkness"))
                          && (!leftaboveNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end"))
                          && (!leftaboveNeighbour.getPicture().equals("settlement")) && (!(leftaboveNeighbour.getRiverName().length() > 0)) ) {
                            leftaboveNeighbour.setPicture(leftaboveNeighbour.getSubtypeName()+"end");
                            leftaboveNeighbour.setRiverEntrance(3);
                            leftaboveNeighbour.setRiverExit(5);
                            leftaboveNeighbour.setRiverName(riverName);                               
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("darkness")
                          || rightaboveNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("wasteland");
                                rightaboveNeighbour.setSubtypeName("wasteland");                    
                                rightaboveNeighbour.setName("Wasteland");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);                              
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("darkness")
                          || rightNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("wasteland");
                                rightNeighbour.setSubtypeName("wasteland");                    
                                rightNeighbour.setName("Wasteland");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("darkness")
                          || belowNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("wasteland");
                                belowNeighbour.setSubtypeName("wasteland");                    
                                belowNeighbour.setName("Wasteland");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("darkness")
                          || leftbelowNeighbour.getPicture().equals("deepdarkness")) ) {                      
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("wasteland");
                                leftbelowNeighbour.setSubtypeName("wasteland");                    
                                leftbelowNeighbour.setName("Wasteland");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                    
                        }
                    }        
                    
                    if(terrain.getRiverExit(8)) {
                        
                        if((aboveNeighbour != null) && (!aboveNeighbour.getSubtypeName().equals("darkness"))
                          && (!aboveNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end"))
                          && (!aboveNeighbour.getPicture().equals("settlement")) && (!(aboveNeighbour.getRiverName().length() > 0)) ) {
                            aboveNeighbour.setPicture(aboveNeighbour.getSubtypeName()+"end");
                            aboveNeighbour.setRiverEntrance(2);
                            aboveNeighbour.setRiverExit(5);
                            aboveNeighbour.setRiverName(riverName); 
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("darkness")
                          || leftNeighbour.getPicture().equals("deepdarkness")) ) {  
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("wasteland");
                                leftNeighbour.setSubtypeName("wasteland");                    
                                leftNeighbour.setName("Wasteland");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("darkness")
                          || rightNeighbour.getPicture().equals("deepdarkness")) ) {  
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("wasteland");
                                rightNeighbour.setSubtypeName("wasteland");                    
                                rightNeighbour.setName("Wasteland");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("darkness")
                          || rightbelowNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("wasteland");
                                rightbelowNeighbour.setSubtypeName("wasteland");                    
                                rightbelowNeighbour.setName("Wasteland");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("darkness")
                          || leftbelowNeighbour.getPicture().equals("deepdarkness")) ) {                      
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("wasteland");
                                leftbelowNeighbour.setSubtypeName("wasteland");                    
                                leftbelowNeighbour.setName("Wasteland");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                      
                        }                                                    
                    }
                    if(terrain.getRiverExit(9)) {
                        
                        if((rightaboveNeighbour != null) && (!rightaboveNeighbour.getSubtypeName().equals("darkness"))
                          && (!rightaboveNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end"))
                          && (!rightaboveNeighbour.getPicture().equals("settlement")) && (!(rightaboveNeighbour.getRiverName().length() > 0)) ) {
                            rightaboveNeighbour.setPicture(rightaboveNeighbour.getSubtypeName()+"end");
                            rightaboveNeighbour.setRiverEntrance(1);
                            rightaboveNeighbour.setRiverExit(5);
                            rightaboveNeighbour.setRiverName(riverName);
                        }                       
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("darkness")
                          || leftaboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("wasteland");
                                leftaboveNeighbour.setSubtypeName("wasteland");                    
                                leftaboveNeighbour.setName("Wasteland");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("darkness")
                          || leftNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("wasteland");
                                leftNeighbour.setSubtypeName("wasteland");                    
                                leftNeighbour.setName("Wasteland");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("darkness")
                          || rightbelowNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("wasteland");
                                rightbelowNeighbour.setSubtypeName("wasteland");                    
                                rightbelowNeighbour.setName("Wasteland");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                          
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("darkness")
                          || belowNeighbour.getPicture().equals("deepdarkness")) ) {                       
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("wasteland");
                                belowNeighbour.setSubtypeName("wasteland");                    
                                belowNeighbour.setName("Wasteland");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                     
                        }                        
                    }
                    if(terrain.getRiverExit(4)) {
                        
                        if((leftNeighbour != null) && (!leftNeighbour.getSubtypeName().equals("darkness"))
                          && (!leftNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end"))
                          && (!leftNeighbour.getPicture().equals("settlement")) && (!(leftNeighbour.getRiverName().length() > 0)) ) {
                            leftNeighbour.setPicture(leftNeighbour.getSubtypeName()+"end");
                            leftNeighbour.setRiverEntrance(6);
                            leftNeighbour.setRiverExit(5);
                            leftNeighbour.setRiverName(riverName); 
                        }
                       
                        //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("darkness")
                          || aboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("wasteland");
                                aboveNeighbour.setSubtypeName("wasteland");                    
                                aboveNeighbour.setName("Wasteland");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("darkness")
                          || rightaboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("wasteland");
                                rightaboveNeighbour.setSubtypeName("wasteland");                    
                                rightaboveNeighbour.setName("Wasteland");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("darkness")
                          || rightbelowNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("wasteland");
                                rightbelowNeighbour.setSubtypeName("wasteland");                    
                                rightbelowNeighbour.setName("Wasteland");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("darkness")
                          || belowNeighbour.getPicture().equals("deepdarkness")) ) {                        
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("wasteland");
                                belowNeighbour.setSubtypeName("wasteland");                    
                                belowNeighbour.setName("Wasteland");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                     
                        }                        
                    }
                    
                    if(terrain.getRiverExit(6)) {
                        
                        if((rightNeighbour != null) && (!rightNeighbour.getSubtypeName().equals("darkness"))
                          && (!rightNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end"))
                          && (!rightNeighbour.getPicture().equals("settlement")) && (!(rightNeighbour.getRiverName().length() > 0)) ) {
                            rightNeighbour.setPicture(rightNeighbour.getSubtypeName()+"end");
                            rightNeighbour.setRiverEntrance(4);
                            rightNeighbour.setRiverExit(5);
                            rightNeighbour.setRiverName(riverName); 
                        }
                        
                         //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("darkness")
                          || aboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("wasteland");
                                aboveNeighbour.setSubtypeName("wasteland");                    
                                aboveNeighbour.setName("Wasteland");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("darkness")
                          || leftaboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("wasteland");
                                leftaboveNeighbour.setSubtypeName("wasteland");                    
                                leftaboveNeighbour.setName("Wasteland");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                          
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("darkness")
                          || leftbelowNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("wasteland");
                                leftbelowNeighbour.setSubtypeName("wasteland");                    
                                leftbelowNeighbour.setName("Wasteland");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((belowNeighbour != null) && (belowNeighbour.getSubtypeName().equals("darkness")
                          || belowNeighbour.getPicture().equals("deepdarkness")) ) {                       
                            if( Math.abs(belowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                belowNeighbour.setPicture("wasteland");
                                belowNeighbour.setSubtypeName("wasteland");                    
                                belowNeighbour.setName("Wasteland");
                                GridVector gv = belowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                belowNeighbour.getLocation().setZ(belowNeighbour.getLocation().getZ()+2);                                                                      
                        }                       
                    }
                    
                    if(terrain.getRiverExit(1)) {
                        
                        if((leftbelowNeighbour != null) && (!leftbelowNeighbour.getSubtypeName().equals("darkness"))
                          && (!leftbelowNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end"))
                          && (!leftbelowNeighbour.getPicture().equals("settlement")) && (!(leftbelowNeighbour.getRiverName().length() > 0)) ) {
                            leftbelowNeighbour.setPicture(leftbelowNeighbour.getSubtypeName()+"end");
                            leftbelowNeighbour.setRiverEntrance(9);
                            leftbelowNeighbour.setRiverExit(5);
                            leftbelowNeighbour.setRiverName(riverName); 
                        }
                            
                        //Also, if no land is surrounding, add some                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("darkness")
                          || aboveNeighbour.getPicture().equals("deepdarkness")) ) {  
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("wasteland");
                                aboveNeighbour.setSubtypeName("wasteland");                    
                                aboveNeighbour.setName("Wasteland");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("darkness")
                          || leftaboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("wasteland");
                                leftaboveNeighbour.setSubtypeName("wasteland");                    
                                leftaboveNeighbour.setName("Wasteland");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("darkness")
                          || rightNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("wasteland");
                                rightNeighbour.setSubtypeName("wasteland");                    
                                rightNeighbour.setName("Wasteland");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightbelowNeighbour != null) && (rightbelowNeighbour.getSubtypeName().equals("darkness")
                          || rightbelowNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(rightbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightbelowNeighbour.setPicture("wasteland");
                                rightbelowNeighbour.setSubtypeName("wasteland");                    
                                rightbelowNeighbour.setName("Wasteland");
                                GridVector gv = rightbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightbelowNeighbour.getLocation().setZ(rightbelowNeighbour.getLocation().getZ()+2);                           
                        }                        
                    }
                    if(terrain.getRiverExit(2)) {
                        
                        if((belowNeighbour != null) && (!belowNeighbour.getSubtypeName().equals("darkness"))
                          && (!belowNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end"))
                          && (!belowNeighbour.getPicture().equals("settlement")) && (!(belowNeighbour.getRiverName().length() > 0)) ) {
                            belowNeighbour.setPicture(belowNeighbour.getSubtypeName()+"end");
                            belowNeighbour.setRiverEntrance(8);
                            belowNeighbour.setRiverExit(5);
                            belowNeighbour.setRiverName(riverName); 
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("darkness")
                          || rightaboveNeighbour.getPicture().equals("deepdarkness")) ) { 
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("wasteland");
                                rightaboveNeighbour.setSubtypeName("wasteland");                    
                                rightaboveNeighbour.setName("Wasteland");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2); 
                        }
                        
                        if((leftaboveNeighbour != null) && (leftaboveNeighbour.getSubtypeName().equals("darkness")
                          || leftaboveNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(leftaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftaboveNeighbour.setPicture("wasteland");
                                leftaboveNeighbour.setSubtypeName("wasteland");                    
                                leftaboveNeighbour.setName("Wasteland");
                                GridVector gv = leftaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftaboveNeighbour.getLocation().setZ(leftaboveNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("darkness")
                          || leftNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("wasteland");
                                leftNeighbour.setSubtypeName("wasteland");                    
                                leftNeighbour.setName("Wasteland");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                             
                        }
                        
                        if((rightNeighbour != null) && (rightNeighbour.getSubtypeName().equals("darkness")
                          || rightNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(rightNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightNeighbour.setPicture("wasteland");
                                rightNeighbour.setSubtypeName("wasteland");                    
                                rightNeighbour.setName("Wasteland");
                                GridVector gv = rightNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightNeighbour.getLocation().setZ(rightNeighbour.getLocation().getZ()+2);                            
                        }                                                       
                    }
                    
                    if(terrain.getRiverExit(3)) {
                        
                        if((rightbelowNeighbour != null) && (!rightbelowNeighbour.getSubtypeName().equals("darkness"))
                          && (!rightbelowNeighbour.getSubtypeName().equals("deepdarkness"))
                          && (!rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end"))
                          && (!rightbelowNeighbour.getPicture().equals("settlement")) && (!(rightbelowNeighbour.getRiverName().length() > 0)) ) {
                            rightbelowNeighbour.setPicture(rightbelowNeighbour.getSubtypeName()+"end");
                            rightbelowNeighbour.setRiverEntrance(7);
                            rightbelowNeighbour.setRiverExit(5);
                            rightbelowNeighbour.setRiverName(riverName);                             
                        }
                        
                        //Also, if no land is surrounding, add some                        
                        if((rightaboveNeighbour != null) && (rightaboveNeighbour.getSubtypeName().equals("darkness")
                          || rightaboveNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(rightaboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                rightaboveNeighbour.setPicture("wasteland");
                                rightaboveNeighbour.setSubtypeName("wasteland");                    
                                rightaboveNeighbour.setName("Wasteland");
                                GridVector gv = rightaboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                rightaboveNeighbour.getLocation().setZ(rightaboveNeighbour.getLocation().getZ()+2);  
                        }
                        
                        if((aboveNeighbour != null) && (aboveNeighbour.getSubtypeName().equals("darkness")
                          || aboveNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(aboveNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                aboveNeighbour.setPicture("wasteland");
                                aboveNeighbour.setSubtypeName("wasteland");                    
                                aboveNeighbour.setName("Wasteland");
                                GridVector gv = aboveNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                aboveNeighbour.getLocation().setZ(aboveNeighbour.getLocation().getZ()+2);                            
                        }
                        
                        if((leftNeighbour != null) && (leftNeighbour.getSubtypeName().equals("darkness")
                          || leftNeighbour.getPicture().equals("deepdarkness")) ) {
                            if( Math.abs(leftNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftNeighbour.setPicture("wasteland");
                                leftNeighbour.setSubtypeName("wasteland");                    
                                leftNeighbour.setName("Wasteland");
                                GridVector gv = leftNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftNeighbour.getLocation().setZ(leftNeighbour.getLocation().getZ()+2);                           
                        }
                        
                        if((leftbelowNeighbour != null) && (leftbelowNeighbour.getSubtypeName().equals("darkness")
                          || leftbelowNeighbour.getPicture().equals("deepdarkness")) ) {                      
                            if( Math.abs(leftbelowNeighbour.getLocation().getZ()-g.getZ()) <= 2 ) {                            
                                leftbelowNeighbour.setPicture("wasteland");
                                leftbelowNeighbour.setSubtypeName("wasteland");                    
                                leftbelowNeighbour.setName("Wasteland");
                                GridVector gv = leftbelowNeighbour.getLocation();                                      
                                if(gv.getZ() < 0)
                                    gv.setZ(0);
                            }
                            else
                                leftbelowNeighbour.getLocation().setZ(leftbelowNeighbour.getLocation().getZ()+2);                                                                      
                        }                       
                    }
                    //End exit ifs
                    
                }                               
                
            }    
            
            choosePicture(terrain);            
        }
        
        return newWorld;
    }
    
    private World createBigLakes(World newWorld)
    {
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();                     
        Set<Integer> terrainSet = terrainMap.keySet();        
        
        //Go through the terrain     
        for(Integer iTemp : terrainSet) {
            boolean other = false;
            boolean skip = false;
            Terrain terrain = terrainMap.get(iTemp);
            Terrain temp;
            int id = iTemp;
            float summerTemp = terrain.getTemperature(1);
            float winterTemp = terrain.getTemperature(3);            
                    
            Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id); 
            Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
            Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);                    
            Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);           
            Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);                    
            Terrain rightNeighbour = getRightNeighbour(terrainMap, id);                    
            Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);                    
            Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);           
           
            if( terrain.getSubtypeName().equals("sea") || terrain.getSubtypeName().equals("brightlight")
              || terrain.getSubtypeName().equals("deepdarkness") || terrain.getSubtypeName().equals("lake") ) {              
                if( leftbelowNeighbour != null && ( leftbelowNeighbour.getSubtypeName().equals("sea") || leftbelowNeighbour.getSubtypeName().equals("brightlight")
                  || leftbelowNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( leftNeighbour != null && ( leftNeighbour.getSubtypeName().equals("sea") || leftNeighbour.getSubtypeName().equals("brightlight")
                  || leftNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( leftaboveNeighbour != null && ( leftaboveNeighbour.getSubtypeName().equals("sea") || leftaboveNeighbour.getSubtypeName().equals("brightlight")
                  || leftaboveNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( aboveNeighbour != null && ( aboveNeighbour.getSubtypeName().equals("sea") || aboveNeighbour.getSubtypeName().equals("brightlight")
                  || aboveNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( rightaboveNeighbour != null && ( rightaboveNeighbour.getSubtypeName().equals("sea") || rightaboveNeighbour.getSubtypeName().equals("brightlight")
                  || rightaboveNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( rightNeighbour != null && ( rightNeighbour.getSubtypeName().equals("sea") || rightNeighbour.getSubtypeName().equals("brightlight")
                  || rightNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( rightbelowNeighbour != null && ( rightbelowNeighbour.getSubtypeName().equals("sea") || rightbelowNeighbour.getSubtypeName().equals("brightlight")
                  || rightbelowNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }
                if( belowNeighbour != null && ( belowNeighbour.getSubtypeName().equals("sea") || belowNeighbour.getSubtypeName().equals("brightlight")
                  || belowNeighbour.getSubtypeName().equals("deepdarkness")) ) {
                       skip = true;                    
                }                
            }
            
            //Lake in sea fix
            if( terrain.getSubtypeName().equals("lake") )
                if( leftbelowNeighbour != null && ( leftbelowNeighbour.getSubtypeName().equals("sea") ) )
                    if( leftNeighbour != null && ( leftNeighbour.getSubtypeName().equals("sea") ) )               
                        if( leftaboveNeighbour != null && ( leftaboveNeighbour.getSubtypeName().equals("sea") ) )
                            if( aboveNeighbour != null && ( aboveNeighbour.getSubtypeName().equals("sea") ) )                         
                                if( rightaboveNeighbour != null && ( rightaboveNeighbour.getSubtypeName().equals("sea") ) ) 
                                    if( rightNeighbour != null && ( rightNeighbour.getSubtypeName().equals("sea") ) )                    
                                        if( rightbelowNeighbour != null && ( rightbelowNeighbour.getSubtypeName().equals("sea") ) )                   
                                            if( belowNeighbour != null && ( belowNeighbour.getSubtypeName().equals("sea") ) ) {
                                                terrain.setPicture("plainsend");
                                                terrain.setSubtypeName("plains");                    
                                                terrain.setName("Island");
                                                choosePicture(terrain);                                               
                                            }
                                            
             //Lake in sea fix
            if( terrain.getSubtypeName().equals("lake") )
                if( leftbelowNeighbour != null && ( leftbelowNeighbour.getSubtypeName().equals("brightlight") ) )
                    if( leftNeighbour != null && ( leftNeighbour.getSubtypeName().equals("brightlight") ) )               
                        if( leftaboveNeighbour != null && ( leftaboveNeighbour.getSubtypeName().equals("brightlight") ) )
                            if( aboveNeighbour != null && ( aboveNeighbour.getSubtypeName().equals("brightlight") ) )                         
                                if( rightaboveNeighbour != null && ( rightaboveNeighbour.getSubtypeName().equals("brightlight") ) ) 
                                    if( rightNeighbour != null && ( rightNeighbour.getSubtypeName().equals("brightlight") ) )                    
                                        if( rightbelowNeighbour != null && ( rightbelowNeighbour.getSubtypeName().equals("brightlight") ) )                   
                                            if( belowNeighbour != null && ( belowNeighbour.getSubtypeName().equals("brightlight") ) ) {
                                                terrain.setPicture("glowplainsend");
                                                terrain.setSubtypeName("glowplains");                    
                                                terrain.setName("Island");
                                                choosePicture(terrain);                                               
                                            }
                                            
            //Lake in sea fix
            if( terrain.getSubtypeName().equals("lake") )
                if( leftbelowNeighbour != null && ( leftbelowNeighbour.getSubtypeName().equals("deepdarkness") ) )
                    if( leftNeighbour != null && ( leftNeighbour.getSubtypeName().equals("deepdarkness") ) )               
                        if( leftaboveNeighbour != null && ( leftaboveNeighbour.getSubtypeName().equals("deepdarkness") ) )
                            if( aboveNeighbour != null && ( aboveNeighbour.getSubtypeName().equals("deepdarkness") ) )                         
                                if( rightaboveNeighbour != null && ( rightaboveNeighbour.getSubtypeName().equals("deepdarkness") ) ) 
                                    if( rightNeighbour != null && ( rightNeighbour.getSubtypeName().equals("deepdarkness") ) )                    
                                        if( rightbelowNeighbour != null && ( rightbelowNeighbour.getSubtypeName().equals("deepdarkness") ) )                   
                                            if( belowNeighbour != null && ( belowNeighbour.getSubtypeName().equals("deepdarkness") ) ) {
                                                terrain.setPicture("wastelandend");
                                                terrain.setSubtypeName("wasteland");                    
                                                terrain.setName("Island");
                                                choosePicture(terrain);                                               
                                            }
                                            
                 
            if(!skip) {
                if(newWorld.getID()>=3) {
                    if(terrain.getPicture().equals(terrain.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                        if(leftbelowNeighbour != null) {
                            if(leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end")) {
                                other = true;
                                leftbelowNeighbour.setPicture("water");
                                leftbelowNeighbour.setName("");
                                leftbelowNeighbour.setSubtypeName("lake");                    
                            }
                        }             
                        if(leftNeighbour != null) {
                            if(leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                leftNeighbour.setPicture("water");
                                leftNeighbour.setName("");
                                leftNeighbour.setSubtypeName("lake");                    
                            }
                        }             
                        if(leftaboveNeighbour != null) {
                            if(leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                leftaboveNeighbour.setPicture("water");
                                leftaboveNeighbour.setName("");
                                leftaboveNeighbour.setSubtypeName("lake");                      
                            }  
                        }            
                        if(aboveNeighbour != null) {
                            if(aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                aboveNeighbour.setPicture("water");
                                aboveNeighbour.setName("");
                                aboveNeighbour.setSubtypeName("lake");                    
                            }  
                        }             
                        if(rightaboveNeighbour != null) {
                            if(rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightaboveNeighbour.setPicture("water");
                                rightaboveNeighbour.setName("");
                                rightaboveNeighbour.setSubtypeName("lake");                     
                            }  
                        }  
                        if(rightNeighbour != null) {
                            if(rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightNeighbour.setPicture("water");
                                rightNeighbour.setName("");
                                rightNeighbour.setSubtypeName("lake");                     
                            } 
                        }
                        if(rightbelowNeighbour != null) {
                            if(rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightbelowNeighbour.setPicture("water");
                                rightbelowNeighbour.setName("");
                                rightbelowNeighbour.setSubtypeName("lake");                      
                            } 
                        }
                        if(belowNeighbour != null) {
                            if(belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp > 0) ) {
                                other = true;
                                belowNeighbour.setPicture("water");
                                belowNeighbour.setName("");
                                belowNeighbour.setSubtypeName("lake");                     
                            }  
                        }
                    }
            
                    if(other == true) {
                        terrain.setPicture("water");
                        terrain.setName(""); 
                        terrain.setSubtypeName("lake");                 
                    }
                }
                //End if id >=3
                
                if(newWorld.getID()==0) {
                    if(terrain.getPicture().equals(terrain.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                        if(leftbelowNeighbour != null) {
                            if(leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end")) {
                                other = true;
                                leftbelowNeighbour.setPicture("light");
                                leftbelowNeighbour.setName("");
                                leftbelowNeighbour.setSubtypeName("lake");                    
                            }
                        }             
                        if(leftNeighbour != null) {
                            if(leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                leftNeighbour.setPicture("light");
                                leftNeighbour.setName("");
                                leftNeighbour.setSubtypeName("lake");                    
                            }
                        }             
                        if(leftaboveNeighbour != null) {
                            if(leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                leftaboveNeighbour.setPicture("light");
                                leftaboveNeighbour.setName("");
                                leftaboveNeighbour.setSubtypeName("lake");                      
                            }  
                        }            
                        if(aboveNeighbour != null) {
                            if(aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                aboveNeighbour.setPicture("light");
                                aboveNeighbour.setName("");
                                aboveNeighbour.setSubtypeName("lake");                    
                            }  
                        }             
                        if(rightaboveNeighbour != null) {
                            if(rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightaboveNeighbour.setPicture("light");
                                rightaboveNeighbour.setName("");
                                rightaboveNeighbour.setSubtypeName("lake");                     
                            }  
                        }  
                        if(rightNeighbour != null) {
                            if(rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightNeighbour.setPicture("light");
                                rightNeighbour.setName("");
                                rightNeighbour.setSubtypeName("lake");                     
                            } 
                        }
                        if(rightbelowNeighbour != null) {
                            if(rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightbelowNeighbour.setPicture("light");
                                rightbelowNeighbour.setName("");
                                rightbelowNeighbour.setSubtypeName("lake");                      
                            } 
                        }
                        if(belowNeighbour != null) {
                            if(belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp > 0) ) {
                                other = true;
                                belowNeighbour.setPicture("light");
                                belowNeighbour.setName("");
                                belowNeighbour.setSubtypeName("lake");                     
                            }  
                        }
                    }
            
                    if(other == true) {
                        terrain.setPicture("light");
                        terrain.setName(""); 
                        terrain.setSubtypeName("lake");                 
                    }
                }
                //End if id == 0
                
                if(newWorld.getID()==1) {
                    if(terrain.getPicture().equals(terrain.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                        if(leftbelowNeighbour != null) {
                            if(leftbelowNeighbour.getPicture().equals(leftbelowNeighbour.getSubtypeName()+"end")) {
                                other = true;
                                leftbelowNeighbour.setPicture("darkness");
                                leftbelowNeighbour.setName("");
                                leftbelowNeighbour.setSubtypeName("lake");                    
                            }
                        }             
                        if(leftNeighbour != null) {
                            if(leftNeighbour.getPicture().equals(leftNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                leftNeighbour.setPicture("darkness");
                                leftNeighbour.setName("");
                                leftNeighbour.setSubtypeName("lake");                    
                            }
                        }             
                        if(leftaboveNeighbour != null) {
                            if(leftaboveNeighbour.getPicture().equals(leftaboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                leftaboveNeighbour.setPicture("darkness");
                                leftaboveNeighbour.setName("");
                                leftaboveNeighbour.setSubtypeName("lake");                      
                            }  
                        }            
                        if(aboveNeighbour != null) {
                            if(aboveNeighbour.getPicture().equals(aboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                aboveNeighbour.setPicture("darkness");
                                aboveNeighbour.setName("");
                                aboveNeighbour.setSubtypeName("lake");                    
                            }  
                        }             
                        if(rightaboveNeighbour != null) {
                            if(rightaboveNeighbour.getPicture().equals(rightaboveNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightaboveNeighbour.setPicture("darkness");
                                rightaboveNeighbour.setName("");
                                rightaboveNeighbour.setSubtypeName("lake");                     
                            }  
                        }  
                        if(rightNeighbour != null) {
                            if(rightNeighbour.getPicture().equals(rightNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightNeighbour.setPicture("darkness");
                                rightNeighbour.setName("");
                                rightNeighbour.setSubtypeName("lake");                     
                            } 
                        }
                        if(rightbelowNeighbour != null) {
                            if(rightbelowNeighbour.getPicture().equals(rightbelowNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp >0) ) {
                                other = true;
                                rightbelowNeighbour.setPicture("darkness");
                                rightbelowNeighbour.setName("");
                                rightbelowNeighbour.setSubtypeName("lake");                      
                            } 
                        }
                        if(belowNeighbour != null) {
                            if(belowNeighbour.getPicture().equals(belowNeighbour.getSubtypeName()+"end") && (summerTemp > 0 || winterTemp > 0) ) {
                                other = true;
                                belowNeighbour.setPicture("darkness");
                                belowNeighbour.setName("");
                                belowNeighbour.setSubtypeName("lake");                     
                            }  
                        }
                    }
            
                    if(other == true) {
                        terrain.setPicture("darkness");
                        terrain.setName(""); 
                        terrain.setSubtypeName("lake");                 
                    }
                }
                //End if id == 1                
            }
        }
        return newWorld;        
    }
    
    
    private World seedSettlements(World newWorld, int stage)
    {    
        HashMap<Integer,Terrain> terrainMap = newWorld.getTerrainMap();                     
        Set<Integer> terrainSet = terrainMap.keySet();        
        boolean chosen = false;         
        
        //Go through the terrain     
        for(Integer iTemp : terrainSet) {
            Terrain terrain = terrainMap.get(iTemp);
            int id = iTemp;
            float summerTemp = terrain.getTemperature(1);
            float winterTemp = terrain.getTemperature(3);
            int avgRain = (int)(terrain.getRainfall(0)+terrain.getRainfall(1)+terrain.getRainfall(2)+terrain.getRainfall(3))/4;
            
            Terrain test = nearbySettlements(terrainMap, terrain, 5);
            
            if(test == null) {
                if( (!terrain.getSubtypeName().equals("sea")) && (!terrain.getSubtypeName().equals("lake")) ) {
                    if( (besideLake(terrainMap, terrain)) || (terrain.getName().equals("Silt Plain") && (terrain.getRiverName().length() > 0)) ) {
                        if( (summerTemp >= 15 || winterTemp >= 15) && (avgRain >= 100) ) {
                            terrain.setSettlementName("Village");
                            terrain.setPicture("settlement");
                            terrain.setInhabited(true);
                        }
                    }
                }
                
                if( (!terrain.getSubtypeName().equals("light")) && (!terrain.getSubtypeName().equals("brightlight")) && (!terrain.getSubtypeName().equals("lake")) ) {
                    if( (besideLake(terrainMap, terrain)) || (terrain.getName().equals("Water Plain") && (terrain.getRiverName().length() > 0)) ) {
                        if( (summerTemp >= 15 || winterTemp >= 15) && (avgRain >= 100) ) {
                            terrain.setSettlementName("Village");
                            terrain.setPicture("settlement");
                            terrain.setInhabited(true);
                        }
                    }
                }                
 
                 if( (!terrain.getSubtypeName().equals("darkness")) && (!terrain.getSubtypeName().equals("deepdarkness")) && (!terrain.getSubtypeName().equals("lake")) ) {
                    if( (besideLake(terrainMap, terrain)) || (terrain.getName().equals("Wasteland") && (terrain.getRiverName().length() > 0)) ) {
                        if( (summerTemp >= 15 || winterTemp >= 15) && (avgRain >= 100) ) {
                            terrain.setSettlementName("Village");
                            terrain.setPicture("settlement");
                            terrain.setInhabited(true);
                        }
                    }
                }
                
                /*
                if(stage > 0 || chosen == false) {
                    if(terrain.getName().equals("Silt Plain")) {
                        if( (summerTemp >= 10 || winterTemp >= 10) && (avgRain >= 50) ) {
                            terrain.setSettlementName("Village");
                            terrain.setPicture("settlement");                            
                            terrain.setInhabited(true);
                        }
                    }                
                }
            
                if(stage > 1 || chosen == false) {
                    if(terrain.getRiverName().length() > 0 || besideLake(terrainMap, terrain) ) {
                        if( (summerTemp >= 10 || winterTemp >= 10) && (avgRain >= 50) ) {
                            terrain.setSettlementName("Village");
                            terrain.setPicture("settlement");                            
                            terrain.setInhabited(true);
                        }
                    }                
                } 
            
                if(stage > 2 || chosen == false) {
                    if(terrain.getRiverName().length() > 0 || terrain.getPicture().equals(terrain.getSubtypeName()+"end") ) {
                        if( (summerTemp >= 10 || winterTemp >= 10) && (avgRain >= 50) ) {
                            terrain.setSettlementName("Village");
                            terrain.setPicture("settlement");                            
                            terrain.setInhabited(true);
                        }
                    }                
                }*/
            }
            
        }
        
        return newWorld;
    }
    
    
    /**
     * 
     */
    private boolean besideLake(HashMap<Integer,Terrain> terrainMap, Terrain terrain)
    {
        int id = terrain.getID();
        
        Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
        if(leftbelowNeighbour != null) {
            if(leftbelowNeighbour.getSubtypeName().equals("lake")) return true;          
        }             
        Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);
        if(leftNeighbour != null) {
            if(leftNeighbour.getSubtypeName().equals("lake")) return true;  
        }             
        Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);
        if(leftaboveNeighbour != null) {
            if(leftaboveNeighbour.getSubtypeName().equals("lake")) return true;   
        }            
        Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id);
        if(aboveNeighbour != null) {
            if(aboveNeighbour.getSubtypeName().equals("lake")) return true;  
        }             
        Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
        if(rightaboveNeighbour != null) {
            if(rightaboveNeighbour.getSubtypeName().equals("lake")) return true; 
        }            
        Terrain rightNeighbour = getRightNeighbour(terrainMap, id);
        if(rightNeighbour != null) {
            if(rightNeighbour.getSubtypeName().equals("lake")) return true; 
        }
        Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
        if(rightbelowNeighbour != null) {
            if(rightbelowNeighbour.getSubtypeName().equals("lake")) return true; 
        }
        Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);
        if(belowNeighbour != null) {
            if(belowNeighbour.getSubtypeName().equals("lake")) return true; 
        }
        
        return false;
    }
    

    /**
     * 
     */
    private Terrain nearbySettlements(HashMap<Integer,Terrain> terrainMap, Terrain terrain, int iterations)
    {
        int id = terrain.getID();
        
        if(iterations <= 0) {
            return null;
        }        
        
        Terrain leftbelowNeighbour = getLeftBelowNeighbour(terrainMap, id);
        Terrain leftNeighbour = getLeftNeighbour(terrainMap, id);            
        Terrain leftaboveNeighbour = getLeftAboveNeighbour(terrainMap, id);   
        Terrain aboveNeighbour = getAboveNeighbour(terrainMap, id); 
        Terrain rightaboveNeighbour = getRightAboveNeighbour(terrainMap, id);
        Terrain rightNeighbour = getRightNeighbour(terrainMap, id);   
        Terrain rightbelowNeighbour = getRightBelowNeighbour(terrainMap, id);
        Terrain belowNeighbour = getBelowNeighbour(terrainMap, id);        
        
        Terrain found = null;
            
        if(leftbelowNeighbour != null) {
            if(leftbelowNeighbour.getInhabited()) found = leftbelowNeighbour;
            else found = getNearbySettlement(terrainMap, leftbelowNeighbour, iterations-1);
        }
        
        if(leftNeighbour != null && found == null) {
            if(leftNeighbour.getInhabited()) found = leftNeighbour;  
            else found = getNearbySettlement(terrainMap, leftNeighbour, iterations-1);            
        }
        
        if(leftaboveNeighbour != null && found == null) {
            if(leftaboveNeighbour.getInhabited()) found = leftaboveNeighbour;  
            else found = getNearbySettlement(terrainMap, leftaboveNeighbour, iterations-1);
        }
        
        if(aboveNeighbour != null && found == null) {
            if(aboveNeighbour.getInhabited()) found = aboveNeighbour;        
            else found = getNearbySettlement(terrainMap, aboveNeighbour, iterations-1);
        }
        
        if(rightaboveNeighbour != null && found == null) {
             if(rightaboveNeighbour.getInhabited()) found = rightaboveNeighbour; 
             else found = getNearbySettlement(terrainMap, rightaboveNeighbour, iterations-1);
        }
        
        if(rightNeighbour != null && found == null) {
             if(rightNeighbour.getInhabited()) found = rightNeighbour;             
             else found = getNearbySettlement(terrainMap, rightNeighbour, iterations-1);
        }
        
        if(rightbelowNeighbour != null && found == null) {
             if(rightbelowNeighbour.getInhabited()) found = rightbelowNeighbour;  
             else found = getNearbySettlement(terrainMap, rightbelowNeighbour, iterations-1);
        }
        
        if(belowNeighbour != null && found == null) {
             if(belowNeighbour.getInhabited()) found = belowNeighbour;        
             else found = getNearbySettlement(terrainMap, belowNeighbour, iterations-1); 
        }
        
        return found;
    }
    
    /**
     * 
     */
    private Terrain getNearbySettlement(HashMap<Integer,Terrain> terrainMap, Terrain terrain, int iterations)
    {        
        if(iterations <= 0) {
            return null;
        }
        
        if(terrain != null) {
            if(terrain.getInhabited()) return terrain;
            else return nearbySettlements(terrainMap, terrain, iterations-1);
        }
        else {
            return null;
        }
    }
}
