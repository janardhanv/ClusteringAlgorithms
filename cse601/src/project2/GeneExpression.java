package project2;

import java.util.ArrayList;
import java.util.List;

// GeneExpression is a base class for Input Data Set which is of size 16 or 12
public class GeneExpression {

	private List<Double> genes;
	private int id;

	public GeneExpression(List<Double> genes, int id){
		// size = 16 | 12
		this.genes = genes;
		this.id = id;
		//System.out.println("Gene Expression Array Initialized: "+ genes);
	}
	
	public GeneExpression average(List<GeneExpression> geneSet){
		List<Double> avgSet = new ArrayList<Double>();
		double avg;
		//System.out.println("geneSet: " +geneSet.size());

		for(int index1=0 ; index1 < Driver.num_of_columns; index1++){
			avg = 0;
			for(int index2 = 0; index2< geneSet.size(); index2++){
				avg += geneSet.get(index2).get(index1);				
			}
			avgSet.add(index1, avg/geneSet.size());
		}		
		return new GeneExpression(avgSet, -1);
	}

	public double eucDist(GeneExpression gene){
		double eucDist = 0;

		for(int index = 0; index < gene.size(); index++){
			double entry = this.genes.get(index) - gene.get(index);
			eucDist+= (entry*entry);
		}		

		return Math.sqrt(eucDist); 
	}

	public int getId(){
		return this.id;
	}

	public Double get(int index){
		return this.genes.get(index);
	}

	
	public int size(){
		return this.genes.size();
	}

	public String toString(){
		return this.genes.toString();
	}

	public void add(Double d){
		this.genes.add(d);
	}
}
