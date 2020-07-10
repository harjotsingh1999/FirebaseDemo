package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    EditText email, password, confirmPassword;
    Button signUp;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        email=findViewById(R.id.edit_text_email);
        password=findViewById(R.id.edit_text_password);
        confirmPassword=findViewById(R.id.edit_text_confirm_password);
        signUp=findViewById(R.id.signup_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailString, passwordString, confirmPasswordString;
                emailString=email.getText().toString();
                passwordString=password.getText().toString();
                confirmPasswordString=confirmPassword.getText().toString();
                if (passwordString.equals(confirmPasswordString) &&
                        !passwordString.equals("") &&
                        !emailString.equals("")) {
                    mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.e(TAG, "createUserWithEmail:success");
                                        Toast.makeText(SignUpActivity.this, "Sign Up successful.", Toast.LENGTH_SHORT).show();

                                        UserData userData=new UserData(emailString,passwordString);
                                        databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.e(TAG, "onComplete: user added to database with user id "+mAuth.getCurrentUser().getUid());
                                                        Toast.makeText(SignUpActivity.this, "User added to database", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onComplete: user could not be added to database with user id "+mAuth.getCurrentUser().getUid());
                                                Toast.makeText(SignUpActivity.this, "User could not be added to database", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Please fill the details correctly",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
