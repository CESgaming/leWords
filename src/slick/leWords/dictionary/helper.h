#include <fstream>
#include <string>
using namespace std;

int remove_words_with_two_letters ( void ){

  ifstream stream;
  stream.open("dictionary/dictionary.txt");
  ofstream out;
  out.open("dictionary/dictionary_longer_2.txt");
  string temp;
  if (stream.is_open()){
    while(!stream.eof()){
      stream>>temp;
      if (temp.length()>2)
        out<<temp<<"\n";

    }
  }
    
}
