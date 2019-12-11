package code.of.advent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 {
	
	private static final int width = 25;
	private static final int height = 6;

	
	private static int countNumber(List<List<Integer>> layer, int number) {
		int total = 0;
		for (List<Integer> row : layer) {
			total += Collections.frequency(row, number);
		}
		return total;
	}
	
	private static void displayImage(char[][] image) {
		System.out.println();
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				System.out.print(image[i][j]);
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		
		List<List<List<Integer>>> layers = new ArrayList<>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("pixels.sif"));
			
			List<Integer> input = br.lines()
					.flatMap((String line) -> Stream.of(line.split("")))
					.map(Integer::valueOf).collect(Collectors.toList());
			
			br.close();
			
			for (int i = 0; i < input.size(); i += width*height) {
				List<List<Integer>> layer = new ArrayList<>();
				for (int j = 0; j < height; j++) {		
					layer.add(input.subList(i+j*width, i+j*width + width));
				}	
				layers.add(layer);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(layers);
		
		int indexWinner = 0;
		int fewest = Integer.MAX_VALUE;
		for (int i = 0; i < layers.size(); i++) {
			int count = countNumber(layers.get(i), 0);
			if (count < fewest) {
				indexWinner = i;
				fewest = count;
			}
				
		}
		
		//part 1
		int numOnes = countNumber(layers.get(indexWinner), 1);
		int numTwos = countNumber(layers.get(indexWinner), 2);
		
		System.out.println("#Ones * #Twos = " + numOnes * numTwos);
		
		//part 2
		
		char[][] image = new char[height][width];
		
		for (int l = layers.size() - 1; l >= 0; l--) {
			List<List<Integer>> layer = layers.get(l);
			int y = 0;
			for (List<Integer> row : layer) {
				for (int x = 0; x < row.size(); x++) {
					int pixel = row.get(x);
					if (pixel == 0) image[y][x] = '#';
					if (pixel == 1) image[y][x] = ' ';
				}
				y++;
			}
		}
		
		displayImage(image);

	}

}
