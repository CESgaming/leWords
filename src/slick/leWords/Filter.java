/*
 * Filter is not used, the filter function is moved to the Board class since it
 * is a simple function each board has
 * 
 import java.io.*;
import java.util.*;
public class Filter{

  Hypercube h;
  Dictionary dOriginal,dFiltered;
  Board b;

  public Filter( Board B, Hypercube H, Dictionary D){
    h = H;
    dOriginal = D;
    b = B;
  }

  public void filter(){


    // estimate number of words that are possible
    int nPossible;
    // read ntuples.txt
    File f = null;
    Scanner scan = null;
     try{
      f = new File("dictionary/ntuples.txt");
      scan = new Scanner(f);
    }
    catch(Exception e){
      System.exit(0);
     }
    
    int griddim = scan.nextInt();
    int maxRecursionDepth = scan.nextInt();
    if (maxRecursionDepth!=3){
      System.out.println("Only works for level 3 recursion.");
    }else{
      int currentRecursionDepth,k,l,m,xk,yk,xl,yl,xm,ym,x,y,z;
      int N = 0;
      char u, v, w;
      Stack<String> stack = new Stack<String>();
      while(scan.hasNext()){
        // read path
        currentRecursionDepth = scan.nextInt();
        k = scan.nextInt();
        l = scan.nextInt();
        m = scan.nextInt();

        // from k l m to xk,yk  xl,yl  xm,ym
        xk = k%griddim;
        yk = k/griddim;

        xl = l%griddim;
        yl = l/griddim;

        xm = m%griddim;
        ym = m/griddim;


        // read corresponding chars from board
        u= b.letters[xk][yk];
        v =b.letters[xl][yl];
        w =b.letters[xm][ym];


        // transform char to x y z position in hypercube
        // a =0, b=1, ..., z=25
        x =(int)u -97;
        y =(int)v -97;
        z =(int)w -97;
        N = N+h.to[x][y][z]-h.from[x][y][z]+1;
        if (h.from[x][y][z]>0) { // otherwise no elements with that prefix
          for (int p = h.from[x][y][z];p<=h.to[x][y][z];p++){
            stack.push(dOriginal.dictionary[p]);
          }
        }
      } 

      // copy stack to dictionaryFiltered
      dFiltered = new Dictionary(N,stack);
        
    } 
  }
      

}
*/

