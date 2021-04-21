package com.example.thesis.Models;

import java.util.HashMap;
import java.util.Map;

public class InjuryModel {
    public String userId, name, recovery, date, id, part;

    public InjuryModel() {


    }

    public InjuryModel(String userId, String name, String recovery, String date, String id, String part) {
        this.userId = userId;
        this.name = name;
        this.recovery = recovery;
        this.date = date;
        this.id = id;
        this.part = part;

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> results = new HashMap<>();
        results.put("id", id);
        results.put("part", part);
        results.put("date", date);
        results.put("recovery", recovery);
        results.put("name", name);
        results.put("userId", userId);

        return results;
    }
}
