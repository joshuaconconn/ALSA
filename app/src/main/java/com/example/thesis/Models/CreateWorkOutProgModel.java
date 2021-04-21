package com.example.thesis.Models;

public class CreateWorkOutProgModel {
    public String progName, id, coachId;

    public CreateWorkOutProgModel(){

    }
    public CreateWorkOutProgModel(String progName, String id, String coachId){
        this.progName = progName;
        this.id = id;
        this.coachId = coachId;

    }

}
