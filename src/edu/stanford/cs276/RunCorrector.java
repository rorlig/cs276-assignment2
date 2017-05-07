package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;





public class RunCorrector {

    public static LanguageModel languageModel;
    public static NoisyChannelModel nsm;
    private static double lambda = 0.05;
    private static double mu=0.33;



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

        CandidateGenerator cg= CandidateGenerator.get(RunCorrector.languageModel, RunCorrector.nsm);
        cg.setLanguageModel(languageModel);


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
            result = "";

            prob = -100000000D;
            Map<String, Integer> candidateSet = cg.getCandidates(query);
//                    System.out.println("candidate generation  "
//                            +  ((System.currentTimeMillis()-startTime)/1000) + "seconds" + "size " + candidateSet.size());


            for (String candidate : candidateSet.keySet()) {
                double p = calculateProbability(query, candidate, candidateSet.get(candidate));
                if (p > prob) {
                    prob = p;
                    result = candidate;
                }
            }

            if ("extra".equals(extra)) {

            }
            // If a gold file was provided, compare our correction to the gold correction
            // and output the running accuracy
            if (goldFileReader != null) {
                String goldQuery = goldFileReader.readLine();
                total++;
                if (goldQuery.trim().equals(result)) {
                    correct++;
                }
            }
            System.out.println(result);
        }

//              System.out.println("total time " +  ((System.currentTimeMillis()-startTime)/1000) + "seconds");
        queriesFileReader.close();

    }

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
/*
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
*/
    private static double calculateProbability(String origQuery, String corrQuery, int dist){
        double prob=0D;
        //get the edit probability (conditional probability of P(R/Q) where R is orig
        prob=Math.log(nsm.ecm_.editProbability(origQuery,corrQuery,dist));
        prob+=langModelProb(corrQuery);
        return prob;
    }

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
