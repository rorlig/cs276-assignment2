package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.Character;
import java.util.function.BiFunction;

/**
 * Implement {@link EditCostModel} interface. Use the query corpus to learn a model
 * of errors that occur in our dataset of queries, and use this to compute P(R|Q).
 */
public class EmpiricalCostModel implements EditCostModel {
    private static final long serialVersionUID = 1L;
    private HashMap<String, HashMap> errors=new HashMap<>();
    private HashMap<String, HashMap<String,Integer>> wordMap = new HashMap<>();

    public EmpiricalCostModel(String editsFile) throws IOException {
        buildWordMap(editsFile);
        BufferedReader input = new BufferedReader(new FileReader(editsFile));
        System.out.println("Constructing edit distance map...");
        String line = null;
        HashMap<String,HashMap<String, Integer>>trans=new HashMap<>();//dictionary for transposition
        HashMap<String,HashMap<String, Integer>>sub=new HashMap<>();//dictionary for substitution
        HashMap<String,HashMap<String, Integer>>ins=new HashMap<>();//dictionary for insertion
        HashMap<String,HashMap<String, Integer>>del=new HashMap<>();//dictionary for deletion

        while ((line = input.readLine()) != null) {
            Scanner lineSc = new Scanner(line);
            lineSc.useDelimiter("\t");
            String noisy = lineSc.next();
            String clean = lineSc.next();
            String key="";
            HashMap<String, Integer>map=new HashMap();

            if (!noisy.equals(clean)){  //filtering 0 distance values
                //get the precomputed word map
                //wordMap=(HashMap<String, HashMap<String,Integer>>) getDictionaryEntry(noisy,clean,wordMap).clone();

                if (noisy.length()==clean.length()){ //either replacement or transposition
                    for (int i=0;i<noisy.length();i++){
                        if (noisy.charAt(i)!=clean.charAt(i)){
                            if (i!=noisy.length()-1){
                                if (noisy.charAt(i+1)==clean.charAt(i) && noisy.charAt(i)==clean.charAt(i+1)){
                                    //transposition; trans(x,y)=count(xy typed as yx)
                                    String xy=noisy.substring(i,i+2);
                                    String yx=clean.substring(i,i+2);
                                    if (trans.containsKey(xy)){
                                        if (trans.get(xy).containsKey(yx))
                                            map.put(yx,trans.get(xy).get(yx)+1);
                                        else
                                            map.put(yx,1);

                                        trans.put(xy,map);
                                    } else {
                                        map.put(yx,1);
                                        trans.put(xy,map);
                                    }
                                    break;

                                } else {
                                    //replacement: sub(x,y)=count(y typed as x); replacement of all but last char
                                    String x=noisy.substring(i,i+1);
                                    String y=clean.substring(i,i+1);
                                    if (sub.containsKey(x)){
                                        if (sub.get(x).containsKey(y)){
                                            map.put(y,sub.get(x).get(y)+1);
                                        } else {
                                            map.put(y,1);
                                        }
                                        sub.put(x,map);
                                    } else {
                                        map.put(y,1);
                                        sub.put(x,map);
                                    }
                                    break;
                                }
                            } else {
                                //replacement of the last char
                                String x=noisy.substring(i,i+1);
                                String y=clean.substring(i,i+1);
                                if (sub.containsKey(x)){
                                    if (sub.get(x).containsKey(y)){
                                        map.put(y,sub.get(x).get(y)+1);
                                    } else {
                                        map.put(y,1);
                                    }
                                    sub.put(x,map);
                                } else {
                                    map.put(y,1);
                                    sub.put(x,map);
                                }
                                break;
                            }
                        }
                    }



                } else if (noisy.length()<clean.length()){ //delete operation
                    //delete; del(x,y)=count[xy typed as x]
                    String x=null;
                    String xy=null;
                    for (int i=0;i<noisy.length();i++){
                        if (noisy.charAt(i)!=clean.charAt(i)){
                            if (i==0){
                                x = noisy.substring(i,i+1);
                                xy=clean.substring(i,i+2);
                            } else {
                                x = noisy.substring(i - 1, i);
                                xy = clean.substring(i - 1, i + 1);
                            }

                            break;
                        }

                    }
                    if (x==null && xy==null){
                        x=noisy.substring(noisy.length()-1);
                        xy=clean.substring(noisy.length()-1);
                    }

                    if (del.containsKey(x)){
                        if (del.get(x).containsKey(xy)){
                            map.put(xy,del.get(x).get(xy)+1);
                        } else {
                            map.put(xy,1);
                        }
                        del.put(x,map);
                    } else {
                        map.put(xy,1);
                        del.put(x,map);
                    }
                } else { // insert
                    //insert: ins(x,y)=count(x typed as xy)
                    String x=null;
                    String xy=null;
                    for (int i=0;i<clean.length();i++){
                        if (noisy.charAt(i)!=clean.charAt(i)){
                            if (i==0){
                                xy=noisy.substring(i,i+2);
                                x=clean.substring(i,i+1);
                            } else {
                                xy = noisy.substring(i - 1, i + 1);
                                x = clean.substring(i - 1, i);
                            }

                        }
                    }
                    if (x==null && xy==null){
                        x=noisy.substring(clean.length()-1);
                        xy=clean.substring(clean.length()-1);
                    }
                    if(ins.containsKey(xy)){
                        if(ins.get(xy).containsKey(x))
                            map.put(x,ins.get(xy).get(x)+1);
                        else
                            map.put(x,1);
                        ins.put(xy,map);
                    } else {
                        map.put(x,1);
                        ins.put(xy,map);
                    }
                }

            }
        }

        //setting the private variable - HashMap
        errors.put("ins",ins);
        errors.put("del",del);
        errors.put("sub",sub);
        errors.put("trans",trans);

        input.close();
        System.out.println("Done.");
    }

