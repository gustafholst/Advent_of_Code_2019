package code.of.advent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 {

	final static int WIDTH = 42;
	final static int HEIGHT = 24;
	private static Tile[][] tiles;

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
		
		private int numOutput = 0;
		private int tempX = 0;
		private int tempY = 0;
		private int tempId = 0;
		
		public IntCodeComputer(List<BigInteger> code) {
			int size = code.size();
			intcode = code;	

			for (int i = 0; i < size*2; i++)
				intcode.add(BigInteger.ZERO);
			
			intcode.set(0, BigInteger.TWO);
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
				out = getPosition(modes[3], third);

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
					displayTiles();
					
					int key = Integer.compare(getBallX(), getPaddleX());
					intcode.set(out, BigInteger.valueOf(key));
	
					instructionPointer += 2;
					break;
				case OUTPUT:
					output = a;
					instructionPointer += 2;
					
					if (tempX == -1 && tempY == 0) {
						System.out.println("SCORE: " + output.intValue());
						tempX = 0;
						numOutput = -1;
					}
					
					else if (numOutput == 0) {
						tempX = output.intValue();
					}
					else if (numOutput == 1) {
						tempY = output.intValue();
					}
					else if (numOutput == 2) {
						tempId = output.intValue();
						tiles[tempY][tempX].id = tempId;
						numOutput = -1;
					}
					
					numOutput++;
					
					//System.out.println("Output: " + output);
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

	private static List<BigInteger> readIntcodeFromFile(String filename) {
		List<BigInteger> list;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
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

	private static class Tile {

		public static final int EMPTY = 0;
		public static final int WALL = 1;
		public static final int BLOCK = 2;
		public static final int PADDLE = 3;
		public static final int BALL = 4;

		int x, y;
		int id;

		public Tile(int x, int y, int id) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static int getBallX() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				Tile t = tiles[i][j];
				if (t.id == Tile.BALL) {
					return j;
				}
			}
		}
		
		return -1;
	}

	private static int getPaddleX() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				Tile t = tiles[i][j];
				if (t.id == Tile.PADDLE) {
					return j;
				}
			}
		}
		
		return -1;
	}
	
	private static void displayTiles() {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				Tile t = tiles[i][j];
				System.out.print(t.id);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		tiles = new Tile[HEIGHT][WIDTH];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				tiles[i][j] = new Tile(j, i, Tile.EMPTY);
			}
		}

		IntCodeComputer computer = new IntCodeComputer(readIntcodeFromFile("intcode_day13"));
		
		try {
			computer.runCode();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		displayTiles();
//		
//		//part 1
//		int numBlockTiles = 0;
//		for (int i = 0; i < tiles.length; i++) {
//			for (int j = 0; j < tiles[i].length; j++) {
//				if (tiles[i][j].id == Tile.BLOCK)
//					numBlockTiles++;
//			}
//		}
//
//		System.out.println("Number of block tiles = " + numBlockTiles);
	}

}
