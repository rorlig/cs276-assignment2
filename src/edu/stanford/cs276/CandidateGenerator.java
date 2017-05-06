package edu.stanford.cs276;

import java.io.Serializable;
import java.util.*;

public class CandidateGenerator implements Serializable {

	private static final long serialVersionUID = 1L;
	private static CandidateGenerator cg_;
    private LanguageModel languageModel ;
    private NoisyChannelModel nsm;

    Map<String, Integer> candidateResultMap = new HashMap<>();

    String query;


    /**
  * Constructor
  * IMPORTANT NOTE: As in the NoisyChannelModel and LanguageModel classes,
  * we want this class to use the Singleton design pattern.  Therefore,
  * under normal circumstances, you should not change this constructor to
  * 'public', and you should not call it from anywhere outside this class.
  * You can get a handle to a CandidateGenerator object using the static
  * 'get' method below.
  */
  private CandidateGenerator() {}

  public static CandidateGenerator get() throws Exception {
    if (cg_ == null) {
      cg_ = new CandidateGenerator();
    }
    return cg_;
  }

  public static final Character[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', ' ', ',' };




    private boolean isQueryValid(String query, String cand, int distance) {

      if (cand.contains("  ")) return  false;
      String tokens[]  = cand.split(" ");
//      String queryT[]  = query.split(" ");

        if (cand.length() == 0 ||
                cand.charAt(cand.length() - 1) == ' ' ||
                cand.charAt(0) == ' ') {
//            System.out.println("here ...1");
            return false;
        }
//      if (tokens.length < queryT.length) return false;

        int qrr = queryErr(query);
//        for (String queryToken: queryT) {
//            if (languageModel.unigram.count(queryToken)==0) qrr++;
//
//        }
//      distance = Math.min(tokens.length, distance);
      int err = 0;
      for (String token: tokens) {
        if (languageModel.unigram.count(token)==0) err++;
      }

      if (err>=qrr && err>0) return false;
        // return false if any err..


      return true;


    }




    // delete
    public Map<String, Integer> getDeleteCandidates(String query, int distance) {
//        Set<CandidateResult> candidates = new HashSet<>();
        Map<String, Integer> candidates = new HashMap<>();

        if (query.length() == 0 ) return candidates;
        for (int i = 0 ; i < query.length(); ++i ) {

            String cand = query.substring(0,i) + query.substring(i+1);
//            cand = cand.trim().replace("  ", " ");

//            CandidateResult result = new CandidateResult(cand, distance);

            if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
//                    candidates.add(result.getCandidate())
//                    candidateResultMap.put(cand, distance);
                candidates.put(cand, distance);
//                    candidates.add(result);
            }

        }
//        return candidates;
        return candidates;
    }

