package ciic4020.project3.main;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.Scanner;

import ciic4020.project3.hashtable.HashTableSC;
import ciic4020.project3.hashtable.SimpleHashFunction;
import ciic4020.project3.lists.List;
import ciic4020.project3.lists.SortedArrayList;
import ciic4020.project3.lists.SortedList;
import ciic4020.project3.tree.BTNode;

public class HuffmanCoding{

	/**
	 * Here we go, Project 3, the thrilling conclusion to the trilogy. This installment sees the return of
	 * our beloved lead star from Project 1, load_data. She definitely pulled her weight the first
	 * time around and now she's back for more, this time reading an input string, from a file in the same 
	 * directory as our source file no less. How does she do it, ladies and gentlemen? There's a colorful cast 
	 * of new characters to boot. Project 3 contains 6 other methods: compute_fd, huffman_tree and min_freq, 
	 * huffman_code, encode, and process_results. Together they'll encode the input string and print results 
	 * to the console.
	 * @author Kevin Purcell
	 */

	public static void main(String[] args) throws Exception {
		String input = load_data("inputData/input2.txt"); // Provides input string from a text file
		HashTableSC<String,Integer> FD = compute_fd(input);	// Provides the hash table containing the frequency distribution
		BTNode<Integer,String> hTree = huffman_tree(FD); // Constructs the huffman tree
		HashTableSC<String, String> hCode = huffman_code(hTree); // Maps out the huffman code from the previously constructed tree
		String output = encode(hCode,input); // Provides the output string encoded using the previously mapped huffman code
		process_results(FD,input,output,hCode);	// Prints the results to the console

//		System.out.println("\n\n--Testing--");
//		SortedArrayList<BTNode<Integer,String>> SL = new SortedArrayList<BTNode<Integer,String>>(FD.size());
//		for(String key : FD.getKeys()) { // Copy the contents of parameter hash table into our sorted list
//			BTNode<Integer, String> newNode = new BTNode<Integer,String>();
//			newNode.setKey(FD.get(key));
//			newNode.setValue(key);
//			SL.add(newNode);
//		}
//		for(int i = 0;i<SL.size();i++) {
//			System.out.println(SL.get(i).getKey() + ":" + SL.get(i).getValue());
//		}
//		System.out.println("\nDecoded String: " + decodeHuff(output,hCode));
//		System.out.println("\nHuffman Tree: ");
//	    display(hTree); 
	}

	/**
	 * Here she is, star of the show. Receives a string containing a path of a file and returns a 
	 * string containing it's contents.
	 */

	public static String load_data(String path) throws FileNotFoundException {
		File file = new File(path); // Search and create file object for file in path
		Scanner scan = new Scanner(file);
		String data = scan.nextLine();
		scan.close();
		return data;
	}

	/**
	 * Constructs a hash table containing the frequency distribution of our input string.
	 * @throws Exception 
	 */

	public static HashTableSC<String,Integer> compute_fd(String str) throws Exception {
		HashTableSC<String,Integer> map = new HashTableSC<String,Integer>(11,new SimpleHashFunction<String>());
		for(int i = 0;i<str.length();i++) {
			if(map.containsKey(str.substring(i,i+1))) { 
				map.put(str.substring(i,i+1), map.get(str.substring(i,i+1)).intValue() + 1);
			}else {
				map.put(str.substring(i,i+1),1);
			}
		}
		return map;
	}

	/**
	 * huffman_tree's trusty side-kick, min_freq receives a list and returns the smallest element, 
	 * but careful, min_freq also removes the element from the list.
	 */
	public static BTNode<Integer,String> min_freq(SortedArrayList<BTNode<Integer,String>> list){
		BTNode<Integer,String> holder = list.get(0); 
		list.removeIndex(0);
		return holder;
	}

	/**
	 * The almost star of the show if it weren't for load_data, huffman_tree constructs a binary tree
	 * from the frequency distribution of the input string. 
	 */

