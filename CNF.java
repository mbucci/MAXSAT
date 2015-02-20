


import java.io.*;


public class CNF {

	public int[] clause;
	public int size;
	
	public CNF(int[] c, int size) {
		this.clause = new int[size];
		this.size = size;
		this.clause = c;
	}
	
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
	
	public void printClause() {
		for (int i = 0; i < this.size; i++) {
			System.out.print(this.clause[i] + " ");	
		}
		System.out.println();
	}
}
