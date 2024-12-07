#!/bin/bash
#spark_compile.sh KDTreeRangeSearchSpark.java KDTree.java PointKD.java


# Define the directory containing the .jar files
JAR_DIR=~/spark-2.3.1-bin-hadoop2.7/jars/

# Build the classpath string by joining all .jar files with a colon separator
CLASSPATH=$(find "$JAR_DIR" -name "*.jar" | tr '\n' ':')

# Remove the trailing colon
CLASSPATH=${CLASSPATH%:}

# Print the classpath string
#echo "$CLASSPATH"

/usr/lib/jvm/java-1.8.0-openjdk/bin/javac -cp $CLASSPATH KDTreeRangeSearchSpark.java KDTree.java PointKD.java
/usr/lib/jvm/java-1.8.0/bin/jar -cvf KDTreeRangeSearchSpark.jar KDTreeRangeSearchSpark.class KDTree*.class PointKD.class 