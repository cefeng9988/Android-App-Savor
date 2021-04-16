//page for registration of a new user flow
package com.example.nutrition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NewAccount extends AppCompatActivity {

    private EditText edtName, edtAge, edtEmail, edtPass;
    private Button btnCreate;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account);

        mAuth = FirebaseAuth.getInstance();

        edtName = (EditText) findViewById(R.id.edtName);
        edtAge = (EditText) findViewById(R.id.edtAge);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        //create new account, go to helper function below
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

            }
        });

        //if user already has an account, directly log in
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(NewAccount.this, Authentication.class);
                    startActivity(intent);

            }
        });

    }

    //required registration fields
    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String age = edtAge.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (name.isEmpty()){
            edtName.setError("Name required");
            edtName.requestFocus();
            return;
        }
        if (age.isEmpty()){
            edtAge.setError("Age required");
            edtAge.requestFocus();
            return;
        }

        if (email.isEmpty()){
            edtEmail.setError("Email required");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Please provide valid email");
            edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            edtPass.setError("Password required");
            edtPass.requestFocus();
            return;
        }

        //firebase to add on new user with entered details/fields
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, age, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        //preset preferences to default for user if they do not have preferences set already
                                        UsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                //default number of recipes shown
                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(userId).child("Recipes Displayed").setValue("5");

                                                //default vegan
                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(userId).child("Vegan").setValue("False");
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                        //upon successful registration, route to home page
                                        Intent intent = new Intent(NewAccount.this, Spoonacular.class);
                                        startActivity(intent);
                                        Toast.makeText(NewAccount.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        //failed registration Toast
                                        Toast.makeText(NewAccount.this, "Failed to register!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(NewAccount.this, "Failed to register", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ); }
}
