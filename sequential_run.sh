
if [ $# -eq 0 ]; then
    echo "No arguments provided. Usage: ./spark_run.sh --input-file-path --number-dimensions --min-separetd-by-, --max-separeted-by-,"
    echo "Example:  sequential_run.sh ../kdtree_nodes1.txt 2 30,20 40,90"
    exit 1
fi


/usr/lib/jvm/java-1.8.0-openjdk/bin/javac -cp KDTreeRangeSearchSeq.java KDTree.java PointKD.java

rm outputSeq.txt

java KDTreeRangeSearchSeq $1 $2 $3 $4