	public static BTNode<Integer,String> huffman_tree(HashTableSC<String, Integer> FD){
		SortedArrayList<BTNode<Integer,String>> SL = new SortedArrayList<BTNode<Integer,String>>(FD.size()); // Store elements in ascending order 
		for(String key : FD.getKeys()) { // Copy the contents of parameter hash table into our sorted list
			BTNode<Integer, String> newNode = new BTNode<Integer,String>();
			newNode.setKey(FD.get(key));
			newNode.setValue(key);
			SL.add(newNode);
		}
		while(SL.size()>1) { // 
			BTNode<Integer,String> N = new BTNode<Integer,String>(); // Assign a new root node
			BTNode<Integer,String> X = min_freq(SL); // Retrieve the smallest element and removes it, may be a previously constructed root
			BTNode<Integer,String> Y = min_freq(SL); // Retrieve the second smallest element and removes it, may be a previously constructed root
			if(X.getKey().equals(Y.getKey()) && X.getValue().compareTo(Y.getValue())>0) { // Tie breaker in the event that both symbols have the same frequency
					N.setLeftChild(Y); // Swap positions in X has a larger symbol than Y
					N.setRightChild(X); 
			}else {
				N.setLeftChild(X);
				N.setRightChild(Y); 
			}
			N.setKey(N.getLeftChild().getKey() + N.getRightChild().getKey()); // The frequency becomes the combination of both nodes so as to move up the tree
			N.setValue(N.getLeftChild().getValue() + N.getRightChild().getValue()); // The symbols combine as well
			SL.add(N); // Adds the element back so it can be assigned a parent or result as the root of the huffman tree
		}
		return min_freq(SL); // Returns the final node i.e. the root of the complete huffman tree
	}

	/**
	 * This method returns a hash table containing the huffman code for the symbols in our input
	 * string. It constructs the code by traversing the tree and accumulating bits for every move
	 * made, 0 if moved left and 1 if moved right. Whenever the traversal leads to a child-less node,
	 * that node's symbol as well and the accumulated code are added into the hash table.
	 *  
	 */

	public static HashTableSC<String,String> huffman_code(BTNode<Integer,String> hRoot){
		HashTableSC<String,String> hCode = new HashTableSC<String,String>(11, new SimpleHashFunction<String>()); // Hash table to store the huffman code
		huffman_code_aux(hRoot,hCode, ""); // First call to the auxiliary function that handles the construction of the huffman code
		return hCode; // Returns completed huffman code
	}

	public static void huffman_code_aux(BTNode<Integer,String> node, HashTableSC<String,String> map, String code) {
		if(node.getLeftChild()==null && node.getRightChild()==null) { // Checks if child-less
			map.put(node.getValue(), code);
		}else{
				huffman_code_aux(node.getLeftChild(),map,code+"0");
				huffman_code_aux(node.getRightChild(),map,code+"1");

		}
	}

	/**
	 * encode has it's work cut out for it. It simply constructs and returns an encoded string
	 * using the hash table containing the huffman code.
	 */

	public static String encode(HashTableSC<String,String> hCode, String input) {
		String output = "";
		for(int i = 0;i<input.length();i++) {
			output+=hCode.get(input.substring(i,i+1));
		}
		return output;
	}

