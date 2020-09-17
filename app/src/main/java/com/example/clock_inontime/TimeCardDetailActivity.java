package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TimeCardDetailActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String entryGlobal, outGlobal;
    TextView entryDetail, outDetail, dateDetail, entryLocationDetail, outLocationDetail,justificationText,workHourDetail;
    Button locationEntry, locationOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_card_detail);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Fragment fr = null;
                switch(item.getItemId()){
                    case R.id.homeFr:
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.justifiesFr:
                        Intent intent1 = new Intent(getApplicationContext(), JustifyListActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.profileFr:
                        Intent intent2 = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.timecardFr:
                        Intent intent3 = new Intent(getApplicationContext(), TimeCardListActivity.class);
                        startActivity(intent3);
                        break;

                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        workHourDetail=(TextView)findViewById(R.id.workHour);
        dateDetail=(TextView)findViewById(R.id.dateDetail);
        entryDetail=(TextView)findViewById(R.id.entryDetail);
        justificationText=(TextView)findViewById(R.id.justificationText);
        outDetail=(TextView)findViewById(R.id.outDetail);
        entryLocationDetail=(TextView)findViewById(R.id.entryLocationDetail);
        outLocationDetail=(TextView)findViewById(R.id.outLocationDetail);

        locationEntry = (Button) findViewById(R.id.locationEntry);
        locationOut = (Button) findViewById(R.id.locationOut);

        Bundle extras = getIntent().getExtras();
        String date="";
        String out="";
        String entry="";
        String entryLocation="";
        String outLocation="";
        String justify="";
        String workHour="";
        if (extras!=null){
            workHour=extras.getString("workHour");
            date = extras.getString("dateDetail");
            out=extras.getString("outDetail");
            entry=extras.getString("entryDetail");
            entryLocation=extras.getString("entryLocationDetail");
            outLocation=extras.getString("outLocationDetail");
            justify=extras.getString("justifyDetail");

        }
        entryGlobal=entryLocation;
        outGlobal=outLocation;
        if(entryGlobal.equals("Sin ubicación")){
            locationEntry.setEnabled(false);
        }
        if(outGlobal.equals("Sin ubicación")){
            locationOut.setEnabled(false);
        }
        locationEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.com/maps?q=" +
                        entryGlobal;

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });
        locationOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.com/maps?q=" +
                        outGlobal;

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });
        workHourDetail.setText(workHour);
        dateDetail.setText(date);
        entryDetail.setText(entry);
        justificationText.setText(justify);
        entryLocationDetail.setText("Ubicación: "+entryLocation);
        outDetail.setText(out);
        outLocationDetail.setText("Ubicación: "+outLocation);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.logoutAction){
            SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
            String emailValueRead=preferences.getString("emailUser","");
            String passValueRead=preferences.getString("passUser","");

            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("emailUser","");
            editor.putString("passUser", "");

            editor.commit();

            firebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
        }
        if(id == R.id.help){
            Intent intent = new Intent(getApplication(), HelpActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
