package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Adapters.AcceptClassAdapter;
import com.example.thesis.Models.AcceptClassModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InstructorHome extends AppCompatActivity {
    Button logout, choose, CreateClass, personal;
    FirebaseDatabase fData;
    FirebaseAuth fAuth;
    DatabaseReference reff;
    String userId;
    TextView textView;
    private List<AcceptClassModel> result;
    private AcceptClassAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_home);
        textView = findViewById(R.id.textView4);
        fData = FirebaseDatabase.getInstance();
        reff = FirebaseDatabase.getInstance().getReference().child("class");
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        choose = findViewById(R.id.Class);
        logout = findViewById(R.id.logout);
        personal = findViewById(R.id.personal);
        CreateClass = findViewById(R.id.CreateClass);
        result = new ArrayList<>();

        CreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Choose = new Intent(getApplicationContext(), CreateWorkOutProg.class);
                Choose.putExtra("id",userId);
                startActivity(Choose);
                finish();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Choose = new Intent(getApplicationContext(), AcceptClass.class);
                Choose.putExtra("id",userId);
                startActivity(Choose);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent InstructorHome = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(InstructorHome);
                finish();
            }
        });
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Choose = new Intent(getApplicationContext(), ClassList.class);
                Choose.putExtra("id",userId);
                startActivity(Choose);
                finish();
            }
        });
        updateList();

    }


    private void updateList() {
        reff = FirebaseDatabase.getInstance().getReference();
        reff.child("class").orderByChild("instructorId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AcceptClassModel model = dataSnapshot.getValue(AcceptClassModel.class);
                if (model.status.equals(0)) {
                    result.add(dataSnapshot.getValue(AcceptClassModel.class));
                }
                choose.setText("Trainee Request(" + result.size() + ")");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

}
