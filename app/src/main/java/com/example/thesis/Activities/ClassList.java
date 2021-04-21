package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thesis.Adapters.ClassListAdapters;
import com.example.thesis.Models.ClassListModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ClassList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ClassListModel> result;
    private ClassListAdapters adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference, reference1;
    Button back;
    FirebaseAuth fAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new ClassListAdapters(result);
        recyclerView.setAdapter(adapter);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        updateList();
        reference1 = FirebaseDatabase.getInstance().getReference().child("class");
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InstructorHome = new Intent(getApplicationContext(), InstructorHome.class);
                startActivity(InstructorHome);
                finish();
            }
        });
    }
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                setUser(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void setUser(int position){
        String id = result.get(position).traineeId.toString();
        Intent TraineeHome = new Intent(getApplicationContext(), PersonalExercise.class);
        TraineeHome.putExtra("classId",id);
        TraineeHome.putExtra("hax", 1);
        startActivity(TraineeHome);
        finish();


    }
    public void removeUser(int position){
        reference1.child(result.get(position).traineeId).removeValue();

    }

    private void updateList() {
        int i = 0;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("class").orderByChild("instructorId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ClassListModel model = dataSnapshot.getValue(ClassListModel.class);
                if (model.status.equals(1)) {
                    result.add(dataSnapshot.getValue(ClassListModel.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ClassListModel model = dataSnapshot.getValue(ClassListModel.class);
                int index = getItemIndex(model);
                int status = result.get(index).status;
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ClassListModel model = dataSnapshot.getValue(ClassListModel.class);
                int index = getItemIndex(model);
                result.remove(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private int getItemIndex(ClassListModel user){
        int index = -1;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).traineeId.equals(user.traineeId)) {
                    index = i;
                    break;
                }
            }

        return index;
    }


    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}
