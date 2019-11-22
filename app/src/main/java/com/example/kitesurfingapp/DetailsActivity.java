package com.example.kitesurfingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static final String KEYC = "keyC";
    public static final String KEYLAT = "keyLat";
    public static final String KEYLONG= "keyLong";
    public static final String KEYW = "keyW";
    public static final String KEYWHEN = "keyWhen";
    public static final String KEYNAME = "keyName";
    public static final String KEYFAV = "keyFav";
    public static final String KEYID = "keyID";

    private TextView mTextViewCountry;
    private TextView mTextViewLatitude;
    private TextView mTextViewLongitude;
    private TextView mTextViewWindProbability;
    private TextView mTextViewWhenToGo;

    private MenuItem mMenuItemStarOn;
    private MenuItem mMenuItemStarOff;

    private boolean initialStar;
    private boolean currentStar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {

        mTextViewCountry = findViewById(R.id.details_country);
        mTextViewLatitude = findViewById(R.id.details_lat);
        mTextViewLongitude = findViewById(R.id.details_long);
        mTextViewWindProbability = findViewById(R.id.details_wind);
        mTextViewWhenToGo = findViewById(R.id.details_when_to_go);
        findViewById(R.id.activity_details).setBackgroundResource(R.drawable.sky);
    }

//    In metoda onResume vom primi intr-un Bundle datele necesare pentru
//    completarea TextView-urilor. Daca device-ul nu este conectat la internet
//    atunci singurele date afisate vor fi campurile Country, WhenToGo si
//    Name care sunt deja cachuite.
    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        if(NetworkManager.isNetworkConnected(this)) {

            mTextViewLatitude.setText(String.valueOf(bundle.getDouble(KEYLAT)));
            mTextViewLongitude.setText(String.valueOf(bundle.getDouble(KEYLONG)));
            String percentage = String.valueOf(bundle.getDouble(KEYW)) + " %";
            mTextViewWindProbability.setText(percentage);
        } else {

            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
        mTextViewCountry.setText(bundle.getString(KEYC));
        mTextViewWhenToGo.setText(bundle.getString(KEYWHEN));
        id = bundle.getString(KEYID);
        initialStar = bundle.getBoolean(KEYFAV);
        currentStar = initialStar;
        getSupportActionBar().setTitle(bundle.getString(KEYNAME));
    }
    //La selectia butonului de "back", verificam daca a fost adusa vreo
    //modificare campului isFavorite, daca da, atunci facem un apel
    //la baza de date pentru modificarea acestei valori si apoi aducem
    //modificari in datele cachuite
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(currentStar != initialStar) {
                    if (currentStar) {
                        new MainActivity().modifyFavoritesInDB(MainActivity.addSpotUrl,
                                MainActivity.spotIdPayload + id +
                                        MainActivity.bracketWithQuote);

                    } else {
                        new MainActivity().modifyFavoritesInDB(MainActivity.removeSpotUrl,
                                MainActivity.spotIdPayload + id +
                                        MainActivity.bracketWithQuote);
                    }
                    try {
                        List<KitePlace> cachedList = (List<KitePlace>) InternalStorage.
                                readObject(this, MainActivity.CACHEKEY);

                        for(KitePlace place : cachedList) {

                            if(place.getId().compareTo(id) == 0) {

                                place.setFavourite(currentStar);
                            }
                        }
                        InternalStorage.writeObject(this, MainActivity.CACHEKEY,cachedList);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Pregatim steaua, aceasta trebuie sa aiba aceiasi valoare (plina/goala)
    //ca si cea din MainActivity
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.details_menu, menu);
        mMenuItemStarOn = menu.findItem(R.id.favorite_button_on);
        mMenuItemStarOff = menu.findItem(R.id.favorite_button_off);
        Bundle bundle = getIntent().getExtras();
        mMenuItemStarOff.setVisible(!bundle.getBoolean(KEYFAV));
        mMenuItemStarOn.setVisible(bundle.getBoolean(KEYFAV));
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    //In bara se afla de fapt 2 itemi ce reprezinta imaginea unei
    //stele selectare si deselectate, la onClick, steaua curenta se
    //ascunde, si cealalta apare
    public void starIsOff(MenuItem item) {

        currentStar = !currentStar;
        item.setVisible(false);
        mMenuItemStarOff.setVisible(true);
    }

    public void starIsOn(MenuItem item) {

        currentStar = !currentStar;
        item.setVisible(false);
        mMenuItemStarOn.setVisible(true);
    }

    public void displayGeolocation(View view) {

        if(NetworkManager.isNetworkConnected(this)) {
            Bundle bundle = getIntent().getExtras();
            String coordinates = "geo:" + String.valueOf(bundle.getDouble(KEYLAT)) + "," +
                    String.valueOf(bundle.getDouble(KEYLONG));
            Uri gmmIntentUri = Uri.parse(coordinates);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No internet conection", Toast.LENGTH_SHORT).show();
        }
    }
}
