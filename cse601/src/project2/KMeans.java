package project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
public class KMeans {
	protected static final int MAX_ITERATIONS = 1000;
	protected static final double EPSILON = 1E-8;

	public Map<Integer, ArrayList<Integer>> cluster(List<GeneExpression> inputs, int numClusters, List<Integer> clusterIndex) {
		List<GeneExpression> clusters = this.initialClusters(inputs, numClusters, clusterIndex);
		// Use these clusters to cluster the inputs.
		return this.clusterInputs(inputs, clusters);
	}

	private List<GeneExpression> initialClusters(List<GeneExpression> inputs, int clusters, List<Integer> clusterIndex) {
		List<GeneExpression> initialClusters = new ArrayList<GeneExpression>();
		Map<Integer, GeneExpression> initialGenes = new HashMap<Integer,GeneExpression>();


		int count = 0;
		for (int index : clusterIndex){
			if(!initialGenes.containsKey(index)){
				initialGenes.put(index, inputs.get(count));
			}
			count++;			
		}
	
		for(int index: initialGenes.keySet()){
			initialClusters.add(initialGenes.get(index));
		}
		//System.out.println("Initial Clusters are: "+initialClusters);
		
		return initialClusters;
	}

	protected Map<Integer, ArrayList<Integer>> clusterInputs(List<GeneExpression> inputs, List<GeneExpression> clusters){
		Map<GeneExpression, List<GeneExpression>> results = new HashMap<GeneExpression, List<GeneExpression>>();
		List<GeneExpression> newClusters;
		GeneExpression tempPoint;
		boolean changed = true;
		int iterations = 0;

		while (changed && iterations < MAX_ITERATIONS) {
			changed = false;

			// Reset the table.
			results.clear();
			for (GeneExpression cluster : clusters) {
				results.put(cluster, new ArrayList<GeneExpression>());
			}
			// Cluster the points.
			for (GeneExpression input : inputs) {
				// Find the nearest cluster.
				tempPoint = null;

				// problem is here
				for (GeneExpression key : results.keySet()) { 	
					if (tempPoint == null || input.eucDist(key) < input.eucDist(tempPoint)) {
						tempPoint = key;
					}
				}
				results.get(tempPoint).add(input);
			}
			newClusters = new ArrayList<GeneExpression>();

			int index = 0;

			for (GeneExpression cluster : clusters) {
				//System.out.println("index: " + index);
				tempPoint = cluster.average(results.get(cluster));
				newClusters.add(tempPoint);

				double delta = Math.abs(cluster.eucDist(tempPoint));
				//System.out.println("delta: " + delta);

				if (delta > EPSILON && !changed){
					changed = true;
				}
				index++;
				//System.out.println("Genes present in cluster are: "+results.get(cluster).size());
			}

			clusters = new ArrayList<GeneExpression>(newClusters);
			//System.out.println("clusters: "+clusters);
			iterations++;
			//System.out.println("iterations: " + iterations);

		}
		// Data Structure containing final result i.e <cluster id -> <gene ids>>
		Map<Integer, ArrayList<Integer>> clusterOfGenes= new HashMap<Integer, ArrayList<Integer>>();
		
		//display final output
		int index =1 ;
		for (GeneExpression gene: results.keySet()){
			List<GeneExpression> result = new ArrayList<GeneExpression>(results.get(gene));
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			//out.println("Cluster ID :"+index+" contains gene ids:");
			String temp = "";
			for(int j=0; j<result.size(); j++){
				
				GeneExpression g = result.get(j);
				temp=temp+g.getId()+",";
				tempList.add(g.getId());				
			}
			clusterOfGenes.put(index, tempList);
			//System.out.println(temp);
			index++;
		}
		return clusterOfGenes;
	}
}
