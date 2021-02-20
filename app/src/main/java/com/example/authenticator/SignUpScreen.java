package com.example.authenticator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.bind.ObjectTypeAdapter;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText fName, lName, email, password;
    Button btnSignUp;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        //Assigning XML resources
        fName = findViewById(R.id.txtFName);
        lName = findViewById(R.id.txtLName);
        email = findViewById(R.id.txtEmail2);
        password = findViewById(R.id.txtPassword2);
        btnSignUp = findViewById(R.id.btnFSignUp);

        //Getting the current firebase instance
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Checking whether the user has already logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),ProfileScreen.class));
            finish();
        }

        //On Click event (Signing up)
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString().trim();
                String getPassword = password.getText().toString().trim();
                String getFName = fName.getText().toString().trim();
                String getLName = lName.getText().toString().trim();

                //Validation
                if (TextUtils.isEmpty(getEmail)){
                    email.setError("Email address is required");
                    return;
                }

                if (TextUtils.isEmpty(getPassword)){
                    password.setError("Password is required");
                    return;
                }

                if (getPassword.length() < 6 ){
                    password.setError("Password must contain at least 6 characters");
                    return;
                }


                //User Registration
                fAuth.createUserWithEmailAndPassword(getEmail,getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpScreen.this, "A new user has been created", Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();


                            //Storing other sign up details
                            DocumentReference documentReference= fStore.collection("users").document(userID);

                            Map<String, Object> user = new HashMap<>();
                            user.put("FirstName",getFName);
                            user.put("LastName",getLName);
                            user.put("Email",getEmail);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User profile has been created for"+ userID);
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),LoginScreen.class));
                        } else {
                            Toast.makeText(SignUpScreen.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}