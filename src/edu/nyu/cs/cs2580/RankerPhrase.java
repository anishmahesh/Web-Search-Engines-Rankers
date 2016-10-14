package edu.nyu.cs.cs2580;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Collections;

/**
 * @CS2580: Use this template to implement the phrase ranker for HW1.
 * 
 * @author congyu
 * @author fdiaz
 */
public class RankerPhrase extends Ranker {

  public RankerPhrase(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    // @CS2580: fill in your code here.
	for (int i = 0; i < _indexer.numDocs(); ++i) {
		all.add(scoreDocument(query, i));
	}
	Collections.sort(all, Collections.reverseOrder());
	Vector < ScoredDocument > results = new Vector < ScoredDocument > ();
	for (int i = 0; i < all.size() && i < numResults; ++i) {
		results.add(all.get(i));
	}
    return results;
  }

 private ScoredDocument scoreDocument(Query query, int did) {
	// Process the raw query into tokens.
	Document doc = _indexer.getDoc(did);
	Vector < String > docTokens = ((DocumentFull) doc).getConvertedBodyTokens(); //getConvertedTitleTokens()
	Vector < String > titleTokens = ((DocumentFull) doc).getConvertedTitleTokens(); //getConvertedTitleTokens()
	docTokens.addAll(titleTokens);
	double score = 0.0;
	int firstTermCount = 0;
	int secondTermCount = 0;
	
	if (query._tokens.size() == 1) {
		score = ((IndexerFullScan)_indexer).documentTermFrequency(query._tokens.get(0),did);
	} else {
		for (int i = 1; i < query._tokens.size(); i++) 
		{
			firstTermCount = ((IndexerFullScan)_indexer).documentTermFrequency(query._tokens.get(i), did);
			if(firstTermCount > 0)
			{
				String bigramQuery = query._tokens.get(i - 1) + query._tokens.get(i);	
				secondTermCount = ((IndexerFullScan)_indexer).documentTermFrequency(query._tokens.get(i-1),did);
				if(secondTermCount > 0){
					for (int j = 1; j < docTokens.size(); j++) 
					{
						String twoWordsFromDocument = docTokens.get(j - 1) + docTokens.get(j);
						if (bigramQuery.equals(twoWordsFromDocument)) 
						{
							score++;
						}
					}
				} else {
					score += 0;
				}
			} else {
				score += 0;				
			}
		}
	}
	return new ScoredDocument(query._query, doc, score);
	}
}
