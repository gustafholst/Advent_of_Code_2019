package code.of.advent;

import java.util.stream.Stream;

public class Day4 {
	
	private static boolean hasTwoAdjacent(String str) {
		boolean twoadjacent = false;
		for (int i = 0; i < str.length() - 2; i++) {
			if ((str.charAt(i) == str.charAt(i + 1))) {
				twoadjacent = true;
			}			
		}
		return twoadjacent;
	}
	
	private static boolean hasStrictlyTwoAdjacent(String str) {
		int adjacentCount = 1;
		char countedChar = str.charAt(0);
		for (int i = 1; i < str.length(); i++) {
			if (str.charAt(i) == countedChar) {
				adjacentCount++;	
			}	
			
			if (str.charAt(i) != countedChar) {
				if (adjacentCount == 2)
					return true;
				
				//reset
				adjacentCount = 1;
				countedChar = str.charAt(i);
			}
					
		}
		return adjacentCount == 2;
	}
	
	private static boolean meetsCriteria(int passw) {
		String pwd_str = String.valueOf(passw);
		
		if (pwd_str.length() != 6) return false;
		
		//part 1
		//if (!hasTwoAdjacent(pwd_str)) return false;
		
		//part 2
		if (!hasStrictlyTwoAdjacent(pwd_str)) return false;
		
		//never decreases?
		for (int i = 0; i < pwd_str.length() - 1; i++) {
			if (Character.getNumericValue(pwd_str.charAt(i)) > Character.getNumericValue(pwd_str.charAt(i + 1)))
				return false;
		}
		
		return true;
	}

	public static void main(String[] args) {
	
		int from = 284639;
		int to = 748759;
		
		
		long numValidPasswords = Stream.iterate(from, n -> n + 1)
		.takeWhile(n -> n <= to)
		.filter(Day4::meetsCriteria)
		.count();

		
		System.out.println("Number of passwords matching criteria: " + numValidPasswords);
	}

}
