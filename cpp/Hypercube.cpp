/*
 * Hypercube.cpp
 *
 *  Created on: Jun 19, 2012
 *      Author: thomas
 */

#include "Hypercube.h"


using namespace std;


Hypercube::Hypercube()
{
  dim = 3;
  hypercube = new fromto**[26];
  for (int i=0;i<26;i++){
      hypercube[i] = new fromto*[26];
      for (int j=0;j<26;j++){
          hypercube[i][j] = new fromto[26];
          for (int k=0;k<26;k++){
              hypercube[i][j][k].from =-1; // -1 not set
              hypercube[i][j][k].to   =-1; // -1 not set
          }
      }
  }
}

Hypercube::~Hypercube()
{
  // TODO Auto-generated destructor stub
}

void Hypercube::initialize(string* dictionary,int length)
{

  int pos = 0;
  string tmpword;
  int ni,nj,nk;
  ni = 0;
  nj = 0;
  nk = 0;
  for(char i = 'a';i<='z';i++){
      for(char j = 'a';j<='z';j++){
          for(char k = 'a';k<='z';k++){

              // create prefix
              stringstream sstr (stringstream::in | stringstream::out);
              string str;
              sstr<< i<<j<<k;
              sstr>>str;
              // search
              tmpword = dictionary[pos];
              if (tmpword[0] == str[0] && tmpword[1] == str[1] && tmpword[2] == str[2]){
                  hypercube[ni][nj][nk].from = pos+2;
                  // +2 because line start at 1 and the first line is lengthinteger
                  while (tmpword[0] == str[0] && tmpword[1] == str[1] && tmpword[2] == str[2] && pos<length){
                      hypercube[ni][nj][nk].to = pos+2;
                      pos++;
                      tmpword = dictionary[pos];

                  }
              }else{
                  hypercube[ni][nj][nk].from = -1; // no item found
                  hypercube[ni][nj][nk].to = -1;   // no item found
              }
              nk++;
          }
          nk = 0;
          nj++;
      }
      nk = 0;
      nj = 0;
      ni++;
  }
}

void Hypercube::printNonZeros()
{
  for (int i=0;i<26;i++){
      for(int j=0;j<26;j++){
          for(int k=0;k<26;k++){
              if (hypercube[i][j][k].from>=0 ){
                  cout << "element: " <<i << " " << j << " " << k;
                  cout <<"   \tfrom " << hypercube[i][j][k].from << " to "<<hypercube[i][j][k].to <<endl;
              }
          }
      }
  }
}

void Hypercube::writeHypercube(char* path){
  ofstream out;
  out.open(path);
  for (int i=0;i<26;i++){
      for(int j=0;j<26;j++){
          for(int k=0;k<26;k++){
              out <<i << " " << j << " " << k;
              out <<"\t"<< hypercube[i][j][k].from << " "<<hypercube[i][j][k].to <<"\n";

          }
      }
  }
  out.close();


}

void Hypercube::estimateNTuples(int gridsize,int depth)
{
  if (depth>5){
      cout << "#############################################\n";
      cout << "Depth>5 results in error due to max recursion\n";
      cout << "#############################################\n";
  }
  // stupid way, but works maybe!
  // lol really works rekursion ftw
  int ** field = new int*[gridsize+2];
  for (int i=0;i<gridsize+2;i++) field[i] = new int[gridsize+2];


  for (int i=1;i<gridsize+1;i++){
      for (int j=1;j<gridsize+1;j++){
          field[i][j] =0;// (i-1)*gridsize+j-1;

      }
  }
  for (int i=0;i<gridsize+2;i++){
      field[i][0] = -1;
      field[i][gridsize+1] = -1;
      field[0][i] = -1;
      field[gridsize+1][i] = -1;
  }
  /* for gridsize = 4:
  -1 -1 -1 -1 -1 -1
  -1 0 0 0 0 -1
  -1 0 0 0 0 -1
  -1 0 0 0 0 -1
  -1 0 0 0 0 -1
  -1 -1 -1 -1 -1 -1

   for (int i=0;i<gridsize+2;i++) {
       for(int j=0;j<gridsize+2;j++){
           cout << field[i][j] << " ";
       }
       cout << endl;
   }
   */
  int  s[gridsize*gridsize];
  for (int i=0;i<gridsize*gridsize;i++) s[i] = 0;
  recursiveSearchDepthN(field,gridsize,0,s,depth);




}

