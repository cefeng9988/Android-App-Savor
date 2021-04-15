//Displays information on the recipe user selected from home page
//shows ingredients, price, instructions, image, calories
package com.example.nutrition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class eachRecipe extends AppCompatActivity {
    private TextView txtTitle, txtCaloriesDisplay, txtPriceDisplay, txtIngredientsDisplay;
    private Button btnSubmitReview, btnShopping, btnTracker, btnSave;
    private EditText txtReview;
    private ListAdapter lvAdapter;
    private ListView recipeList, reviewList;
    private String ingredients, recipeID, recipeName, recipeImageURL;
    private String[] instructionSteps;
    private ImageView image;
    private Intent intent;
    public String[][] tuple;
    private Context context;
    private int count;
    private int calories;

    private DatabaseReference RecipeRef;
    private DatabaseReference ReviewRef;
    private DatabaseReference UserProfileRef;
    private DatabaseReference UsersRef;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.each_recipe);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtCaloriesDisplay = (TextView) findViewById(R.id.txtCaloriesDisplay);
        txtPriceDisplay = (TextView) findViewById(R.id.txtPriceDisplay);
        txtIngredientsDisplay = (TextView) findViewById(R.id.txtIngredientsDisplay);
        recipeList = (ListView) findViewById(R.id.recipeList);
        reviewList = (ListView) findViewById(R.id.reviewList);
        image = (ImageView) findViewById(R.id.image);
        txtReview = (EditText) findViewById(R.id.txtReview);
        btnSubmitReview = (Button) findViewById(R.id.btnSubmitReview);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setEnabled(true);
        btnShopping = (Button) findViewById(R.id.btnShopping);
        btnTracker = (Button) findViewById(R.id.btnTracker);
        context = this.getBaseContext();

        RecipeRef = FirebaseDatabase.getInstance().getReference().child("UserSavedRecipes");
        ReviewRef = FirebaseDatabase.getInstance().getReference().child("ReviewRecipes");
        UserProfileRef = FirebaseDatabase.getInstance().getReference().child("UserProfiles");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        intent = getIntent();
        //get current recipe ID
        recipeID = intent.getStringExtra("recipeID");
        //populate page with recipe data
        getRecipeData(recipeID);

        //call helper function with recipeID to fill tuple with reviews from Firebase
        fillTuple(recipeID);

        //button listener for shopping list
        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to go to shoppingList activity
                intent = new Intent(getApplicationContext(), ShoppingList.class);
                //intent extra add the following array
                String[] ingredientArray = ingredients.split(",");
                intent.putExtra("array", ingredientArray);
                startActivity(intent);
            }
        });

        //button listener for macrotracker
        btnTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to go to shoppingList activity
                intent = new Intent(getApplicationContext(), MacroTracker.class);
                //intent extra add the following array
                intent.putExtra("calories", calories);
                startActivity(intent);
            }
        });

        //button listener for submitting user review
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save user input from txtReview to database under the current recipeID
                ReviewRef.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference("UserProfiles")
                                .child(userId).child(recipeID).setValue(recipeName+"#"+txtReview.getText().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                UserProfileRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference("ReviewRecipes")
                                .child(recipeID).child(userId).setValue(txtReview.getText().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                //new review added so repopulate tuple before inflating
                fillTuple(recipeID);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // read the database and check if the specified recipeID exists
                        if (snapshot.hasChild(recipeID)){
                            Toast.makeText(context, "Recipe Has Already Been Saved", Toast.LENGTH_LONG).show();
                            btnSave.setEnabled(false);
                        }
                        // if the recipeID does not already exist, add it to the database
                        else {
                            FirebaseDatabase.getInstance().getReference("UserSavedRecipes")
                                    .child(userId).child(recipeID).setValue(recipeName+","+recipeImageURL);
                            Toast.makeText(context, "Recipe Has Been Saved", Toast.LENGTH_LONG).show();
                            btnSave.setEnabled(false);
                        }
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
            Intent intent = new Intent(eachRecipe.this, Spoonacular.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_one) {
            Intent intent = new Intent(eachRecipe.this, Preferences.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_two) {
            Intent intent = new Intent(eachRecipe.this, SavedRecipes.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_three) {
            Intent intent = new Intent(eachRecipe.this, ShoppingList.class);
            String[] array = {};
            intent.putExtra("array", array);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_four) {
            Intent intent = new Intent(eachRecipe.this, MacroTracker.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_five) {
            Intent intent = new Intent(eachRecipe.this, Authentication.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }



    //Fill tuple with reviews from Firebase using RecipeID
    private void fillTuple(String recipeID) {
        //each element at index -> {"username", "reviewText"}
        CountDownLatch latch = new CountDownLatch(1);

        //retrieve the userid and the review given by the userid inside ReviewRecipes Table
        ReviewRef.child(recipeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //set String[][] length
                count = 0;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    count++;
                }

                final String[][] tupleCopy = new String[count][2];
                int index = 0;
                //loops through reviews saved and adds key/value to our tupleCopy
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
//                    Log.i("TAG","childkey: "+ childDataSnapshot.getKey()); //displays the key for the node
//                    Log.i("TAG","childvalue: "+  dataSnapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString());

                    tupleCopy[index][0] = childDataSnapshot.getKey();
                    tupleCopy[index][1] = dataSnapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString();
                    index++;
                }
                //initializing tuple with tupleCopy
                tuple = tupleCopy;
                //check if recipe ID has reviews,if so, populate them to start
                if(tuple.length != 0){
                    //instantiate adaptor then set ListView with adapter
                    lvAdapter = new MyCustomAdapter(context, tuple);
                    reviewList.setAdapter(lvAdapter);
                }
                //if not, fill row 1 with "Be the first to leave a review!"
                else{
                    ArrayAdapter reviewAdapter = new ArrayAdapter<String>(eachRecipe.this, android.R.layout.simple_list_item_1, new String[]{"Be the first to leave a review!"});
                    reviewList.setAdapter(reviewAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
        //How many rows to populate(based on number of reviews)
        @Override
        public int getCount() {
            return tuple.length;
        }
        //not used
        @Override
        public Object getItem(int position) {
            return tuple[position];
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
                row = inflater.inflate(R.layout.reviewlist_row, parent, false);
            } else {
                row = convertView;
            }

            //Step 2: now that we have a row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            Button userName = (Button) row.findViewById(R.id.userName);
            TextView userReview = (TextView) row.findViewById(R.id.userReview);

            //changing User ID to User names
            UsersRef.child(tuple[position][0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //Set User name to Button
                    userName.setText(snapshot.child("name").getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
            //set user review to TextView
            userReview.setText(tuple[position][1]);

            //listener and make intent to User Review Profile page with intent String of their username
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //intent to go to reviewProfile activity
                    intent = new Intent(getApplicationContext(), ReviewProfile.class);
                    //intent extra add the userID of row
                    String userID = tuple[position][0];
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                }
            });

            return row;
        }
    }



    //get in detailed recipe data with recipeID (this is used when user specifies a recipe of interest and this will go in depth)
    //gets ingredients, image, name, instructions
    public void getRecipeData(final String recipeId) {
        //shares global ingredients, used to store ingredients from API
        ingredients = "";

        String URL = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=f80bf47afa2549c2bad353d39505fe4c";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // grabs json object from api
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            //set recipe name
                            recipeName = (String)response.get("title");
                            txtTitle.setText(recipeName);
                            //set price per servings
                            double price = (double)response.get("pricePerServing")/100;
                            String priceString = "$"+String.format("%.2f", price);
                            txtPriceDisplay.setText(priceString);
                            //set calories
                            calories = getCalories(response.getString("summary"));
                            if(calories==0){
                                calories = 73;
                                txtCaloriesDisplay.setText("" + (calories+73) + " per serving");
                            }else {
                                txtCaloriesDisplay.setText("" + calories + " per serving");
                            }
                            //set image using URL from response.get("image")
                            recipeImageURL = (String)response.get("image");
                            Glide.with(getApplicationContext()).load(recipeImageURL).into(image);
                            //get/display ingredients
                            JSONArray arr = response.getJSONArray("extendedIngredients"); // parses json array called "extendedIngredients" from the requested object
                            for(int i = 0; i<arr.length(); i++){
                                //remove comma for last ingredient
                                if(i == arr.length()-1){
                                    ingredients = ingredients + arr.getJSONObject(i).getString("name");
                                }else {
                                    ingredients = ingredients + arr.getJSONObject(i).getString("name") + ", ";
                                }
                            }
                            txtIngredientsDisplay.setText(ingredients);

                            //checking if instructions exist, if not then display a warning
                            if(!(response.getJSONArray("analyzedInstructions").length() == 0)) {
                                //loop through instruction steps array
                                JSONArray steps = response.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
                                //String[] is used to ListAdapter
                                instructionSteps = new String[steps.length()];
                                for (int i = 0; i < steps.length(); i++) {
                                    String step = steps.getJSONObject(i).getString("step");
                                    instructionSteps[i] = "Step " + (i + 1) + ": " + step;
                                }
                            }else{
                                //no instructions found warning to user
                                instructionSteps = new String[2];
                                instructionSteps[0] = "Unfortunately, recipe instructions are unavailable.";
                                instructionSteps[1] = "Please search for a new recipe.";
                            }

                            //initialize adapter and fill ListView with instructionSteps
                            ArrayAdapter adapter = new ArrayAdapter<String>(eachRecipe.this, android.R.layout.simple_list_item_1, instructionSteps);
                            recipeList.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    //get Calories helper function
    private int getCalories(String input) {
        int calories = 0;
        try {
            String[] array = input.split("calories");
            String cals = array[0].substring(array[0].length() - 4, array[0].length() - 1);
            calories = Integer.parseInt(cals);
            Log.i("TAG","calories: "+calories);
        }catch(Exception e){
        }
        return calories;
    }
}
