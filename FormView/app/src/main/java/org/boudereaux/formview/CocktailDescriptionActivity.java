package org.boudereaux.formview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CocktailDescriptionActivity extends AppCompatActivity {
    String url_id="http://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=";
    String id;
    ListView ListViewMeasures;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        ListViewMeasures = (ListView) findViewById(R.id.measures_list);
        createView();
    }

    Cocktail getCocktailById() {
        String url=url_id + id;
        Cocktail cocktail = new Cocktail();

        HttpGetRequest request = new HttpGetRequest();
        try {
            String result = request.execute(url).get();
            JSONObject jObject  = new JSONObject(result);
            JSONArray drinks = jObject.getJSONArray("drinks"); // get data object
            JSONObject drink = drinks.getJSONObject(0);

            cocktail.setName(drink.getString("strDrink"));
            cocktail.setCategory(drink.getString("strCategory"));
            cocktail.setAlcohol(drink.getString("strAlcoholic"));
            cocktail.setInstructions(drink.getString("strInstructions"));
            cocktail.setId(drink.getString("idDrink"));
            cocktail.setPicture(drink.getString("strDrinkThumb"));

            int number_ingredient = 0;
            List<String> ingredients = new ArrayList<String>();
            for (int j=1; j<16; j++) {
                String ingredient_id = "strIngredient" + Integer.toString(j);
                String ingredient_name = drink.getString(ingredient_id);
                if (ingredient_name.isEmpty()) {
                    number_ingredient=j-1;
                    break;
                } else {
                    ingredients.add(ingredient_name);
                }
            }
            cocktail.setIngredients(ingredients);

            List<String> measures = new ArrayList<String>();
            for (int p=1; p<=number_ingredient; p++) {
                String measure_id = "strMeasure" + Integer.toString(p);
                String measure_name = drink.getString(measure_id);
                measures.add(measure_name);
            }
            cocktail.setMeasures(measures);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cocktail;
    }

    private void createView() {
        Cocktail cocktail = getCocktailById();
        ImageView image = (ImageView)findViewById(R.id.image);
        Picasso.with(this).load(cocktail.getPicture()).into(image);

        TextView description = (TextView)findViewById(R.id.description);
        String txt_description = cocktail.getInstructions();
        description.setText(txt_description);

        TextView title = (TextView)findViewById(R.id.title);
        String txt_title = cocktail.getName();
        title.setText(txt_title);

        List<String> mes = cocktail.getMeasures();
        List<String> ing = cocktail.getIngredients();

        List<String> mesing = new ArrayList<String>();

        for (int i=0; i<mes.size(); i++) {
            mesing.add(mes.get(i)+'@'+ing.get(i));
        }

        MeasuresAdapter adapter_mes = new MeasuresAdapter(CocktailDescriptionActivity.this, mesing);
        ListViewMeasures.setAdapter(adapter_mes);
    }

}
