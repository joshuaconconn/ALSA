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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptClass extends AppCompatActivity {

    FirebaseAuth fAuth;
    String userId;
    private RecyclerView recyclerView;
    private List<AcceptClassModel> result;
    private AcceptClassAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference, reference1;
    TextView textView;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_accept_class);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new AcceptClassAdapter(result);
        recyclerView.setAdapter(adapter);
        updateList();
        textView = findViewById(R.id.id);
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
            case 1:
                removeUser(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void setUser(int position){
        AcceptClassModel user = result.get(position);
        user.status = 1;
        Map<String, Object> userValues = user.toMap();
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(user.traineeId, userValues);
        reference1.updateChildren(newUser);
        result.remove(position);
        adapter.notifyItemRemoved(position);
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
                AcceptClassModel model = dataSnapshot.getValue(AcceptClassModel.class);
                if (model.status.equals(0)) {
                    result.add(dataSnapshot.getValue(AcceptClassModel.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AcceptClassModel model = dataSnapshot.getValue(AcceptClassModel.class);
                    int index = getItemIndex(model);
                    adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                AcceptClassModel model = dataSnapshot.getValue(AcceptClassModel.class);
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
    private int getItemIndex(AcceptClassModel user){
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
