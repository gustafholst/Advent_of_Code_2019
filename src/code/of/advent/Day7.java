package code.of.advent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {

	private static class IntCodeComputer {
		
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
		
		private List<Integer> intcode = null;
		private boolean phaseSet = false;
		
		int instructionPointer;
		
		private int phase;
		private IntCodeComputer outComputer;
		
		private boolean halted = false;
		
		public int output = 0;
		
		public IntCodeComputer(List<Integer> code, int phase) {
			intcode = code;
			this.phase = phase;
			
			instructionPointer = 0;
		}
		
		public void setOutComputer(IntCodeComputer out) {
			outComputer = out;
		}
		
		private int runCode(int input) throws Exception {
			
			while(!halted) {

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
					instructionPointer += 4;
					break;
				case MULT:
					intcode.set(third, a * b);
					instructionPointer += 4;
					break;
				case INPUT:
					out = first;
					System.out.println("Storing value " + input + " in location " + out);
					if (!phaseSet) {
						intcode.set(out, phase);
						phaseSet = true;
					}
					else 
						intcode.set(out, input);
					
					instructionPointer += 2;
					break;
				case OUTPUT:
					System.out.println("Output: " + a);
					output = a;
					instructionPointer += 2;
					outComputer.runCode(output);
					halted = true;
					break;	
				case JIT:
					if (a != 0) 
						instructionPointer = b;
					else
						instructionPointer += 3;
					break;
				case JIF:
					if (a == 0) 
						instructionPointer = b;
					else
						instructionPointer += 3;
					break;
				case LT:
					if (a < b)
						intcode.set(third, 1);
					else
						intcode.set(third, 0);
					instructionPointer += 4;
					break;
				case EQ:
					if (a == b)
						intcode.set(third, 1);
					else
						intcode.set(third, 0);
					instructionPointer += 4;
					break;
				case HALT:
					halted = true;
					System.out.println("HALT");
					break;
				default:
					throw new Exception("Bad opcode: " + op);
				}
			}
			
			return output;
		}
	}
	
	private static List<Integer> readIntcodeFromFile() {
		List<Integer> list;
		try {
			BufferedReader br = new BufferedReader(new FileReader("intcode_day7"));
			String line = br.readLine();
			list = Stream.of(line.split(","))
					.map(Integer::valueOf)
					.collect(Collectors.toList());
			br.close();
		} catch (Exception e) {
			System.out.println("WTF?!");
			return null;
		} 
		
		return list;
	}
	
	private static long runSimulation(int[] phaseSettings) {
		IntCodeComputer[] computers = new IntCodeComputer[5];
		for (int i = 0; i < computers.length; i++)
			computers[i] = new IntCodeComputer(readIntcodeFromFile(), phaseSettings[i]);
		
		computers[0].setOutComputer(computers[1]);
		computers[1].setOutComputer(computers[2]);
		computers[2].setOutComputer(computers[3]);
		computers[3].setOutComputer(computers[4]);
		computers[4].setOutComputer(computers[0]);
		
		int input = 0;

		try {
			input = computers[0].runCode(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return computers[4].output;
	}
	
	private static void swap(int[] input, int a, int b) {
	    int tmp = input[a];
	    input[a] = input[b];
	    input[b] = tmp;
	}
	
	public static void main(String[] args) {
		
		ArrayList<Long> results = new ArrayList<Long>();
		
		int[] elements = {5, 6, 7, 8, 9};	
		int n = elements.length;
		int[] indexes = new int[n];
		
		for (int i = 0; i < n; i++) {
		    indexes[i] = 0;
		}
		
		results.add(runSimulation(elements));
		 
		int i = 0;
		while (i < n) {
		    if (indexes[i] < i) {
		        swap(elements, i % 2 == 0 ?  0: indexes[i], i);
		      
		        results.add(runSimulation(elements));
		        
		        indexes[i]++;
		        i = 0;
		    }
		    else {
		        indexes[i] = 0;
		        i++;
		    }
		}
		
		long max = Collections.max(results);
		
		System.out.println("Max thruster signal: " + max);

	}

}
