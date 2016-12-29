import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

import weka.core.Instances;

public class Trie {
    Node root;
    ArrayList<ArrayList<Node>> globalList = new ArrayList<ArrayList<Node>>();
    ArrayList<String> allItemSets = new ArrayList<String>();
    ArrayList<Rule> listOfRules = new ArrayList<Rule>();
    Instances data;
    
    public Trie(){
        root = new Node();
    }

    public void insert(String attributeName, String attributeValue, Node parent) {		
    	parent.children.put(attributeValue, new Node(attributeName,attributeValue,parent));  
    }
    
    public void traverseDFS(Node node, String itemset){
    	if (node.attributeName != null){
    		itemset = itemset + node.attributeValue + ",";
    	}
    	
    	if(node.children.size() != 0){
    		Iterator<Node> it = node.getChildren().values().iterator();
    		while(it.hasNext())    		{
    			Node temp = it.next();
    			traverseDFS(temp, itemset);
    		}
    	}
    	if (node.children.size() == 0){		
    		if(node.getParent() != null && node.getParent().attributeName!=null){
	    		System.out.println("       "+itemset.substring(0, itemset.length() - 1));
	        	this.allItemSets.add(itemset.substring(0, itemset.length() - 1));
    		}
    	}  	
    } 
    
    public int getSupportFromTrie(Node node, String itemset){
    	Node current = node;
    	String[] items = itemset.split(",");
    	for (int i = 0; i < items.length; i++){   		
    		current = current.children.get(items[i]);
    	}
    	return current.frequency;
    }
    
    public static int getSupport(Instances data, String itemset){
    	int support = 0;
    	String[] items = itemset.split(",");
    	for (int i = 0; i < data.size(); i++){
    		boolean flag = true;
    		for (int j = 0; j < items.length; j++){
    			String instance = data.instance(i).toString();
    			String[] instanceItems = instance.split(",");
    			
    			boolean flag2 = false;
    			for(int k = 0; k < instanceItems.length; k++){
    				if(instanceItems[k].equals(items[j])){
    					flag2 = true;
    					break;
    				}
    			}  			
    			if(!flag2){
    				flag = false;
    				break;
    			}
    		}
    		if(flag){
    			support++;
    		}
    	}
    	return support;
    }
    
    public Node prune(Node node, int frequency) {
		// new logic : http://stackoverflow.com/questions/6092642/how-to-remove-a-key-from-hashmap-while-iterating-over-it
		Iterator<Node> it = node.getChildren().values().iterator();
		while(it.hasNext()){
			Node temp = it.next();
			if(temp.getFrequency()<frequency){
				it.remove();
			}
		}
		return node;
	}
	
	public void updateFrequency(Node node, String itemset, Instances data){
    	if (node.attributeName != null){
    		itemset = itemset + node.attributeValue + ",";
    	}
    	int support = getSupport(data, itemset);
     	node.frequency = support;
    	
    	if(node.children.size() != 0){
    		Iterator<Node> it = node.getChildren().values().iterator();
    		while(it.hasNext()){
    			Node temp = it.next();
    			updateFrequency(temp, itemset, data);
    		}
    	} 	
    }
    
    // single item set
    
    public void singleItemSet(Instances data, int minimumSupport) {
		this.data = data;
		for (int i=0; i<data.numAttributes(); i++){
			for(int j=0;j<data.attribute(i).numValues();j++){
				this.insert(data.attribute(i).name(), data.attribute(i).value(j), this.root);  // generating list of size 1 and creating the tree
			}
		}
		
		updateFrequency(this.root, "", data);
		Node prunedObj = prune(this.root, minimumSupport);
		getChildList(prunedObj);

		int counter=1;
		boolean cannotExpand = false;
		do{	
			//generate multi level item set
			cannotExpand = multipleItemset(this.root, minimumSupport);
			counter++;
			if(cannotExpand){
				break;
			}			
		}while(counter<data.numAttributes());
	}
    
    public void sortRules(){
    	Collections.sort(this.listOfRules);
    }
    
