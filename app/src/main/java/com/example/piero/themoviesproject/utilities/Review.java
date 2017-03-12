package com.example.piero.themoviesproject.utilities;


public class Review {
    private static final String TAG = Review.class.getSimpleName();
    public String author;
    public String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString(){
        return author + ": " + content.substring(0, 20);
    }
}
