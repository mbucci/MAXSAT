/*
 * GA algorithm
 * Solves and inputted CNF using GA
 *
 * NIC - Professor Majercik
 *Nikki Morin, Megan Maher, Kuangji Chen
 * Created: 2/12/15
 * Last Modified: 2/20/15
 *
 */

import java.io.*;
import java.util.*;

public class GA {
	private static final int UNIFORM_CROSSOVER = 1;
	private static final int ONE_POINT_CROSSOVER = 0;
	private static final int TWO_POINT_CROSSOVER = 2;
	
	private static final int TOURNAMENT = 0;
	private static final int RANK = 1;
	private static final int BOLTZMANN = 2;

	private static final double TOURN_SIZE = .1;

	private static final double MUTATION_CONST = .3;
	
	private static int numIndividuals;
	private static int[][] individuals;
	private static int[] scores;
	private static double mutationProb;
	private static int iterations;
	private static String selectionMethod;
	private static int selectionMethodInt;
	private static String crossoverMethod;
	private static double crossoverProb;
	private static Random rand = new Random();

	private static int crossoverType;

	private static int bestIteration = 0;
	private static int bestScore = 0;
	private	static int worstScore = 10000;
	private	static int[] bestIndividual;
	private	static int[] worstIndividual;

	private static int[] rankings;

	private static int tournamentM;
	private static int tournamentK;

	public GA() {

	}

	public GA(int numIndv, double mutProb, int iters, String selectMethod, String crossMethod, double crossProb) {
		numIndividuals = numIndv;
		mutationProb = mutProb;
		iterations = iters;
		selectionMethod = selectMethod;
		crossoverMethod = crossMethod;
		if (crossMethod.equals("uc")) {
			crossoverType = UNIFORM_CROSSOVER;
		}
		else {
			crossoverType = ONE_POINT_CROSSOVER;
		}
		crossoverProb = crossProb;
		if (selectionMethod.equals("rs")) {
			rankings = new int[numIndividuals];
			selectionMethodInt = RANK;
		}
		else if (selectionMethod.equals("ts")) {
			selectionMethodInt = TOURNAMENT;
		}
		else {
			selectionMethodInt = BOLTZMANN;
		}
	}

	// Performs crossover between two parents, and returns two children
	private static int[][] crossover(int[] parent1, int[] parent2, int numV) {
		int randomNum;
		int[][] children = new int[2][numV+1];
		if (crossoverType == UNIFORM_CROSSOVER) {
			// Each element has a 50% chance of being from one parent or the other
			for (int i = 1; i <= numV; i++) {
				randomNum = rand.nextInt(2) + 1;
				if (randomNum == 1) {
					children[0][i] = parent1[i];
					children[1][i] = parent2[i];
				}
				else {
					children[0][i] = parent2[i];
					children[1][i] = parent1[i];
				}
			}
		}
		else {
			// Randomly decide crossover point
			randomNum = rand.nextInt(numV+1) + 1;
			for (int i = 1; i <= numV; i++) {
				if (i<randomNum){
					children[0][i] = parent1[i];
					children[1][i] = parent2[i];
				}
				else {
					children[0][i] = parent2[i];
					children[1][i] = parent1[i];
				}
			}
		}
		return children;
	}

	// loop through individual and randomly mutate
	private static int[] mutate(int[] indiv, int numV) {
		double randomNum;
		for (int i = 1; i <= numV; i++) {
			randomNum = rand.nextDouble();
			if (randomNum <= MUTATION_CONST) {
				if (indiv[i] != 0) indiv[i] = 0;
				else indiv[i] = 1;
			}
		}
		return indiv;
	}

	// Called by Boltzmann and Rank selection methods
	private static int chooseSpecified(int num, int[] array) {
		// sees where specified num falls in the array
		for (int i = 0; i < array.length; i++) {
			if (num < array[i]) {
				return i;
			}
		}
		System.out.println("ERROR HERE BAD, looking for "+num);
		return 0;
	}

