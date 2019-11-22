package com.example.kitesurfingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerViewKitePlaces;
    private static final String url = "https://internship-2019.herokuapp.com/api-user-get";
    private static final String mail = "{ \"email\": \"t1@gmail.com\" }";
    private static final String listUrl = "https://internship-2019.herokuapp.com/api-spot-get-all";
    public static final String addSpotUrl = "https://internship-2019.herokuapp.com/api-spot-favorites-add";
    public static final String removeSpotUrl = "https://internship-2019.herokuapp.com/api-spot-favorites-remove";
    public static final  String getDetailsUrl = "https://internship-2019.herokuapp.com/api-spot-get-details";
    private static final String defaultPayload = "{ \"country\": \"\", \"windProbability\": \"\" }";
    private static final String countryPayload = "{ \"country\": \"";
    private static final String windPayload = "\", \"windProbability\": ";
    public static final  String bracket = " }";
    public static final String bracketWithQuote = "\" }";
    public static final String bracketWithDoubleQuote = "\"\" }";
    public static final  String spotIdPayload = "{ \"spotId\": \"";
    public static final String CACHEKEY = "cachedData";
    private String countryMessage = "";
    private String windMessage = "";
    private static boolean firstLaunch = true;
    public boolean mTwoPane;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        initView();
        setLayoutManager();

        ParseOperation establishConnection = new ParseOperation();
        establishConnection.execute(url, mail);

        initFirebase();
        initGoogleClient();
        updateDrawer();
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {

        }
    }

    private void initGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void updateDrawer() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personPhoto = acct.getPhotoUrl().toString();


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView navUsername = (TextView) headerView.findViewById(R.id.user_name);
            navUsername.setText(personName);
            TextView navEmail = (TextView) headerView.findViewById(R.id.user_email);
            navEmail.setText(personEmail);

            ImageView navProfileImg = (ImageView) headerView.findViewById(R.id.profile_picture);

            Glide.with(getApplicationContext()).load(personPhoto)
                    .thumbnail(0.2f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(navProfileImg);
        }
    }

    //Aceasta functie parcurge o harta de cheie id si valoare isFavorite, facand
    //apeluri la baza de date pentru fiecare modificare ce trebuie adusa, iar pe
    //langa asta actualizeaza si informatiile cachuite (intr-un mod ineficient
    //este de mentionat : O(n^2), n fiind numarul de elemente din lista). Aceste
    //informatii ce trebuiesc modificate provin din harta creata in onClickListenerFavourites,
    //mai multe detalii acolo.
    public void updateCacheAndDB() throws IOException, ClassNotFoundException {

        OnClickListenerFavourites onClickListenerFavourites = new OnClickListenerFavourites();
        HashMap<String, Boolean> reports = onClickListenerFavourites.
                getmHashMapReportsOfChanges();
        List<KitePlace> cachedKiteList = (List<KitePlace>) InternalStorage.
                readObject(this, CACHEKEY);

        if(reports != null && cachedKiteList != null) {

            for(Map.Entry<String, Boolean> entry : reports.entrySet()) {

                String id = entry.getKey();
                Boolean isFavorite = entry.getValue();

                if(isFavorite) {

                    for(KitePlace place : cachedKiteList) {

                        if(place.getId().compareTo(id) == 0)  {

                            place.setFavourite(true);
                            break;
                        }
                    }
                    if(NetworkManager.isNetworkConnected(this))
                    modifyFavoritesInDB(addSpotUrl, spotIdPayload + id + bracketWithQuote);
                } else {

                    for(KitePlace place : cachedKiteList) {

                        if(place.getId().compareTo(id) == 0) {

                            place.setFavourite(false);
                            break;
                        }
                    }
                    if(NetworkManager.isNetworkConnected(this))
                    modifyFavoritesInDB(removeSpotUrl, spotIdPayload + id + bracketWithQuote);
                }
            }
        reports.clear();
        onClickListenerFavourites.clearmChangedFavouritesIDs();
        }
        InternalStorage.writeObject(this, CACHEKEY, cachedKiteList);
    }
    //Modificari aduse campurilor isFavorite din baza de date
    public void modifyFavoritesInDB(String url, String payload) {

        ParseOperation connectToJsonDB = new ParseOperation();
        connectToJsonDB.execute(url, payload);
    }
    //Apel la baza de date
    public static String getDataFromDB(String url, String payload) {

        ParseOperation connectToJsonDB = new ParseOperation();
        String s = null;
        try {
            s  = connectToJsonDB.execute(url, payload).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return s;
    }

    //La onResume, primim datele legate de vant si numele tarii, precum si o
    //confirmare ce ne garanteza cererea de filtrare a datelor. Construim cererea
    //in payload. Daca este prima lansare (si suntem conectati la internet) sau
    //ne este confirmata cererea de filtrare atunci face o cerere la baza de date
    //pentru aceste date. Daca nu avem aceasta confirmare atunci inseamna ca putem
    //refolosi datele cachuite pentru construirea listei. Acest lucru se va aplica
    //si la prima lansare fara conexiune la internet.
    //Daca nu exista date cachuite la first launch inseamna ca aplicatie este
    //proaspat instalata, deci vom face un apel la baza de data, daca suntem conectati
    //la internet.
    @Override
    protected void onResume() {
        super.onResume();

        String confirmation = null;
        Bundle dataReceiving = getIntent().getExtras();
        if(dataReceiving != null) {
            countryMessage = dataReceiving.getString(FilterActivity.COUNTRYMESSAGE);
            windMessage = dataReceiving.getString(FilterActivity.WINDMESSAGE);
            confirmation = dataReceiving.getString(FilterActivity.CONFMESSAGE);
        }
        String payload;

        if(windMessage != null) {
            if (windMessage.isEmpty()) {
                payload = countryPayload + countryMessage + windPayload + bracketWithDoubleQuote;
            } else {
                payload = countryPayload + countryMessage + windPayload + windMessage + bracket;
            }
        } else {
            payload = defaultPayload;
        }

        if((firstLaunch || (confirmation != null) && NetworkManager.isNetworkConnected(this))) {
            try {
                List<KitePlace> receivedList = parseJsonArray(getDataFromDB(listUrl, payload));
                InternalStorage.writeObject(this, CACHEKEY, receivedList);
                setAdapter(receivedList);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            firstLaunch = false;
        } else {
            try {
                setDefaultUI();
            } catch (IOException | ClassNotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void setDefaultUI() throws IOException, ClassNotFoundException, JSONException {

        List<KitePlace> cachedKitePlaces = (List<KitePlace>) InternalStorage.
                readObject(this, CACHEKEY);

        if(cachedKitePlaces != null) {

            setAdapter(cachedKitePlaces);
        }
        else if(NetworkManager.isNetworkConnected(this)){

            List<KitePlace> receivedList = parseJsonArray(getDataFromDB(listUrl, defaultPayload));
            InternalStorage.writeObject(this, CACHEKEY, receivedList);
            setAdapter(receivedList);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            updateCacheAndDB();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;

        switch (id) {
            case R.id.filter_button:
                intent = new Intent(MainActivity.this,
                        FilterActivity.class);
                if (countryMessage != null && windMessage != null) {
                    try {
                        InternalStorage.writeObject(this,
                                FilterActivity.COUNTRYMESSAGE, countryMessage);
                        InternalStorage.writeObject(this,
                                FilterActivity.WINDMESSAGE, windMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(intent);
                break;

            case R.id.camera_button:

                intent = new Intent(MainActivity.this,
                        CameraActivity.class);
                startActivity(intent);
                break;

            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView() {

        mRecyclerViewKitePlaces = findViewById(R.id.list_of_items);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.destinations);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.main);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setLayoutManager() {

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this);
        mRecyclerViewKitePlaces.setLayoutManager(layoutManager);
    }

    private void setAdapter(List<KitePlace> places) {

        KitePlaceAdapter kitePlaceAdapter = new KitePlaceAdapter(places, this, mTwoPane);
        mRecyclerViewKitePlaces.setAdapter(kitePlaceAdapter);
    }

    public List<KitePlace> parseJsonArray(String payload) throws JSONException {

        List<KitePlace> places = new JSONManager().JSONArrayToObjArray(payload);
        return places;
    }

    public static KitePlace parseJsonObj(String payload) throws JSONException {

        KitePlace place = new JSONManager().JSONObjToObj(payload);
        return place;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }

    //Clasa interna onClickListener, aceasta produce eventul la apasarea pe un
    //item din lista. In cazul in care layoutul este destul de spatios
    //(screenWidth > 600dp), in jumatatea disponibila de ecran vom afisa un fragment
    //cu aceleasi proprietati ca DetailsActivity, facand un apel la baza de date pentru
    //informatii noi in cazul in care suntem conectati la internet, sau folosind
    //datele deja disponibile (nume, tara, isFavorite, whenToGo) daca nu. Daca layoutul
    //nu este destul de spatios vom porni o activitate noua.
    public static class OnClickListenerDetails implements View.OnClickListener {

        private KitePlace mKitePlace;
        private MainActivity mParentActivity;
        private boolean mTwoPane;

        public OnClickListenerDetails(KitePlace mKitePlace, MainActivity mParentActivity, Boolean mTwoPane) {
            this.mKitePlace = mKitePlace;
            this.mParentActivity = mParentActivity;
            this.mTwoPane = mTwoPane;
        }

        @Override
        public void onClick(View v) {

            String id = mKitePlace.getId();
            Boolean isFavorite = mKitePlace.isFavorite();

            if (mTwoPane) {

                Bundle arguments = new Bundle();
                arguments.putString(DetailsActivity.KEYC, mKitePlace.getCountry());
                arguments.putString(DetailsActivity.KEYWHEN, mKitePlace.getWhenToGo());
                if(NetworkManager.isNetworkConnected(v.getContext())) {

                    String jsonData = getDataFromDB(getDetailsUrl, spotIdPayload + id + bracketWithQuote);
                    KitePlace detailedKitePlace = null;
                    try {
                        detailedKitePlace = parseJsonObj(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    arguments.putDouble(DetailsActivity.KEYLONG, detailedKitePlace.getLongitude());
                    arguments.putDouble(DetailsActivity.KEYLAT, detailedKitePlace.getLatitude());
                    arguments.putDouble(DetailsActivity.KEYW, detailedKitePlace.getWindProbability());
                } else {

                    arguments.putDouble(DetailsActivity.KEYLONG, 0);
                    arguments.putDouble(DetailsActivity.KEYLAT, 0);
                    arguments.putDouble(DetailsActivity.KEYW, 0);
                }
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {

                if (NetworkManager.isNetworkConnected(v.getContext())) {

                    String jsonData = getDataFromDB(getDetailsUrl, spotIdPayload + id + bracketWithQuote);
                    KitePlace detailedKitePlace = null;
                    try {
                        detailedKitePlace = parseJsonObj(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.KEYID, detailedKitePlace.getId());
                    intent.putExtra(DetailsActivity.KEYC, detailedKitePlace.getCountry());
                    intent.putExtra(DetailsActivity.KEYLAT, detailedKitePlace.getLatitude());
                    intent.putExtra(DetailsActivity.KEYLONG, detailedKitePlace.getLongitude());
                    intent.putExtra(DetailsActivity.KEYW, detailedKitePlace.getWindProbability());
                    intent.putExtra(DetailsActivity.KEYWHEN, detailedKitePlace.getWhenToGo());
                    intent.putExtra(DetailsActivity.KEYNAME, detailedKitePlace.getName());
                    intent.putExtra(DetailsActivity.KEYFAV, isFavorite);
                    v.getContext().startActivity(intent);
                } else {

                    Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.KEYID, mKitePlace.getId());
                    intent.putExtra(DetailsActivity.KEYC, mKitePlace.getCountry());
                    intent.putExtra(DetailsActivity.KEYLAT, 0);
                    intent.putExtra(DetailsActivity.KEYLONG, 0);
                    intent.putExtra(DetailsActivity.KEYW, 0);
                    intent.putExtra(DetailsActivity.KEYWHEN, mKitePlace.getWhenToGo());
                    intent.putExtra(DetailsActivity.KEYNAME, mKitePlace.getName());
                    intent.putExtra(DetailsActivity.KEYFAV, isFavorite);
                    v.getContext().startActivity(intent);
                }
            }
        }
    }
}
