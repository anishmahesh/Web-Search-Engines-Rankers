package edu.nyu.cs.cs2580;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

/**
 * Representation of a user query.
 * 
 * In HW1: instructors provide this simple implementation.
 * 
 * In HW2: students must implement {@link QueryPhrase} to handle phrases.
 * 
 * @author congyu
 * @auhtor fdiaz
 */
public class Query {
  public String _query = null;
  public Vector<String> _tokens = new Vector<String>();
  public HashMap<String, Double> _vsm = new HashMap<>();

  public Query(String query) {
    _query = query;
  }

  public void processQuery() {
    if (_query == null) {
      return;
    }
    Scanner s = new Scanner(_query);
    while (s.hasNext()) {
      _tokens.add(s.next());
    }
    s.close();
  }

  public void computeVsm() {

    for (String term : _tokens) {

      if (_vsm.containsKey(term)) {
        _vsm.put(term, _vsm.get(term) + 1);
      }
      else {
        _vsm.put(term, 1.0);
      }
    }

    double sumOfSquares = 0;
    for (double value : _vsm.values()) {
      sumOfSquares += Math.pow(value, 2);
    }

    double lengthOfVector = Math.sqrt(sumOfSquares);

    for (String term : _vsm.keySet()) {
      _vsm.put(term, _vsm.get(term) / lengthOfVector);
    }
  }
}