	// Called by Boltzmann and Rank selection methods
	private static int chooseSpecifiedB(double num, double[] array) {
		// sees where specified num falls in the array
		for (int i = 0; i < array.length; i++) {
			if (num < array[i]) {
				return i;
			}
		}
		System.out.println("ERROR HERE BAD, looking for "+num);
		return 0;
	}

	private static int getRanking(int score) {
		int i = 0;
		while (rankings[i] != score) {
			i++;
		}
		return i;
	}

	private static void printranks() {
		for (int i = 0; i < numIndividuals; i++) {
			System.out.print(" " + rankings[i] + " :");
		}
	}

	private static void rankGeneration(List<CNF> cnf, int numC) {
		// First clear the previous rankings
		for (int i = 0; i < numIndividuals; i++) {
			rankings[i] = scores[i];
		}
		Arrays.sort(rankings);
	}

	// Weigh individuals based on rank + choose randomly
	private static int[] selectionByRanking(List<CNF> cnf, int numC, int numV) {
		int randomNum, score;
		int[] sums = new int[numIndividuals];
		int currSum = 0;

		for (int i = 0; i < numIndividuals; i++) {
			currSum += (getRanking(scores[i]) + 1);
			sums[i] = currSum;
		}

		randomNum = rand.nextInt(currSum);
		int chosenOne = chooseSpecified(randomNum, sums);
		return individuals[chosenOne];
	}

	// Choose random pool + choose best from pool
	private static int[] tournamentRanking(List<CNF> cnf, int numC, int numV) {
		int poolSize = (int)((double)numIndividuals * TOURN_SIZE);
		if (poolSize < 1) poolSize = 1;
		int[] chosen = new int[poolSize];
		int[][] pool = new int[poolSize][numV+1];
		int randomNum;

		int counter = 0;
		while (counter < poolSize) {
			randomNum = rand.nextInt(numIndividuals);
			if (!Arrays.asList(chosen).contains(randomNum)) {
				chosen[counter] = randomNum;
				pool[counter] = individuals[counter];
				counter++;
			}
		}

		int[] currBestIndiv = pool[0];
		int currBestScore = scores[0];
		int tempScore;
		for (int i = 1; i < poolSize; i++) {
			if (scores[i] > currBestScore) {
				currBestScore = scores[i];
				currBestIndiv = pool[i];
			}
		}
		return currBestIndiv;
	}

	// Weigh individuals according to fitness
	private static int[] boltzmannSelection(List<CNF> cnf, int numC, int numV) {
		double randomNum;
		double[] sums = new double[numIndividuals];
		double currSum = 0;
		double min_value = Math.exp((double)scores[0]);
		double max_value = min_value;
		for (int i = 1; i < numIndividuals; i++) {
			currSum += Math.exp((double)scores[i]);
			sums[i] = currSum;
			if (currSum < min_value) {
				min_value = currSum;
			}
			if (currSum > max_value) {
				max_value = currSum;
			}
		}

		randomNum = (rand.nextDouble() * (max_value - min_value)) + min_value;
		int chosenOne = chooseSpecifiedB(randomNum, sums);
		return individuals[chosenOne];
	}

	private static int evaluateIndividual(List<CNF> cnf, int numC, int indiv) {
		int score = 0;
		for (int i = 0; i < numC; i++) {
			CNF temp = cnf.get(i);
			score += temp.evaluateClause(individuals[indiv]);	
		}
		return score;
	}

	private static void printIndividual(int[] indiv, int numV) {
		for (int i = 1; i <= numV; i++) {
			if (indiv[i] == 1) System.out.print(i + " ");
			else System.out.print("-" + i + " ");	
		}
		System.out.println();
	}

