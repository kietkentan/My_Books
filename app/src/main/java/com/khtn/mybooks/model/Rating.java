package com.khtn.mybooks.model;

public class Rating {
    private float score;
    private int turn;

    public Rating() {}

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void addRating(Rating rating){
        this.score = (this.score*this.turn + rating.getScore()*rating.getTurn())/(rating.getTurn() + this.turn);
        this.turn += rating.getTurn();
    }

    public void subRating(Rating rating){
        this.score = (this.score*this.turn - rating.getScore()*rating.getTurn())/(rating.getTurn() + this.turn);
        this.turn -= rating.getTurn();
    }
}