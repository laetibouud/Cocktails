package org.boudereaux.formview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
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
            createView();
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


    void createView() throws JSONException {
        List<JSONArray> cocktails_list = new ArrayList<JSONArray>();

        SharedPreferences settings = getSharedPreferences("choices", 0);

        TextView cat_view = (TextView)findViewById(R.id.cat);
        List<String> text_cat = new ArrayList<String>();

        boolean all_kind=settings.getBoolean("all_kind",false);
        String txt = "";
        if (all_kind) {
            txt = "Any kind of drinks";
            Log.i("ALLKIND",txt);
            JSONArray cocktail_cat_list = getCocktailsByFilter(url_filter_cat+"Cocktail");
            cocktails_list.add(cocktail_cat_list);
            JSONArray list = getCocktailsByFilter(url_filter_cat+"Shot");
            cocktails_list.add(list);
            list = getCocktailsByFilter(url_filter_cat+"Beer");
            cocktails_list.add(list);
            list = getCocktailsByFilter(url_filter_cat+"Milk_/_Float_/_Shake");
            cocktails_list.add(list);
            list = getCocktailsByFilter(url_filter_cat+"Punch_/_Party_Drink");
            cocktails_list.add(list);
            list = getCocktailsByFilter(url_filter_cat+"Ordinary_Drink");
            cocktails_list.add(list);
        } else {

            boolean cocktail_cat = settings.getBoolean("Cocktail",true);
            if (cocktail_cat) {
                JSONArray cocktail_cat_list = getCocktailsByFilter(url_filter_cat+"Cocktail");
                cocktails_list.add(cocktail_cat_list);
                JSONArray list_ordinary = getCocktailsByFilter(url_filter_cat+"Ordinary_Drink");
                cocktails_list.add(list_ordinary);
                text_cat.add("Cocktail");
            }
            boolean shot_cat = settings.getBoolean("Shot",true);
            if (shot_cat) {
                JSONArray list = getCocktailsByFilter(url_filter_cat+"Shot");
                cocktails_list.add(list);
                text_cat.add("Shot");
            }
            boolean beer_cat = settings.getBoolean("Beer",true);
            if (beer_cat) {
                JSONArray list = getCocktailsByFilter(url_filter_cat+"Beer");
                cocktails_list.add(list);
                text_cat.add("Beer");
            }

            boolean milk_cat = settings.getBoolean("Milk / Float / Shake",true);
            if (milk_cat) {
                JSONArray list = getCocktailsByFilter(url_filter_cat+"Milk_/_Float_/_Shake");
                cocktails_list.add(list);
                text_cat.add("Milk");
            }
            boolean punch_cat = settings.getBoolean("Punch / Party Drink",true);
            if (punch_cat) {
                JSONArray list = getCocktailsByFilter(url_filter_cat+"Punch_/_Party_Drink");
                cocktails_list.add(list);
                text_cat.add("Punch");
            }
            for (int i=0; i<text_cat.size()-1; i++) {
                txt = txt + text_cat.get(i) + ", ";
            }
            txt = txt + text_cat.get(text_cat.size()-1);

        }
        List<Cocktail> list_cat = getCocktails(cocktails_list);
        cat_view.setText(txt);


        TextView alcohol_description = (TextView)findViewById(R.id.alcohol);
        String txt_alcohol = "";
        List<JSONArray> cocktails_alcohol_ing = new ArrayList<JSONArray>();
        String alcohol = settings.getString("alcohol","");
        if (!alcohol.equals("")) {
            JSONArray list = getCocktailsByFilter(url_filter_alcohol+alcohol);
            cocktails_alcohol_ing.add(list);
            if (alcohol=="Alcoholic") {
                txt_alcohol = "With";
            } else {
                txt_alcohol= "Without";
            }
        } else {
            txt_alcohol="With or without";
        }
        alcohol_description.setText(txt_alcohol);


        TextView ingredients_description=(TextView)findViewById(R.id.ingredients);
        String txt_ing = "";
        Set<String> ingredients = new ArraySet<String>();
        List<String> list_ingredients = new ArrayList<>(settings.getStringSet("Ingredients",ingredients));
        int size = list_ingredients.size();
        if (size==0) txt_ing="Any ingredients";
        else {
            for (int i=0;i<size-1;i++) {
                String ing = list_ingredients.get(i);
                JSONArray list = getCocktailsByFilter(url_filter_ingredients+ing);
                cocktails_alcohol_ing.add(list);
                txt_ing=txt_ing+ ing + ", ";
            }
            String ing = list_ingredients.get(size-1);
            JSONArray list = getCocktailsByFilter(url_filter_ingredients+ing);
            cocktails_alcohol_ing.add(list);
            txt_ing=txt_ing+ ing;
        }
        ingredients_description.setText(txt_ing);
        final CocktailAdapter adapter;
        List<Cocktail> lists = new ArrayList<Cocktail>();
        if (cocktails_alcohol_ing.isEmpty()) {
            lists=list_cat;
        } else {
            lists = getCocktailsFiltered(cocktails_alcohol_ing,list_cat);
        }

        adapter = new CocktailAdapter(CocktailActivity.this, lists);
        mListView.setAdapter(adapter);

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
                    cock.setFav(false);
                    cock.setDone(false);
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
        for (int j=0; j<size_request_cocktails; j++) {
            String drink_id = init_list.get(j).getId();
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
            }
        }
        return cocktails;
    }

    public void onEdit(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
