package org.boudereaux.formview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Laëtitia on 02/12/2017.
 */

public class IngredientAdapter extends ArrayAdapter<String> {
    //tweets est la liste des models à afficher


    public IngredientAdapter(Context context, List<String> ingredients_chosen) {
        super(context, 0, ingredients_chosen);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ingredient,parent, false);
        }

        CocktailViewHolder viewHolder = (CocktailViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new CocktailViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.clear = (Button) convertView.findViewById(R.id.clear);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        final String ingredient = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(ingredient);
        viewHolder.clear.setContentDescription(ingredient);

        viewHolder.clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IngredientAdapter.this.remove(ingredient);
                IngredientAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public class CocktailViewHolder {
        public TextView name;
        public Button clear;
    }



}