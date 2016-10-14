package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Evaluator for HW1.
 * 
 * @author fdiaz
 * @author congyu
 */
class Evaluator {
  public static class DocumentRelevances {
    private Map<Integer, Double> relevances = new HashMap<Integer, Double>();
    
    public DocumentRelevances() { }
    
    public void addDocument(int docid, String grade) {
      relevances.put(docid, convertToBinaryRelevance(grade));
    }
    
    public boolean hasRelevanceForDoc(int docid) {
      return relevances.containsKey(docid);
    }

    public int getTotalRelevantCount() {
      int count = 0;
      for (double score : relevances.values()) {
        if (score == 1.0) {
          count++;
        }
      }
      return count;
    }
    
    public double getRelevanceForDoc(int docid) {
      return relevances.get(docid);
    }
    
    private static double convertToBinaryRelevance(String grade) {
      if (grade.equalsIgnoreCase("Perfect") ||
          grade.equalsIgnoreCase("Excellent") ||
          grade.equalsIgnoreCase("Good")) {
        return 1.0;
      }
      return 0.0;
    }
  }
  
  /**
   * Usage: java -cp src edu.nyu.cs.cs2580.Evaluator [labels] [metric_id]
   */
  public static void main(String[] args) throws IOException {
    Map<String, DocumentRelevances> judgments =
        new HashMap<String, DocumentRelevances>();
    SearchEngine.Check(args.length == 2, "Must provide labels and metric_id!");
    readRelevanceJudgments(args[0], judgments);
    evaluateStdin(Integer.parseInt(args[1]), judgments);
  }

  public static void readRelevanceJudgments(
      String judgeFile, Map<String, DocumentRelevances> judgements)
      throws IOException {
    String line = null;
    BufferedReader reader = new BufferedReader(new FileReader(judgeFile));
    while ((line = reader.readLine()) != null) {
      // Line format: query \t docid \t grade
      Scanner s = new Scanner(line).useDelimiter("\t");
      String query = s.next();
      DocumentRelevances relevances = judgements.get(query);
      if (relevances == null) {
        relevances = new DocumentRelevances();
        judgements.put(query, relevances);
      }
      relevances.addDocument(Integer.parseInt(s.next()), s.next());
      s.close();
    }
    reader.close();
  }

