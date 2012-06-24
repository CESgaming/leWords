/*
 * Dictionary.cpp
 *
 *  Created on: Jun 19, 2012
 *      Author: thomas
 *      http://sourceforge.net/projects/germandict/
 */

#include "Dictionary.h"

Dictionary::Dictionary(char* path)
{
  // path is the path to the dictionary file
      ifstream stream;

      stream.open(path);
      if (stream.is_open()){
          stream >> length; // read length of dictionary
          dictionary = new string[length];
          int i = 0;
          while(!stream.eof()){
              stream >> dictionary[i]; // read every word
              i++;

          }
      }
      stream.close();
}

Dictionary::~Dictionary()
{
  // TODO Auto-generated destructor stub
}

