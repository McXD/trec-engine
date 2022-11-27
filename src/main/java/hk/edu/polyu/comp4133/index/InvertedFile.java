package hk.edu.polyu.comp4133.index;

import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * The inverted index. An index instance is built on a pre-processed corpus.
 * The built index should be persisted in a file, either binary or text.
 * The index has document length data built-in.
 */
public class InvertedFile {
    enum BuildMode {
        IN_MEMORY,
        SORT_BASED,
        MERGE_BASED
    }

    enum LoadMode {
        /**
         * Load the entire structure to memory.
         */
        FULL,
        /**
         * Only load the dictionary and pointers to the postings (on disk). Lazy-load the postings. Free when memory is low.
         */
        DICTIONARY
    }

    public Map<String, PostingList> invertedMap = new HashMap<>();
    public Map<String, PostingList> tempMap = new HashMap<>();
	public int mForIdf = 0;
    /**
     * Build the index.
     */
    public void build(PostInputStream is, BuildMode mode, int threshold) {
        BuildMode mode = BuildMode.IN_MEMORY;
		switch(mode){
            case IN_MEMORY:
                try{
                    File postFile = new File("./fileee.txt");
                    Scanner fileReader = new Scanner(postFile);
                    int docId = 0;

                    while(fileReader.hasNextLine()){
                        String[] result = fileReader.nextLine().split(" ");

                        Posting termPosting;
                        if (tempMap.containsKey(result[0])){
                        
							if(tempMap.get(result[0]).postingList.get(tempMap.get(result[0]).postingList.size()-1).docId == Integer.parseInt(result[1])){
                                termPosting = new Posting(Integer.parseInt(result[1]), tempMap.get(result[0]).postingList.get(tempMap.get(result[0]).postingList.size()-1).termFreq+1, Integer.parseInt(result[2]));
							}	
                            else{
                                termPosting = new Posting(Integer.parseInt(result[1]), 1, Integer.parseInt(result[2]));
                            }
                        }
                        else{
                            termPosting = new Posting(Integer.parseInt(result[1]), 1, Integer.parseInt(result[2]));
                            tempMap.put(result[0], new PostingList());
                        }
                        tempMap.get(result[0]).addPosting(termPosting);
						mForIdf = Integer.parseInt(result[1]);
                    }
                }
                catch (FileNotFoundException e) {
					System.out.print(e);
                }
                break;


            case SORT_BASED:
                // convert temp map from hashmap to tree map
                TreeMap<String, PostingList> sortedMap = new TreeMap<>(tempMap);

                //clear map from the temp and put the sorted value from treemap to temp map
                tempMap.clear();
                for(Map.Entry<String, PostingList> entry : sortedMap.entrySet()){
                    tempMap.put(entry.getKey(), entry.getValue());
                }
                break;

            case MERGE_BASED:
                Iterator < Map.Entry <String, PostingList> > invertedIterator = invertedMap.entrySet().iterator();
                Iterator <Map.Entry <String, PostingList> > tempIterator = tempMap.entrySet().iterator();
                invertedMap.clear();
				
				if(!invertedIterator.hasNext()){
					invertedMap = new HashMap<>(tempMap);
					break;
				}
				
				Map.Entry < String, PostingList> invertedEntry = invertedIterator.next();
                Map.Entry < String, PostingList> tempEntry = tempIterator.next();

                while (invertedIterator.hasNext() || tempIterator.hasNext()){
                    int comparing = invertedEntry.getKey().compareTo(tempEntry.getKey());
                    if(comparing == 0)
                    {
                        for (int i = 0; i < tempEntry.getValue().postingList.size(); i++){
                            invertedEntry.getValue().addPosting(tempEntry.getValue().postingList.get(i));
                        }
                        invertedMap.put(tempEntry.getKey(), tempEntry.getValue());

                        if(invertedIterator.hasNext())
                            invertedEntry = invertedIterator.next();
                        if(tempIterator.hasNext())
                            tempEntry = tempIterator.next();

                    }
                    else if(comparing > 0){
                        invertedMap.put(tempEntry.getKey(), tempEntry.getValue());
                        if(tempIterator.hasNext())
                            tempEntry = tempIterator.next();
                    }
                    else{
                        invertedMap.put(invertedEntry.getKey(), invertedEntry.getValue());
                        if(invertedIterator.hasNext())
                            invertedEntry = invertedIterator.next();
                    }
                }

                while(invertedIterator.hasNext()){
                    invertedEntry = invertedIterator.next();
                    invertedMap.put(invertedEntry.getKey(), invertedEntry.getValue());
                }

                while(tempIterator.hasNext()){
                    tempEntry = tempIterator.next();
                    invertedMap.put(tempEntry.getKey(), tempEntry.getValue());
                }
                break;
        }
    }

    /**
     * Get the posting list of a term
     */
    public PostingList getPostingList(String term) {
        return invertedMap.get(term);
    }

    /**
     * Get vector (e.g., TF-IDF) length of the document.
     * This value should be pre-computed and stored in the index.
     */
    public double getDocLength(int docId) {
        double docLength = 0;
        for (Map.Entry <String, PostingList> set : invertedMap.entrySet()){
			
            int docCounter = 0;
            int counterDocId = -1;
            for (int i = 0; i < set.getValue().postingList.size(); i++){
                if(counterDocId != set.getValue().postingList.get(i).docId){
                    docCounter++;
                    counterDocId = set.getValue().postingList.get(i).docId;
                }
            }
			double m = docCounter;
            double n = mForIdf + 1;
            double idf = Math.log10(n / m);
            double tf = 0;
            for (int i = 0; i < set.getValue().postingList.size(); i++){
                if(docId == set.getValue().postingList.get(i).docId){
                    tf = set.getValue().postingList.get(i).termFreq;
                }
                if(docId < set.getValue().postingList.get(i).docId)
                    break;
            }
            docLength += Math.pow(tf * idf, 2);
        }
        return Math.sqrt(docLength);
    }
}
