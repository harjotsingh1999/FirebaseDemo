package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    TextView userTV;
    Button addProjectButton, seeProjectsButton;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        userTV=findViewById(R.id.user_text_view);
        addProjectButton=findViewById(R.id.add_project_button);
        seeProjectsButton=findViewById(R.id.see_projects_button);

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