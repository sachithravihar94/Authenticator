package com.example.authenticator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;

public class ProfileScreen extends AppCompatActivity {

    Button btnLogout;
    FirebaseAuth fAuth;
    TextView firstName,lastName,displayEmail;
    ImageView imageView;
    FirebaseFirestore fStore;
    String uid;

    private final int IMAGE_REQUEST_ID = 27;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        firstName = findViewById(R.id.txtFirstName);
        lastName = findViewById(R.id.txtLastName);
        displayEmail = findViewById(R.id.txtEmailDisplay);

        //retrieving data from firebase
        uid = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(uid);
        documentReference.addSnapshotListener(ProfileScreen.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstName.setText(documentSnapshot.getString("FirstName"));
                lastName.setText(documentSnapshot.getString("LastName"));
                displayEmail.setText(documentSnapshot.getString("Email"));
            }
        });

        //Logout button
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginScreen.class));
            }

        });


    }


}