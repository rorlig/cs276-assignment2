package edu.stanford.cs276;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CandidateGenerator implements Serializable {

	private static final long serialVersionUID = 1L;
	private static CandidateGenerator cg_;
    private LanguageModel languageModel ;
    private double threshold = 0.0000001;

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
//    if (filter(languageModel.unigram.count(candidate) == 0))
    return candidates;

  }

    // insert
    public Set<String> getInsertCandidates(String query) {
        Set<String> candidates = new HashSet<>();
        for (Character anAlphabet : alphabet) {
            for (int i = 0; i <= query.length(); ++i) {
                String cand = query.substring(0, i) + anAlphabet + query.substring(i);
                if (languageModel.unigram.count(cand) != 0)
                    candidates.add(cand);
//                candidates.add(query.substring(0, i) + anAlphabet + query.substring(i));
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

//                newWord = anAlphabet;
                if (languageModel.unigram.count(cand) != 0)
                    candidates.add(cand);
            }
        }
        return candidates;
    }


    //transpose
    public Set<String> getTransposeCandidates(String query) {
        Set<String> candidates = new HashSet<>();
        //length <= 1 ==> candidates..
        if (query.length() <=1 ) return  candidates;
        for (int i = 0 ; i < query.length() -1; ++i) {
            char[] newWord = query.toCharArray();
            int j = i + 1;
//            for (int j = i + 1; j < query.length() ; ++j) {
                char temp = newWord[i];
                newWord[i] = newWord[j];
                newWord[j] = temp;
//            }
            String cand = new String(newWord);

//                newWord = anAlphabet;
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
                CandidateResult candidateResult = new CandidateResult(cand, distance + 1);
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
                if (languageModel.unigram.count(cand) != 0) {
                    CandidateResult candidateResult = new CandidateResult(cand, distance + 1);
                    candidates.add(candidateResult);
                }
//                candidates.add(query.substring(0, i) + anAlphabet + query.substring(i));
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

//                newWord = anAlphabet;
                if (languageModel.unigram.count(cand) != 0){
                    CandidateResult candidateResult = new CandidateResult(cand, distance + 1);
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
//            for (int j = i + 1; j < query.length() ; ++j) {
            char temp = newWord[i];
            newWord[i] = newWord[j];
            newWord[j] = temp;
//            }
            String cand = new String(newWord);

//                newWord = anAlphabet;
            if (languageModel.unigram.count(cand) != 0){
                CandidateResult candidateResult = new CandidateResult(cand, distance + 1);
                candidates.add(candidateResult);
            }
        }
        return candidates;
    }

    private Set<String> filter(Set<String> candidates) {
      Set<String> filter = new HashSet<>();
      for (String candidate: candidates) {
//          System.out.println("filter checking candidate " + candidate);
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

//      System.out.println("before filter " + candidates.size());

      candidates = filter(candidates);

//      System.out.println("after filter " + candidates.size());
      // Edit distance 2
      Set<String> candidateD2 = getCandidates(candidates);

//      System.out.println("edit distance 2 before filter " + candidateD2.size());


      candidateD2.addAll(candidates);
      candidateD2 = filter(candidateD2);

//      System.out.println("edit distance 2 after filter " + candidateD2.size());


      return candidateD2;

  }
  // Generate all candidates for the target query
  public Set<CandidateResult> getCandidates(String query) throws Exception {

      System.out.println("getCandidates");

      String[] tokens = query.trim().split("\\s+");

      System.out.println("query " + query);


      ArrayList<Set<String>> resultList = new ArrayList<>();
      ArrayList<Set<CandidateResult>> candidateResult = new ArrayList<>();

      /// need to take out all tokens from the query ...
      /// how many errors allowed per query before pruning.
      for (String token: tokens) {
          // does the unigram dict contain the token.
          if (languageModel.unigram.count(token) == 0) {
            //does not contain it..
              resultList.add(getCandidatesForWord(token));
              candidateResult.add(getCandidatesForWord(token, 0));
          } else {
            //contains the word

//              double unigramProbablity = (double)languageModel.unigram.count(token)/(double)languageModel.unigram.termCount();
//              if (unigramProbablity < threshold) {
//                  resultList.add(getCandidatesForWord(token));
//              } else {
                  //just add the word it self as possible..
                  Set<String> candidateSet = new HashSet<>();
                  candidateSet.add(token);
                  Set<CandidateResult> candidateResultSet = new HashSet<>();
                  candidateResultSet.add(new CandidateResult(token,  0));
                  resultList.add(candidateSet);
                  candidateResult.add(candidateResultSet);


//              }

          }

      }
      Set<String> finalCandidateSet = permute(resultList);
      Set<CandidateResult> finalCandidateResultSet = permute1(candidateResult);
//    Set<String> candidates = new HashSet<String>();
//    candidates.addAll(getDeleteCandidates(query));
//    candidates.addAll(getInsertCandidates(query));
//    candidates.addAll(getReplaceCandidate(query));
//    candidates.addAll(getTransposeCandidates(query));
//
//    Set<String> candidateD2 = getCandidates(candidates);
//    candidateD2.addAll(candidates);
;//    candidates = filterCandidatesInDictionary(candidates);
    //1 -> delete any one character and add to the candidate set
    //2 -> insert alphabet at any location and add the new word to the candidate set
    //3 -> transpose two adjacent characters
    //4 -> replace the character with a character in the alphabet
    /*
     * Your code here
     */
    return finalCandidateResultSet;
  }

    private Set<CandidateResult> permute1(ArrayList<Set<CandidateResult>> candidateResult) {
        Set<CandidateResult> resultSet = new HashSet<>();

        if (candidateResult.size()==0) return resultSet;

        resultSet = candidateResult.get(0);

//        System.out.println(resultSet);

        for (int i = 1 ; i < candidateResult.size() ; ++i) {
            // okay look at this now.
            Set<CandidateResult> wordList = candidateResult.get(i);
            Set<CandidateResult> copyList  = new HashSet<>(resultSet);
            resultSet.clear();



//            System.out.println(wordList);
            for (CandidateResult word: wordList) {
                //append this to all the resultSet items
//                System.out.println(copyList);
                for (CandidateResult result: copyList) {
//                    System.out.println(" result " + result + " " + word);
                    resultSet.add(new CandidateResult(result.getCandidate() + " " + word.getCandidate(), result.getDistance()));

                }
            }
        }
        return resultSet;
  }

    private Set<CandidateResult> getCandidatesForWord(String query, int distance) {
        // Edit distance 1
        Set<CandidateResult> candidates = new HashSet<>();
        candidates.addAll(getDeleteCandidates(query,0));
        candidates.addAll(getInsertCandidates(query, 0));
        candidates.addAll(getReplaceCandidate(query, 0));
        candidates.addAll(getTransposeCandidates(query, 0));

//      System.out.println("before filter " + candidates.size());

//        candidates = filter(candidates);

//      System.out.println("after filter " + candidates.size());
        // Edit distance 2
        Set<CandidateResult> candidateD2 = getCandidates(candidates, 1);

//      System.out.println("edit distance 2 before filter " + candidateD2.size());


        candidateD2.addAll(candidates);
//        candidateD2 = filter(candidateD2);

//      System.out.println("edit distance 2 after filter " + candidateD2.size());


        return candidateD2;
//        return null;
    }

    // takes the array list of set of words
    // e.g. [<hello, hell>, <world,word>]  spits out [<hello world>, <hello word>, <hell world> , <hell word>
    public Set<String> permute(ArrayList<Set<String>> resultList) {
        Set<String> resultSet = new HashSet<>();

        if (resultList.size()==0) return resultSet;

        resultSet = resultList.get(0);

//        System.out.println(resultSet);

        for (int i = 1 ; i < resultList.size() ; ++i) {
            // okay look at this now.
            Set<String> wordList = resultList.get(i);
            Set<String> copyList  = new HashSet<>(resultSet);
            resultSet.clear();



//            System.out.println(wordList);
            for (String word: wordList) {
                //append this to all the resultSet items
//                System.out.println(copyList);
                for (String result: copyList) {
//                    System.out.println(" result " + result + " " + word);
                    resultSet.add(result + " " + word);

                }
            }
        }
        return resultSet;
    }

    private Set<String> getCandidates(Set<String> candidates) {
        Set<String> candidatesD2 = new HashSet<>();

        for (String candidate : candidates) {
            candidatesD2.addAll(getDeleteCandidates(candidate));
            candidatesD2.addAll(getInsertCandidates(candidate));
            candidatesD2.addAll(getReplaceCandidate(candidate));
            candidatesD2.addAll(getTransposeCandidates(candidate));
        }
        return candidatesD2;
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

//    private Set<String> filterCandidatesInDictionary(Set<String> candidates) {
//
//    }

}
