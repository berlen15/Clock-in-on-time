package com.example.clock_inontime;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.clock_inontime.entities.TimeCard;
import com.example.clock_inontime.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth firebaseAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button login;
    private Button forgotPass;
    private ProgressDialog progressDialog;

    //Database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize database
        usersDB=FirebaseDatabase.getInstance().getReference();

        //Load preferences (Information of user and app).
        loadPreferences();

        // Analytics Event
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle=new Bundle();
        bundle.putString("Message", "Integración de Firebase completa");
        mFirebaseAnalytics.logEvent("InitScreen",bundle);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Information of layout
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);

        login =(Button)findViewById(R.id.logInButton);
        forgotPass =(Button) findViewById(R.id.forgetPasswordButton);

        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplication(), InfoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loginUser(){
        //Obtenemos el email y la contraseña desde las cajas de texto
        final String emailString = emailEditText.getText().toString().trim();
        final String passwordString  = passwordEditText.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(emailString)){
            Toast.makeText(this,"No se introdujo e-mail",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(passwordString)){
            Toast.makeText(this,"No se introdujo contraseña",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();

        //Login user
        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            int pos = emailString.indexOf("@");
                            String username=emailString.substring(0,pos);
                            Toast.makeText(LoginActivity.this,"Se ha iniciado sesión correctamente",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplication(), HomeActivity.class);

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final String id = user.getUid();
                            final User userLogin = new User(id, emailString, passwordString, new ArrayList<TimeCard>());

                            Query q = FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("email").equalTo(emailString);
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        usersDB.child("usuarios").child(id).child("password").setValue(passwordString);
                                    }else{
                                        usersDB.child("usuarios").child(id).setValue(userLogin);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                            startActivity(intent);
                        }else{

                            Toast.makeText(LoginActivity.this,"Error: Compruebe las credenciales ",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
        savePreferences();
    }

    private void loadPreferences(){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String emailValueRead=preferences.getString("emailUser","");
        String passValueRead=preferences.getString("passUser","");

        if(!emailValueRead.isEmpty() && !passValueRead.isEmpty()){
            // int pos = emailValueRead.indexOf("@");
            // String username=emailValueRead.substring(0,pos);
            Toast.makeText(LoginActivity.this,"Se ha iniciado sesión correctamente",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplication(), HomeActivity.class);
            //intent.putExtra(HomeActivity.user,emailValueRead);
            startActivity(intent);
        }

    }

    private void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String emailValueToSave=emailEditText.getText().toString();
        String passValueToSave=passwordEditText.getText().toString();

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("emailUser",emailValueToSave);
        editor.putString("passUser", passValueToSave);

        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logInButton:
                loginUser();
                break;
            case R.id.forgetPasswordButton:
                Intent intent = new Intent(getApplication(), ForgotPassActivity.class);
                startActivity(intent);
                break;
        }
    }
}
