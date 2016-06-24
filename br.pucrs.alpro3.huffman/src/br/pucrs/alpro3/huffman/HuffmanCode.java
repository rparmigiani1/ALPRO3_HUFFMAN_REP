package br.pucrs.alpro3.huffman;

import java.io.*;
import java.util.PriorityQueue;
import java.util.Scanner;

public class HuffmanCode {
	private static Scanner input;
	private static int IdCounter = 0;
	private static PrintWriter pw;

	public static void main(String[] args) {
		try {
			pw = new PrintWriter("out.txt");
		} catch (IOException e) {}
		
		input = new Scanner(System.in);
		System.out.print("Enter a text: ");
		String text = input.nextLine();

		int[] counts = getCharacterFrequency(text); // Count frequency

		System.out.printf("%-15s%-15s%-15s%-15s\n", "ASCII Code", "Character", "Frequency", "Code");

		Tree tree = getHuffmanTree(counts); // Create a Huffman tree
		String[] codes = getCode(tree.root); // Get codes

		for (int i = 0; i < codes.length; i++)
			if (counts[i] != 0) // (char)i is not in text if counts[i] is 0
				System.out.printf("%-15d%-15s%-15d%-15s\n", i, (char) i + "", counts[i], codes[i]);
		
		Tree.Node n = tree.root;
		generateHuffmanTreeDOT(n);
		System.out.println("\nEncoded Text: " + encode(n));
	}
	
	private static void generateHuffmanTreeDOT(Tree.Node root) {

		pw.println("\ndigraph g {");
		DotNodes(root);
		DotConnections(root);
		pw.println("}\n");
		pw.close();
	}

	private static void DotNodes(Tree.Node node) {
		if (node.element != Character.MIN_VALUE) {
			pw.println(String.format("node%d [label=\"%s,%d\"];", node.id, node.element, node.weight));
			return;
		}
		if (node.left != null) {
			pw.println(String.format("node%d [label=\"%d\"];", node.id, node.weight));
			DotNodes(node.left);
		}
		if (node.right != null) {
			DotNodes(node.right);
		}
	}
	
	private static void DotConnections(Tree.Node node) {
		if (node.left != null) {
			pw.println(String.format("node%d -> node%d", node.id, node.left.id));
			DotConnections(node.left);
		}
		if (node.right != null) {
			pw.println(String.format("node%d -> node%d", node.id, node.right.id));
			DotConnections(node.right);
		}
	}
	
    public static String encode(Tree.Node root){
    	 	
    	String encodeText = "";
		String[] encode = getCode(root);
		for (int i = 0; i < encode.length; i++) {
			
			if ((encode[i] != null)) {
				encodeText += encode[i];
			}				
		}
    	return encodeText;
    }

	/**
	 * Get Huffman codes for the characters This method is called once after a
	 * Huffman tree is built
	 */
	public static String[] getCode(Tree.Node root) {
		if (root == null)
			return null;
		String[] codes = new String[2 * 128];
		assignCode(root, codes);
		return codes;
	}

	/* Recursively get codes to the leaf node */
	private static void assignCode(Tree.Node root, String[] codes) {
		if (root.left != null) {
			root.left.code = root.code + "0";
			assignCode(root.left, codes);

			root.right.code = root.code + "1";
			assignCode(root.right, codes);
		} else {
			codes[(int) root.element] = root.code;
		}
	}

	/** Get a Huffman tree from the codes */
	public static Tree getHuffmanTree(int[] counts) {
		int id = 0;
		PriorityQueue<Tree> heap = new PriorityQueue<Tree>(); 
																
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0)
				heap.add(new Tree(counts[i], (char) i, id)); 
		}

		while (heap.size() > 1) {
			Tree t1 = heap.remove();
			Tree t2 = heap.remove(); 
			heap.add(new Tree(t1, t2)); 
		}

		return heap.remove(); // The final tree
	}

	/** Get the frequency of the characters */
	public static int[] getCharacterFrequency(String text) {
		int[] counts = new int[256]; // 256 ASCII characters

		for (int i = 0; i < text.length(); i++)
			counts[(int) text.charAt(i)]++; // Count the character in text

		return counts;
	}

	/** Define a Huffman coding tree */
	public static class Tree implements Comparable<Tree> {
		Node root; // The root of the tree

		/** Create a tree with two subtrees */
		public Tree(Tree t1, Tree t2) {
			root = new Node();
			root.left = t1.root;
			root.right = t2.root;
			root.weight = t1.root.weight + t2.root.weight;
			root.id = IdCounter++;
		}

		/** Create a tree containing a leaf node */
		public Tree(int weight, char element, int id) {
			root = new Node(weight, element, id);
		}

		@Override /** Compare trees based on their weights */
		public int compareTo(Tree t) {
			if (root.weight > t.root.weight) // Purposely reverse the order
				return 1;
			else if (root.weight == t.root.weight)
				return 0;
			else
				return -1;
		}

		public class Node {
			int id;
			char element; // Stores the character for a leaf node
			int weight; // weight of the subtree rooted at this node
			Node left; // Reference to the left subtree
			Node right; // Reference to the right subtree
			String code = ""; // The code of this node from the root

			/** Create an empty node */
			public Node() {
			}

			/** Create a node with the specified weight and character */
			public Node(int weight, char element, int id) {
				this.id = IdCounter++;
				this.weight = weight;
				this.element = element;
			}
		}
	}
}
