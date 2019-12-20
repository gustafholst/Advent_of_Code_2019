package code.of.advent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day10 {
	
	private static List<List<Character>> grid;
	private static List<Asteroid> asteroids = new ArrayList<>();
	
	private static int width;
	private static int height;
	
	private static void printGrid() {
		
		for(int y = 0; y < grid.size(); y++) {
			for(int x = 0; x < grid.get(y).size(); x++) {
				System.out.print(grid.get(y).get(x));
			}
			System.out.println();
		}
		System.out.println();
	}
	

	private static class Vector {
		public int x, y;
		
		public Vector(int x, int y) {
			this.x = x;
			this.y = y;
			
			normalize();
		}
		
		static int gcd(int a, int b) 
	    { 
	        if (a == 0) 
	            return b; 
	              
	        return gcd(b % a, a); 
	    } 
		
		public void normalize() {
			if (this.x == 0) {
				this.y = (int)Math.signum(this.y);
			}
			else if (this.y == 0) {
				this.x = (int)Math.signum(this.x);
			}
				
			else {
				int absX = Math.abs(this.x);
				int absY = Math.abs(this.y);
				
				int denom = gcd(absX, absY);
				
				absX /= denom;
				absY /= denom;
				
				this.x = absX * (int)Math.signum(this.x);
				this.y = absY * (int)Math.signum(this.y);		
			}
		}
		
		public String toString() {
			return "[" + this.x + "," + this.y + "]";
		}
	}
	
	private static class Position {
		public int x, y;
		
		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		private boolean isAsteroid() {
			return !grid.get(this.y).get(this.x).equals('.');
		}
		
		private boolean notOnGrid() {
			return this.x < 0 || this.x > width || this.y < 0 || this.y > height;
		}
		
		private boolean equals(Asteroid a) {
			return this.x == a.x && this.y == a.y;
		}
		
		public Vector dirBetween(Position other) {
			return new Vector(other.x - this.x, other.y - this.y);
		}
	}
	
	private static class Asteroid {
		
		public int x, y;
		
		public int visibleAsteroids = 0;
		
		public double angle;
		
		public Asteroid(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		private boolean canSee(Asteroid other) {

			Vector direction = new Vector(other.x - this.x, other.y - this.y);
			direction.normalize();
			
			int steps = 1;
			while (true) {
				Position next = new Position(this.x + direction.x * steps, this.y + direction.y *steps);
				if (next.notOnGrid()) {
					return false;
				}
				
				if (next.equals(other)) {
					return true;
				}
				
				if (next.isAsteroid()) {
					return false;
				}
				steps++;
			}
		}
		
		public void markVisibleNumberOfAsteroid(int num) {
			grid.get(this.y).set(this.x, Character.forDigit(num, 10));
		}
		
		public boolean equals(Asteroid other) {
			return this.x == other.x && this.y == other.y;
		}
		
		public void hit() {
			grid.get(this.y).set(this.x, '.');
		}
		
		public String toString() {
			return "[" + this.x + "," + this.y + "] angle = " + this.angle;
		}
	}
	
	private static class GiantLaser {

		private Position position;
		private List<Asteroid> visibleAsteroids = new ArrayList<>();
		
		public int hitCount = 0;
		
		public GiantLaser(Position pos) {
			this.position = pos;
		}
		
		public void shoot(Asteroid a) {
			a.hit();
		}
		
		public void findVisibleAsteroids() {
			visibleAsteroids.clear();
			
			Asteroid home = asteroids.stream()
					.filter(a -> a.x == this.position.x && a.y == this.position.y)
					.findAny().get();

			for (Asteroid other : asteroids) {
				if (other.equals(home)) continue;
	
				if (home.canSee(other)) {
					double angle = Math.atan2(other.y - home.y, other.x - home.x);
					other.angle = angle + Math.PI;
					visibleAsteroids.add(other);	
				}
			}
		
			visibleAsteroids = visibleAsteroids.stream()
			.sorted((a,b) -> Double.compare(a.angle, b.angle))
			.collect(Collectors.toList());
		}
		
		public void shootOneRotation() {
			findVisibleAsteroids();
			
			int index = 0;
			for (int i = 0; i < visibleAsteroids.size(); i++) {
				if (visibleAsteroids.get(i).angle > Math.PI/2) {
					index = i-1;
					break;
				}
			}
			
			Collections.rotate(visibleAsteroids, -index);
				
			for (Asteroid target : visibleAsteroids) {
				shoot(target);				
				hitCount++;			
				System.out.println("Hit #" + hitCount + " was asteroid: " + target);
			}
		}
	}

	public static void main(String[] args) {
		
		grid = new ArrayList<>();
			
		try {
			Files.lines(Paths.get("asteroids"))
			.map(str -> Arrays.stream(str.split("")).map(s -> s.charAt(0)).collect(Collectors.toList()))		
			.forEach(list -> grid.add(list));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		width = grid.get(0).size();
		height = grid.size();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (grid.get(i).get(j) != '.') {
					asteroids.add(new Asteroid(j, i));
				}				
			}
		}	
	
		printGrid();
		
		for (Asteroid a : asteroids) {
			
			for (Asteroid other : asteroids) {
				if (a.equals(other)) continue;
				
				if (a.canSee(other)) {
					a.visibleAsteroids++;
				}
			}
			//a.markVisibleNumberOfAsteroid(total);
		}
		
		Asteroid winner = asteroids.stream()
		.sorted((a,b) -> Integer.compare(b.visibleAsteroids,a.visibleAsteroids))
				.findFirst().get();
		
		//part 1
		System.out.println("Winning asteroid: " + winner + " with " + winner.visibleAsteroids + " asteroids detected" );
		
		
		//part 2
		GiantLaser laser = new GiantLaser(new Position(winner.x, winner.y));
		
		laser.shootOneRotation();
		
	}

}
