package com.example.kitesurfingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailFragment extends Fragment {

    private String mCountry;
    private String mWhenToGo;
    private Double mLatitude;
    private Double mLongitude;
    private Double mWindProbability;

    public ItemDetailFragment() {
        // Required empty public constructor
    }
    //Salvam datele primite in campuri
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(DetailsActivity.KEYC)) {

            mCountry = getArguments().getString(DetailsActivity.KEYC);
            mWhenToGo = getArguments().getString(DetailsActivity.KEYWHEN);
            mLatitude = getArguments().getDouble(DetailsActivity.KEYLAT);
            mLongitude = getArguments().getDouble(DetailsActivity.KEYLONG);
            mWindProbability = getArguments().getDouble(DetailsActivity.KEYW);
        }
    }
    //Refolosim layoutul activity_details ce va aparea langa layoutul listei,
    //activity_main
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_details, container, false);

        ((TextView) rootView.findViewById(R.id.details_country)).
                setText(mCountry);
        ((TextView) rootView.findViewById(R.id.details_when_to_go)).
                setText(mWhenToGo);
        if(container != null && NetworkManager.isNetworkConnected(container.getContext())) {
            ((TextView) rootView.findViewById(R.id.details_lat)).
                    setText(String.valueOf(mLatitude));
            ((TextView) rootView.findViewById(R.id.details_long)).
                    setText(String.valueOf(mLongitude));
            ((TextView) rootView.findViewById(R.id.details_wind)).
                    setText(String.valueOf(mWindProbability));
        }
        Button button = (Button) rootView.findViewById(R.id.details_geolocation_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGeolocation(v);
            }
        });
        return rootView;
    }
    //Afisare Google Maps in functie de latitudine si longitudinte
    public void displayGeolocation(View view) {

        if(NetworkManager.isNetworkConnected(view.getContext())) {

            String coordinates = "geo:" + String.valueOf(mLatitude) + "," +
                    String.valueOf(mLongitude);
            Uri gmmIntentUri = Uri.parse(coordinates);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {
            Toast.makeText(view.getContext(), "No internet conection", Toast.LENGTH_SHORT).show();
        }
    }

}