    // insert
    public Map<String, Integer> getInsertCandidates(String query, int distance) {

      // go through the candidate for the entire query.
        // if the candidate has more than dist errors discard.
//        Set<CandidateResult> candidates = new HashSet<>();

        Map<String, Integer> candidates = new HashMap<>();

        for (int i = 0; i <= query.length(); ++i) {

            for (int j =0; j < alphabet.length; ++j) {
                String cand = query.substring(0, i) + alphabet[j] + query.substring(i);
//                cand = cand.trim().replace("  ", " ");
//                System.out.println(cand);
                if (cand.equals("vcimage generated on")) {
                    System.out.println("created the correct candidate");
                }

//                CandidateResult result = new CandidateResult(cand, distance);

                if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
//                    candidates.add(result.getCandidate())
//                    candidateResultMap.put(cand, distance);
                    candidates.put(cand, distance);
//                    candidates.add(result);
                }

            }
        }
        return candidates;
    }

    //replace
    public Map<String, Integer> getReplaceCandidate(String query, int distance) {
//        Set<CandidateResult> candidates = new HashSet<>();
        Map<String, Integer> candidates = new HashMap<>();

        if (query.length() == 0 ) return candidates ;
        for (int i = 0; i < query.length(); ++i) {

        for (Character anAlphabet : alphabet) {
                char[] newWord = query.toCharArray();
                newWord[i] = anAlphabet;
                String cand = new String(newWord);

//                cand = cand.trim().replace("  ", " ");

//                CandidateResult result = new CandidateResult(cand, distance);

            if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
//                    candidates.add(result.getCandidate())
//                    candidateResultMap.put(cand, distance);
                candidates.put(cand, distance);
//                    candidates.add(result);
            }
//                if (languageModel.unigram.count(cand) != 0){
//                    CandidateResult candidateResult = new CandidateResult(cand.trim().replace("  ", " "), distance );
//                    candidates.add(candidateResult);
//                }
            }
        }
        return candidates;
    }


    //transpose
    public Map<String, Integer> getTransposeCandidates(String query, int distance) {
//        Set<CandidateResult> candidates = new HashSet<>();
        //length <= 1 ==> candidates..
//        if (query.length() <=1 ) return  candidates;


        Map<String, Integer> candidates = new HashMap<>();



        for (int i = 0 ; i < query.length() -1; ++i) {
            char[] newWord = query.toCharArray();
            int j = i + 1;
            char temp = newWord[i];
            newWord[i] = newWord[j];
            newWord[j] = temp;
            String cand = new String(newWord);
//            cand = cand.trim().replace("  ", " ");

//            CandidateResult result = new CandidateResult(cand, distance);

            if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
//                    candidates.add(result.getCandidate())
//                    candidateResultMap.put(cand, distance);
                candidates.put(cand, distance);
//                    candidates.add(result);
            }
//            if (isQueryValid(query, cand, distance)) {
//                candidates.add(new CandidateResult(cand, distance));
//            }

//            if (languageModel.unigram.count(cand) != 0){
//                CandidateResult candidateResult = new CandidateResult(cand.trim().replace("  ", " "), distance );
//                candidates.add(candidateResult);
//            }
        }

        return candidates;
    }




  // Generate all candidates for the target query
  public Map<String, Integer> getCandidates(String query) throws Exception {


//      this.query = query;
//      String[] tokens = query.trim().split("\\s+");
////      ArrayList<Set<String>> resultList = new ArrayList<>();
//      Set<CandidateResult> candidateResultOne = new HashSet<>();
//      Set<CandidateResult> candidateResultTwo = new HashSet<>();

      candidateResultMap.clear();

      // tokens ---
//      if (isQueryValid(query, query, 0)){
//          candidateResult.add(new CandidateResult(query, 0));
//      }
//      for (int dist = 0; dist<2; ++dist) {


//      candidateResultOne.add(new CandidateResult(query, 0));

      candidateResultMap.put(query, 0);
      //don't get distance 1 & 2 if not required
//      if (queryErr(query) == 0) {
//          System.out.println("all terms found!!!");
//          return candidateResultMap;
//      }

      candidateResultMap.putAll(getInsertCandidates(query, 1));
      candidateResultMap.putAll(getDeleteCandidates(query, 1));
      candidateResultMap.putAll(getReplaceCandidate(query, 1));
      candidateResultMap.putAll(getTransposeCandidates( query, 1));
//      }

//      return candidateResultOne;

//      candidateResultTwo.addAll(candidateResultOne);


        Iterator<String> iter = candidateResultMap.keySet().iterator();

      Map<String, Integer> candidateResultMapTwo = new HashMap<>();


      while (iter.hasNext()) {
            String candidate = iter.next();

//            System.out.println("candidate "  + candidate + " distance " + candidateResultMap.get(candidate));
//            if (candidate.equals(query)) continue;
          candidateResultMapTwo.putAll(getInsertCandidates(candidate, 2));
          candidateResultMapTwo.putAll(getDeleteCandidates(candidate, 2));
          candidateResultMapTwo.putAll(getReplaceCandidate(candidate, 2));
          candidateResultMapTwo.putAll(getTransposeCandidates(candidate, 2));




        }

        candidateResultMap.putAll(candidateResultMapTwo);

//      iter = candidateResultMap.keySet().iterator();

//      Map<String, Integer> removeMap = new HashMap<>();


//      while (iter.hasNext()) {
//          String candidate = iter.next();
//
//          int err = queryErr(candidate);
//
//          if (err>0) {
//              removeMap.put(candidate, candidateResultMapTwo.get(candidate));
//          }
//
//
//
//
//      }
//
//      System.out.println("removing elements " + removeMap.size());
//
//      iter = removeMap.keySet().iterator();
//      while (iter.hasNext()) {
//          String candidate = iter.next();
//          candidateResultMap.remove(removeMap.get(candidate));
//      }


//      iter = candidateResultMap.keySet().iterator();

//      while (iter.hasNext()) {
//          String candidate = iter.next();
//          int distance = candidateResultMap.get(candidate);
//          if (distance==1) {
//              System.out.println("candidate " + candidate + " distance " + candidateResultMap.get(candidate));
//          }
//      }

////
//          for (String candidate: candidateResultMap.keySet()) {
//              if (candidate.equals(query)) continue;
//              getInsertCandidates(candidate, 2);
//              getDeleteCandidates(candidate, 2);
//              getReplaceCandidate(candidate, 2);
//              getTransposeCandidates( candidate, 2);
//          }
//
//
//      /// need to take out all tokens from the query ...
//      /// how many errors allowed per query before pruning.
////      for (String token: tokens) {
////          // does the unigram dict contain the token.
////          if (languageModel.unigram.count(token) == 0) {
////            //does not contain it..
////              resultList.add(getCandidatesForWord(token));
////              candidateResult.add(getCandidatesForWord(token, 1));
////          } else {
////              //contains the word
////              //just add the word it self as possible..
////              Set<String> candidateSet = new HashSet<>();
////              candidateSet.add(token);
////              Set<CandidateResult> candidateResultSet = new HashSet<>();
////              candidateResultSet.add(new CandidateResult(token,  0));
////              resultList.add(candidateSet);
////              candidateResult.add(candidateResultSet);
////
////          }
////
////      }
////      //Set<String> finalCandidateSet = permute(resultList);
////      Set<CandidateResult> finalCandidateResultSet = permute1(candidateResult);
//
    return candidateResultMap;
  }

