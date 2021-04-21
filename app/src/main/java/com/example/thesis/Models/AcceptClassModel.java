package com.example.thesis.Models;

import java.util.HashMap;
import java.util.Map;

public class AcceptClassModel {
    public String instructorName, instructorId, traineeName, traineeId, date, classType, restDay, level;
    public Integer status;

    public AcceptClassModel(){

    }
    public AcceptClassModel(String classType, String date, String instructorId, String instructorName, int status
    , String traineeId, String traineeName, String restDay, String level){

        this.classType = classType;
        this.date = date;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.status = status;
        this.traineeId = traineeId;
        this.traineeName = traineeName;
        this.restDay = restDay;
        this.level = level;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("classType", classType);
        result.put("date", date);
        result.put("restDay", restDay);
        result.put("instructorId", instructorId);
        result.put("instructorName", instructorName);
        result.put("status", status);
        result.put("traineeId", traineeId);
        result.put("traineeName", traineeName);
        result.put("level", level);

       return result;
    }
}
