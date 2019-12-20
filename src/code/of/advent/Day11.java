package code.of.advent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
	
	final static int WIDTH = 200;
	final static int HEIGHT = 200;
	private static Panel[][] panels = new Panel[HEIGHT][WIDTH];
	private static Robot R2D2;

private static class IntCodeComputer {
		
		private static final int PARAMETER = 0;
		private static final int IMMEDIATE = 1;
		private static final int RELATIVE = 2;
		
		private static final int ADD = 1;
		private static final int MULT = 2;
		private static final int INPUT = 3;
		private static final int OUTPUT = 4;
		private static final int JIT = 5;
		private static final int JIF = 6;
		private static final int LT = 7;
		private static final int EQ = 8;
		private static final int RB = 9;
		private static final int HALT = 99;
		
		private List<BigInteger> intcode = null;
		
		private int instructionPointer = 0;	
		private int relativeBase = 0;
		private boolean halted = false;
		private BigInteger output = BigInteger.ZERO;
		
		public IntCodeComputer(List<BigInteger> code) {
			int size = code.size();
			intcode = code;	
			
			for (int i = 0; i < size*2; i++)
				intcode.add(BigInteger.ZERO);
		}
		
		public BigInteger fetchMemory(int mode, BigInteger param) {
			BigInteger mem = BigInteger.ZERO;
			if (mode == PARAMETER)
				return intcode.get(param.intValue());
			
			if (mode == IMMEDIATE)
				return param;
			
			if (mode == RELATIVE)
				return intcode.get(relativeBase + param.intValue());
			
			return mem;
		}
		
		public int getPosition(int mode, BigInteger param) {
			if (mode == RELATIVE)
				return relativeBase + param.intValue();
			
			return param.intValue();
		}
		
