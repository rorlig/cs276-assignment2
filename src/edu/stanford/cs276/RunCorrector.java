package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


//public class RunCorrector {
//
//  public static LanguageModel languageModel;
//  public static NoisyChannelModel nsm;
//
//  public static void main(String[] args) throws Exception {
//
//    // Parse input arguments
//    String uniformOrEmpirical = null;
//    String queryFilePath = null;
//    String goldFilePath = null;
//    String extra = null;
//    BufferedReader goldFileReader = null;
//
//    if (args.length == 2) {
//      // Default: run without extra credit code or gold data comparison
//      uniformOrEmpirical = args[0];
//      queryFilePath = args[1];
//    }
//    else if (args.length == 3) {
//      uniformOrEmpirical = args[0];
//      queryFilePath = args[1];
//      if (args[2].equals("extra")) {
//        extra = args[2];
//      } else {
//        goldFilePath = args[2];
//      }
//    }
//    else if (args.length == 4) {
//      uniformOrEmpirical = args[0];
//      queryFilePath = args[1];
//      extra = args[2];
//      goldFilePath = args[3];
//    }
//    else {
//      System.err.println(
//          "Invalid arguments.  Argument count must be 2, 3 or 4 \n"
//          + "./runcorrector <uniform | empirical> <query file> \n"
//          + "./runcorrector <uniform | empirical> <query file> <gold file> \n"
//          + "./runcorrector <uniform | empirical> <query file> <extra> \n"
//          + "./runcorrector <uniform | empirical> <query file> <extra> <gold file> \n"
//          + "SAMPLE: ./runcorrector empirical data/queries.txt \n"
//          + "SAMPLE: ./runcorrector empirical data/queries.txt data/gold.txt \n"
//          + "SAMPLE: ./runcorrector empirical data/queries.txt extra \n"
//          + "SAMPLE: ./runcorrector empirical data/queries.txt extra data/gold.txt \n");
//      return;
//    }
//
//    if (goldFilePath != null) {
//      goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
//    }
//
//    // Load models from disk
//    languageModel = LanguageModel.load();
//    nsm = NoisyChannelModel.load();
//    BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
//    nsm.setProbabilityType(uniformOrEmpirical);
//
//
//    CandidateGenerator candidateGenerator = CandidateGenerator.get();
//
//    candidateGenerator.setLanguageModel(languageModel);
//
//
//    String query = null;
//
//    /*
//     * Each line in the file represents one query. We loop over each query and find
//     * the most likely correction
//     */
//    while ((query = queriesFileReader.readLine()) != null) {
//
//      String correctedQuery = query;
//
//      Set<CandidateResult> candidateSet  = candidateGenerator.getCandidates(query);
//
//      System.out.println("candidateSet size " + candidateSet.size());
//      System.out.println("query: "  + query);
//      for (CandidateResult candidate: candidateSet) {
//        System.out.println("candidate: " + candidate);
//      }
//
//        if (candidateSet.size()!=0) {
////            getScores(candidateSet, query);
//        }
////      System.out.println(candidateSet);
//      /*
//       * Your code here: currently the correctQuery and original query are the same
//       * Complete this implementation so that the spell corrector corrects the
//       * (possibly) misspelled query
//       *
//       */
//
//      if ("extra".equals(extra)) {
//        /*
//         * If you are going to implement something regarding to running the corrector,
//         * you can add code here. Feel free to move this code block to wherever
//         * you think is appropriate. But make sure if you add "extra" parameter,
//         * it will run code for your extra credit and it will run you basic
//         * implementations without the "extra" parameter.
//         */
//      }
//
//      // If a gold file was provided, compare our correction to the gold correction
//      // and output the running accuracy
//      if (goldFileReader != null) {
//        String goldQuery = goldFileReader.readLine();
//        /*
//         * You can do any bookkeeping you wish here - track accuracy, track where your solution
//         * diverges from the gold file, what type of errors are more common etc. This might
//         * help you improve your candidate generation/scoring steps
//         */
//      }
//
//      /*
//       * Output the corrected query.
//       * IMPORTANT: In your final submission DO NOT add any additional print statements as
//       * this will interfere with the autograder
//       */
//      System.out.println(correctedQuery);
//    }
//    queriesFileReader.close();
//  }
//
//    private static void getScores(Set<String> candidateSet, String query) {
//
//    }
//
////    private static ArrayList<Score> populateScores(ArrayList<StringBuilder> ar,
////                                                   String query,
////                                                   ArrayList<Set<String>> distanceList){
////        ArrayList<Score> scoredList = new ArrayList<Score>(ar.size());
////        for(int i = 0; i < ar.size(); i++){
////            String str = ar.get(i).toString();
////            scoredList.add(new Score(query,str,ms,nsm.ecm_,distanceList));
////        }
////        return scoredList;
////    }
//
//}


public class RunCorrector {

    public static LanguageModel languageModel;
    public static NoisyChannelModel nsm;
    private static double lambda = 0.05;
    private static double mu=0.05;



