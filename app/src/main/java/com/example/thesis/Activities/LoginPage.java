package com.example.thesis.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button loginbtn;
    TextView mRegBtn;
    ProgressBar progress;
    FirebaseAuth fAuth;
    String userId;
    Integer Type;
    DatabaseReference ref;
    RegisterPage registerPage;
    String testUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null){

        }
        mEmail = findViewById(R.id.regemail);
        mPassword = findViewById(R.id.password);

        loginbtn = findViewById(R.id.button);
        mRegBtn = findViewById(R.id.textView2);
        progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        //ref = FirebaseDatabase.getInstance().getReference().child("user").orderByChild("id").equalTo(id);
        //Type = dataSnapshot.child("type").getValue().toString();

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loginbtn.setVisibility(View.INVISIBLE);
                mRegBtn.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty() ) {
                    showMessage("Please fill all fields");
                    mRegBtn.setVisibility(View.VISIBLE);
                    loginbtn.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                }else{
                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                userId = fAuth.getCurrentUser().getUid();
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
                                mRegBtn.setVisibility(View.VISIBLE);
                                loginbtn.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }


        });
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), RegisterPage.class);
                startActivity(main);
                finish();
            }
        });
    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}
