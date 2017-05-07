package edu.stanford.cs276;


import java.beans.ConstructorProperties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.Character;

/**
 * Implement {@link EditCostModel} interface. Use the query corpus to learn a model
 * of errors that occur in our dataset of queries, and use this to compute P(R|Q).
 */
public class EmpiricalCostModel implements EditCostModel {
    private static final long serialVersionUID = 1L;
    HashMap<String,Integer> hmap=new HashMap<String,Integer>();

    @Override
    public double editProbability(String original, String R, int distance) {
        original="$"+original;
        R="$"+R;

        if(R.equals(original))
            return 0.98;
        if (distance==1){
            String change=findChange(R, original);
            String cleankey=change.split("|")[0];
            if (hmap.containsKey(change)){
                int errCount=hmap.get(change);
                int total=1;
                for (String key:hmap.keySet()){
                    if (key.split("|")[0].equals(cleankey)){
                        total+=hmap.get(key);
                        total++;
                    }
                }
                return (double)errCount/total;
            } else {
                int total=1;
                for(String key:hmap.keySet()){
                    if(key.split("|")[0].equals(cleankey)){
                        total+=hmap.get(key);
                        total++;
                    }
                }
                return 1.0/total;
            }
        }
        return 0.1/Math.pow(distance,2);
    }

    public EmpiricalCostModel(String editsFile) throws IOException{
        BufferedReader input = new BufferedReader(new FileReader(editsFile));
        System.out.println("Constructing edit distance map...");
        String line=null;
        while((line=input.readLine())!=null){
            Scanner lineSc = new Scanner(line);
            lineSc.useDelimiter("\t");
            String noisy = "$"+lineSc.next();
            String clean = "$"+lineSc.next();
            String change=findChange(clean,noisy);
            if (change!=null){
                if (hmap.containsKey(change)){
                    hmap.put(change,hmap.get(change)+1);
                }else{
                    hmap.put(change,1);
                }
            }
        }
        input.close();

    }

    private String findChange(String clean, String noisy){
        String result=null;
        if (noisy.equals(clean)) return result;
        if (noisy.length()==clean.length()){
            for(int i=0;i<noisy.length()-1;i++){
                if (noisy.charAt(i)!=clean.charAt(i)){
                    if (noisy.charAt(i)==clean.charAt(i+1) &&
                            noisy.charAt(i+1)==clean.charAt(i)){
                        result=clean.substring(i-1,i+1)+"|"+noisy.substring(i-1,i+1);
                        return result;
                    }
                }
            }
            result=clean.substring(noisy.length()-1)+"|"+noisy.substring(noisy.length()-1);
            return result;
        } else {
            if (noisy.length()<clean.length()){
                for(int i=0;i<noisy.length();i++){
                    if(noisy.charAt(i)!=clean.charAt(i)){
                        result=clean.substring(i-1,i+1)+"|"+noisy.substring(i-1,i);
                        return result;
                    }
                }
                result=clean.substring(noisy.length()-1)+"|"+noisy.substring(noisy.length()-1);
                return result;
            } else {
                for(int i=0;i<clean.length();i++){
                    if(clean.charAt(i)!=noisy.charAt(i)){
                        result=clean.substring(i-1,i)+"|"+noisy.substring(i-1,i+1);
                        return result;
                    }
                }
                result=clean.substring(clean.length()-1)+"|"+noisy.substring(clean.length()-1);
                return result;
            }
        }
        //return result;
    }

}
