package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddNewProjectActivity extends AppCompatActivity {

    private static final int GET_IMAGE_REQUEST_CODE = 10;
    EditText projectTitleET, projectDescET;
    Button selectImageButton, uploadImageButton, uploadProjectButton;
    ImageView projectImageView;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference projectsDatabaseReference, userDatabaseReference;
    FirebaseUser firebaseUser;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;

    private static final String TAG = "AddNewProjectActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);

        projectTitleET=findViewById(R.id.project_title_edit_text);
        projectDescET=findViewById(R.id.project_description_edit_text);
        selectImageButton=findViewById(R.id.select_image_button);
        uploadImageButton=findViewById(R.id.upload_image_button);
        uploadProjectButton=findViewById(R.id.upload_project_button);
        projectImageView=findViewById(R.id.project_image_view);
        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        projectsDatabaseReference=firebaseDatabase.getReference("Projects");
        userDatabaseReference=firebaseDatabase.getReference("Users/"+firebaseUser.getUid());

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getImageIntent=new Intent(Intent.ACTION_GET_CONTENT);
                getImageIntent.setType("image/*");
                startActivityForResult(getImageIntent,GET_IMAGE_REQUEST_CODE);
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri!=null) {
                    progressDialog.setTitle("Uploading Image...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final StorageReference imageReference = mStorageRef.child("images/" + firebaseUser.getUid() + System.currentTimeMillis() + ".jpg");
                    UploadTask uploadTask = (UploadTask) imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewProjectActivity.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewProjectActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return imageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageUri = task.getResult();
                                //Toast.makeText(AddNewProjectActivity.this,"Error getting image url", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onComplete: got uploaded image url" + imageUri.toString());
                            } else {
                                Toast.makeText(AddNewProjectActivity.this, "Error getting image url", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onComplete: error getting uploaded image url");
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(AddNewProjectActivity.this, "Select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String projectTitle=projectTitleET.getText().toString();
                String projectDescription=projectDescET.getText().toString();
                String imageUrl= imageUri.toString();
                if(!(imageUrl.startsWith("https://") || imageUrl.startsWith("http://")))
                {
                    Log.e(TAG, "onClick: image url or uri is "+imageUrl);
                    Toast.makeText(AddNewProjectActivity.this, "Upload the image first", Toast.LENGTH_SHORT).show();
                }
                else if(projectTitle.equals("") || projectDescription.equals(""))
                {
                    Log.e(TAG, "onClick: fields are empty" );
                    Toast.makeText(AddNewProjectActivity.this, "Please fill project details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ProjectData projectData=new ProjectData(projectTitle,projectDescription,imageUrl,firebaseUser.getUid());
                    projectsDatabaseReference.child(firebaseUser.getUid()+System.currentTimeMillis()).setValue(projectData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddNewProjectActivity.this,"Uploaded to projects database", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ProjectData projectData2=new ProjectData(projectTitle,projectDescription,imageUrl);
                    userDatabaseReference.child("Projects").child(firebaseUser.getUid()+System.currentTimeMillis()).setValue(projectData2)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddNewProjectActivity.this,"Uploaded to user database", Toast.LENGTH_SHORT).show();
                        }
                    });

                    AddNewProjectActivity.super.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GET_IMAGE_REQUEST_CODE && resultCode==Activity.RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(projectImageView);
        }
    }
}