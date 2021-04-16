package com.example.nutrition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class SavedRecipes extends AppCompatActivity {
    private DatabaseReference UserSavedRecipes;
    private ListAdapter lvAdapter;
    TextView txtUser;
    ListView savedProfileList;
    Intent intent;
    String userID;
    String[][] savedData;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recipes_list);

        txtUser = (TextView) findViewById(R.id.txtUser);
        savedProfileList = (ListView) findViewById(R.id.savedProfileList);
        context = this.getBaseContext();
        //setting link to correct database
        UserSavedRecipes = FirebaseDatabase.getInstance().getReference().child("UserSavedRecipes");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // check if there is a record of saved recipes for a user
        UserSavedRecipes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userID)) {

                    //loop through reviews under userID and save to String[]
                    UserSavedRecipes.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int count = 0;
                            for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                                count++;
                            }
                            //instantiate our String[] to hold each review data
                            savedData = new String[count][2];
                            int index = 0;
                            for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                                Log.i("TAG","childkey: "+ childDataSnapshot.getKey()); //displays the key for the node
                                Log.i("TAG","childvalue: "+  snapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString());

                                savedData[index][0] = childDataSnapshot.getKey();
                                savedData[index][1] = snapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString();
                                index++;
                            }

                            Log.i("TAG","savedData key: "+ savedData[0][0]);
                            Log.i("TAG","savedData value: "+ savedData[0][1]);

                            //instantiate adaptor then set ListView with adapter
                            lvAdapter = new MyCustomAdapter(context, savedData);
                            savedProfileList.setAdapter(lvAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

        // Home page intent
        if (id == R.id.mnu_zero) {
            Intent intent = new Intent(SavedRecipes.this, Spoonacular.class);
            startActivity(intent);
            return true;
        }
        // Preferences intent
        if (id == R.id.mnu_one) {
            Intent intent = new Intent(SavedRecipes.this, Preferences.class);
            startActivity(intent);
            return true;
        }
        // SavedRecipes intent
        if (id == R.id.mnu_two) {
            Intent intent = new Intent(SavedRecipes.this, SavedRecipes.class);
            startActivity(intent);
            return true;
        }
        // ShoppingList intent
        if (id == R.id.mnu_three) {
            Intent intent = new Intent(SavedRecipes.this, ShoppingList.class);
            String[] array = {};
            intent.putExtra("array", array);
            startActivity(intent);
            return true;
        }
        // MacroTracker intent
        if (id == R.id.mnu_four) {
            Intent intent = new Intent(SavedRecipes.this, MacroTracker.class);
            startActivity(intent);
            return true;
        }
        // Log out intent
        if (id == R.id.mnu_five) {
            Intent intent = new Intent(SavedRecipes.this, Authentication.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }


    //our custom adapter that creates Views to populate our Recipe ListView
    class MyCustomAdapter extends BaseAdapter {
        Context context;
        String[][] savedData;
        Button btnRemoveRecipe;

        //constructor
        public MyCustomAdapter(Context aContext, String[][] aSavedData) {
            context = aContext;
            savedData = aSavedData;
        }
        //How many rows to populate(based on number of reviews)
        @Override
        public int getCount() {
            return savedData.length;
        }
        //not used
        @Override
        public Object getItem(int position) {
            return savedData[position];
        }
        //not used
        @Override
        public long getItemId(int position) {
            return position;
        }

        //Step-by-step on create each view to fill recipeList
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Step 1: Inflate the listview row
            View row;
            //check if convertView has been inflated already, optimizing step
            if (convertView == null) {  //indicates this is the first time we are creating this row.
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
                row = inflater.inflate(R.layout.saved_recipes_list_row, parent, false);
            } else {
                row = convertView;
            }

            //Step 2: now that we have a row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            Button btnRecipe = (Button) row.findViewById(R.id.btnRecipe);
            ImageView imageView = (ImageView) row.findViewById(R.id.imageView);
            //extracting the "recipeName#review" into an array
            String[] savedValue = savedData[position][1].split(",");
            //set the button and textView text from tuple
            btnRecipe.setText(savedValue[0]);
            Glide.with(getApplicationContext()).load(savedValue[1]).into(imageView);

            //button listener to go to eachRecipe with recipeID
            btnRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //intent to go to reviewProfile activity
                    intent = new Intent(getApplicationContext(), eachRecipe.class);
                    //intent extra add the recipeID of row
                    String recipeID = savedData[position][0];
                    intent.putExtra("recipeID", recipeID);
                    startActivity(intent);
                }
            });

            // remove item
            btnRemoveRecipe = (Button) row.findViewById(R.id.btnRemoveRecipe);
            btnRemoveRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // remove the recipe from firebasee
                    UserSavedRecipes.child(userID).child(savedData[position][0]).removeValue();

                    // Remove the recipe from the listview
                    ArrayList<String[]> temp = new ArrayList<>(Arrays.asList(savedData));
                    temp.remove(position);
                    String[][] buffer = new String[temp.size()][];
                    savedData = temp.toArray(buffer);
                    notifyDataSetChanged();

                }
            });


            return row;
        }
    }

}
