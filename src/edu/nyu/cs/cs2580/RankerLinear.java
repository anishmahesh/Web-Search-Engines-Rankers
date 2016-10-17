package edu.nyu.cs.cs2580;
import java.util.*;


import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;


/**
 * @CS2580: Use this template to implement the linear ranker for HW1. You must
 * use the provided _betaXYZ for combining the signals.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerLinear extends Ranker {
    private float _betaCosine = 1.0f;
    private float _betaQl = 1.0f;
    private float _betaPhrase = 1.0f;
    private float _betaNumviews = 1.0f;
    private double minCosine = 10000000;
    private double maxCosine = -1.0;
    private double minQl = 10000000;
    private double maxQl = -1.0;
    private double minNumviews = 10000000;
    private double maxNumviews = -1.0;
    private double minPhrase = 10000000;
    private double maxPhrase = -1.0;

    Map<Integer,Double> cosineMap = new HashMap<Integer,Double>();
    Map<Integer,Double> numviewsMap = new HashMap<Integer,Double>();
    Map<Integer,Double> qlMap = new HashMap<Integer,Double>();
    Map<Integer,Double> phraseMap = new HashMap<Integer,Double>();

    public RankerLinear(Options options,
                        CgiArguments arguments, Indexer indexer) {
        super(options, arguments, indexer);
        System.out.println("Using Ranker: " + this.getClass().getSimpleName());
        _betaCosine = options._betaValues.get("beta_cosine");
        _betaQl = options._betaValues.get("beta_ql");
        _betaPhrase = options._betaValues.get("beta_phrase");
        _betaNumviews = options._betaValues.get("beta_numviews");
    }

    @Override
    public Vector<ScoredDocument> runQuery(Query query, int numResults) {
        System.out.println("  with beta values" +
                ": cosine=" + Float.toString(_betaCosine) +
                ", ql=" + Float.toString(_betaQl) +
                ", phrase=" + Float.toString(_betaPhrase) +
                ", numviews=" + Float.toString(_betaNumviews));
        Vector<ScoredDocument> all = new Vector<ScoredDocument>();
        query.computeVsm();
        for (int i = 0; i < _indexer.numDocs(); ++i) {
            setGlobalValues(query,i);
        }
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

    private void setGlobalValues(Query query, int did)
    {
        double cosineScore = cosineScoreDocument(query,did);
        minCosine = setMinimum(cosineScore,minCosine);
        maxCosine = setMaximum(cosineScore, maxCosine);
        double qlScore = qlScoreDocument(query,did);
        minQl = setMinimum(qlScore,minQl);
        maxQl = setMaximum(qlScore, maxQl);
        double phraseScore = phraseScoreDocument(query,did);
        minPhrase = setMinimum(phraseScore,minPhrase);
        maxPhrase = setMaximum(phraseScore, maxPhrase);
        double numviewsScore= numviewScoreDocument(query,did);
        minNumviews = setMinimum(numviewsScore,minNumviews);
        maxNumviews = setMaximum(numviewsScore, maxNumviews);

        cosineMap.put(did,cosineScore);
        numviewsMap.put(did,numviewsScore);
        phraseMap.put(did,phraseScore);
        qlMap.put(did,qlScore);
    }

    private double setMinimum (double score, double currentMin){
        return currentMin<score? currentMin : score;
    }

    private double setMaximum (double score, double currentMax){
        return currentMax>score? currentMax : score;
    }

    private double normalize (double score,double max,double min){
        return ((score-min)/(max-min));
    }

    public ScoredDocument scoreDocument(Query query, int did){
        Document doc = _indexer.getDoc(did);
        double normalizedCosineScore = normalize(cosineMap.get(did),maxCosine, minCosine);
        double normalizedQlScore = normalize(qlMap.get(did),maxQl, minQl);
        double normalizedPhraseScore= normalize(phraseMap.get(did),maxPhrase, minPhrase);
        double normalizedNumviewsScore= normalize(numviewsMap.get(did),maxNumviews, minNumviews);
        double score = _betaCosine * normalizedCosineScore  + _betaQl * normalizedQlScore + _betaPhrase * normalizedPhraseScore  + _betaNumviews * normalizedNumviewsScore;
        return new ScoredDocument(query._query, doc, score);
    }

    private double cosineScoreDocument(Query query, int did) {
        Document doc = _indexer.getDoc(did);
        Map<String, Double> docVsm = ((DocumentFull) doc).getVsmRepresentation();

        double score = 0.0;
        for (String queryToken : query._tokens) {
            if (docVsm.containsKey(queryToken)) {
                score += docVsm.get(queryToken) * query._vsm.get(queryToken);
            }
        }
        return score;
    }

    private double phraseScoreDocument(Query query, int did) {
        // Process the raw query into tokens.
        Document doc = _indexer.getDoc(did);
        Vector < String > docTokens = ((DocumentFull) doc).getConvertedBodyTokens(); //getConvertedTitleTokens()
        Vector < String > titleTokens = ((DocumentFull) doc).getConvertedTitleTokens(); //getConvertedTitleTokens()
        docTokens.addAll(titleTokens);
        double score = 0.0;
        int firstTermCount = 0;
        int secondTermCount = 0;

        if (query._tokens.size() == 1) {
            score = _indexer.documentTermFrequency(query._tokens.get(0),did);
        } else {
            for (int i = 1; i < query._tokens.size(); i++)
            {
                firstTermCount = _indexer.documentTermFrequency(query._tokens.get(i), did);
                if(firstTermCount > 0)
                {
                    String bigramQuery = query._tokens.get(i - 1) + query._tokens.get(i);
                    secondTermCount = _indexer.documentTermFrequency(query._tokens.get(i-1),did);
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
        return score;
    }

    private double qlScoreDocument(Query query, int did) {

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
        return queryLikelyhoodProbability;
    }

    private double numviewScoreDocument(Query query, int did) {
        Document doc = _indexer.getDoc(did);
        return doc.getNumViews();
    }
}
