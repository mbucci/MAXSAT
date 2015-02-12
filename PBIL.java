

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
	
	public PBIL(int numInd, double posLearn, double negLearn, double mutProb, double mutAmt, int iter) {
		
		numIndividuals = numInd;
		posLearnRate = posLearn;
		negLearnRate = negLearn;
		mutationAmount = mutAmt;
		mutationProb = mutProb;
		iterations = iter;
	}
	
	public static void runPBIL(List<CNF> cnf, int numC, int numV) {
			
		
		initProbVector(numC);	
		initIndividuals(numV);
		
		int count = 0;
		do {
			for (int i = 0; i < numIndividuals; i++) {
				System.out.println(evaluateIndividual(cnf, numC, i));	
			
			}
			
			
		} while (count < iterations);
	
		
	}
	
	public static void printResults() {
		
		
		
	}
	
	private static void initProbVector(int size) {
		
		probVector = new double[size];
		Arrays.fill(probVector, 0.5);
	}
	
	private static void initIndividuals(int numV) {
		
		
		individuals = new int[numIndividuals][numV+1];
		//Initially set all variables to 0 (i.e. false)
		for (int i = 0; i < numIndividuals; i++) {
			for (int j = 1; j <= numV; j++) {
				individuals[i][j] = 0;	
			}
		}
	}
	
	private static int evaluateIndividual(List<CNF> cnf, int numC, int indiv) {
		
		int score = 0;
		for (int i = 0; i < numC; i++) {
			//System.out.println(i);
			CNF temp = cnf.get(i);
			//temp.printClause();
			score += temp.evaluateClause(individuals[indiv]);	
		}
		return score;
	}
}
