package code.of.advent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
	
	private static final int PARAMETER = 0;
	private static final int IMMEDIATE = 1;
	
	private static final int ADD = 1;
	private static final int MULT = 2;
	private static final int INPUT = 3;
	private static final int OUTPUT = 4;
	private static final int JIT = 5;
	private static final int JIF = 6;
	private static final int LT = 7;
	private static final int EQ = 8;
	private static final int HALT = 99;

	private static Scanner userInput = new Scanner(System.in);
	
	private static List<Integer> intcode = null;
	
	// 1 ADD	    	four parameters
	// 2 MULT       	four parameters
	// 3 INPUT      	one parameter
	// 4 OUTPUT     	one parameter
	// 5 JUMP-IF-TRUE   three parameters
	// 6 JUMP-IF-FALSE  three parameters
	// 7 LESS-THAN      four parameters
	// 8 EQUALS	        four parameters
	// 99 HALT
	
	private static void runCode() throws Exception {
			
		//run program
		boolean halt = false;
		int instructionPointer = 0;
		
		// for testing part 1
		int numInstructions = 0;
		
		while(!halt) {
			int offset = 0;
			numInstructions++;
			
			int opCode = intcode.get(instructionPointer);
			
			String str_op = String.valueOf(opCode / 100);
			
			int op = opCode % 100;
			opCode /= 100;
			
			int[] modes = {-1, PARAMETER, PARAMETER, PARAMETER};
			
			for (int i = str_op.length() - 1, j = 1; i >= 0; j++, i--) {
				modes[j] = Character.getNumericValue(str_op.charAt(i));
			}
			
			int first = 0, second = 0, third = 0;
			if (op != HALT) {
				first = intcode.get(instructionPointer + 1);
				if (op != INPUT && op != OUTPUT) {
					second = intcode.get(instructionPointer + 2);
					if (op != JIT && op != JIF)
						third = intcode.get(instructionPointer + 3);
				}
			}
			
			int a = 0, b = 0, out = 0;
			a = modes[1] == PARAMETER ? intcode.get(first) : first;
			b = modes[2] == PARAMETER ? intcode.get(second) : second;
			out = modes[3] == PARAMETER ? intcode.get(third) : third;
			
			switch (op) {
			case ADD:			
				System.out.println("ADD " + a + " and " + b + " and store in " + third);	
				intcode.set(third, a + b);
				offset = 4;
				break;
			case MULT:
				intcode.set(third, a * b);
				offset = 4;
				break;
			case INPUT:
				System.out.print("Input: ");
				int input = userInput.nextInt();
				out = first;
				System.out.println("Storing value " + input + " in location " + out);
				intcode.set(out, input);
				
				offset = 2;
				break;
			case OUTPUT:
				System.out.println("Output: " + a);
				
				//for Part 1 only
//				if (a != 0) {
//					System.out.println("\tFAIL at instruction number " + numInstructions + " instruction pointer = " + instructionPointer); 
//					System.out.print("Memory dump: ");
//					printUntil(instructionPointer + 1);
//				}					
//				else 
//					System.out.println();
				
				offset = 2;
				break;	
			case JIT:
				if (a != 0) 
					instructionPointer = b;
				else
					offset = 3;
				break;
			case JIF:
				if (a == 0) 
					instructionPointer = b;
				else
					offset = 3;
				break;
			case LT:
				if (a < b)
					intcode.set(third, 1);
				else
					intcode.set(third, 0);
				offset = 4;
				break;
			case EQ:
				if (a == b)
					intcode.set(third, 1);
				else
					intcode.set(third, 0);
				offset = 4;
				break;
			case HALT:
				halt = true;
				System.out.println("HALT");
				break;
			default:
				throw new Exception("Bad opcode: " + op);
			}
				
			instructionPointer += offset;
		}
	}
	
	private static boolean readIntcodeFromFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("intcode_day5"));
			String line = br.readLine();
			intcode = Stream.of(line.split(","))
					.map(Integer::valueOf)
					.collect(Collectors.toList());
			br.close();
		} catch (Exception e) {
			System.out.println("WTF?!");
			return false;
		} 
		
		return true;
	}
	
	private static void printUntil(int ip) {
		intcode.stream().limit(ip).forEach(n -> System.out.print(n + " "));
		System.out.println();
	}
	
	public static void main(String[] args) {

		if (!readIntcodeFromFile())
			System.exit(0);
		
//		intcode = Arrays.asList(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
//				1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
//				999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99);
		
		try {
			runCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
