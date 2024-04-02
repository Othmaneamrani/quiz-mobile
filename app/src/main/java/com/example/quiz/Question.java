package com.example.quiz;

import com.google.firebase.firestore.DocumentId;

public class Question {

    @DocumentId
    String id;

    public Question (){}

    public Question(String id, String question, String rep, String rep1, String rep2, String image, int score) {
        this.id = id;
        this.question = question;
        this.rep = rep;
        this.rep1 = rep1;
        this.rep2 = rep2;
        this.image = image;
        this.score = score;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRep() {
        return rep;
    }

    public void setRep(String rep) {
        this.rep = rep;
    }

    public String getRep1() {
        return rep1;
    }

    public void setRep1(String rep1) {
        this.rep1 = rep1;
    }

    public String getRep2() {
        return rep2;
    }

    public void setRep2(String rep2) {
        this.rep2 = rep2;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private String question;
    private String rep,rep1,rep2,image;
    private int score=0;

}
