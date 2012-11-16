package project2;

import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class InternalIndexValidation {

	
	public double validate(Map<Integer,Integer> gene_cluster, List<GeneExpression> geneSet) {
		double correlation = 0.0;
		int size = geneSet.size();
		
		double[] D = getDistanceMatrix(geneSet);
		double[] C = new double[size*size];
		int count = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(gene_cluster.get(i) == gene_cluster.get(j))
					C[count] = 1;
				else
					C[count] = 0;
				count++;
			}
		}
		//System.out.println("size = " + size + " count = " + count);
		correlation = new PearsonsCorrelation().correlation(D, C);
		//System.out.println(getPearsonCorrelation(C, D));
		return Math.abs(correlation * (size*size - 1));
	}
	
	public static double[] getDistanceMatrix(List<GeneExpression> geneSet) {
		int N = geneSet.size();

		double[] distance_matrix= new double[N*N];
		int count = 0;
		for(int i= 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(i == j) 
					distance_matrix[count] = 0;
				else 
					distance_matrix[count]= geneSet.get(i).eucDist(geneSet.get(j));
				count++;
				}
			}
		
		//System.out.println("N = " + N + " count = " + count);
		return distance_matrix;
	}
	
	public static double getPearsonCorrelation(double[] array1,double[] array2){
		double result = 0;
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = array1[0];
		double mean_y = array2[0];
		for(int i=2;i<array1.length+1;i+=1){
			double sweep =Double.valueOf(i-1)/i;
			double delta_x = array1[i-1]-mean_x;
			double delta_y = array2[i-1]-mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}
		double pop_sd_x = (double) Math.sqrt(sum_sq_x/array1.length);
		double pop_sd_y = (double) Math.sqrt(sum_sq_y/array1.length);
		double cov_x_y = sum_coproduct ;// scores1.length;
		result = cov_x_y / (pop_sd_x*pop_sd_y);
		return Math.abs(result);
	}
	
	
}
