package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Adapters.ChooseInstructorAdapter;
import com.example.thesis.Models.ChooseInstructorModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TraineeHome extends AppCompatActivity {
    Button logout, choose, showclass, profile;
    TextView text, wait;
    FirebaseAuth fAuth;
    String userId, name;
    Integer Type;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private List<ChooseInstructorModel> result;
    private ChooseInstructorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traineehome);

        //Bundle bundle = getIntent().getExtras();
        //String id = bundle.getString("msg");
        logout = findViewById(R.id.logout);
        choose = findViewById(R.id.choose);
        showclass = findViewById(R.id.showclass);
        text = findViewById(R.id.textView4);
        wait = findViewById(R.id.wait);
        profile = findViewById(R.id.profile);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        choose.setVisibility(View.VISIBLE);
        wait.setVisibility(View.INVISIBLE);
        showclass.setVisibility(View.INVISIBLE);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("class").orderByChild("traineeId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Type = dataSnapshot.child("status").getValue(Integer.class);
                name = dataSnapshot.child("traineeName").getValue().toString();
                choose.setVisibility(View.VISIBLE);
                wait.setVisibility(View.INVISIBLE);
                showclass.setVisibility(View.INVISIBLE);
                    if (Type.equals(0)) {
                        choose.setVisibility(View.INVISIBLE);
                        wait.setVisibility(View.VISIBLE);
                        showclass.setVisibility(View.INVISIBLE);
                    }else if (Type.equals(1)){
                        choose.setVisibility(View.INVISIBLE);
                        wait.setVisibility(View.INVISIBLE);
                        showclass.setVisibility(View.VISIBLE);
                    }else {
                    choose.setVisibility(View.VISIBLE);
                    wait.setVisibility(View.INVISIBLE);
                    showclass.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Type = dataSnapshot.child("status").getValue(Integer.class);
                name = dataSnapshot.child("traineeName").getValue().toString();
                choose.setVisibility(View.VISIBLE);
                wait.setVisibility(View.INVISIBLE);
                showclass.setVisibility(View.INVISIBLE);
                if (Type.equals(0)) {
                    choose.setVisibility(View.INVISIBLE);
                    wait.setVisibility(View.VISIBLE);
                    showclass.setVisibility(View.INVISIBLE);
                }else if (Type.equals(1)){
                    choose.setVisibility(View.INVISIBLE);
                    wait.setVisibility(View.INVISIBLE);
                    showclass.setVisibility(View.VISIBLE);
                }else {
                    choose.setVisibility(View.VISIBLE);
                    wait.setVisibility(View.INVISIBLE);
                    showclass.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    choose.setVisibility(View.VISIBLE);
                    wait.setVisibility(View.INVISIBLE);
                    showclass.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChooseHome = new Intent(getApplicationContext(), UserInfo.class);
                ChooseHome.putExtra("id",userId);
                startActivity(ChooseHome);
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
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChooseHome = new Intent(getApplicationContext(), ChooseInstructor.class);
                ChooseHome.putExtra("id",userId);
                startActivity(ChooseHome);
                finish();
            }
        });
        showclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChooseHome = new Intent(getApplicationContext(), PersonalExercise.class);
                ChooseHome.putExtra("id",userId);
                ChooseHome.putExtra("hax",0);
                startActivity(ChooseHome);
                finish();
            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}