	private static void initPopulation(int numV) {
		int randomNum;
		individuals = new int[numIndividuals][numV+1];
		// Initialize all variables to true or false randomly
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 1; j <= numV; j++) {
				randomNum = rand.nextInt(2);
				individuals[i][j] = randomNum;	
			}
		}
	}

	// Calls appropriate selection method
	private static int[] selectParent(List<CNF> cnf, int numC, int numV) {
		int[] parent = new int[numV+1];
		switch(selectionMethodInt) {
			case RANK: parent = selectionByRanking(cnf, numC, numV); break;
			case TOURNAMENT: parent = tournamentRanking(cnf, numC, numV); break;
			case BOLTZMANN: parent = boltzmannSelection(cnf, numC, numV); break;
		}
		return parent;
	}

	public static void printResults(String fileName, int numC, int numV) {
		double percent = (double)bestScore / (double)numC;
		System.out.println("Results found for file: " + fileName);
		System.out.println("Number of Clauses: " + numC);
		System.out.println("Number of Variables: " + numV);
		System.out.println("--------------------------------------");
		System.out.format("Clauses satisfied: %d -> %%%.1f\n", bestScore, percent*100);
		System.out.print("Assignment: ");
		printIndividual(bestIndividual, numV);
		System.out.println("Found in iteration: " + bestIteration);
		System.out.println("I gave iterations of: " + iterations);
	}

	// Replaces old generation with new generation
	private static void replaceGeneration(int[][] newGeneration, int numV) {
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 1; j <= numV; j++) {
				individuals[i][j] = newGeneration[i][j];
			}
		}
	}

	public static void runGA(List<CNF> cnf, int numC, int numV) {
		int generationCount = 1;
		int score = 0;
		int[][] newGeneration = new int[numIndividuals][numV+1];
		scores = new int[numIndividuals];
		double randomNum;

		initPopulation(numV);
		while (generationCount <= iterations) {
			// Evaluate each individual according to the fitness funciton
			for (int i = 0; i < numIndividuals; i++) {
				score = evaluateIndividual(cnf, numC, i);
				if (score >= bestScore) {
					bestIteration = generationCount;
					bestScore = score;
					bestIndividual = individuals[i];
				}
				scores[i] = score;
			}

			if (selectionMethodInt == RANK)
				rankGeneration(cnf, numC);

			// Create the new generation
			for (int i = 0; i < numIndividuals; i+=2) {
				int[] parent1 = selectParent(cnf, numC, numV);
				int[] parent2 = selectParent(cnf, numC, numV);
				
				// preform crossover
				randomNum = rand.nextDouble();
				if (randomNum <= crossoverProb) {
					int[][] children = crossover(parent1, parent2, numV);
					newGeneration[i] = children[0];
					newGeneration[i+1] = children[1];
				} else {
					newGeneration[i] = parent1;
					newGeneration[i+1] = parent2;
				}

				// Perform mutation
				randomNum = rand.nextDouble();
				if (randomNum <= mutationProb) {
					newGeneration[i] = mutate(newGeneration[i], numV);
				}
				randomNum = rand.nextDouble();
				if (randomNum <= mutationProb) {
					newGeneration[i+1] = mutate(newGeneration[i+1], numV);
				}
			}

			replaceGeneration(newGeneration, numV);
			generationCount++;
		}
	}

	public static void main(String args[]) throws Exception {

	}
}



	// // Weigh individuals according to fitness
	// private static int[] boltzmannSelection(List<CNF> cnf, int numC, int numV) {
	// 	int randomNum;
	// 	int[] sums = new int[numIndividuals];
	// 	int currSum = 0;
	// 	int min_fitness = scores[0];
	// 	for (int i = 1; i < numIndividuals; i++) {
	// 		currSum += scores[i];
	// 		sums[i] = currSum;
	// 		if (scores[i] < min_fitness) {
	// 			min_fitness = scores[i];
	// 		}
	// 	}

	// 	randomNum = rand.nextInt(currSum-min_fitness) + min_fitness;
	// 	int chosenOne = chooseSpecified(randomNum, sums);
	// 	return individuals[chosenOne];
	// }