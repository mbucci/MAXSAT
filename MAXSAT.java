/*
 * MAXSAT solver 
 *
 * NIC - Professor Majercik
 * Max Bucci, Nikki Morin, Megan Maher, Kuangji Chen
 * Created: 2/11/15
 * Last Modified: 2/20/15
 *
 */


import java.io.*;
import java.util.*;


public class MAXSAT{
	
	//Algorithm Instances
	private static PBIL runner;
	private static GA gaRunner;

	//File Variables
	private static BufferedReader reader = null;
	private static File file;
	
	//CNF variables 
	private static List<CNF> cnf = new ArrayList<CNF>();
	private static int numVariables = 0;
	private static int numClauses = 0;

	
	//General Variables
	private static String algorithm;
	private static int individuals;
	private static double mutationProb;
	private static int iterations;
	
	//GA Variables
	private static String selectionMethod;
	private static String crossoverMethod;
	private static double crossoverProb;

	//PBIL Variables
	private static double posLearnRate;
	private static double negLearnRate;
	private static double mutationAmount;

		
	public static void main(String[] args) {
		
		file = new File(args[0]);
		//System.out.println("File is: " + file);
		
		individuals = Integer.parseInt(args[1]);
		iterations = Integer.parseInt(args[6]);
		algorithm = args[7];
		
		if(algorithm.equals("p")) {
			selectionMethod = args[2];
			crossoverMethod = args[3];
			crossoverProb = Double.parseDouble(args[4]);
			mutationProb = Double.parseDouble(args[5]);
			
			readFile(file);
			runner = new PBIL(individuals, posLearnRate, negLearnRate, mutationProb, mutationAmount, iterations);
			runner.runPBIL(cnf, numClauses, numVariables);
			runner.printResults(args[0], numClauses, numVariables);
			
			
		} else if (algorithm.equals("g")) {
			selectionMethod = args[2];
			crossoverMethod = args[3];
			crossoverProb = Double.parseDouble(args[4]);
			mutationProb = Double.parseDouble(args[5]);

			// posLearnRate = Double.parseDouble(args[2]);
			// negLearnRate = Double.parseDouble(args[3]);
			// mutationProb = Double.parseDouble(args[4]);
			// mutationAmount = Double.parseDouble(args[5]);
			
			readFile(file);

			gaRunner = new GA(individuals, mutationProb, iterations, selectionMethod, crossoverMethod, crossoverProb);
			gaRunner.runGA(cnf, numClauses, numVariables);
			gaRunner.printResults(args[0], numClauses, numVariables);
			
		} else {
			System.out.println("Incorrect input type\n");
			System.exit(0);
		}
	}
	
	
	public static void readFile(File f) {
		
		try {
			reader = new BufferedReader(new FileReader(f));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!(line.charAt(0) == 'c')) {
					
					String[] splitStr = line.split("\\s+");
					if (numVariables != 0 && numClauses != 0) {
						
						int[] temp = new int[splitStr.length - 1];
						
						for (int i = 0; i < temp.length; i++) {
							temp[i] = Integer.parseInt(splitStr[i]);
						}
						CNF clause = new CNF(temp, temp.length);
						cnf.add(clause);
					}
					if(numVariables == 0) numVariables = Integer.parseInt(splitStr[2]);
					if(numClauses == 0) numClauses = Integer.parseInt(splitStr[3]);
				}
			}
			reader.close();
			
		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.", f);
			e.printStackTrace();	
		}
	}
}
