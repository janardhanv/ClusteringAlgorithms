package project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBScanCluster {

	public static ArrayList<ArrayList<Integer>> clusterList;
	public static ArrayList<Integer> visitedList;
	public static Map<Integer,Integer> gene_cluster_dbscan;
	
	public DBScanCluster() {
		DBScanCluster.clusterList = new ArrayList<ArrayList<Integer>>();
		DBScanCluster.visitedList = new ArrayList<Integer>();
		DBScanCluster.gene_cluster_dbscan = new HashMap<Integer,Integer>();
	}
	
	public double calculateEps(List<GeneExpression> geneSet, int minPts) {
		double eps = 0;
		
		for(int i = 0; i < geneSet.size(); i++) {
			double dist[] = new double[geneSet.size() - 1];
			int count = 0;
			for(int j = 0; j < geneSet.size(); j++) {
				if(j != i) {
					dist[count++] = geneSet.get(i).eucDist(geneSet.get(j));
				}
			}
			Arrays.sort(dist);
			//if(i == 0)
				//System.out.println(dist[0] + " " + dist[dist.length - 1]);
			eps+= dist[minPts - 1];
		}
		//System.out.println(eps);
		return eps/geneSet.size();
	}
	
	
	public void DBScan(List<GeneExpression> geneSet, double eps, int minPts) {
		int index = 0;
		List<Integer> gene_id = new ArrayList<Integer>();
		for(int i = 0; i < geneSet.size(); i++)
			gene_id.add(geneSet.get(i).getId());
		int num_clusters = 0;
		while(geneSet.size() > index) {
			int ID = gene_id.get(index);
			if(!visitedList.contains(ID)) {
				visitedList.add(ID);
				
				ArrayList<Integer> neighborList = getNeighbors(geneSet, gene_id, ID, eps);
				
				if(neighborList.size() >= minPts) {
					int index2 = 0;
					ArrayList<Integer> clusters = new ArrayList<Integer>();
					clusters.add(ID);
					num_clusters++;
					gene_cluster_dbscan.put(ID,num_clusters);
					for(index2 = 0; index2 < neighborList.size(); index2++){
						int neighbor_id = neighborList.get(index2);
						if(!visitedList.contains(neighbor_id)){
							visitedList.add(neighbor_id);
							
							ArrayList<Integer> neighbors2 = getNeighbors(geneSet, gene_id, neighbor_id,eps);
							
							if (neighbors2.size() >= minPts){
								if(!gene_cluster_dbscan.containsKey(neighbor_id)) {
									gene_cluster_dbscan.put(neighbor_id,num_clusters);
									clusters.add(neighbor_id);
								}
							}
						}
					}
					if(clusters.size() > 0)
						clusterList.add(clusters);
				}
				else 
					gene_cluster_dbscan.put(ID, -1);
			}
			index++;
		}
	}
	
	private ArrayList<Integer> appendLists(ArrayList<Integer> neighborList, ArrayList<Integer> neighbors2) {
		for(int i = 0; i < neighbors2.size(); i++) {
			Integer geneIndex = neighbors2.get(i);
			if(!neighborList.contains(geneIndex)) {
				neighborList.add(geneIndex);
			}
		}
		return neighborList;
	}

	public ArrayList<Integer> getNeighbors(List<GeneExpression> geneSet, List<Integer> gene_id, int ID, double eps) {
		int index = gene_id.indexOf(ID);
		//System.out.println(index);
		ArrayList<Integer> neighborList = new ArrayList<Integer>();
		for(int i = 0; i < geneSet.size(); i++) {
			if((geneSet.get(index).eucDist(geneSet.get(i)) < eps) && (index != i)) {
				//if(!visitedList.contains(geneSet.get(i).getId()))
					neighborList.add(geneSet.get(i).getId());
			}
		}
		
		return neighborList;
	}
	
	public static void main(String[] args) {
		FileOp io = new FileOp("iyer.txt");
		List<GeneExpression> geneSet = io.createInputs();
		System.out.println("Total genes = " +geneSet.size());
		System.out.println("Columns in each gene " + geneSet.get(0).size());
		
		DBScanCluster dbscanTest = new DBScanCluster();
		
		int minPts = 10;
		double eps = dbscanTest.calculateEps(geneSet, minPts);
		System.out.println("eps = " + eps);
		
		dbscanTest.DBScan(geneSet, eps * 2.3, minPts);
		System.out.println("Cluster size = " + DBScanCluster.clusterList.size());
		
		System.out.println(gene_cluster_dbscan.size());
		System.out.println(io.getExternalIndex().size());
		
		ExternalIndexValidation externalIndexTest = new ExternalIndexValidation();
		System.out.println(externalIndexTest.validate(gene_cluster_dbscan, io.getExternalIndex()));
		
		InternalIndexValidation internalIndexTest = new InternalIndexValidation();
		System.out.println(internalIndexTest.validate(gene_cluster_dbscan, geneSet));
	}
	
	
}