    // You need to add code for this interface method to calculate the proper empirical cost.
    @Override
    public double editProbability(String original, String R, int distance) {
        String typeDiff = getTypeAndDiff(original,R);
        String[] keys=typeDiff.split("\t");
        if (original.equals(R))
            return 1;
        else{
            double prob=0;
            if (distance == 1) {
                /*
                if (errors.containsKey(keys[0])) {
                    HashMap<String, HashMap> opMap = errors.get(keys[0]);
                    if (opMap.containsKey(keys[1])) {
                        HashMap<String, Integer> m = opMap.get(keys[1]);
                        int total = getTotalSumofValues(m);
                        if (m.containsKey(keys[2])) {
                            prob= (double) (1 - (m.get(keys[2]) + 1) / (total + m.size()));
                        }
                    }
                }
                */
                String[] origTokens=original.split("\\s+");
                String[] Rtokens=R.split("\\s+");
                if (origTokens.length==Rtokens.length){
                    for(int i=0;i<origTokens.length;i++){
                        if (!origTokens[i].equals(Rtokens[i])){
                            if (wordMap.containsKey(origTokens[i])){
                                int total=getTotalSumofValues(wordMap.get(origTokens[i]));
                                HashMap<String, Integer> dict=wordMap.get(origTokens[i]);
                                if (dict.containsKey(Rtokens[i])){
                                    prob=(dict.get(Rtokens[i])/total);
                                }
                            }
                        }
                    }
                }
                return prob;
            }
        }

        return 0.2;
    }

    public HashMap getErrors(){return errors;}

    private String getTypeAndDiff(String original, String R){
        //System.out.println(R);
        String type="";
        String diff="";
        if (original.length()==R.length()){
            for(int i=0;i<R.length();i++){
                if (i<R.length()-1) {
                    if (R.charAt(i) != original.charAt(i)) {
                        if (R.charAt(i) == original.charAt(i + 1) &&
                                R.charAt(i+1)==original.charAt(i)){
                            type="trans";
                            diff=R.substring(i,i+2)+"\t"+original.substring(i,i+2);
                            break;
                        } else{
                            type="sub";
                            diff=R.substring(i,i+1)+"\t"+original.substring(i,i+1);
                            break;
                        }
                    }
                }else{
                    type="sub";
                    diff=R.substring(i,i+1)+"\t"+original.substring(i,i+1);
                    break;
                }
            }

        }else if (original.length()<R.length()){
            type="ins";
            for(int i=0;i<original.length();i++){
                if(original.charAt(i)!=R.charAt(i)){
                    if(i==0){
                        diff = R.substring(i, i + 2) + "\t" + original.substring(i, i+1);
                    }else {
                        diff = R.substring(i - 1, i + 1) + "\t" + original.substring(i - 1, i);
                    }
                    break;
                }

            }
            if (diff.length()==0)
                diff=R.substring(original.length()-1)+"\t"+original.substring(original.length()-1);

        }else{
            type="del";
            for(int i=0;i<R.length();i++){
                if(original.charAt(i)!=R.charAt(i)){
                    if(i==0){
                        diff = R.substring(i, i + 1) + "\t" + original.substring(i, i+2);
                    }else {
                        diff=R.substring(i-1,i)+"\t"+original.substring(i-1,i+1);
                    }

                    break;
                }

            }
            if (diff.length()==0)
                diff=R.substring(R.length()-1)+"\t"+original.substring(R.length()-1);
        }
        return type+"\t"+diff;
    }

