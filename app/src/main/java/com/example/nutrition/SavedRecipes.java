//saved recipes page
//retrieve user saved recipes with back and logout functionality
package com.example.nutrition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SavedRecipes extends AppCompatActivity {

    DatabaseReference reff;
    private TextView txtRecID, txtName, txtIngre, txtSummary;
    private Button btnLogout3, btnBack, btnDisplay;
    private ImageView imageView;
    private String id;
    private String name;
    private String FireIngredients;
    private String FireSummary;
    private String ingredients, summary;
    private String[] tuple;
    private DatabaseReference RecipeRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recipes);

        txtRecID = (TextView) findViewById(R.id.txtRecID);
        txtName = (TextView) findViewById(R.id.txtName);
        txtIngre = (TextView) findViewById(R.id.txtIngre);
        txtSummary = (TextView) findViewById(R.id.txtSummary);
        btnLogout3 = (Button) findViewById(R.id.btnLogout3);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);
        imageView = (ImageView) findViewById(R.id.imageView);
        RecipeRef = FirebaseDatabase.getInstance().getReference().child("UserSavedRecipes");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // get saved recipe ids and store in a list
        //UserSavedRecipes Table in Firebase
        RecipeRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("childkey",""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v("childvalue", ""+ dataSnapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //display user saved recipes
        btnDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // currently only grab the first child of the recipe
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                reff = FirebaseDatabase.getInstance().getReference().child("UserSavedRecipes").child(userId);
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // retrieve id, name, ingredients, and summary from database
                        id = snapshot.child("0").getValue().toString();
                        //name = snapshot.child("name").getValue().toString();
                        //FireIngredients = snapshot.child("ingredients").getValue().toString();
                        //FireSummary = snapshot.child("summary").getValue().toString();
                        txtRecID.setText(id);
                        //txtName.setText(name);
                        //txtIngre.setText(FireIngredients);
                        //txtSummary.setText(FireSummary);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //getRecipeData(id);
            }
        });

        //logout button listener
        btnLogout3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedRecipes.this, Authentication.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            }
        });
        //back button listener
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedRecipes.this, Spoonacular.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            }
        });



    }
/*
    //get in detailed recipe data with recipeID (this is used when user specifies a recipe of interest and this will go in depth)
    //gets ingredients, image, name,  not yet summary/instructions
    public void getRecipeData(final String recipeId) {
        //shares global ingredients, used to store ingredients from API
        ingredients = "";
        summary = "";
        txtName.setText("Loading...");
        imageView.setImageResource(R.drawable.loading);
        txtRecID.setText("ID: "+recipeId);

        String URL = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=f80bf47afa2549c2bad353d39505fe4c";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            //set recipe name
                            txtName.setText("Name: "+(String)response.get("title"));
                            //set image using URL from response.get("image")
                            Glide.with(getApplicationContext()).load(response.get("image")).into(imageView);
                            //get/display ingredients
                            JSONArray arr = response.getJSONArray("extendedIngredients");
                            for(int i = 0; i<arr.length(); i++){
                                if(i==0){
                                    ingredients = ingredients + "Ingredients: "+arr.getJSONObject(i).getString("name") + ", ";
                                }else {
                                    ingredients = ingredients + arr.getJSONObject(i).getString("name") + ", ";
                                }
                            }
                            Log.i("ingredients", ingredients);
                            txtIngre.setText(ingredients);
                            txtSummary.setText("Name: "+(String)response.get("summary"));

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
    */
}