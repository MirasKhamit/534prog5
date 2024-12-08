#!/bin/sh

# Check if at least one argument is provided
if [ $# -eq 0 ]; then
  echo "No arguments provided. Usage: ./spark_run.sh --master --total-executor-cores --input-file-path --number-dimensions --min-separetd-by-, --max-separeted-by-,"
  echo "Example:  spark_run.sh spark://cssmpi24h.uwb.edu:58200 4 kdtree_nodes.txt 2 30,20 40,90"
  exit 1
fi
#spark://cssmpi24h.uwb.edu:58200

# Define the directory containing the .jar files
JAR_DIR=~/spark-2.3.1-bin-hadoop2.7/jars/

# Build the classpath string by joining all .jar files with a colon separator
CLASSPATH=$(find "$JAR_DIR" -name "*.jar" | tr '\n' ':')

# Remove the trailing colon
CLASSPATH=${CLASSPATH%:}

# Print the classpath string
#echo "$CLASSPATH"

~/spark-2.3.1-bin-hadoop2.7/sbin/start-master.sh
~/spark-2.3.1-bin-hadoop2.7/sbin/start-slaves.sh

/usr/lib/jvm/java-1.8.0-openjdk/bin/javac -cp $CLASSPATH KDTreeRangeSearchSpark.java KDTree.java PointKD.java
/usr/lib/jvm/java-1.8.0/bin/jar -cvf KDTreeRangeSearchSpark.jar KDTreeRangeSearchSpark.class KDTree*.class PointKD.class 

rm outputSpark.txt
spark-submit --class KDTreeRangeSearchSpark --master $1 --executor-memory 1G --total-executor-cores $2 KDTreeRangeSearchSpark.jar $3 $4 $5 $6

~/spark-2.3.1-bin-hadoop2.7/sbin/stop-all.sh

