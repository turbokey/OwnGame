package com.nesterov.owngame;

public class ScoreItem {
    private int score;
    private String name;
    private boolean itsme;

    public ScoreItem(int score, String name, boolean itsme) {
        this.score = score;
        this.name = name;
        this.itsme = itsme;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isItsme() {
        return itsme;
    }
}
