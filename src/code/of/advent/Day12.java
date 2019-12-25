package code.of.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day12 {
	
	private static List<Moon> originalState;
	private static List<Moon> moons;
	private static int numSteps = 0;
	
	private static class Vector3D {
		int x,y,z;
		
		public Vector3D(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public boolean equals(Vector3D other) {
			return this.x == other.x && this.y == other.y && this.z == other.z;
		}
		
		public boolean xEquals(Vector3D other) {
			return this.x == other.x;
		}
		
		public boolean yEquals(Vector3D other) {
			return this.y == other.y;
		}
		
		public boolean zEquals(Vector3D other) {
			return this.z == other.z;
		}
		
		public Vector3D add(Vector3D other) {
			return new Vector3D(this.x+ other.x, this.y + other.y, this.z + other.z);
		}
	}
	
	private static class Moon {
		Vector3D position;
		Vector3D velocity;
		
		public Moon(Vector3D startPos) {
			position = startPos;
			velocity = new Vector3D(0,0,0);
		}
		
		public Moon(Moon m) {
			this.position = new Vector3D(m.position.x, m.position.y, m.position.z);
			this.velocity = new Vector3D(m.velocity.x, m.velocity.y, m.velocity.z);
		}
		
		public void move() {
			position = position.add(velocity);
		}
		
		public int calcPotentialEnergy() {
			return Math.abs(this.position.x) + Math.abs(this.position.y) +Math.abs(this.position.z);
		}
		
		public int calcKineticEnergy() {
			return Math.abs(this.velocity.x) + Math.abs(this.velocity.y) +Math.abs(this.velocity.z);
		}
		
		public int calcTotalEnergy() {
			return calcPotentialEnergy() * calcKineticEnergy();
		}
		
		public boolean matches(Moon other) {
			return this.position.equals(other.position) && this.velocity.equals(other.velocity); 
		}
		
		public boolean matchesDimension(Moon other, String dim) {

			if (dim.equals("x")) {
				return this.position.xEquals(other.position) && this.velocity.xEquals(other.velocity);
			}
			if (dim.equals("y")) {
				return this.position.yEquals(other.position) && this.velocity.yEquals(other.velocity);
			}
			if (dim.equals("z")) {
				return this.position.zEquals(other.position) && this.velocity.zEquals(other.velocity);
			}

			return false;
		}
		
		
		public void display() {
			System.out.printf("pos=<x=%2d, y=%2d, z=%2d>, vel=<x=%2d, y=%2d, z=%2d>\n", 
					this.position.x, this.position.y, this.position.z,
					this.velocity.x, this.velocity.y, this.velocity.z);
		}
	}
	
	private static void readMoonsFromFile(String filename) {
		
		originalState = new ArrayList<>();
		
		try {
			List<String> lines = Files.readAllLines(Paths.get(filename));
			
			int index = 0;
			for (String l : lines ) {
				int x,y,z;
				index = l.indexOf("x=");
				l = l.substring(index+2);
				index = l.indexOf(", ");
				x = Integer.parseInt(l.substring(0,index));
				
				index = l.indexOf("y=");
				l = l.substring(index+2);
				index = l.indexOf(", ");
				y = Integer.parseInt(l.substring(0,index));
				
				index = l.indexOf("z=");
				l = l.substring(index+2);
				index = l.indexOf(">");
				z = Integer.parseInt(l.substring(0,index));
				
				
				originalState.add(new Moon(new Vector3D(x,y,z)));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void showMoons() {
		System.out.println("After " + numSteps + " steps:");
		for (Moon m : moons)
			m.display();
		
		System.out.println();
	}
	
	public static void gravitate(Moon a, Moon b) {
		if (a.position.x < b.position.x) {
			a.velocity.x++;
			b.velocity.x--;
		}
		if (a.position.x > b.position.x) {
			a.velocity.x--;
			b.velocity.x++;
		}
		
		if (a.position.y < b.position.y) {
			a.velocity.y++;
			b.velocity.y--;
		}
		if (a.position.y > b.position.y) {
			a.velocity.y--;
			b.velocity.y++;
		}
		
		if (a.position.z < b.position.z) {
			a.velocity.z++;
			b.velocity.z--;
		}
		if (a.position.z > b.position.z) {
			a.velocity.z--;
			b.velocity.z++;
		}
	}
	
	public static void update() {
		
		//calc and adjust velocity pairwise
		for (int i = 0; i < moons.size(); i++) {
			for (int j = i+1; j < moons.size(); j++) {
				gravitate(moons.get(i), moons.get(j));
			}
		}
		
		//apply velocity (add to position)
		for (Moon m : moons) {
			m.move();
		}
		
		numSteps++;
	}
	
	public static int calcSystemEnergy() {
		return moons.stream().map(m -> m.calcTotalEnergy()).reduce(0, (tot, m) -> tot + m);
	}
	
	public static boolean matchesOriginalState(int moon) {
		for (int i = 0; i < originalState.size(); i++) {
			if (!moons.get(moon).matches(originalState.get(moon))) {
				return false;
			}
		}
		
		return true;
	}

	public static void part1() {
		showMoons();
		
		for (int i = 0; i < 1000; i++) {
			update();
			//showMoons();
		}
		
		showMoons();
		
		int totalEnergy = calcSystemEnergy();
		System.out.println("Total Solar System Energy = " + totalEnergy);
	}
	
	public static void part2() {
		
		numSteps = 0;
		//showMoons();
		
		int xPeriod = 0;
		int yPeriod = 0;
		int zPeriod = 0;

		boolean xFound = false;
		boolean yFound = false;
		boolean zFound = false;
		while (!xFound || !yFound || !zFound) {
			update();
			
			boolean xMatch = true;
			boolean yMatch = true;
			boolean zMatch = true;
			for (int j = 0; j < originalState.size(); j++) {
				if (!moons.get(j).matchesDimension(originalState.get(j), "x")) {
					xMatch = false;			
				}
				if (!moons.get(j).matchesDimension(originalState.get(j), "y")) {
					yMatch = false;
				}
				if (!moons.get(j).matchesDimension(originalState.get(j), "z")) {
					zMatch = false;					
				}
			}
			
			if (xMatch && !xFound) {
				xFound = true;
				xPeriod = numSteps;
				System.out.println("Position x axis has period = " + numSteps);
			}
		  	if (yMatch && !yFound) {
		  		yFound = true;
		  		yPeriod = numSteps;
		  		System.out.println("Position y axis has period = " + numSteps);
		  	}
		  	if (zMatch && !zFound) {
		  		zFound = true;
		  		zPeriod = numSteps;
		  		System.out.println("Position z axis has period = " + numSteps);
		  	}
		}
		
		Map<Integer, Integer> xFactors = getPrimeFactors(xPeriod);
		Map<Integer, Integer> yFactors = getPrimeFactors(yPeriod);
		Map<Integer, Integer> zFactors = getPrimeFactors(zPeriod);
		
		Map<Integer, Integer> lcmMap = new HashMap<>();
		
		updateLcmMap(lcmMap, xFactors);
		updateLcmMap(lcmMap, yFactors);
		updateLcmMap(lcmMap, zFactors);
		
		long lcm = 1;
		
		for (Map.Entry<Integer, Integer> entry : lcmMap.entrySet()) {		
			for (int i = 0; i < entry.getValue(); i++) {
				lcm *= entry.getKey();
			}	
		}
		
		System.out.println("Number of steps until repeat = " + lcm);
	}
	
	public static void updateLcmMap(Map<Integer, Integer> lcmMap, Map<Integer, Integer> factors) {
		for (Map.Entry<Integer, Integer> f : factors.entrySet()) {
			int factor = f.getKey();
			int power = f.getValue();
			if (lcmMap.containsKey(factor)) {
				if (power > lcmMap.get(factor)) {
					lcmMap.put(factor, power);
				}
			}
			else {
				lcmMap.put(factor, power);
			}
		}
	}
	
	public static Map<Integer, Integer> getPrimeFactors(int number) {
	    int absNumber = Math.abs(number);
	 
	    Map<Integer, Integer> primeFactorsMap = new HashMap<Integer, Integer>();
	 
	    for (int factor = 2; factor <= absNumber; factor++) {
	        while (absNumber % factor == 0) {
	            Integer power = primeFactorsMap.get(factor);
	            if (power == null) {
	                power = 0;
	            }
	            primeFactorsMap.put(factor, power + 1);
	            absNumber /= factor;
	        }
	    }
	 
	    return primeFactorsMap;
	}
	
	public static void main(String[] args) {
		
		readMoonsFromFile("moons");
		
		moons = new ArrayList<>();
		for (Moon m : originalState) {
			moons.add(new Moon(m));
		}
		
		//part1();
		
		part2();
		
	}

}
