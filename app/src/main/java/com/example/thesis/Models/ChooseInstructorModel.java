package com.example.thesis.Models;

public class ChooseInstructorModel {
    public String name, email, id;
    public Integer type;

    public ChooseInstructorModel(){

    }

    public ChooseInstructorModel(String email, String id, String name, int type) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.type = type;
    }
}
