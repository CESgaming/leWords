/*
 * Hypercube.h
 *
 *  Created on: Jun 19, 2012
 *      Author: thomas
 */

#ifndef HYPERCUBE_H_
#define HYPERCUBE_H_

#include <string>
#include <sstream>
#include <iostream>
#include <stack>
#include <fstream>

using namespace std;

struct fromto {
  int from;
  int to;
} ;
struct way{
  int start;
  int mid;
  int end;
};
struct nway{
  int * steps;
  // Store steps[0] = number of steps
  // steps[1-...] = steps

};


class Hypercube
{
public:
  int dim;
  fromto*** hypercube;
  stack <way> possible3tuples;
  stack <nway> possibleNtuples;

  void initialize(string*,int);
  void printNonZeros();
  void writeHypercube(char*);

  void estimateNTuples(int,int);
  void recursiveSearchDepthN(int**,int,int,int[],int);
  void printPossibleNTuples();
  void writePossibleNTuples(char*);
  void writeNTuples(char*,int,int);

  Hypercube();
  virtual
  ~Hypercube();
};

#endif /* HYPERCUBE_H_ */
