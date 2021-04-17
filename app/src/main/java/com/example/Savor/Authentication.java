//Starting file where user logs in/create new account
package com.example.Savor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Authentication extends AppCompatActivity {
    private ImageView logoImage;
    private EditText edtEmail;
    private EditText edtPass;
    private Button btnLogin;
    private Button btnCreate;

    private FirebaseAuth mAuth;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //force night mode off
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);

        // create firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        logoImage = (ImageView) findViewById(R.id.logoImage);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        //set Logo Image
        logoImage.setImageResource(R.drawable.savor_icon);

        //button log in listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();

            }
        });

        //button create new account listener
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authentication.this, NewAccount.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Toast.makeText(getApplicationContext(), "There is no back action", Toast.LENGTH_SHORT).show();
    }

    //log in helper function
    private void loginUser() {

        //parse user input
        if (edtEmail.getText().toString().trim().isEmpty()){
            email = "";
        }
        else {
            email = edtEmail.getText().toString().trim();
        }
        if (edtPass.getText().toString().trim().isEmpty()) {
            password = "";
        }
        else{
            password = edtPass.getText().toString().trim();
        }

        // log in user using firebase method
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Authentication.this, Spoonacular.class);
                    startActivity(intent);
                    Toast.makeText(Authentication.this, "Logged In Successfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Authentication.this, "Incorrect Login Information", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
