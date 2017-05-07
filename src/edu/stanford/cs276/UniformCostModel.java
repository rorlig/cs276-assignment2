package edu.stanford.cs276;

/**
 * Implement {@link EditCostModel} interface by assuming assuming
 * that any single edit in the Damerau-Levenshtein distance is equally likely,
 * i.e., having the same probability
 */
public class UniformCostModel implements EditCostModel {
	
	private static final long serialVersionUID = 1L;


    private static final double EDIT_PROB = 0.1;
    private static final double MATCH_PROB = 0.98;
	
  @Override
  public double editProbability(String original, String R, int distance) {
      if (R.equals(original)){
          return MATCH_PROB;
      }
      if (distance<=1)
          return EDIT_PROB;
      else
          return EDIT_PROB/(Math.pow(distance,2));
  }
}
