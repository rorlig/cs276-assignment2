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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CandidateResult)) return false;

        CandidateResult that = (CandidateResult) o;

        return candidate != null ? candidate.equals(that.candidate) : that.candidate == null;
    }

    @Override
    public int hashCode() {
        return candidate != null ? candidate.hashCode() : 0;
    }
}
