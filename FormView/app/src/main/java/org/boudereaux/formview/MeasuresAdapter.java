package org.boudereaux.formview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Laëtitia on 04/12/2017.
 */

public class MeasuresAdapter  extends ArrayAdapter<String> {
    //tweets est la liste des models à afficher
    public MeasuresAdapter(Context context, List<String> ing) {
        super(context, 0, ing);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final MeasuresAdapter.MeasureViewHolder viewHolder;
        String measures = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_measures_description, parent, false);
            viewHolder = new MeasuresAdapter.MeasureViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MeasuresAdapter.MeasureViewHolder) convertView.getTag();
        }

        Log.i("res",measures);
        String [] result = measures.split("@", 2);
        Log.i("res",result[0]);
        viewHolder.mes.setText(result[0]);
        viewHolder.ing.setText(result[1]);
        return convertView;
    }

    public class MeasureViewHolder {
        public TextView mes;
        public TextView ing;

        public MeasureViewHolder(View convertView) {
            mes= (TextView) convertView.findViewById(R.id.mesView);
            ing= (TextView) convertView.findViewById(R.id.ingView);

        }
    }
}