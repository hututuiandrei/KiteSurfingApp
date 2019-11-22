package com.example.kitesurfingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class KitePlaceAdapter extends RecyclerView.Adapter<KitePlaceViewHolder> {

    private List<KitePlace> mPlaces;
    private MainActivity parent;
    private Boolean mTwoPane;

    public KitePlaceAdapter(List<KitePlace> mPlaces, MainActivity parent, Boolean mTwoPane) {
        this.mPlaces = mPlaces;
        this.parent = parent;
        this.mTwoPane = mTwoPane;
    }

    @NonNull
    @Override
    public KitePlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kite_place_item,
                viewGroup, false);

        return new KitePlaceViewHolder(itemView);
    }
    @Override
    //La onBind setam clickListener atat pentru stele cat si pentru
    //item in sine
    public void onBindViewHolder(@NonNull final KitePlaceViewHolder kitePlaceViewHolder, int i) {

        final KitePlace currentKitePlace = mPlaces.get(i);
        final ImageView mImageViewStar = kitePlaceViewHolder.getmImageViewStar();
        kitePlaceViewHolder.itemView.setOnClickListener(
                new MainActivity.OnClickListenerDetails(currentKitePlace, parent, mTwoPane));
        if (currentKitePlace != null) {

            if(currentKitePlace.getName() != null) {

                kitePlaceViewHolder.getmTextViewName().setText(currentKitePlace.getName());
            }
            if(currentKitePlace.getCountry() != null) {

                kitePlaceViewHolder.getmTextViewCountry().setText(currentKitePlace.getCountry());
            }
            mImageViewStar.setOnClickListener(
                    new OnClickListenerFavourites(currentKitePlace, mImageViewStar));

            //Interschimbam imaginea stelei din item
            if(currentKitePlace.isFavorite()) {

                mImageViewStar.setImageResource(R.drawable.star_on);
            } else {

                mImageViewStar.setImageResource(R.drawable.star_off);
            }
        }
    }
    @Override
    public int getItemCount() {

        return mPlaces.size();
    }
}
