/*
 * CNF Class
 * Class for a CNF clause
 *
 * NIC - Professor Majercik
 * Max Bucci, Nikki Morin, Megan Maher, Kuangji Chen
 * Created: 2/11/15
 * Last Modified: 2/20/15
 *
 */


import java.io.*;


public class CNF {

	public int[] clause;
	public int size;
	
	//Constructor, set the clause and its size
	public CNF(int[] c, int size) {
		this.clause = new int[size];
		this.size = size;
		this.clause = c;
	}
	
	//Evaluate a clause using a given evaluation arry (i.e. individual). 
	//Returns 1 if at least one assignment is true, 0 otherwise. 
	public int evaluateClause(int[] evalArray) {
		
		int count = 0;
		for(int i = 0; i < this.size; i++) {
			if (this.clause[i] < 0) {
				if(evalArray[-this.clause[i]] == 0) count++;	
			} else {
				if (evalArray[this.clause[i]] == 1) count++;
			}
		}
		
		if (count >= 1) return 1;
		else return 0;
	}
	
	
	//Print the clause
	public void printClause() {
		for (int i = 0; i < this.size; i++) {
			System.out.print(this.clause[i] + " ");	
		}
		System.out.println();
	}
}
