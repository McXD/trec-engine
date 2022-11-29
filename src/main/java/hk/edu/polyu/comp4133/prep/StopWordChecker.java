package hk.edu.polyu.comp4133.prep;

import java.io.*;
import java.util.TreeSet;

public class StopWordChecker {
    /**
     * Check if a term is a stop word.
     * @param term the term to check
     * @return true if the term is a stop word, false otherwise
     */

    //using the red-black tree like structure to assign the stop words
    TreeSet <String> ts= new TreeSet<>();

    StopWordChecker(){
        //read from stopword.txt to get all the stop word
        File file = new File(".\\stopword.txt");
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String stopWord;

            //assign stop words to tree set

            while ((stopWord = br.readLine()) != null){
                ts.add(stopWord);
            }
        }
        catch (FileNotFoundException e){
            System.out.println("stop word list file not found" + e);
        }
        catch (IOException e){
            System.out.println(e);
        }

    }

    public boolean isStopWord(String term) {
        return ts.contains(term);
    }
}
