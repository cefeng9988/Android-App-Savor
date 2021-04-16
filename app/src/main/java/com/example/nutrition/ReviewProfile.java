//this activity populates the reviews left by a user
package com.example.nutrition;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewProfile extends AppCompatActivity {
    private DatabaseReference UserProfileRef, UsersRef;
    private ListAdapter lvAdapter;
    TextView txtHeader;
    ListView revProfileList;
    Intent intent;
    String userID;
    String[][] reviewData;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_profile_list);

        txtHeader = (TextView) findViewById(R.id.txtName);
        revProfileList = (ListView) findViewById(R.id.revProfileList);
        context = this.getBaseContext();
        //setting link to correct database
        UserProfileRef = FirebaseDatabase.getInstance().getReference().child("UserProfiles");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //get userID passed from eachRecipes
        intent = getIntent();
        userID = intent.getStringExtra("userID");

        //set txtHeader with reviewer's name
        UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get reviewer's name
                String reviewerName = dataSnapshot.child("name").getValue().toString();
                //set txtHeader with reviewer's name
                txtHeader.setText(reviewerName+"'s Reviews");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //loop through reviews under userID and save to String[]
        UserProfileRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    count++;
                }
                //instantiate our String[] to hold each review data
                reviewData = new String[count][2];
                int index = 0;
                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    Log.i("TAG","childkey: "+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.i("TAG","childvalue: "+  snapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString());

                    reviewData[index][0] = childDataSnapshot.getKey();
                    reviewData[index][1] = snapshot.child(String.valueOf(childDataSnapshot.getKey())).getValue().toString();
                    index++;
                }

                Log.i("TAG","reviewData key: "+ reviewData[0][0]);
                Log.i("TAG","reviewData value: "+ reviewData[0][1]);

                //instantiate adaptor then set ListView with adapter
                lvAdapter = new MyCustomAdapter(context, reviewData);
                revProfileList.setAdapter(lvAdapter);
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

        if (id == R.id.mnu_zero) {
            Intent intent = new Intent(ReviewProfile.this, Spoonacular.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_one) {
            Intent intent = new Intent(ReviewProfile.this, Preferences.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_two) {
            Intent intent = new Intent(ReviewProfile.this, SavedRecipes.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_three) {
            Intent intent = new Intent(ReviewProfile.this, ShoppingList.class);
            String[] array = {};
            intent.putExtra("array", array);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_four) {
            Intent intent = new Intent(ReviewProfile.this, MacroTracker.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnu_five) {
            Intent intent = new Intent(ReviewProfile.this, Authentication.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged Out",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }



    //our custom adapter that creates Views to populate our Recipe ListView
    class MyCustomAdapter extends BaseAdapter {
        Context context;
        String[][] reviewData;

        //constructor
        public MyCustomAdapter(Context aContext, String[][] aReviewData) {
            context = aContext;
            reviewData = aReviewData;
        }
        //How many rows to populate(based on number of reviews)
        @Override
        public int getCount() {
            return reviewData.length;
        }
        //not used
        @Override
        public Object getItem(int position) {
            return reviewData[position];
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
                row = inflater.inflate(R.layout.review_profile_list_row, parent, false);
            } else {
                row = convertView;
            }

            //Step 2: now that we have a row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            ImageView reviewImage = (ImageView) row.findViewById(R.id.reviewImage);
            Button btnRecipeName = (Button) row.findViewById(R.id.btnRecipeName);
            TextView txtUserReview = (TextView) row.findViewById(R.id.txtUserReview);
            //extracting the "recipeName#review" into an array
            String[] reviewValue = reviewData[position][1].split("#");
            //set the image,button and textView text from tuple
            Glide.with(getApplicationContext()).load(reviewValue[0]).into(reviewImage);
            btnRecipeName.setText(reviewValue[1]);
            txtUserReview.setText(reviewValue[2]);

            //button listener to go to eachRecipe with recipeID
            btnRecipeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //intent to go to reviewProfile activity
                    intent = new Intent(getApplicationContext(), eachRecipe.class);
                    //intent extra add the recipeID of row
                    String recipeID = reviewData[position][0];
                    intent.putExtra("recipeID", recipeID);
                    startActivity(intent);
                }
            });

            return row;
        }
    }

}
