package com.weatherapp.zoe.weatherapp.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherapp.zoe.weatherapp.R;

import static com.weatherapp.zoe.weatherapp.R.string.zipcode;

public class SettingsActivity extends AppCompatActivity{


    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;//add data to the spinner
    EditText inputZipText;
    Switch switch1;
    Button button;
    TextView set;
    TextView days;
    TextView enterZip;
    TextView unit;
    TextView g;
    Switch gpsSwitch;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //keep settings;sharedPreference;
        SharedPreferences settings = getSharedPreferences("PrefFile", 0);


        set = (TextView) findViewById(R.id.settingsText);
        set.setText(R.string.set);

        days = (TextView) findViewById(R.id.daysText);
        days.setText(R.string.days);

        enterZip = (TextView) findViewById(R.id.zipText);
        enterZip.setText(zipcode);

        unit = (TextView) findViewById(R.id.unitText);
        unit.setText(R.string.unit);

        g = (TextView) findViewById(R.id.gText);
        g.setText(R.string.g);

        //EditText;
        inputZipText = (EditText) findViewById(R.id.inputZipText);
        inputZipText.setEnabled(settings.getBoolean("type",true));
        inputZipText.setText(settings.getString("zipcode", "22202"));

        //gpsSwitch; relation with EditText;
        gpsSwitch = (Switch) findViewById(R.id.gpsSwitch);
        gpsSwitch.setChecked(!settings.getBoolean("type",true));
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputZipText.setEnabled(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.methodg), Toast.LENGTH_SHORT).show();
                } else {
                    inputZipText.setEnabled(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.methodz), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //switch(C/F);
        switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setChecked(settings.getBoolean("tempUnit", true));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), getString(R.string.unitc), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.unitf), Toast.LENGTH_SHORT).show();
                }

            }
        });


        //spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.forecastWeather_days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String compareValue = "" + settings.getInt("forecastDate", 3);
        if (!compareValue.equals(null)) {
            int spinnerPosition = adapter.getPosition(compareValue);
            spinner.setSelection(spinnerPosition);
        }

        //interface
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + getString(R.string.select), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //button
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Alert:if EditText is empty;
                if(inputZipText.isEnabled() && inputZipText.getText().toString().matches("")){
                    AlertDialog.Builder zipAlertBuilder = new AlertDialog.Builder(SettingsActivity.this);
                    zipAlertBuilder.setTitle(getString(R.string.ziptitle));
                    zipAlertBuilder.setMessage(getString(R.string.zipmessage));

                    zipAlertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(SettingsActivity.this,getString(R.string.zipclick),Toast.LENGTH_LONG).show();
                        }
                    });

                    AlertDialog zipAlertDialog = zipAlertBuilder.create();
                    zipAlertDialog.show();
                }

                else {
                    SharedPreferences settings = getSharedPreferences("PrefFile", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("tempUnit", switch1.isChecked());
                    editor.putInt("forecastDate", spinner.getSelectedItemPosition() + 1);
                    editor.putBoolean("type",inputZipText.isEnabled());
                    if (inputZipText.isEnabled()) {
                        editor.putString("zipcode", inputZipText.getText().toString());
                    }
                    editor.commit();
                    Intent intent = new Intent(SettingsActivity.this, WeatherActivity.class);
                    startActivity(intent);

                    //indeterminate dialog;
                    ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
                    progressDialog.setTitle(getString(R.string.indetitle));
                    progressDialog.setMessage(getString(R.string.indemessage));
                    progressDialog.show();}


            }
        });


    }
}
