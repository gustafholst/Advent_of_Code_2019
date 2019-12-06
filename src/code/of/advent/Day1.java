package code.of.advent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {
	
	private static int calcFuel(int mass) {
		int fuel = (mass/3) - 2;
		System.out.println(mass);	
		if (fuel <= 0)
			return 0;
		
		return fuel + calcFuel(fuel);
	}

	public static void main(String[] args) {
		
		//Part One
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("module_masses"));
//			
//			int sum = br.lines().map(str -> Integer.parseInt(str))
//					.map(i -> i/3)
//					.map(i -> i - 2)
//					.reduce((tot, i) -> tot + i)
//					.get();
//
//					System.out.println("Fuel needed: " + sum);
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		// Part Two
		try {
			BufferedReader br = new BufferedReader(new FileReader("module_masses"));
			
			int sum = br.lines().map(str -> Integer.parseInt(str))
					.map(Day1::calcFuel)
					.reduce((tot, i) -> tot + i)
					.get();

					System.out.println("Fuel needed: " + sum);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
