//recipe ingredients list, shopping list should be saved in sharedPreferences
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
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class MacroTracker extends AppCompatActivity {
    //Setting up the ListView
    Context aContext;
    Button btnClear;
    ListView lvTrackerList;     //Reference to the listview GUI component
    ListAdapter trackerAdapter;   //Reference to the Adapter used to populate the listview
    Intent intent;
    TextView txtTotalKcals;
    //input is 2D array for ingredient and boolean for if box has been checked or not
    String[][] listInput;
    String[] recipelistInput;

    String calories;
    String recipeName;
    String userId;

    int totalcalories = 0;

    ArrayList<String> current_recipeNames = new ArrayList<>();
    ArrayList<String> current_calories = new ArrayList<>();

    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.macrotracker);

        aContext = this.getBaseContext();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        txtTotalKcals = (TextView) findViewById(R.id.txtTotalKcals);

        //used shared preferences to save previously saved ingredients
        pref = getSharedPreferences("pref", Context.MODE_PRIVATE);

        intent = getIntent();
        //get String[] of ingredients from intent extras
        calories = String.valueOf(intent.getIntExtra("calories",0));
        recipeName = intent.getStringExtra("recipeName");

        //retrieve newly added calorie into total/previously stored calories
        int size = pref.getInt(userId + "total_calories_size", 0);
        current_calories = new ArrayList<>(size);
        for(int i=0;i<size;i++)
            current_calories.add(pref.getString(userId + "total_calories" + "_" + i, null));

        //retrieve newly added recipeNames into total/previously stored recipeNames
        int recipeNameSize = pref.getInt(userId + "total_recipes_size", 0);
        current_recipeNames = new ArrayList<>(recipeNameSize);
        for(int i=0;i<size;i++)
            current_recipeNames.add(pref.getString(userId + "total_recipes" + "_" + i, null));

        // handle case when recipe has 0 calories
        if (!calories.equals("0")) {
            // scenario when using enters activity through menu or when recipe has calories listed as 0
            current_calories.add(calories);
        }

        // handle case when recipe name is not null (so app does not add null variables into list)
        if (recipeName != null) {
            current_recipeNames.add(recipeName);
        }

        for(int i = 0; i<current_calories.size(); i++){
            totalcalories += Integer.parseInt(current_calories.get(i));
        }
        txtTotalKcals.setText(String.valueOf(totalcalories));

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("totalcalories", totalcalories);
        editor.apply();

        //fill our 2D array of calories and boolean value for setChecked
        listInput = new String[current_calories.size()][2];
        for(int i = 0; i<listInput.length; i++){
            listInput[i][0] = current_calories.get(i);
            listInput[i][1] = "false";
        }

        //fill our 2D array with recipeNames
        recipelistInput = new String[current_recipeNames.size()];
        for(int i = 0; i<recipelistInput.length; i++){
            recipelistInput[i] = current_recipeNames.get(i);
        }


        //inflate tracker list
        lvTrackerList = (ListView) findViewById(R.id.lvTrackerList);
        trackerAdapter = new MacroTrackerAdapter(this.getBaseContext(), listInput, recipelistInput);
        lvTrackerList.setAdapter(trackerAdapter);


        //remove all calories
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listInput = new String[0][2];
                recipelistInput = new String[0];
                trackerAdapter = new MacroTrackerAdapter(aContext, listInput, recipelistInput);
                lvTrackerList.setAdapter(trackerAdapter);
                current_calories.clear();
                current_recipeNames.clear();
                txtTotalKcals.setText("0");
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
            Intent intent = new Intent(MacroTracker.this, Spoonacular.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_one) {
            Intent intent = new Intent(MacroTracker.this, Preferences.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_two) {
            Intent intent = new Intent(MacroTracker.this, SavedRecipes.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_three) {
            Intent intent = new Intent(MacroTracker.this, ShoppingList.class);
            String[] array = {};
            intent.putExtra("array", array);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_four) {
            Intent intent = new Intent(MacroTracker.this, MacroTracker.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_five) {
            Intent intent = new Intent(MacroTracker.this, Authentication.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }


    @Override
    protected void onPause() {
        super.onPause();
        // save ingredients using shared preferences
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(userId + "total_calories_size", current_calories.size());
        for(int i=0;i<current_calories.size();i++)
            editor.putString(userId + "total_calories" + "_" + i, current_calories.get(i));


        editor.putInt(userId + "total_recipes_size", current_recipeNames.size());
        for(int i=0;i<current_recipeNames.size();i++)
            editor.putString(userId + "total_recipes" + "_" + i, current_recipeNames.get(i));

        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // save ingredients using shared preferences
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(userId + "total_calories_size", current_calories.size());
        for(int i=0;i<current_calories.size();i++)
            editor.putString(userId + "total_calories" + "_" + i, current_calories.get(i));


        editor.putInt(userId + "total_recipes_size", current_recipeNames.size());
        for(int i=0;i<current_recipeNames.size();i++)
            editor.putString(userId + "total_recipes" + "_" + i, current_recipeNames.get(i));

        editor.apply();
    }



    class MacroTrackerAdapter extends BaseAdapter {
        private
        String[][] listInput;
        Button btnRemove;
        Context context;

        public MacroTrackerAdapter(Context aContext, String[][] input, String[] recipeinput) {
            context = aContext;
            listInput = input;
            recipelistInput = recipeinput;
        }

        //the must have
        @Override
        public int getCount() {
            return listInput.length;
        }

        @Override
        public Object getItem(int position) {
            return listInput[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row;  //this will refer to the row to be inflated or displayed if it's already been displayed.

//// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
            if (convertView == null) {  //indicates this is the first time we are creating this row.
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
                row = inflater.inflate(R.layout.macrotrackerrow, parent, false);
            } else {
                row = convertView;
            }

//Now that we have a valid row instance, we need to get references to the views within that row and fill it
            Button btnRecipe = (Button) row.findViewById(R.id.btnRecipe);
            btnRecipe.setText(recipelistInput[position]);

            CheckBox checkBox = (CheckBox) row.findViewById(R.id.calorieCheck);
            checkBox.setText(listInput[position][0]);
            checkBox.setChecked(Boolean.parseBoolean(listInput[position][1]));

            //if item is checked by user, update listInput to be true at position
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listInput[position][1].equals("false")) {
                        listInput[position][1] = "true";
                    }else{
                        listInput[position][1] = "false";
                    }
                }
            });



            // remove item
            btnRemove = (Button) row.findViewById(R.id.btnRemove);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String[]> temp = new ArrayList<>(Arrays.asList(listInput));
                    temp.remove(position);
                    String[][] buffer = new String[temp.size()][];
                    listInput = temp.toArray(buffer);
                    notifyDataSetChanged();
                    current_calories.remove(position);


                    ArrayList<String> recipetemp = new ArrayList<>(Arrays.asList(recipelistInput));
                    recipetemp.remove(position);
                    String[] recipebuffer = new String[recipetemp.size()];
                    recipelistInput = recipetemp.toArray(recipebuffer);
                    notifyDataSetChanged();
                    current_recipeNames.remove(position);


                    /*
                    SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);

                    int kcals = pref.getInt("totalcalories", 0);
                    // update total calories
                    kcals -= Integer.parseInt(current_calories.get(position));
                    //txtTotalKcals.setText(String.valueOf(kcals));
                    Log.i("kcals", String.valueOf(kcals));
*/
                }
            });
//the row has been inflated and filled with data, return it.
            return row;  //return convertView;
        }
    }
}