package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Preferences extends AppCompatActivity {

    private EditText edtUser;
    private Button btnUpdate;


    private DatabaseReference UsersRef;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        edtUser = (EditText) findViewById(R.id.edtUser);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // Saves the id, name, ingredients, and summary of the recipe to firebase
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(userId).child("name").setValue(edtUser.getText().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });


    }


    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.my_test_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_zero) {
            Intent intent = new Intent(Preferences.this, Spoonacular.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_one) {
            Intent intent = new Intent(Preferences.this, Preferences.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_two) {
            Intent intent = new Intent(Preferences.this, SavedRecipes.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_three) {
            Intent intent = new Intent(Preferences.this, ShoppingList.class);
            String[] array = {};
            intent.putExtra("array", array);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_four) {
            Intent intent = new Intent(Preferences.this, MacroTracker.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_five) {
            Intent intent = new Intent(Preferences.this, Authentication.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }
}
