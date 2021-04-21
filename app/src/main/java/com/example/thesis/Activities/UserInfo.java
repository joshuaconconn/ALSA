package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import com.example.thesis.Adapters.BmiAdapter;
import com.example.thesis.Adapters.InjuryAdapter;
import com.example.thesis.Models.BmiModel;

import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Class.BmiClass;
import com.example.thesis.Class.InjuryClass;
import com.example.thesis.Models.InjuryModel;
import com.example.thesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfo extends AppCompatActivity {
    TextView fname, femail;
    Button bmiUpdate, injury, back;
    DatabaseReference reff, reference11, reference1, reference111;
    BmiClass bmiClass;
    FirebaseAuth fAuth;
    String userId;
    InjuryClass injuryClass;
    private RecyclerView bmiView, injuryView;
    private List<BmiModel> result;
    private BmiAdapter adapter;
    private List<InjuryModel> results;
    private InjuryAdapter adapterr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        //--------FOR AUTH----------------------------------------------------------=
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        //--------FOR BMI------------------------------------------------------------
        bmiView = findViewById(R.id.bmiView);
        bmiView.setHasFixedSize(true);
        result = new ArrayList<>();
        result.clear();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        bmiView.setLayoutManager(llm);
        adapter = new BmiAdapter(result);
        bmiView.setAdapter(adapter);
        //--------FOR INJURY REPORT---------------------------------------------------

        injury = findViewById(R.id.injury);

        reference11 = FirebaseDatabase.getInstance().getReference().child("injury");
        injuryView = findViewById(R.id.injuryView);
        injuryView.setHasFixedSize(true);
        injuryClass = new InjuryClass();
        results = new ArrayList<>();
        results.clear();
        LinearLayoutManager llmm = new LinearLayoutManager(this);
        llmm.setOrientation(LinearLayoutManager.VERTICAL);
        injuryView.setLayoutManager(llmm);
        adapterr = new InjuryAdapter(results);
        injuryView.setAdapter(adapterr);
        //-----------------------------------------------------------------------------

        fname = findViewById(R.id.name2);
        femail = findViewById(R.id.name3);
        bmiUpdate = findViewById(R.id.bmiupdate);
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("id");
        reff = FirebaseDatabase.getInstance().getReference().child("bmi");
        bmiClass = new BmiClass();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
        final String dateToday = format.format(today);
        //---------------------------------------------------------------------------
        setProfile();
        updateList();
        updateList1();
        back = findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InstructorHome = new Intent(getApplicationContext(), TraineeHome.class);
                startActivity(InstructorHome);
                finish();
            }
        });
        injury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInjury();
            }
        });

        bmiUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserInfo.this);
                final View mView = getLayoutInflater().inflate(R.layout.pop_up_bmi, null);
                final EditText height = mView.findViewById(R.id.height);
                final EditText weight = mView.findViewById(R.id.weight);
                mBuilder.setView(mView);
                Button bmiBtn = mView.findViewById(R.id.bmiupdate);
                final AlertDialog dialog = mBuilder.create();

                bmiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //int bmi = bmi.setText(String.format("%.2f", a));
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

                            showMessage("BMI = " + bmi);
                            if (bmi < 18.5) {
                                status = "Underweight";
                            } else if (bmi < 25) {
                                status = "Normal";
                            } else if (bmi < 30) {
                                status = "Overweight";
                            } else {
                                status = "Obese";
                            }
                            Bundle bundle = getIntent().getExtras();
                            String message = bundle.getString("id");
                            bmiClass.setDate(dateToday);
                            bmiClass.setType(0);
                            bmiClass.setHeight(height1);
                            bmiClass.setWeight(weight1);
                            bmiClass.setUserId(message);
                            bmiClass.setBmi(bmi);
                            bmiClass.setStatus(status);
                            String key = reff.push().getKey();
                            bmiClass.setId(key);
                            reff.child(key).setValue(bmiClass);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }
    private void setInjury(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserInfo.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_injury, null);
        final TextView InjuryName = mView.findViewById((R.id.InjuryName));
        final Spinner recovery = mView.findViewById(R.id.recovery);
        final Spinner recoveryPart = mView.findViewById(R.id.recoveryPart);
        final Button injuryUpdate = mView.findViewById(R.id.injuryupdate);
        ArrayAdapter<CharSequence> adapterspinn = ArrayAdapter.createFromResource(this, R.array.injury,
                android.R.layout.simple_spinner_item);
        adapterspinn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapterspinnn = ArrayAdapter.createFromResource(this, R.array.injuryPart,
                android.R.layout.simple_spinner_item);
        adapterspinnn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recovery.setAdapter(adapterspinn);
        recoveryPart.setAdapter(adapterspinnn);
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        injuryUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int day = day + 1;
                DatePicker datepicker = mView.findViewById(R.id.datepicker);
                int year = datepicker.getYear();
                int day = datepicker.getDayOfMonth();
                int month = datepicker.getMonth() + 1;
                Bundle bundle = getIntent().getExtras();
                String message = bundle.getString("id");
                String date = year + "-" + month + "-" + day;
                injuryClass.setDate(date);
                injuryClass.setUserId(message);
                injuryClass.setRecovery(recovery.getSelectedItem().toString());
                injuryClass.setPart(recoveryPart.getSelectedItem().toString());
                injuryClass.setName(InjuryName.getText().toString());
                String key= reference11.push().getKey();
                injuryClass.setId(key);
                reference11.child(key).setValue(injuryClass);
                dialog.dismiss();
            }
        });
    }
    private void setProfile(){
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("id");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                fname.setText("Name: " + name);
                femail.setText("Email: " + email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    private void updateList() {
        int i = 0;
        reference111 = FirebaseDatabase.getInstance().getReference();
        reference111.child("bmi").orderByChild("userId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                if (model.type.equals(0)) {
                    result.add(dataSnapshot.getValue(BmiModel.class));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
                int index = getItemIndex(model);
                adapter.notifyItemChanged(index);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                BmiModel model = dataSnapshot.getValue(BmiModel.class);
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

    private int getItemIndex(BmiModel user){
        int index = -1;

        for (int i = 0; i < result.size(); i++){
            if(result.get(i).userId.equals(user.userId)){
                index = i;
                break;
            }
        }
        return index;
    }
    private void updateList1() {
        int i = 0;
        reference111 = FirebaseDatabase.getInstance().getReference();
        reference111.child("injury").orderByChild("userId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                InjuryModel model = dataSnapshot.getValue(InjuryModel.class);
                results.add(dataSnapshot.getValue(InjuryModel.class));
                adapterr.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                InjuryModel model = dataSnapshot.getValue(InjuryModel.class);
                int index = getItemIndex1(model);
                results.set(index, model);
                adapterr.notifyItemChanged(index);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                InjuryModel model = dataSnapshot.getValue(InjuryModel.class);
                int index = getItemIndex1(model);
                results.remove(index);
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
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                setUser(item.getGroupId());
                break;

        }
        return super.onContextItemSelected(item);
    }
    public void setUser(final int position){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserInfo.this);
        final View mView = getLayoutInflater().inflate(R.layout.pop_up_injury_update, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        final Spinner recovery1 = mView.findViewById(R.id.recovery1);
        Button injuryupdate1 = mView.findViewById(R.id.injuryupdate1);
        ArrayAdapter<CharSequence> adapterspin = ArrayAdapter.createFromResource(this, R.array.injury,
                android.R.layout.simple_spinner_item);
        adapterspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recovery1.setAdapter(adapterspin);

        injuryupdate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InjuryModel user = results.get(position);
                user.recovery = recovery1.getSelectedItem().toString();

                Map<String, Object> userValues = user.toMap();
                Map<String, Object> newUser = new HashMap<>();

                newUser.put(user.id, userValues);

                reference11.updateChildren(newUser);
            }
        });
    }
    private int getItemIndex1(InjuryModel user){
        int index = -1;

        for (int i = 0; i < results.size(); i++){
            if(results.get(i).id.equals(user.id)){
                index = i;
                break;
            }
        }
        return index;
    }
}
