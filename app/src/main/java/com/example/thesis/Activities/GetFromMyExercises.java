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
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Adapters.BmiAdapter;
import com.example.thesis.Adapters.GetFromMyAdapter;
import com.example.thesis.Adapters.GetFromMyExerciseAdapter;
import com.example.thesis.Adapters.PersonalExerciseAdapter;
import com.example.thesis.Adapters.ProgramExerciseAdapter;
import com.example.thesis.Class.CreateWorkOutProgClass;
import com.example.thesis.Class.DailyExerciseClass;
import com.example.thesis.Class.ProgramExerciseClass;
import com.example.thesis.Models.CreateWorkOutProgModel;
import com.example.thesis.Models.InjuryModel;
import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GetFromMyExercises extends AppCompatActivity {
    TextView name;
    FirebaseAuth fAuth;
    String userId;
    Button back;
    private RecyclerView recyclerView;
    private List<CreateWorkOutProgModel> result;
    private List<ProgramExerciseModel> resultt;
    private GetFromMyExerciseAdapter adapter;
    private GetFromMyAdapter adapterr;
    private DatabaseReference reference;
    DailyExerciseClass dailyExerciseClass;
    DatabaseReference refff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_from_my_exercises);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new GetFromMyExerciseAdapter(result);
        recyclerView.setAdapter(adapter);
        dailyExerciseClass = new DailyExerciseClass();
        refff = FirebaseDatabase.getInstance().getReference().child("DailyExercise");

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        updateList();
        Bundle bundle = getIntent().getExtras();
        final String classId = bundle.getString("classId");
        final String date = bundle.getString("date");
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), PersonalExerciseAdd.class);
                TraineeHome.putExtra("classId", classId);
                TraineeHome.putExtra("date", date);
                startActivity(TraineeHome);
                finish();
            }
        });

    }

    private void updateList() {
        int i = 1;
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("id");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("coachProgram").orderByChild("coachId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                result.add(dataSnapshot.getValue(CreateWorkOutProgModel.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                int index = getItemIndex(model);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
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
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                updateList1(item.getGroupId());
                bmiList1(item.getGroupId());
                break;
            case 1:
                updateList1(item.getGroupId());
                copy();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private int getItemIndex(ProgramExerciseModel user) {
        int index = -1;

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).id.equals(user.id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void bmiList1(int position) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(GetFromMyExercises.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_daily_exer, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        RecyclerView recyclerView = mView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        resultt = new ArrayList<>();
        adapterr = new GetFromMyAdapter(resultt);
        recyclerView.setAdapter(adapterr);
        dialog.show();
    }

    private void updateList1(int position) {
        int i = 1;
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("id");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("ProgExercise").orderByChild("coachProgId").equalTo(result.get(position).id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                resultt.add(dataSnapshot.getValue(ProgramExerciseModel.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                int index = getItemIndex(model);
                adapterr.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                int index = getItemIndex(model);
                resultt.remove(index);
                adapterr.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void copy(){
        Bundle bundle = getIntent().getExtras();
        String classId = bundle.getString("classId");
        String date = bundle.getString("date");
        for (int i = 0; i < resultt.size(); i++) {
            dailyExerciseClass.setPart(resultt.get(i).part);
            dailyExerciseClass.setAlternative(resultt.get(i).alternative);
            dailyExerciseClass.setAlternativeURL(resultt.get(i).alternativeURL);
            dailyExerciseClass.setName(resultt.get(i).name);
            dailyExerciseClass.setClassId(classId);
            dailyExerciseClass.setSet(resultt.get(i).set);
            dailyExerciseClass.setDate(date);
            dailyExerciseClass.setStatus("0");
            dailyExerciseClass.setReps(resultt.get(i).reps);
            dailyExerciseClass.setWeight(resultt.get(i).weight);
            dailyExerciseClass.setUrl(resultt.get(i).url);
            String keyy = refff.push().getKey();
            dailyExerciseClass.setId(keyy);
            refff.child(keyy).setValue(dailyExerciseClass);
        }
        Intent TraineeHome = new Intent(getApplicationContext(), PersonalExerciseAdd.class);
        TraineeHome.putExtra("classId", classId);
        TraineeHome.putExtra("date", date);
        startActivity(TraineeHome);
        finish();
    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }
}
