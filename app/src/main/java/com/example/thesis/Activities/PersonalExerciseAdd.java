package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Adapters.PersonalExerciseAdapter;
import com.example.thesis.Adapters.PersonalExerciseAddAdapter;
import com.example.thesis.Adapters.ProgramExerciseAdapter;
import com.example.thesis.Class.BmiClass;
import com.example.thesis.Class.CreateWorkOutProgClass;
import com.example.thesis.Class.DailyExerciseClass;
import com.example.thesis.Class.ProgramExerciseClass;
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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PersonalExerciseAdd extends AppCompatActivity {
    TextView name, id;
    Button add, copy, backDate, sched, copy2;
    DatabaseReference reference1, refff, refCopy, bmiCheck, resreff;
    ProgramExerciseClass programExerciseClass;
    DailyExerciseClass dailyExerciseClass;
    CreateWorkOutProgClass createWorkOutProgClass;
    FirebaseAuth fAuth;
    String userId;
    private RecyclerView recyclerView1;
    private List<ProgramExerciseModel> result;
    private List<InjuryModel> injuryUpper, injuryLower;
    private List<BmiModel> bmilist;
    private List<String> injuryAll;
    private PersonalExerciseAddAdapter adapter;
    private DatabaseReference reference;
    BmiClass bmiClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_exercise_add);
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("classId");
        dailyExerciseClass = new DailyExerciseClass();
        refff = FirebaseDatabase.getInstance().getReference().child("DailyExercise");
        refCopy = FirebaseDatabase.getInstance().getReference().child("coachProgram");
        bmiCheck = FirebaseDatabase.getInstance().getReference().child("bmi");
        bmiClass = new BmiClass();

        recyclerView1 = findViewById(R.id.recyclerview1);
        recyclerView1.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(llm);
        adapter = new PersonalExerciseAddAdapter(result);
        recyclerView1.setAdapter(adapter);

        sched = findViewById(R.id.sched);
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        add = findViewById(R.id.add);
        copy = findViewById(R.id.copy);
        copy2 = findViewById(R.id.copy2);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        backDate = findViewById(R.id.backDate);
        injuryUpper = new ArrayList<>();
        injuryUpper.clear();
        injuryLower = new ArrayList<>();
        injuryLower.clear();
        injuryAll = new ArrayList<>();
        injuryAll.clear();
        bmilist = new ArrayList<>();
        bmilist.clear();
        updateInjury();
        String date = bundle.getString("date");
        bmiChecker(date);


        name.setText("Add Exercises to: " + date);
        Date today = new Date();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
        final String dateToday = format.format(today);
        try {
            Date curDate = format.parse(dateToday);
            Date comDate = format.parse(date);
            long diff = curDate.getTime() - comDate.getTime();
            String dayOfTheWeek = (String) DateFormat.format("EEEE", comDate);
            addRestday(dayOfTheWeek);
            if(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 0){
                add.setVisibility(View. INVISIBLE);
                sched.setVisibility(View. INVISIBLE);
                copy.setVisibility(View. INVISIBLE);
                copy2.setVisibility(View. INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        updateList1(date);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClick();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result.size() == 0){
                    showMessage("No Exercises to copy");
                }else{
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(PersonalExerciseAdd.this);
                    final View mView = getLayoutInflater().inflate(R.layout.pop_up_date, null);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    final EditText progName = mView.findViewById(R.id.progName);
                    Bundle bundle = getIntent().getExtras();
                    final String classId = bundle.getString("classId");
                    Button bmiBtn = mView.findViewById(R.id.injuryupdate);
                    bmiBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePicker datepicker = mView.findViewById(R.id.datepicker);
                            int year = datepicker.getYear();
                            int day = datepicker.getDayOfMonth();
                            int month = datepicker.getMonth() + 1;
                            String date = year + "-" + month + "-" + day;
                            String stat = "0";
                            try {
                                Date comDate = format.parse(date);
                                String dayOfTheWeek = (String) DateFormat.format("EEEE", comDate);
                                addRestday1(dayOfTheWeek);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < result.size(); i++) {
                                dailyExerciseClass.setAlternative(result.get(i).alternative);
                                dailyExerciseClass.setAlternativeURL(result.get(i).alternativeURL);
                                dailyExerciseClass.setPart(result.get(i).part);
                                dailyExerciseClass.setName(result.get(i).name);
                                dailyExerciseClass.setClassId(classId);
                                dailyExerciseClass.setSet(result.get(i).set);
                                dailyExerciseClass.setDate(date);
                                dailyExerciseClass.setStatus(stat);
                                dailyExerciseClass.setReps(result.get(i).reps);
                                dailyExerciseClass.setWeight(result.get(i).weight);
                                dailyExerciseClass.setUrl(result.get(i).url);
                                String keyy = refff.push().getKey();
                                dailyExerciseClass.setId(keyy);
                                refff.child(keyy).setValue(dailyExerciseClass);
                            }
                            //copy(key);
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
        backDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                String message = bundle.getString("classId");
                Intent TraineeHome = new Intent(getApplicationContext(), PersonalExercise.class);
                TraineeHome.putExtra("classId", message);
                TraineeHome.putExtra("hax", 1);
                startActivity(TraineeHome);
                finish();
            }
        });
        sched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                String message = bundle.getString("classId");
                String date = bundle.getString("date");
                addBmi(date, message);
                sched.setVisibility(View. INVISIBLE);
            }
        });
        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                String message = bundle.getString("classId");
                String date = bundle.getString("date");
                Intent TraineeHome = new Intent(getApplicationContext(), GetFromMyExercises.class);
                TraineeHome.putExtra("classId", message);
                TraineeHome.putExtra("date", date);
                startActivity(TraineeHome);
                finish();

            }
        });
        if(bmilist.size() > 0){

        }else{
            sched.setVisibility(View.VISIBLE);
        }
    }

    private void updateInjury(){
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("classId");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("injury").orderByChild("userId").equalTo(message).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                InjuryModel model = dataSnapshot.getValue(InjuryModel.class);
                if (model.recovery.equals("Recovering")) {
                    injuryAll.add(dataSnapshot.child("part").getValue(String.class));
                    if(model.part.equals("Neck") || model.part.equals("Shoulder") || model.part.equals("Chest")
                            || model.part.equals("Arm") || model.part.equals("Back") || model.part.equals("Elbow")
                            || model.part.equals("Hips") || model.part.equals("Ribs")){
                        injuryUpper.add(dataSnapshot.getValue(InjuryModel.class));
                    }else{
                        injuryLower.add(dataSnapshot.getValue(InjuryModel.class));

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
    private void updateList1(final String date){
        int i = 1;
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("classId");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("DailyExercise").orderByChild("classId").equalTo(message).addChildEventListener(new ChildEventListener() {
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
                editExer(item.getGroupId());
                break;
            case 1:
                removeExer(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void removeExer(int position){
            refff.child(result.get(position).id).removeValue();
    }

    public void editExer(final int position){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PersonalExerciseAdd.this);
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
        if(result.get(position).part.equals("Cardio")){
            part.setText(result.get(position).part);
            exerName.setText(result.get(position).name);
            set.setHint("Resistance" + result.get(position).set);
            reps.setHint(result.get(position).reps + " mins");
            weight.setVisibility(View.INVISIBLE);
        }else {
            part.setText(result.get(position).part);
            exerName.setText(result.get(position).name);
            set.setHint(result.get(position).set + " sets");
            reps.setHint(result.get(position).reps + " reps");
            weight.setHint(result.get(position).weight + " kg");
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgramExerciseModel user = result.get(position);
                if(result.get(position).part.equals("Cardio")){
                    if(set.getText().toString().isEmpty() || reps.getText().toString().isEmpty()){
                        showMessage("please fill all fields");
                    }else {
                        user.set = set.getText().toString();
                        user.reps = reps.getText().toString();

                        Map<String, Object> userValues = user.toMaps();
                        Map<String, Object> newUser = new HashMap<>();

                        newUser.put(user.id, userValues);

                        refff.updateChildren(newUser);
                        dialog.dismiss();
                    }

                }else {
                    if(set.getText().toString().isEmpty() || reps.getText().toString().isEmpty() || weight.getText().toString().isEmpty()){
                        showMessage("please fill all fields");
                    }else {
                        user.set = set.getText().toString();
                        user.reps = reps.getText().toString();
                        user.weight = weight.getText().toString();

                        Map<String, Object> userValues = user.toMaps();
                        Map<String, Object> newUser = new HashMap<>();

                        newUser.put(user.id, userValues);

                        refff.updateChildren(newUser);
                        dialog.dismiss();
                    }
                }
            }
        });
    }
    private void bmiChecker(final String date){
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("classId");
        reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child("bmi").orderByChild("userId").equalTo(message).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                if(model.date.equals(date) && model.type.equals(1)){
                    bmilist.add(dataSnapshot.getValue(BmiModel.class));
                    sched.setVisibility(View.INVISIBLE);
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

    private void addClick(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PersonalExerciseAdd.this);
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
        final TextView warning1 = mView.findViewById(R.id.warning1);
        Bundle bundle = getIntent().getExtras();
        final String classId = bundle.getString("classId");
        Button addExer = mView.findViewById(R.id.addExer);
        upper.setVisibility(View.VISIBLE);
        warning.setVisibility(View.INVISIBLE);
        lower.setVisibility(View.INVISIBLE);
        cardio.setVisibility(View.INVISIBLE);

        part.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ExerPart = part.getSelectedItem().toString();

                if(ExerPart.equals("Upper Body")){
                    set.setVisibility(view.VISIBLE);
                    reps.setVisibility(view.VISIBLE);
                    weight.setVisibility(view.VISIBLE);
                    set.setHint("Add Set");
                    reps.setHint("Add Reps");
                    weight.setHint("Add Weight in kg");
                    upper.setVisibility(View.VISIBLE);
                    lower.setVisibility(View.INVISIBLE);
                    cardio.setVisibility(View.INVISIBLE);
                    if(injuryUpper.size() > 0){
                        warning.setText("Warning: Injury on Upper Body");
                        warning.setVisibility(View.VISIBLE);
                        warning1.setVisibility(View.VISIBLE);
                        showMessage("Injury on Upper Body");
                    }else{
                        warning.setVisibility(View.INVISIBLE);
                        warning1.setVisibility(View.INVISIBLE);
                    }
                }else if (ExerPart.equals("Lower Body")){
                    set.setVisibility(view.VISIBLE);
                    reps.setVisibility(view.VISIBLE);
                    weight.setVisibility(view.VISIBLE);
                    upper.setVisibility(View.INVISIBLE);
                    lower.setVisibility(View.VISIBLE);
                    cardio.setVisibility(View.INVISIBLE);
                    set.setHint("Add Set");
                    reps.setHint("Add Reps");
                    weight.setHint("Add Weight in kg");
                    if(injuryLower.size() > 0){
                        warning.setText("Warning: Injury on Lower Body");
                        warning.setVisibility(View.VISIBLE);
                        warning1.setVisibility(View.VISIBLE);
                        showMessage("Injury on Lower Body");
                    }else{
                        warning1.setVisibility(View.INVISIBLE);
                        warning.setVisibility(View.INVISIBLE);
                    }
                    upper.setVisibility(View.INVISIBLE);
                    lower.setVisibility(View.VISIBLE);
                    cardio.setVisibility(View.INVISIBLE);
                }else{
                    upper.setVisibility(View.INVISIBLE);
                    lower.setVisibility(View.INVISIBLE);
                    weight.setVisibility(view.INVISIBLE);
                    warning.setVisibility(View.INVISIBLE);
                    warning1.setVisibility(View.INVISIBLE);
                    set.setHint("Add Resistance");
                    reps.setHint("Add Time");
                    cardio.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        upper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = "";
                    String upperIn = upper.getSelectedItem().toString();
                    if(upperIn.equals("Barbell Bench Press")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Chest") || injuryAll.get(i).equals("Shoulder") || injuryAll.get(i).equals("Back")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }
                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Machine Inclined Chest Press")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Chest") || injuryAll.get(i).equals("Shoulder") || injuryAll.get(i).equals("Arm")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }
                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Dumbbell Chest Fly")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Chest")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }

                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Lat Pull Down")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Back")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }

                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Dead Lift")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Chest") || injuryAll.get(i).equals("Shoulder")|| injuryAll.get(i).equals("Back") || injuryAll.get(i).equals("Thigh(Leg)")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }

                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Low Rows")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Chest") || injuryAll.get(i).equals("Shoulders") || injuryAll.get(i).equals("Back")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }
                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Shoulder Press")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Arm") || injuryAll.get(i).equals("Shoulder")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }

                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Side Raises")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Ribs") || injuryAll.get(i).equals("Shoulder")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }

                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("EZ Bar Curls(Biceps")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Arm")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }

                        warning1.setText("Injury on: " + text);
                    }
                    if(upperIn.equals("Triceps Rope Extension")){
                        warning1.setVisibility(View.INVISIBLE);
                        for(int i = 0; injuryAll.size() > i; i++) {
                            if (injuryAll.get(i).equals("Arm")) {
                                text = text + injuryAll.get(i) + ", ";
                                warning1.setVisibility(View.VISIBLE);
                            }
                        }
                        warning1.setText("Injury on: " + text);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String upperIn = lower.getSelectedItem().toString();
                String text = "";
                if(upperIn.equals("Barbell Squats(Squat Rack)")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)") || injuryAll.get(i).equals("Back")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }

                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Leg Press(Machine)")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Leg Extension(Machine)")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Leg Curls(Machine)")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)") || injuryAll.get(i).equals("Calf(Leg)")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Angled Leg Press")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Inner Abduction Machine")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)") || injuryAll.get(i).equals("Calf(Leg)") || injuryAll.get(i).equals("Hips")) {
                            text = text + injuryLower.get(i).part + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Outer Abduction Machine")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)") || injuryAll.get(i).equals("Calf(Leg)") || injuryAll.get(i).equals("Hips")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Dumbbell Lunges")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)") || injuryAll.get(i).equals("Hips")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }

                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Smith Machine Calf Raises")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Calf(Leg)")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
                if(upperIn.equals("Smith Machine Squats")){
                    warning1.setVisibility(View.INVISIBLE);
                    for(int i = 0; injuryAll.size() > i; i++) {
                        if (injuryAll.get(i).equals("Thigh(Leg)")) {
                            text = text + injuryAll.get(i) + ", ";
                            warning1.setVisibility(View.VISIBLE);
                        }
                    }
                    warning1.setText("Injury on: " + text);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addExer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(part.getSelectedItem().toString().equals("Cardio")){
                    if(set.getText().toString().isEmpty() || reps.getText().toString().isEmpty()){
                        showMessage("Please fill everything");
                    }else{
                        Bundle bundle = getIntent().getExtras();
                        String date = bundle.getString("date");
                        dailyExerciseClass.setUrl("N/A");
                        String string = "0";
                        dailyExerciseClass.setPart(part.getSelectedItem().toString());
                        dailyExerciseClass.setName(cardio.getSelectedItem().toString());
                        dailyExerciseClass.setDate(date);
                        dailyExerciseClass.setClassId(classId);
                        dailyExerciseClass.setSet(set.getText().toString());
                        dailyExerciseClass.setReps(reps.getText().toString());
                        dailyExerciseClass.setWeight("N/A");
                        dailyExerciseClass.setAlternative("N/A");
                        dailyExerciseClass.setAlternativeURL("N/A");
                        dailyExerciseClass.setStatus(string);
                        String key = refff.push().getKey();
                        dailyExerciseClass.setId(key);
                        refff.child(key).setValue(dailyExerciseClass);
                    }


                }else if(set.getText().toString().isEmpty() || reps.getText().toString().isEmpty() || weight.getText().toString().isEmpty()){
                    showMessage("Please fill everything");
                }else{

                    Bundle bundle = getIntent().getExtras();
                    String date = bundle.getString("date");
                    dailyExerciseClass.setPart(part.getSelectedItem().toString());
                    if (part.getSelectedItem().toString().equals("Upper Body")) {

                        if (upper.getSelectedItem().toString().equals("Barbell Bench Press")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/BarbellBenchPress.mp4?alt=media&token=fbaed264-ab5b-4cd9-946c-d20bdd13691c");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Machine Inclined Chest Press")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Machine%20inclined%20chest%20press.mp4?alt=media&token=92598422-709d-47d4-819c-7b13e58d7d64");
                            dailyExerciseClass.setAlternative("Dumbbell Incline Chest Press");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20%20Dumbbell%20Biceps%20Curl%20-%20Arm%20Workout.mp4?alt=media&token=4fec62ab-e5de-4503-ab76-17fc9e728f1b");
                        } else if (upper.getSelectedItem().toString().equals("Dumbbell Chest Fly")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Dumbbell%20Chest%20Fly.mp4?alt=media&token=08d51030-daac-4018-b936-247c4292498a");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Lat Pull Down")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Wide-Grip%20Lat%20Pulldown%20_%20Back%20Exercise%20Guide.mp4?alt=media&token=c02fa428-fac0-43bf-9ce1-e017aa69d878");
                            dailyExerciseClass.setAlternative("Pull Ups");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Perfect%20Pullup.mp4?alt=media&token=6804a36f-a68f-45f6-987e-58265bdf13c7");
                        } else if (upper.getSelectedItem().toString().equals("Dead Lift")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/deadlift.mp4?alt=media&token=52ca189b-8aca-415a-848c-7b4b261679b4");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Low Rows")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Seated%20Cable%20Row%20_%20Exercise%20Guide.mp4?alt=media&token=f764c1d3-db1f-46d7-b208-dcecff34566f");
                            dailyExerciseClass.setAlternative("Dumbell Shoulder press");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Dumbbell%20Shoulder%20Press.mp4?alt=media&token=214e34fd-f782-4815-9194-7fad7a1f2b0a");
                        } else if (upper.getSelectedItem().toString().equals("Shoulder Press")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/shoulder%20press.mp4?alt=media&token=5cf67c96-2374-4554-860f-37338c07c7d8");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (upper.getSelectedItem().toString().equals("Side Raises")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/side%20raise.mp4?alt=media&token=a7380e88-ce83-4515-9eed-bd2f3dc979ae");
                            dailyExerciseClass.setAlternative("Dumbell Curls");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20%20Dumbbell%20Biceps%20Curl%20-%20Arm%20Workout.mp4?alt=media&token=4fec62ab-e5de-4503-ab76-17fc9e728f1b");
                        } else if (upper.getSelectedItem().toString().equals("EZ Bar Curls(Biceps)")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20to%20Do%20a%20Barbell%20Curl%20_%20Arm%20Workout.mp4?alt=media&token=f558ef34-b9b3-44d6-bcc5-7038619cf5ce");
                            dailyExerciseClass.setAlternative("Dumbell Triceps Extension");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Dumbbell%20Tricep%20Extension%20-%20Arm%20Workout.mp4?alt=media&token=33c003de-6f03-4864-a96a-ace29bee98a7");
                        } else {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20To_%20Rope%20Push-Down.mp4?alt=media&token=f9c3914e-ed1b-4aa2-913c-ac848cbfade8");
                            dailyExerciseClass.setAlternative("Lying Dumbbell Tricep Extensions");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20To-%20Laying%20Dumbbell%20Tricep%20Extension.mp4?alt=media&token=da677df8-4932-4664-afa5-163c49bb8497");
                        }
                        String string = "0";
                        dailyExerciseClass.setName(upper.getSelectedItem().toString());
                        dailyExerciseClass.setDate(date);
                        dailyExerciseClass.setClassId(classId);
                        dailyExerciseClass.setSet(set.getText().toString());
                        dailyExerciseClass.setReps(reps.getText().toString());
                        dailyExerciseClass.setWeight(weight.getText().toString());
                        dailyExerciseClass.setStatus(string);

                        String key = refff.push().getKey();
                        dailyExerciseClass.setId(key);
                        refff.child(key).setValue(dailyExerciseClass);
                    } else{

                        if (lower.getSelectedItem().toString().equals("Barbell Squats(Squat Rack)")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20to%20Do%20a%20Squat%20_%20Gym%20Workout.mp4?alt=media&token=8ca58602-f2ce-4cba-93a5-8fc8391258ae");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (lower.getSelectedItem().toString().equals("Leg Press(Machine)")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/leg%20presas%20machine.mp4?alt=media&token=90c354c9-a84b-4726-8d8b-c6ff2c684d02");
                            dailyExerciseClass.setAlternative("Barbell Step-Ups");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Barbell%20Step-ups.mp4?alt=media&token=49595e11-4bd4-499d-8864-c7641b46458e");
                        } else if (lower.getSelectedItem().toString().equals("Leg Extension(Machine)")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/leg%20extension.mp4?alt=media&token=30b6eb48-d94c-4032-87ed-96c7f185fc26");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (lower.getSelectedItem().toString().equals("Leg Curls(Machine)")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/LEG%20CURL.mp4?alt=media&token=3b4cf9fe-a734-47ef-ac4c-91a45159b081");
                            dailyExerciseClass.setAlternative("Barbell Step-Ups");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Barbell%20Step-ups.mp4?alt=media&token=49595e11-4bd4-499d-8864-c7641b46458e");
                        } else if (lower.getSelectedItem().toString().equals("Angled Leg Press")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/angled%20leg%20press.mp4?alt=media&token=f8839796-a468-4b09-a090-ce5db268acf2");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        } else if (lower.getSelectedItem().toString().equals("Inner Abduction Machine")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Hip%20Abduction%20and%20Adduction%20Machine.mp4?alt=media&token=6084898b-c26e-4d96-8648-d3528791a6de");
                            dailyExerciseClass.setAlternative("Forwards and Backward Band Walks");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Resistance%20Band%20Exercises.mp4?alt=media&token=b953038e-bc22-4cfa-b4be-5c63463cb91e");
                        } else if (lower.getSelectedItem().toString().equals("Outer Abduction Machine")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Hip%20Abduction%20and%20Adduction%20Machine.mp4?alt=media&token=6084898b-c26e-4d96-8648-d3528791a6de");
                            dailyExerciseClass.setAlternative("Forwards and Backward Band Walks");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Resistance%20Band%20Exercises.mp4?alt=media&token=b953038e-bc22-4cfa-b4be-5c63463cb91e");
                        } else if (lower.getSelectedItem().toString().equals("Dumbbell Lunges")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/How%20To_%20Dumbbell%20Stepping%20Lunge.mp4?alt=media&token=f983b685-47d1-484e-b7a4-7e8d2336905f");
                            dailyExerciseClass.setAlternative("Dumbell Calf Raise");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20How%20to%20Do%20a%20Calf%20Raise.mp4?alt=media&token=c43f8ba3-1a11-4117-a639-beb838963de9");
                        } else if (lower.getSelectedItem().toString().equals("Smith Machine Calf Raises")) {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/smith%20machine%20calf%20raise.mp4?alt=media&token=1b42d065-62fd-468c-b1e7-7473947b6e57");
                            dailyExerciseClass.setAlternative("Barbell Back Squat");
                            dailyExerciseClass.setAlternativeURL("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/Alt%20Barbell%20Back%20Squat.mp4?alt=media&token=b2445c37-5a1d-4648-9e83-4cde57fc77b2");
                        } else {
                            dailyExerciseClass.setUrl("https://firebasestorage.googleapis.com/v0/b/thesisfinal-7f58a.appspot.com/o/smith%20machine%20squats.mp4?alt=media&token=5ced24ce-3dc9-48b6-b204-3a80f4005650");
                            dailyExerciseClass.setAlternative("N/A");
                            dailyExerciseClass.setAlternativeURL("N/A");
                        }
                        String string = "0";
                        dailyExerciseClass.setName(lower.getSelectedItem().toString());
                        dailyExerciseClass.setDate(date);
                        dailyExerciseClass.setClassId(classId);
                        dailyExerciseClass.setSet(set.getText().toString());
                        dailyExerciseClass.setReps(reps.getText().toString());
                        dailyExerciseClass.setWeight(weight.getText().toString());
                        dailyExerciseClass.setStatus(string);

                        String key = refff.push().getKey();
                        dailyExerciseClass.setId(key);
                        refff.child(key).setValue(dailyExerciseClass);
                    }
                }
                dialog.dismiss();
            }

        });
        dialog.show();
    }
    private void restDay(){

    }
    private void addBmi(String date, String userId){
        bmiClass.setDate(date);
        bmiClass.setType(1);
        bmiClass.setHeight(0);
        bmiClass.setWeight(0);
        bmiClass.setUserId(userId);
        bmiClass.setBmi(0.00);
        bmiClass.setStatus("N/A");
        String key = bmiCheck.push().getKey();
        bmiClass.setId(key);
        bmiCheck.child(key).setValue(bmiClass);

    }
    private void addRestday(final String day){
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("classId");
        resreff = FirebaseDatabase.getInstance().getReference();
        resreff.child("restday").orderByChild("classId").equalTo(message).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String y = dataSnapshot.child("day").getValue(String.class);
                if(y.equals(day)){
                    copy.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.INVISIBLE);
                    copy2.setVisibility(View.INVISIBLE);
                    showMessage("Today is trainee's rest day");
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
    private void addRestday1(final String day){
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("classId");
        resreff = FirebaseDatabase.getInstance().getReference();
        resreff.child("restday").orderByChild("classId").equalTo(message).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String y = dataSnapshot.child("day").getValue(String.class);
                if(y.equals(day)){
                    copy.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.INVISIBLE);
                    copy2.setVisibility(View.INVISIBLE);
                    showMessage("Today is trainee's rest day");
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
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }
}
