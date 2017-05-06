package edu.stanford.cs276;

import java.io.Serializable;
import java.util.*;

public class CandidateGenerator implements Serializable {

	private static final long serialVersionUID = 1L;
	private static CandidateGenerator cg_;
    private LanguageModel languageModel ;
    private NoisyChannelModel nsm;

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


  // delete
  public Set<String> getDeleteCandidates(String query) {
    Set<String> candidates = new HashSet<>();
    if (query.length() == 0 ) return  candidates;
    for (int i = 0 ; i < query.length(); ++i ) {
        String cand = query.substring(0,i) + query.substring(i+1);
        if (languageModel.unigram.count(cand) != 0)
            candidates.add(cand);
    }
    return candidates;

  }

    // insert
    public Set<String> getInsertCandidates(String query) {
        Set<String> candidates = new HashSet<>();
        for (Character anAlphabet : alphabet) {
            for (int i = 0; i <= query.length(); ++i) {
                String cand = query.substring(0, i) + anAlphabet + query.substring(i);
                if (anAlphabet==' '){
                    boolean valid=true;
                    String tokens[] = cand.split("\\s+");
                    for (String token: tokens){
                        if (languageModel.unigram.count(token)==0)
                            valid=false;

                    }
                    if (valid) {
                        for (String token: tokens) {
                            candidates.add(token);

                        }
                    }
                }
                if (languageModel.unigram.count(cand) != 0)
                    candidates.add(cand);
            }
        }
        return candidates;
    }

    //replace
    public Set<String> getReplaceCandidate(String query) {
        Set<String> candidates = new HashSet<>();
        if (query.length() == 0 ) return  candidates;

        for (Character anAlphabet : alphabet) {
            for (int i = 0; i < query.length(); ++i) {
                char[] newWord = query.toCharArray();
                newWord[i] = anAlphabet;
                String cand = new String(newWord);

                if (languageModel.unigram.count(cand) != 0)
                    candidates.add(cand);
            }
        }
        return candidates;
    }


    //transpose
    public Set<String> getTransposeCandidates(String query) {
        Set<String> candidates = new HashSet<>();
        if (query.length() <=1 ) return  candidates;
        for (int i = 0 ; i < query.length() -1; ++i) {
            char[] newWord = query.toCharArray();
            int j = i + 1;
            char temp = newWord[i];
            newWord[i] = newWord[j];
            newWord[j] = temp;

            String cand = new String(newWord);

            if (languageModel.unigram.count(cand) != 0)
                candidates.add(cand);
        }
        return candidates;
    }


    // delete
    public Set<CandidateResult> getDeleteCandidates(String query, int distance) {
        Set<CandidateResult> candidates = new HashSet<>();
        if (query.length() == 0 ) return  candidates;
        for (int i = 0 ; i < query.length(); ++i ) {
            String cand = query.substring(0,i) + query.substring(i+1);
            if (languageModel.unigram.count(cand) != 0) {
                CandidateResult candidateResult = new CandidateResult(cand.trim().replace("  ", " "), distance );
                candidates.add(candidateResult);
            }

        }
        return candidates;
    }

    // insert
    public Set<CandidateResult>  getInsertCandidates(String query, int distance) {
        Set<CandidateResult> candidates = new HashSet<>();
        for (Character anAlphabet : alphabet) {
            for (int i = 0; i <= query.length(); ++i) {
                String cand = query.substring(0, i) + anAlphabet + query.substring(i);
                if (anAlphabet==' ') {
                    boolean valid = true;
                    for (String str : cand.split("\\s+")) {
                        if (languageModel.unigram.count(str) == 0)
                            valid = false;

                    }
                    if (valid)
                        candidates.add(new CandidateResult(cand, distance ));
                }
                if (languageModel.unigram.count(cand) != 0) {
                    CandidateResult candidateResult = new CandidateResult(cand.trim().replace("  ", " "), distance );
                    candidates.add(candidateResult);
                }
            }
        }
        return candidates;
    }

    //replace
    public Set<CandidateResult>  getReplaceCandidate(String query, int distance) {
        Set<CandidateResult> candidates = new HashSet<>();
        if (query.length() == 0 ) return  candidates;

        for (Character anAlphabet : alphabet) {
            for (int i = 0; i < query.length(); ++i) {
                char[] newWord = query.toCharArray();
                newWord[i] = anAlphabet;
                String cand = new String(newWord);

                if (languageModel.unigram.count(cand) != 0){
                    CandidateResult candidateResult = new CandidateResult(cand.trim().replace("  ", " "), distance );
                    candidates.add(candidateResult);
                }
            }
        }
        return candidates;
    }


