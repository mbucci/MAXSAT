


import java.io.*;


public class MAXSAT {

	//General Constants
	private static String ALGORITHM;
	private static int INDIVIDUALS;
	private static double MUTATION_PROB;
	private static int ITERATIONS;
	
	//GA Constants
	private static String SELECTION_METHOD;
	private static String CROSSOVER_METHOD;
	private static double CROSSOVER_PROB;

	//PBIL Constants
	private static double POS_LEARN_RATE;
	private static double NEG_LEARN_RATE;
	private static double MUTATION_AMOUNT;

		
	public static void main(String[] args) {
		
		INDIVIDUALS = Integer.parseInt(args[1]);
		ITERATIONS = Integer.parseInt(args[6]);
		ALGORITHM = args[7];
		
		if(ALGORITHM.equals("g")) {
			SELECTION_METHOD = args[2];
			CROSSOVER_METHOD = args[3];
			CROSSOVER_PROB = Double.parseDouble(args[4]);
			MUTATION_PROB = Double.parseDouble(args[5]);
			
		} else if (ALGORITHM.equals("p")) {
			POS_LEARN_RATE = Double.parseDouble(args[2]);
			NEG_LEARN_RATE = Double.parseDouble(args[3]);
			MUTATION_PROB = Double.parseDouble(args[4]);
			MUTATION_AMOUNT = Double.parseDouble(args[5]);
			
		} else {
			System.out.println("Incorrect input type\n");
			System.exit(0);
		}
	}
}
