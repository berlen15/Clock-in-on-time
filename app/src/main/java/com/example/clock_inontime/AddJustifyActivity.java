package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.Manifest.permission;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clock_inontime.dialog.DialogChangeForgotPass;
import com.example.clock_inontime.dialog.DialogConfirmJustify;
import com.example.clock_inontime.dialog.DialogJustifyCurrentDay;
import com.example.clock_inontime.entities.TimeCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddJustifyActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    Uri pdfUri;
    ProgressDialog progressDialog;
    //Elements of layout
    private Button send, cancel, file;
    private EditText justification;
    private TextView notificationFile;

    private DatabaseReference usersDB;
    String today, username;
    private FirebaseStorage storage;
    Application app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.app=getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_justify);
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        usersDB=FirebaseDatabase.getInstance().getReference();

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

        DialogJustifyCurrentDay dialog = new DialogJustifyCurrentDay();
        dialog.show(getSupportFragmentManager(), "dialogJustify");

        //Obtain buttons of layout
        send = (Button) findViewById(R.id.sendJustification);
        cancel = (Button)findViewById(R.id.cancel);
        file = (Button)findViewById(R.id.selectFile);
        //EditText
        justification=(EditText)findViewById(R.id.justification);

        //TextView
        notificationFile=(TextView)findViewById(R.id.notificationFile);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            today=extras.getString("today");
            username=extras.getString("nombre_apellidos");
        }
        //User id
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        final String id = userId.getUid();
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddJustifyActivity.this, permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }else{
                    ActivityCompat.requestPermissions(AddJustifyActivity.this, new String[]{permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), HomeActivity.class);
                startActivity(intent);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri!=null){
                    uploadFile(pdfUri);
                    Query q = FirebaseDatabase.getInstance().getReference("fichajes").orderByChild("userid").equalTo(id);
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            TimeCard timeCard = new TimeCard(today, "", "", id, "", "", justification.getText().toString(), 0.0, 0.0);
                            usersDB.child("fichajes").child(id).child(today).setValue(timeCard);
                            DialogConfirmJustify dialog = new DialogConfirmJustify(app);
                            dialog.show(getSupportFragmentManager(), "dialogConfirmJustify");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }else{
                    Toast.makeText(AddJustifyActivity.this, "Selecciona un archivo", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void uploadFile(Uri pdfUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Subiendo justificante...");
        progressDialog.setProgress(0);
        progressDialog.show();
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        final String id = userId.getUid();
        final String fileName=""+today+"_"+System.currentTimeMillis()+".pdf";
        final String fileName1 = today+"_"+System.currentTimeMillis();
        StorageReference storageReference=storage.getReference();
        storageReference.child("Uploads").child(fileName).putFile(pdfUri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        usersDB.child("justificantes").child(id).child(fileName1).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddJustifyActivity.this, "Justificación subida con éxito", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplication(), HomeActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(AddJustifyActivity.this, "No se ha subido el archivo con éxtio:(", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddJustifyActivity.this, "No se ha subido el archivo con éxtio:(", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress= (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectFile();
        }else{
            Toast.makeText(this, "Por favor, proporcione permisos a la aplicación", Toast.LENGTH_SHORT).show();

        }
    }

    private void selectFile(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode==RESULT_OK && data!=null) {
            pdfUri=data.getData();
            notificationFile.setText("Archivo seleccionado: "+data.getData().getLastPathSegment());
        }else{
            Toast.makeText(this, "Por favor, seleccione un archivo", Toast.LENGTH_SHORT).show();
        }

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
