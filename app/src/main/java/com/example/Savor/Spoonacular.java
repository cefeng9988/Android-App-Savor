//Spoonacular powers our recipe search functionality
package com.example.Savor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Spoonacular extends AppCompatActivity {
    private Button btnSubmit1, btnSubmit2;
    private EditText txtEdtKeyWords, txtEdtIngredients;
    private ImageView image;
    private String query, ingredients;
    private DatabaseReference UsersRef;
    String userId, recipesDisplayed, vegan;
    private ListView recipeList;
    private ListAdapter lvAdapter;
    private int recipeLength;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spoonacular);

        btnSubmit1 = (Button) findViewById(R.id.btnSubmit1);
        btnSubmit2 = (Button) findViewById(R.id.btnSubmit2);
        txtEdtKeyWords = (EditText) findViewById(R.id.txtEdtKeyWords);
        txtEdtIngredients = (EditText) findViewById(R.id.txtEdtIngredients);
        image = (ImageView) findViewById(R.id.image);
        //defining database and the current user ID
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UsersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipesDisplayed = dataSnapshot.child("Recipes Displayed").getValue().toString();
                vegan = dataSnapshot.child("Vegan").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recipeList = (ListView) findViewById(R.id.recipeList);
        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //pass ID of the recipe to eachRecipe page
                intent = new Intent(getApplicationContext(), eachRecipe.class);
                intent.putExtra("recipeID", ""+view.getId());
                startActivity(intent);
            }
        });


        // Submits user input for type of cuisine and outputs the recipes related to the cuisine
        btnSubmit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = txtEdtKeyWords.getText().toString();
                if(input.equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter Recipe or Cuisine", Toast.LENGTH_LONG).show();
                }else {
                    query = input;
                    //get recipe with query and get back recipe ID, name and ingredients
                    getRecipeByQuery(query);
                }
                query="";
            }
        });

        // Submits user input for list of ingredients and outputs the recipes related to the cuisine
        btnSubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = txtEdtIngredients.getText().toString();
                if(input.equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter Ingredients", Toast.LENGTH_LONG).show();
                }else {
                    //get recipe with pantry ingredients
                    getRecipeByIngredients(input);
                }
            }
        });


    }



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
            Intent intent = new Intent(Spoonacular.this, Spoonacular.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_one) {
            Intent intent = new Intent(Spoonacular.this, Preferences.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_two) {
            Intent intent = new Intent(Spoonacular.this, SavedRecipes.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_three) {
            Intent intent = new Intent(Spoonacular.this, ShoppingList.class);
            String[] array = {};
            intent.putExtra("array", array);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_four) {
            Intent intent = new Intent(Spoonacular.this, MacroTracker.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_five) {
            Intent intent = new Intent(Spoonacular.this, Authentication.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }



    //helper function to fill ListView with JSONObject recipes
    private void fillListObject(JSONObject response) {
        //get/display ingredients
        try {
            recipeLength = response.getInt("totalResults");
            //covers less than designated recipes but does not exceed it
            if(recipeLength>Integer.parseInt(recipesDisplayed)){
                recipeLength = Integer.parseInt(recipesDisplayed);
            }
            String[][] tuple = new String[recipeLength][3];
            JSONArray arr = response.getJSONArray("results");
            for(int i = 0; i<recipeLength; i++){
                //fill 2D array with tuples of image+recipe name
                String name = arr.getJSONObject(i).getString("title");
                String getImage = arr.getJSONObject(i).getString("image");
                String getID = arr.getJSONObject(i).getString("id");
                tuple[i][0] = name;
                tuple[i][1] = getImage;
                tuple[i][2] = getID;
                Log.i("TAG", "tuple["+i+"][0]: "+tuple[i][0]);
                Log.i("TAG", "tuple["+i+"][1]: "+tuple[i][1]);
                Log.i("TAG", "tuple["+i+"][2]: "+tuple[i][2]);
            }

            //create instance of MyCustomAdapter and pass in the 2D array of tuples
            //code the MyCustomAdapter class and set constructor like example in Lect7_CustomListView
            lvAdapter = new MyCustomAdapter(this.getBaseContext(), tuple);
            recipeList.setAdapter(lvAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //helper function to fill ListView with JSONArray recipes
    private void fillListArray(JSONArray response) {
        //get/display ingredients
        try{
            recipeLength = response.length();
            //covers less than designated recipes but does not exceed it
            if(recipeLength>Integer.parseInt(recipesDisplayed)){
                recipeLength = Integer.parseInt(recipesDisplayed);
            }
            String[][] tuple = new String[recipeLength][3];
            for(int i = 0; i<recipeLength; i++) {
                //get each JSONObject from JSONArray
                JSONObject object = response.getJSONObject(i);
                //get name, image and id
                String name = object.getString("title");
                String getImage = object.getString("image");
                String getID = object.getString("id");
                //fill tuple
                tuple[i][0] = name;
                tuple[i][1] = getImage;
                tuple[i][2] = getID;
            }

            //create instance of MyCustomAdapter and pass in the 2D array of tuples
            //code the MyCustomAdapter class and set constructor like example in Lect7_CustomListView
            lvAdapter = new MyCustomAdapter(this.getBaseContext(), tuple);
            recipeList.setAdapter(lvAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //our custom adapter that creates Views to populate our Recipe ListView
    class MyCustomAdapter extends BaseAdapter {
        Context context;
        String[][] tuple;

        //constructor
        public MyCustomAdapter(Context aContext, String[][] aTuple) {
            context = aContext;
            tuple = aTuple;
        }

        @Override
        public int getCount() {
            return tuple.length;
        }

        @Override
        public Object getItem(int position) {
            return tuple[position];
        }

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
                row = inflater.inflate(R.layout.listview_row, parent, false);
            } else {
                row = convertView;
            }

            //Step 2: now that we have a row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            ImageView recipeImage = (ImageView) row.findViewById(R.id.recipeImage);
            TextView recipeName = (TextView) row.findViewById(R.id.userReview);
            //set TextView and ImageView
            recipeName.setText(tuple[position][0]);
            Glide.with(getApplicationContext()).load(tuple[position][1]).into(recipeImage);
            //set view ID to be the recipe ID
            row.setId(Integer.parseInt(tuple[position][2]));

            return row;
        }
    }

    //Helper function, get in recipe data with query words
    public void getRecipeByQuery(final String query) {
        String URL =  "https://api.spoonacular.com/recipes/complexSearch?apiKey=f80bf47afa2549c2bad353d39505fe4c&instructionsRequired =true&number=20&query=" + query;
        //check if user is Vegan
        if(vegan.equals("True")){
            URL = "https://api.spoonacular.com/recipes/complexSearch?apiKey=f80bf47afa2549c2bad353d39505fe4c&instructionsRequired =true&number=20&diet=vegan&query=" + query;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // grabs json object from api
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            URL,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //call helper function to fill ListView
                    fillListObject(response);

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("the res is error:",error.toString());
                }
            }
        );
        requestQueue.add(jsonObjectRequest);
    }

    //get recipe with ingredients
    public void getRecipeByIngredients(String pantry) {
        ingredients="";
        Log.i("TAG", "Pantry: "+pantry);
        String URL = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=f80bf47afa2549c2bad353d39505fe4c&ingredients="+pantry;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // grabs json object from api
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
            Request.Method.GET,
            URL,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //call helper function to fill ListView
                    fillListArray(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("the res is error:",error.toString());
                }
            }
        );
        requestQueue.add(jsonArrayRequest);
    }
}