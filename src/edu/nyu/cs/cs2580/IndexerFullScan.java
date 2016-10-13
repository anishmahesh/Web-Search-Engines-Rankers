package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * Instructor's implementation of a simple full scan Indexer, used in HW1.
 * 
 * @author fdiaz
 * @author congyu
 */
class IndexerFullScan extends Indexer implements Serializable {
  private static final long serialVersionUID = 1077111905740085030L;

  // Maps each term to their integer representation
  private Map<String, Integer> _dictionary = new HashMap<String, Integer>();
  // All unique terms appeared in corpus. Offsets are integer representations.
  private Vector<String> _terms = new Vector<String>();

  // Term document frequency, key is the integer representation of the term and
  // value is the number of documents the term appears in.
  private Map<Integer, Integer> _termDocFrequency =
      new HashMap<Integer, Integer>();
  // Term frequency, key is the integer representation of the term and value is
  // the number of times the term appears in the corpus.
  private Map<Integer, Integer> _termCorpusFrequency =
      new HashMap<Integer, Integer>();

  // Stores all Document in memory.
  private Vector<Document> _documents = new Vector<Document>();

  // Provided for serialization
  public IndexerFullScan() { }
  
  // The real constructor
  public IndexerFullScan(Options option) {
    super(option);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  ///// Construction related functions.
  
  /**
   * Constructs the index from the corpus file.
   * 
   * @throws IOException
   */
  @Override
  public void constructIndex() throws IOException {
    String corpusFile = _options._corpusPrefix + "/corpus.tsv";
    System.out.println("Construct index from: " + corpusFile);

    BufferedReader reader = new BufferedReader(new FileReader(corpusFile));
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        processDocument(line);
      }
    } finally {
      reader.close();
    }

    for (Document document : _documents) {
      computeRepresentations((DocumentFull) document);
    }

    System.out.println(
        "Indexed " + Integer.toString(_numDocs) + " docs with " +
        Long.toString(_totalTermFrequency) + " terms.");

