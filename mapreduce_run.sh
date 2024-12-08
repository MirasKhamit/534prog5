


if [ $# -eq 0 ]; then
    echo "No arguments provided. Usage: ./mapreduce_run.sh --input-file-path --number-dimensions --min-separetd-by-, --max-separeted-by-,"
    echo "Example:  mapreduce_run.sh kdtree_nodes1.txt 2 30,20 40,90"
    exit 1
fi


~/hadoop-0.20.2/rm-hadoop-tmp-files.sh ~/hadoop-0.20.2/conf/slaves
~/hadoop-0.20.2/bin/hadoop namenode -format
~/hadoop-0.20.2/bin/start-all.sh
~/hadoop-0.20.2/bin/hadoop fs -mkdir /user
~/hadoop-0.20.2/bin/hadoop fs -mkdir /user/$USER
~/hadoop-0.20.2/bin/hadoop fs -chown $USER:$USER /user/$USER
~/hadoop-0.20.2/bin/hadoop dfsadmin -setSpaceQuota 10g /user/$USER
~/hadoop-0.20.2/bin/hadoop fs -mkdir /user/$USER/input
~/hadoop-0.20.2/bin/hadoop fs -put $1 /user/$USER/input
~/hadoop-0.20.2/bin/hadoop fs -rmr /user/mirask/output/
javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar -d . KDTree.java PointKD.java KDTreeRangeSearchMapReduce.java
jar -cvf KDTreeRangeSearchMapReduce.jar KDTreeRangeSearchMapReduce*.class KDTree*.class KDTree$DimensionalComparator.class PointKD.class
~/hadoop-0.20.2/bin/hadoop jar KDTreeRangeSearchMapReduce.jar KDTreeRangeSearchMapReduce input output $2 $3 $4
rm outputMapReduce.txt
~/hadoop-0.20.2/bin/hadoop fs -get /user/mirask/output/part-r-00000 outputMapReduce.txt
echo "Result saved in outputMapReduce.txt"
${HADOOP_HOME}/bin/stop-all.sh

