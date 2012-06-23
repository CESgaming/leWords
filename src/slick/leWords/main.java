import java.io.*;
public class main{
  public static void main(String[] args){
    Board b = new Board(4);
    Hypercube h = new Hypercube();
    Dictionary d = new Dictionary();
    
    h.fillHypercube();
    d.fillCompleteDictionary();
    b.fillBoard();
    b.filterLevelOne(d,h);
    b.printBoard();
    System.out.println("before: " + b.boardDictionary.length);
    b.filterLevelTwo();
    System.out.println("after: " + b.boardDictionary.length);
    //b.boardDictionary.printDictionary();
    System.out.println("haus? : 1= true 0 = false:"+ b.checkWord("haus"));
    System.out.println("hauy? : 1= true 0 = false:"+ b.checkWord("hauy"));

    b.printBoard();


  }
}

