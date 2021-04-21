package com.example.thesis.Models;

import java.util.HashMap;
import java.util.Map;

public class BmiModel {
    public String date, userId, status, id;
    public Integer weight, height, type;
    public Double bmi;

    public BmiModel(){

    }
    public BmiModel(String date, String userId, String status, int weight, int height, double bmi, int type, String id){
        this.id = id;
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.status = status;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> results = new HashMap<>();
        results.put("id", id);
        results.put("type", type);
        results.put("date", date);
        results.put("userId", userId);
        results.put("status", status);
        results.put("weight", weight);
        results.put("height", height);
        results.put("bmi", bmi);

        return results;
    }
}