    public static void main(String[] args) throws Exception {


        // Parse input arguments
        String uniformOrEmpirical = null;
        String queryFilePath = null;
        String goldFilePath = null;
        String extra = null;
        BufferedReader goldFileReader = null;

        if (args.length == 2) {
            // Default: run without extra credit code or gold data comparison
            uniformOrEmpirical = args[0];
            queryFilePath = args[1];
        }
        else if (args.length == 3) {
            uniformOrEmpirical = args[0];
            queryFilePath = args[1];
            if (args[2].equals("extra")) {
                extra = args[2];
            } else {
                goldFilePath = args[2];
            }
        }
        else if (args.length == 4) {
            uniformOrEmpirical = args[0];
            queryFilePath = args[1];
            extra = args[2];
            goldFilePath = args[3];
        }
        else {
            System.err.println(
                    "Invalid arguments.  Argument count must be 2, 3 or 4 \n"
                            + "./runcorrector <uniform | empirical> <query file> \n"
                            + "./runcorrector <uniform | empirical> <query file> <gold file> \n"
                            + "./runcorrector <uniform | empirical> <query file> <extra> \n"
                            + "./runcorrector <uniform | empirical> <query file> <extra> <gold file> \n"
                            + "SAMPLE: ./runcorrector empirical data/queries.txt \n"
                            + "SAMPLE: ./runcorrector empirical data/queries.txt data/gold.txt \n"
                            + "SAMPLE: ./runcorrector empirical data/queries.txt extra \n"
                            + "SAMPLE: ./runcorrector empirical data/queries.txt extra data/gold.txt \n");
            return;
        }

        // Load models from disk
        languageModel = LanguageModel.load();
        nsm = NoisyChannelModel.load();
        nsm.setProbabilityType(uniformOrEmpirical);

        String query = null;

        System.out.println("loaded the model");

    /*
     * Each line in the file represents one query. We loop over each query and find
     * the most likely correction
     */

        CandidateGenerator2 cg=CandidateGenerator2.get(RunCorrector.languageModel, RunCorrector.nsm);
        cg.setLanguageModel(languageModel);


        for (int i=1;i<10;i++) {
            lambda = i*0.05;
//            lambda=0.05;
            for (int j=2;j<5; j++) {
                mu=1.0/j;
//                mu=0.25;

                long startTime = System.currentTimeMillis();

                BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
                if (goldFilePath != null) {
                    goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
                }

                String result;
                double prob;
                int correct = 0;
                int total = 0;

                while ((query = queriesFileReader.readLine()) != null) {
//                    query = "vcimage generate on";
//                    query=query.trim();
                    result = "";

                    prob = -100000000D;
//                    System.out.println("query term " + query);

                    Map<String, Integer> candidateSet = cg.getCandidates(query);

                    System.out.println("candidate generation  "
                            +  ((System.currentTimeMillis()-startTime)/1000) + "seconds" + "size " + candidateSet.size());


                    for (String candidate : candidateSet.keySet()) {
                        double p = calculateProbability(query, candidate, candidateSet.get(candidate));
//                        System.out.println("candidate " + candidate
//                                            + " probability " + p + " distance " + candidateSet.get(candidate));

                        if (p > prob) {
                            prob = p;
                            result = candidate;
                        }
                    }

                    /*
                    HashMap<String, Integer> candQueries=cg.getCandidates(query);
                    for(String qry:candQueries.keySet()){
                        double p = calculateProbability(query,qry,candQueries.get(qry));
                        if (p>prob){
                            prob=p;
                            result=qry;
                        }
                    }
                    */

                    if ("extra".equals(extra)) {
        /*
         * If you are going to implement something regarding to running the corrector,
         * you can add code here. Feel free to move this code block to wherever
         * you think is appropriate. But make sure if you add "extra" parameter,
         * it will run code for your extra credit and it will run you basic
         * implementations without the "extra" parameter.
         */
                    }

                    // If a gold file was provided, compare our correction to the gold correction
                    // and output the running accuracy
                    if (goldFileReader != null) {
                        String goldQuery = goldFileReader.readLine();
                        total++;
                        if (goldQuery.trim().equals(result)) {
                            correct++;
//                            System.out.println("************"+result + " correct " + correct + " total " + total);
                        } else {
//                            System.out.println(query + ":" + result
//                                    + ":" + goldQuery);


                        }
                        //else
                          //  System.out.println(query+"-"+result+":"+goldQuery+":"+getDistance(goldQuery,query));
        /*
         * You can do any bookkeeping you wish here - track accuracy, track where your solution
         * diverges from the gold file, what type of errors are more common etc. This might
         * help you improve your candidate generation/scoring steps
         */
                    }
                    /*String freq="";
                    for (String str:result.split("\\s+"))
                        freq=freq+languageModel.unigram.getMap().get(str)+",";
                    System.out.println(result+"---"+freq);*/
                }
                System.out.println("result " + lambda +","+mu+"," + correct);

                System.out.println("total time " +  ((System.currentTimeMillis()-startTime)/1000) + "seconds");

//                System.out.println(correct);
                queriesFileReader.close();
            }
        }
    }

