package com.example.interviewlogapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {
    EditText usernameInput, passwordInput;
    Button loginButton;
    String userID, userType,userName;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String TAG = "LoginPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameInput= findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser()!= null) {
            loadPanel();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim(),
                        password = passwordInput.getText().toString();
                if(TextUtils.isEmpty(username)){
                    usernameInput.setError("The User Name field is empty");
                }
                if(TextUtils.isEmpty(password)){
                    usernameInput.setError("The Password field is empty");
                }
                fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPage.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            loadPanel();
                        }
                        else{
                            Toast.makeText(LoginPage.this, "Error !"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Features to be added, Will send email if there's any issues", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



    }

    public void loadPanel(){

        FirebaseUser user = fAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userType=value.getString("userType");
                userName = value.getString("name");
                Log.d(TAG, "The user type is "+ userType + " and user name is "+ userName);
                if (userType.equals("researcher")){
                    Intent intent = new Intent(LoginPage.this,ResearcherPanel.class);
                    intent.putExtra("researcherName", userName);
                    startActivity(intent);
                    finish();
                }
                else if (userType.equals("user")){
                    startActivity(new Intent(getApplicationContext(), UserPanel.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginPage.this, "The user type was not successfully defined, please contact admin", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}