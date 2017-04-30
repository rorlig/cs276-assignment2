package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guptaga on 4/29/17.
 */
public class TestEdit {

    private static LanguageModel languageModel;
    private static NoisyChannelModel nsm;

    public static void main(String args[]) {
        try {
            CandidateGenerator cg = CandidateGenerator.get();
            languageModel = LanguageModel.load();
            nsm = NoisyChannelModel.load();
//            BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File("data/queries.txt")));
            nsm.setProbabilityType("uniform");
            cg.setLanguageModel(languageModel);
            Set<CandidateResult> candidateSet = cg.getCandidates("304669 101719 4063882026 75360");
            System.out.println("assert size "  + candidateSet.size());
            for (CandidateResult candidate : candidateSet){
                System.out.println(candidate);
            }
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
