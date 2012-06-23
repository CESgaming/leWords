/*
 * Dictionary.h
 *
 *  Created on: Jun 19, 2012
 *      Author: thomas
 */

#ifndef DICTIONARY_H_
#define DICTIONARY_H_

#include <iostream>
#include <fstream>
#include <string.h>

using namespace std;


class Dictionary
{
public:
  string* dictionary;
  int length;
  Dictionary(char*);
  virtual
  ~Dictionary();
};

#endif /* DICTIONARY_H_ */


