package com.example.nutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MacroTracker extends AppCompatActivity {
    //Setting up the ListView
    private
    Context aContext;
    Button btnClear;
    TextView txttotalKcals;
    ListView lvMacroTracker;     //Reference to the listview GUI component
    ListAdapter mtAdapter;   //Reference to the Adapter used to populate the listview
    //default empty
    //String[] nameInput = new String[0];
    //int[] kcalsInput = new String[0];
    int totalKcals = 0;
    //test
    String[] nameInput = new String[] { "apple", "banana", "chocolate","..."};
    int[] kcalsInput = new int[] { 1,2,3,4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.macrotracker);
        //calculate total kcals
        for (int i: kcalsInput)
            totalKcals += i;

        txttotalKcals = (TextView) findViewById(R.id.txtTotalKcals);
        txttotalKcals.setText(Integer.toString(totalKcals));

        lvMacroTracker = (ListView) findViewById(R.id.lvShoppingList);
        mtAdapter = new MacroTrackerAdapter(this.getBaseContext(), nameInput, kcalsInput);
        lvMacroTracker.setAdapter(mtAdapter);
        aContext = this.getBaseContext();

        //remove all
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInput = new String[0];
                kcalsInput = new int[0];
                mtAdapter = new MacroTrackerAdapter(aContext, nameInput, kcalsInput);
                lvMacroTracker.setAdapter(mtAdapter);
                totalKcals = 0;
                txttotalKcals.setText(Integer.toString(totalKcals));
            }
        });
    }
}
class MacroTrackerAdapter extends BaseAdapter {

    private
    String[] nameInput;
    int[] kcalsInput;
    Button btnRemove;
    Context context;

    public MacroTrackerAdapter(Context aContext, String[] name, int[] kcals) {
        context = aContext;
        nameInput = name;
        kcalsInput = kcals;


    }

    //the must have
    @Override
    public int getCount() { return nameInput.length; }
    @Override
    public Object getItem(int position) { return nameInput[position]; }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed.

//// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.macrotrackerrow, parent, false);
        }
        else
        { row = convertView; }

//Now that we have a valid row instance, we need to get references to the views within that row and fill it
        TextView txtRecipeName = (TextView) row.findViewById(R.id.txtRecipeName);
        TextView txtKcals = (TextView) row.findViewById(R.id.txtKcals);
        txtRecipeName.setText(nameInput[position]);
        txtKcals.setText(Integer.toString(kcalsInput[position]));
// remove item
        btnRemove = (Button) row.findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //array to arraylist to array
                ArrayList<String> tempStr = new ArrayList<>(Arrays.asList(nameInput));
                ArrayList<Integer> tempInt = new ArrayList<>();
                for (int i: kcalsInput) {
                    tempInt.add(i);}
                tempStr.remove(position);
                tempInt.remove(position);
                String[] bufferStr = new String[tempStr.size()];
                int[] bufferInt = new int[tempInt.size()];
                for (int x=0; x < bufferInt.length; x++)
                { bufferInt[x] = tempInt.get(x).intValue(); }
                nameInput=tempStr.toArray(bufferStr);
                kcalsInput=bufferInt;

                notifyDataSetChanged();
            }

        });
//the row has been inflated and filled with data, return it.
        return row;  //return convertView;
    }
}