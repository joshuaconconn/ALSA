package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.thesis.Adapters.PersonalExerciseAdapter;
import com.example.thesis.Class.BmiClass;
import com.example.thesis.Models.BmiModel;
import com.example.thesis.Models.InjuryModel;
import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraineeExerView extends AppCompatActivity {
    private List<ProgramExerciseModel> result;
    private List<BmiModel> bmiModels;
    private PersonalExerciseAdapter adapter;
    private DatabaseReference reference, reference1, resreff;
    List<String> restday;
    BmiClass bmiClass;
    Button close, bmi;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    String userId;
    TextView id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_exer_view);
        fAuth = FirebaseAuth.getInstance();
        restday = new ArrayList<>();

        userId = fAuth.getCurrentUser().getUid();
        close = findViewById(R.id.button3);
        id = findViewById(R.id.id);
        bmi = findViewById(R.id.bmi);
        bmi.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        bmiModels = new ArrayList<>();
        bmiModels.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new PersonalExerciseAdapter(result);
        recyclerView.setAdapter(adapter);
        bmiClass = new BmiClass();
        reference1 = FirebaseDatabase.getInstance().getReference().child("bmi");
        Bundle bundle = getIntent().getExtras();
        String date = bundle.getString("date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");

        updateList1(date);
        bmiUpdate(date);

        id.setText("Your Activity for: " + date);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), PersonalExercise.class);
                TraineeHome.putExtra("hax", 0);
                startActivity(TraineeHome);
                finish();
            }
        });
        bmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBmi();

            }
        });
        try {
            Date comDate = format.parse(date);
            String dayOfTheWeek = (String) DateFormat.format("EEEE", comDate);
            addRestday(dayOfTheWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    private void updateList1(final String date) {
        int i = 1;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("DailyExercise").orderByChild("classId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                if (model.date.equals(date)) {
                    result.add(dataSnapshot.getValue(ProgramExerciseModel.class));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                int index = getItemIndex(model);
                result.set(index, model);
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
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                if(result.get(item.getGroupId()).url.equals("N/A")){
                    showMessage("Not Available");
                }else {
                    setUser1(item.getGroupId());
                    break;
                }
            case 1:
                if(result.get(item.getGroupId()).alternativeURL.equals("N/A")){
                    showMessage("Not Available");
                }else {
                    setUser2(item.getGroupId());
                    break;
                }
        }
        return super.onContextItemSelected(item);
    }

    public void setUser1(int position){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(TraineeExerView.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_video, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        String url = result.get(position).url;
        VideoView videoView = mView.findViewById(R.id.videoView);
        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        Button close = mView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void setUser2(int position){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(TraineeExerView.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_video, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        String url = result.get(position).alternativeURL;
        VideoView videoView = mView.findViewById(R.id.videoView);
        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        Button close = mView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void bmiUpdate(final String date){
        int i = 1;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("bmi").orderByChild("userId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                if(model.date.equals(date) && model.type.equals(1)){
                    bmiModels.add(dataSnapshot.getValue(BmiModel.class));
                    if(model.weight.equals(0)){
                        bmi.setVisibility(View.VISIBLE);
                        showMessage("You need to update your BMI for this day");
                    }else{
                        bmi.setVisibility(View.INVISIBLE);
                    }

                }

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
    private void updateBmi(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(TraineeExerView.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_bmi, null);
        final EditText height = mView.findViewById(R.id.height);
        final EditText weight = mView.findViewById(R.id.weight);
        mBuilder.setView(mView);
        Button bmiBtn = mView.findViewById(R.id.bmiupdate);
        final AlertDialog dialog = mBuilder.create();

        bmiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uHeight = height.getText().toString();
                String uWeight = weight.getText().toString();
                if(uHeight.isEmpty() || uWeight.isEmpty()){
                    showMessage("Please fill up");

                }else {
                    int weight1 = Integer.parseInt(uWeight);
                    int height1 = Integer.parseInt(uHeight);
                    DecimalFormat df = new DecimalFormat("#.#");
                    String status;
                    double bmi = Math.round(100 * 100 * weight1) / (height1 * height1);

                    if (bmi < 18.5) {
                        status = "Underweight";
                    } else if (bmi < 25) {
                        status = "Normal";
                    } else if (bmi < 30) {
                        status = "Overweight";
                    } else {
                        status = "Obese";
                    }
                    BmiModel user = bmiModels.get(0);
                    user.height = height1;
                    user.weight = weight1;
                    user.bmi = bmi;
                    user.status = status;

                    Map<String, Object> userValues = user.toMap();
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put(user.id, userValues);
                    reference1.updateChildren(newUser);

                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }
    private void addRestday(final String day){
        resreff = FirebaseDatabase.getInstance().getReference();
        resreff.child("restday").orderByChild("classId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String y = dataSnapshot.child("day").getValue(String.class);
                if(y.equals(day)){
                    id.setText("Your Activity for this day: Rest");
                }
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
