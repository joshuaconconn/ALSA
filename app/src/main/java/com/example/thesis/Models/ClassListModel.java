package com.example.thesis.Models;

public class ClassListModel {
    public String instructorName, instructorId, traineeName, traineeId, date, classType, restday, level;
    public Integer status;
    public ClassListModel(){

    }
    public ClassListModel(String classType, String date, String instructorId, String instructorName, int status
            , String traineeId, String traineeName, String restday, String level){

        this.classType = classType;
        this.date = date;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.status = status;
        this.traineeId = traineeId;
        this.traineeName = traineeName;
        this.restday = restday;
        this.level = level;
    }
}
