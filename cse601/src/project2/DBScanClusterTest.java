package project2;

import java.util.ArrayList;
import java.util.Arrays;

public class DBScanClusterTest {

	public static ArrayList<ArrayList<Integer>> clusterList;
	public static ArrayList<Integer> visitedList;
	
	public float calculateEps(ArrayList<ArrayList<Float>> data, int minPts) {
		float eps = 0;
		
		for(int i = 0; i < data.size(); i++) {
			float dist[] = new float[data.size() - 1];
			int count = 0;
			for(int j = 0; j < data.size(); j++) {
				if(j != i) {
					dist[count++] = distance(data.get(i), data.get(j));
				}
			}
			Arrays.sort(dist);
			eps+= dist[minPts];
		}
		
		return eps/data.size();
	}
	
	
	public void DBScan(ArrayList<ArrayList<Float>> data, float eps, int minPts) {
		int index = 0;
		while(data.size() > index) {
			if(!visitedList.contains(index)) {
				visitedList.add(index);
				
				ArrayList<Integer> neighborList = getNeighbors(data, index, eps);
				
				if(neighborList.size() >= minPts) {
					int index2 = 0;
					while(neighborList.size() > index2){

						if(!visitedList.contains(index2)){
							visitedList.add(index2);
							ArrayList<Integer> neighbors2 = getNeighbors(data,index2,eps);
							if (neighbors2.size() >= minPts){
								neighborList = appendLists(neighborList, neighbors2);
							}
						} 
						index2++;
					}
				}
				clusterList.add(neighborList);
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

	public ArrayList<Integer> getNeighbors(ArrayList<ArrayList<Float>> data,int index, float eps) {
		ArrayList<Integer> neighborList = new ArrayList<Integer>();
		for(int i = 0; i < data.size(); i++) {
			if(distance(data.get(index), data.get(i)) < eps) {
				neighborList.add(i);
			}
		}
		return neighborList;
	}

	private float distance(ArrayList<Float> arrayList, ArrayList<Float> arrayList2) {
		float dist = 0;
		for(int i = 0; i < arrayList.size(); i++) {
			float diff = arrayList.get(i) - arrayList2.get(i);
			dist+= (diff * diff); 
		}
		return (float) Math.sqrt(dist);
	}
}
