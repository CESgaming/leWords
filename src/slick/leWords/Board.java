package slick.leWords;
import java.util.*;
import java.io.*;
public class Board{


  public int dim;
  public char[][] letters;
  public int [][] points;
  public Dictionary boardDictionary;
  public boolean initialized= false;


  public Board(int d){
    dim = d;
    letters = new char[d][d];
    points  = new int[d][d];
  }

  public void filterLevelOne(Dictionary full,Hypercube h){
    if (!initialized){
      System.out.println("Not initialized." );
    }
    // look at all possible sequences of 3 letters and filter all words
    // which dont start with any of the 3 letter sequences
    
    boolean [][][] b = new boolean[26][26][26]; // free this later!
    for (int i=0;i<26;i++){
      for (int j=0;j<26;j++){
        for(int k=0;k<26;k++){
          b[i][j][k] = false;
        }
      }
    }
    // estimate number of words that are possible
    // read ntuples.txt
    File f = null;
    Scanner scan = null;
     try{
      f = new File("dictionary/3tuplesOn5.txt");
      scan = new Scanner(f);
    }
    catch(Exception e){
      System.exit(0);
     }
    
    int griddim = scan.nextInt();
    int maxRecursionDepth = scan.nextInt();
    if (maxRecursionDepth!=3){
      System.out.println("Only works for level 3 recursion. Error!");
    }else{
      int currentRecursionDepth,k,l,m,xk,yk,xl,yl,xm,ym,x,y,z;
      int N = 0;
      char u, v, w;
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
        u= letters[xk][yk];
        v =letters[xl][yl];
        w =letters[xm][ym];


        // transform char to x y z position in hypercube
        // a =0, b=1, ..., z=25
        x =(int)u -97;
        y =(int)v -97;
        z =(int)w -97;
        if (!b[x][y][z]){
          if (h.from[x][y][z]>=0){
          b[x][y][z] = true;
          N = N+h.to[x][y][z]-h.from[x][y][z]+1;
          }
        }
       } 
    
    
   // now that one has estimated the combinations, write words
   
    String[]  S = new String[N];

    int pos = 0;
    for (k=0;k<26;k++){
    for(l=0;l<26;l++){
        for(m=0;m<26;m++){
          if (b[k][l][m]){ // prefix exists in board
            if (h.from[k][l][m]>=0){ // prefix exist in dictionary
              for (int p=h.from[k][l][m] ;p<=h.to[k][l][m];p++){
                S[pos] = full.dictionary[p-2];
                pos ++;
              }
            }
          }
        }
      }
    }
  
    boardDictionary = new Dictionary(N,S);
    }
  
  }

  
  public void filterLevelTwo(){
    // filter all words which cant exisit because
    // the needed letters dont exist
    // the words where the letters exist but arent
    // neighboured dont get filtered
    //
    // state : we have a filtered dictionary
    // size ~ 30k
    // naive approach :
    //  check each word
    //    check each letter of word
    //      check whether that letter occours on our 5x5 grid
    //  ... wow this looks bad
    //  30k words * ~4letters/word * up to 25 checks
    //  better would be:
    //  check aachen
    //  see aach doesnt work because no h on grid
    //  therefore remove aachener aachenerin aachenerdom ....

    // other possibility
    // SIGMA = {all letters} \ {letters on grid}
    // for sigma in SIGMA
    //  for all words
    //    if sigma in word
    //      remove word
    //  
    //  start with a letter that occours often then already a lot of words
    //  get filtered
    //  i take this approach

    if (!initialized) {
     System.out.println("Not initialized") ;
    }

    Stack<Character> Sigma= new Stack<Character>();
    boolean found=false;
    for (char c = 'a';c<='z';c++){
    found = false;
      for (int i = 0;i< dim &&!found;i++){
        for (int j=0;j<dim &&!found;j++){
          if (c == letters[i][j]){
            found = true;
          }
        }
      }
      if (!found) Sigma.push(c);
    }


    String tmp;
    boolean [] possible = new boolean[boardDictionary.length];
    for (int i=0;i<boardDictionary.length;i++){
      possible[i] = true;
    }
    char c;
    int notPossible = 0;
    while(!Sigma.empty()){
      c = Sigma.peek();

            System.out.println(c);
      Sigma.pop();
      // this is a bed letter
      for (int i=0;i<boardDictionary.length;i++){
        if (possible[i]){
          tmp = boardDictionary.dictionary[i];
          if (tmp==null){
            // shouldnt happen but it does
            possible[i] = false;
            notPossible++;
          }else if (-1!=tmp.indexOf(c)){
            possible[i] = false;
            notPossible++;
          }
        }
      }
    }


    // re alloc dictionary
    /*
    int newLength = boardDictionary.length - notPossible;
    System.out.println("length of array" + newLength);
    int pos = 0;
    String[] oldDictionary = new String[boardDictionary.length];
    for (int i=0;i<boardDictionary.length;i++){
     oldDictionary[i]=  boardDictionary.dictionary[i];
    }
    boardDictionary = new Dictionary(newLength);
    for (int i=0;i<newLength;i++){
      if(possible[i]){
        if (oldDictionary[i]!=null){
      
        boardDictionary.dictionary[pos] = oldDictionary[i];
        pos++;
        }
      }
    }
    System.out.println("pos = " +pos);
    */
    int pos = 0;
    String[] newDictionary = new String[boardDictionary.length];
    for (int i=0;i<boardDictionary.length;i++){
      if (possible[i]){
        if (boardDictionary.dictionary[i]!=null){
          newDictionary[pos] = boardDictionary.dictionary[i];
          pos ++;
        }
      }
    }
    boardDictionary = new Dictionary(pos,newDictionary);

  }

  public void filterLevelThree(){
  // the result after this filter is a
  // dictionary with only the possible words

  // take depthsearch vom hypercube.cpp and modify such that you only go to field which fit the char

  int[][] field = new int[dim+2][dim+2];
  for(int i=1 ; i<dim+1 ;i++){
    for(int j=1 ; j<dim+1 ;j++){
      field[i][j] = 0;
    }
  }
  
  for(int i=0 ; i<dim+2 ;i++){ 
    field[i][0] = -1;
    field[i][dim+1] = -1;
    field[0][i] = -1;
    field[dim+1][i] = -1;
  }

  char[] letters;
  int m;
  for (String word : boardDictionary.dictionary)
  {
    letters = word.toCharArray();
    m = letters.length;

 // recursiveSearchDepthN(field,gridsize,0,letters,m);
  }
  }




  public void fillBoard(){
    for (int i=0;i<dim;i++){
      for (int j=0;j<dim;j++){
        char c = getRandomChar();
        if (c=='x' || c=='y' || c == 'q'
            || c == 'z' || c =='z' || c =='p'
            || c=='v' || c =='w' || c=='c'){
            c = getRandomChar();
        }else if (c!='a' && c!='i' && c!='e' && c!='o'){
            c = getRandomChar();
        }
        if (c=='x' || c=='y' || c == 'q'
                || c == 'z' || c =='z' || c =='p'
                || c=='v' || c =='w' || c=='c'){
                c = getRandomChar();
            }else if (c!='a' && c!='i' && c!='e' && c!='o'){
                c = getRandomChar();
            }

        // from here
        /*
        if (i%3==1) {c  = 'a'; // TODO REMOVE THIS 
        }else if (i%3==2){ c= 'm';
        }else{
         c='n';}
        // to here
        */
        c = 'a';
        letters[i][j] = c;
        points[i][j] = pointsOf(c);  
      }
    }
    letters[0][0] = 'h';
    letters[0][1] = 'a';
    letters[0][2] = 'x';
    letters[0][3] = 's';
    letters[1][0] = 'm';
    letters[1][1] = 'r';
    letters[1][2] = 'n';
    letters[1][3] = 'u';
    letters[2][0] = 'f';
    letters[2][1] = 'i';
    letters[2][2] = 's';
    letters[2][3] = 'c';
    letters[3][0] = 'd';
    letters[3][1] = 'e';
    letters[3][2] = 'n';
    letters[3][3] = 'h';
    letters[1][4] = 'c';
    letters[2][4] = 'h';
    letters[3][4] = 'e';
    letters[4][4] = 'n';
    letters[4][3] = 'd';
    letters[4][2] = 'e';
    letters[4][1] = 'r';
    
    		
    initialized = true;

  }
  
  public void printBoard(){


    if (!initialized) {
      System.out.println("Not initialized") ;
    }

    for (int i=0;i<dim;i++){
      for (int j=0;j<dim;j++){
        System.out.print(letters[i][j]);
        System.out.print(" ");
      }
      System.out.println(" ");
    }
    System.out.println("");

    for (int i=0;i<dim;i++){
      for (int j=0;j<dim;j++){
        System.out.print(points[i][j]);
        System.out.print(" ");
      }
      System.out.println(" ");
    }

    System.out.println("");

  }

  


  public char getRandomChar(){
    Random r = new Random();
    char c = (char)(r.nextInt(26) + 'a');
    return c;
  }

  public char getRandomVowel(){
    Random r = new Random();
    char c = (char)(r.nextInt(26) + 'a');
    if (c=='a' || c=='e' || c=='i' || c=='o'|| c=='u'){
      return c;
    }else{
      return getRandomVowel();
    }
  }

  public char getRandomConsonant(){
    Random r = new Random();
    char c = (char)(r.nextInt(26) + 'a');

    if (c=='a' || c=='e' || c=='i' || c=='o'|| c=='u'){
      return getRandomConsonant();
    }else{
      return c;
    }
  }

  public int pointsOf(char c){
    // this is heuristic
    // see http://de.wikipedia.org/wiki/Buchstabenh%C3%A4ufigkeit
    // points by position of frequency
  
    int points=0;
    switch(c){
      case'e':
      case'n':
      case'i':
      case's': 
        points = 1; 
        break;
      case'r':
      case'a':
      case't':
      case'd':
        points = 2; 
        break;
      case'h':
      case'u':
      case'l':
      case'c':
        points = 3; 
          break;
      case'g':
      case'm':
      case'o':
      case'b':
        points = 4; 
        break;
      case'w':
      case'f':
      case'k':
      case'z':
        points = 5;
        break;
      case'p':
      case'v':
      case'j':
        points = 6;
        break;
      case'y':
      case'x':
      case'q':
        points =7; 
        break;
    }
    


    return points;
  }

  public int checkWord(String S){
    int p =0 ; // if word doesnt exist : 0 points
    boolean found = false;
  for ( int i=0;i<boardDictionary.length &&!found ; i++){
  // for(int i=340;i<390;i++){
  // System.out.println(S );
  //    System.out.println(i );
  //    System.out.println(boardDictionary.dictionary[i] );
      if (boardDictionary.dictionary[i].equalsIgnoreCase(S)){
     // if( boardDictionary.dictionary[i] == S){
        p = 1 ; // TODO calc points correctly!
        found = true;
      }
    }
  
  
    return p;
  }

}
