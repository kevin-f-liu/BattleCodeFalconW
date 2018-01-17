import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bc.*;

public class Player {	
	public static char[][] fetchMapData(Planet p, GameController gc) {
		PlanetMap m = gc.startingMap(p);
		long width = m.getWidth();
		long height = m.getHeight();
		char[][] map = new char[(int) height][(int) width];
		for (int i = 0; i < (int) height; i++) {
			for (int j = 0; j < (int) width; j++) {
				MapLocation tmp = new MapLocation(gc.planet(), j, i);
				map[i][j] = m.initialKarboniteAt(tmp) > 0 ? 'b' : '0';
				if (!(m.isPassableTerrainAt(tmp) == 1)) {
					map[i][j] = '1';
				}
			}
		}
		return map;
	}
	
	public static ArrayList<int[]> getNeighbours(int x, int y, int height, int width) {
		// Get every walkable neighbour
		ArrayList<int[]> neighbours = new ArrayList<int[]>();
		
		for (int i = y - 1; i <= y + 1; i ++) {
			for (int j = x - 1; j <= x + 1; j ++) {
				if (j != x || i != y) {
					if (j >= 0 && i >= 0 && i < height && j < width) {
						neighbours.add(new int[] {i, j});
					}
				}
			}
		}
		
		return neighbours;
	}
	
	public static MapLocation breadthFirstSearchMap(GameController gc, char[][] map, char target, int x, int y) {
		// Hack with arrays
		// TODO: Make "node" universal.. with different uses?
		ArrayDeque<int[]> openSet = new ArrayDeque<int[]>();
		openSet.add(new int[] {x, y});
		
		while (!openSet.isEmpty()) {
			int[] temp = openSet.remove();
			if (map[temp[1]][temp[0]] == target) {
				return new MapLocation(gc.planet(), temp[0], temp[1]);
			}
			ArrayList<int[]> neighbours = getNeighbours(temp[0], temp[1], map.length, map[0].length);
			for (int[] n : neighbours) {
				if (!openSet.contains(n)) {
					openSet.add(new int[] {n[0], n[1]});
				}
			}
		}
			
		// Nothing found?
		return null;
	}

	public static void main(String[] args) {
        // Connect to the manager, starting the game
        GameController gc = new GameController();
        // Fetch the map of the current planet and store it in an array
 		char[][] currentMap = fetchMapData(gc.planet(), gc);
 		for (char[] a : currentMap) {
 			System.out.println(a);
 		}
 		
 		HashMap<Integer, PathFinder> pathFinders = new HashMap<Integer, PathFinder>();

        while (true) {
            System.out.println("Current round: "+gc.round());
            
            
            VecUnit myUnits = gc.myUnits();
            for (int i = 0; i < myUnits.size(); i++) {
            	Unit unit = myUnits.get(i);
            	MapLocation unitMapLocation = unit.location().mapLocation();
            	System.out.println(unit.id() + ": " + unitMapLocation);
            	int[] unitCoord = new int[] {unitMapLocation.getX(), unitMapLocation.getY()};
            	if (unit.unitType() == UnitType.Worker) {
            		// Get the worker's pathfinder
            		PathFinder pf;
            		if (!pathFinders.containsKey(new Integer(unit.id()))) {
            			// Add to map if new unit
            			System.out.println(unit.id() + ": new pathfinder");
            			pf = new PathFinder(currentMap);
            			pathFinders.put(new Integer(unit.id()), pf);
            		} else {
            			System.out.println(unit.id() + ": old pathfinder");
            			pf = pathFinders.get(new Integer(unit.id()));
            		}
            		
            		pf.target(unitCoord[0], unitCoord[1], 0, 0);

            		Direction next = pf.nextStep();
        			System.out.println(unit.id() + ": move direction " + next);
        			System.out.println(unit.id() + ": " + next + " | " + gc.isMoveReady(unit.id()) + " | " + gc.canMove(unit.id(), next));
        			if (next != null && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), next)) {
        				gc.moveRobot(unit.id(), next);
        				pf.advanceStep();
            			System.out.println(unit.id() + ": moved to " + unit.location().mapLocation());
        			}
            		
//            		// See if the worker is standing on karbonite, if it is, mine it
//            		boolean mining = false;
//            		if (gc.karboniteAt(unitMapLocation) > 0) {
//            			System.out.println(unit.id() + ": Mining karbonite");
//            			mining = true;
//            			gc.harvest(unit.id(), Direction.Center);
//            		}
//            		
//            		// Find the nearest target if not mining
//            		if (!mining) {
//            			if (!pf.isTargeting()) {
//                			MapLocation target = breadthFirstSearchMap(gc, currentMap, 'b', unitCoord[0], unitCoord[1]);
//                			System.out.println(unit.id() + ": new target: " + target);
//                			pf.target(unitCoord[0], unitCoord[1], target.getX(), target.getY());
//                		}
//            			
//            			// Move the unit if it didn't mine, either new target or old
//            			Direction next = pf.advanceStep();
//            			System.out.println(unit.id() + ": move direction " + next);
//            			if (next != null && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), next)) {
//            				gc.moveRobot(unit.id(), next);
//                			System.out.println(unit.id() + ": moved to " + unit.location().mapLocation());
//            			}
//            		}
            	}
            	
            	
            	
            	
            	
            	
            	
            	
            }
            
            // foo() => Get data
            // foo2() => Workers move
            // foo3() => Workers Action
            // foo4() => 
            // foo4() => Rangers Move
            // foo5() => Rangers Action
            // 
            // endturn()
            
            
            
            
            
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}
