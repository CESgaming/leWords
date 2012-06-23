import java.io.*;
import java.util.*;
public class Dictionary{

  public String[] dictionary;
  public int length;
  
  
  public Dictionary(){
  
  }

  public Dictionary(int Length){
    length = Length;
    dictionary = new String[length];
    
  }

  public Dictionary(int Length,String[] S){
    length = Length;
    dictionary = new String[length];
    for (int i=0;i<length;i++){
    dictionary[i]= S[i];
    }
    
    // user has to take care, that length(S) == Length
    
  }
  /*
  public Dictionary(int Length, Stack<String> stack){
    length = Length;
    dictionary = new String[length];
    int i=0;
    while (!stack.empty()){  
    dictionary[length-1-i] = stack.peek(); // stack is FIFO
    stack.pop();
    i++;
    }
  }
  */

  public void fillCompleteDictionary(){

   try{
   FileInputStream fstream = new FileInputStream("dictionary/dictionary.txt");
		// Get the object of DataInputStream
   DataInputStream in = new DataInputStream(fstream);
   BufferedReader br = new BufferedReader(new InputStreamReader(in));
   String strLine;
   //Read File Line By Line
   
   strLine = br.readLine();
   length = Integer.parseInt(strLine);
   dictionary = new String[length];
   int i= 0;
   while ((strLine = br.readLine()) != null) 	{
     dictionary[i] = strLine;
     i++;
   }  
   //Close the input stream
  in.close();
   }catch (Exception e){
      System.err.println("Error: " + e.getMessage());
   }
  }

  public void printDictionary(){

    for (int i=0;i<length ; i++){
    System.out.println(dictionary[i]);
    }
  
  
  
  }


}
