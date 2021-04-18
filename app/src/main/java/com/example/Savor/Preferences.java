//user preferences page, user can change their name, age, Vegan? and # of recipes to display
package com.example.Savor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Preferences extends AppCompatActivity {

    private EditText edtUser, edtUserAge;
    private TextView txtWelcome;
    private ToggleButton toggleButtonVegan;
    private Button btnUpdate, btn5, btn10, btn15;
    private boolean recipeChanged, veganChanged;

    private DatabaseReference UsersRef;
    String userId;

    String recipesDisplayed;
    String vegan = "False";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        edtUser = (EditText) findViewById(R.id.edtUser);
        edtUserAge = (EditText) findViewById(R.id.edtUserAge);
        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btn5 = (Button) findViewById(R.id.btn5);
        btn10 = (Button) findViewById(R.id.btn10);
        btn15 = (Button) findViewById(R.id.btn15);
        toggleButtonVegan = (ToggleButton) findViewById(R.id.toggleButtonVegan);

        //boolean tells us if user changed recipe count or if vegan is toggled
        recipeChanged = false;
        veganChanged = false;

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //set recipesDisplayed to value saved in database
        UsersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //preset Vegan toggle based on saved data
                if(dataSnapshot.child("Vegan").getValue().toString().equals("True")) {
                    toggleButtonVegan.setChecked(true);
                }else{
                    toggleButtonVegan.setChecked(false);
                }
                recipesDisplayed = dataSnapshot.child("Recipes Displayed").getValue().toString();
                if(recipesDisplayed.equals("5")){
                    //enable the buttons based on which is pressed
                    btn5.setEnabled(false);
                    btn10.setEnabled(true);
                    btn15.setEnabled(true);
                }else if(recipesDisplayed.equals("10")){
                    //enable the buttons based on which is pressed
                    btn10.setEnabled(false);
                    btn15.setEnabled(true);
                    btn5.setEnabled(true);
                }else if(recipesDisplayed.equals("15")){
                    //enable the buttons based on which is pressed
                    btn15.setEnabled(false);
                    btn10.setEnabled(true);
                    btn5.setEnabled(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //set Welcome message
        setWelcome();
        //user changed # of recipes
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeChanged = true;
                recipesDisplayed = "5";
                //enable the buttons based on which is pressed
                btn5.setEnabled(false);
                btn10.setEnabled(true);
                btn15.setEnabled(true);
            }
        });
        //user changed # of recipes
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeChanged = true;
                recipesDisplayed = "10";
                //enable the buttons based on which is pressed
                btn10.setEnabled(false);
                btn15.setEnabled(true);
                btn5.setEnabled(true);
            }
        });
        //user changed # of recipes
        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeChanged = true;
                recipesDisplayed = "15";
                //enable the buttons based on which is pressed
                btn15.setEnabled(false);
                btn10.setEnabled(true);
                btn5.setEnabled(true);
            }
        });

        //user changed Vegan preferences
        toggleButtonVegan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                veganChanged = true;
                if (toggleButtonVegan.isChecked()) {
                    vegan = "True";
                }
                else {
                    vegan = "False";
                }
            }
        });


        // Saves the id, name, ingredients, and summary of the recipe to firebase
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //only update if the user entered a new name
                        if(!edtUser.getText().toString().equals("")) {
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId).child("name").setValue(edtUser.getText().toString());
                        }

                        //only update if the user entered a new age
                        if(!edtUserAge.getText().toString().equals("")) {
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId).child("age").setValue(edtUserAge.getText().toString());
                        }

                        //only update if the user entered # of recipes to display
                        if(recipeChanged) {
                            Log.i("TAG","recipes Displayed: "+recipesDisplayed);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId).child("Recipes Displayed").setValue(recipesDisplayed);
                        }

                        //only update if the user toggled vegan
                        if(veganChanged) {
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId).child("Vegan").setValue(vegan);
                        }

                        //repopulate welcome message
                        setWelcome();
                        Toast.makeText(getApplicationContext(),"Preferences Updated!",Toast.LENGTH_LONG).show();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void setWelcome() {
        //get back user name, age and vegan from database
        UsersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name, age, vegan;
                name = dataSnapshot.child("name").getValue().toString();
                age = dataSnapshot.child("age").getValue().toString();
                vegan = dataSnapshot.child("Vegan").getValue().toString();

                if(vegan.equals("True")){
                    vegan = "vegan.";
                }else{
                    vegan = "not vegan.";
                }
                //set welcome text
                txtWelcome.setText("Welcome "+name+", you are age "+age+" years old and you are "+vegan);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.my_test_menu, menu);
        return true;
    }

    //globally shared menu across several activities
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
