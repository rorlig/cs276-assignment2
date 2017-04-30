package edu.stanford.cs276;

/**
 * Created by rorlig on 4/30/17.
 */
public class CandidateResult {
    private String candidate;
    private int distance;


    public CandidateResult(String candidate, int distance) {
        this.candidate = candidate;
        this.distance = distance;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "CandidateResult{" +
                "candidate='" + candidate + '\'' +
                ", distance=" + distance +
                '}';
    }
}