    public void printRules(){
    	DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i < this.listOfRules.size(); i++){
			System.out.println(this.listOfRules.get(i).rule + " (" + "Conf: " + df.format((this.listOfRules.get(i).confidence * 100)) + "% " + "Lift: " + df.format((this.listOfRules.get(i).lift * 100)) + "% " + "Lev: " + (this.listOfRules.get(i).leverage) + ")");
		}
    }
    
    public void getPermutations(){
    	System.out.println("\nRule generation starts  ");
    	if (!this.allItemSets.isEmpty()){
			for(int i =0; i<this.allItemSets.size();i++){			
				permutations(this.allItemSets.get(i));
			}
    	}
    	System.out.println("Rule generation completed  ");
		System.out.println("Number of rule generated: " + this.listOfRules.size());
    }
    
	public void getChildList(Node node){
		LinkedHashMap <String, Node> listOfChildrens = node.children;  // storing children of ROOT node
		ArrayList<Node> currentLevelNodeObjects = new ArrayList<Node>();
		if(!listOfChildrens.isEmpty()){
    		for(Node temp : listOfChildrens.values()){
    				currentLevelNodeObjects.add(temp);
    		}
		}
		this.globalList.add(currentLevelNodeObjects);
	}

	/*
	 * reference : http://stackoverflow.com/questions/127704/algorithm-to-return-all-combinations-of-k-elements-from-n
	 */
	public void permutations(String input){
		int inputSupport = getSupportFromTrie(this.root, input);
		ArrayList<String> itemset = new ArrayList<String>();
		String[] temp = input.split(",");
		for (int k = 0; k < temp.length; k++){
			itemset.add(temp[k]);
		}
		
		int countOfSubsets = ((int) (Math.pow(2, itemset.size()))) - 2; 
		int longestBinaryString = Integer.toBinaryString(countOfSubsets).length();
		String formatString = "%" + longestBinaryString + "s";
		
		int i, j;
		
		String[] setOfLHS = new String[countOfSubsets];
		String[] setOfRHS = new String[countOfSubsets];
		String[] binarySequence = new String[countOfSubsets];
		
		for (i = 0; i < countOfSubsets; i++){
			binarySequence[i] = String.format(formatString, Integer.toBinaryString(i+1)).replace(' ', '0');
		}
		
		StringBuffer tempSB = new StringBuffer();
		
		//generating LHS of rules
		for (i = 0; i < countOfSubsets; i++){
			tempSB.setLength(0);
			for (j = 0; j < longestBinaryString; j++){
				if (binarySequence[i].charAt(j) == '1'){
					tempSB.append(itemset.get(j) + ",");
				}
			}
			setOfLHS[i] = tempSB.toString();
		}
		
		//generating RHS of rules
		for (i = 0; i < countOfSubsets; i++){
			tempSB.setLength(0);
			for (j = 0; j < itemset.size(); j++){	
				String[] LHSitems = setOfLHS[i].split(",");
				String currentRHSitem = itemset.get(j);
				boolean isPresent = false;
				for (int k = 0; k < LHSitems.length; k++){
					if (LHSitems[k].equals(currentRHSitem)){
						isPresent = true;
						break;
					}
				}
				if(!isPresent){
					tempSB.append(currentRHSitem + ",");
				}
				
			}
			setOfRHS[i] = tempSB.toString();
		}	
		
		for (i = 0; i < countOfSubsets; i++){
			int lhsSupport = getSupportFromTrie(this.root, setOfLHS[i]);
			int rhsSupport = getSupportFromTrie(this.root, setOfRHS[i]);
			double confidence = (double)inputSupport/lhsSupport;
			int productOfLhsRhs = lhsSupport * rhsSupport;
			double lift = (double)(inputSupport)/(productOfLhsRhs);
			double leverage = (double)(inputSupport - (productOfLhsRhs));
			this.listOfRules.add(new Rule(setOfLHS[i].substring(0,setOfLHS[i].length()-1) + " --> " + setOfRHS[i].substring(0,setOfRHS[i].length() - 1), confidence, lift, inputSupport, leverage));
		}		
	}
	
	public boolean multipleItemset(Node node, int minimumSupport) {
		boolean stopTree = false;
    	
		System.out.println("We are at level " + this.globalList.size() + " itemsets generation.");
    	ArrayList<Node> tempList = this.globalList.get(this.globalList.size() - 1);
    	ArrayList<Node> newObjList = new ArrayList<Node>();
    	for(int i=0; i<tempList.size();i++){
    		Node prunedNode;
    		if(i == tempList.size() - 1){
    			System.out.println("Last element, exit the loop");
    			tempList.get(i).isLeaf=true;
    			break;
    		}
    		
    		String tempAttributeName = tempList.get(i).attributeName;
    		Node currentNode = tempList.get(i);
    		Node parentNode = tempList.get(i).getParent();
    		LinkedHashMap <String, Node> listOfChildren = parentNode.children;  // storing children of ROOT node
    	    ArrayList<String> tempChildList = new ArrayList<String>();
    	    if(!listOfChildren.isEmpty()){
    	    	for(Node temp : listOfChildren.values()){
    	    			tempChildList.add(temp.attributeValue);
    	    	}
    	    }
    		for(int j = tempChildList.indexOf(currentNode.attributeValue)+1;j<tempChildList.size();j++){
    			if((!tempAttributeName.equals(parentNode.children.get(tempChildList.get(j)).attributeName))){
    				insert(parentNode.children.get(tempChildList.get(j)).attributeName,parentNode.children.get(tempChildList.get(j)).attributeValue,tempList.get(i));
    			 }
    		 }
    		updateFrequency(node, "", this.data); 

    		prunedNode = prune(tempList.get(i), minimumSupport);
    		if(!prunedNode.children.isEmpty()){
    		for(Node temp : prunedNode.children.values()){
    			// adding the new child to the Object List
				newObjList.add(temp);
				}
    		}
    		else{
    			tempList.get(i).isLeaf=true;
    		}    		    	
    	} 
    	System.out.println("Number of new nodes:"+newObjList.size());
    	System.out.println("-----------------");
    	if(newObjList.isEmpty()){
    		stopTree = true;
    	}
    	this.globalList.add(newObjList);
    	return stopTree;
		}
	}
