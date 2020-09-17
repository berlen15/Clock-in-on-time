package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private DatabaseReference usersDB;
    EditText cityEdit, telEdit, pass, pass2;
    Button confirm, cancel;
    private String company, dni, name, surname, email, city, phone, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cityEdit=(EditText)findViewById(R.id.citylEdit);
        telEdit=(EditText)findViewById(R.id.telephoneEdit);
        pass=(EditText)findViewById(R.id.passwordEdit);
        pass2=(EditText)findViewById(R.id.passwordEdit2);
        confirm =(Button) findViewById(R.id.confirmEdit);
        cancel =(Button) findViewById(R.id.cancelEdit);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        usersDB= FirebaseDatabase.getInstance().getReference().child("usuarios").child(id);
        loadProfile();
        confirm.setOnClickListener(new View.OnClickListener() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String id = user.getUid();
            @Override
            public void onClick(View v) {
                if(pass.getText().toString().equals(pass2.getText().toString())){
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("password", pass.getText().toString());
                    user.put("telefono", telEdit.getText().toString());
                    user.put("apellidos", surname);
                    user.put("nombre", name);
                    user.put("empresa", company);
                    user.put("poblacion", cityEdit.getText().toString());
                    user.put("dni", dni);
                    user.put("userid", id);


                    usersDB.setValue(user);
                    Intent intent = new Intent(getApplication(), ProfileActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(EditProfileActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
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
                 name = snapshot.child("nombre").getValue().toString();
                 surname = snapshot.child("apellidos").getValue().toString();
                 email = snapshot.child("email").getValue().toString();
                 dni = snapshot.child("dni").getValue().toString();
                 company = snapshot.child("empresa").getValue().toString();
                 city = snapshot.child("poblacion").getValue().toString();
                 phone = snapshot.child("telefono").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                cityEdit.setText(city);
                telEdit.setText(phone);
                pass.setText(password);
                pass2.setText(password);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    public void ShowPassword(View view) {
        if(view.getId()==R.id.iv1){

            if(pass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.hide_password);
                //Show Password
                pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility_black_24dp);
                //Hide Password
                pass.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    public void ShowPassword2(View view) {
        if(view.getId()==R.id.iv2){
            if(pass2.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.hide_password);

                //Show Password
                pass2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility_black_24dp);

                //Hide Password
                pass2.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }
}
