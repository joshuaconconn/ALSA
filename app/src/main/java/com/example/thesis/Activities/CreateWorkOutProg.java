package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thesis.Adapters.CreateWorkOutProgAdapter;

import com.example.thesis.Class.CreateWorkOutProgClass;
import com.example.thesis.Models.CreateWorkOutProgModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateWorkOutProg extends AppCompatActivity {
    Button create, back;
    DatabaseReference reff;
    FirebaseAuth fAuth;
    String userId;
    CreateWorkOutProgClass createWorkOutProgClass;
    private RecyclerView recyclerView;
    private List<CreateWorkOutProgModel> result;
    private CreateWorkOutProgAdapter adapter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_out_prog);
        reff = FirebaseDatabase.getInstance().getReference().child("coachProgram");
        createWorkOutProgClass = new CreateWorkOutProgClass();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        create = findViewById(R.id.create);
        //-------------------------------------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new CreateWorkOutProgAdapter(result);
        recyclerView.setAdapter(adapter);
        back = findViewById(R.id.back);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        //-------------------------------------------------------------------------------------
        updateList();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClick();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), InstructorHome.class);
                startActivity(TraineeHome);
                finish();
            }
        });
    }
    private void createClick(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateWorkOutProg.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_create_program, null);
        final EditText progName = mView.findViewById(R.id.progName);
        Button bmiBtn = mView.findViewById(R.id.bmiupdate);
        bmiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWorkOutProgClass.setProgName(progName.getText().toString());
                createWorkOutProgClass.setCoachId(userId);
                String key= reff.push().getKey();
                createWorkOutProgClass.setId(key);
                reff.child(key).setValue(createWorkOutProgClass);
                Intent TraineeHome = new Intent(getApplicationContext(), ProgramExercise.class);
                TraineeHome.putExtra("id",key);
                TraineeHome.putExtra("name",progName.getText().toString());
                TraineeHome.putExtra("coachId", userId);
                TraineeHome.putExtra("hax", 0);
                startActivity(TraineeHome);
                finish();
                //RN111818  CA: 45885
                showMessage("Id = " + key);
            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                setUser(item.getGroupId());
                break;
            case 1:
                removeUser(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void setUser(int position){
        String id = result.get(position).id;
        String name = result.get(position).progName;
        String coachId = result.get(position).coachId;
        Intent TraineeHome = new Intent(getApplicationContext(), ProgramExercise.class);
        TraineeHome.putExtra("id",id);
        TraineeHome.putExtra("name",name);
        TraineeHome.putExtra("coachId",coachId);
        TraineeHome.putExtra("hax", 0);
        startActivity(TraineeHome);
        finish();
    }

    public void removeUser(int position){
        reff.child(result.get(position).id).removeValue();
    }

    private void updateList() {
        int i = 1;

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("coachProgram").orderByChild("coachId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.add(dataSnapshot.getValue(CreateWorkOutProgModel.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CreateWorkOutProgModel model = dataSnapshot.getValue(CreateWorkOutProgModel.class);
                int index = getItemIndex(model);
                result.set(index, model);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                CreateWorkOutProgModel model = dataSnapshot.getValue(CreateWorkOutProgModel.class);
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

    private int getItemIndex(CreateWorkOutProgModel user){
        int index = -1;

        for (int i = 0; i < result.size(); i++){
            if(result.get(i).id.equals(user.id)){
                index = i;
                break;
            }
        }
        return index;
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }
}