    String indexFile = _options._indexPrefix + "/corpus.idx";
    System.out.println("Store index to: " + indexFile);
    ObjectOutputStream writer =
        new ObjectOutputStream(new FileOutputStream(indexFile));
    writer.writeObject(this);
    writer.close();
  }

  private void computeRepresentations(DocumentFull doc) {
    HashMap<String, Double> termMap = new HashMap<>();

    double sumOfSquares = 0.0;
    for (int termIdx : doc.getTermFrequency().keySet()) {
      String term = _terms.get(termIdx);
      double tf = tf(term, doc._docid);
      double idf = idf(term);
      termMap.put(term, tf*idf);
      sumOfSquares += Math.pow(tf*idf, 2);
    }

    double lengthOfVector = Math.sqrt(sumOfSquares);
    for (String term : termMap.keySet()) {

      termMap.put(term, termMap.get(term) / lengthOfVector);
    }

    doc.setVsmRepresentation(termMap);
  }

  private double tf (String term, int docId){
    int termFreqInDocument= documentTermFrequency(term, docId);
    return termFreqInDocument == 0 ? 0 : 1 + Math.log(termFreqInDocument);
  }

  private double idf (String term){
    return Math.log((_numDocs/(corpusDocFrequencyByTerm(term))));
  }

  /**
   * Process the raw content (i.e., one line in corpus.tsv) corresponding to a
   * document, and constructs the token vectors for both title and body.
   * @param content
   */
  private void processDocument(String content) {
    Scanner s = new Scanner(content).useDelimiter("\t");

    Map<Integer, Integer> termFrequencyInDoc = new HashMap<Integer, Integer>();

    String title = s.next();
    Vector<Integer> titleTokens = new Vector<Integer>();
    readTermVector(title, titleTokens, termFrequencyInDoc);

    Vector<Integer> bodyTokens = new Vector<Integer>();
    readTermVector(s.next(), bodyTokens, termFrequencyInDoc);

    int numViews = Integer.parseInt(s.next());
    s.close();

    DocumentFull doc = new DocumentFull(_documents.size(), this);
    doc.setTitle(title);
    doc.setNumViews(numViews);
    doc.setTitleTokens(titleTokens);
    doc.setBodyTokens(bodyTokens);

    Set<Integer> uniqueTerms = new HashSet<Integer>();

    updateStatistics(doc.getTitleTokens(), uniqueTerms, termFrequencyInDoc);
    updateStatistics(doc.getBodyTokens(), uniqueTerms, termFrequencyInDoc);
    for (Integer idx : uniqueTerms) {
      _termDocFrequency.put(idx, _termDocFrequency.get(idx) + 1);
    }

    doc.setTermFrequency(termFrequencyInDoc);
    _documents.add(doc);
    ++_numDocs;
  }
  
  /**
   * Tokenize {@code content} into terms, translate terms into their integer
   * representation, store the integers in {@code tokens}.
   * @param content
   * @param tokens
   * @param termFrequencyInDoc
   */
  private void readTermVector(String content, Vector<Integer> tokens, Map<Integer, Integer> termFrequencyInDoc) {
    Scanner s = new Scanner(content);  // Uses white space by default.
    while (s.hasNext()) {
      String token = s.next();
      int idx = -1;
      if (_dictionary.containsKey(token)) {
        idx = _dictionary.get(token);
      } else {
        idx = _terms.size();
        _terms.add(token);
        _dictionary.put(token, idx);
        _termCorpusFrequency.put(idx, 0);
        _termDocFrequency.put(idx, 0);
        termFrequencyInDoc.put(idx, 0);
      }
      tokens.add(idx);
    }
    return;
  }
  
  /**
   * Update the corpus statistics with {@code tokens}. Using {@code uniques} to
   * bridge between different token vectors.
   * @param tokens
   * @param uniques
   * @param termFrequencyInDoc
   */
  private void updateStatistics(Vector<Integer> tokens, Set<Integer> uniques,
                                Map<Integer, Integer> termFrequencyInDoc) {
    for (int idx : tokens) {
      uniques.add(idx);
      _termCorpusFrequency.put(idx, _termCorpusFrequency.get(idx) + 1);
      ++_totalTermFrequency;

      if (termFrequencyInDoc.containsKey(idx)) {
        termFrequencyInDoc.put(idx, termFrequencyInDoc.get(idx) + 1);
      }
      else {
        termFrequencyInDoc.put(idx, 1);
      }
    }
  }

  ///// Loading related functions.

  /**
   * Loads the index from the index file.
   * 
   * N.B. For this particular implementation, loading the index from the simple
   * serialization format is in fact slower than constructing the index from
   * scratch. For the more efficient indices, loading should be much faster
   * than constructing.
   * 
   * @throws IOException, ClassNotFoundException
   */
  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
    String indexFile = _options._indexPrefix + "/corpus.idx";
    System.out.println("Load index from: " + indexFile);

    ObjectInputStream reader =
        new ObjectInputStream(new FileInputStream(indexFile));
    IndexerFullScan loaded = (IndexerFullScan) reader.readObject();

    this._documents = loaded._documents;
    // Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
    this._numDocs = _documents.size();
    for (Integer freq : loaded._termCorpusFrequency.values()) {
      this._totalTermFrequency += freq;
    }
    this._dictionary = loaded._dictionary;
    this._terms = loaded._terms;
    this._termCorpusFrequency = loaded._termCorpusFrequency;
    this._termDocFrequency = loaded._termDocFrequency;
    reader.close();

    System.out.println(Integer.toString(_numDocs) + " documents loaded " +
        "with " + Long.toString(_totalTermFrequency) + " terms!");
  }

  ///// Serving related functions.

  @Override
  public Document getDoc(int did) {
    return (did >= _documents.size() || did < 0) ? null : _documents.get(did);
  }

  @Override
  public Document nextDoc(Query query, int docid) {
    SearchEngine.Check(false, "Not implemented!");
    return null;
  }

  @Override
  public int corpusDocFrequencyByTerm (String term) {
    return _dictionary.containsKey(term) ?
        _termDocFrequency.get(_dictionary.get(term)) : 0;
  }

  @Override
  public int corpusTermFrequency(String term) {
    return _dictionary.containsKey(term) ?
        _termCorpusFrequency.get(_dictionary.get(term)) : 0;
  }

  @Override
  public int documentTermFrequency(String term, int docid) {
    if (getDoc(docid) == null || !_dictionary.containsKey(term)) return 0;
    DocumentFull doc = (DocumentFull) getDoc(docid);

    return doc.getTermFrequency().containsKey(_dictionary.get(term)) ?
            doc.getTermFrequency().get(_dictionary.get(term)) : 0;
  }

  ///// Utility

  public Vector<String> getTermVector(Vector<Integer> tokens) {
    Vector<String> retval = new Vector<String>();
    for (int idx : tokens) {
      retval.add(_terms.get(idx));
    }
    return retval;
  }

}