		private BigInteger runCode() throws Exception {
			
			while(!halted) {

				int opCode = intcode.get(instructionPointer).intValue();
				
				String str_op = String.valueOf(opCode / 100);
				
				int op = opCode % 100;
				opCode /= 100;
				
				int[] modes = {-1, PARAMETER, PARAMETER, PARAMETER};
				
				for (int i = str_op.length() - 1, j = 1; i >= 0; j++, i--) {
					modes[j] = Character.getNumericValue(str_op.charAt(i));
				}
				
				BigInteger first = BigInteger.ZERO, second = BigInteger.ZERO, third = BigInteger.ZERO;
				if (op != HALT) {
					first = intcode.get(instructionPointer + 1);
					if (op != INPUT && op != OUTPUT && op != RB) {
						second = intcode.get(instructionPointer + 2);
						if (op != JIT && op != JIF)
							third = intcode.get(instructionPointer + 3);
					}
				}
				
				BigInteger a = BigInteger.ZERO, b = BigInteger.ZERO;
				int out = 0;
					
				a = fetchMemory(modes[1], first);
				b = fetchMemory(modes[2], second);
				out = getPosition(modes[3], third); //fetchMemory(modes[3], third).intValue();
				
				switch (op) {
				case ADD:			
					//System.out.println("ADD " + a + " and " + b + " and store in " + out);	
					intcode.set(out, a.add(b));
					instructionPointer += 4;
					break;
				case MULT:
					//System.out.println("MULT " + a + " and " + b + " and store in " + out);
					intcode.set(out, a.multiply(b));
					instructionPointer += 4;
					break;
				case INPUT:
					out = getPosition(modes[1], first);
					BigInteger input = BigInteger.valueOf(R2D2.readColor());
					//System.out.println("Storing value " + input + " in location " + out);
					intcode.set(out, input);			
					instructionPointer += 2;
					break;
				case OUTPUT:
					output = a;
					instructionPointer += 2;
					R2D2.handleOutput(output.intValue());
					System.out.println("Output: " + output);
					break;	
				case JIT:
					//System.out.println("JIT");
					if (!a.equals(BigInteger.ZERO)) 
						instructionPointer = b.intValue();
					else
						instructionPointer += 3;
					break;
				case JIF:
					//System.out.println("JIF");
					if (a.equals(BigInteger.ZERO)) 
						instructionPointer = b.intValue();
					else
						instructionPointer += 3;
					break;
				case LT:
					//System.out.println("LT");
					if (a.compareTo(b) < 0)
						intcode.set(out, BigInteger.ONE);
					else
						intcode.set(out,BigInteger.ZERO);
					instructionPointer += 4;
					break;
				case EQ:
					//System.out.println("EQ");
					if (a.equals(b))
						intcode.set(out, BigInteger.ONE);
					else
						intcode.set(out,BigInteger.ZERO);
					instructionPointer += 4;
					break;
				case RB:		
					relativeBase += a.intValue();
					//System.out.println("RB = " + relativeBase);
					instructionPointer += 2;
					break;
				case HALT:
					halted = true;
					//System.out.println("HALT");
					break;
				default:
					throw new Exception("Bad opcode: " + op);
				}
			}
			
			return output;
		}
	}

	private static List<BigInteger> readIntcodeFromFile() {
		List<BigInteger> list;
		try {
			BufferedReader br = new BufferedReader(new FileReader("intcode_day11"));
			String line = br.readLine();
			list = Stream.of(line.split(","))
					.map(Long::valueOf)
					.map(BigInteger::valueOf)
					.collect(Collectors.toList());
			br.close();
		} catch (Exception e) {
			System.out.println("WTF?!");
			return null;
		} 
		
		return list;
	}
	
	private static void displayPanels(Robot robot) {
		for (int i = 0; i < panels.length; i++) {
			for (int j = 0; j < panels[i].length; j++) {
				Panel p = panels[i][j];
				System.out.print(p.hasRobot(robot) ? robot.direction : p.color);
			}
			System.out.println();
		}
	}
	
	private static void initializePanels() {
		for (int i = 0; i < panels.length; i++) {
			for (int j = 0; j < panels[i].length; j++) {
				panels[i][j] = new Panel(j, i);
			}
		}
	}
	
	private static class Panel {
		
		int x, y;
		boolean painted = false;
		char color = '.';
		
		public boolean hasRobot(Robot robot) {
			return robot.x == this.x && robot.y == this.y;
		}
		
		public Panel(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void paint(char color) {
			this.color = color;
			painted = true;
		}
	}
	
	private static class Robot {
		
		public static final char DOWN = 'v';
		public static final char LEFT = '<';
		public static final char RIGHT = '>';
		public static final char UP = '^';
		
		public int x, y;
		public boolean paint = true;
		public char direction = UP; 
		
		public Robot(int x, int y) {
			this.x = x;
			this.y = y;
			
			//for part 2 only (start robot on a white panel)
			panels[this.y][this.y].color = '#';
		}
		
		public int readColor() {
			Panel currentPanel = panels[this.y][this.x];
			if (currentPanel.color == '.')
				return 0;
			else 
				return 1;
		}
		
		public void handleOutput(int instruction) {
			if (paint) {
				panels[this.y][this.x].paint(instruction == 0 ? '.' : '#');
				paint = false;
			}
			else {
				if (instruction == 0) {
					turnLeft();
				}
				else {
					turnRight();
				}
				
				paint = true;
			}
		}
		
		private void turnLeft() {
			switch (direction) {
			case UP:
				direction = LEFT;
				this.x--;
				break;
			case DOWN:
				direction = RIGHT;
				this.x++;
				break;
			case LEFT:
				direction = DOWN;
				this.y++;
				break;
			case RIGHT:
				direction = UP;
				this.y--;
				break;
			}
		}
		
		private void turnRight() {
			switch (direction) {
			case UP:
				direction = RIGHT;
				this.x++;
				break;
			case DOWN:
				direction = LEFT;
				this.x--;
				break;
			case LEFT:
				direction = UP;
				this.y--;
				break;
			case RIGHT:
				direction = DOWN;
				this.y++;
				break;
			}
		}
	}

	public static void main(String[] args) {
		
		List<BigInteger> code = readIntcodeFromFile();
		
		IntCodeComputer computer = new IntCodeComputer(code);
		
		initializePanels();
		
		R2D2 = new Robot(WIDTH/2, HEIGHT/2);
		
		try {
			computer.runCode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		displayPanels(R2D2);

		int numPainted = 0;
		
		for (int i = 0; i < panels.length; i++) {
			for (int j = 0; j < panels[i].length; j++) {
				Panel p = panels[i][j];
				if (p.painted) {
					numPainted++;
				}
			}
			System.out.println();
		}
		
		//Part 1
		System.out.println("Number of panels painted at least once: " + numPainted);
	}
}
