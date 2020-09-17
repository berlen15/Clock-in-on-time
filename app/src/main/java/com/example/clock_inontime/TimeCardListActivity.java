package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.clock_inontime.adapters.Adapter;
import com.example.clock_inontime.entities.TimeCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeCardListActivity extends AppCompatActivity{
    private String dateToSearch="";
    DatePickerDialog picker;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    RecyclerView rv;
    List<TimeCard> list;
    Adapter adapter;
    FirebaseDatabase timeCardDB;
    private FirebaseAuth firebaseAuth;
    Button search;
    Button displaySearch;
    private int day, month, year;
    TextView infoDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_card_list);

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

        infoDate = (TextView)findViewById(R.id.infoDate);
        rv = (RecyclerView)findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        timeCardDB = FirebaseDatabase.getInstance();


        adapter = new Adapter (list);

        rv.setAdapter(adapter);

        search = (Button)findViewById(R.id.searchByDate);
        displaySearch=(Button)findViewById(R.id.searchTimeCard);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        displaySearch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                loadTimeCardFilter(dateToSearch);
            }
        });
        loadTimeCardList();
    }

    private void loadTimeCardList(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        timeCardDB.getReference("fichajes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    for (DataSnapshot data: snapshot.getChildren()){
                        TimeCard timeCard = data.getValue(TimeCard.class);
                        list.add(timeCard);
                        adapter.notifyDataSetChanged();
                    }
                    infoDate.setText("");
                }else{
                    infoDate.setText("Aún no tienes fichajes registrados");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDatePickerDialog(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        String date="";
        // date picker dialog
        picker = new DatePickerDialog(TimeCardListActivity.this,R.style.DatePickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if(monthOfYear+1<10 && dayOfMonth<10){
                            infoDate.setText(year+"-"+"0"+(monthOfYear+1) +"-"+"0"+dayOfMonth);
                        }else if(monthOfYear+1>=10 && dayOfMonth<10){
                            infoDate.setText(year+"-"+(monthOfYear + 1) +"-"+"0"+dayOfMonth);
                        }else if(monthOfYear+1<10 && dayOfMonth>=10){
                            infoDate.setText(year+"-0"+(monthOfYear+1) +"-"+dayOfMonth);
                        }else{
                            infoDate.setText(year+"-"+ (monthOfYear + 1) +"-"+dayOfMonth);
                        }

                    }
                }, year, month, day);
        picker.show();
        dateToSearch=infoDate.getText().toString();
    }
    private void loadTimeCardFilter(final String date_search){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        timeCardDB.getReference("fichajes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot data: snapshot.getChildren()){
                        TimeCard timeCard = data.getValue(TimeCard.class);
                        if(timeCard.getDate().equals(infoDate.getText())){
                            list.clear();
                            list.add(timeCard);
                            adapter.notifyDataSetChanged();
                            infoDate.setText("");
                        }
                    }

                    if(!snapshot.getChildren().toString().contains(infoDate.getText())){
                        infoDate.setText("No existen registros para la fecha seleccionada");
                        list.clear();
                        for (DataSnapshot data: snapshot.getChildren()){
                            TimeCard timeCard = data.getValue(TimeCard.class);
                            list.add(timeCard);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
