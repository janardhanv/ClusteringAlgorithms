package com.kmeansmr;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

@SuppressWarnings({ "deprecation", "unused" })
public class KMeansMR extends Configured implements Tool {
	private static final String dataPath = "./input/";
	
	private static String choClusterPath = dataPath+"cho-center.txt";	
	private static String choDataPath = dataPath+"cho.txt";
	private static String iyerDataPath = dataPath+"/iyer.txt";
	private static String iyerClusterPath = dataPath+"/iyer-center.txt";;
	
	public static final String KEY_PREFIX = "centroid.";  

	public static class Map extends MapReduceBase implements
	Mapper<Text, Text, IntWritable, Text> {

		private List<GeneExpression> centers;

		@Override
		public void configure(JobConf conf) {
			super.configure(conf);

			if (this.centers == null) {
				this.centers = new ArrayList<GeneExpression>();
				String[] in;
				List<Double> entries;
				GeneExpression eachC;

				int count = Integer.parseInt(conf.get(KEY_PREFIX + "count"));

				for (int i=0; i < count; i++) {
					in = conf.get(KEY_PREFIX + i).split("\t");

					entries = new ArrayList<Double>();
					for(int index=1; index < in.length ; index++){
						//System.out.println("in["+index+"]: "+in[index]);
						entries.add(Double.parseDouble(in[index]));
					}
					eachC = new GeneExpression(entries, -1);
					centers.add(eachC);					
				}
				System.out.println("Clusters initialized: "+centers);
			}
		}

		@Override
		public void map(Text key, Text input,
				OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {

			// Parse the input in the format of GeneExpression
			//System.out.println("Key sent to Mapper: "+input);
			String inputPath = input.toString();
			String[] in = inputPath.split("\t");

			List<Double> entries = new ArrayList<Double>();

			// Load the inputs from inputPath {cho.txt / iyer.txt}
			for(int index=1; index < in.length ; index++)
				entries.add(Double.parseDouble(in[index]));

			GeneExpression eachC = new GeneExpression(entries, -1);

			double minDist = Double.MAX_VALUE;
			int minIndex = 0;
			int index = 0;

			// Assign the input to a cluster.
			for (GeneExpression center : centers) {
				if (center.eucDist(eachC) < minDist) {
					minDist = center.eucDist(eachC);
					minIndex = index;
				}
				index++;
			}
			//System.out.println("Mapper done");
			// Emit the output as <cluster-id>,<data belonging to this cluster>
			output.collect(new IntWritable(minIndex), new Text(entries.toString()));
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {

		private List<GeneExpression> centers;
		private MultipleOutputs mos;
		@Override
		public void configure(JobConf conf) {
			mos = new MultipleOutputs(conf);
			super.configure(conf);
			if (this.centers == null) {
				this.centers = new ArrayList<GeneExpression>();
				// Load the centers from the conf.
				List<Double> entries;
				int count = Integer.parseInt(conf.get(KEY_PREFIX + "count"));
				String[] in;
				GeneExpression eachC;

				for (int i=0; i < count; i++) {
					in = conf.get(KEY_PREFIX + i).split("\t");
					entries = new ArrayList<Double>();
					for(int index=1; index < in.length ; index++)
						entries.add(Double.parseDouble(in[index]));

					eachC = new GeneExpression(entries, -1);
					centers.add(eachC);
				}
			}
		}

		//@SuppressWarnings("unused")
		@SuppressWarnings("unchecked")
		@Override
		public void reduce(IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output,
				Reporter reporter) throws IOException {
			//int count = 0;
			int newKey = Integer.parseInt(key.toString());
			String[] reducerSplits;
			String temp;
			StringBuffer tempBuff;
			//int count = 0;
			//String[] valueSplits;
			List<ArrayList<Double>> reducerResults = new ArrayList<ArrayList<Double>>();

			System.out.println("cluster being computed: "+key);

			while (values.hasNext()) {
				//System.out.println("value.next: "+values.next().toString());
				temp = values.next().toString();
				//temp = reducerSplits[0].toString();
				reducerSplits = temp.split(",");
				ArrayList<Double> oneRow = new ArrayList<Double>();
				//System.out.println(" reducerSplits length: "+reducerSplits.length);

				for(int index=0; index < reducerSplits.length; index++){
					//System.out.println("reducerSplits["+index+"]: "+reducerSplits[index]);
					if(index==0 || index==reducerSplits.length-1){

						tempBuff = new StringBuffer(reducerSplits[index]);
						reducerSplits[index]="";
						if(index==0)
							for(int z=1; z<tempBuff.length(); z++)
								reducerSplits[index]=reducerSplits[index]+tempBuff.charAt(z);
						else
							for(int z=0; z<tempBuff.length()-1; z++)
								reducerSplits[index]=reducerSplits[index]+tempBuff.charAt(z);
						//System.out.println(" updated reducerSplits: "+reducerSplits[index]);
					}					
					oneRow.add(Double.parseDouble(reducerSplits[index]));					
				}
				reducerResults.add(oneRow);
				//count += Double.parseDouble((reducerSplits[reducerSplits.length-1]));
				//count+=1;
			}

			List<Double> avgSet = new ArrayList<Double>();
			int columns = reducerResults.get(0).size();
			int rows = reducerResults.size();
			double avg;
			for(int index1 = 0 ; index1 < columns; index1++){
				avg = 0;
				for(int index2=0; index2 < rows ; index2++){					
					//reducerResults.set(index, reducerResults.get(index)/count);				
					avg += reducerResults.get(index2).get(index1);				
				}
				avgSet.add(index1, avg/rows);
			}

			String newcentroidString = "";
			String outputString = "";
			
			for(Double each:avgSet)
				newcentroidString = newcentroidString+Double.toString(roundTwoDecimals(each))+"\t";

			for(ArrayList<Double> each: reducerResults){
				for(Double eachDouble: each)
					outputString = outputString+Double.toString(eachDouble)+"\t";
			}
			
			key = new IntWritable(newKey++);

			mos.getCollector("newcentroid", reporter).collect(key, new Text(newcentroidString));
			mos.getCollector("output", reporter).collect(key, new Text(outputString));
			
			output.collect(key, new Text(outputString));
		}
		@Override
	    public void close() throws IOException {
	        // TODO Auto-generated method stub
	    mos.close();
	    }	
	}

	static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	private void startHadoopJob(String jobName, String inputPath, String clusterPath, String outputPath, int numMappers) throws Exception {
		JobConf conf = new JobConf(getConf(), KMeansMR.class);

		conf.setJobName(jobName);
		// the keys are strings.
		conf.setInputFormat(KeyValueTextInputFormat.class);

		conf.setOutputKeyClass(IntWritable.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(Map.class);
		//conf.setCombinerClass(Combiner.class);
		conf.setReducerClass(Reduce.class);

		conf.setNumMapTasks(numMappers);
		
		MultipleOutputs.addNamedOutput(conf, "newcentroid", TextOutputFormat.class , IntWritable.class, Text.class);
	    MultipleOutputs.addNamedOutput(conf, "output", TextOutputFormat.class , IntWritable.class, Text.class);
	    
		FileInputFormat.setInputPaths(conf, new Path(inputPath));
		FileOutputFormat.setOutputPath(conf, new Path(outputPath));

		// Read the clusters file to generate the clusters.
		FileSystem fs = FileSystem.get(conf);
		DataInputStream stream = new DataInputStream(fs.open(new Path(clusterPath)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		int index = 0;
		while ((line = reader.readLine()) != null) {
			conf.set(KEY_PREFIX + index, line);
			index++;
		}

		conf.set(KEY_PREFIX + "count", Integer.toString(index));

		// Write out the clusters
		stream.close();

		
		JobClient.runJob(conf);
		

		

	}

	@Override
	public int run(String[] args) throws Exception {
		String jobName;
		
		String outputPath;
		
		jobName = "kmeans-cho-output";    
		outputPath = "./output/"+jobName+"/0";
		long start = System.currentTimeMillis();
		this.startHadoopJob(jobName, choDataPath, choClusterPath, outputPath, 5);
		
		String choNewClusterPath = "./output/"+jobName+"/"+0+"/newcentroid-r-00000";
		
		
		for(int count = 11 ; count > 0 ;count--){
			outputPath = "./output/"+jobName+"/"+count;
			this.startHadoopJob(jobName, choDataPath, choNewClusterPath, outputPath, 5);
		}
		
		long end = System.currentTimeMillis();
		System.out.println(jobName+ "took " + ((end - start) / 1000) + " seconds");

		
		
		jobName = "kmeans-iyer-output";    
		outputPath = "./output/"+jobName+"/0";
		long start2 = System.currentTimeMillis();
		this.startHadoopJob(jobName, iyerDataPath, iyerClusterPath, outputPath, 5);
		
		String iyerNewClusterPath = "./output/"+jobName+"/"+0+"/newcentroid-r-00000";
		
		
		for(int count = 11 ; count > 0 ;count--){
			outputPath = "./output/"+jobName+"/"+count;
			this.startHadoopJob(jobName, iyerDataPath, iyerNewClusterPath, outputPath, 5);
		}
		
		long end2 = System.currentTimeMillis();
		System.out.println(jobName+ "took " + ((end2 - start2) / 1000) + " seconds");

		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		
		int res = ToolRunner.run(new Configuration(), new KMeansMR(), args);
		System.exit(res);
	}
}