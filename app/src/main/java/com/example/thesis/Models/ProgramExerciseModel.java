package com.example.thesis.Models;

import java.util.HashMap;
import java.util.Map;

public class ProgramExerciseModel {
    public String part, name, coachProgId, id, set, reps, weight, url, classId, status, date, alternative, alternativeURL;


    public ProgramExerciseModel(){

    }

    public ProgramExerciseModel(String part, String name, String coachProgId, String id
    ,String set, String reps, String weight, String url, String date, String classId, String status, String alternative, String alternativeURL){
        this.part = part;
        this.name = name;
        this.coachProgId = coachProgId;
        this.id = id;
        this.set = set;
        this.reps = reps;
        this.weight = weight;
        this.url = url;
        this.date = date;
        this.classId = classId;
        this.status = status;
        this.alternative = alternative;
        this.alternativeURL = alternativeURL;

    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("part", part);
        result.put("name", name);
        result.put("coachProgId", coachProgId);
        result.put("id", id);
        result.put("set", set);
        result.put("reps", reps);
        result.put("weight", weight);
        result.put("url", url);
        result.put("alternative", alternative);
        result.put("alternativeURL", alternativeURL);
        return result;
    }
    public Map<String, Object> toMaps(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("part", part);
        result.put("name", name);
        result.put("classId", classId);
        result.put("id", id);
        result.put("set", set);
        result.put("reps", reps);
        result.put("weight", weight);
        result.put("url", url);
        result.put("date", date);
        result.put("status", status);
        result.put("alternative", alternative);
        result.put("alternativeURL", alternativeURL);
        return result;
    }
}
