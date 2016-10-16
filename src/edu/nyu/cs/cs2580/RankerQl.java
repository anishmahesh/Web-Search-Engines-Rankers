package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Collections;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the query likelihood ranker for HW1.
 * 
 * @author congyu
 * @author fdiaz
 */
public class RankerQl extends Ranker {

  public RankerQl(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    for (int i = 0; i < _indexer.numDocs(); ++i) {
      all.add(scoreDocument(query, i));
    }
    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }
    return results;
  }
  /*
      Scores Document based on Query Likelyhood probability
   */
  private ScoredDocument scoreDocument(Query query, int did) {

    Document doc = _indexer.getDoc(did);

    double queryLikelyhoodProbability = 1.0;
    double totalTermsInDoc = ((IndexerFullScan)_indexer).totalTermsInDocument(did);
    double totalTermsInCourpus = _indexer._totalTermFrequency;
    double lambda = 0.5;

    for (String queryToken : query._tokens) {
      double termFrequency = _indexer.documentTermFrequency(queryToken,did);
      double corpusTermFrequency = _indexer.corpusDocFrequencyByTerm(queryToken);
      queryLikelyhoodProbability *= (1-lambda)*(termFrequency/totalTermsInDoc)+(lambda)*(corpusTermFrequency/totalTermsInCourpus);
    }
    return new ScoredDocument(query._query, doc, queryLikelyhoodProbability);
  }
}