    //transpose
    public Set<CandidateResult> getTransposeCandidates(String query, int distance) {
        Set<CandidateResult> candidates = new HashSet<>();
        //length <= 1 ==> candidates..
        if (query.length() <=1 ) return  candidates;
        for (int i = 0 ; i < query.length() -1; ++i) {
            char[] newWord = query.toCharArray();
            int j = i + 1;
            char temp = newWord[i];
            newWord[i] = newWord[j];
            newWord[j] = temp;
            String cand = new String(newWord);

            if (languageModel.unigram.count(cand) != 0){
                CandidateResult candidateResult = new CandidateResult(cand.trim().replace("  ", " "), distance );
                candidates.add(candidateResult);
            }
        }
        return candidates;
    }

    private Set<String> filter(Set<String> candidates) {
      Set<String> filter = new HashSet<>();
      for (String candidate: candidates) {
          if (languageModel.unigram.count(candidate) != 0) {
              filter.add(candidate);
          }
      }

      return filter;

    }

  private Set<String> getCandidatesForWord(String query) {
      // Edit distance 1
      Set<String> candidates = new HashSet<String>();
      candidates.addAll(getDeleteCandidates(query));
      candidates.addAll(getInsertCandidates(query));
      candidates.addAll(getReplaceCandidate(query));
      candidates.addAll(getTransposeCandidates(query));

      candidates = filter(candidates);
/*
      // Edit distance 2
      Set<String> candidateD2 = getCandidates(candidates);
      candidateD2.addAll(candidates);
      candidateD2 = filter(candidateD2);
      return candidateD2;
*/
      return candidates;
  }
  // Generate all candidates for the target query
  public Set<CandidateResult> getCandidates(String query) throws Exception {

      String[] tokens = query.trim().split("\\s+");
      ArrayList<Set<String>> resultList = new ArrayList<>();
      ArrayList<Set<CandidateResult>> candidateResult = new ArrayList<>();


      /// need to take out all tokens from the query ...
      /// how many errors allowed per query before pruning.
      for (String token: tokens) {
          // does the unigram dict contain the token.
          if (languageModel.unigram.count(token) == 0) {
            //does not contain it..
              resultList.add(getCandidatesForWord(token));
              candidateResult.add(getCandidatesForWord(token, 1));
          } else {
              //contains the word
              //just add the word it self as possible..
              Set<String> candidateSet = new HashSet<>();
              candidateSet.add(token);
              Set<CandidateResult> candidateResultSet = new HashSet<>();
              candidateResultSet.add(new CandidateResult(token,  0));
              resultList.add(candidateSet);
              candidateResult.add(candidateResultSet);

          }

      }
      //Set<String> finalCandidateSet = permute(resultList);
      Set<CandidateResult> finalCandidateResultSet = permute1(candidateResult);

    return finalCandidateResultSet;
  }

    private Set<CandidateResult> permute1(ArrayList<Set<CandidateResult>> candidateResult) {
        Set<CandidateResult> resultSet = new HashSet<>();
        if (candidateResult.size()==0) return resultSet;
        resultSet = candidateResult.get(0);

        for (int i = 1 ; i < candidateResult.size() ; ++i) {
            // okay look at this now.
            Set<CandidateResult> wordList = candidateResult.get(i);
            Set<CandidateResult> copyList  = new HashSet<>(resultSet);
            resultSet.clear();

            for (CandidateResult word: wordList) {
                //append this to all the resultSet items
                int distance = Integer.MIN_VALUE;
                for (CandidateResult result: copyList) {
                    distance = Math.max(result.getDistance(), distance);
                    resultSet.add(new CandidateResult(result.getCandidate() + " " + word.getCandidate(), word.getDistance() + result.getDistance()));

                }
            }
        }
        return resultSet;
  }

    private Set<CandidateResult> getCandidatesForWord(String query, int distance) {
        // Edit distance 1
        Set<CandidateResult> candidates = new HashSet<>();
        candidates.addAll(getDeleteCandidates(query,distance));
        candidates.addAll(getInsertCandidates(query, distance));
        candidates.addAll(getReplaceCandidate(query, distance));
        candidates.addAll(getTransposeCandidates(query, distance));

        // Edit distance 2
        Set<CandidateResult> candidateD2 = getCandidates(candidates, distance + 1);
        candidateD2.addAll(candidates);
        candidates.clear();
        return candidateD2;
    }




    private Set<CandidateResult> getCandidates(Set<CandidateResult> candidates, int distance) {
        Set<CandidateResult> candidatesD2 = new HashSet<>();

        for (CandidateResult candidate : candidates) {
            candidatesD2.addAll(getDeleteCandidates(candidate.getCandidate(), distance));
            candidatesD2.addAll(getInsertCandidates(candidate.getCandidate(), distance));
            candidatesD2.addAll(getReplaceCandidate(candidate.getCandidate(), distance));
            candidatesD2.addAll(getTransposeCandidates(candidate.getCandidate(), distance));
        }
        return candidatesD2;
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
