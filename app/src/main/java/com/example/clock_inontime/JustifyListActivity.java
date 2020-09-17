package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.clock_inontime.adapters.AdapterJustify;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class JustifyListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_justify_list);

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

        loadJustifyList();

        recyclerView=findViewById(R.id.rvJustify);

        recyclerView.setLayoutManager(new LinearLayoutManager(JustifyListActivity.this));
        AdapterJustify adapter = new AdapterJustify(recyclerView, JustifyListActivity.this,
                new ArrayList<String>(),new ArrayList<String>());
        recyclerView.setAdapter(adapter);

    }

    private void loadJustifyList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        DatabaseReference justificantes = FirebaseDatabase.getInstance().getReference("justificantes");
        justificantes.child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String fileName = snapshot.getKey();
                String url = snapshot.getValue(String.class);
                ((AdapterJustify)recyclerView.getAdapter()).update(fileName, url);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
