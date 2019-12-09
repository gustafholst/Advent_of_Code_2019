package code.of.advent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day6 {
	
	private static class Node {
		public Node parent = null;
		public List<Node> children;
		public String value;
		
		public Node(String val, Node parent) {
			value = val;
			this.parent = parent;
			children = new ArrayList<Node>();
		}
		
		public void addChild(String val) {
			children.add(new Node(val, this));
		}
		
		public Node findNode(String val) {		
			if (value.equals(val))
				return this;
			
			Node found = null;
			for (Node n : children) {
				found = n.findNode(val);
				if (found != null)
					break;
			}
			
			return found;
		}
		
		public int countNodesUpUntil(Node target) {
			int count = 0;
			Node current = this;
			while (current != target) {
				current = current.parent;
				count++;
			}
			
			return count;
		}
		
		public Set<Node> getSubNodes() {
			Set<Node> set = new HashSet<Node>();
			
			set.add(this);
			for (Node c : children) {
				set.addAll(c.getSubNodes());
			}
			
			return set;
		}
		
		public String toString() {
			return value;
		}
	}
	
	private static class Tree {
		public Node root;
		
		public Tree() {}
		
		public void add(String val, String par) {
			if (root == null) {
				root = new Node(val, null);
				return;
			}
			
			Node parent = findNode(par);
			parent.addChild(val);
		}
		
		public Node findNode(String val) {
			return root.findNode(val);
		}
		
		public Set<Node> getAllNodes() {
			return root.getSubNodes();
		}
		
		public int getTotalOrbits() {
			int total = 0;
			for (Node n : getAllNodes()) {
				total += n.countNodesUpUntil(root);
			}
			return total;
		}
		
		public int getCountBetween(String f, String t) {
			
			int dist = 0;
			boolean found = false;
			Node from = root.findNode(f);
			
			Node to = null;
			
			while (!found) {
				//is santa below in the tree?
				to = from.findNode(t);		
				if (to != null) {
					dist += to.countNodesUpUntil(from);
					found = true;
				}
				else {
					// go up
					from = from.parent;
					dist++;
				}
			}
			
			return dist - 2; 
		}
	}

	public static void main(String[] args) {
		
		Map<String,List<String>> parent_child = new HashMap<String,List<String>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader("orbits"));
			
			br.lines().forEach(line -> {
				String parent = line.substring(0, 3);
				String child = line.substring(4);
				
				parent_child.compute(parent, (s, strings) -> strings == null ? new ArrayList<>() : strings).add(child);
				
			});
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String root = "COM";
		Tree tree = new Tree();
		
		tree.add(root, null);
		
		List<String> nextGeneration = Arrays.asList(root);
			
		while (!parent_child.isEmpty()) {
			
			List<String> next = new ArrayList<String>();
			for (String c : nextGeneration) {
				
				List<String> children = parent_child.get(c);
				if (children != null) {
					for (String x : children)
						tree.add(x, c);
					
					next.addAll(parent_child.get(c));
				}
				
				parent_child.remove(c);
			}
			
			nextGeneration = next;
		}
		
		//Part One
		System.out.println("Count: " + tree.getTotalOrbits());
		
		//Part Two
		int distance = tree.getCountBetween("YOU", "SAN");
		System.out.println("Distance to santa: "+ distance);
	}
}
