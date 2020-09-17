package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clock_inontime.dialog.DialogJustifyCurrentDay;
import com.example.clock_inontime.dialog.DialogOutTime;
import com.example.clock_inontime.dialog.DialogReminderRegisterLocation;
import com.example.clock_inontime.entities.TimeCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.time.LocalTime.now;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity{
    public static final String user="names";
    public static final String email="names";
    //Location
    private static String entryLocation="";
    private static String outLocation="";
    private String location="";
    private String usernameString="";
    private String usersurnameString;
    private String timeTranscurredString="";

    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    FirebaseUser userId= FirebaseAuth.getInstance().getCurrentUser();;
    //Database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDB;
    private DatabaseReference fichajes;
    TextView username, infoGPS,timeTranscurred;
    Button inputButton, outputButton, justify;
    private LocalDate today;
    private LocalTime entryTime;
    private LocalTime outTime;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private TextView inputTimeText, outputTimeText, dayText, monthText, yearText;

    BottomNavigationView bottomNavigationView;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNav);

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

        //Activate location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        getLocation();
        openDialogReminder();

        //Initialize database
        usersDB=FirebaseDatabase.getInstance().getReference();

        //Initialize auth variables
        final String id = userId.getUid();

        //Initialize Elements of Layout
        username=(TextView)findViewById(R.id.emailUser);
        infoGPS=(TextView)findViewById(R.id.infoGPS);
        timeTranscurred=(TextView)findViewById(R.id.timeTranscurred);
        inputButton=(Button)findViewById(R.id.clockinButton);
        outputButton=(Button)findViewById(R.id.clockoutButton);
        justify = (Button)findViewById(R.id.justify);
        inputTimeText=(TextView)findViewById(R.id.inputTime);
        outputTimeText=(TextView)findViewById(R.id.outputTime);
        dayText =(TextView)findViewById(R.id.dayInfo);
        monthText=(TextView)findViewById(R.id.monthInfo);
        yearText=(TextView)findViewById(R.id.yearInfo);

        //Query to obtain name and surname of user
        Query q = FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("userid").equalTo(id);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = snapshot.child("nombre").getValue().toString();
                String surname = snapshot.child("apellidos").getValue().toString();
                usernameString=name;
                usersurnameString=surname;
                username.setText("Bienvenid@: "+name+" "+surname);
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

        //Initialize datetime variables and set texts
        today=LocalDate.now();
        dayText.setText(""+today.getDayOfMonth());
        monthText.setText(""+today.getMonthValue());
        yearText.setText(""+today.getYear());

        ValidateIfJustifyExist(); //If justify exist, input and output buttons doesn't work
        initializeTimeCardsIfExist(); //If exist timeCard, this method obtain the values.

        //FingerPrint recognition
        final FragmentActivity activity = this;
        final Executor executor = Executors.newSingleThreadExecutor();

        final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("RECONOCIMIENTO DACTILAR")
                .setSubtitle("Se necesita reconocer la huella para fichar")
                .setDescription("Mantenga la huella dactilar en el sensor inferior para iniciar el reconocimiento")
                .setNegativeButton("Cancelar", executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).build();



        //Input button action
        inputButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Obtain current location
                                /*if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);*/
                                if(isLocationEnabled(getApplicationContext())){
                                    entryTime=LocalTime.parse(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(dtf));
                                    entryLocation=location;
                                    inputTimeText.setText("ENTRADA: "+entryTime);
                                    inputButton.setText("Entrada registrada");
                                    inputButton.setEnabled(false);



                                    Query q = FirebaseDatabase.getInstance().getReference("fichajes").orderByChild("userid").equalTo(id);
                                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            TimeCard timeCard = new TimeCard(today.toString(), entryTime.toString(), "",id, entryLocation, "", "",0.0,0.0);
                                            usersDB.child("fichajes").child(id).child(today.toString()).setValue(timeCard);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                }else{
                                    Toast.makeText(activity, "Active el GPS para fichar", Toast.LENGTH_SHORT).show();
                                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                                }
                                Toast.makeText(HomeActivity.this, "Fichaje realizado", Toast.LENGTH_LONG);
                            }
                        });
                    }
                    @Override
                    public void onAuthenticationFailed(){
                        super.onAuthenticationFailed();

                    }
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT) {
                            Toast.makeText(HomeActivity.this, "Su dispositivo no posee el Hardware necesario", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
        //output button action
        outputButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(isLocationEnabled(getApplicationContext())){
                                    if (entryTime == null) {
                                        openDialog();
                                    } else {
                                        outTime = LocalTime.parse(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(dtf));
                                        getLocation();
                                        outLocation = location;
                                        outputTimeText.setText("SALIDA: " + outTime);
                                        outputButton.setText("Salida registrada");
                                        outputButton.setEnabled(false);

                                        final double hour = outTime.getHour() - entryTime.getHour();
                                        final double min = entryTime.getMinute() - outTime.getMinute();
                                        timeTranscurred.setText("Total jornada: " + hour + " horas y " + min + " minutos");
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        final String id = user.getUid();

                                        Query q = FirebaseDatabase.getInstance().getReference("fichajes").orderByChild("userid").equalTo(id);
                                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                TimeCard timeCard = new TimeCard(today.toString(), entryTime.toString(), outTime.toString(), id, entryLocation, outLocation, "", hour, min);
                                                usersDB.child("fichajes").child(id).child(today.toString()).setValue(timeCard);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                    }
                                }else{
                                    Toast.makeText(activity, "Active el GPS para fichar", Toast.LENGTH_SHORT).show();
                                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                                }
                            }
                        });
                    }
                    @Override
                    public void onAuthenticationFailed(){
                        super.onAuthenticationFailed();

                    }
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT) {
                            Toast.makeText(HomeActivity.this, "Su dispositivo no posee el Hardware necesario", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
        //justify button action
        justify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), AddJustifyActivity.class);
                intent.putExtra("today",today.toString());
                intent.putExtra("nombre_apellidos",usernameString);
                startActivity(intent);
            }
        });
    }

    private void initializeTimeCardsIfExist() {
        fichajes = FirebaseDatabase.getInstance().getReference();
        final String id = userId.getUid();
        fichajes.child("fichajes").child(id).child(today.toString()).child("entryTime").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(!snapshot.getValue().toString().equals("")){
                                DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm:ss");
                                entryTime= LocalTime.parse(snapshot.getValue().toString(), f);
                                inputTimeText.setText("ENTRADA: "+snapshot.getValue().toString());
                                inputButton.setEnabled(false);
                                justify.setEnabled(false);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
        fichajes.child("fichajes").child(id).child(today.toString()).child("outTime").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if(snapshot.getValue().toString().equals("")){
                                outputButton.setEnabled(true);
                            }else{
                                outputTimeText.setText("SALIDA: "+snapshot.getValue().toString());
                                fichajes.child("fichajes").child(id).child(today.toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        TimeCard timeCard = snapshot1.getValue(TimeCard.class);
                                        timeTranscurred.setText("Total jornada: " +timeCard.getHour()  + " horas y " + timeCard.getMin() + " minutos");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                outputButton.setEnabled(false);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
        fichajes = FirebaseDatabase.getInstance().getReference();
        fichajes.child("fichajes").child(id).child(today.toString()).child("justify").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(!snapshot.getValue().toString().equals("")){
                                outputTimeText.setText("No registrada");
                                inputTimeText.setText("No registrada");
                                inputButton.setEnabled(false);
                                outputButton.setClickable(false);
                                justify.setEnabled(false);
                                justify.setText("JUSTIFICADO");
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void ValidateIfJustifyExist() {
        final String id = userId.getUid();
        fichajes = FirebaseDatabase.getInstance().getReference();
        fichajes.child("fichajes").child(id).child(today.toString()).child("justify").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                           // outputButton.setEnabled(false);
                            inputButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
/*LOCATION SETTINGS*/
    public static Boolean isLocationEnabled(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
    // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }
    private void getLocation() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(HomeActivity.this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!((List) list).isEmpty()) {
                    Address DirCalle = list.get(0);
                    location=DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**----------------[END OF LOCATION SETTINGS]--------------------*/
    public void openDialogReminder(){
        DialogReminderRegisterLocation dialog = new DialogReminderRegisterLocation();
        dialog.show(getSupportFragmentManager(), "dialogReminder");
    }
    public void openDialog(){
        DialogOutTime dialog = new DialogOutTime();
        dialog.show(getSupportFragmentManager(), "Dialog");
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
    public class Localizacion implements LocationListener {
        HomeActivity mainActivity;
        public HomeActivity getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(HomeActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            String sLatitud = String.valueOf(loc.getLatitude());
            String sLongitud = String.valueOf(loc.getLongitude());

            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            infoGPS.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            infoGPS.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}

