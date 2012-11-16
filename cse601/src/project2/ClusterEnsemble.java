package project2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Jama.Matrix;

public class ClusterEnsemble {

	public static Map<Integer, Integer> cluster_ensemble(Map<Integer, Integer> gene_cluster_kmeans,
			Map<Integer, Integer> gene_cluster_dbscan, Map<Integer, Integer> gene_cluster_hierarchical,
			int num_clusters) {
		//System.out.println("num_clusters "+num_clusters);
		//if(num_clusters >= 5) {
			Map<Integer, Integer> gene_cluster = new HashMap<Integer,Integer>();
			int rows = gene_cluster_kmeans.size();
			int[][] M = new int[rows][3];

			for(int i = 1; i <= rows; i++) {
				/*
			M[i-1][0] = gene_cluster_kmeans.get(i);
			M[i-1][1] = Math.abs(gene_cluster_dbscan.get(i));
			M[i-1][2] = gene_cluster_hierarchical.get(i);
				 */
				if(gene_cluster_kmeans.containsKey(i))
					M[i-1][0] = gene_cluster_kmeans.get(i);
				else
					M[i-1][0] = 1;

				if(gene_cluster_dbscan.containsKey(i))
					M[i-1][1] = Math.abs(gene_cluster_dbscan.get(i));
				else
					M[i-1][1] = 1;
				if(gene_cluster_hierarchical.containsKey(i))
					M[i-1][2] = gene_cluster_hierarchical.get(i);
				else
					M[i-1][2] = 1;
			}

			double M0[][] = new double[rows][num_clusters];
			for(int i = 1; i <= M0.length; i++) {
				int cluster_id = M[i-1][0] - 1;//gene_cluster_kmeans.get(i) - 1;
				for(int j = 0; j < num_clusters; j++) {
					if(cluster_id == j)
						M0[i-1][j] = 1;
					else
						M0[i-1][j] = 0;
				}
			}

			//for(int algo = 1; algo < 3; algo++) {
			double M_current[][] = new double[rows][num_clusters];
			for(int i = 1; i <= M_current.length; i++) {
				int cluster_id = M[i-1][1] - 1;//gene_cluster_dbscan.get(i) - 1;
				if(cluster_id < 0) {
					//System.out.println("cluster_id < 0 " + cluster_id);
					cluster_id = 0;
					//return null;
				}
				for(int j = 0; j < num_clusters; j++) {
					if(cluster_id == j)
						M_current[i-1][j] = 1;
					else
						M_current[i-1][j] = 0;
				}
			}

			int[] res = relabel(M_current,M0);
			for(int i = 0; i < rows; i++) {
				int cluster_id = M[i][1];
				M[i][1] = res[cluster_id - 1];
			}


			for(int i = 0; i < M.length; i++) {
				Map<Integer, Integer> max_count = new HashMap<Integer, Integer>();
				for(int j = 0; j < M[0].length; j++) {
					if(!max_count.containsKey(M[i][j]))
						max_count.put(M[i][j], 1);
					else
						max_count.put(M[i][j],max_count.get(M[i][j]) + 1);
				}

				int max = Integer.MIN_VALUE;
				Iterator<Entry<Integer, Integer>> it = max_count.entrySet().iterator();

				while (it.hasNext()) {
					Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it.next();
					if(entry.getValue() > max)
						max = entry.getValue();
				}
				gene_cluster.put(i+1, max);
			}

			return gene_cluster;
		//}
		//else
			//return gene_cluster_kmeans;
	}
	
	public static int[] relabel(double[][] vals1, double[][] vals2) {
		
	    Matrix A = new Matrix(vals1);
	    Matrix B = new Matrix(vals2);
	    
	    Matrix x = A.solve(B);
	    //System.out.println(vals1[0].length);
	    //System.out.println(vals2[0].length);
	    double[][] X = x.getArray();
	    
	    int[] result = new int[X.length];
	    for(int i = 0; i < X.length; i++) {
	    	double max = X[i][0];
	    	result[i] = 0;
	    	for(int j = 0; j < X[0].length; j++) {
	    		if(X[i][j] > max)
	    			result[i] = j ;
	    		//System.out.print(Math.abs(X[i][j]) + " ");
	    	}
	    }
	    
	    /*for(int i = 0; i < result.length; i++) 
	    	System.out.print(result[i] + " ");
	    */
	    //System.out.println();
	    return result;
	}
}
