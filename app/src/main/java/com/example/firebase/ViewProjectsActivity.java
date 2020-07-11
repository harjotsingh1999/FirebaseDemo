package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewProjectsActivity extends AppCompatActivity {

    TextView emptyTextView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<ProjectData> projectDataArrayList;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDatabaseReference;
    FirebaseUser firebaseUser;
    private static final String TAG = "ViewProjectsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_projects);


        emptyTextView=findViewById(R.id.no_projects_text_view);
        recyclerView=findViewById(R.id.projects_recycler_view);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        projectDataArrayList=new ArrayList<>();

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        userDatabaseReference=firebaseDatabase.getReference("Users/"+firebaseUser.getUid());

        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: datasnapshot to string "+dataSnapshot.toString() );
                if(dataSnapshot.hasChild("Projects"))
                {
                    projectDataArrayList.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.child("Projects").getChildren())
                    {
                        Log.e(TAG, "onDataChange: datasnapshot key and to string "+dataSnapshot1.getKey()+"\n"+dataSnapshot1.toString() );
                        String projTitle= (String) dataSnapshot1.child("title").getValue();
                        String projDesc= (String) dataSnapshot1.child("description").getValue();
                        String projImageUrl= (String) dataSnapshot1.child("imageUrl").getValue();
                        projectDataArrayList.add(new ProjectData(projTitle,projDesc,projImageUrl));
                    }
                    setUpRecyclerView();
                }
                else
                {
                    emptyTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled: database error"+databaseError.toString());
            }
        });

    }
    private void setUpRecyclerView()
    {
        ProjectsAdapter adapter=new ProjectsAdapter(this,projectDataArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }
}