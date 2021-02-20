package com.example.authenticator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginScreen extends AppCompatActivity {

    EditText lgEmail, lgPassword;
    Button btnLogin2;
    FirebaseAuth fAuth;


    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        //Assigning XML resources
        lgEmail = findViewById(R.id.txtEmail);
        lgPassword = findViewById(R.id.txtPassword);
        btnLogin2 = findViewById(R.id.btnLogin2);
        loginButton = findViewById(R.id.login_button);

        fAuth = FirebaseAuth.getInstance();

        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validation
                String getEmail = lgEmail.getText().toString().trim();
                String getPassword = lgPassword.getText().toString().trim();

                //Validation
                if (TextUtils.isEmpty(getEmail)){
                    lgEmail.setError("Email address is required");
                    return;
                }

                if (TextUtils.isEmpty(getPassword)){
                    lgPassword.setError("Password is required");
                    return;
                }

                if (getPassword.length() < 6 ){
                    lgPassword.setError("Password must contain at least 6 characters");
                    return;
                }


                //Authenticating the user
                fAuth.signInWithEmailAndPassword(getEmail,getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginScreen.this, "Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),ProfileScreen.class));
                        } else {
                            Toast.makeText(LoginScreen.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        //Login using Facebook
        callbackManager = CallbackManager.Factory.create();

        //Assigning additional permissions first
        loginButton.setPermissions(Arrays.asList("user_gender,user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Demo","Login successful!");
            }

            @Override
            public void onCancel() {
                Log.d("Demo","Login canceled!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Demo","Login error!");
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields","gender,name,id,first_name,last_name");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }

    //Checking whether the user has already logged in

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut();
            }
        }
    };

    //Stop tracking when we destroy the activity

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

}