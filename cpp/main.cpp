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

  Dictionary D("dictionary/dictionary.txt");

  Hypercube H;

  H.initialize(D.dictionary,D.length);
  H.writeHypercube("dictionary/hypercube.txt");

  int gridsize = 4;
  int recursionDepth = 3;
  H.estimateNTuples(gridsize,recursionDepth);
  H.writeNTuples("dictionary/ntuples.txt",gridsize,recursionDepth);


  cout << "...done\n";
  return 0;
}

