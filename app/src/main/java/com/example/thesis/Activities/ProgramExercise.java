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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.thesis.Adapters.CreateWorkOutProgAdapter;
import com.example.thesis.Adapters.PersonalExerciseAdapter;
import com.example.thesis.Adapters.ProgramExerciseAdapter;
import com.example.thesis.Class.CreateWorkOutProgClass;
import com.example.thesis.Class.DailyExerciseClass;
import com.example.thesis.Class.ProgramExerciseClass;
import com.example.thesis.Models.AcceptClassModel;
import com.example.thesis.Models.CreateWorkOutProgModel;
import com.example.thesis.Models.DailyExerciseModel;
import com.example.thesis.Models.InjuryModel;
import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProgramExercise extends AppCompatActivity{
    TextView name, id;
    Button add, copy, backTrainee, backDate;
    DatabaseReference reff;
    ProgramExerciseClass programExerciseClass;
    DailyExerciseClass dailyExerciseClass;
    CreateWorkOutProgClass createWorkOutProgClass;
    FirebaseAuth fAuth;
    String userId;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView;
    private List<ProgramExerciseModel> result;
    private List<InjuryModel> injuryUpper, injuryLower;
    private ProgramExerciseAdapter adapter;
    private PersonalExerciseAdapter adapterr;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_exercise);
        Bundle bundle = getIntent().getExtras();
        String ExerId = bundle.getString("id");
        String ExerName = bundle.getString("name");
        programExerciseClass = new ProgramExerciseClass();
        dailyExerciseClass = new DailyExerciseClass();
        reff = FirebaseDatabase.getInstance().getReference().child("ProgExercise");

        //================================================================================
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new ProgramExerciseAdapter(result);
        recyclerView.setAdapter(adapter);

        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        add = findViewById(R.id.add);
        copy = findViewById(R.id.copy);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        backTrainee = findViewById(R.id.backTrainee);

        createWorkOutProgClass = new CreateWorkOutProgClass();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClick();
            }
        });


            name.setText("Add Exercises to: " + ExerName);
            updateList();

        backTrainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), CreateWorkOutProg.class);
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
        reference.child("ProgExercise").orderByChild("coachProgId").equalTo(message).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                result.add(dataSnapshot.getValue(ProgramExerciseModel.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProgramExerciseModel model = dataSnapshot.getValue(ProgramExerciseModel.class);
                int index = getItemIndex(model);
                //result.set(index, model);
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

    private int getItemIndex(ProgramExerciseModel user){
        int index = -1;

        for (int i = 0; i < result.size(); i++){
            if(result.get(i).id.equals(user.id)){
                index = i;
                break;
            }
        }
        return index;
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                editExer(item.getGroupId());
                break;
            case 1:
                removeExer(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void removeExer(int position){
            reff.child(result.get(position).id).removeValue();
    }

    public void editExer(final int position){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProgramExercise.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_edit_exer, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        TextView part = mView.findViewById(R.id.part);
        TextView exerName = mView.findViewById(R.id.exerName);
        final EditText set = mView.findViewById(R.id.set);
        final EditText reps = mView.findViewById(R.id.reps);
        final EditText weight = mView.findViewById(R.id.weight);
        Button save = mView.findViewById(R.id.save);
        part.setText(result.get(position).part);
        exerName.setText(result.get(position).name);
        set.setHint(result.get(position).set + " sets");
        reps.setHint(result.get(position).reps + " reps");
        weight.setHint(result.get(position).weight + " kg");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ProgramExerciseModel user = result.get(position);
                    user.set = set.getText().toString();
                    user.reps = reps.getText().toString();
                    user.weight = weight.getText().toString();

                    Map<String, Object> userValues = user.toMap();
                    Map<String, Object> newUser = new HashMap<>();

                    newUser.put(user.id, userValues);

                    reff.updateChildren(newUser);
                    dialog.dismiss();
            }
        });
    }

    private void addClick(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProgramExercise.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_exercise_add, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        final Spinner part = mView.findViewById(R.id.part);
        ArrayAdapter<CharSequence> adapterspinn = ArrayAdapter.createFromResource(this, R.array.bodypart,
                android.R.layout.simple_spinner_item);
        adapterspinn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        part.setAdapter(adapterspinn);

        final Spinner upper = mView.findViewById(R.id.exerName);
        ArrayAdapter<CharSequence> adapterspin = ArrayAdapter.createFromResource(this, R.array.upper,
                android.R.layout.simple_spinner_item);
        adapterspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upper.setAdapter(adapterspin);

        final Spinner lower = mView.findViewById(R.id.exerName1);
        ArrayAdapter<CharSequence> adapterspinnn = ArrayAdapter.createFromResource(this, R.array.lower,
                android.R.layout.simple_spinner_item);
        adapterspinnn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lower.setAdapter(adapterspinnn);

        final Spinner cardio = mView.findViewById(R.id.exerName2);
        ArrayAdapter<CharSequence> adapterspinnnn = ArrayAdapter.createFromResource(this, R.array.cardio,
                android.R.layout.simple_spinner_item);
        adapterspinnnn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardio.setAdapter(adapterspinnnn);
        final EditText set = mView.findViewById(R.id.set);
        final EditText reps = mView.findViewById(R.id.reps);
        final EditText weight = mView.findViewById(R.id.weight);
        final TextView warning = mView.findViewById(R.id.warning);
        Bundle bundle = getIntent().getExtras();
        final String ExerId = bundle.getString("id");
        final String classId = bundle.getString("classId");
        String coachId = bundle.getString("coachId");
        Button addExer = mView.findViewById(R.id.addExer);
        upper.setVisibility(View.VISIBLE);
        warning.setVisibility(View.INVISIBLE);
        lower.setVisibility(View.INVISIBLE);

        part.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ExerPart = part.getSelectedItem().toString();
                   if(ExerPart.equals("Upper Body")){
                      upper.setVisibility(View.VISIBLE);
                      lower.setVisibility(View.INVISIBLE);
                      cardio.setVisibility(View.INVISIBLE);
                       set.setVisibility(view.VISIBLE);
                       reps.setVisibility(view.VISIBLE);
                       weight.setVisibility(view.VISIBLE);
                       set.setHint("Add Set");
                       reps.setHint("Add Reps");
                       weight.setHint("Add Weight in kg");

                  }else if(ExerPart.equals("Lower Body")){
                      upper.setVisibility(View.INVISIBLE);
                       lower.setVisibility(View.VISIBLE);
                       cardio.setVisibility(View.INVISIBLE);
                       set.setVisibility(view.VISIBLE);
                       reps.setVisibility(view.VISIBLE);
                       weight.setVisibility(view.VISIBLE);
                       set.setHint("Add Set");
                       reps.setHint("Add Reps");
                       weight.setHint("Add Weight in kg");
                    }else{
                       upper.setVisibility(View.INVISIBLE);
                       lower.setVisibility(View.INVISIBLE);
                       cardio.setVisibility(View.VISIBLE);
                       weight.setVisibility(view.INVISIBLE);
                       set.setHint("Add Resistance");
                       reps.setHint("Add Time");

                   }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        upper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addExer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    programExerciseClass.setPart(part.getSelectedItem().toString());
                    if (part.getSelectedItem().toString().equals("Upper Body")) {
                        programExerciseClass.setName(upper.getSelectedItem().toString());
                        if (upper.getSelectedItem().toString().equals("Barbell Bench Press")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/BarbellBenchPress.mp4?alt=media&token=fbaed264-ab5b-4cd9-946c-d20bdd13691c");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Machine Inclined Chest Press")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Machine%20inclined%20chest%20press.mp4?alt=media&token=92598422-709d-47d4-819c-7b13e58d7d64");
                            programExerciseClass.setAlternative("Dumbbel Incline Chest Press");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20%20Dumbbell%20Biceps%20Curl%20-%20Arm%20Workout.mp4?alt=media&token=4fec62ab-e5de-4503-ab76-17fc9e728f1b");
                        } else if (upper.getSelectedItem().toString().equals("Dumbbell Chest Fly")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Dumbbell%20Chest%20Fly.mp4?alt=media&token=08d51030-daac-4018-b936-247c4292498a");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Lat Pull Down")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Wide-Grip%20Lat%20Pulldown%20_%20Back%20Exercise%20Guide.mp4?alt=media&token=c02fa428-fac0-43bf-9ce1-e017aa69d878");
                            programExerciseClass.setAlternative("Pull Ups");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Perfect%20Pullup.mp4?alt=media&token=6804a36f-a68f-45f6-987e-58265bdf13c7");
                        } else if (upper.getSelectedItem().toString().equals("Dead Lift")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/deadlift.mp4?alt=media&token=52ca189b-8aca-415a-848c-7b4b261679b4");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Low Rows")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Seated%20Cable%20Row%20_%20Exercise%20Guide.mp4?alt=media&token=f764c1d3-db1f-46d7-b208-dcecff34566f");
                            programExerciseClass.setAlternative("Dumbell Shoulder press");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Dumbbell%20Shoulder%20Press.mp4?alt=media&token=214e34fd-f782-4815-9194-7fad7a1f2b0a");
                        } else if (upper.getSelectedItem().toString().equals("Shoulder Press")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/shoulder%20press.mp4?alt=media&token=5cf67c96-2374-4554-860f-37338c07c7d8");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Side Raises")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/side%20raise.mp4?alt=media&token=a7380e88-ce83-4515-9eed-bd2f3dc979ae");
                            programExerciseClass.setAlternative("Dumbell Curls");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20%20Dumbbell%20Biceps%20Curl%20-%20Arm%20Workout.mp4?alt=media&token=4fec62ab-e5de-4503-ab76-17fc9e728f1b");
                        } else if (upper.getSelectedItem().toString().equals("EZ Bar Curls(Biceps)")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20to%20Do%20a%20Barbell%20Curl%20_%20Arm%20Workout.mp4?alt=media&token=f558ef34-b9b3-44d6-bcc5-7038619cf5ce");
                            programExerciseClass.setAlternative("Dumbell Triceps Extension");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Dumbbell%20Tricep%20Extension%20-%20Arm%20Workout.mp4?alt=media&token=33c003de-6f03-4864-a96a-ace29bee98a7");
                        } else {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20To_%20Rope%20Push-Down.mp4?alt=media&token=f9c3914e-ed1b-4aa2-913c-ac848cbfade8");
                            programExerciseClass.setAlternative("Lying Dumbbell Tricep Extensions");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20To-%20Laying%20Dumbbell%20Tricep%20Extension.mp4?alt=media&token=da677df8-4932-4664-afa5-163c49bb8497");
                        }
                        programExerciseClass.setCoachProgId(ExerId);
                        programExerciseClass.setSet(set.getText().toString());
                        programExerciseClass.setReps(reps.getText().toString());
                        programExerciseClass.setWeight(weight.getText().toString());
                        String key = reff.push().getKey();
                        programExerciseClass.setId(key);
                        reff.child(key).setValue(programExerciseClass);
                        dialog.dismiss();
                    } else if (part.getSelectedItem().toString().equals("Upper Body")) {
                        programExerciseClass.setName(lower.getSelectedItem().toString());
                        if (lower.getSelectedItem().toString().equals("Barbell Squats(Squat Rack)")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20to%20Do%20a%20Squat%20_%20Gym%20Workout.mp4?alt=media&token=8ca58602-f2ce-4cba-93a5-8fc8391258ae");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (lower.getSelectedItem().toString().equals("Leg Press(Machine)")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/leg%20presas%20machine.mp4?alt=media&token=90c354c9-a84b-4726-8d8b-c6ff2c684d02");
                            programExerciseClass.setAlternative("Barbell Step-Ups");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Barbell%20Step-ups.mp4?alt=media&token=49595e11-4bd4-499d-8864-c7641b46458e");
                        } else if (lower.getSelectedItem().toString().equals("Leg Extension(Machine)")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/leg%20extension.mp4?alt=media&token=30b6eb48-d94c-4032-87ed-96c7f185fc26");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (lower.getSelectedItem().toString().equals("Leg Curls(Machine)")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/LEG%20CURL.mp4?alt=media&token=3b4cf9fe-a734-47ef-ac4c-91a45159b081");
                            programExerciseClass.setAlternative("Barbell Step-Ups");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Barbell%20Step-ups.mp4?alt=media&token=49595e11-4bd4-499d-8864-c7641b46458e");
                        } else if (lower.getSelectedItem().toString().equals("Angled Leg Press")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/angled%20leg%20press.mp4?alt=media&token=f8839796-a468-4b09-a090-ce5db268acf2");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        } else if (lower.getSelectedItem().toString().equals("Inner Abduction Machine")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Hip%20Abduction%20and%20Adduction%20Machine.mp4?alt=media&token=6084898b-c26e-4d96-8648-d3528791a6de");
                            programExerciseClass.setAlternative("Forwards and Backward Band Walks");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Resistance%20Band%20Exercises.mp4?alt=media&token=b953038e-bc22-4cfa-b4be-5c63463cb91e");
                        } else if (lower.getSelectedItem().toString().equals("Outer Abduction Machine")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Hip%20Abduction%20and%20Adduction%20Machine.mp4?alt=media&token=6084898b-c26e-4d96-8648-d3528791a6de");
                            programExerciseClass.setAlternative("Forwards and Backward Band Walks");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Resistance%20Band%20Exercises.mp4?alt=media&token=b953038e-bc22-4cfa-b4be-5c63463cb91e");
                        } else if (lower.getSelectedItem().toString().equals("Dumbbell Lunges")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20To_%20Dumbbell%20Stepping%20Lunge.mp4?alt=media&token=f983b685-47d1-484e-b7a4-7e8d2336905f");
                            programExerciseClass.setAlternative("Dumbell Calf Raise");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20How%20to%20Do%20a%20Calf%20Raise.mp4?alt=media&token=c43f8ba3-1a11-4117-a639-beb838963de9");
                        } else if (lower.getSelectedItem().toString().equals("Smith Machine Calf Raises")) {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/smith%20machine%20calf%20raise.mp4?alt=media&token=1b42d065-62fd-468c-b1e7-7473947b6e57");
                            programExerciseClass.setAlternative("Barbell Back Squat");
                            programExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Barbell%20Back%20Squat.mp4?alt=media&token=b2445c37-5a1d-4648-9e83-4cde57fc77b2");
                        } else {
                            programExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/smith%20machine%20squats.mp4?alt=media&token=5ced24ce-3dc9-48b6-b204-3a80f4005650");
                            programExerciseClass.setAlternative("N/A");
                            programExerciseClass.setAlternativeURL("N/A");
                        }
                        programExerciseClass.setCoachProgId(ExerId);
                        programExerciseClass.setSet(set.getText().toString());
                        programExerciseClass.setReps(reps.getText().toString());
                        programExerciseClass.setWeight(weight.getText().toString());
                        String key = reff.push().getKey();
                        programExerciseClass.setId(key);
                        reff.child(key).setValue(programExerciseClass);
                        dialog.dismiss();
                    }else{
                        programExerciseClass.setUrl("N/A");
                        programExerciseClass.setAlternative("N/A");
                        programExerciseClass.setAlternativeURL("N/A");
                        programExerciseClass.setName(cardio.getSelectedItem().toString());
                        programExerciseClass.setCoachProgId(ExerId);
                        programExerciseClass.setSet(set.getText().toString());
                        programExerciseClass.setReps(reps.getText().toString());
                        programExerciseClass.setWeight(weight.getText().toString());
                        String key = reff.push().getKey();
                        programExerciseClass.setId(key);
                        reff.child(key).setValue(programExerciseClass);
                        dialog.dismiss();

                    }

            }
        });
        dialog.show();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }
}
