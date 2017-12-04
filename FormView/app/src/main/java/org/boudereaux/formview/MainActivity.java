package org.boudereaux.formview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ListView mListView;
    List<String> ingredients_chosen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ingredients_chosen = new ArrayList<String>();
        setIngredientsSpinner();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setIngredientsSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> ingredients_list = getIngredients();

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,ingredients_list){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    spinner.setAdapter(spinnerArrayAdapter);


                    ingredients_chosen.add(selectedItemText);
                    updateListView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ArrayList<String> getIngredients() {
        String url="http://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";
        ArrayList<String> ingredients = new ArrayList<String>();
        ingredients.add("Select an ingredient...");

        HttpGetRequest request = new HttpGetRequest();
        try {
            String result = request.execute(url).get();
            JSONObject jObject  = new JSONObject(result);
            JSONArray ingred = jObject.getJSONArray("drinks"); // get data object
            int number_ingredient = ingred.length();
            for (int i=0; i<number_ingredient; i++) {
                JSONObject ingredient = ingred.getJSONObject(i);
                String name_ingred = ingredient.getString("strIngredient1");
                ingredients.add(name_ingred);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    void updateListView() {
        mListView = (ListView) findViewById(R.id.listView);
        IngredientAdapter adapter = new IngredientAdapter(MainActivity.this, ingredients_chosen);
        mListView.setAdapter(adapter);
    }

    public void onClick(View v) {
        CheckBox cocktail_cat = (CheckBox)findViewById(R.id.cocktail_cat);
        CheckBox shot_cat = (CheckBox)findViewById(R.id.shot_cat);
        CheckBox beer_cat = (CheckBox)findViewById(R.id.beer_cat);
        CheckBox punch_cat = (CheckBox)findViewById(R.id.punch_cat);
        CheckBox milk_cat = (CheckBox)findViewById(R.id.milk_cat);

        RadioGroup radio_alcohol = (RadioGroup) findViewById(R.id.radio_alcohol);
        int selectedId = radio_alcohol.getCheckedRadioButtonId();

        RadioButton alcohol = (RadioButton) findViewById(R.id.alcohol_check);
        RadioButton no_alcohol = (RadioButton) findViewById(R.id.no_alcohol_check);

        SharedPreferences settings = getSharedPreferences("choices", 0);

        SharedPreferences.Editor editor = settings.edit();

        if (selectedId == alcohol.getId()) {
            editor.putString("alcohol","Alcoholic");
        } else if (selectedId == no_alcohol.getId()){
            editor.putString("alcohol","Non_alcoholic");
        } else {
            editor.putString("alcohol","");
        }

        if ((cocktail_cat.isChecked() && punch_cat.isChecked() && milk_cat.isChecked() && beer_cat.isChecked() && shot_cat.isChecked()) ||
                (!cocktail_cat.isChecked() && !punch_cat.isChecked() && !milk_cat.isChecked() && !beer_cat.isChecked() && !shot_cat.isChecked())) {
            editor.putBoolean("all_kind",true);
        } else {
            editor.putBoolean("all_kind",false);
            if (cocktail_cat.isChecked()) {
                editor.putBoolean("Cocktail",true);
            } else {
                editor.putBoolean("Cocktail",false);
            }

            if (milk_cat.isChecked()) {
                editor.putBoolean("Milk / Float / Shake",true);
            } else {
                editor.putBoolean("Milk / Float / Shake",false);
            }


            if (beer_cat.isChecked()) {
                editor.putBoolean("Beer",true);
            } else {
                editor.putBoolean("Beer",false);
            }


            if (shot_cat.isChecked()) {
                editor.putBoolean("Shot",true);
            } else {
                editor.putBoolean("Shot",false);
            }


            if (punch_cat.isChecked()) {
                editor.putBoolean("Punch / Party Drink",true);
            } else {
                editor.putBoolean("Punch / Party Drink",false);
            }
        }

        Set<String> ingredients = new ArraySet<String>(ingredients_chosen);
        editor.putStringSet("Ingredients",ingredients);

        // Commit the edits!
        editor.commit();
        Intent intent = new Intent(this, CocktailActivity.class);
        startActivity(intent);

    }


}
