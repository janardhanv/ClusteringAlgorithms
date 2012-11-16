package project2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class FileOp {
	private File fileHandler;
	private Scanner scan;
	private int rows, columns;
	private Map<Integer,Integer> externalIndex;
	private List<GeneExpression> inputs;
	
	
	public FileOp(String nameOfFile){
		fileHandler = new File(nameOfFile);		
		try {
			scan = new Scanner(fileHandler);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

	public List<GeneExpression> createInputs(){

		List<Double> geneData;		
		this.inputs = new ArrayList<GeneExpression>();		
		GeneExpression gene;
		int id;

		// ignore the first line;
		/*String line = scan.nextLine();
		String[] columns = line.split("\t");

		this.rows = Integer.parseInt(columns[0]);
		this.columns = Integer.parseInt(columns[1]);
		*/

		this.externalIndex = new TreeMap<Integer,Integer>();		

		while(scan.hasNext()){

			geneData = new ArrayList<Double>();
			String line = scan.nextLine();
			String[] splits = line.split("\t");

			int extIndex = Integer.parseInt((splits[1]));
			
			// exclude genes which are Outliers
			if(extIndex > -1){
				id = Integer.parseInt(splits[0]);

				this.externalIndex.put(id,extIndex);

				for(int index = 2; index<splits.length; index++){
					geneData.add(Double.parseDouble(splits[index]));
				}
				
				gene = new GeneExpression(geneData, id);
				this.inputs.add(gene);
				
			}
		}
		//System.out.println("input: "+inputs);
		return this.inputs;
	}

	public int getRowSize(){
		return this.rows;
	}

	public int getColumnSize(){
		return this.columns;
	}

	public Map<Integer,Integer> getExternalIndex(){
		return this.externalIndex;
	}

	/*
	public static void main(String[] args){
		FileOp f = new FileOp("/Users/saurabhtalbar/Documents/workspace/ClusteringAlgo/src/cho.txt");
		f.createInputs();
	}
	*/
}