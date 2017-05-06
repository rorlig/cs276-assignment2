package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by guptaga on 4/29/17.
 */
public class TestEdit {

    private static LanguageModel languageModel;
    private static NoisyChannelModel nsm;

    public static void main(String args[]) {
        /*
        try{
            BufferedReader editsFileReader = new BufferedReader(new FileReader(new File("data/training_set/edit1s.txt")));
            String line;
            HashMap<Integer,Integer> hmap=new HashMap<>();
            while((line=editsFileReader.readLine())!=null){
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter("\t");
                String noisy=scanner.next();
                String clean=scanner.next();
                int distance=getDistance(noisy,clean);
                if (hmap.containsKey(distance)){
                    hmap.put(distance,hmap.get(distance)+1);
                } else {
                    hmap.put(distance,1);
                }
            }
            for (int key:hmap.keySet()){
                System.out.println(key+":"+hmap.get(key));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        */
        String x="kam"+"\t"+" "+"\t"+"chinta";
        System.out.println(x.split("\t").length);

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

    private static void test(){
        try {
<<<<<<< HEAD
//            CandidateGenerator cg = CandidateGenerator.get();
            languageModel = LanguageModel.load();
            nsm = NoisyChannelModel.load();
//            BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File("data/queries.txt")));
            nsm.setProbabilityType("uniform");
//            cg.setLanguageModel(languageModel);
//            Set<CandidateResult> candidateSet = cg.getCandidates("hp support website api doxygen documantation p04 06q");
//            System.out.println("assert size "  + candidateSet.size());
//            for (CandidateResult candidate : candidateSet){
//                System.out.println(candidate);
//            }

            String cand = "de eloped the notion of a";
            String tokens[] = cand.split(" ");
            for (String tok: tokens) {
               System.out.println("tok " + tok + " count " + languageModel.unigram.count(tok));
//                System.out.println("tok "  + tok);
=======

            BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File("data/dev_set/queries.txt")));
            BufferedReader goldFileReader = new BufferedReader(new FileReader(new File("data/dev_set/gold.txt")));

            CandidateGenerator cg = CandidateGenerator.get();
            languageModel = LanguageModel.load();
            cg.setLanguageModel(languageModel);

            String query;
            int correct=0;
            HashMap<Integer,Integer> freq = new HashMap<Integer, Integer>();
            while((query=queriesFileReader.readLine())!=null) {

                String result="";
                for (String token : query.split("\\s+")) {
                    Set<String> candidates = cg.getDeleteCandidates(token);
                    candidates.addAll(cg.getInsertCandidates(token));
                    candidates.addAll(cg.getReplaceCandidate(token));
                    candidates.addAll(cg.getTransposeCandidates(token));
                    int maxFreq = Integer.MIN_VALUE;
                    String val = "";
                    for (String alt : candidates) {
                        if (languageModel.unigram.count(alt) > maxFreq) {
                            maxFreq = languageModel.unigram.count(alt);
                            val = alt;
                        }
                    }
                    //System.out.println(token + "-->" + val);
                    result=result+" "+val;
                    result=result.trim();

                }

                String goldQuery=goldFileReader.readLine();

                int distance = getDistance(query,goldQuery);
                if (freq.containsKey(distance))
                    freq.put(distance,freq.get(distance)+1);
                else
                    freq.put(distance,1);

                if (distance==1)
                    System.out.println(query+"--->"+goldQuery);



                if (goldQuery.equalsIgnoreCase(result))
                    correct++;
                System.out.println(result);
>>>>>>> 46d3a2a21f9a82b70bf7a1eab8dd6f419ea8dfba

            }
            System.out.println(correct);
            for(int key:freq.keySet())
                System.out.println(key+":"+freq.get(key));
//
//
//            //304669 101719 4063882026 75360
//
//            Set<String> wordList1 = new HashSet<>();
//            wordList1.add("hello");
//            wordList1.add("hell");
//            wordList1.add("heel");
//
//            Set<String> wordList2 = new HashSet<>();
//            wordList2.add("world");
//            wordList2.add("word");
//            wordList2.add("words");
//
//            ArrayList<Set<String>> words = new ArrayList<>();
//            words.add(wordList1);
//            words.add(wordList2);
//
//            Set<String> resultSet = cg.permute(words);
//
//            System.out.println(resultSet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
