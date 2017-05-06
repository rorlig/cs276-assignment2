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
	
  public EmpiricalCostModel(String editsFile) throws IOException {
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
            for (int i=0;i<noisy.length();i++){
                if (noisy.charAt(i)!=clean.charAt(i)){
                    String x;
                    String xy;
                    if (i==0){
                        x = noisy.substring(i,i+1);
                        xy=clean.substring(i,i+2);
                    } else {
                        x = noisy.substring(i - 1, i);
                        xy = clean.substring(i - 1, i + 1);
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
                    break;
                }
            }
        } else { // insert
            //insert: ins(x,y)=count(x typed as xy)
            for (int i=0;i<clean.length();i++){
                if (noisy.charAt(i)!=clean.charAt(i)){
                    String x;
                    String xy;
                    if (i==0){
                        xy=noisy.substring(i,i+2);
                        x=clean.substring(i,i+1);
                    } else {
                        xy = noisy.substring(i - 1, i + 1);
                        x = clean.substring(i - 1, i);
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
    if (distance<=1) {
        if (errors.containsKey(keys[0])) {
            HashMap<String, HashMap> opMap = errors.get(keys[0]);
            if (opMap.containsKey(keys[1])) {
                HashMap<String, Integer> m = opMap.get(keys[1]);
                int total = getTotalSumofValues(m);
                if (m.containsKey(keys[2])) {
                    return (float) (1-m.get(keys[2]) / total);
                }
            }
        }
    }

    return 0.1/(Math.pow(distance,2));
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

  private int getTotalSumofValues(HashMap<String, Integer> map){
      int total=0;
      for(int i:map.values())
          total+=i;
      return total;
  }

}
