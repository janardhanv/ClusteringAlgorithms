
Part A) For All Algorithms except KMeans Map Reduce:

The folder contains:
	1.	projetc2.jar - runnable .jar file for all the 3 algorithms and cluster ensembling
	2.	cse601 - contains source code for project 2 other than hadoop/map reduce code which can be directly imported into eclipse for testing.
	We have used two external libraries which can be found in cse601/lib.
	Please import these folders in Eclipse to correctly execute all the three algorithms.

To run the code, execute the following command:

java -jar project2.jar file_name
Eg: java -jar project2.jar cho.txt

The output contains:
	1.	External index value (Jaccard coefficient) for the following algorithms:
	⁃	KMeans
	⁃	Hierarchical clustering using MIN/Single link as the distance criteria
	⁃	DBScan
	2.	Internal index (cor(X, Y) = Σ[(xi - E(X))(yi - E(Y))] / [s(X)s(Y)] where E(X) is the mean of X, E(Y) is the mean of the Y values and s(X), s(Y) are standard deviations) for all the above algorithms. Note that correlation is not divided by size of the data;
	3.	External and internal index values after Clustering ensemble algorithm(relabeling and voting)
The algorithms are described in the report.

Part B)
1. All the required files are under KMeans-MapReduce folder in the submission
2. Source files are under KMeansMR/src
3. KMeansMR.jar is present under KMeans-MapReduce folder.
4. To execute KMeansMR.jar just use the script run.sh as below:
	sh run.sh
5. Output will be generated under output folder
6. Most recent output will be under the highest iteration number for particular job
    e.g. for cho.txt , it will be under : output/kmeans-cho-output/11 ; for 12 iterations
   This file contains the output file which has all the cluster ids and the genes present under them.