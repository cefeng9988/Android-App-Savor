//an old test file, not in use
package com.example.nutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText edt1, edt2, edt3, edt4, edtMusic, edtServing1;
    Button btnSubmit, btnLogout;
    DatabaseReference firebase;
    Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Firebase connected", Toast.LENGTH_LONG).show();

        edt1 = (EditText) findViewById(R.id.edt1);
        edt2 = (EditText) findViewById(R.id.edt2);
        edt3 = (EditText) findViewById(R.id.edt3);
        edt4 = (EditText) findViewById(R.id.edt4);
        edtServing1 = (EditText) findViewById(R.id.edtServing1);
        edtMusic = (EditText) findViewById(R.id.edtMusic);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        recipe = new Recipe();
        firebase = FirebaseDatabase.getInstance().getReference().child("Recipe");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.setIngredient1(edt1.getText().toString().trim());
                recipe.setIngredient2(edt2.getText().toString().trim());
                recipe.setIngredient3(edt3.getText().toString().trim());
                recipe.setIngredient4(edt4.getText().toString().trim());

                if (edtServing1.getText().toString().trim().equals("")) {
                    recipe.setServing(0);
                }
                else{
                    recipe.setServing(Integer.parseInt(edtServing1.getText().toString().trim()));
                }

                recipe.setMusic(edtMusic.getText().toString().trim());
                firebase.push().setValue(recipe);
                //firebase.child("recipe1").setValue(recipe);
                Toast.makeText(getApplicationContext(), "Recipe Submitted", Toast.LENGTH_LONG).show();

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Authentication.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            }
        });


    }
}