//    private Set<CandidateResult> permute1(ArrayList<Set<CandidateResult>> candidateResult) {
//        Set<CandidateResult> resultSet = new HashSet<>();
//        if (candidateResult.size()==0) return resultSet;
//        resultSet = candidateResult.get(0);
//
//        for (int i = 1 ; i < candidateResult.size() ; ++i) {
//            // okay look at this now.
//            Set<CandidateResult> wordList = candidateResult.get(i);
//            Set<CandidateResult> copyList  = new HashSet<>(resultSet);
//            resultSet.clear();
//
//            for (CandidateResult word: wordList) {
//                //append this to all the resultSet items
//                int distance = Integer.MIN_VALUE;
//                for (CandidateResult result: copyList) {
//                    distance = Math.max(result.getDistance(), distance);
//                    resultSet.add(new CandidateResult(result.getCandidate() + " " + word.getCandidate(), word.getDistance() + result.getDistance()));
//
//                }
//            }
//        }
//        return resultSet;
//  }

//    private Set<CandidateResult> getCandidatesForWord(String query, int distance) {
//        // Edit distance 1
//        Set<CandidateResult> candidates = new HashSet<>();
//        candidates.addAll(getDeleteCandidates(query,distance));
//        candidates.addAll(getInsertCandidates(query, distance));
//        candidates.addAll(getReplaceCandidate(query, distance));
//        candidates.addAll(getTransposeCandidates(query, distance));
//
//        // Edit distance 2
//        Set<CandidateResult> candidateD2 = getCandidates(candidates, distance + 1);
//        candidateD2.addAll(candidates);
//        candidates.clear();
//        return candidateD2;
//    }


//    private Set<String> getCandidates(Set<String> candidates) {
//        Set<String> candidatesD2 = new HashSet<>();
//
//        for (String candidate : candidates) {
//            candidatesD2.addAll(getDeleteCandidates(candidate));
//            candidatesD2.addAll(getInsertCandidates(candidate));
//            candidatesD2.addAll(getReplaceCandidate(candidate));
//            candidatesD2.addAll(getTransposeCandidates(candidate));
//        }
//        return candidatesD2;
//    }

//    private Set<CandidateResult> getCandidates(Set<CandidateResult> candidates, int distance) {
//        Set<CandidateResult> candidatesD2 = new HashSet<>();
//
//        for (CandidateResult candidate : candidates) {
//            candidatesD2.addAll(getDeleteCandidates(candidateResultOne, candidate.getCandidate(), distance));
//            candidatesD2.addAll(getInsertCandidates(candidateResultOne, candidate.getCandidate(), distance));
//            candidatesD2.addAll(getReplaceCandidate(candidate.getCandidate(), distance));
//            candidatesD2.addAll(getTransposeCandidates(candidate.getCandidate(), distance));
//        }
//        return candidatesD2;
//    }

    public int queryErr(String query) {
        String queryT[]  = query.split(" ");

        int qrr = 0;
        for (String queryToken: queryT) {
//            System.out.println("queryToken "  + queryToken + " count " + languageModel.unigram.count(queryToken));
            if (languageModel.unigram.count(queryToken)==0) qrr++;

        }
//        System.out.println("query error" + qrr);
        return qrr;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }

    public static CandidateGenerator get(LanguageModel languageModel, NoisyChannelModel nsm) {
        if (cg_ == null) {
            cg_ = new CandidateGenerator();
        }
        return cg_;

    }

}
