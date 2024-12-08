/*
    AJ Plumlee
    CSS 534 Final Project
    MPI Implementation of K-D Tree Range Search
*/

#include <iostream>
#include <vector>
#include <algorithm>
#include <fstream>
#include <sstream>
#include "Timer.h"
#include "mpi.h"
using namespace std;

// K-D tree
template <typename T>
class KDTree {
public:
    struct Point {
        int index;
        vector<T> coords;
    };

    // constructor
    KDTree() : root(NULL) {}

    // insert a point into the tree
    void insert(const Point& point) {
        root = insertRecursive(root, 0, point);
    }

    // run a range search
    std::vector<Point> rangeSearch(const Point& min, const Point& max) const {
        std::vector<Point> result;
        rangeSearchRecursive(root, 0, min, max, result);
        return result;
    }

private:
    struct Node {
        Point point;
        Node* left;
        Node* right;

        Node(const Point& p) : point(p), left(NULL), right(NULL) {}
    };

    Node* root;

    // recursive function to insert a point into the K-D Tree
    Node* insertRecursive(Node* node, int depth, const Point& point) {
        if (node == NULL) {
            return new Node(point);
        }

        int cd = depth % point.coords.size();
        if (point.coords[cd] < node->point.coords[cd]) {
            node->left = insertRecursive(node->left, depth + 1, point);
        } else {
            node->right = insertRecursive(node->right, depth + 1, point);
        }

        return node;
    }

    // recursive function to run a range search
    void rangeSearchRecursive(Node* node, int depth, const Point& min, const Point& max, std::vector<Point>& result) const {
        if (node == NULL) {
            return;
        }

        // check if node within range
        int cd = depth % min.coords.size();
        if (node->point.coords[cd] >= min.coords[cd] && node->point.coords[cd] <= max.coords[cd]) {
            bool inRange = true;
            for (int i = 0; i < min.coords.size(); ++i) {
                if (node->point.coords[i] < min.coords[i] || node->point.coords[i] > max.coords[i]) {
                    inRange = false;
                    break;
                }
            }
            // add to results if within range
            if (inRange) {
                result.push_back(node->point);
            }
        }

        // if greater than min, traverse left
        if (min.coords[cd] < node->point.coords[cd]) {
            rangeSearchRecursive(node->left, depth + 1, min, max, result);
        }

        // if less than max, traverse right
        if (max.coords[cd] > node->point.coords[cd]) {
            rangeSearchRecursive(node->right, depth + 1, min, max, result);
        }
    }
};

// read file, output coordinates
vector<double> readInputFile(string path)
{
    // open input file
    ifstream inFile(path.c_str());

    // read input file
    string line;
    vector<double> coords;
    while (getline(inFile, line))
    {
        // read line
        stringstream ss(line);
        string temp;
        char del = ',';
        while (getline(ss, temp, del))
        {
            std::stringstream ss(temp);
            double num;
            ss >> num;
            coords.push_back(num);
        }
    }

    // close file
    inFile.close();
    return coords;
}

// read bound
KDTree<double>::Point readBound(string bound)
{
    KDTree<double>::Point p;
    stringstream ssb(bound);
    vector<double> bcoords;
    string temp;
    char del = ',';
    while (getline(ssb, temp, del))
    {
        std::stringstream ss(temp);
        double num;
        ss >> num;;
        bcoords.push_back(num);
    }
    p.index = -1;
    p.coords = bcoords;
    return p;
}

// application process entry-point
int main(int argc, char *argv[])
{
    // read args
    if (argc != 5)
    {
        cerr << "Usage: KDTreeMPI dimensions input lower upper" << endl;
        return -1;
    }

    int dimensions = atoi(argv[1]); // read dimensions
    string fPath = argv[2];     // read file path
    string lowerb = argv[3];     // lower search bounds
    string upperb = argv[4];     // upper search bounds

    // read file, convert to coordinates
    vector<double> coords = readInputFile(fPath);

    // copy coordinate vector to array
    double* data = new double[coords.size()];
    std::copy(coords.begin(), coords.end(), data);

    // read bounds 
    KDTree<double>::Point lower = readBound(lowerb);
    KDTree<double>::Point upper = readBound(upperb);

    // start timer
    Timer time;
    time.start();

    // start mpi
    int my_rank;
    int mpi_size;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &mpi_size);

    // calculate range size
    int rangeSize = coords.size() / mpi_size;
    if (coords.size() % mpi_size > 0)
        rangeSize++;

    // scatter data across nodes
    double local[rangeSize];
    MPI_Scatter(data, rangeSize, MPI_DOUBLE, local, rangeSize, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    // build tree
    KDTree<double> tree;
    for(int i = 0; i < rangeSize; i+=dimensions)
    {
        vector<double> pointCoords;
        for(int j = 0; j < dimensions; j++)
        {
            pointCoords.push_back(local[i + j]);
        }
        KDTree<double>::Point p;
        p.index = i;
        p.coords = pointCoords;
        tree.insert(p);
    }

    // run range search
    vector<KDTree<double>::Point> results = tree.rangeSearch(lower, upper);

    // assign results back to local array
    std::fill(local, local + rangeSize, -1.0);
    for (int i = 0; i < results.size(); i++)
    {
        for (int j = 0; j < results[i].coords.size(); j++)
            local[results[i].index + j] = results[i].coords[j];
    }
   
    // gather data from nodes
    MPI_Gather(local, rangeSize, MPI_DOUBLE, data, rangeSize, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    // output from rank 0
    if(my_rank == 0)
    {            
        // print results
        cout << "Results: ";
        int resultTotal = 0;
        
        for (int i = 0; i < coords.size(); i++)
        {
            if (data[i] >= 0)
            {
                // check if line begin
                if (i % dimensions == 0)
                {
                    //cout << endl;
                    //cout << "Point: ";
                }
                std::ostringstream sstream;
                sstream << data[i];
                std::string sCoord = sstream.str();
                // check if line end
                string output = i % dimensions == dimensions - 1 ? sCoord : sCoord +  + ", ";
                //cout << output;
                resultTotal++;
            }
        }
        cout << endl;

        // print time
        cout << "Result total: " << resultTotal / dimensions << endl;
        cout << "Execution time: " << time.lap() << endl;
    }

    // finalize MPI
    MPI_Finalize();
    return 0;
}