	/**
	 * This method processes the results, shocker, I know. It takes as parameter a hash table containing the frequency distribution
	 * of the input string, the input string, the output string, and a hash table containing the input string's huffman code. The 
	 * method takes these parameters and produces a table containing the symbols of the input string, along with their respective
	 * frequencies and huffman code. It also produces the bytes necessary to store the input and output string, as well as the 
	 * percentage difference between them. These results are printed onto the console.
	 */
	public static void process_results(HashTableSC<String,Integer> FD, String input, String output, HashTableSC<String,String> hCode) {
		int inputBytes = input.length(); // 1 character = 1 byte
		int outputBytes = (int) Math.ceil(output.length()/8.0); // 1 binary code = 1 bit = 1/8 byte
		double difference = (((inputBytes - outputBytes)*1.0)/inputBytes)*100; 
		SortedList<BTNode<Integer,String>> SL = new SortedArrayList<BTNode<Integer,String>>(FD.size()); // Sorted list to store the elements in ascending frequency order

		for(String key : FD.getKeys()) { // Copies the contents of the parameter hash table into our hash table
			BTNode<Integer, String> newNode = new BTNode<Integer,String>();
			newNode.setKey(FD.get(key));
			newNode.setValue(key);
			SL.add(newNode);
		}

		System.out.println("Symbol  Frequency       Code\n------  ---------       ----");

		for(int i = SL.size()-1;i>=0;i--) { // Prints the symbols along with their frequency and huffman code in descending frequency order
			String key = SL.get(i).getValue();
			System.out.println(key + "       " + FD.get(key) + "               " + hCode.get(key)); // Print 1 row of results	
		}
		System.out.println("\nOriginal string: \n" + input);
		System.out.println("Encoded string: \n" + output);
		System.out.println("\nThe original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + outputBytes + " bytes.");
		System.out.print("Difference in space required is ");
		if(difference%1 != 0) { // Rounds the difference to the nearest 2 decimal point if necessary
			System.out.printf("%.2f", difference);
			System.out.print("%.");
		}else { // Prints as integer if there is no remainder
			System.out.print((int) difference + "%.");
		}
	}
	

	/**
	 * The following are methods used solely for testing.
	 */

	///////////////////////////////////////////////////////////////////////
	// The following are miscellaneous methods to display the content of //
	// the tree .... (Scrapped and altered from CIIC4020 Lab 8)          //
	///////////////////////////////////////////////////////////////////////
	public static void display(BTNode<Integer,String> root) {            //
		final int MAXHEIGHT = 100;                                       //
		int[] control = new int[MAXHEIGHT];                              //
		control[0]=0;                                                    //
		if (root!=null)                                                  //
			recDisplay(root, control, 0, "ROOT");                        //
		else                                                             //
			System.out.println("Tree is empty.");                        //
	}                                                                    //
	                                                                     //
	// Auxiliary Method to support display                               //
	public static void recDisplay(BTNode<Integer,String> root,           //
			int[] control, int level, String pos)                        //
	{                                                                    //
		printPrefix(level, control);                                     //
		System.out.println();                                            //
		printPrefix(level, control);                                     //
		Integer key = root.getKey();					         		 //
		String val = root.getValue();					    			 //
		System.out.println("__(" + key +"," + val +")" + pos);           //
		control[level]--;                                                //
		int nc = 2;                                                      //
		control[level+1] = nc;                                           //
		if(root.getLeftChild()!=null){                                   //
			recDisplay(root.getLeftChild(), control, level+1, "L");      //
		}                                                                //
		if(root.getRightChild()!=null) {                                 //
			recDisplay(root.getRightChild(),control,level+1, "R");       //
		} 														         //
	}                                                                    //
	                                                                     //
	// Auxiliary method to support display                               //
	protected static void printPrefix(int level, int[] control) {        //
		for (int i=0; i<=level; i++)                                     //
			if (control[i] <= 0)                                         //
				System.out.print("    ");                                //
			else                                                         //
				System.out.print("   |");                                //
	}                                                                    //
	///////////////////////////////////////////////////////////////////////

	/**
	 * Method to decode the output string using our huffman code.
	 *@author Fabiola E Robles-Vega
	 */

	public static String decodeHuff(String output, HashTableSC<String, String> hCode) {
		String result = "";
		int start = 0;
		List<String>  huffCodes = hCode.getValues();
		List<String> symbols = hCode.getKeys();

		/*looping through output until a huffcode is found on map and
		 * adding the symbol that the huffcode represents to result */
		for(int i = 0; i<= output.length();i++){

			String searched = output.substring(start, i);

			int index = huffCodes.firstIndex(searched);

			if(index>=0) { //Found it
				result= result + symbols.get(index);
				start = i;
			}
		}
		return result;   
	}

}
