package com.nesterov.owngame;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<ScoreItem> {

    private int resourceLayout;
    private Context mContext;

    public ListAdapter(Context context, int resource, List<ScoreItem> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        ScoreItem p = getItem(position);

        if (p != null) {
            final TextView tt1 = v.findViewById(R.id.score_name);
            TextView tt2 = v.findViewById(R.id.score);
            LinearLayout itemLayout = v.findViewById(R.id.item_layout);

            if (tt1 != null) {
                tt1.setText(p.getName());
                ViewTreeObserver vto = tt1.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (1 < tt1.getLineCount()) {
                            tt1.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                    tt1.getTextSize() - 1);
                        }
                    }
                });
            }

            if (tt2 != null) {
                tt2.setText(String.valueOf(p.getScore()));
            }

            if (p.isItsme())
                itemLayout.setBackgroundColor(Color.parseColor("#0090ff"));
            else
                itemLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        return v;
    }

}