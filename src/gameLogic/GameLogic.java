package gameLogic;

/**
 * GameLogic
 * 
 * @author  : Robin Osborne
 * @version : Version 0.1, 2012-03-07
 * 
 */

import GUI.WorldMap;
import modelTypes.World;

public class GameLogic
{
    private WorldGenerator worldGenerator;
    private WorldMap worldMap;
    
    //No argument constructor for GameLogic
    public GameLogic()
    {
        worldGenerator = new WorldGenerator();
        worldMap = new WorldMap();

        //World celestialWorld = worldGenerator.generateCelestialTerrain();       
        //worldMap.showMap(celestialWorld);         

        //World infernalWorld = worldGenerator.generateInfernalTerrain();       
        //worldMap.showMap(infernalWorld);         
        
        World standardWorld = worldGenerator.generateStandardTerrain();       
        worldMap.showMap(standardWorld);        
        
        while(true) {
        }
    }
    
    public static void main (String[] args)
    {
    	GameLogic gameLogic = new GameLogic();
    }
}
