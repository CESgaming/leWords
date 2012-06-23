/*
 * main.cpp
 *
 *  Created on: Jun 19, 2012
 *      Author: thomas
 */



#include "Dictionary.h"
#include "Hypercube.h"
using namespace std;



int main (void) {

  Dictionary D("/home/thomas/workspace/leWords/dictionary/dictionary.txt");

  Hypercube H;

  H.initialize(D.dictionary,D.length);
  H.writeHypercube("hypercube.txt");

  int gridsize = 5;
  int recursionDepth = 3;
  H.estimateNTuples(gridsize,recursionDepth);
  H.writeNTuples("../dictionary/3tuplesOn5.txt",gridsize,recursionDepth);


  cout << "...done\n";
  return 0;
}

