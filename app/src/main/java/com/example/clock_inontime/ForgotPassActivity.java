package com.example.clock_inontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clock_inontime.dialog.DialogChangeForgotPass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassActivity extends AppCompatActivity {
    Button change;
    EditText email;
    private String emailString = "";
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        email=(EditText)findViewById(R.id.emailForgot);
        change=(Button)findViewById(R.id.changePassForgot);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailString = email.getText().toString();
                if (!emailString.isEmpty()){
                    mDialog.setMessage("Espere un momento...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetPassword();
                }else{
                    Toast.makeText(ForgotPassActivity.this, "Debe insertar el e-mail", Toast.LENGTH_SHORT);
                }

            }
        });
    }
    private void resetPassword(){
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DialogChangeForgotPass dialog = new DialogChangeForgotPass(email.getText().toString(), getApplication());
                    dialog.show(getSupportFragmentManager(), "dialogForgot");
                }else{
                    Toast.makeText(ForgotPassActivity.this, "No se pudo enviar el correo para reestablecer contraseña", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }
}
