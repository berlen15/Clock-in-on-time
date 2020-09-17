package com.example.clock_inontime.dialog;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clock_inontime.ForgotPassActivity;
import com.example.clock_inontime.LoginActivity;
import com.example.clock_inontime.R;

public class DialogChangeForgotPass extends AppCompatDialogFragment {
    String email;
    Application app;
    public DialogChangeForgotPass(String email, Application application) {
        this.email=email;
        this.app=application;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedIntanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogOutTime);
        builder.setTitle("SOLICITUD DE CAMBIO DE CONTRASEÑA")
                .setMessage("Se han enviado las instrucciones para el cambio de contraseña a "+email)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(app, LoginActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}
