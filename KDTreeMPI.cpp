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

// k-dimensional point
struct Point
{
    int index;
    std::vector<double> coords;
    // Point(std::initializer_list<double> init) : coords(init) {}
    Point(const int iinit, const std::vector<double> &cinit)
    {
        index = iinit;
        coords = cinit;
    }
};

// function to sort points by dimension
struct PointComparator 
{
    int dimension;
    bool operator()(const Point &a, const Point &b) const {
        return a.coords[dimension] < b.coords[dimension];
    }
};

// k-d tree node
struct Node
{
    Point point;
    Node *left;
    Node *right;
    Node(Point p) : point(p), left(NULL), right(NULL) {}
};

// k-d tree
class KDTree
{
public:
    // constructor
    KDTree(const std::vector<Point> &points)
    {
        root = build(points, 0);
    }

    // public range search
    std::vector<Point> rangeSearch(const Point &lower, const Point &upper)
    {
        std::vector<Point> result;
        rangeSearch(root, lower, upper, 0, result);
        return result;
    }

private:
    Node *root; // tree root

    // build tree
    Node *build(std::vector<Point> points, int depth)
    {
        if (points.empty())
            return NULL;
        PointComparator comparator = PointComparator();
        comparator.dimension =  depth % points[0].coords.size();
        std::sort(points.begin(), points.end(), comparator);
        int median = points.size() / 2;
        Node *node = new Node(points[median]);
        std::vector<Point> leftPoints(points.begin(), points.begin() + median);
        std::vector<Point> rightPoints(points.begin() + median + 1, points.end());
        node->left = build(leftPoints, depth + 1);
        node->right = build(rightPoints, depth + 1);
        return node;
    }

    // private rangeSearch
    void rangeSearch(Node *node, const Point &lower, const Point &upper, int depth, std::vector<Point> &result)
    {
        // check if null
        if (!node)
            return;
        
        // check if node is outside range
        bool inside = true;
        for (size_t i = 0; i < lower.coords.size(); ++i)
        {
            if (node->point.coords[i] < lower.coords[i] || node->point.coords[i] > upper.coords[i])
            {
                inside = false;
                break;
            }
        }

        // if within range add point to results
        if (inside)
            result.push_back(node->point);
        int dimension = depth % lower.coords.size(); // get dimension

        // if lower bounds less than node dimension, rangeSearch left
        if (lower.coords[dimension] <= node->point.coords[dimension])
            rangeSearch(node->left, lower, upper, depth + 1, result);

        // if upper bounds greater than node dimension, rangeSearch right
        if (upper.coords[dimension] >= node->point.coords[dimension])
            rangeSearch(node->right, lower, upper, depth + 1, result);
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
Point readBound(string bound)
{
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
    return Point(-1, bcoords);
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
    double data[coords.size()];
    //double* data = new double[coords.size()];
    std::copy(coords.begin(), coords.end(), data);

    //cout << "Test: " << data[1] << endl;

    // read bounds 
    Point lower = readBound(lowerb);
    Point upper = readBound(upperb);

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

    //cout << "Test local: " << local[1] << endl;

    // build tree
    vector<Point> points;
    for(int i = 0; i < rangeSize; i+=dimensions)
    {
        vector<double> pointCoords;
        for(int j = 0; j < dimensions; j++)
        {
            pointCoords.push_back(local[i + j]);
        }
        points.push_back(Point(i, pointCoords));
    }
    KDTree tree(points);

    // run range search
    vector<Point> results = tree.rangeSearch(lower, upper);

    cout << "Results: " << results.size() << endl;

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
        //cout << "Test: " << data[0] << endl;
        
        // print results
        cout << "Results: ";
        
        for (int i = 0; i < sizeof(data) / sizeof(double); i++)
        {
            if (data[i] >= 0)
            {
                // check if line begin
                if (i % dimensions == 0)
                {
                    cout << endl;
                    cout << "Point: ";
                }
                std::ostringstream sstream;
                sstream << data[i];
                std::string sCoord = sstream.str();
                // check if line end
                string output = i % dimensions == dimensions - 1 ? sCoord : sCoord +  + ", ";
                cout << output;
            }
        }
        cout << endl;

        // print time
        cout << "Execution time: " << time.lap() << endl;
    }

    // finalize MPI
    MPI_Finalize();
    return 0;
}