package com.example.clock_inontime.dialog;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.clock_inontime.LoginActivity;
import com.example.clock_inontime.R;

public class DialogConfirmJustify extends AppCompatDialogFragment {
    Application app;

    public DialogConfirmJustify(Application app) {
        this.app=app;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedIntanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogOutTime);
        builder.setTitle("JUSTIFICACIÓN REALIZADA")
                .setMessage("Se ha justificado la falta con éxito")
                .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(app, LoginActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}
