    package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.Class.User;
import com.example.thesis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText userEmail, userPassword, userPassword2, userName;

     private ProgressBar loadingProgress;
     TextView mRegBtn;
     private Button regBtn;
     private FirebaseAuth mAuth;
     FirebaseFirestore fStore;
     String userId;
     Integer numtype, Type;
     FirebaseDatabase fData;
     Spinner spinner;
     DatabaseReference reff;
     User user;
     LoginPage loginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseUser userr = FirebaseAuth.getInstance().getCurrentUser();
        if (userr != null) {
            userId = userr.getUid();
            showMessage("Logging-in, Please Wait!");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Type = dataSnapshot.child("type").getValue(Integer.class);
                    if(Type.equals(0)){
                        Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                        startActivity(TraineeHome);
                        finish();

                    }if(Type.equals(1)){
                        Intent InsHome = new Intent(getApplicationContext(), InstructorHome.class);
                        startActivity(InsHome);
                        finish();
                    }else{
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {

        }

        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.progressBar);
        loadingProgress.setVisibility(View.INVISIBLE);
        regBtn = findViewById(R.id.regBtn);
        mRegBtn = findViewById(R.id.textView);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.choice,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        fStore = FirebaseFirestore.getInstance();
        fData = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reff = FirebaseDatabase.getInstance().getReference().child("user");
        user = new User();

        regBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
               regBtn.setVisibility(View.INVISIBLE);
               loadingProgress.setVisibility(View.VISIBLE);
               final String email = userEmail.getText().toString();
               final String password = userPassword.getText().toString();
               final String password2 = userPassword2.getText().toString();
               final String name = userName.getText().toString();

               String cs = spinner.getSelectedItem().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(password2)) {
                    showMessage("Please fill all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{
                    if(cs.equals("Trainee")){
                        numtype = 0;
                    }else if(cs.equals("Instructor")){
                        numtype = 1;
                    }
                    CreateUserAccount(email,name, password,numtype);
                }
            }
        } );
        mRegBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent main = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(main);
                finish();
            }
        });
    }
    private void CreateUserAccount(final String email, final String name,final String password, final Integer numtype) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userId = mAuth.getCurrentUser().getUid();
                            showMessage("Account Created");
                            String id = userId;
                            user.setId(id);
                            user.setName(name);
                            user.setEmail(email);
                            user.setType(numtype);
                            reff.child(userId).setValue(user);
                            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        userId = mAuth.getCurrentUser().getUid();
                                        showMessage("Logged in Successfully!");
                                        //For Knowing user type and designate them to respective HomePage
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Type = dataSnapshot.child("type").getValue(Integer.class);
                                                if(Type.equals(0)){
                                                    Intent TraineeHome = new Intent(getApplicationContext(), TraineeHome.class);
                                                    startActivity(TraineeHome);
                                                    finish();
                                                }if(Type.equals(1)){
                                                    Intent InsHome = new Intent(getApplicationContext(), InstructorHome.class);
                                                    startActivity(InsHome);
                                                    finish();
                                                    }
                                                else{
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }else{
                                        showMessage("Email or Password is Incorrect!");
                                    }
                                }
                            });
                        }else{
                            showMessage("Account Creation Failed " + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    private void updateUserInfo(String name, FirebaseUser currentUser) {
        // Profile Pic if needed
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                setDisplayName(name).build();
         currentUser.updateProfile(profileUpdate)
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                         }
                     }
                 });
    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String chs = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
