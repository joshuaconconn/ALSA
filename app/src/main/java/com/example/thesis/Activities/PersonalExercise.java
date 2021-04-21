package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Adapters.BmiAdapter;
import com.example.thesis.Adapters.PersonalExerciseAdapter;
import com.example.thesis.Models.BmiModel;
import com.example.thesis.Models.DailyExerciseModel;
import com.example.thesis.Models.ProgramExerciseModel;
import com.example.thesis.Models.RestDayModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.List;

public class PersonalExercise extends AppCompatActivity {
    private CalendarView mCalendarView;
    private List<BmiModel> bmiList;
    private List<RestDayModel> restdayModel;
    private List<DailyExerciseModel> deleteExer;
    private PersonalExerciseAdapter adapter;
    BmiAdapter adapterr;
    private DatabaseReference reference1, resreff, forRestdayDelete, forExerDelete, classDelete;
    Button back, backToClass, bmi, bmi2, delete;
    FirebaseAuth fAuth;
    List<String> restday;
    String userId;
    TextView id2, id22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_exercise);
        restday = new ArrayList<>();
        restdayModel = new ArrayList<>();
        deleteExer = new ArrayList<>();
        bmi2 = findViewById(R.id.bmi2);
        forRestdayDelete = FirebaseDatabase.getInstance().getReference().child("restday");
        forExerDelete = FirebaseDatabase.getInstance().getReference().child("DailyExercise");
        classDelete = FirebaseDatabase.getInstance().getReference().child("class");
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        back = findViewById(R.id.back);
        backToClass = findViewById(R.id.backToClass);
        bmi = findViewById(R.id.bmi);
        delete = findViewById(R.id.delete);
        bmiList = new ArrayList<>();
        adapterr = new BmiAdapter(bmiList);
        id2 = findViewById(R.id.id2);
        id22 = findViewById(R.id.id22);
        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Bundle bundle = getIntent().getExtras();
                int hax = bundle.getInt("hax");
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                SimpleDateFormat fmonth = new SimpleDateFormat("MM");
                int m = month + 1;
                if (hax == 1) {
                    String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                    setUser(date);
                } else {
                    String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                    openTrainee(date);
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        int hax = bundle.getInt("hax");
        if (hax == 1) {
            bmiChecker();
            back.setVisibility(View.INVISIBLE);
            bmi2.setVisibility(View.INVISIBLE);
            backToClass.setVisibility(View.VISIBLE);
            bmi.setVisibility(View.VISIBLE);
            id2.setVisibility(View.INVISIBLE);
            id22.setVisibility(View.VISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }else {
            bmiChecker1();
            back.setVisibility(View.VISIBLE);
            backToClass.setVisibility(View.INVISIBLE);
            bmi.setVisibility(View.INVISIBLE);
            bmi2.setVisibility(View.VISIBLE);
            id2.setVisibility(View.VISIBLE);
            id22.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.VISIBLE);
            deleteRest();
            setDeleteExer();
        }
        backToClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), ClassList.class);
                startActivity(TraineeHome);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                startActivity(TraineeHome);
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        bmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmiList1();
            }
        });
        bmi2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmiList1();
            }
        });
    }

    public void openTrainee(String date) {
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("classId");
        Intent TraineeHome = new Intent(getApplicationContext(), TraineeExerView.class);
        TraineeHome.putExtra("classId", userId);
        TraineeHome.putExtra("hax", 0);
        TraineeHome.putExtra("date", date);
        startActivity(TraineeHome);
        finish();
    }



    public void setUser(String date) {
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("classId");
        Intent TraineeHome = new Intent(getApplicationContext(), PersonalExerciseAdd.class);
        TraineeHome.putExtra("classId", id);
        TraineeHome.putExtra("date", date);
        TraineeHome.putExtra("hax", 1);
        startActivity(TraineeHome);
        finish();
    }
    private void bmiChecker(){
        int i = 0;
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("classId");
        reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child("bmi").orderByChild("userId").equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                if(model.type.equals(1)){
                    bmiList.add(dataSnapshot.getValue(BmiModel.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                int index = getItemIndex(model);
                bmiList.set(index, model);
                adapterr.notifyItemChanged(index);
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
    private void bmiChecker1(){
        int i = 0;
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("classId");
        reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child("bmi").orderByChild("userId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                if(model.type.equals(1)){
                    bmiList.add(dataSnapshot.getValue(BmiModel.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                int index = getItemIndex(model);
                bmiList.set(index, model);
                adapterr.notifyItemChanged(index);
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
    private int getItemIndex(BmiModel user){
        int index = -1;

        for (int i = 0; i < bmiList.size(); i++){
            if(bmiList.get(i).userId.equals(user.userId)){
                index = i;
                break;
            }
        }
        return index;
    }
    private void bmiList1(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(PersonalExercise.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_bmi_list, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        Button button = mView.findViewById(R.id.button3);
        RecyclerView recyclerView = mView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapterr);
        dialog.show();
    }

    private void deleteRest(){
        reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child("restday").orderByChild("classId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RestDayModel model = dataSnapshot.getValue(RestDayModel.class);
                restdayModel.add(dataSnapshot.getValue(RestDayModel.class));
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
    private void setDeleteExer(){
        reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child("DailyExercise").orderByChild("classId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                deleteExer.add(dataSnapshot.getValue(DailyExerciseModel.class));
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
    private void clearAll(){
        showMessage("Exer = " + deleteExer.size());
        for(int j = 0; j < deleteExer.size(); j++){
            forExerDelete.child(deleteExer.get(j).id).removeValue();
        }
        for(int i = 0; i < restdayModel.size(); i++){
            forRestdayDelete.child(restdayModel.get(i).id).removeValue();
        }
        classDelete.child(userId).removeValue();
        Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
        startActivity(TraineeHome);
        finish();

    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}
