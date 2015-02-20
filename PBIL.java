/*
 * PBIL algorithm
 * Solves and inputted CNF using PBIL
 *
 * NIC - Professor Majercik
 * Max Bucci
 * Created: 2/11/15
 * Last Modified: 2/20/15
 *
 */

import java.io.*;
import java.util.*;


public class PBIL{
	
	//Algorithm Constants
	private static String algorithm;
	private static int numIndividuals;
	private static double mutationProb;
	private static double posLearnRate;
	private static double negLearnRate;
	private static double mutationAmount; 
	private static int iterations;

	//Algorithm Data Structures
	private static double[] probVector;
	private static int[][] individuals;
	
	//Algorithm Variables
	private static int bestIteration = 0;
	private static int bestScore = 0;
	private	static int worstScore = 10000;
	private	static int[] bestIndividual;
	private	static int[] worstIndividual;
	
	//Random Number Generator
	private static Random rand = new Random();
	
	
	//Constructor, aquires algorithm constants
	public PBIL(int numInd, double posLearn, double negLearn, double mutProb, double mutAmt, int iter) {
		
		numIndividuals = numInd;
		posLearnRate = posLearn;
		negLearnRate = negLearn;
		mutationAmount = mutAmt;
		mutationProb = mutProb;
		iterations = iter;
	}
	
	
	//Initialize the probability vector to 0.5. Note that the size of
	//the vector is one great than the number of variables. This is done
	//So that the index of the vector can be used as the variable number. 
	private static void initProbVector(int numV) {
		
		probVector = new double[numV+1];
		Arrays.fill(probVector, 0.5);
	}
	
	
	//Initialize individuals
	private static void initIndividuals(int numV) {
		
		individuals = new int[numIndividuals][numV+1];
		//Initially set all variables to 0 (i.e. false)
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 1; j <= numV; j++) {
				individuals[i][j] = 0;	
			}
		}
	}
	
	
	//Workhouse of the class, takes file specific paramters (the CNF, number
	//or variable and number of clauses) and runs the PBIL algorithm on the
	//CNF using previously provided constants.
	public static void runPBIL(List<CNF> cnf, int numC, int numV) {
				
		initProbVector(numV);	
		initIndividuals(numV);
		
		bestIndividual = new int[numV];
		worstIndividual = new int[numV];
		
		int count = 1;
		do {
			int score = 0;
			//Generating and evaluating individuals 
			for (int i = 0; i < numIndividuals; i++) {
				individuals[i] = generateIndividualWithProb(numV);	
				score = evaluateIndividual(cnf, numC, i);
				if (score >= bestScore) {
					bestIteration = count;
					bestScore = score;
					bestIndividual = individuals[i];
				}
				if (score <= worstScore) {
					worstScore = score;
					worstIndividual = individuals[i];
				}
			}
			
			
			//Update towards the best individual
			for (int i = 1; i <= numV; i++) {
				probVector[i] = (probVector[i]*(1.0-posLearnRate)) + (bestIndividual[i]*posLearnRate);
			}
			
			
			//Update away from the worst individual
			for (int i = 1; i <= numV; i++) {
				if (bestIndividual[i] != worstIndividual[i]) {
					probVector[i] = (probVector[i]*(1.0-negLearnRate)) + (bestIndividual[i]*negLearnRate);
				}
			}
			
			//Mutate
			int mutateDirection = 0;
			for (int i = 1; i <= numV; i++) {
				if (rand.nextDouble() < mutationProb) {
					if (rand.nextDouble() > 0.5) mutateDirection = 1;
					else mutateDirection = 0;
					
					probVector[i] = probVector[i]*(1.0-mutationAmount) + (mutateDirection*mutationAmount);
				}
			}
			count++;	
		} while (count <= iterations);
	}
	
	
	//Print the results from PBIL
	public static void printResults(String fileName, int numC, int numV) {
		
		double percent = (double)bestScore / (double)numC;
		System.out.println("Results found for file: " + fileName);
		System.out.println("Number of Clauses: " + numC);
		System.out.println("Number of Variables: " + numV);
		System.out.println("--------------------------------------");
		System.out.format("Clauses satisfied: %d -> %.1f%%\n", bestScore, percent*100);
		System.out.print("Assignment: ");
		printIndividual(bestIndividual, numV);
		System.out.println("Found in iteration: " + bestIteration);
		System.out.println();
		//printProbVector(numV);
	}
	
	
	//Print a given individuals specific variable assignment
	private static void printIndividual(int[] indiv, int numV) {
		
		for (int i = 1; i <= numV; i++) {
			if (indiv[i] == 1) System.out.print(i + " ");
			else System.out.print("-" + i + " ");	
		}
		System.out.println();
	}
	
	
	//Print the probability vector, used for debugging. 
	private static void printProbVector(int numV) {
		
		for (int i = 1; i <= numV; i++) {
			System.out.format("%.4f ", probVector[i]);	
		}
		System.out.println();
	}
	
	
	//Evaluate an individual. Its score is the number of satisfied clauses
	private static int evaluateIndividual(List<CNF> cnf, int numC, int indiv) {
		
		int score = 0;
		for (int i = 0; i < numC; i++) {
			CNF temp = cnf.get(i);
			score += temp.evaluateClause(individuals[indiv]);	
		}
		return score;
	}
	
	
	//Generate an individual from the probability vector 
	private static int[] generateIndividualWithProb(int numV) {
		
		int[] temp = new int[numV+1];
		for (int i = 1; i <= numV; i++) {
			if (rand.nextDouble() < probVector[i]) temp[i] = 0;
			else temp[i] = 1;
		}
		return temp;
	}
}
