package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    TextView userTV;
    Button addProjectButton, seeProjectsButton, signOutButton;
    FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        userTV=findViewById(R.id.user_text_view);
        addProjectButton=findViewById(R.id.add_project_button);
        seeProjectsButton=findViewById(R.id.see_projects_button);
        signOutButton=findViewById(R.id.sign_out_button);

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser != null;
        String email= firebaseUser.getEmail();

        String user="Welcome\n"+email+"\n"+firebaseUser.getUid();
        userTV.setText(user);

        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddNewProjectActivity.class));
            }
        });
        seeProjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,ViewProjectsActivity.class));
            }
        });


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(MainActivity.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    Log.e(TAG, "onAuthStateChanged: successfully signed in "+user.getEmail());
                    Toast.makeText(MainActivity.this,"Successfully signed in with "+user, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.e(TAG, "onAuthStateChanged: successfully signed out");
                    Toast.makeText(MainActivity.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null)
        {
            Log.e(TAG, "onStart: current user null" );
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}