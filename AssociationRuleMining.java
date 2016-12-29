import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.Scanner;
import java.io.File;

public class AssociationRuleMining {
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception {
		AssociationRuleMining arm = new AssociationRuleMining();
		Instances data;
		
		//READING INPUT AND DATASET 
		System.out.println("Enter dataset path: ");
		String filePath = scanner.nextLine();
		if (arm.checkFileExistence(filePath)){
			data = arm.readDataSet(filePath);
		}
		else{
			System.out.println("Incorrect file");
			return;
		}
		
		int minimumSupport = arm.getUserInput(data.size());
		if(minimumSupport > data.size() || minimumSupport < 0){
			System.out.println("Invalid minimum support should be between 0 and " + data.size());
			return;
		}
		
		Long startTime = System.currentTimeMillis();
		
		//BUILDING TRIE
		Trie trieObj = new Trie();
		trieObj.singleItemSet(data, minimumSupport);
		
		//FREQUENT ITEMSET GENERATION
		System.out.println("Candidate itemset generation starts.");
		trieObj.traverseDFS(trieObj.root, "");
		System.out.println("Candidate itemset generation completed.");
		
		//RULE GENERATION
		trieObj.getPermutations();
		
		//SORTING RULES IN DESCENDING ORDER OF CONFIDENCE
		trieObj.sortRules();
		
		//PRINTING RULES
		trieObj.printRules();
		
		Long stopTime = System.currentTimeMillis();
		Long elapsedTime = stopTime - startTime;
		System.out.println("Runtime: " + elapsedTime/1000.0 + " seconds");
	}

	public Instances readDataSet(String filePath) throws Exception{
		//DataSource input = new DataSource("/Users/niraj/Downloads/weather.nominal.arff");
		DataSource input = new DataSource(filePath);
		Instances data = input.getDataSet();
		return data;
	}
	
	public boolean checkFileExistence(String filePath){
		File file = new File(filePath);
		if(file.exists() && !file.isDirectory()){
			return true;
		}
		return false;
	}
	
	public int getUserInput(int dataSize){
		System.out.println("Total number of instances in dataset is: " + dataSize);
		System.out.println("Enter minimum frequency(support) for generating rules: ");
		return Integer.parseInt(scanner.nextLine());		
	}
}

