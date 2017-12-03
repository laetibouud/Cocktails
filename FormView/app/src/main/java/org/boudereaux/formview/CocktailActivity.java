package org.boudereaux.formview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class CocktailActivity extends AppCompatActivity {
    ListView mListView;
    String url_filter_alcohol="http://www.thecocktaildb.com/api/json/v1/1/filter.php?a=";
    String url_filter_cat="http://www.thecocktaildb.com/api/json/v1/1/filter.php?c=";
    String url_filter_ingredients="http://www.thecocktaildb.com/api/json/v1/1/filter.php?i=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.listView);

        try {
            createList();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    JSONArray getCocktailsByFilter(String url) {
        JSONArray drinks = new JSONArray();
        HttpGetRequest request = new HttpGetRequest();

        try {
            String result = request.execute(url).get();
            JSONObject jObject  = new JSONObject(result);
            drinks = jObject.getJSONArray("drinks");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return drinks;
    }

    void createList() throws JSONException {
        List<JSONArray> cocktails_list = new ArrayList<JSONArray>();

        SharedPreferences settings = getSharedPreferences("choices", 0);

        boolean cocktail_cat = settings.getBoolean("Cocktail",true);
        if (cocktail_cat) {
            JSONArray cocktail_cat_list = getCocktailsByFilter(url_filter_cat+"Cocktail");
            cocktails_list.add(cocktail_cat_list);
        }
        boolean shot_cat = settings.getBoolean("Shot",true);
        if (shot_cat) {
            JSONArray list = getCocktailsByFilter(url_filter_cat+"Shot");
            cocktails_list.add(list);
        }
        boolean beer_cat = settings.getBoolean("Beer",true);
        if (beer_cat) {
            JSONArray list = getCocktailsByFilter(url_filter_cat+"Beer");
            cocktails_list.add(list);
        }
        boolean ordinary_cat = settings.getBoolean("Ordinary Drink",true);
        if (ordinary_cat) {
            JSONArray list = getCocktailsByFilter(url_filter_cat+"Ordinary_Drink");
            cocktails_list.add(list);
        }
        boolean milk_cat = settings.getBoolean("Milk / Float / Shake",true);
        if (milk_cat) {
            JSONArray list = getCocktailsByFilter(url_filter_cat+"Milk_/_Float_/_Shake");
            cocktails_list.add(list);
        }
        boolean punch_cat = settings.getBoolean("Punch / Party Drink",true);
        if (punch_cat) {
            JSONArray list = getCocktailsByFilter(url_filter_cat+"Punch_/_Party_Drink");
            cocktails_list.add(list);
        }
        List<Cocktail> list_cat = getCocktails(cocktails_list);

        List<JSONArray> cocktails_alcohol_ing = new ArrayList<JSONArray>();

        String alcohol = settings.getString("alcohol","");
        Log.i("alcohol",alcohol);
        if (!alcohol.equals("")) {
            JSONArray list = getCocktailsByFilter(url_filter_alcohol+alcohol);
            cocktails_alcohol_ing.add(list);
        }

        Set<String> ingredients = new ArraySet<String>();
        List<String> list_ingredients = new ArrayList<>(settings.getStringSet("Ingredients",ingredients));
        int size = list_ingredients.size();

        for (int i=0;i<size;i++) {
            String ing = list_ingredients.get(i);
            JSONArray list = getCocktailsByFilter(url_filter_ingredients+ing);
            cocktails_alcohol_ing.add(list);
            Log.i("ingredient",ing);
        }

        if (cocktails_alcohol_ing.isEmpty()) {
            Log.i("size list", Integer.toString(list_cat.size()));
            CocktailAdapter adapter = new CocktailAdapter(CocktailActivity.this, list_cat);
            mListView.setAdapter(adapter);
        } else {
            List<Cocktail> real_list = getCocktailsFiltered(cocktails_alcohol_ing,list_cat);
            CocktailAdapter adapter = new CocktailAdapter(CocktailActivity.this, real_list);
            mListView.setAdapter(adapter);
        }

    }

    List<Cocktail> getCocktails(List<JSONArray> drinks) throws JSONException {
        int size_list = drinks.size();
        List<String> ids = new ArrayList<String>();
        List<Cocktail> cocktails = new ArrayList<Cocktail>();

        for (int i=0; i<size_list; i++) {
            JSONArray cocktailsList = drinks.get(i);
            int size_request = cocktailsList.length();
            for (int j=0; j<size_request; j++) {
                JSONObject drink = cocktailsList.getJSONObject(j);
                if (!ids.contains(drink.getString("idDrink"))) {
                    Cocktail cock = new Cocktail();
                    ids.add(drink.getString("idDrink"));
                    cock.setId(drink.getString("idDrink"));
                    cock.setName(drink.getString("strDrink"));
                    cock.setPicture(drink.getString("strDrinkThumb"));
                    Log.i("cock1", cock.getName());
                    cocktails.add(cock);
                }
            }
        }
        return cocktails;
    }

    List<Cocktail> getCocktailsFiltered(List<JSONArray> drinks, List<Cocktail> init_list) throws JSONException {
        int size_list = drinks.size();
        List<String> ids = new ArrayList<String>();
        List<Cocktail> cocktails = new ArrayList<Cocktail>();

        int size_request_cocktails = init_list.size();
        Log.i("List drinks alcohol",Integer.toString(size_list));
        for (int j=0; j<size_request_cocktails; j++) {
            String drink_id = init_list.get(j).getId();
            Log.i("drink_id",drink_id);
            boolean exist_in_list = true;
            for (int i=0;i<size_list;i++) {
                boolean exist_in_object=false;
                JSONArray cocktailsList = drinks.get(i);
                int size_request = cocktailsList.length();
                for (int k=0; k<size_request; k++) {
                    JSONObject drink_compar = cocktailsList.getJSONObject(k);
                    if (drink_compar.getString("idDrink").equals(drink_id)) {
                        exist_in_object=true;
                    }
                }
                if (!exist_in_object) exist_in_list=false;
            }

            if (exist_in_list) {
                cocktails.add(init_list.get(j));
                Log.i("cock2", init_list.get(j).getName());
            }
        }
        return cocktails;
    }

}
