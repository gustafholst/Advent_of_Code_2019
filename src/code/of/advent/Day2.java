package code.of.advent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {
	
	private static List<Integer> intcode = null;
	
	private static void readIntcodeFromFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("intcode"));
			String line = br.readLine();
			intcode = Stream.of(line.split(","))
					.map(Integer::valueOf)
					.collect(Collectors.toList());
		} catch (Exception e) {
			System.out.println("WTF?!");
			return;
		}
		
	}
	
	private static void runCode(int noun, int verb) {
		//reset to 1202 program alarm state
		intcode.set(1, noun);
		intcode.set(2, verb);
		
		//run program
		boolean halt = false;
		int currentIndex = 0;
		while(!halt) {
			int opCode = intcode.get(currentIndex);
			if (intcode.get(currentIndex) == 99)
				break;
			
			int a = intcode.get(currentIndex + 1);
			int b = intcode.get(currentIndex + 2);
			int out = intcode.get(currentIndex + 3);
			
			switch (opCode) {
			case 1:
				intcode.set(out, intcode.get(a) + intcode.get(b));
				break;
			case 2:
				intcode.set(out, intcode.get(a) * intcode.get(b));
				break;
			default:
				halt = true;
				break;
			}
				
			currentIndex += 4;
		}
	}

	public static void main(String[] args) {


		// Opcodes
		// 1 add     pos 1 2 <- input , output -> 3
		// 2 mult
		// 99 halt

		
		//List<Integer> intcode = Arrays.asList(1,1,1,4,99,5,6,0,99);
		
		//Part 1
		readIntcodeFromFile();	
		runCode(12,2);
		
		System.out.println("Position 0: " + intcode.get(0));
		
		// Part 2
		boolean found = false;
		
		int n = 0, v = 0;
		for (int noun = 0; noun < 100 && !found; noun++) {
			for (int verb = 0; verb < 100 && !found; verb++) {
				readIntcodeFromFile();
				runCode(noun, verb);
				if (intcode.get(0) == 19690720) {
					System.out.println("Found!");
					n = noun;
					v = verb;
					found = true;
				}		
			}
		}
		
		//System.out.println(intcode.stream().map(i -> i.toString()).collect(Collectors.joining(",")));	
		
		System.out.println("Noun = " + n + " and Verb = " + v);
		System.out.println("Answer: " + (100 * n + v));
	}

}
