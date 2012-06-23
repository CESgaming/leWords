import java.io.*;
import java.util.Scanner;

public class Hypercube{

  public int [][][] from;
  public int [][][] to;

  public Hypercube(){
    from = new int[26][26][26];
    to = new int[26][26][26];
  }


public void fillHypercube(){
    // assume : dictionary.txt hypercube.txt exist
    //          Board.init() has been called
    // then   : calculate number of words that you have in your dictionary
   int n;
  
  File f = null;
  Scanner scan = null;
  try{
    f = new File("dictionary/hypercube.txt");
    scan = new Scanner(f);
  }
  catch(Exception e){

    System.exit(0);
  }

  
  int i,j,k;
  //Assuming you know all your data on the file are ints
  while(scan.hasNext()){
    i = scan.nextInt();
    j = scan.nextInt();
    k = scan.nextInt();
    from[i][j][k] = scan.nextInt();
    to[i][j][k] = scan.nextInt();
  }
}

public void printHypercube(){
  int i,j,k;
  for (i=0;i<26;i++){
    for (j=0;j<26;j++){
      for (k=0;k<26;k++){
        System.out.printf("pos i=%d  j=%d  k=%d : from=%d  to=%d \n",i,j,k,from[i][j][k],to[i][j][k]);
      }
    }
  }
}





}