    //    private static double calculateProbability(String origQuery, String corrQuery, int dist){
    //        double prob=0D;
    //        //get the edit probability (conditional probability of P(R/Q) where R is orig
    //        //int distance = getDistance(origQuery, corrQuery);
    //        prob=Math.log(nsm.ecm_.editProbability(origQuery,corrQuery,dist));
    //        //calculate the probability of P(Q) using the lang model
    //        prob+=langModelProb(corrQuery);
    //        return prob;
    //    }

    private static double langModelProb(String corrQuery){
        String[] tokens=corrQuery.split("\\s+");
        double prob=0;
        for(int i=0;i<tokens.length;i++){
            if (i==0)
                prob+=mu*Math.log(
                        (double)languageModel.unigram.count(tokens[i])/languageModel.unigram.termCount());
            else{
                double temp=0D;
                temp+=(lambda * (double)languageModel.unigram.count(tokens[i])/languageModel.unigram.termCount());
                temp+=((1-lambda)*(double) languageModel.bigram.count(tokens[i-1]+"|"+tokens[i])/
                        (languageModel.unigram.count(tokens[i-1])));
                prob+=mu*Math.log(temp);
            }

        }
        return prob;
    }

    private static double langModelProbLaplaceSmoothing(String corrQuery){
        String[] tokens=corrQuery.split("\\s+");
        double prob=0;
        for(int i=0;i<tokens.length;i++){
            if (i==0){
                prob+=Math.log(((double)languageModel.unigram.count(tokens[i])+1) /
                        (languageModel.unigram.termCount()+languageModel.unigram.getMap().size()));
            } else {
                prob+=Math.log(((double)languageModel.bigram.count(tokens[i-1]+"|"+tokens[i])+1) *  languageModel.unigram.count(tokens[i-1]) /
                        (languageModel.unigram.count(tokens[i-1])+languageModel.unigram.getMap().size()));
            }
        }
        return prob*mu;
    }

    private static double calculateProbability(String origQuery, String corrQuery, int dist){
        double prob=0D;
        //get the edit probability (conditional probability of P(R/Q) where R is orig
        //int distance = getDistance(origQuery, corrQuery);
        prob=Math.log(nsm.ecm_.editProbability(origQuery,corrQuery,dist));

//        System.out.println("prob from the the nsm " + prob);
        //calculate langModelProbLaplaceSmoothing probability of P(Q) using the lang model
        prob+=langModelProb(corrQuery);

//        System.out.println("after lang model" + prob);


        return prob;
    }


//    private static double langModelProb(String corrQuery){
//        String[] tokens=corrQuery.split("\\s+");
////        double prob=0;
//        double prob = unigramProbability(tokens[0]); // P(w2)
////        double bigramProbability =  (double) _bigramCounts.count(w1 + " " + w2) / _unigramCounts.count(w1); 			   // P(w2|w1)
//
////        return Math.log(LAMBDA * unigramProbability + (1 - LAMBDA) * bigramProbability);
//
//        for(int i=0;i<tokens.length -1;i++){
//                prob += bigramProbability(tokens[i], tokens[i+1], lambda);
//
////                temp+=(lambda * (double)languageModel.unigram.count(tokens[i])/languageModel.unigram.termCount());
////                temp+=((1-lambda)*(double) languageModel.bigram.count(tokens[i-1]+"|"+tokens[i])/
////                        (languageModel.unigram.count(tokens[i-1])));
////                prob+=mu*Math.log(temp);
//
//
//        }
//        return prob;
//    }

    private static double bigramProbability(String w1, String w2, double lambda) {
        double unigramProbability = unigramProbability(w1); // P(w2)
        double bigramProbability =  (double) languageModel.bigram.count(w1 + " " + w2)
                                            / languageModel.unigram.count(w1); 			   // P(w2|w1)

        return Math.log(lambda * unigramProbability + (1 - lambda) * bigramProbability);
    }

    private static double unigramProbability(String w) {
        return (double) languageModel.unigram.count(w) / languageModel.unigram.termCount();

    }

//    private static double bigramProbablity(String w1, String w2) {
//
//    }
    //Damerau-Levenshtein edit distance at a token level
    private static int getDistance(String t1, String t2){
        int[][] matrix = new int[t1.length()+1][t2.length()+1];
        for (int i=1;i<=t1.length();i++)
            matrix[i][0]=i;

        for (int j=1;j<=t2.length();j++)
            matrix[0][j]=j;

        for (int i=0;i<t1.length();i++) {
            for (int j = 0; j < t2.length(); j++) {
                //substituion, deletion, insertion
                matrix[i + 1][j + 1] = Math.min(matrix[i][j] + (t1.charAt(i) == t2.charAt(j) ? 0 : 1),
                        Math.min(matrix[i][j + 1] + 1, matrix[i + 1][j] + 1));
                //transposition
                if (i>0 && j>0 && t1.charAt(i)==t2.charAt(j-1) && t1.charAt(i-1)==t2.charAt(j)){
                    matrix[i+1][j+1]=Math.min(matrix[i+1][j+1], matrix[i-1][j-1]+1);
                }
            }
        }
        return matrix[t1.length()][t2.length()];
    }
}
