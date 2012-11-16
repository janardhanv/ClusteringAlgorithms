package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Driver {
	public static int num_of_clusters = 5;
	public static int num_of_columns;
	public static Map<Integer, Integer> gene_cluster_kmeans;
	public static Map<Integer, Integer> gene_cluster_hierarchical;
	
	public static void main(String[] args) {
		// Generate a list of items.
		List<GeneExpression> geneSet = new ArrayList<GeneExpression>();	    
		String filename = args[0];
		if(filename.compareTo("cho.txt") == 0)
			num_of_clusters = 5;
		if(filename.compareTo("iyer.txt") == 0) {
			num_of_clusters = 10;
		}
			
		
		FileOp io = new FileOp(filename);
		geneSet = io.createInputs();
		num_of_columns = geneSet.get(0).size();
		Map<Integer, Integer> external_index = io.getExternalIndex();
		
		//Test K-Means algorithm
				
		List<Integer> clusterIndex = new ArrayList<Integer>();
		Iterator<Entry<Integer, Integer>> it = external_index.entrySet().iterator();
		
		while (it.hasNext()) {
			 Entry<Integer, Integer> entry = (Entry<Integer, Integer>) it.next();
	        clusterIndex.add(entry.getValue());
	    }
		System.out.println("Executing KMeans \n");
		KMeans(geneSet, clusterIndex, external_index);
		//End of K-Means  
		
		//Test DBScan
		System.out.println("\nExecuting DBScan \n");
		DBScan(geneSet, external_index);
		//End DBScan
		
		//Test Aggleromative clustering
		System.out.println("\nExecuting Hierarchical clustering \n");
		Hierarchical(geneSet, external_index);
		//End Aggleromative clustering
		
		System.out.println("\nExecuting Ensembling using re-labelling and voting \n");
		Map<Integer, Integer> gene_cluster_ensemble = ClusterEnsemble.cluster_ensemble(gene_cluster_kmeans, DBScanCluster.gene_cluster_dbscan, gene_cluster_hierarchical, num_of_clusters);
		
		ExternalIndexValidation externalIndexTest = new ExternalIndexValidation();
		System.out.println("External Index in Ensemble = " + externalIndexTest.validate(gene_cluster_ensemble, external_index));
		
		InternalIndexValidation internalIndexTest = new InternalIndexValidation();
		System.out.println("Internal Index in Ensemble = " + internalIndexTest.validate(gene_cluster_ensemble, geneSet));
		
	}
	
	public static void KMeans(List<GeneExpression> geneSet, List<Integer> clusterIndex,Map<Integer, Integer> external_index) {
		KMeans clusterer = new KMeans();
		
		
		Map<Integer, ArrayList<Integer>> results = clusterer.cluster(geneSet, num_of_clusters, clusterIndex);
		
		int cluster_id = 0;
		gene_cluster_kmeans = new HashMap<Integer,Integer>();
		Iterator<Entry<Integer, ArrayList<Integer>>> it = results.entrySet().iterator();
		while (it.hasNext()) {
	        Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) it.next();
	        List<Integer> gene_list = entry.getValue();
	        //System.out.println(gene_list);
	        for(int i = 0; i < gene_list.size(); i++) {
	        	gene_cluster_kmeans.put(gene_list.get(i), cluster_id);
	        }
	        cluster_id++;
	    }
		
		//System.out.println(gene_cluster_kmeans);
		
		ExternalIndexValidation externalIndexTest = new ExternalIndexValidation();
		System.out.println("External Index in K-Means = " + externalIndexTest.validate(gene_cluster_kmeans, external_index));
		
		InternalIndexValidation internalIndexTest = new InternalIndexValidation();
		System.out.println("Internal Index in K-Means = " + internalIndexTest.validate(gene_cluster_kmeans, geneSet));
	}
	
	public static void DBScan(List<GeneExpression> geneSet, Map<Integer, Integer> external_index) {
		DBScanCluster dbscanTest = new DBScanCluster();
		
		int minPts = num_of_clusters;
		double eps = dbscanTest.calculateEps(geneSet, minPts);
		//System.out.println("eps = " + eps);
		
		if(num_of_clusters == 5)
			eps = eps * 3;
		else
			eps = eps * 2.3;
		dbscanTest.DBScan(geneSet, eps, minPts);
		//System.out.println("Cluster size = " + DBScanCluster.clusterList.size());
		
		//System.out.println(DBScanCluster.gene_cluster_dbscan.size());
		//System.out.println(external_index.size());
		
		//System.out.println(DBScanCluster.gene_cluster_dbscan.containsValue(0));
		
		ExternalIndexValidation externalIndexTest = new ExternalIndexValidation();
		System.out.println("External Index in DBScan = " + externalIndexTest.validate(DBScanCluster.gene_cluster_dbscan, external_index));
		
		InternalIndexValidation internalIndexTest = new InternalIndexValidation();
		System.out.println("Internal Index in DBScan = " + internalIndexTest.validate(DBScanCluster.gene_cluster_dbscan, geneSet));
	}
	
	public static void Hierarchical(List<GeneExpression> geneSet, Map<Integer, Integer> external_index) {
		
		HierarchicalClustering test = new HierarchicalClustering();
		test.formClusters2(geneSet);
		//System.out.println(HierarchicalClustering.cluster_map.size());
		//System.out.println(HierarchicalClustering.cluster_map);
		
		int cluster_id = 0;
		gene_cluster_hierarchical = new HashMap<Integer,Integer>();
		Iterator<Entry<Integer, ArrayList<Integer>>> it = HierarchicalClustering.cluster_map.entrySet().iterator();
		
		while (it.hasNext()) {
	        Entry<Integer, ArrayList<Integer>> entry = (Entry<Integer, ArrayList<Integer>>) it.next();
	        List<Integer> gene_list = entry.getValue();
	        for(int i = 0; i < gene_list.size(); i++) {
	        	gene_cluster_hierarchical.put(gene_list.get(i), cluster_id);
	        }
	        cluster_id++;
	    }
		
		//System.out.println(gene_cluster_hierarchical.values());
		
		ExternalIndexValidation externalIndexTest = new ExternalIndexValidation();
		System.out.println("External Index in Hierarchical = " + externalIndexTest.validate(gene_cluster_hierarchical, external_index));
		
		InternalIndexValidation internalIndexTest = new InternalIndexValidation();
		System.out.println("Internal Index in Hierarchical = " + internalIndexTest.validate(gene_cluster_hierarchical, geneSet));
	}
}
