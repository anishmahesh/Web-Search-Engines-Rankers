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
import java.util.Collections;

/**
 * Evaluator for HW1.
 *
 * @author fdiaz
 * @author congyu
 */
class Evaluator {
  public static class DocumentRelevances {
    private Map<Integer, Double> relevances = new HashMap<Integer, Double>();
    private Map<Integer, Double> scoredRelevances = new HashMap<Integer, Double>();

    public DocumentRelevances() { }

    public void addDocument(int docid, String grade) {
      relevances.put(docid, convertToBinaryRelevance(grade));
      scoredRelevances.put(docid, convertToScoredRelevance(grade));
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

    public double getScoredRelevanceForDoc(int docid) {
      return scoredRelevances.get(docid);
    }

    private static double convertToBinaryRelevance(String grade) {

      if (grade.equalsIgnoreCase("Perfect") ||
          grade.equalsIgnoreCase("Excellent") ||
          grade.equalsIgnoreCase("Good")) {
        return 1.0;
      }
      return 0.0;
    }

    private static double convertToScoredRelevance(String grade) {
      Map<String, Double> scoredRelevances = new HashMap<String, Double>();
      if(grade.length()>0) {
        scoredRelevances.put(("Perfect").toLowerCase(), 10.0);
        scoredRelevances.put(("Excellent").toLowerCase(), 7.0);
        scoredRelevances.put(("Good").toLowerCase(), 5.0);
        scoredRelevances.put(("Fair").toLowerCase(), 1.0);
        scoredRelevances.put(("Bad").toLowerCase(), 0.0);
        return scoredRelevances.get(grade.toLowerCase());
      } else {
        return 0.0;
      }
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
          case 3: evaluateQueryAtMetric3(currentQuery, results, judgments);
            break;
          case 4: evaluateQueryAtMetric4(currentQuery, results, judgments);
            break;
          case 5: evaluateQueryAtMetric5(currentQuery, results, judgments);
            break;
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
        case 3: evaluateQueryAtMetric3(currentQuery, results, judgments);
          break;
        case 4: evaluateQueryAtMetric4(currentQuery, results, judgments);
          break;
        case 5: evaluateQueryAtMetric5(currentQuery, results, judgments);
          break;
        case 6: evaluateQueryAtMetric6(currentQuery, results, judgments);
          break;
        default:
          // @CS2580: add your own metric evaluations above, using function
          // names like evaluateQueryMetric0.
          System.err.println("Requested metric not implemented!");
      }
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
    int totalRelevantCount = relevances.getTotalRelevantCount();

    System.out.println(query + "\t" + Double.toString(getRecallAtIndex(1, totalRelevantCount, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getRecallAtIndex(5, totalRelevantCount, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getRecallAtIndex(10, totalRelevantCount, relevantDocCumulative)));
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
    int totalRelevantCount = relevances.getTotalRelevantCount();

    double beta = 0.5;
    System.out.println(query + "\t" + Double.toString(getFMeasureAtIndex(1, beta, totalRelevantCount, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getFMeasureAtIndex(5, beta, totalRelevantCount, relevantDocCumulative)));
    System.out.println(query + "\t" + Double.toString(getFMeasureAtIndex(10, beta, totalRelevantCount, relevantDocCumulative)));
  }

  public static void evaluateQueryAtMetric3(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {
    DocumentRelevances relevances = judgments.get(query);
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> relevantDocCumulative = getTotalRelevantDocCumulative(relevances, docids);
    List<Double> allRecall = getAllRecall (relevances, relevantDocCumulative);
    List<Double> allMaxPrecision = getAllMaxPrecision (relevances, relevantDocCumulative);
    try {
      Double[] Precisions = getPrecisionForRecallRange(allRecall, allMaxPrecision);
      double count = 0.0;
      for(int i=0; i<=10;i++){
        count = i * 1.0;
        System.out.println(query + "\t" + Precisions[i] + " at " + count/10);
      }
    } catch (IndexOutOfBoundsException e) {
      System.err.println("IndexOutOfBoundsException: " + e.getMessage());
      System.err.println("Not all relevant cases included, please add more docs to result. To do this, in your query add greater count for :&num=<count>.");
      System.err.println("Query Format :\n http://<host>:<port>/search?query=<query>&ranker=<ranker>&format=<format>&num=<count>");
    }
  }

  public static List<Double> getAllRecall(DocumentRelevances relevances, List<Double> relevantDocCumulative){
    List<Double> allRecall = new ArrayList<>();
    int totalRelevantCount = relevances.getTotalRelevantCount();
    for(int i=relevantDocCumulative.size(); i>0; i--){
      allRecall.add(getRecallAtIndex(i,totalRelevantCount,relevantDocCumulative));
    }
    return allRecall;
  }

  public static List<Double> getAllMaxPrecision(DocumentRelevances relevances, List<Double> relevantDocCumulative){
    List<Double> allMaxPrecision = new ArrayList<>();
    double maxPrecision = 0.0;
    double precision = 0.0;
    for(int i=relevantDocCumulative.size(); i>0; i--){
      precision = getPrecisionAtIndex(i,relevantDocCumulative);
      if(precision > maxPrecision){
        maxPrecision = precision;
      }
      allMaxPrecision.add(maxPrecision);
    }
    return allMaxPrecision;
  }

  public static Double[] getPrecisionForRecallRange(List<Double> allRecall, List<Double> allPrecision){
    double previousPrecision = 1.0;
    Double[] Precision = new Double[11];
    for(int i=0; i<11;i++){
      Precision[i] = -1.0;
    }
    for (int i = 0; i < allRecall.size(); i++) {
        if (allRecall.get(i) == 1.0) {
          Precision[10] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.9) {
          Precision[9] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.8) {
          Precision[8] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.7) {
          Precision[7] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.6) {
          Precision[6] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.5) {
          Precision[5] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.4) {
          Precision[4] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.3) {
          Precision[3] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.2) {
          Precision[2] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.1) {
          Precision[1] = allPrecision.get(i);
        } else if (allRecall.get(i) == 0.0) {
          Precision[0] = allPrecision.get(i);
        } else if (allRecall.get(i) < 0.9 && allRecall.get(i) > 0.8 && Precision[9] == -1) {
          Precision[9] = previousPrecision;
        } else if (allRecall.get(i) < 0.8 && allRecall.get(i) > 0.7 && Precision[8] == -1) {
          Precision[8] = previousPrecision;
        } else if (allRecall.get(i) < 0.7 && allRecall.get(i) > 0.6 && Precision[7] == -1) {
          Precision[7] = previousPrecision;
        } else if (allRecall.get(i) < 0.6 && allRecall.get(i) > 0.5 && Precision[6] == -1) {
          Precision[6] = previousPrecision;
        } else if (allRecall.get(i) < 0.5 && allRecall.get(i) > 0.4 && Precision[5] == -1) {
          Precision[6] = previousPrecision;
        } else if (allRecall.get(i) < 0.4 && allRecall.get(i) > 0.3 && Precision[4] == -1) {
          Precision[4] = previousPrecision;
        } else if (allRecall.get(i) < 0.3 && allRecall.get(i) > 0.2 && Precision[3] == -1) {
          Precision[3] = previousPrecision;
        } else if (allRecall.get(i) < 0.2 && allRecall.get(i) > 0.1 && Precision[2] == -1) {
          Precision[2] = previousPrecision;
        } else if (allRecall.get(i) < 0.1 && allRecall.get(i) > 0.0 && Precision[1] == -1) {
          Precision[1] = previousPrecision;
        }
        previousPrecision = allPrecision.get(i);
      }
      if (Precision[0] == -1) {
        Precision[0] = previousPrecision;
      }
      for (int i = 10; i >= 0; i--) {
        if (Precision[i] == -1.0) {
          Precision[i] = Precision[i + 1];
        }
      }
      return Precision;

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

  public static void evaluateQueryAtMetric5(
          String query, List<Integer> docids,
          Map<String, DocumentRelevances> judgments) {
    DocumentRelevances scoredRelevances = judgments.get(query);
    if (scoredRelevances == null) {
      System.out.println("Query [" + query + "] not found!");
      return;
    }

    List<Double> resultRelevance = getRelevanceAtEachResult(scoredRelevances, docids);
    List<Double> DCG = getDCGForAllResults(resultRelevance);
    Collections.sort(resultRelevance, Collections.reverseOrder());
    List<Double> idealDCG = getDCGForAllResults(resultRelevance);

    System.out.println(query + "\t" + Double.toString(getNDCGAtIndex(1, DCG, idealDCG)));
    System.out.println(query + "\t" + Double.toString(getNDCGAtIndex(5, DCG, idealDCG)));
    System.out.println(query + "\t" + Double.toString(getNDCGAtIndex(10, DCG, idealDCG)));
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

  public static double getFMeasureAtIndex(int index, double beta, int totalRelevantCount, List<Double> relevantDocCumulative) {

    double precision = getPrecisionAtIndex(index, relevantDocCumulative);
    double recall = getRecallAtIndex(index, totalRelevantCount, relevantDocCumulative);

    return (1 + Math.pow(beta, 2))*(precision*recall)/((Math.pow(beta, 2) * precision) + recall);
  }


  public static double getPrecisionAtIndex(int index, List<Double> relevantDocCumulative) {

    return relevantDocCumulative.get(index-1) / index;

  }

  public static double getRecallAtIndex(int index, int totalRelevantCount, List<Double> relevantDocCumulative) {
    return relevantDocCumulative.get(index-1) / totalRelevantCount;
  }

  public static double getNDCGAtIndex(int index, List<Double> DCG, List<Double> IdealDCG){
    double DCGval = 0.0;
    double IdealDCGval =  1.0;
    if(DCG.size() > index-1 && IdealDCG.size() > index-1){
      DCGval = DCG.get(index-1);
      IdealDCGval = IdealDCG.get(index-1);
    } else if(DCG.size()>0 && IdealDCG.size() > 0){
      DCGval = DCG.get(DCG.size()-1);
      IdealDCGval = IdealDCG.get(DCG.size()-1);
    }
    return DCGval/IdealDCGval;
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

  public static List<Double> getTotalRelevantDocCumulative(DocumentRelevances relevances, List<Integer> docids) {

    List<Double> cumulativeCount = new ArrayList<>();
    double count = 0;
    double totalCount = relevances.getTotalRelevantCount();
    for (int i = 0; i < docids.size(); i++) {
      int docid = docids.get(i);
      if (relevances.hasRelevanceForDoc(docid) && relevances.getRelevanceForDoc(docid) == 1.0) {
        count++;
      }
      cumulativeCount.add(count);
      if(count == totalCount){
        break;
      }
    }
    return cumulativeCount;
  }

  public static List<Double> getRelevanceAtEachResult(DocumentRelevances scoredRelevances, List<Integer> docids) {
    List<Double> resultRelevanceList = new ArrayList<>();
    for( int docid : docids) {
      if(scoredRelevances.hasRelevanceForDoc(docid)){
        resultRelevanceList.add(scoredRelevances.getScoredRelevanceForDoc(docid));
      } else {
        resultRelevanceList.add(0.0);
      }
    }
    return resultRelevanceList;
  }

  public static List<Double> getDCGForAllResults(List<Double> resultRelevance){
    List<Double> DCGVal = new ArrayList<>();
    double sum = 0.0;
    int count = 1;
    for(double rel: resultRelevance){
      if(count > 1){
        sum += (rel/((Math.log(count)/Math.log(2))));
      } else {
        sum += rel;
      }
      count++;
      DCGVal.add(sum);
    }
    return DCGVal;
  }
}
