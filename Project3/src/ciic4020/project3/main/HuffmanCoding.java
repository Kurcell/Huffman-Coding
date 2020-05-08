package ciic4020.project3.main;
import java.io.File;

import java.io.FileNotFoundException;
import java.util.Scanner;

import ciic4020.project3.hashtable.HashTableSC;
import ciic4020.project3.hashtable.SimpleHashFunction;
import ciic4020.project3.lists.SortedArrayList;
import ciic4020.project3.lists.SortedList;
import ciic4020.project3.tree.BTNode;

public class HuffmanCoding{

	public static void main(String[] args) throws FileNotFoundException {
		String input = load_data("inputData/stringData.txt");
		HashTableSC<String,Integer> FD = compute_fd(input);
		
		
		//Testing//////////////////////////////////////////////////////////////////////////////////////
		HashTableSC<String, String> hCode = huffman_code(huffman_tree(FD));
		
		String output = encode(hCode,input);
		process_results(FD,input,output);
		
		
		//Testing//////////////////////////////////////////////////////////////////////////////////////
		
		
	}
	
	public static String load_data(String path) throws FileNotFoundException {
		File file = new File(path);
		Scanner scan = new Scanner(file);
		String data = scan.nextLine();
		scan.close();
		return data;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashTableSC<String,Integer> compute_fd(String str) {
		HashTableSC<String,Integer> map = new HashTableSC<String,Integer>(1,new SimpleHashFunction());
		for(int i = 0;i<str.length();i++) {
			if(map.containsKey(str.substring(i,i+1))) {
				map.put(str.substring(i,i+1), map.get(str.substring(i,i+1)).intValue() + 1);
			}else {
				map.put(str.substring(i,i+1),1);
			}
		}
		return map;
	}
	
	public static BTNode<Integer,String> min_freq(SortedList<BTNode<Integer,String>> list){
		BTNode<Integer,String> holder = list.get(0);
		list.removeIndex(0);
		return holder;
	}
	
	public static BTNode<Integer,String> huffman_tree(HashTableSC<String, Integer> FD){
		SortedList<BTNode<Integer,String>> SL = new SortedArrayList<BTNode<Integer,String>>(FD.size());
		for(String key : FD.getKeys()) {
			BTNode<Integer, String> newNode = new BTNode<Integer,String>();
			newNode.setKey(FD.get(key));
			newNode.setValue(key);
			SL.add(newNode);
		}
		while(SL.size()>1) {
			BTNode<Integer,String> N = new BTNode<Integer,String>();
			BTNode<Integer,String> X = min_freq(SL);
			BTNode<Integer,String> Y = min_freq(SL);
			N.setLeftChild(X);
			N.setRightChild(Y);
			N.setKey(X.getKey() + Y.getKey());
			N.setValue(X.getValue() + Y.getValue());
			SL.add(N);
		}
		return min_freq(SL);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashTableSC<String,String> huffman_code(BTNode<Integer,String> hRoot){
		//TODO
		HashTableSC<String,String> hCode = new HashTableSC<String,String>(1, new SimpleHashFunction());
		huffman_code_aux(hRoot,hCode, "");
		return hCode;
	}
	
	public static void huffman_code_aux(BTNode<Integer,String> node, HashTableSC<String,String> map, String code) {
		if(node.getLeftChild()==null && node.getRightChild()==null) {
			map.put(node.getValue(), code);
		}else{
			if(node.getLeftChild() != null) {
				huffman_code_aux(node.getLeftChild(),map,code+"0");
			}
            if(node.getRightChild() != null) {
				huffman_code_aux(node.getRightChild(),map,code+"1");
			}
			
		}
	}
	
	public static String encode(HashTableSC<String,String> hCode, String input) {
		String output = "";
		for(int i = 0;i<input.length();i++) {
			output+=hCode.get(input.substring(i,i+1));
		}
		return output;
	}
	
	public static void process_results(HashTableSC<String,Integer> FD, String input, String output) {
		int inputBytes = input.length();
		int holder = output.length();
		while(holder%8 != 0) {
			holder++;
		}
		int outputBytes = holder/8;
		double savings = (((inputBytes - outputBytes)*1.0)/inputBytes)*100;
		System.out.println("Symbol|Frequency");
		for(String key : FD.getKeys()) {
			System.out.println(key +"     |" + FD.get(key));
		}
		System.out.println("Original text: " + input + "       |Necessary bytes: " + inputBytes);
		System.out.println("Encoded text: " + output + "  |Necessary bytes: " + outputBytes);
		System.out.println("Savings : " + savings + "%");
	}
	
}
