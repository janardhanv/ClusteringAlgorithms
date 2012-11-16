package project2;

import java.util.Map;

public class ExternalIndexValidation {

	public double validate(Map<Integer,Integer> gene_cluster, Map<Integer,Integer> external_index) {
		double jaccard = 0.0;
		int size = gene_cluster.size();
		//int[][] C = {{1,1,1,0,0},{1,1,1,0,0},{1,1,1,0,0},{0,0,0,1,1},{0,0,0,1,1}};
		//int[][] P = {{1,1,0,0,0},{1,1,0,0,0},{0,0,1,1,1},{0,0,1,1,1},{0,0,1,1,1}};
		
		int[][] C = new int[size][size];
		int[][] P = new int[size][size];
		
		for(int i = 0; i < size; i++) {
			for(int j = i; j < size; j++) {
				if(gene_cluster.get(i) == gene_cluster.get(j))
					C[i][j] = C[j][i] = 1;
				else
					C[i][j] = C[j][i] = 0;
				if(external_index.get(i) == external_index.get(j))
					P[i][j] = P[j][i] = 1;
				else
					P[i][j] = P[j][i] = 0;
			}
		}
		
		int SS = 0;
		int SD = 0;
		int DS = 0;
		
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(C[i][j] == P[i][j]) {
					if(C[i][j] == 1)
						SS++;
				}
				else {
					if(C[i][j] == 1 && P[i][j] == 0)
						SD++;
					else if(C[i][j] == 0 && P[i][j] == 1)
						DS++;
				}
			}
		}
		//System.out.println("SS = " + SS);
		//System.out.println("SD = " + SD);
		//System.out.println("DS = " + DS);
		jaccard = (SS) / (double) (SS + SD + DS);
		return jaccard;
	}
}
