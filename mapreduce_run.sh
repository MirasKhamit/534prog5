
~/hadoop-0.20.2/rm-hadoop-tmp-files.sh ~/hadoop-0.20.2/conf/slaves
~/hadoop-0.20.2/bin/hadoop namenode -format
~/hadoop-0.20.2/bin/start-all.sh
~/hadoop-0.20.2/bin/hadoop fs -mkdir /user
~/hadoop-0.20.2/bin/hadoop fs -mkdir /user/$USER
~/hadoop-0.20.2/bin/hadoop fs -chown $USER:$USER /user/$USER
~/hadoop-0.20.2/bin/hadoop dfsadmin -setSpaceQuota 10g /user/$USER
~/hadoop-0.20.2/bin/hadoop fs -mkdir /user/$USER/input
~/hadoop-0.20.2/bin/hadoop fs -put /home/NETID/mirask/mass_java_appl/Applications/RangeSearch/MapReduce/input/points1mil.txt /user/$USER/input
#~/hadoop-0.20.2/bin/hadoop fs -put /home/NETID/mirask/prog5/kdtree_nodes1.txt  /user/$USER/input
~/hadoop-0.20.2/bin/hadoop fs -rmr /user/mirask/output/
javac -classpath ${HADOOP_HOME}/hadoop-0.20.2-core.jar -d . KDTree.java PointKD.java KDTreeRangeSearchMapReduce.java
jar -cvf KDTreeRangeSearchMapReduce.jar KDTreeRangeSearchMapReduce*.class KDTree*.class KDTree$DimensionalComparator.class PointKD.class
~/hadoop-0.20.2/bin/hadoop jar KDTreeRangeSearchMapReduce.jar KDTreeRangeSearchMapReduce input output 2 30,20 40,90
rm outputMapReduce.txt
~/hadoop-0.20.2/bin/hadoop fs -get /user/mirask/output/part-r-00000 outputMapReduce.txt
${HADOOP_HOME}/bin/stop-all.sh
