//recipe ingredients list, shopping list should be saved in sharedPreferences
package com.example.nutrition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShoppingList extends AppCompatActivity {
    //Setting up the ListView
    Context aContext;
    Button btnClear;
    ListView lvShoppingList;     //Reference to the listview GUI component
    ListAdapter slAdapter;   //Reference to the Adapter used to populate the listview
    Intent intent;
    //input is 2D array for ingredient and boolean for if box has been checked or not
    String[][] listInput;
    String[] ingredients;

    ArrayList<String> current_ingredients = new ArrayList<>();

    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        aContext = this.getBaseContext();

        //used shared preferences to save previously saved ingredients
        pref = getSharedPreferences("pref", Context.MODE_PRIVATE);

        intent = getIntent();
        //get String[] of ingredients from intent extras
        ingredients = intent.getStringArrayExtra("array");

        //retrieve newly added ingredients into total/previously stored ingredients
        int size = pref.getInt("total_ingredients_size", 0);
        current_ingredients = new ArrayList<>(size);
        for(int i=0;i<size;i++)
            current_ingredients.add(pref.getString("total_ingredients" + "_" + i, null));

        //add new ingredients to the current_ingredients list
        for(int i = 0; i<ingredients.length; i++){
            current_ingredients.add(ingredients[i]);
        }


        //fill our 2D array of ingredients and boolean value for setChecked
        listInput = new String[current_ingredients.size()][2];
        for(int i = 0; i<listInput.length; i++){
            listInput[i][0] = current_ingredients.get(i);
            listInput[i][1] = "false";
        }

        //inflate shopping list
        lvShoppingList = (ListView) findViewById(R.id.lvShoppingList);
        slAdapter = new ShoppingListAdapter(this.getBaseContext(), listInput);
        lvShoppingList.setAdapter(slAdapter);

        //remove all
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listInput = new String[0][2];
                slAdapter = new ShoppingListAdapter(aContext, listInput);
                lvShoppingList.setAdapter(slAdapter);
                current_ingredients.clear();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // save ingredients using shared preferences
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("total_ingredients_size", current_ingredients.size());
        for(int i=0;i<current_ingredients.size();i++)
            editor.putString("total_ingredients" + "_" + i, current_ingredients.get(i));

        editor.apply();
    }



    class ShoppingListAdapter extends BaseAdapter {
        private
        String[][] listInput;
        Button btnRemove;
        Context context;

        public ShoppingListAdapter(Context aContext, String[][] input) {
            context = aContext;
            listInput = input;
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
                row = inflater.inflate(R.layout.shoppinglistrow, parent, false);
            } else {
                row = convertView;
            }

//Now that we have a valid row instance, we need to get references to the views within that row and fill it
            CheckBox checkBox = (CheckBox) row.findViewById(R.id.ingredientCheck);
            checkBox.setText(listInput[position][0]);
            checkBox.setChecked(Boolean.parseBoolean(listInput[position][1]));

            //if item is checked by user, update listInput to be true at position
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listInput[position][1] = "true";
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
                    current_ingredients.remove(position);
                    Log.i("remove", "removed " + current_ingredients.get(position));
                }
            });
//the row has been inflated and filled with data, return it.
            return row;  //return convertView;
        }
    }
}