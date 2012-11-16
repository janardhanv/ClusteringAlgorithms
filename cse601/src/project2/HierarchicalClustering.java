package project2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HierarchicalClustering {
	
	
	public static Map<Integer,Integer> gene_map;
	public static Map<Integer,ArrayList<Integer>> cluster_map;
	
	public HierarchicalClustering() {
		
		HierarchicalClustering.gene_map = new HashMap<Integer, Integer>();
		HierarchicalClustering.cluster_map = new HashMap<Integer, ArrayList<Integer>>();
	}
	
	public void formClusters(List<GeneExpression> geneSet) {
		
		for(int i = 0; i < geneSet.size(); i++) {
			int gene_id = geneSet.get(i).getId();
			gene_map.put(i, gene_id);
			ArrayList<Integer> cluster = new ArrayList<Integer>();
			cluster.add(gene_id);
			cluster_map.put(i, cluster);
		}
		//System.out.println(HierarchicalClustering.gene_map);
		//System.out.println(HierarchicalClustering.cluster_map);
		
		int N = geneSet.size();
		double max = Double.POSITIVE_INFINITY;
		double[][] distance_matrix= new double[N][N];
		int[] dmin = new int[N];
		
		for(int i= 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(i == j) 
					distance_matrix[i][j] = max;
				else distance_matrix[i][j]= geneSet.get(i).eucDist(geneSet.get(j));
				if(distance_matrix[i][j] < distance_matrix[i][dmin[i]])
					dmin[i] = j;
				}
			}
	
		int clusterSize = cluster_map.size();
		while(clusterSize > Driver.num_of_clusters) {
			// find closest pair of clusters (i1, i2)
			int i1 = 0;
			for(int i = 0;i < N;i++)
				if(distance_matrix[i][dmin[i]] < distance_matrix[i1][dmin[i1]]) 
					i1 =i;
			int i2 = dmin[i1];
			
			// overwrite row i1 with minimum of entries in row i1 and i2
			for(int j =0;j <N;j++)
				if(distance_matrix[i2][j]<distance_matrix[i1][j])
					distance_matrix[i1][j]=distance_matrix[j][i1]=distance_matrix[i2][j];
			distance_matrix[i1][i1] = max;
			
			// infinity-out old row i2 and column i2
			for(int i =0;i <N;i++)
				distance_matrix[i2][i]=distance_matrix[i][i2] = max;
			// update dmin and replace ones that previous pointed to i2 to point to i1
			
			for(int j =0;j <N;j++){
				if(dmin[j]==i2)
					dmin[j]=i1;
				if(distance_matrix[i1][j] < distance_matrix[i1][dmin[i1]])
					dmin[i1]=j;
			}
			
			List<Integer> firstCluster = cluster_map.get(i1);
			List<Integer> secondCluster = cluster_map.get(i2);
			
			firstCluster.addAll(secondCluster);
			
			cluster_map.remove(i2);
			clusterSize--;
			//System.out.println(HierarchicalClustering.clusterMap);
		}
	}
	
	
	public void formClusters2(List<GeneExpression> geneSet) {
		
		Collections.shuffle(geneSet);
		
		for(int i = 0; i < geneSet.size(); i++) {
			int gene_id = geneSet.get(i).getId();
			gene_map.put(i, gene_id);
			ArrayList<Integer> cluster = new ArrayList<Integer>();
			cluster.add(gene_id);
			cluster_map.put(i, cluster);
		}
		//System.out.println(HierarchicalClustering.gene_map);
		//System.out.println(HierarchicalClustering.cluster_map);
		
		int N = geneSet.size();
		double max = Double.POSITIVE_INFINITY;
		double[][]d = new double[N][N];
		int[] dmin = new int[N];
		
		for(int i= 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(i == j) 
					d[i][j] = max;
				else d[i][j]= geneSet.get(i).eucDist(geneSet.get(j));
				if(d[i][j] < d[i][dmin[i]])
					dmin[i] = j;
				}
			}
		
		int clusterSize = cluster_map.size();
		while(clusterSize > 5) {
			// find closest pair of clusters (i1, i2)
			int i1 = 0;
			for(int i = 0;i < N;i++)
				if(d[i][dmin[i]] < d[i1][dmin[i1]]) 
					i1 =i;
			int i2 = dmin[i1];
			
			// overwrite row i1 with minimum of entries in row i1 and i2
			for(int j = 0;j < N;j++)
				if(d[i1][j]<d[i2][j])
					d[i2][j]=d[j][i2]=d[i1][j];
			d[i2][i2] = max;
			
			// infinity-out old row i2 and column i2
			for(int i = 0;i < N;i++)
				d[i1][i]=d[i][i1] = max;
			
			// update dmin and replace ones that previous pointed to i2 to point to i1
			
			for(int j = 0;j < N;j++){
				if(dmin[j]==i1)
					dmin[j]=i2;
				if(d[i2][j] < d[i2][dmin[i2]])
					dmin[i2]=j;
			}
			
			List<Integer> firstCluster = cluster_map.get(i1);
			List<Integer> secondCluster = cluster_map.get(i2);
			
			//System.out.println("i1 = " + i1 + " i2 = " + i2);
			//firstCluster.addAll(secondCluster);
			secondCluster.addAll(firstCluster);
			cluster_map.remove(i1);
			//firstCluster.clear();
			clusterSize--;
			//System.out.println(HierarchicalClustering.clusterMap);
		}
	}
	
	public static void main(String[] args) {
		FileOp io = new FileOp("cho.txt");
		List<GeneExpression> geneSet = io.createInputs();
		//System.out.println("Total genes = " +geneSet.size());
		//System.out.println("Columns in each gene" + geneSet.get(0).size());
		
		HierarchicalClustering test = new HierarchicalClustering();
		test.formClusters2(geneSet);
		//System.out.println(HierarchicalClustering.cluster_map.size());
		//System.out.println(HierarchicalClustering.cluster_map);
		
		int cluster_id = 0;
		Map<Integer, Integer> gene_cluster = new HashMap<Integer,Integer>();
		Iterator<Entry<Integer, ArrayList<Integer>>> it = cluster_map.entrySet().iterator();
		while (it.hasNext()) {
	        Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) it.next();
	        List<Integer> gene_list = entry.getValue();
	        for(int i = 0; i < gene_list.size(); i++) {
	        	gene_cluster.put(gene_list.get(i), cluster_id);
	        }
	        cluster_id++;
	    }
		
		//System.out.println(gene_cluster);
		
		ExternalIndexValidation externalIndexTest = new ExternalIndexValidation();
		System.out.println(externalIndexTest.validate(gene_cluster, io.getExternalIndex()));
		
		InternalIndexValidation internalIndexTest = new InternalIndexValidation();
		System.out.println(internalIndexTest.validate(gene_cluster, geneSet));
		
		
		
	}

}
