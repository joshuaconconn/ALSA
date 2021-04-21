package com.example.thesis.Models;

public class DailyExerciseModel {
    public String part, name, classId, id, set, reps, weight, url, date, alternative, alternativeURL;

    public DailyExerciseModel(){

    }

    public DailyExerciseModel(String part, String name, String classId, String id
            ,String set, String reps, String weight, String url, String date, String alternative, String alternativeURL){
        this.part = part;
        this.name = name;
        this.classId = classId;
        this.id = id;
        this.set = set;
        this.reps = reps;
        this.weight = weight;
        this.url = url;
        this.date = date;
        this.alternative = alternative;
        this.alternativeURL = alternativeURL;
    }
}
