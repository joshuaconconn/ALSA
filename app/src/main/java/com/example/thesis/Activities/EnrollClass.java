package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Class.EnrollClassClass;
import com.example.thesis.Class.RestDayClass;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EnrollClass extends AppCompatActivity {
    Button button, button2;
    TextView name, hide, hide2;
    String Type;
    FirebaseDatabase fData;
    Spinner spinner, spinner2, restday, restday1, restday2;
    DatabaseReference reff, refff;
    EnrollClassClass createClass;
    RestDayClass restClass;
    FirebaseAuth fAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eroll_class);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        spinner2 = findViewById(R.id.spinner2);
        restday = findViewById(R.id.restday);
        restday1 = findViewById(R.id.restday1);
        restday2 = findViewById(R.id.restday2);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        name = findViewById(R.id.ins);
        hide = findViewById(R.id.hide);
        hide2 = findViewById(R.id.hide2);
        spinner = findViewById(R.id.spinner);
        fData = FirebaseDatabase.getInstance();
        reff = FirebaseDatabase.getInstance().getReference().child("class");
        refff = FirebaseDatabase.getInstance().getReference().child("restday");
        createClass = new EnrollClassClass();
        restClass = new RestDayClass();
        Bundle bundle = getIntent().getExtras();
        final String instructorId = bundle.getString("instructor");
        final String traineeId = bundle.getString("trainee");
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final String dateToday = format.format(today);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ClassType,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.progLevel,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        ArrayAdapter<CharSequence> restdayNo = ArrayAdapter.createFromResource(this, R.array.restdayNo,
                android.R.layout.simple_spinner_item);
        restdayNo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restday.setAdapter(restdayNo);

        ArrayAdapter<CharSequence> restday11 = ArrayAdapter.createFromResource(this, R.array.restday,
                android.R.layout.simple_spinner_item);
        restday11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restday1.setAdapter(restday11);

        ArrayAdapter<CharSequence> restday22 = ArrayAdapter.createFromResource(this, R.array.restday,
                android.R.layout.simple_spinner_item);
        restday22.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restday2.setAdapter(restday22);


        showMessage(traineeId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(instructorId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Type = dataSnapshot.child("name").getValue(String.class);
                name.setText("You Chose Coach " + Type );
                hide.setText(Type);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference referencee = FirebaseDatabase.getInstance().getReference().child("user").child(traineeId);
        referencee.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Type = dataSnapshot.child("name").getValue(String.class);
                hide2.setText(Type);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                startActivity(TraineeHome);
                finish();
            }
        });
        restday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String restNo = restday.getSelectedItem().toString();
                if(restNo.equals("1")){
                    restday1.setVisibility(View.VISIBLE);
                    restday2.setVisibility(View.INVISIBLE);
                }else{
                    restday1.setVisibility(View.VISIBLE);
                    restday2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restNo = restday.getSelectedItem().toString();
                if(restNo.equals("1")){
                    final String cs = spinner.getSelectedItem().toString();
                    final int status = 0;
                    createClass.setLevel(spinner2.getSelectedItem().toString());
                    createClass.setInstructorName(hide.getText().toString());
                    createClass.setInstructorId(instructorId);
                    createClass.setTraineeName(hide2.getText().toString());
                    createClass.setTraineeId(userId);
                    createClass.setDate(dateToday);
                    createClass.setClassType(cs);
                    createClass.setStatus(status);
                    reff.child(userId).setValue(createClass);

                    restClass.setDay(restday1.getSelectedItem().toString());
                    restClass.setClassId(userId);
                    String key= refff.push().getKey();
                    restClass.setId(key);
                    refff.child(key).setValue(restClass);

                    Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                    startActivity(TraineeHome);
                    finish();
                }
                if(restNo.equals("2")){
                    if(restday1.getSelectedItem().toString().equals(restday2.getSelectedItem().toString())){
                        showMessage("Choose 2 Different Days");
                    }else{
                        final String cs = spinner.getSelectedItem().toString();
                        final int status = 0;
                        createClass.setLevel(spinner2.getSelectedItem().toString());
                        createClass.setInstructorName(hide.getText().toString());
                        createClass.setInstructorId(instructorId);
                        createClass.setTraineeName(hide2.getText().toString());
                        createClass.setTraineeId(userId);
                        createClass.setDate(dateToday);
                        createClass.setClassType(cs);
                        createClass.setStatus(status);
                        reff.child(userId).setValue(createClass);

                        restClass.setDay(restday1.getSelectedItem().toString());
                        restClass.setClassId(userId);
                        String key= refff.push().getKey();
                        restClass.setId(key);
                        refff.child(key).setValue(restClass);

                        restClass.setDay(restday2.getSelectedItem().toString());
                        restClass.setClassId(userId);
                        String keyy= refff.push().getKey();
                        restClass.setId(keyy);
                        refff.child(keyy).setValue(restClass);

                        Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                        startActivity(TraineeHome);
                        finish();
                    }
                }

            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

}
