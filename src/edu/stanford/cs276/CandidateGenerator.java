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
        if (cand.length() == 0 ||
                cand.charAt(cand.length() - 1) == ' ' ||
                cand.charAt(0) == ' ') {
            return false;
        }

        int qrr = queryErr(query);
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
        Map<String, Integer> candidates = new HashMap<>();

        if (query.length() == 0 ) return candidates;
        for (int i = 0 ; i < query.length(); ++i ) {
            String cand = query.substring(0,i) + query.substring(i+1);
            if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
                candidates.put(cand, distance);
            }

        }
        return candidates;
    }

    // insert
    public Map<String, Integer> getInsertCandidates(String query, int distance) {
        Map<String, Integer> candidates = new HashMap<>();

        for (int i = 0; i <= query.length(); ++i) {
            for (int j =0; j < alphabet.length; ++j) {
                String cand = query.substring(0, i) + alphabet[j] + query.substring(i);
                if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
                    candidates.put(cand, distance);
                }

            }
        }
        return candidates;
    }

    //replace
    public Map<String, Integer> getReplaceCandidate(String query, int distance) {
        Map<String, Integer> candidates = new HashMap<>();

        if (query.length() == 0 ) return candidates ;
        for (int i = 0; i < query.length(); ++i) {

        for (Character anAlphabet : alphabet) {
                char[] newWord = query.toCharArray();
                newWord[i] = anAlphabet;
                String cand = new String(newWord);

                if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
                    candidates.put(cand, distance);
                }
            }
        }
        return candidates;
    }


    //transpose
    public Map<String, Integer> getTransposeCandidates(String query, int distance) {

        Map<String, Integer> candidates = new HashMap<>();
        for (int i = 0 ; i < query.length() -1; ++i) {
            char[] newWord = query.toCharArray();
            int j = i + 1;
            char temp = newWord[i];
            newWord[i] = newWord[j];
            newWord[j] = temp;
            String cand = new String(newWord);

            if (isQueryValid(query, cand, distance) && !candidateResultMap.containsKey(cand) && !candidates.containsKey(cand)) {
                candidates.put(cand, distance);
            }
        }

        return candidates;
    }

  // Generate all candidates for the target query
  public Map<String, Integer> getCandidates(String query) throws Exception {

      candidateResultMap.clear();
      candidateResultMap.put(query, 0);
      candidateResultMap.putAll(getInsertCandidates(query, 1));
      candidateResultMap.putAll(getDeleteCandidates(query, 1));
      candidateResultMap.putAll(getReplaceCandidate(query, 1));
      candidateResultMap.putAll(getTransposeCandidates( query, 1));

      Iterator<String> iter = candidateResultMap.keySet().iterator();
      Map<String, Integer> candidateResultMapTwo = new HashMap<>();

      while (iter.hasNext()) {
          String candidate = iter.next();
          candidateResultMapTwo.putAll(getInsertCandidates(candidate, 2));
          candidateResultMapTwo.putAll(getDeleteCandidates(candidate, 2));
          candidateResultMapTwo.putAll(getReplaceCandidate(candidate, 2));
          candidateResultMapTwo.putAll(getTransposeCandidates(candidate, 2));
      }

        candidateResultMap.putAll(candidateResultMapTwo);

      return candidateResultMap;
  }


    public int queryErr(String query) {
        String queryT[]  = query.split(" ");
        int qrr = 0;
        for (String queryToken: queryT) {
            if (languageModel.unigram.count(queryToken)==0) qrr++;

        }
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
