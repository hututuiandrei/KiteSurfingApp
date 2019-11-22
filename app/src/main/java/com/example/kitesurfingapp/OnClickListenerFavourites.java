package com.example.kitesurfingapp;

import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.HashSet;

public class OnClickListenerFavourites implements View.OnClickListener {

    private String id;
    private KitePlace currentKitePlace;
    private ImageView mImageViewStar;
    private static HashMap<String, Boolean> mHashMapReportsOfChanges = new HashMap<>();
    private static HashSet<String> mChangedFavouritesIDs = new HashSet<>();

    public OnClickListenerFavourites(){}

    public OnClickListenerFavourites(KitePlace mKitePlace, ImageView mImageView) {
        this.currentKitePlace = mKitePlace;
        this.mImageViewStar = mImageView;
        this.id = currentKitePlace.getId();
    }
    @Override
    public void onClick(View v) {

        boolean isFavorite = currentKitePlace.isFavorite();
        if(isFavorite) {

            mImageViewStar.setImageResource(R.drawable.star_off);

        } else {

            mImageViewStar.setImageResource(R.drawable.star_on);
        }
        currentKitePlace.setFavourite(!isFavorite);
        IDtoMap(id, currentKitePlace.isFavorite());
    }
    //Aceasta metoda se foloseste de un hashmap si un hashset pentru a retine
    //perechi cheie valoare, id - isFavorite. Pentru a nu face apel la baza de
    //date mereu cand se apasa pe o stea, aceste intentii le vom pastra intr-un
    //hashmap, si id urile intr-un hashset. Daca in hashset exista deja id ul
    //item ulul pe care vrem sa aplicam modificarea inseamna ca steaua a fost
    //apasata de fapt de 2 (4,6,8..) ori, ceea ce inseamna ca ea a revenit la
    //starea ei initiala, deci nu necesita ca baza de date sa fie modificata.
    //Astfel micsoram numarul de apeluri inutile la baza de date si crestem
    //substantial fluiditatea UI ului.
    public void IDtoMap(String id, boolean isFavorite) {

        if(mChangedFavouritesIDs.contains(id)) {

            mHashMapReportsOfChanges.remove(id);
            mChangedFavouritesIDs.remove(id);
        } else {

            mChangedFavouritesIDs.add(id);
            mHashMapReportsOfChanges.put(id, isFavorite);
        }
    }

    public HashMap<String, Boolean> getmHashMapReportsOfChanges() {
        return mHashMapReportsOfChanges;
    }

    public void clearmChangedFavouritesIDs() {

        OnClickListenerFavourites.mChangedFavouritesIDs.clear();
    }
}