    private void buildWordMap(String editsFile) throws IOException{
        BufferedReader input = new BufferedReader(new FileReader(editsFile));
        String line;
        while ((line = input.readLine()) != null) {
            Scanner lineSc = new Scanner(line);
            lineSc.useDelimiter("\t");
            String noisy = lineSc.next();
            String clean = lineSc.next();
            String[] noisyTokens=noisy.split("\\s+");
            String[] cleanTokens=clean.split("\\s+");
            HashMap<String, Integer> dict=new HashMap<String, Integer>();
            if (noisyTokens.length!=noisyTokens.length){
                if (noisyTokens.length<cleanTokens.length){
                    for (int i=0;i<noisyTokens.length;i++){
                        if (noisyTokens[i].equalsIgnoreCase(cleanTokens[i]+cleanTokens[i+1])){
                            if (wordMap.containsKey(noisyTokens[i])){
                                dict=wordMap.get(noisyTokens[i]);
                                dict.put(cleanTokens[i]+cleanTokens[i+1],dict.get(cleanTokens[i]+cleanTokens[i+1])+1);
                                wordMap.put(noisyTokens[i],dict);
                                break;
                            } else {
                                dict.put(cleanTokens[i]+cleanTokens[i+1],1);
                                wordMap.put(noisyTokens[i],dict);
                                break;
                            }
                        }
                    }
                } else {
                    for (int i=0;i<cleanTokens.length;i++){
                        if (cleanTokens[i].equalsIgnoreCase(noisyTokens[i]+noisyTokens[i+1])){
                            if (wordMap.containsKey(noisyTokens[i]+noisyTokens[i+1])){
                                dict=wordMap.get(noisyTokens[i]+noisyTokens[i+1]);
                                dict.put(cleanTokens[i],dict.get(cleanTokens[i]+1));
                            } else {
                                dict.put(cleanTokens[i],1);
                                wordMap.put(noisyTokens[i]+noisyTokens[i+1],dict);
                                break;
                            }
                        }
                    }
                }
            } else {
                for(int i=0;i<noisyTokens.length;i++){
                    if (!noisyTokens[i].equalsIgnoreCase(cleanTokens[i])){
                        if (wordMap.containsKey(noisyTokens[i])){
                            dict=wordMap.get(noisyTokens[i]);
                            if (dict.containsKey(cleanTokens[i])){
                                dict.put(cleanTokens[i],dict.get(cleanTokens[i])+1);
                            } else {
                                dict.put(cleanTokens[i],1);
                            }
                            wordMap.put(noisyTokens[i],dict);
                        } else {
                            dict.put(cleanTokens[i],1);
                            wordMap.put(noisyTokens[i],dict);
                        }
                        break;
                    }
                }
            }
        }
        input.close();
    }

    private HashMap<String, HashMap<String, Integer>> getDictionaryEntry(String noisy, String clean, HashMap map){
        HashMap<String, HashMap<String, Integer>> hmap= (HashMap<String, HashMap<String, Integer>>) map.clone();
        String[] noisyTokens=noisy.split("\\s+");
        String[] cleanTokens=clean.split("\\s+");

        if (noisyTokens.length!=cleanTokens.length){
            if (noisyTokens.length<cleanTokens.length){
                int ctr=0;
                HashMap<String, Integer> dict=new HashMap<>();
                while(ctr<noisyTokens.length){
                    if (noisyTokens[ctr].equals(cleanTokens[ctr]+cleanTokens[ctr+1])){
                        if (hmap.containsKey(noisyTokens[ctr])){
                            dict=hmap.get(noisyTokens[ctr]);
                            if (dict.containsKey(cleanTokens[ctr]+cleanTokens[ctr+1])){
                                dict.put(cleanTokens[ctr]+cleanTokens[ctr+1],
                                        dict.get(cleanTokens[ctr]+cleanTokens[ctr+1])+1);
                            } else{
                                dict.put(cleanTokens[ctr]+cleanTokens[ctr+1],1);
                            }
                        } else {
                            dict.put(cleanTokens[ctr]+cleanTokens[ctr+1],1);
                        }
                        hmap.put(noisyTokens[ctr],dict);
                        break;
                    }
                    ctr++;

                }
            } else {
                int ctr=0;
                HashMap<String, Integer> dict=new HashMap<>();
                while(ctr<cleanTokens.length) {
                    if (cleanTokens[ctr].equals(noisyTokens[ctr] + noisyTokens[ctr + 1])) {
                        if (hmap.containsKey(noisyTokens[ctr] + noisyTokens[ctr + 1])) {
                            dict = hmap.get(noisyTokens[ctr] + noisyTokens[ctr + 1]);
                            if (dict.containsKey(cleanTokens[ctr])) {
                                dict.put(cleanTokens[ctr],
                                        dict.get(cleanTokens[ctr]) + 1);
                            } else {
                                dict.put(cleanTokens[ctr], 1);
                            }
                        } else {
                            dict.put(cleanTokens[ctr], 1);
                        }
                        hmap.put(noisyTokens[ctr] + noisyTokens[ctr + 1], dict);
                        break;
                    }
                    ctr++;
                }
            }

        }else{
            HashMap<String, Integer> dict=new HashMap<>();
            for(int i=0;i<noisyTokens.length;i++){
                if (!noisyTokens[i].equals(cleanTokens[i])){
                    if (hmap.containsKey(noisyTokens[i])){
                        dict=hmap.get(noisyTokens[i]);
                        if (dict.containsKey(cleanTokens[i])){
                            dict.put(cleanTokens[i],dict.get(cleanTokens[i])+1);
                        } else {
                            dict.put(cleanTokens[i],1);
                        }
                        hmap.put(noisyTokens[i],dict);
                        break;
                    } else {
                        dict.put(cleanTokens[i],1);
                        hmap.put(noisyTokens[i],dict);
                    }
                }
            }
        }
        return hmap;
    }
    private int getTotalSumofValues(HashMap<String, Integer> map){
        int total=0;
        for(int i:map.values())
            total+=i;
        return total;
    }

}
