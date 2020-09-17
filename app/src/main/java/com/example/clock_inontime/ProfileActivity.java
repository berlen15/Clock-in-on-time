package com.example.clock_inontime;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDB;
    private TextView emailText, phoneText, nameText, surnameText, cityText, dniText, companyText, nameTitle;
    private ImageView profile_photo;
    private Button edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profile_photo = findViewById(R.id.profile_photo);
        nameTitle = (TextView) findViewById(R.id.nameProfile);
        emailText=(TextView) findViewById(R.id.emailProfile);
        phoneText=(TextView) findViewById(R.id.phoneProfile);
        nameText=(TextView) findViewById(R.id.nameUser);
        surnameText=(TextView) findViewById(R.id.surnameProfile);
        cityText=(TextView) findViewById(R.id.cityProfile);
        cityText=(TextView) findViewById(R.id.cityProfile);
        dniText=(TextView) findViewById(R.id.dniProfile);
        companyText=(TextView) findViewById(R.id.companyProfile);

        edit = (Button) findViewById(R.id.modifyProfile);
        usersDB= FirebaseDatabase.getInstance().getReference();
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
        loadProfile();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        Query q = FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("userid").equalTo(id);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = snapshot.child("nombre").getValue().toString();
                String surname = snapshot.child("apellidos").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String dni = snapshot.child("dni").getValue().toString();
                String company = snapshot.child("empresa").getValue().toString();
                String city = snapshot.child("poblacion").getValue().toString();
                String phone = snapshot.child("telefono").getValue().toString();
                if (name.equals("Belén")){
                    String archivo = "android.resource://"+ getPackageName()+"/"+ R.drawable.belen_photo;
                    Uri ruta = Uri.parse(archivo);

                    profile_photo.setImageURI(ruta);
                }else{
                    profile_photo.setImageResource(R.drawable.ic_person);
                }
                nameText.setText(name);
                nameTitle.setText(name+" "+surname);
                surnameText.setText(surname);
                emailText.setText(email);
                dniText.setText(dni);
                companyText.setText(company);
                cityText.setText(city);
                phoneText.setText(phone);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
