package com.example.clock_inontime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class HelpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
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