  // @CS2580: implement various metrics inside this function
  public static void evaluateStdin(
      int metric, Map<String, DocumentRelevances> judgments)
          throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));
    List<Integer> results = new ArrayList<Integer>();
    String line = null;
    String currentQuery = "";
    while ((line = reader.readLine()) != null) {
      Scanner s = new Scanner(line).useDelimiter("\t");
      final String query = s.next();
      if (!query.equals(currentQuery)) {
        if (results.size() > 0) {
          switch (metric) {
          case -1:
            evaluateQueryInstructor(currentQuery, results, judgments);
            break;
          case 0: evaluateQueryAtMetric0(currentQuery, results, judgments);
            break;
          case 1: evaluateQueryAtMetric1(currentQuery, results, judgments);
            break;
          case 2: evaluateQueryAtMetric2(currentQuery, results, judgments);
            break;
          case 3:
          case 4: evaluateQueryAtMetric4(currentQuery, results, judgments);
            break;
          case 5:
          case 6: evaluateQueryAtMetric6(currentQuery, results, judgments);
            break;
          default:
            // @CS2580: add your own metric evaluations above, using function
            // names like evaluateQueryMetric0.
            System.err.println("Requested metric not implemented!");
          }
          results.clear();
        }
        currentQuery = query;
      }
      results.add(Integer.parseInt(s.next()));
      s.close();
    }
    reader.close();
    if (results.size() > 0) {
      evaluateQueryAtMetric6(currentQuery, results, judgments);
    }
  }
  
  public static void evaluateQueryInstructor(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {
    double R = 0.0;
    double N = 0.0;
    for (int docid : docids) {
      DocumentRelevances relevances = judgments.get(query);
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid)) {
          R += relevances.getRelevanceForDoc(docid);
        }
        ++N;
      }
    }
    System.out.println(query + "\t" + Double.toString(R / N));
  }

  public static void evaluateQueryAtMetric0(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {

    DocumentRelevances relevances = judgments.get(query);
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> relevantDocCumulative = getRelevantDocCumulative(relevances, docids);

    System.out.println(query + "\t" + Double.toString(getPrecisionAtIndex(1, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getPrecisionAtIndex(5, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getPrecisionAtIndex(10, relevantDocCumulative)));
  }


  public static void evaluateQueryAtMetric1(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {
    DocumentRelevances relevances = judgments.get(query);
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> relevantDocCumulative = getRelevantDocCumulative(relevances, docids);

    System.out.println(query + "\t" + Double.toString(getRecallAtIndex(1, relevances, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getRecallAtIndex(5, relevances, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getRecallAtIndex(10, relevances, relevantDocCumulative)));
  }

  public static void evaluateQueryAtMetric6(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {
    DocumentRelevances relevances = judgments.get(query);
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> relevantDocCumulative = getRelevantDocCumulative(relevances, docids);

    int i;
    for (i = 0; i < relevantDocCumulative.size(); i++) {
      if (relevantDocCumulative.get(i) == 1) {
        break;
      }
    }

    System.out.println(query + "\t" +  (1.0 / (i+1)));
  }

  public static void evaluateQueryAtMetric4(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {
    DocumentRelevances relevances = judgments.get(query);
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> relevantDocCumulative = getRelevantDocCumulative(relevances, docids);

    double precisionSum = 0;
    double currentCount = 0;
    for (int i = 0; i < relevantDocCumulative.size(); i++) {

      if (relevantDocCumulative.get(i) > currentCount) {
        precisionSum += getPrecisionAtIndex(i+1, relevantDocCumulative);
        currentCount = relevantDocCumulative.get(i);
      }

    }

    System.out.println(query + "\t" + precisionSum / relevantDocCumulative.get(relevantDocCumulative.size() - 1));
  }

  public static void evaluateQueryAtMetric2(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {
    DocumentRelevances relevances = judgments.get(query);
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> relevantDocCumulative = getRelevantDocCumulative(relevances, docids);

    double beta = 0.5;
    System.out.println(query + "\t" + Double.toString(getFMeasureAtIndex(1, beta, relevances, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getFMeasureAtIndex(5, beta, relevances, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getFMeasureAtIndex(10, beta, relevances, relevantDocCumulative)));
  }

  public static double getFMeasureAtIndex(int index, double beta, DocumentRelevances relevances, List<Double> relevantDocCumulative) {

    double precision = getPrecisionAtIndex(index, relevantDocCumulative);
    double recall = getRecallAtIndex(index, relevances, relevantDocCumulative);

    return (1 + Math.pow(beta, 2))*(precision*recall)/((Math.pow(beta, 2) * precision) + recall);
  }


  public static double getPrecisionAtIndex(int index, List<Double> relevantDocCumulative) {

    return relevantDocCumulative.get(index-1) / index;

  }

  public static double getRecallAtIndex(int index, DocumentRelevances relevances, List<Double> relevantDocCumulative) {


    int totalRelevantCount = relevances.getTotalRelevantCount();

    return relevantDocCumulative.get(index-1) / totalRelevantCount;
  }

  public static List<Double> getRelevantDocCumulative(DocumentRelevances relevances, List<Integer> docids) {

    List<Double> cumulativeCount = new ArrayList<>();
    double count = 0;
    for (int i = 0; i < docids.size(); i++) {
      int docid = docids.get(i);
      if (relevances.hasRelevanceForDoc(docid) && relevances.getRelevanceForDoc(docid) == 1.0) {
        count++;
      }
      cumulativeCount.add(count);
    }
    return cumulativeCount;
  }


}
