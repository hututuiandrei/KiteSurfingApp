package com.example.kitesurfingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class KitePlaceViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextViewName;
    private TextView mTextViewCountry;
    private ImageView mImageViewStar;

    public KitePlaceViewHolder(@NonNull View itemView) {
        super(itemView);

        mTextViewName = itemView.findViewById(R.id.text_view_name);
        mTextViewCountry = itemView.findViewById(R.id.text_view_country);
        mImageViewStar = itemView.findViewById(R.id.star_icon);
    }

    public TextView getmTextViewName() {return mTextViewName; }

    public TextView getmTextViewCountry() {return mTextViewCountry; }

    public ImageView getmImageViewStar() {return mImageViewStar; }
}
