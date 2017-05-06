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

      if (R.equals(original)){
          return 0.98;
      }
      if (distance<=1)
          return 0.1;
      else
          return 0.1/(Math.pow(distance,2));
  }
}
