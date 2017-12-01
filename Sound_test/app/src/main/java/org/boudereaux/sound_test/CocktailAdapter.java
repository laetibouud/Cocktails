package org.boudereaux.sound_test;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Laëtitia on 29/11/2017.
 */
public class CocktailAdapter extends ArrayAdapter<Cocktail> {

    //tweets est la liste des models à afficher
    public CocktailAdapter(Context context, List<Cocktail> cocktails) {
        super(context, 0, cocktails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_cocktail,parent, false);
        }

        CocktailViewHolder viewHolder = (CocktailViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new CocktailViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.Name);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Cocktail cocktail = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(cocktail.getName());
        Picasso.with(context).load(cocktail.getPicture()).into(viewHolder.picture);

        return convertView;
    }

    public class CocktailViewHolder {
        public TextView name;
        public ImageView picture;
    }

}