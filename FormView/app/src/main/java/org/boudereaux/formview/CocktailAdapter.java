package org.boudereaux.formview;

/**
 * Created by Laëtitia on 02/12/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;


public class CocktailAdapter extends ArrayAdapter<Cocktail> {

    //tweets est la liste des models à afficher
    public CocktailAdapter(Context context, List<Cocktail> cocktails) {
        super(context, 0, cocktails);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Cocktail cock = getItem(position);

        final Context context = parent.getContext();
        final CocktailViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_cocktail,parent, false);
            viewHolder = new CocktailViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CocktailViewHolder) convertView.getTag();
        }


        viewHolder.name.setText(cock.getName());
        Picasso.with(context).load(cock.getPicture()).into(viewHolder.picture);

        if (cock.isFav()) {
            viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_yellow,0,0,0);
        } else {
            viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star,0,0,0);
        }

        if (cock.isDone()) {

            viewHolder.done.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green,0,0,0);
        } else {
            viewHolder.done.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0);
        }

        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cock.setFav(!cock.isFav());
                CocktailAdapter.this.notifyDataSetChanged();
            }
        });

        viewHolder.done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cock.setDone(!cock.isDone());
                CocktailAdapter.this.notifyDataSetChanged();
            }
        });

        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),CocktailDescriptionActivity.class);
                intent.putExtra("id", cock.getId());
                view.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    public class CocktailViewHolder {
        public TextView name;
        public ImageView picture;
        public Button favorite;
        public Button done;

        CocktailViewHolder(View linha) {
            name= (TextView) linha.findViewById(R.id.Name);
            picture = (ImageView) linha.findViewById(R.id.picture );
            favorite = (Button) linha.findViewById(R.id.favorite);
            done = (Button) linha.findViewById(R.id.done);
        }
    }

}