void Hypercube::recursiveSearchDepthN(int** field,int n,int l,int s[],int N)
{
  if (l==0){ // beginning
      for (int i=0;i<n*n;i++){
          s[0] = i;
          field[(int)(s[0]/n)+1][s[0]%n+1] = 1; // mark starting field
          recursiveSearchDepthN(field,n,1,s,N); // start searching
      }
  }else if(l==N){ //end
      nway w;
      w.steps = new int(l+1);
      w.steps[0] = l;
      for (int i=0;i<N;i++) w.steps[i+1] = s[i];
      possibleNtuples.push(w);
      // reset field
      field[(int)(s[l-1]/n)+1][s[l-1]%n+1] = 0;
      // end of search
  }else  if (l==1 || l==2){ // case 1 and 2, no storage of data
      int posx,posy;
      posx = (int)s[l-1]/n+1;
      posy = s[l-1] %n+1;
      for (int i=posx-1;i<=posx+1;i++){
          for(int j=posy-1;j<=posy+1;j++){
              if (i!=posx || j!=posy){
                  if(field[i][j]!=-1) { //else would be boundary
                      if (field[i][j]!=1) { // else would be already marked
                          field[i][j] =1 ;// mark field
                          s[l] = (i-1)*n+(j-1); // calculate current position and store in s
                          recursiveSearchDepthN(field,n,l+1,s,N); // call next level ;
                      }
                  }
              }
          }
      }
      field[(int)(s[l-1]/n)+1][s[l-1]%n+1] = 0;
  }else{ // all other cases from 3 to N-1

      // store current path
      nway w;
      w.steps = new int[l+1];
      w.steps[0] = l;
      for (int i=0;i<N;i++) w.steps[i+1] = s[i];
      possibleNtuples.push(w);

      // but continue searching till N
      int posx,posy;
      posx = (int)s[l-1]/n+1;
      posy = s[l-1] %n+1;
      for (int i=posx-1;i<=posx+1;i++){
          for(int j=posy-1;j<=posy+1;j++){
              if (i!=posx || j!=posy){
                  if(field[i][j]!=-1) { //else would be boundary
                      if (field[i][j]!=1) { // else would be already marked
                          field[i][j] =1 ;// mark field
                          s[l] = (i-1)*n+(j-1); // calculate current position and store in s
                          recursiveSearchDepthN(field,n,l+1,s,N); // call next level ;
                      }
                  }
              }
          }
      }
      field[(int)(s[l-1]/n)+1][s[l-1]%n+1] = 0;


  }







}

void Hypercube::printPossibleNTuples()
{

  int i=0;
  int to;
  nway w;
  // we want to start top left position
  while(!possibleNtuples.empty()){
      cout<< "way i= "<<i++ << ":\t";
      w = possibleNtuples.top();
      to = w.steps[0];
      for (int j=1;j<=to;j++) cout << w.steps[j] << " " ;
      cout << endl;

      possibleNtuples.pop();
  }
}

void Hypercube::writeNTuples(char* path,int gridsize,int recursionDepth)
{
  ofstream out;
  out.open(path);
  nway tmp;
  out << gridsize << "\n" << recursionDepth << "\n";
  while(!possibleNtuples.empty()){
      tmp = possibleNtuples.top();
      out <<tmp.steps[0] << "\t";
      for (int k=1;k<=tmp.steps[0];k++) out << tmp.steps[k] << " ";
      out << endl;
      possibleNtuples.pop();
  }


}










