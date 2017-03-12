package com.example.piero.themoviesproject.utilities;


public class Trailer {
    private static final String TAG = Trailer.class.getSimpleName();
    public String key;
    public String name;

    public Trailer (String name, String key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public String toString(){
        return name;
    }
}
