package com.example.kitesurfingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;

public class FilterActivity extends AppCompatActivity {

    private EditText mEditTextCountry;
    private EditText mEditTextWind;
    public static final String CONFMESSAGE = "confirmation";
    public final static String COUNTRYMESSAGE = "country";
    public final static String WINDMESSAGE = "wind";

    //La onCreate preluam datele cachuite anterior in MainActivity si
    //le afisam in EditText
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            mEditTextCountry.setText(InternalStorage.readObject(this, COUNTRYMESSAGE)
                    .toString());
            mEditTextWind.setText(InternalStorage.readObject(this, WINDMESSAGE)
                    .toString());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    //In cazul rotirii ecranului scrisul va fi salvat in outState, dupa
    //revenirea in noul mod (landscape/portrait) acesta va fi reafisat in
    //EditText cu ajutorul savedInstanceState
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(COUNTRYMESSAGE, mEditTextCountry.getText().toString());
        outState.putString(WINDMESSAGE, mEditTextWind.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mEditTextCountry.setText(savedInstanceState.getString(COUNTRYMESSAGE));
        mEditTextWind.setText(savedInstanceState.getString(WINDMESSAGE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void initView() {

        mEditTextCountry = findViewById(R.id.edit_text_country);
        mEditTextWind = findViewById(R.id.edit_text_wind);
    }

    //Trimitem intr-un intent un mesaj catre MainActivity in care specificam
    //datele prezente curent in EditText. Acest lucru nu se poate realiza fara
    //conexiune la internet
    public void sendMessage(View view) {

        if(NetworkManager.isNetworkConnected(this)) {
            String sendCountryMsg = mEditTextCountry.getText().toString();
            String sendWindMsg = mEditTextWind.getText().toString();
            String confirmation = "confirm";

            Intent sendMessage = new Intent(FilterActivity.this, MainActivity.class);
            sendMessage.putExtra(COUNTRYMESSAGE, sendCountryMsg);
            sendMessage.putExtra(WINDMESSAGE, sendWindMsg);
            sendMessage.putExtra(CONFMESSAGE, confirmation);
            sendMessage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(sendMessage);
            finish();
        } else {

            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
