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

import java.util.ArrayList;
import java.util.List;

public class ChooseInstructor extends AppCompatActivity{

    FirebaseAuth fAuth;
    TextView idd;
    String userId;
    Button back;
    private RecyclerView recyclerView;
    private List<ChooseInstructorModel> result;
    private ChooseInstructorAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_instructor);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("id");

        back = findViewById(R.id.back);
        idd = findViewById(R.id.id);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new ChooseInstructorAdapter(result);
        recyclerView.setAdapter(adapter);
        updateList();
        textView = findViewById(R.id.id);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                startActivity(TraineeHome);
                finish();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InstructorHome = new Intent(getApplicationContext(), TraineeHome.class);
                startActivity(InstructorHome);
                finish();
            }
        });

    }
    //@Override
    public boolean onContextItemSelected(MenuItem item) {

        //int id = reference.child(result.get(item));

        switch (item.getItemId()) {
            case 0:
                setUser(item.getGroupId());
               break;
        }
       return super.onContextItemSelected(item);
    }
    public void setUser(int position){
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("id");
        String idd = result.get(position).id;
        showMessage("ID = " + idd);
        Intent TraineeHome = new Intent(getApplicationContext(), EnrollClass.class);
        TraineeHome.putExtra("instructor",idd);
        TraineeHome.putExtra("trainee", message);
        startActivity(TraineeHome);
        finish();

    }
    private void updateList() {
        int i = 1;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("user").orderByChild("type").equalTo(i).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.add(dataSnapshot.getValue(ChooseInstructorModel.class));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChooseInstructorModel model = dataSnapshot.getValue(ChooseInstructorModel.class);
                int index = getItemIndex(model);
                result.set(index, model);
                adapter.notifyItemChanged(index);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ChooseInstructorModel model = dataSnapshot.getValue(ChooseInstructorModel.class);
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

    private int getItemIndex(ChooseInstructorModel user){
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
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


}
