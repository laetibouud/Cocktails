package org.boudereaux.sound_test;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity{
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        List<Cocktail> cocktails = new ArrayList<Cocktail>();
        cocktails = getCocktailsByFilterAlcoholic("Alcoholic");
        int size = cocktails.size();
        if (size != 0) {
            Log.i("size list",Integer.toString(size));
            CocktailAdapter adapter = new CocktailAdapter(MainActivity.this, cocktails);
            mListView.setAdapter(adapter);
        }
        setSpinner();
    }

    Cocktail getCocktailById(int id) {
        String url="http://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=" + Integer.toString(id);
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

    List<Cocktail> getRandomsCocktails(int number_cocktails) {
        String url="http://www.thecocktaildb.com/api/json/v1/1/random.php";
        List<Cocktail> listCocktails = new ArrayList<Cocktail>();

        for(int i=0; i<number_cocktails; i++) {
            HttpGetRequest request = new HttpGetRequest();
            try {
                String result = request.execute(url).get();
                JSONObject jObject  = new JSONObject(result);
                JSONArray drinks = jObject.getJSONArray("drinks");
                JSONObject drink = drinks.getJSONObject(0);
                Cocktail cocktail = new Cocktail();

                cocktail.setName(drink.getString("strDrink"));
                cocktail.setId(drink.getString("idDrink"));
                cocktail.setPicture(drink.getString("strDrinkThumb"));
                listCocktails.add(cocktail);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listCocktails;
    }

    List<Cocktail> getCocktailsByFilterAlcoholic(String alcohol) {
        String url="http://www.thecocktaildb.com/api/json/v1/1/filter.php?a="+alcohol;
        List<Cocktail> listCocktails = new ArrayList<Cocktail>();

        HttpGetRequest request = new HttpGetRequest();

        try {
            String result = request.execute(url).get();
            JSONObject jObject  = new JSONObject(result);
            JSONArray drinks = jObject.getJSONArray("drinks");
            int size = drinks.length();
            for(int i=0; i<size; i++) {
                JSONObject drink = drinks.getJSONObject(i);
                Cocktail cocktail = new Cocktail();
                cocktail.setName(drink.getString("strDrink"));
                cocktail.setId(drink.getString("idDrink"));
                cocktail.setPicture(drink.getString("strDrinkThumb"));
                listCocktails.add(cocktail);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listCocktails;
    }

    List<Cocktail> getCocktails(List<JSONArray> drinks) throws JSONException {
        int size_list = drinks.size();
        List<String> ids = new ArrayList<String>();
        List<Cocktail> cocktails = new ArrayList<Cocktail>();
        for (int i=0; i<size_list; i++) {
            JSONArray cocktailsList = drinks.get(i);
            int size_request_cocktails = cocktailsList.length();
            for (int j=0; j<size_request_cocktails; j++) {
                JSONObject drink = cocktailsList.getJSONObject(j);
                if (!ids.contains(drink.getString("idDrink"))) {
                    ids.add(drink.getString("idDrink"));
                    Cocktail cocktail = new Cocktail();
                    cocktail.setPicture(drink.getString("strDrinkThumb"));
                    cocktail.setId(drink.getString("idDrink"));
                    cocktail.setName(drink.getString("strDrink"));
                    cocktails.add(cocktail);
                }
            }
        }
        return cocktails;
    }

    void setSpinner() {
        final String[] select_qualification = {
                "Select Qualification", "10th / Below", "12th", "Diploma", "UG",
                "PG", "Phd"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<StateVO> listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(select_qualification[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, 0,listVOs);
        spinner.setAdapter(myAdapter);
    }
}