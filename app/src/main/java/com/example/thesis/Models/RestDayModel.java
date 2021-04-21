package com.example.thesis.Models;

import java.util.HashMap;
import java.util.Map;

public class RestDayModel {
    public String id, day, classId;

    public RestDayModel(){

    }
    public RestDayModel(String id, String day, String classId){
        this. id = id;
        this.day = day;
        this.classId = classId;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("day", day);
        result.put("classId", classId);

        return result;
    }
}
