package edu.stanford.cs276;

/**
 * Implement {@link EditCostModel} interface by assuming assuming
 * that any single edit in the Damerau-Levenshtein distance is equally likely,
 * i.e., having the same probability
 */
public class UniformCostModel implements EditCostModel {
	
	private static final long serialVersionUID = 1L;
	
  @Override
  public double editProbability(String original, String R, int distance) {
      //return 1.0/(1+Math.pow(distance,3));

      if (distance == 0) {
          return 0.99;
      } else  if (distance==1){
          return 0.9;
      } else
          return 0.99/(Math.pow(distance,2));

    // TODO: Your code here
  }
}
