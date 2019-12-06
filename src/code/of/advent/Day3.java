package code.of.advent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day3 {
	
	private static final int GRID_WIDTH = 30000;
	private static final int GRID_HEIGHT = 30000;
	
	private static char[][] grid = new char[GRID_WIDTH][GRID_HEIGHT];
	
	private static boolean firstPath = true;
	
	private static void initGrid() {
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				grid[i][j] = '.';
			}
		}
		
		grid[GRID_WIDTH/2][GRID_HEIGHT/2] = 'O';
	}
	
	private static void printGrid() {
		
		try {
			FileOutputStream file = new FileOutputStream("thegrid");
			
			for (int i = 0; i < GRID_HEIGHT; i++) {
				for (int j = 0; j < GRID_WIDTH; j++) {
					System.out.print(grid[j][i]);
					//file.write(grid[j][i]);
				}
				
				//file.write('\n');
				System.out.println();
			}
			
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void markGrid(char dir, int x, int y) {
		if (grid[x][y] != '.') {
			if (firstPath)
				grid[x][y] = 'x';
			else if (grid[x][y] == 'i' || grid[x][y] == '_')
				grid[x][y] = 'x';
			else
				grid[x][y] = 'X';   //collision between the two paths
		}
		else {
			if (dir == '|' && !firstPath)
				dir = 'i';
			if (dir == '-' && !firstPath)
				dir = '_';
			grid[x][y] = dir;
		}
			
	}
	
	private static int distToCentralPort(int x, int y) {
		int xDist = Math.abs(GRID_WIDTH/2 - x);
		int yDist = Math.abs(GRID_HEIGHT/2 - y);
		System.out.println(xDist + yDist);
		return xDist + yDist;
	}
	
	private static void tracePath(List<String> path) {
		
		int currentX = GRID_WIDTH/2; //central port
		int currentY = GRID_HEIGHT/2; 
		
		for (String p : path) {
			
			String dir = p.substring(0,1);
			int steps = Integer.parseInt(p.substring(1));
			
			switch (dir) {
			case "U":
				for (int i = 1; i < steps; i++) {
					markGrid('|', currentX, currentY - i);
				}
				currentY -= steps;
				break;
			case "D":
				for (int i = 1; i < steps; i++) {
					markGrid('|', currentX, currentY + i);
				}
				currentY += steps;
				break;
			case "L":
				for (int i = 1; i < steps; i++) {
					markGrid('-', currentX - i, currentY);
				}
				currentX -= steps;
				break;
			case "R":
				for (int i = 1; i < steps; i++) {
					markGrid('-', currentX + i, currentY);
				}
				currentX += steps;
				break;
			default:
				System.out.println("WTF?!");
			}
			
			markGrid('+', currentX, currentY);
		}

		firstPath = false;
	}
	
	private static int trailDist(List<String> path, int x, int y) {
		
		int currentX = GRID_WIDTH/2; //central port
		int currentY = GRID_HEIGHT/2; 
		
		int total_steps = 0;
		
		for (String step : path) {
			String dir = step.substring(0,1);
			int steps = Integer.parseInt(step.substring(1));
			
			switch (dir) {
			case "U":
				for (int i = 0; i < steps; i++) {
					currentY--;
					total_steps++;
					if (currentX == x && currentY == y)
						return total_steps;
				}			
				break;
			case "D":
				for (int i = 0; i < steps; i++) {
					currentY++;
					total_steps++;
					if (currentX == x && currentY == y)
						return total_steps;
				}
				break;
			case "L":
				for (int i = 0; i < steps; i++) {
					currentX--;
					total_steps++;
					if (currentX == x && currentY == y)
						return total_steps;
				}
				break;
			case "R":
				for (int i = 0; i < steps; i++) {
					currentX++;
					total_steps++;
					if (currentX == x && currentY == y)
						return total_steps;
				}
				break;
			default:
				System.out.println("WTF?!");
			}
		}
		
		return -1;
	}

	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader("wire_paths"));	
		
		String w1 = br.readLine(); // "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51";//"R75,D30,R83,U83,L12,D49,R71,U7,L72"; //"R8,U5,L5,D3"; 
		String w2 = br.readLine(); // "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7";//"U62,R66,U55,R34,D71,R55,D58,R83"; // "U7,R6,D4,L4";
		
		List<String> wire1 = Arrays.asList(w1.split(","));
		List<String> wire2 = Arrays.asList(w2.split(","));
		
		initGrid();   
		
		
		tracePath(wire1);
		tracePath(wire2);
		//printGrid();
		
		// part One
		/*int shortestDist = Integer.MAX_VALUE;
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				if (grid[i][j] == 'X') {
					int dist = distToCentralPort(i, j);
					if (dist < shortestDist) {
						shortestDist = dist;
					}
				}
			}
		}
		
		System.out.println("Closest crossing is at distance: " + shortestDist);*/
		
		// part Two
		int shortestDist = Integer.MAX_VALUE;
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				if (grid[i][j] == 'X') {
					System.out.println("X: " + j + " Y: " + i);
					int dist1 = trailDist(wire1, i, j);
					int dist2 = trailDist(wire2, i, j);
					//System.out.println("Wire 1: " + dist1 + " Wire 2: " + dist2);
					int dist = dist1 + dist2;
					if (dist < shortestDist) {
						shortestDist = dist;
					}
				}
			}
		}
		
		System.out.println("Crossing at the closest combined trail distance is at distance: " + shortestDist);
	}

}
